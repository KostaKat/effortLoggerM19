package com.Frontend.Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;

import org.json.JSONException;
import org.json.JSONObject;

import com.Frontend.Main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.management.ManagementFactory;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class SignUpController {
    public static String firstT;
    public static String lastT;
    @FXML
    private TextField firstTextField;

    @FXML
    private TextField lastTextField;

    @FXML
    private TextField passTextField;

    @FXML
    private Button signupButton;

    @FXML
    private Button loginButton;

    @FXML
    private TextField userTextField;

    @FXML
    private CheckBox manager;

    @FXML
    private TextField managerId;

    @FXML
    public void initialize() {
        manager.selectedProperty().addListener((obs, wasSelected, isNowSelected) -> {
            // If checkbox is checked, disable textfield
            if (isNowSelected) {
                managerId.setDisable(true);
            } else {
                managerId.setDisable(false);
            }
        });
    }

    @FXML
    void change() throws IOException {
        if (!userTextField.getText().trim().isEmpty() && !passTextField.getText().trim().isEmpty()) {
            String url = "http://localhost:8080/register";

            // Set the JSON data to send in the request body
            JSONObject requestBody = new JSONObject();
            if (manager.isSelected()) {
                requestBody.put("Username", userTextField.getText());
                requestBody.put("Password", passTextField.getText());
                requestBody.put("First-Name", firstTextField.getText());
                requestBody.put("Last-Name", lastTextField.getText());
                requestBody.put("User-Type", "Manager");
            } else {
                requestBody.put("Username", userTextField.getText());
                requestBody.put("Password", passTextField.getText());
                requestBody.put("First-Name", firstTextField.getText());
                requestBody.put("Last-Name", lastTextField.getText());
                requestBody.put("User-Type", "Employee");
                requestBody.put("ManagerID", managerId.getText());
            }

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

            // Read the response from the connection's input stream or error stream,
            // depending on the response code
            int responseCode = con.getResponseCode();
            String responseMessage = con.getResponseMessage();
            StringBuilder responseBuilder = new StringBuilder();

            InputStream inputStream;
            if (responseCode >= 200 && responseCode < 300) {
                inputStream = con.getInputStream();
            } else {
                inputStream = con.getErrorStream();
            }

            try (BufferedReader in = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
                String line;
                while ((line = in.readLine()) != null) {
                    responseBuilder.append(line);
                }
            }
            String response = responseBuilder.toString();

            // Print the response from the server
            System.out.println("Response code: " + responseCode);
            System.out.println("Response message: " + responseMessage);
            System.out.println("Response body: " + response);
            if (responseCode == 200) {
                firstT = firstTextField.getText();
                lastT = lastTextField.getText();
                Main.setRoot("login");
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Username already exists");
                alert.show();
            }
        } else {
            System.out.println("Please fill in all information");
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Please Fill in all information");
            alert.show();
        }
    }

    @FXML
    void login() throws IOException {
        Main.setRoot("login");
    }
}
