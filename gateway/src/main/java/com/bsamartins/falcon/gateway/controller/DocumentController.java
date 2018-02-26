package com.bsamartins.falcon.gateway.controller;

import com.bsamartins.falcon.gateway.service.DocumentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * Documents endpoint controller
 */
@RestController
@RequestMapping("/api/documents")
public class DocumentController {

    @Autowired
    private DocumentService documentService;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Payload creation handler.
     *
     * Takes any type of JSON payload and saves it.
     * Payload can be a JSON Object, Array or scalar value.
     *
     *
     * @param input Byte array representation of a JSON payload
     * @return ResponseEntity with 201 status code if no error occurred
     * @throws Exception
     */
    @PostMapping(consumes = { MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_JSON_UTF8_VALUE })
    public Mono<ResponseEntity> create(@RequestBody ByteArrayResource input) throws Exception {
        Object obj = objectMapper.readValue(input.getInputStream(), Object.class);
        return documentService.create(obj)
                .then(Mono.just(ResponseEntity.status(HttpStatus.CREATED).build()));
    }
}
