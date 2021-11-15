package cn.lin.wbtopic.controller;

import cn.lin.wbtopic.model.Topic;
import cn.lin.wbtopic.model.User;
import cn.lin.wbtopic.model.Weibo;
import cn.lin.wbtopic.service.result.ServiceResult;
import cn.lin.wbtopic.wbservice.facade.TopicService;
import cn.lin.wbtopic.wbservice.facade.WeiboService;
import com.google.common.util.concurrent.RateLimiter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.List;
import java.util.Map;


@Controller
@RequestMapping("/weibo")
public class WeiboController extends BaseController{

    @Autowired
    private WeiboService weiboService;

    @Autowired
    private TopicService topicService;

    //限制对应微博点赞的限流器
    private RateLimiter rateLimiter = null;

    @PostConstruct
    public void init(){
        rateLimiter = RateLimiter.create(100);
    }

    @GetMapping("/list")
    @ResponseBody
    public Map list(){
        User currentUser = getCurrentUser();
        ServiceResult<List<Weibo>> list = null;
        if(currentUser == null){
            list = weiboService.list();
        }else {
            list = weiboService.listByFeed(currentUser.getId());
        }
        if (list == null){
            return getErrResp("调用微博信息异常");
        }
        if (list.isSuccess()){
            return getResp(list.getData());
        }else {
            return getErrResp(list.getErrMsg());
        }
    }

    @GetMapping("/detail")
    public ModelAndView detail(@RequestParam(name = "id")Integer id){
        ServiceResult<Weibo> serviceResult = weiboService.get(id);
        if (weiboService == null){
            ModelAndView modelAndView = new ModelAndView("error");
            modelAndView.addObject("content","获取微博服务异常");
            return modelAndView;
        }else if (!serviceResult.isSuccess()){
            ModelAndView modelAndView = new ModelAndView("error");
            modelAndView.addObject("content",serviceResult.getErrMsg());
            return modelAndView;
        }else {
            ModelAndView modelAndView = new ModelAndView("getweibo");
            modelAndView.addObject("data",serviceResult.getData());
            return modelAndView;
        }
    }

    @GetMapping("/get")
    @ResponseBody
    public Map get(@RequestParam(name = "id")Integer id){
        ServiceResult<Weibo> serviceResult = weiboService.get(id);
        if (weiboService == null){
            return getErrResp("获取微博服务异常");
        }else if (!serviceResult.isSuccess()){
            return getErrResp(serviceResult.getErrMsg());
        }else {
            return getResp(serviceResult.getData());
        }
    }


    @GetMapping("/publish")
    public ModelAndView publish(){

        User currentUser = getCurrentUser();
        if (currentUser == null){
            ModelAndView modelAndView = new ModelAndView("redirect:/user/login");
            return modelAndView;
        }
        ModelAndView modelAndView = new ModelAndView("publish");
        return modelAndView;
    }

    /**
     * 点赞功能
     * @param weiboId 微博ID
     */
    @PostMapping("/like")
    @ResponseBody
    public Map like(@RequestParam(name = "weiboId")Integer weiboId){

        double limit = rateLimiter.acquire();
        if(limit > 0){
            return getErrResp("访问过量，稍后再试");
        }

        User currentUser = getCurrentUser();
        if (currentUser == null){
            return getErrResp("当前用户没有登录，无法点赞");
        }

        ServiceResult<Weibo> serviceResult = weiboService.like(currentUser.getId(),weiboId);

        if(serviceResult == null){
            return  getErrResp("微博服务异常");
        }else if (!serviceResult.isSuccess()){
            return getErrResp(serviceResult.getErrMsg());
        }else {
            return getResp(serviceResult.getData());
        }
    }

    @PostMapping("/publish")
    @ResponseBody
    public Map publish(@RequestParam(name = "content")String content,
                       @RequestParam(name = "imgUrl")String imgUrl,
                       @RequestParam(name = "topicTitle")String topicTitle){

        User currentUser = getCurrentUser();
        if (currentUser == null){
            return getErrResp("当前用户没有登录，无法发布微博");
        }
        Integer topicId = 0;
        if(StringUtils.hasText(topicTitle)){
            ServiceResult<Topic> topicResult = topicService.getByTitle(topicTitle);
            if(topicResult == null){
                return getErrResp("话题服务通信异常");
            }else if (!topicResult.isSuccess()){
                return getErrResp(topicResult.getErrMsg());
            }else if(topicResult.getData()==null){
                return getErrResp("不存在对应的话题，无法发布微博");
            }else {
                topicId = topicResult.getData().getId();
            }
        }

        Weibo weiboForCreate = new Weibo();
        weiboForCreate.setContent(content);
        weiboForCreate.setImgUrl(imgUrl);
        weiboForCreate.setCreateTime(new Date());
        weiboForCreate.setUserId(currentUser.getId());
        weiboForCreate.setTopicId(topicId);

        ServiceResult<Weibo> serviceResult = weiboService.create(weiboForCreate);

        if(serviceResult == null){
            return  getErrResp("微博服务异常");
        }else if (!serviceResult.isSuccess()){
            return getErrResp(serviceResult.getErrMsg());
        }else {
            return getResp(serviceResult.getData());
        }
    }

}
