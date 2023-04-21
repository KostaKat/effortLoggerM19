package com.HTTPHandler.TestServer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import com.HTTPHandler.server.Server;
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestServer {
    private Server server;
    private int port = 8081;
    private static String username;
    private static String password;
    private static String firstName;
    private static String lastName;
    private static String userType;
    @BeforeAll
    public static void setUp()  {
    	username =  UUID.randomUUID().toString().substring(0,5);
    	password =  UUID.randomUUID().toString().substring(0,5);
    	firstName =  UUID.randomUUID().toString().substring(0,5);
    	lastName =  UUID.randomUUID().toString().substring(0,5);
    	userType =  UUID.randomUUID().toString().substring(0,5);
    }
    @BeforeEach
    public void startServer() throws Exception {
        server = new Server(this.port);
        server.startServer();
    }

    @AfterEach
    public void tearDown() {
        server.stopServer();
    }

    public int sendRequest(String endpoint, String method) throws IOException {
        URL url = new URL("http://localhost:" + this.port + endpoint);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod(method);

        return connection.getResponseCode();
    }
    public int sendRequest(String endpoint, String method, String requestBody) throws IOException {
        URL url = new URL("http://localhost:" + this.port + endpoint);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod(method);

        if (requestBody != null) {
            // Set the content type of the request to application/json
            connection.setRequestProperty("Content-Type", "application/json");

            // Enable output on the connection to allow sending the request body
            connection.setDoOutput(true);

            // Write the request body to the connection's output stream
            try (OutputStream os = connection.getOutputStream()) {
                byte[] requestBodyBytes = requestBody.getBytes(StandardCharsets.UTF_8);
                os.write(requestBodyBytes, 0, requestBodyBytes.length);
            }
        }

        return connection.getResponseCode();
    }


    @Test
    public void testAddLogEndpointMethods() throws IOException {
        String endpoint = "/addLog";
        assertEquals(200, sendRequest(endpoint, "POST"));
        assertEquals(400, sendRequest(endpoint, "GET"));
        assertEquals(400, sendRequest(endpoint, "PUT"));
        assertEquals(400, sendRequest(endpoint, "DELETE"));
       
    }

    @Test
    public void testEditLogEndpointMethods() throws IOException {
        String endpoint = "/editLog";
        assertEquals(400, sendRequest(endpoint, "POST"));
        assertEquals(400, sendRequest(endpoint, "GET"));
        assertEquals(200, sendRequest(endpoint, "PUT"));
        assertEquals(400, sendRequest(endpoint, "DELETE"));
      
    }

    @Test
    public void testDeleteLogEndpointMethods() throws IOException {
        String endpoint = "/deleteLog";
        assertEquals(400, sendRequest(endpoint, "POST"));
        assertEquals(400, sendRequest(endpoint, "GET"));
        assertEquals(400, sendRequest(endpoint, "PUT"));
        assertEquals(200, sendRequest(endpoint, "DELETE"));
   
    }

    @Test
    public void testNotFoundHandler() throws IOException {
        String endpoint = "/invalid";
        assertEquals(404, sendRequest(endpoint, "POST"));
        assertEquals(404, sendRequest(endpoint, "GET"));
        assertEquals(404, sendRequest(endpoint, "PUT"));
        assertEquals(404, sendRequest(endpoint, "DELETE"));
       
    
    }
    @Test
    @Order(1)
    public void testRegistrationEndpoint() throws IOException {
        // Set the endpoint to test
        String endpoint = "/register";

        // Set the request body JSON object
        JSONObject requestBody = new JSONObject();
        requestBody.put("Username", username);
        requestBody.put("Password", password);
        requestBody.put("First-Name",firstName);
        requestBody.put("Last-Name",lastName);
        requestBody.put("User-Type", userType);
        System.out.println(requestBody);
        // Send a valid request and expect a 200 response code
        assertEquals(200, sendRequest(endpoint, "POST", requestBody.toString()));

        // Send an invalid request and expect a 400 response code
        requestBody.remove("Last-Name");
        assertEquals(400, sendRequest(endpoint, "POST", requestBody.toString()));
        
        // Send an invalid request with an invalid JSON object as the request body and expect a 400 response code
        String invalidRequestBody = "This is not a valid JSON object";
        assertEquals(400, sendRequest(endpoint, "POST", invalidRequestBody));
        
        // Send unsupported HTTP methods and expect a 404 response code
        assertEquals(400, sendRequest(endpoint, "GET"));
        assertEquals(400, sendRequest(endpoint, "PUT"));
        assertEquals(400, sendRequest(endpoint, "DELETE"));
    }

    @Test
    @Order(2)
    public void testLoginEndpoint() throws Exception {
        // Set the endpoint to test
        String endpoint = "/login";
        
     
        assertEquals(400, sendRequest(endpoint, "GET"));
        assertEquals(400, sendRequest(endpoint, "PUT"));
        assertEquals(400, sendRequest(endpoint, "DELETE"));
     
        // Set the request body JSON object
        JSONObject requestBody = new JSONObject();
        requestBody.put("Username", this.username);
        requestBody.put("Password",this.password);
        requestBody.put("User-Type", this.userType);
        System.out.println(requestBody);
        // Send a valid request and expect a 200 response code
        assertEquals(200, sendRequest(endpoint, "POST", requestBody.toString()));


        // Send an invalid request with missing attribute and expect a 400 response code
        requestBody.remove("Password");
        System.out.println(requestBody);
        assertEquals(401, sendRequest(endpoint, "POST", requestBody.toString()));
        
        
        
    }
}