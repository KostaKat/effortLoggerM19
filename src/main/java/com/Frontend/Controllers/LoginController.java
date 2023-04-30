/*
Author : Yihui Wu
 */
package com.Frontend.Controllers;

import com.Frontend.Defect;
import com.Frontend.Log;
import com.Frontend.LogWebSocketClient;
import com.Frontend.Main;
import com.WebSocket.WebSocketClient;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.websocket.ContainerProvider;
import jakarta.websocket.Session;
import jakarta.websocket.WebSocketContainer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import javafx.scene.control.TextField;

public class LoginController {

    ObservableList<Log> logs = FXCollections.observableArrayList();
    ObservableList<Defect> defects = FXCollections.observableArrayList();
    String authToken;
    @FXML
    Button pass;
    @FXML
    TextField username;
    @FXML
    PasswordField password;
    private static Session webSocketSession;

    @FXML
    void loadPage() throws Exception {
        if (!username.getText().trim().isEmpty() && !password.getText().trim().isEmpty()) {
            String url = "http://localhost:8080/login";

            // Set the JSON data to send in the request body
            JSONObject requestBody = new JSONObject();
            requestBody.put("Username", username.getText());
            requestBody.put("Password", password.getText());

            // Convert the JSON object to a string
            String requestBodyString = requestBody.toString();

            // Create a new HTTP connection
            HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
            con.setRequestMethod("POST");

            // Set the content type of the request to application/json
            con.setRequestProperty("Content-Type", "application/json");

            // Enable output on the connection to allow sending the request body
            con.setDoOutput(true);

            // Write the request body to the connection's output stream
            try (OutputStream os = con.getOutputStream()) {
                byte[] requestBodyBytes = requestBodyString.getBytes(StandardCharsets.UTF_8);
                os.write(requestBodyBytes, 0, requestBodyBytes.length);
            }

            int responseCode = con.getResponseCode();
            String responseMessage = con.getResponseMessage();
            StringBuilder responseBuilder = new StringBuilder();

            try (BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = in.readLine()) != null) {
                    responseBuilder.append(line);
                }
            } catch (Exception e) {
                // If there is an exception, read the response from the error stream instead
                try (BufferedReader in = new BufferedReader(
                        new InputStreamReader(con.getErrorStream(), StandardCharsets.UTF_8))) {
                    String line;
                    while ((line = in.readLine()) != null) {
                        responseBuilder.append(line);
                    }
                }
            }
            String responseBody = responseBuilder.toString();

            // Parse the response body as a JSON object
            JSONObject jsonResponse = new JSONObject(responseBody);

            // Extract values from the JSON response
            String token = jsonResponse.optString("Token");
            String manager = jsonResponse.optString("ManagerID", "");
            if (!manager.isEmpty()) {
                System.out.println("Response code: " + responseCode);
                System.out.println("Response message: " + responseMessage);
                System.out.println("Token: " + token);
                System.out.println("Manager ID: " + manager);
                connectWebSocket(token);
                if (responseCode == 200) {
                    System.out.println(logs.size());
                    ObjectMapper objectMapper = new ObjectMapper();
                    JsonNode jsonNode = objectMapper.readTree(responseBody);
                    authToken = jsonNode.get("Token").asText();
                    Main.setManagerRoot(logs, defects, authToken, manager);
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setContentText("Username or Password is not correct, you need register one");
                    alert.show();
                }
            } else {
                System.out.println("Response code: " + responseCode);
                System.out.println("Response message: " + responseMessage);
                System.out.println("Token: " + token);
                connectWebSocket(token);
                if (responseCode == 200) {
                    System.out.println(logs.size());
                    ObjectMapper objectMapper = new ObjectMapper();
                    JsonNode jsonNode = objectMapper.readTree(responseBody);
                    authToken = jsonNode.get("Token").asText();
                    Main.setRoot("CreateLog", logs, defects, authToken);
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setContentText("Username or Password is not correct\n Or you are not registered");
                    alert.show();
                }
            }

        } else {
            System.out.println("Please fill in all information");
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Please Fill in all information");
            alert.show();
        }
    }

    @FXML
    void loadCreatePage() throws IOException {
        Main.setRoot("CreateLog", logs, defects, authToken);
    }

    @FXML
    void register() throws IOException {
        Main.setRoot("signUp");
    }

    private void connectWebSocket(String token) throws Exception {
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        String wsUrl = "ws://localhost:8081/getLogs?Token=" + token;
        WebSocketClient webSocketClient = new WebSocketClient(logs, defects);
        webSocketSession = container.connectToServer(webSocketClient, new URI(wsUrl));
    }
}
