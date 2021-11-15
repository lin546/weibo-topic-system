package cn.lin.wbtopic.service.impl;

import cn.lin.wbtopic.mapper.TopicMapper;
import cn.lin.wbtopic.mapper.UserTopicFollowMapper;
import cn.lin.wbtopic.mapper.UserTopicReadMapper;
import cn.lin.wbtopic.model.Topic;
import cn.lin.wbtopic.model.UserTopicFollow;
import cn.lin.wbtopic.model.UserTopicRead;
import cn.lin.wbtopic.service.SyncDiscussCountJob;
import cn.lin.wbtopic.service.result.ServiceResult;
import cn.lin.wbtopic.wbservice.facade.TopicService;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Set;

public class TopicServiceImpl implements TopicService {

    @Autowired
    private TopicMapper topicMapper;

    @Autowired
    private UserTopicFollowMapper userTopicFollowMapper;

    @Autowired
    private UserTopicReadMapper userTopicReadMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    private static final String topicDiscussRedisKeyPrefix = "Topic_Discuss_Count";

    @PostConstruct
    public void init() throws SchedulerException {
        Scheduler defaultScheduler = StdSchedulerFactory.getDefaultScheduler();
        JobDetail jobDetail = JobBuilder.newJob(SyncDiscussCountJob.class).withIdentity("sync","sync").build();
        jobDetail.getJobDataMap().put("springBean",this);
        Trigger trigger = TriggerBuilder.newTrigger().withIdentity("trigger","trigger").withSchedule(CronScheduleBuilder.cronSchedule("0/5 * * * * ?")).build();
        defaultScheduler.scheduleJob(jobDetail,trigger);
        defaultScheduler.start();
    }


    @Override
    @Transactional
    public ServiceResult<Topic> create(Topic topic) {
        ServiceResult<Topic> result = new ServiceResult<>();
        if(!StringUtils.hasText(topic.getTitle())||!StringUtils.hasText(topic.getDescription())){
            result.setSuccess(false);
            result.setErrMsg("入参异常");
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return  result;
        }

        try {
            topicMapper.insertSelective(topic);
        }catch (DuplicateKeyException ex){
            result.setSuccess(false);
            result.setErrMsg("话题名已经存在,创建失败");
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return  result;
        }
        result.setData(topic);
        return result;
    }

    private void fillDiscussCount(Topic topic){
        if (topic == null){
            return;
        }
        Integer discussCount = (Integer) redisTemplate.opsForValue().get(topicDiscussRedisKeyPrefix+topic.getId());
        if (discussCount !=null){
            topic.setDiscussCount(discussCount.intValue());
        }else {
            //若Redis内GET不到,用数据库的替换
            redisTemplate.opsForValue().set(topicDiscussRedisKeyPrefix+topic.getId(),topic.getDiscussCount());
        }
    }

    @Override
    public ServiceResult<Topic> get(Integer id) {

        ServiceResult<Topic> serviceResult = new ServiceResult<>();
        if(id == null){
            serviceResult.setSuccess(false);
            serviceResult.setErrMsg("入参不合法");
            return serviceResult;
        }
        Topic topic = topicMapper.selectByPrimaryKey(id);
        fillDiscussCount(topic);
        serviceResult.setData(topic);
        return serviceResult;
    }

    @Override
    public ServiceResult<Topic> getByTitle(String title) {
        ServiceResult<Topic> serviceResult = new ServiceResult<>();
        if(!StringUtils.hasText(title)){
            serviceResult.setSuccess(false);
            serviceResult.setErrMsg("入参不合法");
            return serviceResult;
        }
        Topic topic = topicMapper.selectByTitle(title);
        fillDiscussCount(topic);
        serviceResult.setData(topic);
        return serviceResult;
    }

    @Override
    @Transactional
    public ServiceResult increaseDiscussCount(Integer id) {
        ServiceResult serviceResult = new ServiceResult();
        //调用数据库将对应话题的讨论数量+1
        //topicMapper.increaseDiscussCount(id);

        //调用Redis将对应话题的讨论数量+1
        redisTemplate.opsForValue().increment(topicDiscussRedisKeyPrefix+id,1);

        return serviceResult;
    }

    @Override
    public ServiceResult<List<Topic>> list() {
        ServiceResult<List<Topic>> serviceResult = new ServiceResult<>();
        List<Topic> topicList = topicMapper.list();
        topicList.forEach(topic -> {
            fillDiscussCount(topic);
        });
        serviceResult.setData(topicList);

        return serviceResult;
    }

    @Override
    @Transactional
    public ServiceResult follow(Integer userId, Integer topicId) {
        ServiceResult serviceResult = new ServiceResult();
        ServiceResult<Topic> topicServiceResult = this.get(topicId);
        if(topicServiceResult == null){
            serviceResult.setSuccess(false);
            serviceResult.setErrMsg("话题服务通信异常");
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return serviceResult;
        }else if (!topicServiceResult.isSuccess()||topicServiceResult.getData()==null){
            serviceResult.setSuccess(false);
            serviceResult.setErrMsg("获取话题不存在或发生错误");
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return serviceResult;
        }
        Topic topic = topicServiceResult.getData();

        UserTopicFollow userTopicFollow = new UserTopicFollow();
        userTopicFollow.setUserId(userId);
        userTopicFollow.setTopicId(topicId);
        try {
            userTopicFollowMapper.insertSelective(userTopicFollow);
        }catch (DuplicateKeyException ex){
            serviceResult.setSuccess(true);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return serviceResult;
        }
        topicMapper.increaseFollowCount(topicId);
        return serviceResult;
    }

    @Override
    @Transactional
    public ServiceResult read(Integer userId, Integer topicId) {
        ServiceResult serviceResult = new ServiceResult();
        ServiceResult<Topic> topicServiceResult = this.get(topicId);
        if(topicServiceResult == null){
            serviceResult.setSuccess(false);
            serviceResult.setErrMsg("话题服务通信异常");
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return serviceResult;
        }else if (!topicServiceResult.isSuccess()||topicServiceResult.getData()==null){
            serviceResult.setSuccess(false);
            serviceResult.setErrMsg("获取话题不存在或发生错误");
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return serviceResult;
        }
        Topic topic = topicServiceResult.getData();

        UserTopicRead userTopicRead = new UserTopicRead();
        userTopicRead.setUserId(userId);
        userTopicRead.setTopicId(topicId);
        try {
            userTopicReadMapper.insertSelective(userTopicRead);
        }catch (DuplicateKeyException ex){
            serviceResult.setSuccess(true);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return serviceResult;
        }
        topicMapper.increaseReadCount(topicId);
        return serviceResult;
    }

    @Override
    public void syncTopicDiscussCount() {
        //System.out.println("SyncDiscussCountJob");
        Set<String> keys = redisTemplate.keys("Topic_Discuss_Count*");
        keys.forEach(key->{
            Integer count = (Integer) redisTemplate.opsForValue().get(key);
            Integer topicId = Integer.valueOf(key.replace("Topic_Discuss_Count", ""));
            if (count == null && topicId == null){
                return;
            }
            topicMapper.updateDiscussCount(topicId,count);
        });
    }

}
