package cn.lin.wbtopic.controller;

import cn.lin.wbtopic.model.User;
import cn.lin.wbtopic.service.UserService;
import cn.lin.wbtopic.service.result.ServiceResult;
import cn.lin.wbtopic.wbservice.facade.DemoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
@Controller
@RequestMapping("/user")
public class UserController extends BaseController{

    @Autowired
    private UserService userService;

    @Autowired
    private HttpServletRequest httpServletRequest;

    @Autowired
    private DemoService demoService;

    @GetMapping("/demo")
    @ResponseBody
    public String demo(){
        String hello = demoService.sayHello("world"); // 执行远程方法
        return  hello; // 显示调用结果
    }

    @GetMapping("/get")
    @ResponseBody
    public Map getUser(@RequestParam(name = "id")Integer id){
        User user = userService.getUser(id);
        return getResp(user);
    }

    @GetMapping("/login")
    public ModelAndView login(){
        ModelAndView modelAndView = new ModelAndView("login");
        return  modelAndView;
    }

    @PostMapping("/login")
    @ResponseBody
    public Map login(@RequestParam(name = "loginName")String loginName,
                        @RequestParam(name = "password")String password){
        ServiceResult serviceResult = userService.login(loginName,password);
        if(!serviceResult.isSuccess()){
            return getErrResp((String) serviceResult.getData());
        }else{
            httpServletRequest.getSession().setAttribute("loginUser",serviceResult.getData());
            return getResp(serviceResult.getData());
        }
    }

    @GetMapping("/register")
    public ModelAndView register(){
        ModelAndView modelAndView = new ModelAndView("register");
        return  modelAndView;
    }

    @PostMapping("/register")
    @ResponseBody
    public Map register(@RequestParam(name = "loginName")String loginName,
                                        @RequestParam(name = "password")String password,
                                        @RequestParam(name = "nickName")String nickName,
                                        @RequestParam(name = "gender")Integer gender,
                                        @RequestParam(name = "age")Integer age,
                                        @RequestParam(name = "avatarUrl")String avatarUrl){

        if (!StringUtils.hasText(loginName)||!StringUtils.hasText(password)
                ||!StringUtils.hasText(nickName)||!StringUtils.hasText(avatarUrl)){
            return getErrResp("输入参数不合法");
        }

        if(gender != 1 && gender != 2){
            return getErrResp("性别参数不合法");
        }
        ServiceResult serviceResult = userService.register(loginName, password, nickName, gender, age, avatarUrl);
        if(serviceResult.isSuccess()){
            return getResp(null);
        }else{
            return getErrResp((String) serviceResult.getData());
        }
    }
}

