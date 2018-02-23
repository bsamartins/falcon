package com.bsamartins.falcon.gateway.controller;

import com.bsamartins.falcon.gateway.model.Document;
import com.bsamartins.falcon.gateway.service.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/documents")
public class DocumentController {

    @Autowired
    private DocumentService documentService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<List<Document>> findAll() {
        return documentService.findAll()
                .collectList();
    }

    @PostMapping()
    public Mono<Void> findAll(@RequestBody String payload) {
        return documentService.create(payload);
    }

}
