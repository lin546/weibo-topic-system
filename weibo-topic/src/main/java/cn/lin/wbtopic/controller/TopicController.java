package cn.lin.wbtopic.controller;

import cn.lin.wbtopic.model.Topic;
import cn.lin.wbtopic.model.User;
import cn.lin.wbtopic.model.Weibo;
import cn.lin.wbtopic.service.result.ServiceResult;
import cn.lin.wbtopic.wbservice.facade.TopicService;
import cn.lin.wbtopic.wbservice.facade.WeiboService;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.LoadingCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Controller
@RequestMapping("/topic")
public class TopicController extends BaseController{

    @Autowired
    private TopicService topicService;

    @Autowired
    private WeiboService weiboService;

    @Autowired
    private RedisTemplate redisTemplate;

    private Cache<Object, Object> topicCache;

    @PostConstruct
    public void init(){
        topicCache = CacheBuilder.newBuilder().initialCapacity(10).maximumSize(100).expireAfterWrite(10,TimeUnit.SECONDS).build();
    }


    @GetMapping("/hottopic")
    public ModelAndView hotTopic(){
        ModelAndView modelAndView = new ModelAndView("topiclist");
        return modelAndView;
    }

    @GetMapping("/detail")
    public ModelAndView detail(@RequestParam(name = "id")Integer topicId){
        ModelAndView modelAndView = new ModelAndView("gettopic");
        modelAndView.addObject("topicId",topicId);
        return modelAndView;
    }


    @PostMapping("/create")
    @ResponseBody
    public Map createTopic(@RequestParam(name = "title")String title,
                           @RequestParam(name = "description")String description){
        Topic topic = new Topic();
        topic.setTitle(title);
        topic.setDescription(description);
        ServiceResult<Topic> result = topicService.create(topic);
        if(result == null){
            return getErrResp("话题服务通信异常");
        }else if (!result.isSuccess()){
            return getErrResp(result.getErrMsg());
        }else {
            return getResp(result.getData());
        }
    }

    @PostMapping("/follow")
    @ResponseBody
    public Map follow(@RequestParam(name = "topicId") Integer topicId){

        User currentUser = getCurrentUser();
        if (currentUser == null){
            return getErrResp("还未登录，请先登录后才能关注");
        }
        ServiceResult serviceResult = topicService.follow(currentUser.getId(),topicId);
        if(serviceResult == null){
            return getErrResp("话题服务通信异常");
        }else if(!serviceResult.isSuccess()){
            return getErrResp(serviceResult.getErrMsg());
        }else {
            return getResp(serviceResult.getData());
        }
    }



    @GetMapping("/get")
    @ResponseBody
    public Map getTopic(@RequestParam(name = "id")Integer id) throws ExecutionException {

        User currentUser = getCurrentUser();
        if (currentUser !=null){
            //阅读数+1
            try {
                topicService.read(currentUser.getId(),id);
            }catch (Exception ex){
                ex.printStackTrace();
            }
        }


        final String topicKeyPrefix = "Topic_Detail_";
        String commonKey = topicKeyPrefix + id;

        //优先读取本地内存
        Map<String,Object> cacheResultMap = (Map<String, Object>) topicCache.getIfPresent(commonKey);
        if (cacheResultMap != null){
            return getResp(cacheResultMap);
        }

        Map<String,Object> redisResultMap = (Map<String, Object>) redisTemplate.opsForValue().get(commonKey);

        if(redisResultMap!=null){
            topicCache.put(commonKey,redisResultMap);
            return getResp(redisResultMap);
        }

        ServiceResult<Topic> topicServiceResult = topicService.get(id);
        Topic topic = null;
        if(topicServiceResult == null){
            return getErrResp("话题服务通信异常");
        }else if(!topicServiceResult.isSuccess()){
            return getErrResp(topicServiceResult.getErrMsg());
        }else if(topicServiceResult.getData() == null){
            return getErrResp("没有话题id对应的话题存在");
        }else {
            topic = topicServiceResult.getData();
        }

        //根据话题id查询出话题下的讨论微博内容
        List<Weibo> weiboList = new ArrayList<>();
        ServiceResult<List<Weibo>>  serviceResult = weiboService.listByTopicId(id);
        if (serviceResult == null){
            return getErrResp("微博服务通信异常");
        }else if (!serviceResult.isSuccess()){
            return getErrResp(serviceResult.getErrMsg());
        }else {
            weiboList = serviceResult.getData();
        }
        Map<String,Object> resultMap = new HashMap<>();
        resultMap.put("topic",topic);
        resultMap.put("weiboList",weiboList);

        redisTemplate.opsForValue().set(commonKey,resultMap);
        // 设置超时时间
        redisTemplate.expire(commonKey,1, TimeUnit.MINUTES);
        topicCache.put(commonKey,resultMap);
        return getResp(resultMap);
    }

    @GetMapping("/list")
    @ResponseBody
    public Map listTopic(){
        ServiceResult<List<Topic>> listServiceResult = topicService.list();
        if(listServiceResult == null){
            return getErrResp("话题服务通信异常");
        }else if(!listServiceResult.isSuccess()){
            return getErrResp(listServiceResult.getErrMsg());
        }else {
            return getResp(listServiceResult.getData());
        }
    }
}
