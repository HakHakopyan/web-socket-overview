package com.hakop.websocketoverview.controller;

import com.hakop.websocketoverview.model.RegInfo;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WebSocketOverviewController {

    @SubscribeMapping("/ping")
    public String sendOneTimeMessage(@Header("simpSessionId") String sessionId) {
        return "Server one-time message via the app. Socket session id = " + sessionId;
    }

    @MessageMapping("register/body")
    @SendTo("/topic/process")
    public String registerInBody(@Payload RegInfo regInfo) {
        return String.format("%s you are registered by body.", regInfo.name());
    }

    @MessageMapping("register/{name}/param")
    @SendTo("/queue/process")
    public String registerInParam(@DestinationVariable String name) {
        return String.format("%s you are registered by query param.", name);
    }
}
