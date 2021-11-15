package cn.lin.wbtopic.service.impl;

import cn.lin.wbtopic.wbservice.facade.DemoService;

public class DemoServiceImpl implements DemoService {
    public String sayHello(String name) {
        return "Hello " + name;
    }
}