在conf文件里面设置

```
requirepass password
```

springboot连接不上redis

可能是有springdata的原因

```yml
spring:
  data:
    redis:
      host: localhost
      port: 6379
      password: 123456
```

以前不需要data

