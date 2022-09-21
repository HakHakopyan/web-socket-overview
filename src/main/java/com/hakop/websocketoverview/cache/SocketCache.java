package com.hakop.websocketoverview.cache;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SocketCache {

    // Username is uniq and cannot be registered across different sessions
    private final Map<String, String> uniqUserNameToSocketSessionIdMap = new ConcurrentHashMap<>();

    public void add(String sessionId, String userName) throws IllegalArgumentException {
        String registeredSessionId = uniqUserNameToSocketSessionIdMap.get(userName);
        if (registeredSessionId != null) {
            throw new IllegalArgumentException(String.format("User '%s' already registered in %s session.",
                    userName,
                    registeredSessionId.equals(sessionId) ? "this" : "other")
            );
        }
        uniqUserNameToSocketSessionIdMap.put(userName, sessionId);
    }

    public String getSessionId(String userName) {
        return uniqUserNameToSocketSessionIdMap.get(userName);
    }

    public boolean removeByUserName(String userName) {
        String registeredSessionId = uniqUserNameToSocketSessionIdMap.get(userName);
        uniqUserNameToSocketSessionIdMap.remove(userName);
        return registeredSessionId != null;
    }

    public void removeBySessionId(String sessionId) {
        Optional<String> keyToRemove = uniqUserNameToSocketSessionIdMap.entrySet().stream()
                .filter(userToSesId -> userToSesId.getValue().equals(sessionId))
                .map(Map.Entry::getKey)
                .findFirst();



        keyToRemove.ifPresent(uniqUserNameToSocketSessionIdMap::remove);
    }
}

