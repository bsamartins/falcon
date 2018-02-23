package com.bsamartins.falcon.documentservice.service;

import com.bsamartins.falcon.documentservice.model.Document;
import com.bsamartins.falcon.documentservice.repository.DocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Encapsulates logic for documents
 *
 */
@Service
public class DocumentService {

    @Autowired
    private DocumentRepository documentRepository;

    /**
     * Gets all documents
     *
     * @return Stream of documents
     */
    public Flux<Document> findAll() {
        return documentRepository.findAll();
    }

    /**
     * Saves the document
     *
     * @param document Document to be stored
     * @return Saved document
     */
    public Mono<Document> save(Document document) {
        return documentRepository.save(document);
    }
}
