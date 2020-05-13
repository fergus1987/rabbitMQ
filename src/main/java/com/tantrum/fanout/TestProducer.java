package com.tantrum.fanout;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.tantrum.RabbitMQUtil;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

// 消息生成者
public class TestProducer {

    private final static String EXCHANGE_NAME = "fanout_exchange";

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

        //通过通道发送消息到队列
        for (int i = 0; i < 100; i++) {
            String message = "direct 消息 " + i;
            /**
             * 参数列表：
             * 1 交换器名称
             * 2 Routing Key
             * 3 传递 AMQP.BasicProperties属性信息
             * 4 消息
             *
             * Routing Key(路由键)：一个 String值，用于定义路由规则，
             * 在队列绑定的时候需要指定路由键，在生产者发布消息的时候也需要指定路由键，
             * 当消息的路由键和队列绑定的路由键匹配时，消息就会发送到该队列
             */
            channel.basicPublish(EXCHANGE_NAME, "", null, message.getBytes("UTF-8"));
            System.out.println("发送消息： " + message);
        }

        //关闭通道和连接
        channel.close();
        connection.close();
    }
}
