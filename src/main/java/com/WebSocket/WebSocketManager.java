package com.WebSocket;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import jakarta.websocket.Session;

public class WebSocketManager {
    private static WebSocketManager instance;
    private Map<String, Session> sessions = new ConcurrentHashMap<>();

    private WebSocketManager() {
    }

    public static WebSocketManager getInstance() {
        if (instance == null) {
            instance = new WebSocketManager();
        }
        return instance;
    }
    public void addSession(String employeeID, Session session) {
        sessions.put(employeeID, session);
    }

    public void removeSession(String employeeID) {
    	if (employeeID != null)
        sessions.remove(employeeID);
    }

    public void sendUpdate(String employeeID, String message) {
        Session session = sessions.get(employeeID);
      
        if (session != null) {
      
            session.getAsyncRemote().sendText(message);
            
        }
    }
}
