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
    	System.out.println("In remove session");
    	if (employeeID != null)
        sessions.remove(employeeID);
    }

    public void sendUpdate(String employeeID, String message) {
        Session session = sessions.get(employeeID);
        System.out.println(session.toString());
        if (session != null) {
        	System.out.println("Sending update to WebSocket client with employee ID: " + employeeID);
        System.out.println(message);
            session.getAsyncRemote().sendText(message);
            System.out.println("send message");
            
        }
    }
}
