package com.hakop.websocketoverview.controller;

import com.hakop.websocketoverview.cache.SocketCache;
import com.hakop.websocketoverview.model.RegInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.AbstractHandshakeHandler;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
public class WebSocketOverviewController {

    private final SocketCache socketCache;
    private final SimpMessageSendingOperations msTemplate;

    @SubscribeMapping("/ping")
    public String sendOneTimeMessage(@Header("simpSessionId") String sessionId) {
        return "Server one-time message via the app. Socket session id = " + sessionId;
    }

    @MessageMapping("register/body")
    @SendTo("/topic/process")
    public String registerInBody(@Payload RegInfo regInfo, @Header("simpSessionId") String sessionId) {
        socketCache.add(sessionId, regInfo.name());
        return String.format("%s you are registered by 'body' in session %s.", regInfo.name(), sessionId);
    }

    @MessageMapping("register/{name}/param")
    @SendTo("/queue/process")
    public String registerInParam(@DestinationVariable String name, @Header("simpSessionId") String sessionId) {
        socketCache.add(sessionId, name);
        return String.format("%s you are registered by 'query param' in session %s.", name, sessionId);
    }

    @MessageExceptionHandler
    @SendTo("/queue/error")
    public String handleException(Throwable exception) {
        return exception.getMessage();
    }

    /**
     * {@link AbstractHandshakeHandler#doHandshake(ServerHttpRequest, ServerHttpResponse, WebSocketHandler, Map)}
     */
    @PostMapping("/result")
    public String result(@RequestParam("user_name") final String userName, @RequestParam("result") final boolean result) {
        String sessionId = socketCache.getSessionId(userName);
        if (sessionId == null) {
            return "Such user not registered";
        }
        msTemplate.convertAndSendToUser(
                UUID.randomUUID().toString(),
                "/topic/answer",
                String.format("%s your registration result is: %s", userName, result ? "success" : "denied"),
                Collections.singletonMap("simpSessionId", sessionId)
        );
        return "User success notified.";
    }
}
