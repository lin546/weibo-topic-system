<p align="center">
  <a href="" rel="noopener">
 <img width=200px height=200px src="https://iconfont.alicdn.com/t/58c8f739-bbd7-4831-ae22-c877489d2cbc.png" alt="Project logo"></a>
</p>

<h3 align="center">微博热门话题系统架构</h3>

<div align="center">

[![Status](https://img.shields.io/badge/status-active-success.svg)]()
[![License](https://img.shields.io/badge/license-MIT-blue.svg)](/LICENSE)

</div>

---

## 📝 Table of Contents

- [About](#about)
- [Getting Started](#getting_started)
- [Usage](#usage)
- [Built Using](#built_using)
- [TODO](#TODO)

## 🧐 About <a name = "about"></a>

通过多级缓存、队列操作、泄洪操作、限流降级术等解决方案展示如何搭建高性能大流量微博社交网站的系统架构设计

## 🏁 Getting Started <a name = "getting_started"></a>


### Prerequisites

+ JDK 8
+ Redis 6.2.5
+ RocketMQ 4.3
+ MYSQL 8.0

### Installing

```
git clone git@github.com:lin546/weibo-topic-system.git

cd weibo-topic

mvn clean package

cd ../weibo-service

mvn clean pcakage
```

## 🎈 Usage <a name="usage"></a>


+ 导入数据库文件：数据库文件 `docs/weibo.sql`
+ 启动RockeMQ,执行`mqadmin updateTopic -n localhost:9876 -c DefaultCluster -t TopicTrade`
+ 运行 **weibo-service.jar**: `java -jar weibo-wervice.jar`
+ 运行 **weibo-topic.jar**: `java -jar weibo-topic.jar`
+ 访问：localhost:6066/index


## ⛏️ Built Using <a name = "built_using"></a>

- [MySQL](https://www.mysql.com/)
- [SpringBoot](https://spring.io/projects/spring-boot)
- [Redis](http://www.redis.cn/)
- [RocketMQ](https://rocketmq.apache.org/)

## 🎉 TODO <a name = "TODO"></a>

- 完善话题接口前台界面
- 完善 Hystrix 线程隔离
