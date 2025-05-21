package pl.com.shugo.reactivechat.handlers;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class EchoHandler implements WebSocketHandler {

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        Flux<WebSocketMessage> echoes = session.receive()
                .map(WebSocketMessage::getPayloadAsText)
                .map(text -> "Echo: " + text)
                .map(session::textMessage);
        return session.send(echoes);
     }
}
