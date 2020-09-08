package com.aero.ops.config;

import com.aero.ops.controller.websocket.DeployHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
@ComponentScan({"com.aero.ops.controller.websocket"})
public class WebSocketConfig implements WebSocketConfigurer {

    @Autowired
    DeployHandler deployHandler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        String url = "/deploy";
        registry.addHandler(deployHandler, url).withSockJS();
    }
}
