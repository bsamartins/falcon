package com.bsamartins.falcon.documentservice.messaging;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;

/*
 * Channel definitions for documents
 */
public interface DocumentChannels {

    String SINK_NEW = "documents";
    String SINK_CREATED = "documents-created";

    /*
     * Channel where new json payloads for creation will be
     * coming from
     */
    @Input(SINK_NEW)
    SubscribableChannel input();

    /*
     * Channel to broadcast created documents once they have been
     * created
     */
    @Output(SINK_CREATED)
    MessageChannel created();
}
