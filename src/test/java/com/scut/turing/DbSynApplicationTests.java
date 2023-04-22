package com.scut.turing;

import com.scut.turing.service.PgService;
import com.scut.turing.service.RedisService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;

@SpringBootTest
class DbSynApplicationTests {

	@Autowired
	private RedisTemplate redisTemplate;
	@Autowired
	PgService pgService;

	@Autowired
	RedisService redisService;
	@Test
	void contextLoads() {

	}

	@Test
	void connectRedis()
	{
		//redisTemplate.opsForValue().set("sql","value");
//		System.out.println(redisTemplate.opsForValue().get("sql"));
		ListOperations listOperations = redisTemplate.opsForList();

		listOperations.rightPush("sqlList","insert into demo values(5,\"hello world\")");
		listOperations.rightPush("sqlList","insert into demo values(6,\"hello world\")");
		listOperations.rightPush("sqlList","insert into demo values(7,\"hello world\")");


		System.out.println(listOperations.size("sqlList"));
		while(listOperations.size("sqlList")>0)
		{
			System.out.println(listOperations.leftPop("sqlList"));
		}
		System.out.println(listOperations.size("sqlList"));
	}

	@Test
	void rg()
	{
		redisService.SqlHandler("insert into demo values(10,\'hello\')");
		redisService.SqlHandler("insert into demo values(7,\'hello\')");
		redisService.SqlHandler("insert into demo values(8,\'hello\')");
		redisService.SqlHandler("insert into demo values(9,\'hello\')");
	}

	@Test

	void testAsync()
	{

	}

}
