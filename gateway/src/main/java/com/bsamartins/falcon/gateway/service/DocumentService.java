package com.bsamartins.falcon.gateway.service;

import com.bsamartins.falcon.gateway.messaging.DocumentChannels;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class DocumentService {

    private final static Logger LOGGER = LoggerFactory.getLogger(DocumentService.class);

    @Autowired
    private DocumentChannels documentChannels;

    @Autowired
    private ObjectMapper objectMapper;

    public Mono<Boolean> create(Object payload) {
        LOGGER.info("Creating payload: {}", payload);
        try {
            Message<byte[]> message = MessageBuilder.withPayload(objectMapper.writeValueAsBytes(payload))
                    .build();
            documentChannels.newDocumentsChannel().send(message);
            return Mono.empty();
        } catch(JsonProcessingException jse) {
            return Mono.error(jse);
        }
    }
}
