# 架构思想

监听binlog文件，然后生成sql到RabbitMQ，postgreSQL端的就不断读取RabbitMQ里面的sql，将sql语句处理为pg的sql然后放在redis里面缓存起来，然后定时执行sql。

注意mysql和pg的语法区别。进行处理

# 技术栈

- Java
- SpringBoot
- RabbitMQ
- Mysql
- PostgreSQL
- Maven
- Redis
- 可能会有多线程编程



# 如何搭建

prerequisite：

- Maven
- IDAE
- Java 17

具体流程

- git clone获得代码
- 使用idea打开
- 等待maven同步完毕



push失败解决方案

先

```
git pull --rebase origin main
```

再

push
