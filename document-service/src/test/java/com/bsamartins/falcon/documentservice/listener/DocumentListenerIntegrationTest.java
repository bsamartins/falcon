package com.bsamartins.falcon.documentservice.listener;

import com.bsamartins.falcon.documentservice.config.MongoTestConfig;
import com.bsamartins.falcon.documentservice.controller.DocumentControllerTest;
import com.bsamartins.falcon.documentservice.repository.DocumentRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.messaging.SubscribableChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@WebFluxTest
@ContextConfiguration(classes = MongoTestConfig.class)
@ComponentScan("com.bsamartins.falcon.documentservice")
public class DocumentListenerIntegrationTest {

    @Autowired
    private SubscribableChannel documents;

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private ReactiveMongoTemplate reactiveMongoTemplate;

    @AfterEach
    public void tearDown() {
        // Clean up database after test
        reactiveMongoTemplate.getCollectionNames()
                .flatMap(reactiveMongoTemplate::dropCollection)
                .blockLast();
    }

    @Test
    public void testSave() {
        DocumentControllerTest.TestObjectPayload payload = new DocumentControllerTest.TestObjectPayload();
        payload.setMessage("Hello World");

        assertTrue(documents.send(MessageBuilder.withPayload("Hello World").build()));
        StepVerifier.create(documentRepository.findAll())
                .assertNext(d -> assertEquals(d.getPayload(), "Hello World"))
                .verifyComplete();
    }

    @Test
    public void testPush() {
        DocumentControllerTest.TestObjectPayload payload = new DocumentControllerTest.TestObjectPayload();
        assertTrue(documents.send(MessageBuilder.withPayload("Hello World").build()));
    }
}
