package com.WebSocket;

import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;



import com.Frontend.Defect;
import com.Frontend.Log;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import jakarta.websocket.ClientEndpoint;
import jakarta.websocket.CloseReason;
import jakarta.websocket.ContainerProvider;
import jakarta.websocket.DeploymentException;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.WebSocketContainer;
import javafx.collections.ObservableList;
import javafx.application.Platform;

@ClientEndpoint
public class WebSocketClient {

    private Session session;
    private ObservableList<Log> logs;
    private ObservableList<Defect> defects;

    public WebSocketClient(ObservableList<Log> logs, ObservableList<Defect> defects) {

        this.logs = logs;
        this.defects = defects;
    }

    @OnOpen
    public void onOpen(Session session) {
        System.out.println("WebSocket connection opened: " + session.getId());
        this.session = session;
    }

    @OnMessage
    public void onMessage(String message) {
        
    	 Gson gson = new GsonBuilder().create();

    	    // Parse the JSON message as a JsonElement using the com.google.gson library
    	    JsonElement jsonElement = gson.fromJson(message, JsonElement.class);

    	    if (jsonElement.isJsonArray()) {
    	        System.out.println("WebSocket message received: \n" + message);
    	        JsonArray jsonArray = jsonElement.getAsJsonArray();

    	        String actionValue = null;
    	       
    	        JsonArray newJsonArray = new JsonArray();
    	        for (JsonElement element : jsonArray) {
    	            if (element.isJsonObject()) {
    	                JsonObject object = element.getAsJsonObject();
    	                if (object.has("action")) {
    	                    actionValue = object.get("action").getAsString();
    	                } else {
    	                    newJsonArray.add(element);
    	                }
    	            } else {
    	                newJsonArray.add(element);
    	            }
    	        }

    	        System.out.println("Action value: " + actionValue);

    	        if (actionValue.compareTo("getLogs") == 0) {
    	            System.out.println("In getLogs");
    	            Log[] logArray = gson.fromJson(newJsonArray.toString(), Log[].class);
    	            ArrayList<Log> receivedLogs = new ArrayList<>(Arrays.asList(logArray));
    	            System.out.println("ArrayList" + receivedLogs);
    	            logs.removeIf(log -> log.getProject() == null);

    	            Platform.runLater(() -> {
    	                logs.addAll(receivedLogs);
    	                System.out.println(logs);
    	            });

    	            System.out.println(receivedLogs);
    	        } else if (actionValue.compareTo("getDefects") == 0) {
    	            Defect[] defectArray = gson.fromJson(newJsonArray.toString(), Defect[].class);
    	            ArrayList<Defect> receivedDefects = new ArrayList<>(Arrays.asList(defectArray));
    	            receivedDefects.removeIf(defect -> defect.getName() == null);

    	            Platform.runLater(() -> {
    	                defects.addAll(receivedDefects);
    	                System.out.println(defects);
    	            });

    	            System.out.println(receivedDefects);
    	        }
    	    }else if (jsonElement.isJsonObject()) {
    	        // Received message is a JSON object
    	        JsonObject jsonObject = jsonElement.getAsJsonObject();

    	        String action = jsonObject.get("action").getAsString();
    	        System.out.println(action);

    	        JsonObject newJsonObject = jsonObject.entrySet().stream()
    	                .filter(entry -> !entry.getKey().equals("action"))
    	                .collect(JsonObject::new, (jObj, entry) -> jObj.add(entry.getKey(), entry.getValue()),
    	                        (jObj1, jObj2) -> {});

    	        switch (action) {
    	            case "addLog":
    	                Log log = gson.fromJson(newJsonObject, Log.class);
    	                Platform.runLater(() -> {
    	                	logs.add(log);
        	                
        	            });
    	                
    	                System.out.println(log.toString());
    	                break;

    	            case "deleteLog":
    	                String logID = newJsonObject.get("logID").getAsString();
    	                System.out.println(logID);
    	                for (int i = 0; i < logs.size(); i++) {
    	                    Log logDelete = logs.get(i);
    	                    if (logDelete.getLogID().equals(logID)) {
    	                        System.out.println(logDelete);
    	                        final int indexToRemove = i;  
    	                        Platform.runLater(() -> {
    	                        	logs.remove(indexToRemove);
    	        	                
    	        	            });
    	    	                
    	                        
    	                        System.out.println(logs);
    	                        break;
    	                    }
    	                }
    	                break;

    	            case "editLog":
    	                Log logEdit = gson.fromJson(newJsonObject, Log.class);
    	                System.out.println(logEdit.toString());
    	                for (int i = 0; i < logs.size(); i++) {
    	                    Log logEdit2 = logs.get(i);
    	                    if (logEdit2.getLogID().equals(logEdit.getLogID())) {
    	                        System.out.println("Log found");
    	                        final int indexToRemove = i;  
    	                        Platform.runLater(() -> {
    	                        	logs.set(indexToRemove, logEdit);
    	        	                
    	        	            });
    	                        
    	                        System.out.println(logs);
    	                        break;
    	                    }
    	                }
    	                break;
    	            case "addDefect":
    	                Defect defect = gson.fromJson(newJsonObject, Defect.class);
    	                Platform.runLater(() -> {
    	                	defects.add(defect);
        	                
        	            });
    	                break;

    	            case "deleteDefect":
    	                String defectID = newJsonObject.get("defectID").getAsString();

    	                for (int i = 0; i < defects.size(); i++) {
    	                    Defect defectDelete = defects.get(i);
    	                    if (defectDelete.getDefectID().equals(defectID)) {
    	                    	final int indexToRemove = i;  
    	                        Platform.runLater(() -> {
    	                        	defects.remove(indexToRemove);
    	        	                
    	        	            });
    	                        break;
    	                    }
    	                }
    	                break;

    	            case "editDefect":
    	                String defectIDEdit = newJsonObject.get("defectID").getAsString();
    	                String fixStatus = newJsonObject.get("fixStatus").getAsString();
    	                String description = newJsonObject.get("description").getAsString();
    	                for (int i = 0; i < defects.size(); i++) {
    	                    Defect currentDefect = defects.get(i);
    	                    if (currentDefect.getDefectID().compareTo(defectIDEdit) == 0) {
    	                        System.out.println("Log found");
    	                        currentDefect.setFixStatus(fixStatus);
    	                        currentDefect.setDescription(description);
    	                        break;
    	                    }
    	                }
    	                break;
    	        }
    	    } else {
    	        // Received message is neither a JSON array nor a JSON object
    	        // Handle the error as needed
    	    }

    }

    @OnClose
    public void onClose(CloseReason reason) {
        System.out.println("WebSocket connection closed with reason: " + reason.getReasonPhrase());
    }

    @OnError
    public void onError(Throwable throwable) {
        throwable.printStackTrace();
    }

    public void connect(String uri) throws DeploymentException, InterruptedException, IOException, URISyntaxException {
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        container.connectToServer(this, new URI(uri));
    }

    public void sendMessage(String message) throws Exception {
        session.getBasicRemote().sendText(message);
    }
}
