package com.bsamartins.falcon.gateway.websocket.websocket;

import com.bsamartins.falcon.gateway.websocket.DocumentWebsocketHandler;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.mockito.quality.Strictness;
import org.reactivestreams.Publisher;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.ReplayProcessor;

import java.time.Duration;
import java.util.List;

import static junit.framework.TestCase.assertSame;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class DocumentWebsocketHandlerTest {

    @InjectMocks
    private DocumentWebsocketHandler documentWebsocketHandler;

    @Mock
    private WebSocketSession webSocketSession;

    private ReplayProcessor<Message<byte[]>> createdDocumentsPublisher = ReplayProcessor.create();

    private MockitoSession session;

    @BeforeEach
    void setup() {
        this.session = Mockito.mockitoSession()
                .initMocks(this)
                .strictness(Strictness.STRICT_STUBS)
                .startMocking();
        // Because mockito cannot mock or spy final classes
        ReflectionTestUtils.setField(this.documentWebsocketHandler, "createdDocumentsChannel", createdDocumentsPublisher);
    }

    @AfterEach
    void tearDown() {
        this.session.finishMocking();
    }

    @Test
    void send() {
        String payload = "Hello World";
        byte[] payloadBytes = payload.getBytes();
        Flux.just(MessageBuilder.withPayload(payloadBytes).build())
                .subscribeWith(this.createdDocumentsPublisher);
        WebSocketMessage wsMessage = new WebSocketMessage(WebSocketMessage.Type.TEXT, new DefaultDataBufferFactory().allocateBuffer());

        // if we return an empty flux session will be terminated,
        // which we don't want so we just create an empty flux that
        // emits nothing
        when(webSocketSession.receive()).thenReturn(Flux.create(s -> {}));
        when(webSocketSession.send(any())).thenReturn(Mono.empty());
        when(webSocketSession.textMessage(payload)).thenReturn(wsMessage);

        documentWebsocketHandler.handle(webSocketSession)
                .block(Duration.ofSeconds(1));

        ArgumentCaptor<Publisher<WebSocketMessage>> wsPublisher = ArgumentCaptor.forClass(Publisher.class);

        verify(webSocketSession).send(wsPublisher.capture());
        verify(webSocketSession, never()).close();

        List<WebSocketMessage> result = Flux.from(wsPublisher.getValue())
                .collectList()
                .block(Duration.ofSeconds(1));

        assertEquals(1, result.size());

        WebSocketMessage wsResult = result.get(0);
        assertSame(wsMessage, wsResult);
    }

    @Test
    void closeOnDisconnect() {
        when(webSocketSession.receive()).thenReturn(Flux.empty());
        when(webSocketSession.send(any())).thenReturn(Mono.empty());

        documentWebsocketHandler.handle(webSocketSession)
                .block(Duration.ofSeconds(1));

        verify(webSocketSession).close();
    }
}
