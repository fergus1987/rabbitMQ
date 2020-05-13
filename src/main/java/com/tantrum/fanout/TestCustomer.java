package com.tantrum.fanout;

import cn.hutool.core.util.RandomUtil;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.tantrum.RabbitMQUtil;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class TestCustomer {

    private final static String EXCHANGE_NAME = "fanout_exchange";
    private final static String QUEUE_NAME = "fanout_queue";

    public static void main(String[] args) throws IOException, TimeoutException {

        //为当前消费者取随机名
        final String name = "consumer-" + RandomUtil.randomString(5);
        //判断服务器是否启动
        RabbitMQUtil.checkServer();

        //创建连接工厂，并进行配置
        ConnectionFactory factory = new ConnectionFactory();
        //设置RabbitMQ服务器的地址
        factory.setHost("localhost");

        //创建一个新的连接
        Connection connection = factory.newConnection();
        //创建一个通道
        Channel channel = connection.createChannel();

        /**
         * 创建交换器
         *
         * 参数列表：
         * 1 交换器名称
         * 2 交换器类型
         *
         * fanout类型：不处理任何的路由键，它会把所有发送到该交换器的消息路由到所有与该交换器绑定的队列中
         *
         * 该方法返回值是 Exchange.Declare-ok，用来标识成功创建了一个交换器，
         * 即在客户端声明了一个交换器之后，需要等待服务器的返回(服务器会返回 Exchange.Declare-Ok 这个 AMQP 命令)
         */
        channel.exchangeDeclare(EXCHANGE_NAME, "fanout");

        //创建队列
        channel.queueDeclare(QUEUE_NAME, false, false, true, null);

        /**
         * 创建队列与交换器的绑定
         *
         * 参数列表：
         * 1 队列名称
         * 2 交换器名称
         * 3 Routing Key，也可以说是 Binding Key，很多时候可以理解成同一个东西
         *
         * Routing Key(路由键)：一个String值，用于定义路由规则，
         * 队列绑定的时候需要指定路由键，在生产者发布消息的时候也需要指定路由键，
         * 当消息的路由键和队列绑定的路由键匹配时，消息就会发送到该队列
         */
        channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, "");

        System.out.println(name + " 等待接受消息");

        /**
         * 创建消费者
         *
         * DefaultConsumer类实现了Consumer接口，通过传入一个频道，
         * 告诉服务器我们需要那个频道的消息，如果频道中有消息，就会执行回调函数handleDelivery
         */
        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope,
                                       AMQP.BasicProperties properties, byte[] body)
                    throws IOException {
                String message = new String(body, "UTF-8");
                System.out.println(name + " 接收到消息 '" + message + "'");
            }
        };

        /**
         * 自动回复队列应答 -- RabbitMQ中的消息确认机制
         *
         * 第二个参数为 autoAck（应答模式），若为 true（自动应答），即消费者获取到消息，该消息就会从队列中删除掉；
         * 若为 true（手动应答），当从队列中取出消息后，需要程序员手动调用方法应答，如果没有应答，
         * 该消息还会再放进队列中，就会出现该消息一直没有被消费掉的现象
         */
        channel.basicConsume(QUEUE_NAME, true, consumer);
    }
}