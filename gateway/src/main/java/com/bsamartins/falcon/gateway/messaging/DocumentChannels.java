package com.bsamartins.falcon.gateway.messaging;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;

/**
 * Document channel definitions
 */
public interface DocumentChannels {

    String SINK_NEW = "documents";
    String SINK_CREATED = "documents-created";

    @Input(SINK_CREATED)
    SubscribableChannel createdDocumentsChannel();

    @Output(SINK_NEW)
    MessageChannel newDocumentsChannel();
}
