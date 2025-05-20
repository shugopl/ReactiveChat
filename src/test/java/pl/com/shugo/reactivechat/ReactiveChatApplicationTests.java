package pl.com.shugo.reactivechat;

import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient;
import org.springframework.web.reactive.socket.client.WebSocketClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.ReplayProcessor;

import java.net.URI;
import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ReactiveChatApplicationTests {


    @Test
    public void chatServerShouldEchoMessages() {
        int count = 3;

        Flux<String> input = Flux.range(1, count).map(i -> "msg-" + i);
        ReplayProcessor<String> output = ReplayProcessor.create(count);

        WebSocketClient client = new ReactorNettyWebSocketClient();

        client.execute(
                URI.create("ws://localhost:8080/websocket/chat"),
                session -> session
                        .send(input.map(session::textMessage))
                        .thenMany(session.receive().take(count).map(WebSocketMessage::getPayloadAsText))
                        .subscribeWith(output)
                        .then()
        ).block(Duration.ofSeconds(5));

        List<String> sent = input.collectList().block();
        List<String> received = output.collectList().block();

        assertEquals(sent, received);
    }

}
