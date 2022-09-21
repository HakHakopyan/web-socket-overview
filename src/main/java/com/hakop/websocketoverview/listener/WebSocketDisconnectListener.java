package com.hakop.websocketoverview.listener;

import com.hakop.websocketoverview.cache.SocketCache;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@RequiredArgsConstructor
@Component
public class WebSocketDisconnectListener implements ApplicationListener<SessionDisconnectEvent> {

    private final SocketCache socketCache;

    @Override
    @EventListener
    public void onApplicationEvent(SessionDisconnectEvent event) {
        socketCache.removeBySessionId(event.getSessionId());
    }
}
