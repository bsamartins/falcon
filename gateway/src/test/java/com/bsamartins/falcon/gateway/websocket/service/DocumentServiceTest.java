package com.bsamartins.falcon.gateway.websocket.service;

import com.bsamartins.falcon.gateway.messaging.DocumentChannels;
import com.bsamartins.falcon.gateway.service.DocumentService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoSession;
import org.mockito.quality.Strictness;
import org.springframework.messaging.MessageChannel;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class DocumentServiceTest {

    @InjectMocks
    private DocumentService documentService;

    @Mock
    private ObjectMapper mapper;

    @Mock
    private DocumentChannels documentChannels;

    private MockitoSession session;

    @BeforeEach
    void setup() {
        this.session = Mockito.mockitoSession()
                .initMocks(this)
                .strictness(Strictness.STRICT_STUBS)
                .startMocking();
    }

    @AfterEach
    void tearDown() {
        this.session.finishMocking();
    }

    @Test
    void create() throws Exception {
        String payload = "test";
        byte[] payloadAsBytes = payload.getBytes();

        MessageChannel channel = mock(MessageChannel.class);

        when(mapper.writeValueAsBytes(payload)).thenReturn(payloadAsBytes);
        when(documentChannels.newDocumentsChannel()).thenReturn(channel);

        StepVerifier.create(documentService.create(payload))
                .verifyComplete();

        verify(mapper).writeValueAsBytes(payload);
        verify(documentChannels).newDocumentsChannel();
        verify(channel).send(any());
    }

    @Test
    void createHandleError() throws Exception {
        String payload = "test";

        when(mapper.writeValueAsBytes(payload)).thenThrow(new TestJsonProcessingException("test"));

        StepVerifier.create(documentService.create(payload))
                .expectError(TestJsonProcessingException.class);

        verify(mapper).writeValueAsBytes(payload);
    }

    class TestJsonProcessingException extends JsonProcessingException {
        TestJsonProcessingException(String msg) {
            super(msg);
        }
    }
}
