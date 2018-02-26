package com.bsamartins.falcon.documentservice.config;

import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractReactiveMongoConfiguration;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;
import org.testcontainers.containers.GenericContainer;

@Configuration
@EnableReactiveMongoRepositories
public class MongoTestConfig extends AbstractReactiveMongoConfiguration {

    @Bean
    public GenericContainer mongoTestContainer() {
        GenericContainer container = new GenericContainer("mongo:3.6");
        container.start();
        return container;
    }

    @Bean public int mongoPort() {
        return mongoTestContainer().getMappedPort(27017);
    }

    @Override
    public MongoClient reactiveMongoClient() {
        return MongoClients.create(String.format("mongodb://localhost:%d", mongoPort()));
    }

    @Override
    protected String getDatabaseName() {
        return "test";
    }
}
