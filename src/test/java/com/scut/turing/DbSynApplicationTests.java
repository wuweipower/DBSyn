package com.scut.turing;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.scut.turing.service.UpdateService;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

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
		connectionFactory.setVirtualHost("vh");
		connectionFactory.setUsername("test");
		connectionFactory.setPassword("test");
		try {
			// 3: 从连接工厂中获取连接
			Connection connection = connectionFactory.newConnection("生产者");
			// 4: 从连接中获取通道channel
			Channel channel = connection.createChannel();
			channel.queueDeclare("queue1", false, false, false, null);
			// 6： 准备发送消息的内容
			String message = "你好，学相伴！！！";
			channel.basicPublish("sql_exchange", "queue1", null, message.getBytes());
			System.out.println("消息发送成功!");
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("发送消息出现异常...");
		}

	}

}
