package com.hakop.websocketoverview.cache;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SocketCache {

    private final Map<String, List<String>> socketSessionIdToUserNamesMap = new ConcurrentHashMap<>();

    public boolean add(String sessionId, String userName) {
        List<String> names = socketSessionIdToUserNamesMap.getOrDefault(sessionId, Collections.emptyList());
        if (names.isEmpty()) {
            names = Collections.synchronizedList(new ArrayList<>());
            socketSessionIdToUserNamesMap.put(sessionId, names);
        }
        if (names.contains(userName)) {
            return false;
        }
        names.add(userName);
        return true;
    }

    public void remove(String sessionId) {
        socketSessionIdToUserNamesMap.remove(sessionId);
    }
}

