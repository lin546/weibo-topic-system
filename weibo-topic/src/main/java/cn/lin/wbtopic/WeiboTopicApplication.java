package cn.lin.wbtopic;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;

@SpringBootApplication
@ImportResource({"classpath*:/applicationContext.xml"})
@MapperScan(basePackages = "cn.lin.wbtopic.mapper")
public class WeiboTopicApplication {

    public static void main(String[] args) {
        SpringApplication.run(WeiboTopicApplication.class, args);
    }

}
