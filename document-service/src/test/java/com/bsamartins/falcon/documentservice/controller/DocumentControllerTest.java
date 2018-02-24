package com.bsamartins.falcon.documentservice.controller;

import com.bsamartins.falcon.documentservice.config.MongoTestConfig;
import com.bsamartins.falcon.documentservice.configuration.MessagingConfig;
import com.bsamartins.falcon.documentservice.model.Document;
import com.bsamartins.falcon.documentservice.repository.DocumentRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.cloud.stream.reactive.ReactiveSupportAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.Collections;

@ExtendWith(SpringExtension.class)
@WebFluxTest(controllers = DocumentController.class)
@ContextConfiguration(classes = {
        MongoTestConfig.class,
        MessagingConfig.class,
        ReactiveSupportAutoConfiguration.class
})
@ComponentScan("com.bsamartins.falcon.documentservice")
public class DocumentControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private ReactiveMongoTemplate reactiveMongoTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @AfterEach
    public void tearDown() {
        // Clean up database after test
        reactiveMongoTemplate.getCollectionNames()
                .flatMap(reactiveMongoTemplate::dropCollection)
                .blockLast();
    }

    @Test
    public void testFindAll() {
        TestObjectPayload payload = new TestObjectPayload("Hello World");

        Document d0 = new Document(payload);
        Document d1 = new Document(Collections.singletonList(5));
        Document d2 = new Document("Hello World");
        Document d3 = new Document(null);

        documentRepository.save(d0).block();
        documentRepository.save(d1).block();
        documentRepository.save(d2).block();
        documentRepository.save(d3).block();

        webTestClient.get().uri("/documents")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .expectBody()
                .jsonPath("$").isArray()
                .jsonPath("$.length()").isEqualTo(4)

                // Payload as document
                .jsonPath("$[0].id").exists()
                .jsonPath("$[0].payload").isMap()
                .jsonPath("$[0].payload.message").isEqualTo("Hello World")

                // Payload as array
                .jsonPath("$[1].id").exists()
                .jsonPath("$[1].payload").isArray()
                .jsonPath("$[1].payload[0]").isEqualTo(5)

                // Payload as string
                .jsonPath("$[2].id").exists()
                .jsonPath("$[2].payload").isEqualTo("Hello World")

                // Payload null
                .jsonPath("$[3].id").exists()
                .jsonPath("$[3].payload").isEmpty();
    }

    public static class TestObjectPayload {

        private String message;

        public TestObjectPayload(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }
}
