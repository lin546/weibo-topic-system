package cn.lin.wbtopic.service.impl;

import cn.lin.wbtopic.mapper.FeedMapper;
import cn.lin.wbtopic.mapper.UserTopicFollowMapper;
import cn.lin.wbtopic.mapper.UserWeiboLikeMapper;
import cn.lin.wbtopic.mapper.WeiboMapper;
import cn.lin.wbtopic.model.Feed;
import cn.lin.wbtopic.model.UserTopicFollow;
import cn.lin.wbtopic.model.UserWeiboLike;
import cn.lin.wbtopic.model.Weibo;
import cn.lin.wbtopic.service.result.ServiceResult;
import cn.lin.wbtopic.wbservice.facade.TopicService;
import cn.lin.wbtopic.wbservice.facade.WeiboService;
import com.alibaba.fastjson.JSON;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class WeiboServiceImpl implements WeiboService {

    @Autowired
    private WeiboMapper weiboMapper;

    @Autowired
    private TopicService topicService;

    @Autowired
    private UserWeiboLikeMapper userWeiboLikeMapper;

    @Autowired
    private FeedMapper feedMapper;

    @Autowired
    private UserTopicFollowMapper userTopicFollowMapper;

    private DefaultMQProducer defaultMQProducer;

    @PostConstruct
    public void init() throws MQClientException {
        defaultMQProducer = new DefaultMQProducer("default_group");
        defaultMQProducer.setNamesrvAddr("localhost:9876");
        defaultMQProducer.start();

        DefaultMQPushConsumer consumerFeed = new DefaultMQPushConsumer("default_group");
        consumerFeed.setNamesrvAddr("localhost:9876");
        consumerFeed.subscribe("TopicTrade","TagA");
        consumerFeed.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
                msgs.forEach(msg->{
                    String jsonString = new String(msg.getBody());
                    Weibo weibo = JSON.parseObject(jsonString,Weibo.class);
                    createFeed(weibo);
                });
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });
        consumerFeed.start();


        DefaultMQPushConsumer consumerDiscuss= new DefaultMQPushConsumer("default_group2");
        consumerDiscuss.setNamesrvAddr("localhost:9876");
        consumerDiscuss.subscribe("TopicTrade","TagB");
        consumerDiscuss.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
                msgs.forEach(msg->{
                    String jsonString = new String(msg.getBody());
                    Weibo weibo = JSON.parseObject(jsonString,Weibo.class);
                    if(weibo.getTopicId()!=null&&weibo.getTopicId().intValue()!=0){
                        topicService.increaseDiscussCount(weibo.getTopicId());
                    }
                });
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });
        consumerDiscuss.start();


    }

    @Override
    public ServiceResult<Weibo> get(Integer id) {

        ServiceResult<Weibo> serviceResult = new ServiceResult<>();

        if(id == null){
            serviceResult.setSuccess(false);
            serviceResult.setErrMsg("请求入参不能为空");
            return serviceResult;
        }
        Weibo weibo = weiboMapper.selectByPrimaryKey(id);
        if (weibo == null){
            serviceResult.setSuccess(false);
            serviceResult.setErrMsg("请求微博不存在");
            return serviceResult;
        }
        serviceResult.setData(weibo);
        return serviceResult;
    }

    protected  void asyncIncreaseTopicCount(Weibo weibo) throws MQBrokerException, RemotingException, InterruptedException, MQClientException {
        Message message = new Message("TopicTrade","TagB", JSON.toJSON(weibo).toString().getBytes(Charset.forName("UTF-8")));
        defaultMQProducer.send(message);
    }

    @Override
    @Transactional
    public ServiceResult<Weibo> create(Weibo weiboForCreate) {
        ServiceResult<Weibo> weiboServiceResult = new ServiceResult<>();
        if(!StringUtils.hasText(weiboForCreate.getContent())||
                !StringUtils.hasText(weiboForCreate.getImgUrl())||
                weiboForCreate.getUserId() == null){
            weiboServiceResult.setSuccess(false);
            weiboServiceResult.setErrMsg("入参不合法");
            //操作切面回滚事务
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return  weiboServiceResult;
        }
        weiboMapper.insertSelective(weiboForCreate);

        //若微博关联了对应的话题，则需要将话题的讨论数量+1
        if(weiboForCreate.getTopicId()!=null&&weiboForCreate.getTopicId().intValue()!=0){
            try {
                asyncIncreaseTopicCount(weiboForCreate);
            } catch (MQBrokerException e) {
                e.printStackTrace();
            } catch (RemotingException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (MQClientException e) {
                e.printStackTrace();
            }
        }
        weiboServiceResult.setData(weiboForCreate);

        //事务提交完成后执行对应的创建feed流的操作
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                //createFeed(weiboForCreate);
                try {
                    sendFeedMsg(weiboForCreate);
                } catch (MQBrokerException e) {
                    e.printStackTrace();
                } catch (RemotingException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (MQClientException e) {
                    e.printStackTrace();
                }
            }
        });
        return weiboServiceResult;
    }

    protected void sendFeedMsg(Weibo weibo) throws MQBrokerException, RemotingException, InterruptedException, MQClientException {
        //mqadmin updateTopic -n localhost:9876 -c DefaultCluster -t TopicTrade
        Message message = new Message("TopicTrade","TagA", JSON.toJSON(weibo).toString().getBytes(Charset.forName("UTF-8")));
        defaultMQProducer.send(message);
    }

    protected void createFeed(Weibo weibo){
        //创建feed信息的操作
        if(weibo.getTopicId()!=null && weibo.getTopicId().intValue()!=0){
            List<UserTopicFollow> userTopicFollows = userTopicFollowMapper.listFollowByTopicId(weibo.getTopicId());
            userTopicFollows.forEach(userTopicFollow -> {
                Feed feed = new Feed();
                feed.setCreateTime(weibo.getCreateTime());
                feed.setLinkId(weibo.getId());
                feed.setType(1);
                feed.setUserId(userTopicFollow.getUserId());
                try {
                    feedMapper.insertSelective(feed);
                }catch (Exception ex){
                    ex.printStackTrace();
                }
            });
        }
    }

    @Override
    public ServiceResult<List<Weibo>> list() {
        ServiceResult<List<Weibo>> list = new ServiceResult<>();
        List<Weibo> weibos = weiboMapper.listWeibo();
        list.setData(weibos);
        return list;
    }

    @Override
    @Transactional
    public ServiceResult like(Integer userId, Integer weiboId) {
        ServiceResult serviceResult = new ServiceResult();

        if(userId == null || weiboId == null){
            serviceResult.setSuccess(false);
            serviceResult.setErrMsg("入参不完整");
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return serviceResult;
        }

        Weibo weibo = weiboMapper.selectByPrimaryKey(weiboId);
        if (weibo == null){
            serviceResult.setSuccess(false);
            serviceResult.setErrMsg("所赞的微博不存在");
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return serviceResult;
        }
        UserWeiboLike userWeiboLike = new UserWeiboLike();
        userWeiboLike.setUserId(userId);
        userWeiboLike.setWeiboId(weiboId);
        try {
            userWeiboLikeMapper.insertSelective(userWeiboLike);
        }catch (DuplicateKeyException ex){
            return serviceResult;
        }

        weiboMapper.increaseLikeCount(weiboId);

        return serviceResult;
    }

    @Override
    public ServiceResult<List<Weibo>> listByTopicId(Integer topicId) {
        ServiceResult<List<Weibo>> serviceResult = new ServiceResult<>();
        if(topicId == null || topicId.intValue() == 0){
            serviceResult.setSuccess(false);
            serviceResult.setErrMsg("入参错误");
            return serviceResult;

        }
        List<Weibo> list = weiboMapper.listWeiboByTopicId(topicId);
        serviceResult.setData(list);
        return serviceResult;
    }

    @Override
    public ServiceResult<List<Weibo>> listByFeed(Integer userId) {
        ServiceResult<List<Weibo>> serviceResult = new ServiceResult<>();
        List<Feed> feedList = feedMapper.list(userId);
        if(feedList.size() == 0){
            serviceResult.setData(new ArrayList<>());
            return serviceResult;
        }
        List<Integer> weiboIdList = feedList.stream().map(feed -> {
            return feed.getLinkId();
        }).collect(Collectors.toList());
        System.out.println(weiboIdList);
        List<Weibo> list = weiboMapper.listWeiboByIds(weiboIdList);
        serviceResult.setData(list);
        return serviceResult;
    }
}
