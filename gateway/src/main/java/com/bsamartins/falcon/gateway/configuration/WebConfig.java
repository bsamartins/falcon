package com.bsamartins.falcon.gateway.configuration;

import com.bsamartins.falcon.gateway.websocket.DocumentWebsocketHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.web.reactive.HandlerAdapter;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.config.ResourceHandlerRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableWebFlux
public class WebConfig implements WebFluxConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/");
    }

    /**
     * WebSocket handler mapping configuration
     *
     * @return The handler mapping
     */
    @Bean
    public HandlerMapping webSocketMapping() {
        Map<String, Object> map = new HashMap<>();
        map.put("/documents", documentWebsocketHandler());
        SimpleUrlHandlerMapping simpleUrlHandlerMapping = new SimpleUrlHandlerMapping();
        simpleUrlHandlerMapping.setUrlMap(map);

        simpleUrlHandlerMapping.setOrder(10);
        return simpleUrlHandlerMapping;
    }

    /**
     * Document WebSocket handler
     *
     * @return WebSocket handler
     */
    @Bean
    public DocumentWebsocketHandler documentWebsocketHandler() {
        return new DocumentWebsocketHandler();
    }

    /**
     * WebSocket handler adapter configuration
     * Application will not handle WS requests unless adapter is configured
     *
     * @return WebSocket handler adapter
     */
    @Bean
    HandlerAdapter wsHandlerAdapter() {
        return new WebSocketHandlerAdapter();
    }

    @Bean
    ObjectMapper objectMapper() {
        return new Jackson2ObjectMapperBuilder().build();
    }
}
