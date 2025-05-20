package pl.com.shugo.reactivechat.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;

import jdk.jfr.Event;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Component
public class ChatWebSocketHandler implements WebSocketHandler {

    private final Sinks.Many<Event> chatHistory;
    private final ObjectMapper objectMapper;

    public ChatWebSocketHandler() {
        this.chatHistory = Sinks.many().replay().limit(1000);
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        AtomicReference<Event> lastReceivedMessage = new AtomicReference<Event>();

        Mono<Void> input = session.receive()
                .map(WebSocketMessage::getPayloadAsText)
                .map(this::toEvent)
                .doOnNext(event -> {
                    lastReceivedMessage.set(event);
                    chatHistory.tryEmitNext(event);
                })
                .doOnComplete(() -> {
                    Event event = lastReceivedMessage.get();
                    if (event != null) {
                        event.commit();
                        chatHistory.tryEmitNext(event);
                    }
                    log.info("WebSocket session completed!");
                })
                .then();

        //wysyłanie wiadomośći
        Mono<Void> output = session.send(
                chatHistory.asFlux()
                        .map(this::toString)
                        .map(session::textMessage)
        );
        return Mono.zip(input, output).then();
    }

    @SneakyThrows
    private Event toEvent(String message) {
        return objectMapper.readValue(message, Event.class);
    }

    @SneakyThrows
    private String toString(Event event) {
        return objectMapper.writeValueAsString(event);
    }
}
