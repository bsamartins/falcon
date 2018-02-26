package com.bsamartins.falcon.gateway.websocket.controller;

import com.bsamartins.falcon.gateway.configuration.MessagingConfig;
import com.bsamartins.falcon.gateway.controller.DocumentController;
import com.bsamartins.falcon.gateway.websocket.config.TestMessagingConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

@ExtendWith(SpringExtension.class)
@WebFluxTest(controllers = DocumentController.class)
@ContextConfiguration(classes =  {
        MessagingConfig.class,
        TestMessagingConfig.class
})
@ComponentScan("com.bsamartins.falcon.gateway")
public class DocumentControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    public void create() {
        webTestClient.post().uri("/api/documents")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromObject("{ \"prop\": \"value\" }"))
                .exchange()
                .expectStatus().isCreated();
    }
}
