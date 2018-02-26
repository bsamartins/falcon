package com.bsamartins.falcon.gateway.websocket;

import com.bsamartins.falcon.gateway.service.DocumentService;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class DocumentWebsocketHandler implements WebSocketHandler {

    private final static Logger LOGGER = LoggerFactory.getLogger(DocumentService.class);

    @Autowired
    private Publisher<Message<byte[]>> createdDocumentsChannel;

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        LOGGER.info("New WebSocket session: {}", session.getId());

        // Only way to check socket has been closed is to check the
        // the receive channel on then
        session.receive().doOnTerminate(() -> {
            LOGGER.info("WebSocket session terminated: {}", session.getId());
            session.close();
        }).subscribe();

        return session.send(Flux.from(createdDocumentsChannel)
                .map(msg -> new String(msg.getPayload()))
                .doOnNext(msg -> LOGGER.debug("Pushing message to WebSocket: {} -> {}", session.getId(), msg))
                .map(session::textMessage));
    }
}
