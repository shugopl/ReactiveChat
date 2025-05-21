package pl.com.shugo.reactivechat;


import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient;
import org.springframework.web.reactive.socket.client.WebSocketClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.net.URI;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ReactiveChatApplicationTests {

    @LocalServerPort
    private int port;

    private final WebSocketClient webSocketClient = new ReactorNettyWebSocketClient();

    @Test
    void echoEndpointShouldReturnPrefixedMessage() {

        URI uri = URI.create("ws://localhost:" + port + "/ws/echo");

        Flux<String> replies = Flux.create(sink ->
                webSocketClient.execute(uri, session ->
                        session.send(Mono.just(session.textMessage("Hello World!")))
                                .thenMany(session.receive()
                                        .map(WebSocketMessage::getPayloadAsText)
                                        .doOnNext(sink::next))
                                .then()
                ).subscribe());

        StepVerifier.create(replies)
                .expectNext("Echo: Hello World!")
                .thenCancel()
                .verify();
    }
}


