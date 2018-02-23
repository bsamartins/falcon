package com.bsamartins.falcon.documentservice.controller;

import com.bsamartins.falcon.documentservice.model.Document;
import com.bsamartins.falcon.documentservice.service.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * REST controllet for Documents
 */
@RestController
@RequestMapping("/documents")
public class DocumentController {

    @Autowired
    private DocumentService documentService;

    /**
     * Returns all documents
     * @return List of documents
     */
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<List<Document>> findAll() {
        return documentService.findAll()
                .collectList();
    }

}
