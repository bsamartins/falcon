package com.bsamartins.falcon.documentservice.configuration;

import com.bsamartins.falcon.documentservice.messaging.DocumentChannels;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableBinding(DocumentChannels.class)
public class MessagingConfig {
}
