package com.WebSocket;

import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.Database.DatabaseManager;

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

    public void sendUpdate(String employeeID, String message) throws SQLException {
        DatabaseManager db = new DatabaseManager();
        Session session = sessions.get(employeeID);
        String managerID = db.getManagerID(employeeID);

        if (session != null) {

            session.getAsyncRemote().sendText(message);
            if (managerID != null) {
                Session managerSession = sessions.get(managerID);
                if (managerSession != null) {
                    managerSession.getAsyncRemote().sendText(message);
                }
            }
        }
    }
}
