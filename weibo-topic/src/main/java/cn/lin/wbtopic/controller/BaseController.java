package cn.lin.wbtopic.controller;

import cn.lin.wbtopic.model.User;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

public class BaseController {

    @Autowired
    private HttpServletRequest httpServletRequest;

    protected User getCurrentUser(){
        User currentUser = (User) httpServletRequest.getSession().getAttribute("loginUser");
        return currentUser;
    }

    protected Map getResp(Object data){
        Map<String,Object> result = new HashMap<>();
        result.put("success",true);
        result.put("data",data);
        return result;
    }

    protected Map getErrResp(String errMsg){
        Map<String,Object> result = new HashMap<>();
        result.put("success",false);
        result.put("data",errMsg);
        return result;
    }
}
