package com.WebSocket;

import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import javax.json.JsonValue;

import com.Frontend.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

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

@ClientEndpoint
public class WebSocketClient {

    private Session session;
    private ObservableList<Log> logs;
    private ObservableList<Defect> defects;

    public WebSocketClient(ObservableList<Log> logs) {

        this.logs = logs;
    }

    @OnOpen
    public void onOpen(Session session) {
        System.out.println("WebSocket connection opened: " + session.getId());
        this.session = session;
    }

    @OnMessage
    public void onMessage(String message) {
        Gson gson = new GsonBuilder().create();
        try (JsonReader reader = Json.createReader(new StringReader(message))) {
            JsonValue jsonValue = reader.readValue();
            if (jsonValue.getValueType() == JsonValue.ValueType.ARRAY) {
                // Parse the JSON array string as a JsonArray using the javax.json library
                JsonReader jsonReader = Json.createReader(new StringReader(message));
                JsonArray jsonArray = jsonReader.readArray();

                // Iterate over the JSON array to find the object with an "action" attribute
                JsonObject objectToRemove = null;
                for (JsonValue value : jsonArray) {
                    if (value.getValueType() == JsonValue.ValueType.OBJECT) {
                        JsonObject object = (JsonObject) value;
                        if (object.containsKey("action") && object.getString("action").equals("message")) {
                            objectToRemove = object;
                            break;
                        }
                    }
                }
                String messageObj = "default";
                // If an object with an "action" attribute was found, extract its "message"
                // attribute and remove the object from the JSON array
                if (objectToRemove != null) {
                    messageObj = objectToRemove.getString("message");
                    jsonArray.remove(objectToRemove);
                }

                if (messageObj.compareTo("getLogs") == 0) {
                    Log[] logArray = gson.fromJson(message, Log[].class);
                    ArrayList<Log> receivedLogs = new ArrayList<>(Arrays.asList(logArray));
                    logs.addAll(receivedLogs);
                } else if (messageObj.compareTo("getDefects") == 0) {
                    Defect[] defectArray = gson.fromJson(message, Defect[].class);
                    ArrayList<Log> receivedDefects = new ArrayList<>(Arrays.asList(defectArray));
                    defects.addAll(receivedDefects);
                }

            } else if (jsonValue.getValueType() == JsonValue.ValueType.OBJECT) {
                // Received message is a JSON object
                JsonObject jsonObject = jsonValue.asJsonObject();

                String action = jsonObject.getString("action");
                System.out.println(action);
                JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();
                for (String key : jsonObject.keySet()) {
                    if (!key.equals("action")) {
                        jsonObjectBuilder.add(key, jsonObject.get(key));
                    }
                }
                JsonObject newJsonObject = jsonObjectBuilder.build();

                switch (action) {
                    case "addLog":
                        Log log = gson.fromJson(newJsonObject.toString(), Log.class);
                        logs.add(log);
                        System.out.println(log.toString());
                        break;

                    case "deleteLog":
                        String logID = newJsonObject.getString("logID");
                        System.out.println(logID);
                        for (int i = 0; i < logs.size(); i++) {
                            Log logDelete = logs.get(i);
                            if (logDelete.getLogID().equals(logID)) {
                                System.out.println(logDelete);
                                logs.remove(i);
                                System.out.println(logs);
                                break;
                            }
                        }
                        break;

                    case "editLog":
                        Log logEdit = gson.fromJson(newJsonObject.toString(), Log.class);
                        System.out.println(logEdit.toString());
                        for (int i = 0; i < logs.size(); i++) {
                            Log logEdit2 = logs.get(i);
                            if (logEdit2.getLogID().equals(logEdit.getLogID())) {
                                System.out.println("Log found");
                                logs.set(i, logEdit);
                                System.out.println(logs);
                                break;
                            }
                        }
                        break;

                }
            } else {
                // Received message is neither a JSON array nor a JSON object
                // Handle the error as needed
            }
        } catch (Exception e) {
            // Handle the exception as needed
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
