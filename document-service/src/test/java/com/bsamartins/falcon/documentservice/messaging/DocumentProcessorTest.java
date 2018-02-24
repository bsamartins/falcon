package com.bsamartins.falcon.documentservice.messaging;

import com.bsamartins.falcon.documentservice.controller.DocumentControllerTest;
import com.bsamartins.falcon.documentservice.model.Document;
import com.bsamartins.falcon.documentservice.service.DocumentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.mockito.quality.Strictness;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DocumentProcessorTest {

    @InjectMocks
    private DocumentProcessor documentProcessor = new DocumentProcessor();

    @Mock
    private DocumentService documentService;

    @Spy
    private ObjectMapper objectMapper;

    private MockitoSession mockitoSession;

    @BeforeEach
    void setup() {
        this.objectMapper = Jackson2ObjectMapperBuilder
                .json()
                .build();
        this.mockitoSession = Mockito.mockitoSession()
                .initMocks(this)
                .strictness(Strictness.STRICT_STUBS)
                .startMocking();
    }

    @AfterEach
    void tearDown() {
        this.mockitoSession.finishMocking();
    }

    @Test
    void processDocument() throws Exception {
        DocumentControllerTest.TestObjectPayload obj = new DocumentControllerTest.TestObjectPayload("Hello World");
        Document savedDocument = new Document("test");

        when(documentService.save(any())).thenReturn(Mono.just(savedDocument));

        Flux<Document> result = documentProcessor.processDocument(toMessagePublisher(obj));
        StepVerifier.create(result.checkpoint())
                .assertNext((d) -> assertSame(savedDocument, d))
                .verifyComplete();

        ArgumentCaptor<Document> argumentCaptor = ArgumentCaptor.forClass(Document.class);
        verify(documentService).save(argumentCaptor.capture());

        Document inputDocument = argumentCaptor.getValue();

        assertNotNull(inputDocument);
        assertNotNull(inputDocument.getPayload());

        HashMap<String, Object> payload = (HashMap<String, Object>) inputDocument.getPayload();
        assertEquals(obj.getMessage(), payload.get("message"));

    }

    private Flux<Message<byte[]>> toMessagePublisher(Object obj) throws Exception {
        Message<byte[]> message = MessageBuilder.withPayload(convertObject(obj))
                .build();
        return Flux.just(message);
    }

    private byte[] convertObject(Object obj) throws Exception {
        return objectMapper.writeValueAsBytes(obj);
    }
}
