package com.hakop.websocketoverview.cache;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SocketCache {

    // Username is uniq and cannot be registered across different sessions
    private final Map<String, String> uniqUserNameToSocketSessionIdMap = new ConcurrentHashMap<>();
    private final Map<String, String> uniqUserNameToUserIdMap = new ConcurrentHashMap<>();

    public void add(String sessionId, String userName, String userId) throws IllegalArgumentException {
        String registeredSessionId = uniqUserNameToSocketSessionIdMap.get(userName);
        if (registeredSessionId != null) {
            throw new IllegalArgumentException(String.format("User '%s' already registered in %s session.",
                    userName,
                    registeredSessionId.equals(sessionId) ? "this" : "other")
            );
        }
        uniqUserNameToSocketSessionIdMap.put(userName, sessionId);
        uniqUserNameToUserIdMap.put(userName, userId);
    }

    public String getSessionId(String userName) {
        return uniqUserNameToSocketSessionIdMap.get(userName);
    }

    public String getUserId(String userName) {
        return uniqUserNameToUserIdMap.get(userName);
    }

    public boolean removeByUserName(String userName) {
        String registeredSessionId = uniqUserNameToSocketSessionIdMap.get(userName);
        uniqUserNameToSocketSessionIdMap.remove(userName);
        uniqUserNameToUserIdMap.remove(userName);
        return registeredSessionId != null;
    }

    public void removeBySessionId(String sessionId) {
        Optional<String> userName = uniqUserNameToSocketSessionIdMap.entrySet().stream()
                .filter(userToSesId -> userToSesId.getValue().equals(sessionId))
                .map(Map.Entry::getKey)
                .findFirst();

        userName.ifPresent(uniqUserNameToSocketSessionIdMap::remove);
        userName.ifPresent(uniqUserNameToUserIdMap::remove);
    }
}

