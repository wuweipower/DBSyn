package com.scut.turing;

import com.scut.turing.service.PgService;
import com.scut.turing.service.RedisService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.JdbcUtils;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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

	@Autowired
	private DataSource dataSource;
	@Test
	void testAsync()
	{
		List<String> sqls = new ArrayList<>();
		sqls.add("insert into test12 values(2,\'name\',null,10)");
		pgService.executeSql(sqls);
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		List<String> tableNames = jdbcTemplate.execute((Connection connection) -> {
			DatabaseMetaData metaData = connection.getMetaData();
			System.out.println(connection.getCatalog());
			ResultSet rs = metaData.getTables(connection.getCatalog(), null, "%", new String[]{"TABLE"});
			List<String> list = new ArrayList<>();
			while(rs.next())
			{
				list.add(rs.getString("TABLE_NAME"));
			}
			return list;
		});

		// 打印结果
		for (String tableName : tableNames) {
			System.out.println(tableName);
		}

	}

	//test redis push
	@Test
	void testRedisPush()
	{
		long start = System.currentTimeMillis();
		ListOperations listOperations = redisTemplate.opsForList();
		for(int i=0;i<10*10000;++i)
		{
			listOperations.rightPush("test","test"+String.valueOf(i));
		}
		long end = System.currentTimeMillis();
		System.out.println("executing time push "+String.valueOf(end-start));

		start = System.currentTimeMillis();
		List<String> test = listOperations.range("test", 0, -1);
		System.out.println(test.size());
		listOperations.trim("test",0,0);
		System.out.println(listOperations.index("test",0));
		listOperations.remove("test",1,listOperations.index("test",0));
		System.out.println(listOperations.size("test"));
		end = System.currentTimeMillis();
		System.out.println("executing time remove "+String.valueOf(end-start));

	}

	@Test
	void testJavaBuff()
	{
		long start = System.currentTimeMillis();
		HashMap<String,List<String>> map = new HashMap<>();
		map.put("test",new ArrayList<>());
		for(int i=0;i<10*10000;++i)
		{
			map.get("test").add("test");
		}
		long end = System.currentTimeMillis();
		System.out.println("executing time push "+String.valueOf(end-start));

		start = System.currentTimeMillis();
		map.get("test").clear();
		end = System.currentTimeMillis();
		System.out.println("executing time remove "+String.valueOf(end-start));

	}

	@Test
	void testPgWrite()
	{
		long start = System.currentTimeMillis();
		List<String> sqls = new ArrayList<>();
		for(int i=0;i<10*10000;++i)
		{
			sqls.add("insert into t3 values("+String.valueOf(i)+",\'aa\',\'bb\')");
		}
		pgService.executeSql(sqls);
		long end = System.currentTimeMillis();
		System.out.println("executing time pg write "+String.valueOf(end-start));
	}

	@Autowired
	private JdbcTemplate jdbcTemplate;
	@Test
	void loopWrite()
	{
		long start = System.currentTimeMillis();
		List<String> sqls = new ArrayList<>();
		for(int i=0;i<10*10000;++i)
		{
			sqls.add("insert into t3 values("+String.valueOf(i)+",\'aa\',\'bb\')");
		}
		for(String str:sqls)
		{
			jdbcTemplate.execute(str);
		}
		long end = System.currentTimeMillis();
		System.out.println("executing time pg write "+String.valueOf(end-start));
	}

	@Test
	void loopBatchWrite()
	{
		long start = System.currentTimeMillis();
		List<String> sqls = new ArrayList<>();
		for(int i=0;i<10*10000;++i)
		{
			sqls.add("insert into t3 values("+String.valueOf(i)+",\'aa\',\'bb\');");
		}
		List<String> batch = new ArrayList<>();
		StringBuilder temp = new StringBuilder();
		for(int i=0;i<sqls.size();++i)
		{
			temp.append(sqls.get(i));
			if(i%1000==0)
			{
				batch.add(temp.toString());
				temp.setLength(0);
			}
		}
		batch.add(temp.toString());
		for(String str:batch)
		{
			jdbcTemplate.execute(str);
		}
		long end = System.currentTimeMillis();
		System.out.println("executing time pg write "+String.valueOf(end-start));
	}

}
