package com.Frontend;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue;

import java.io.StringReader;

public class LogWebSocketClient extends WebSocketClient {
    private ObservableList<Log> logArrayList;

    public LogWebSocketClient(String serverURI, ObservableList<Log> logArrayList) throws URISyntaxException

    {
        super(new URI(serverURI));
        this.logArrayList = logArrayList;
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        System.out.println("WebSocket connection opened");
    }

    @Override
    public void onMessage(String message) {

        Gson gson = new GsonBuilder().create();
        try (JsonReader reader = Json.createReader(new StringReader(message))) {
            JsonValue jsonValue = reader.readValue();
            if (jsonValue.getValueType() == JsonValue.ValueType.ARRAY) {
                // Received message is a JSON array

                Log[] logs = gson.fromJson(message, Log[].class);
                ArrayList<Log> receivedLogs = new ArrayList<>(Arrays.asList(logs));
                logArrayList.addAll(receivedLogs);

            } else if (jsonValue.getValueType() == JsonValue.ValueType.OBJECT) {
                // Received message is a JSON object
                JsonObject jsonObject = jsonValue.asJsonObject();
                String action = jsonObject.get("action").toString();
                jsonObject.remove("action");
                switch (action) {
                    case "addLog":
                        Log log = gson.fromJson(jsonObject.toString(), Log.class);
                        logArrayList.add(log);
                        break;
                    case "deleteLog":
                        System.out.println("Received deleteLog message: " + jsonObject.toString());
                        String logID = jsonObject.get("logID").toString();
                        System.out.println(logID);
                        for (int i = 0; i < logArrayList.size(); i++) {
                            Log logDelete = logArrayList.get(i);
                            if (logDelete.getLogID().equals(logID)) {
                                logArrayList.remove(i);
                                break;
                            }
                        }
                        break;
                    case "editLog":
                        Log logEdit = gson.fromJson(jsonObject.toString(), Log.class);
                        System.out.println(logEdit);
                        for (int i = 0; i < logArrayList.size(); i++) {
                            Log logEdit2 = logArrayList.get(i);
                            if (logEdit2.getLogID().equals(logEdit.getLogID())) {
                                System.out.println("edit log websocket");
                                logArrayList.set(i, logEdit);
                                System.out.println(logArrayList);
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

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("WebSocket connection closed: " + reason);
    }

    @Override
    public void onError(Exception ex) {
        System.out.println("WebSocket error occurred: " + ex.getMessage());
    }

}
