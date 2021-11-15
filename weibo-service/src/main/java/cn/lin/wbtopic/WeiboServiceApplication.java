package cn.lin.wbtopic;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;

@SpringBootApplication
@MapperScan(basePackages = "cn.lin.wbtopic.mapper")
@ImportResource({"classpath*:/applicationContext.xml"})
public class WeiboServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(WeiboServiceApplication.class, args);
    }
}
