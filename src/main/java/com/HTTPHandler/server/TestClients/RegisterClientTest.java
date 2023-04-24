package com.HTTPHandler.server.TestClients;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import org.json.JSONObject;

public class RegisterClientTest {
	public static void main(String[] args) throws Exception {
		// Set the URL of the server endpoint to send the request to
		String url = "http://localhost:8086/register";

		// Set the JSON data to send in the request body
		JSONObject requestBody = new JSONObject();
		requestBody.put("Username", "asdfsdaf7");
		requestBody.put("Password", "asdffasdf7");
		requestBody.put("First-Name", "John");
		requestBody.put("Last-Name", "Doe");
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
	}
}
