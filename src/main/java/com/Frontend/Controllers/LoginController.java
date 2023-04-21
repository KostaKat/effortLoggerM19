/*
Author : Yihui Wu
 */
package com.Frontend.Controllers;

import com.Frontend.Log;
import com.Frontend.Main;
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
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import javafx.scene.control.TextField;
public class LoginController {
    ArrayList<Log> logArrayList = new ArrayList<Log>();
    @FXML Button pass;
    @FXML TextField username;
    @FXML PasswordField password;

    @FXML
    void loadPage() throws IOException {
        if (!username.getText().trim().isEmpty() && !password.getText().trim().isEmpty()) {
            String url = "http://localhost:8086/login";

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
            String response = responseBuilder.toString();

            // Print the response from the server
            System.out.println("Response code: " + responseCode);
            System.out.println("Response message: " + responseMessage);
            System.out.println("Response body: " + response);
            if(responseCode == 200){
                Main.setRoot("CreateLog", logArrayList);
            }else{
                System.out.println("Username or Password is not correct, you need register one first");
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Username or Password is not correct, you need register one");
                alert.show();
            }
        }else{
            System.out.println("Please fill in all information");
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Please Fill in all information");
            alert.show();
        }
    }
     @FXML
     void loadCreatePage() throws IOException {
         Main.setRoot("CreateLog", logArrayList);
     }
    @FXML
    void register() throws IOException {
        Main.setRoot("signUp");
    }
}
