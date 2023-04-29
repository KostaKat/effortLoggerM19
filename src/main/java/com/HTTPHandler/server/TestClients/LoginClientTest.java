package com.HTTPHandler.server.TestClients;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;

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

		connectWebSocket(token);
		// Set the URL of the server endpoint to add a log
		String logUrl = "http://localhost:8080/addLog";

		// Set the JSON data for the log request
		JSONObject logData = new JSONObject();
		logData.put("Token", token);
		logData.put("Date", "2023-04-23");
		logData.put("StartTime", "09:00:00");
		logData.put("EndTime", "10:00:00");
		logData.put("Project", "Project X");
		logData.put("EffortCategory", "Development");
		logData.put("EffortDetail", "c2");
		logData.put("LifeCycleStep", "Coding");

		// Convert the JSON object to a string
		String logDataString = logData.toString();

		// Create a new HTTP connection for the log request
		HttpURLConnection logCon = (HttpURLConnection) new URL(logUrl).openConnection();
		logCon.setRequestMethod("POST");

		// Set the content type of the request to application/json
		logCon.setRequestProperty("Content-Type", "application/json");

		// Enable output on the connection to allow sending the request body
		logCon.setDoOutput(true);

		// Write the log request body to the connection's output stream
		try (OutputStream os = logCon.getOutputStream()) {
			byte[] logDataBytes = logDataString.getBytes(StandardCharsets.UTF_8);
			os.write(logDataBytes, 0, logDataBytes.length);
		}

		// Read the response from the log request's input stream
		int logResponseCode = logCon.getResponseCode();
		String logResponseMessage = logCon.getResponseMessage();

		// Print the response from the log request
		System.out.println("Log request code: " + logResponseCode);
		System.out.println("Log request message: " + logResponseMessage);
		// Connect to the WebSocket server
		String logUrlEdit = "http://localhost:8080/editLog";

		// Set the JSON data for the log request
		JSONObject logDataEdit = new JSONObject();
		logDataEdit.put("Token", token);
		logDataEdit.put("LogID", "ebfd3483-6311-41fb-91fd-b09b5ef3ff45");
		logDataEdit.put("Date", "2023-04-23");
		logDataEdit.put("StartTime", "09:00:00");
		logDataEdit.put("EndTime", "10:00:00");
		logDataEdit.put("Project", "Project X");
		logDataEdit.put("EffortCategory", "Development");
		logDataEdit.put("EffortDetail", "edited2");
		logDataEdit.put("LifeCycleStep", "Coding");

		// Convert the JSON object to a string
		String logDataEditString = logDataEdit.toString();

		// Create a new HTTP connection for the log request
		HttpURLConnection logConEdit = (HttpURLConnection) new URL(logUrlEdit).openConnection();
		logConEdit.setRequestMethod("PUT");

		// Set the content type of the request to application/json
		logConEdit.setRequestProperty("Content-Type", "application/json");

		// Enable output on the connection to allow sending the request body
		logConEdit.setDoOutput(true);

		// Write the log request body to the connection's output stream
		try (OutputStream os = logConEdit.getOutputStream()) {
			byte[] logDataBytes = logDataEditString.getBytes(StandardCharsets.UTF_8);
			os.write(logDataBytes, 0, logDataBytes.length);
		}

		// Read the response from the log request's input stream
		int logResponseCodeEdit = logConEdit.getResponseCode();
		String logResponseMessageEdit = logConEdit.getResponseMessage() + "edit";

		// Print the response from the log request
		System.out.println("Log request code: " + logResponseCodeEdit);
		System.out.println("Log request message: " + logResponseMessageEdit);

		String logUrlDelete = "http://localhost:8080/deleteLog";

		// Set the JSON data for the log request
		JSONObject logDataDelete = new JSONObject();
		logDataDelete.put("Token", token);
		logDataDelete.put("LogID", "ad6889e2-b56a-4134-9a24-76ea645d6205");

		// Convert the JSON object to a string
		String logDataDeleteString = logDataDelete.toString();

		// Create a new HTTP connection for the log request
		HttpURLConnection logConDelete = (HttpURLConnection) new URL(logUrlDelete).openConnection();
		logConDelete.setRequestMethod("DELETE");

		// Set the content type of the request to application/json
		logConDelete.setRequestProperty("Content-Type", "application/json");

		// Enable output on the connection to allow sending the request body
		logConDelete.setDoOutput(true);

		// Write the log request body to the connection's output stream
		try (OutputStream os = logConDelete.getOutputStream()) {
			byte[] logDataBytes = logDataDeleteString.getBytes(StandardCharsets.UTF_8);
			os.write(logDataBytes, 0, logDataBytes.length);
		}

		// Read the response from the log request's input stream
		int logResponseCodeDelete = logConDelete.getResponseCode();
		String logResponseMessageDelete = logConDelete.getResponseMessage();

		// Print the response from the log request
		System.out.println("Log request code: " + logResponseCodeDelete + "DELETE");
		System.out.println("Log request message: " + logResponseMessageDelete);
	}

	private static void connectWebSocket(String token) throws Exception {
		WebSocketContainer container = ContainerProvider.getWebSocketContainer();
		String wsUrl = "ws://localhost:8081/getLogs?Token=" + token;
		webSocketSession = container.connectToServer(WebSocketClient.class, new URI(wsUrl));

	}

}
