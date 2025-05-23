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
        SimpleUrlHandlerMapping handlerMapping = new SimpleUrlHandlerMapping();
        handlerMapping.setOrder(10);
        handlerMapping.setUrlMap(Map.of("/ws/echo", (WebSocketHandler) echoHandler));
        return handlerMapping;
    }

    @Bean
    public WebSocketHandlerAdapter handlerAdapter() {
        return new WebSocketHandlerAdapter();
    }
}
