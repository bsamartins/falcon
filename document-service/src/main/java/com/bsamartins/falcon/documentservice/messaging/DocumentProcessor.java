package com.bsamartins.falcon.documentservice.messaging;

import com.bsamartins.falcon.documentservice.model.Document;
import com.bsamartins.falcon.documentservice.service.DocumentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;

/**
 * Handler for incoming payload creation requests
 */
@Component
public class DocumentProcessor {

    @Autowired
    private DocumentService documentService;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Listens to channel and handles incoming messages from,
     * publishing them to target output channel after they have been
     * successfully processed
     *
     * @param stream Message stream from input channel
     * @return Stream of processed documents
     */
    @StreamListener
    @Output(DocumentChannels.SINK_CREATED)
    public Flux<Document> processDocument(@Input(DocumentChannels.SINK_NEW) Flux<Message<byte[]>> stream) {
        return stream
                .flatMap(this::convertMessage)
                .map(Document::new)
                .flatMap(documentService::save)
                .log();
    }

    private Mono<Object> convertMessage(Message<byte[]> message) {
        try {
            Object obj = objectMapper.readValue(message.getPayload(), Object.class);
            return Mono.just(obj);
        } catch (IOException ioe) {
            return Mono.error(ioe);
        }
    }

}
