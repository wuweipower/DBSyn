package com.scut.turing;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.scut.turing.service.SendSqlService;
import com.scut.turing.service.UpdateService;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SpringBootTest
class DbSynApplicationTests {

	@Autowired
	private RabbitTemplate rabbitTemplate;

	//1.定义交换机
	private String exchangeName = "sql_exchange";
	//2.路由key
	private String routeKey = "sql";
	@Autowired
	UpdateService updateService;

	@Test
	void contextLoads() {
		ConnectionFactory connectionFactory = new ConnectionFactory();
		// 2: 设置连接属性
		connectionFactory.setHost("127.0.0.1");
		connectionFactory.setPort(5672);
		connectionFactory.setVirtualHost("/");
		connectionFactory.setUsername("Lanny");
		connectionFactory.setPassword("Lanny");
		Connection connection = null;
		Channel channel = null;
		try {
			// 3: 从连接工厂中获取连接
			connection = connectionFactory.newConnection("生产者");
			// 4: 从连接中获取通道channel
			channel = connection.createChannel();
			// 5: 申明队列queue存储消息
			/*
			 *  如果队列不存在，则会创建
			 *  Rabbitmq不允许创建两个相同的队列名称，否则会报错。
			 *
			 *  @params1： queue 队列的名称
			 *  @params2： durable 队列是否持久化
			 *  @params3： exclusive 是否排他，即是否私有的，如果为true,会对当前队列加锁，其他的通道不能访问，并且连接自动关闭
			 *  @params4： autoDelete 是否自动删除，当最后一个消费者断开连接之后是否自动删除消息。
			 *  @params5： arguments 可以设置队列附加参数，设置队列的有效期，消息的最大长度，队列的消息生命周期等等。
			 * */
			channel.queueDeclare("queue1", false, false, false, null);
			// 6： 准备发送消息的内容
			String message = "你好，学相伴！！！";
			// 7: 发送消息给中间件rabbitmq-server
			// @params1: 交换机exchange
			// @params2: 队列名称/routing
			// @params3: 属性配置
			// @params4: 发送消息的内容
			channel.basicPublish("sql_exchange", "queue1", null, message.getBytes());
			System.out.println("消息发送成功!");
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("发送消息出现异常...");
		} finally {
			// 7: 释放连接关闭通道
			if (channel != null && channel.isOpen()) {
				try {
					channel.close();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
			if (connection != null) {
				try {
					connection.close();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}
	}

	@Autowired
	SendSqlService sendSqlService;
	@Test
	void testSpeed()
	{
		for(int i= 50000;i<60000;++i){
			String sql = "insert into t3 values( "+ String.valueOf(i) + ", \'"+"n"+String.valueOf(i)+"\', "+"\'i"+
					String.valueOf(i)+"\');";
			sendSqlService.sendSql(sql);
		}
	}

	@Test
	void testSqlProcess()
	{
		String str="create table t4(id int unsigned not null,sid int unique auto_increment,primary key(id));";
		if (!str.equals("BEGIN")) {
			str = str.toLowerCase();
			str = str.replaceAll("datetime", "date");
			str = str.replaceAll("tinyint", "smallint");
			str = str.replaceAll("blob", "bytea");
			str = str.replace('`', ' ');
			String pattern = "auto_increment";
			Pattern p = Pattern.compile(pattern);
			Matcher m = p.matcher(str);
			if (m.find())
			{
				String[] result = str.split(",");
				for(int i=0;i< result.length;i++)
				{
					System.out.println(result[i]);
					pattern = "auto_increment";
					p = Pattern.compile(pattern);
					m = p.matcher(result[i]);
					if(m.find())
					{
						result[i]=result[i].replaceAll("int","serial");
						result[i]=result[i].replaceAll("unsigned","");
						result[i] = result[i].replaceAll("auto_increment", "");
					}
					pattern = "unsigned";
					p = Pattern.compile(pattern);
					m = p.matcher(result[i]);
					if(m.find())
					{
						result[i]=result[i].replaceAll("int","");
						result[i]=result[i].replaceAll("unsigned","bigint");
					}
					System.out.println(result[i]);
				}
				String newString="";
				for(int i=0;i< result.length;i++)
				{
					newString+=result[i];
					if(i!=result.length-1)
						newString+=",";
				}
				str=newString;
			}
			pattern = "unsigned";
			p = Pattern.compile(pattern);
			m = p.matcher(str);
			if(m.find())
			{
				str=str.replaceAll("unsigned","bigint");
				str=str.replaceAll("int","");
			}
			System.out.println(str);
		}
	}

	@Test
	void TestMultiQueue()
	{
		for(int i=0;i<30000;i=i+3)
		{
			String sql = "insert into t3 values( "+ String.valueOf(i) + ", \'"+"n"+String.valueOf(i)+"\', "+"\'i"+
					String.valueOf(i)+"\');";
			sendSqlService.sendSqlBatch(sql,"batch_1");
			sql = "insert into t3 values( "+ String.valueOf(i+1) + ", \'"+"n"+String.valueOf(i+1)+"\', "+"\'i"+
					String.valueOf(i+1)+"\');";
			sendSqlService.sendSqlBatch(sql,"batch_2");
			sql = "insert into t3 values( "+ String.valueOf(i+2) + ", \'"+"n"+String.valueOf(i+2)+"\', "+"\'i"+
					String.valueOf(i+2)+"\');";
			sendSqlService.sendSqlBatch(sql,"batch_3");
		}
	}

}
