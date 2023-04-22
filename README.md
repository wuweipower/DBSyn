# 架构思想

监听binlog文件，然后生成到RabbitMQ，postgreSQL端的就不断读取RabbitMQ里面的sql，然后执行。



# 技术栈

- Java
- SpringBoot
- RabbitMQ
- Mysql
- PostgreSQL
- Maven



# 如何搭建

prerequisite：

- Maven
- IDAE
- Java 17

具体流程

- git clone获得代码
- 使用idea打开
- 等待maven同步完毕



使用@EnableAsync启动异步任务

在类上加@EnableScheduling就可以异步了

