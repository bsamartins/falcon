package com.bsamartins.falcon.documentservice.listener;

import com.bsamartins.falcon.documentservice.model.Document;
import com.bsamartins.falcon.documentservice.service.DocumentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.converter.CompositeMessageConverter;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;

/**
 * Handler for incoming payload creation requests
 */
@EnableBinding(DocumentSink.class)
public class DocumentListener {

    @Autowired
    private DocumentService documentService;

    @Autowired
    private CompositeMessageConverter messageConverter;

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
    @Output(DocumentSink.SINK_CREATED)
    public Flux<Document> processDocument(@Input(DocumentSink.SINK_NEW) Flux<Message<byte[]>> stream) {
        return stream
                .flatMap(this::convertMessage)
                .map(Document::new)
                .flatMap(documentService::save)
                .log();
    }

    private Mono<Object> convertMessage(Message<byte[]> message) {
        try {
            String payload = (String) messageConverter.fromMessage(message, String.class);
            Object obj = objectMapper.readValue(payload, Object.class);
            return Mono.just(obj);
        } catch (IOException ioe) {
            return Mono.error(ioe);
        }
    }

}
