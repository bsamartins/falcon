package com.bsamartins.falcon.gateway.websocket.config;

import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.HostPortWaitStrategy;

@Configuration
public class TestMessagingConfig {

    @Bean
    GenericContainer rabbitmqContainer() {
        // Initializes rabbitmq container and waits until it's ready
        GenericContainer container = new GenericContainer("rabbitmq:3.7");
        container.addExposedPort(5672);
        container.waitingFor(new HostPortWaitStrategy());
        container.start();
        return container;
    }

    @Bean
    int rabbitmqPort() {
        return rabbitmqContainer().getMappedPort(5672);
    }

    @Bean
    @Primary
    RabbitProperties rabbitmqProperties() {
        RabbitProperties properties = new RabbitProperties();
        properties.setPort(rabbitmqPort());
        return properties;
    }
}