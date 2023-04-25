package com.WebSocket;

import com.Database.DatabaseManager;
import com.HTTPHandler.server.HandlerHelpers;

import jakarta.websocket.CloseReason;
import jakarta.websocket.EndpointConfig;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;

@ServerEndpoint("/getLogs")
public class WebSocket {
    private HandlerHelpers helper = new HandlerHelpers();
    private DatabaseManager db = new DatabaseManager();
    private static WebSocketManager manager = WebSocketManager.getInstance();

    @OnOpen
    public void onOpen(Session session, EndpointConfig config) throws Exception {
        String token = (String) session.getRequestParameterMap().get("Token").get(0);
        System.out.println(token);
        System.out.println(token);
        if (token == null || !helper.verifyToken(token)) {
            try {
                session.close(new CloseReason(CloseReason.CloseCodes.VIOLATED_POLICY, "Invalid token"));
            } catch (Exception e) {
                // Handle the exception as needed
            }
         
        }
        System.out.println(token);
        String logs = "";
        String employeeID = db.getIDbyUsernameUserType(helper.getClaims(token).get("Username"),helper.getClaims(token).get("User-Type")); // Implement this method to extract the employee ID from the token
        session.getUserProperties().put("employeeID", employeeID);
        manager.addSession(employeeID, session);
        if(helper.getClaims(token).get(("User-Type")).equals("Employee")){
            logs = db.getLogsEmployee(employeeID);
        }else{
            logs = db.getLogsManager(employeeID);
        }
        session.setMaxTextMessageBufferSize(10000);
        manager.sendUpdate(employeeID, logs);
    }

    @OnMessage
    public String onMessage(String message, Session session) {
        System.out.println("Received message: " + message + " from: " + session.getId());
        return "Echo: " + message;
    }
   

    @OnClose
    public void onClose(Session session) {
        String employeeID = (String) session.getUserProperties().get("employeeID");
        manager.removeSession(employeeID);
        // ...
    }

    public static void sendUpdate(String employeeID, String message) {
    	System.out.println("In send update");
    	System.out.println("Sending update to WebSocket client with employee ID: " + employeeID);

        manager.sendUpdate(employeeID, message);
    }
    @OnError
    public void onError(Session session, Throwable throwable) {
        System.out.println("Error: " + throwable.getMessage());
        // Perform any additional error handling here
    }


}
