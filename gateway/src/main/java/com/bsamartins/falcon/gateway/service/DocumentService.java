package com.bsamartins.falcon.gateway.service;

import com.bsamartins.falcon.gateway.listener.DocumentSink;
import com.bsamartins.falcon.gateway.model.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class DocumentService {

    @Autowired
    private DocumentSink documentSink;

    public Flux<Document> findAll() {
//        return documentRepository.findAll();
        return Flux.empty();
    }

    public Mono<Void> create(String payload) {
        Document document = new Document();
        document.setPayload(payload);
        documentSink.createdDocuments().send(MessageBuilder.withPayload(document).build());
        return Mono.empty();
    }

}
