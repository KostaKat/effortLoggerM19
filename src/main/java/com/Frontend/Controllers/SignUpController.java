package com.Frontend.Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import org.json.JSONObject;

import com.Frontend.Main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;


public class SignUpController{
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
    void change() throws IOException {
        if (!userTextField.getText().trim().isEmpty() && !passTextField.getText().trim().isEmpty()) {
            String url = "http://localhost:8086/register";

            // Set the JSON data to send in the request body
            JSONObject requestBody = new JSONObject();
            requestBody.put("Username", userTextField.getText());
            requestBody.put("Password", passTextField.getText());
            requestBody.put("First-Name", firstTextField.getText());
            requestBody.put("Last-Name", lastTextField.getText());
            requestBody.put("User-Type", "Employee");
            requestBody.put("ManagerID", "5aec5abe-57cc-4049-9e95-5c122bc2b133");

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

            // Read the response from the connection's input stream
            int responseCode = con.getResponseCode();
            String responseMessage = con.getResponseMessage();
            StringBuilder responseBuilder = new StringBuilder();

            try (BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8))) {
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
            if(responseCode == 200){
                firstT = firstTextField.getText();
                lastT = lastTextField.getText();
                Main.setRoot("login");
            }else{
                System.out.println("Register did not successful");
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
