package com.hakop.websocketoverview.controller;

import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WebSocketOverviewController {

    @SubscribeMapping("/ping")
    public String sendOneTimeMessage(@Header("simpSessionId") String sessionId) {
        return "Server one-time message via the app. Socket session id = " + sessionId;
    }
}
