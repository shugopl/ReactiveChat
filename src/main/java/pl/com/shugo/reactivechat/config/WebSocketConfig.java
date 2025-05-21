package pl.com.shugo.reactivechat.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter;
import pl.com.shugo.reactivechat.handlers.EchoHandler;

import java.util.Map;

@Configuration
public class WebSocketConfig {

    @Bean
    public SimpleUrlHandlerMapping handlerMapping(EchoHandler echoHandler) {
        return new SimpleUrlHandlerMapping() {{
            setOrder(10);
            setUrlMap(Map.of("/ws/echo", (WebSocketHandler) echoHandler));
        }};
    }

    @Bean
    public WebSocketHandlerAdapter handlerAdapter() {
        return new WebSocketHandlerAdapter();
    }
}
