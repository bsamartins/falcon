package com.bsamartins.falcon.gateway.controller;

import com.bsamartins.falcon.gateway.service.DocumentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.BodyExtractors;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 * Documents endpoint controller
 */
@RestController
@RequestMapping("/api/documents")
public class DocumentController {

    private static final String DOCUMENTS_BASE_URL = "/documents";

    @Value("${falcon.services.documents.host}")
    private String host;

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

    /**
     * Proxies call to /documents endpoint
     *
     * @param serverResponse Server response handler
     * @return Void Mono
     */
    @GetMapping
    public Mono<Void> findAll(ServerHttpResponse serverResponse) {
        return WebClient.builder()
                .baseUrl(host + DOCUMENTS_BASE_URL)
                .build()
                .get()
                .exchange()
                .flatMap(response -> {
                    serverResponse.setStatusCode(response.statusCode());
                    return serverResponse.writeWith(response.body(BodyExtractors.toDataBuffers()));
                });
    }
}
