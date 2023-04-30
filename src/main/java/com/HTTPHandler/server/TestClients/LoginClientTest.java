package com.HTTPHandler.server.TestClients;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

import org.json.JSONObject;

import com.WebSocket.WebSocketClient;

import jakarta.websocket.ClientEndpoint;
import jakarta.websocket.ContainerProvider;
import jakarta.websocket.OnMessage;
import jakarta.websocket.Session;
import jakarta.websocket.WebSocketContainer;

@ClientEndpoint
public class LoginClientTest {
	private static Session webSocketSession;

	public static void main(String[] args) throws Exception {
		// Set the URL of the server endpoint to send the request to
		String url = "http://localhost:8080/login";

		// Set the JSON data to send in the request body
		JSONObject requestBody = new JSONObject();
		requestBody.put("Username", "c");
		requestBody.put("Password", "c");

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
		String responseBody = responseBuilder.toString();

		// Parse the response body as a JSON object
		JSONObject jsonResponse = new JSONObject(responseBody);

		// Extract values from the JSON response
		String token = jsonResponse.getString("Token");

		// Print the response from the server
		System.out.println("Response code: " + responseCode);
		System.out.println("Response message: " + responseMessage);
		System.out.println("Token: " + token);

		String serverUrl = "http://localhost:8080/addDefect"; // Replace with your server's URL
		URL addDefectUrl = new URL(serverUrl);
		con = (HttpURLConnection) addDefectUrl.openConnection();

		// Set request method and headers
		con.setRequestMethod("POST");
		con.setRequestProperty("Content-Type", "application/json");
		con.setRequestProperty("Accept", "application/json");
		con.setDoOutput(true);

		// Create JSON payload

		JSONObject defectAddData = new JSONObject();
		defectAddData.put("Token", token);
		defectAddData.put("stepWhenInjected", "2023-04-23");
		defectAddData.put("stepWhenRemoved", "2023-04-24"); // Corrected value
		defectAddData.put("defectCategory", "UI"); // Corrected value
		defectAddData.put("fixStatus", "Project X");
		defectAddData.put("name", "Sample Defect"); // Corrected value
		defectAddData.put("description", "edited2");

		String defectAddString = defectAddData.toString();

		// Send the request
		try (OutputStream os = con.getOutputStream()) {
			byte[] input = defectAddString.getBytes("utf-8");
			os.write(input, 0, input.length);
		}

		// Read the response
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
		String editUrl = "http://localhost:8080/editDefect"; // Replace with your server's URL
		URL editURL = new URL(editUrl);
		con = (HttpURLConnection) editURL.openConnection();

		// Set request method and headers
		con.setRequestMethod("PUT");
		con.setRequestProperty("Content-Type", "application/json");
		con.setRequestProperty("Accept", "application/json");
		con.setDoOutput(true);

		// Create JSON payload
		JSONObject defectEditData = new JSONObject();
		defectEditData.put("Token", token);
		defectEditData.put("stepWhenInjected", "2023-04-23");
		defectEditData.put("stepWhenRemoved", "2023-04-24"); // Corrected value
		defectEditData.put("defectCategory", "UI"); // Corrected value
		defectEditData.put("fixStatus", "Project X");
		defectEditData.put("name", "Sample Defect"); // Corrected value
		defectEditData.put("description", "edit");
		defectEditData.put("defectID", "b7c1f5d1-ad10-459f-ae82-4db3ad1b0595");
		String defectEditDataString = defectEditData.toString();

		// Send the request
		try (OutputStream os = con.getOutputStream()) {
			byte[] input = defectEditDataString.getBytes(StandardCharsets.UTF_8);
			os.write(input, 0, input.length);
		}

		// Check for server errors
		if (con.getResponseCode() >= 400) {
			try (BufferedReader in = new BufferedReader(
					new InputStreamReader(con.getErrorStream(), StandardCharsets.UTF_8))) {
				String errorResponse = in.lines().collect(Collectors.joining());
				throw new RuntimeException(
						"Server returned error code " + con.getResponseCode() + ": " + errorResponse);
			}
		}

		// Read the response
		StringBuilder responseBuildera = new StringBuilder();
		try (BufferedReader in = new BufferedReader(
				new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8))) {
			String line;
			while ((line = in.readLine()) != null) {
				responseBuildera.append(line);
			}
		}

		// Print the response
		String response = responseBuilder.toString();
		System.out.println(response);
		String deleteURL = "http://localhost:8080/deleteDefect"; // Replace with your server's URL
		URL deleteURL_ = new URL(deleteURL);
		con = (HttpURLConnection) deleteURL_.openConnection();

		// Set request method and headers
		con.setRequestMethod("DELETE");
		con.setRequestProperty("Content-Type", "application/json");
		con.setRequestProperty("Accept", "application/json");
		con.setDoOutput(true);

		// Create JSON payload
		JSONObject defectDeleteData = new JSONObject();
		defectDeleteData.put("Token", token);
		defectDeleteData.put("defectID", "b7c1f5d1-ad10-459f-ae82-4db3ad1b0595");
		String defectDeleteDataString = defectDeleteData.toString();

		// Send the request
		try (OutputStream os = con.getOutputStream()) {
			byte[] input = defectDeleteDataString.getBytes(StandardCharsets.UTF_8);
			os.write(input, 0, input.length);
		}

		// Check for server errors
		if (con.getResponseCode() >= 400) {
			try (BufferedReader in = new BufferedReader(
					new InputStreamReader(con.getErrorStream(), StandardCharsets.UTF_8))) {
				String errorResponse = in.lines().collect(Collectors.joining());
				throw new RuntimeException(
						"Server returned error code " + con.getResponseCode() + ": " + errorResponse);
			}
		}

		// Read the response
		StringBuilder responseBuilderb = new StringBuilder();
		try (BufferedReader in = new BufferedReader(
				new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8))) {
			String line;
			while ((line = in.readLine()) != null) {
				responseBuilderb.append(line);
			}
		}

		// Print the response
		response = responseBuilderb.toString();
		System.out.println(response);

	}

	private static void connectWebSocket(String token) throws Exception {
		WebSocketContainer container = ContainerProvider.getWebSocketContainer();
		String wsUrl = "ws://localhost:8081/getLogs?Token=" + token;
		webSocketSession = container.connectToServer(WebSocketClient.class, new URI(wsUrl));

	}

}
