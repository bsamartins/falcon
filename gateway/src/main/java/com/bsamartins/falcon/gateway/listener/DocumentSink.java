package com.bsamartins.falcon.gateway.listener;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;

public interface DocumentSink {

    String DOCUMENTS_CREATE = "documents-create";
    String DOCUMENTS_CREATED = "documents-created";

    @Input(DOCUMENTS_CREATED)
    SubscribableChannel input();

    @Output(DOCUMENTS_CREATE)
    MessageChannel createdDocuments();
}
