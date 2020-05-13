package com.tantrum.Topic;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.tantrum.RabbitMQUtil;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 消息生成者
 * 分别在四个路由："usa.news", "usa.weather", "europe.news", "europe.weather"上发布
 * "美国新闻", "美国天气", "欧洲新闻", "欧洲天气"
 */
public class TestTopicProducer {

    private final static String EXCHANGE_NAME = "topics_exchange";

    public static void main(String[] args) throws IOException, TimeoutException {

        //判断服务器是否启动
        RabbitMQUtil.checkServer();

        //创建连接工厂，并进行配置
        ConnectionFactory factory = new ConnectionFactory();
        //设置RabbitMQ服务器的地址
        factory.setHost("localhost");
        //端口
        factory.setPort(5672);
        //指定HOST
        factory.setVirtualHost("/");
        //登录名
        factory.setUsername("guest");
        //密码
        factory.setPassword("guest");

        //创建一个新的连接
        Connection connection = factory.newConnection();
        //创建一个通道
        Channel channel = connection.createChannel();

        String[] routing_keys = new String[]{"usa.news", "usa.weather",
                "europe.news", "europe.weather", "usa.news3"};
        String[] messages = new String[]{"美国新闻", "美国天气",
                "欧洲新闻", "欧洲天气", "美国新闻3"};

        //通过通道发送消息到队列
        for (int i = 0; i < routing_keys.length; i++) {
            String routingKey = routing_keys[i];
            String message = messages[i];
            /**
             * 参数列表：
             * 1 交换器名称
             * 2 Routing Key
             * 3 传递 AMQP.BasicProperties属性信息
             * 4 消息
             *
             * Routing Key(路由键)：一个String值，用于定义路由规则，
             * 在队列绑定的时候需要指定路由键，在生产者发布消息的时候也需要指定路由键，
             * 当消息的路由键和队列绑定的路由键匹配时，消息就会发送到该队列
             */
            channel.basicPublish(EXCHANGE_NAME, routingKey, null, message.getBytes());
            System.out.printf("发送消息到路由：%s, 内容是: %s%n", routingKey, message);
        }

        //关闭通道和连接
        channel.close();
        connection.close();
    }
}
