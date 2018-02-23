package com.bsamartins.falcon.documentservice.listener;

import com.bsamartins.falcon.documentservice.controller.DocumentControllerTest;
import com.rabbitmq.client.*;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

public class FalconClient {

    private static final String ROUTING_KEY = "bm_key";

    public static void main(String ...args) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        CountDownLatch latch = new CountDownLatch(1);
        try(Connection conn = factory.newConnection()) {
            Channel channel = conn.createChannel();

            channel.exchangeDeclare(DocumentSink.SINK_NEW, "topic", true);
            channel.exchangeDeclare(DocumentSink.SINK_CREATED, "fanout", true);

            listeners(channel, 2);
            publisher(channel, 100);
            System.out.println("Waiting...");
            latch.await();
        }
    }

    private static void publisher(Channel channel, int count) throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        Flux.range(1, count)
                .parallel(4)
                .doOnNext(c -> {
                    try {
                        DocumentControllerTest.TestObjectPayload payload = new DocumentControllerTest.TestObjectPayload();
                        payload.setMessage(c + " Hello World");
                        byte[] messageBytes = mapper.writeValueAsBytes(payload);

                        channel.basicPublish(DocumentSink.SINK_NEW,
                            ROUTING_KEY,
                            null,
                            messageBytes);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }).subscribe();
    }

    private static void listeners(Channel channel, int count) throws Exception {
        for(int i=0; i < count; i++) {
            listener(channel, "consumer[" + i + "]", ROUTING_KEY);
        }
    }

    private static void listener(Channel channel, String id, String routingKey) throws IOException {
        String queueName = channel.queueDeclare().getQueue();
        channel.queueBind(queueName, DocumentSink.SINK_CREATED, routingKey);
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
