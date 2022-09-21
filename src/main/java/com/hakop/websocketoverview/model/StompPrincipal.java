package com.hakop.websocketoverview.model;


import java.security.Principal;

/**
 * Custom Principal class which is used for anonymous user sessions in Websocket connection
 */
public record StompPrincipal(String name) implements Principal {
    @Override
    public String getName() {
        return name;
    }
}
