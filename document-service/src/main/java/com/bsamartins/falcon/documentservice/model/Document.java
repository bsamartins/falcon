package com.bsamartins.falcon.documentservice.model;

import org.springframework.data.annotation.Id;

@org.springframework.data.mongodb.core.mapping.Document(collection = "documents")
public class Document {

    @Id
    private String id;
    private Object payload;

    public Document(Object payload) {
        this.payload = payload;
    }

    public String getId() {
        return id;
    }

    public Object getPayload() {
        return payload;
    }
}
