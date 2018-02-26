package com.bsamartins.falcon.gateway.configuration;

import com.bsamartins.falcon.gateway.messaging.DocumentChannels;
import org.reactivestreams.Publisher;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.PublishSubscribeChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.messaging.Message;

@Configuration
@EnableBinding(DocumentChannels.class)
public class MessagingConfig {

    /**
     * Creates publisher for created documents from output channel from
     *
     * @param channels Document channel definition
     * @return Message publisher
     */
    @Bean
    Publisher<Message<byte[]>> createdDocumentsPublisher(DocumentChannels channels) {
        return IntegrationFlows.from(channels.createdDocumentsChannel())
                .channel(createdDocumentsChannel())
                .toReactivePublisher();
    }

    /**
     * Channel for the created documents.
     * Required to allow messages to be delivered to all
     * subscribers
     *
     * @return Channel
     */
    @Bean
    PublishSubscribeChannel createdDocumentsChannel() {
        return new PublishSubscribeChannel();
    }
}
