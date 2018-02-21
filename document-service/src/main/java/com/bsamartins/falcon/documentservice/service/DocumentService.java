package com.bsamartins.falcon.documentservice.service;

import com.bsamartins.falcon.documentservice.model.Document;
import com.bsamartins.falcon.documentservice.repository.DocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class DocumentService {

    @Autowired
    private DocumentRepository documentRepository;

    public Flux<Document> findAll() {
        return documentRepository.findAll();
    }

    public Mono<Document> save(Document document) {
        return documentRepository.save(document);
    }
}
