package com.bsamartins.falcon.documentservice.listener;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.time.Duration;

public class AmqpClientTest {

    private static final String EXCHANGE = "bm_exchange";
    private static final String ROUTING_KEY = "bm_key";

    public static void main(String ...args) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();

        try(Connection conn = factory.newConnection()) {
            Channel channel = conn.createChannel();

            channel.exchangeDeclare(EXCHANGE, "fanout", true);
            listeners(channel);
            publisher(channel);
            System.out.println("Waiting...");
            Thread.sleep(Duration.ofSeconds(60).toMillis());
        }
    }

    private static void publisher(Channel channel) throws IOException {
        byte[] messageBytes = "Hello World".getBytes();
        channel.basicPublish(EXCHANGE,
                ROUTING_KEY,
                null,
                messageBytes);
    }

    private static void listeners(Channel channel) throws Exception {
        listener(channel, "c1", ROUTING_KEY);
        listener(channel, "c2", "xpto");
    }

    private static void listener(Channel channel, String id, String routingKey) throws IOException {
        String queueName = channel.queueDeclare().getQueue();
        channel.queueBind(queueName, EXCHANGE, routingKey);
        channel.basicConsume(queueName, new MessageHandler(channel, id));
        System.out.println(id + " -> " + queueName);
    }

    private static class MessageHandler extends DefaultConsumer {

        private String id;

        MessageHandler(Channel channel, String id) {
            super(channel);
            this.id = id;
        }

        @Override
        public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
            System.out.println(this.id + " " + new String(body));
        }
    }
}
