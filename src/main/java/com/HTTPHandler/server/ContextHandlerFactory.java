package com.HTTPHandler.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.Database.DatabaseManager;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.sql.*;
/**
 * <p>A factory class that creates HttpHandler objects based on the given path.</p>
 * @author Kosta Katergaris
 * @version prototype
 */
public class ContextHandlerFactory {
	/**
     * <p>Returns an HttpHandler object for the given path.</p>
     *
     * @param path the path to create an HttpHandler for
     * @return an HttpHandler object
     * @author Kosta Katergaris
     * @version prototype
     */
    HttpHandler create(String path) {
        switch (path) {
            case "/login":
                return new LoginContextHandler();
            case "/register":
                return new RegisterContextHandler();
            case "/addLog":
                return new AddLogContextHandler();
            case "/editLog":
                return new EditLogContextHandler();
            case "/deleteLog":
                return new DeleteLogContextHandler();
            case "/getLog":
            	return new GetLogContextHandler();
            default:
                return new NotFoundHandler();
        }
    }
}
/**
 * <p>An HTTP handler for a user logging in to the application.</p>
 * @author Kosta Katergaris
 * @version prototype
 */

class LoginContextHandler implements HttpHandler {
    private LoginHandlerHelper helper = new LoginHandlerHelper();
	/**
     * <p>Handles an HTTP exchange for a user logging in</p>
     * @author Kosta Katergaris
     * @param exchange - HTTP exchange to handle
     * @throws IOException if an I/O error occurs while handling the exchange
     * @version prototype
     */
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String requestMethod = exchange.getRequestMethod().toString();
            if ("POST".equals(requestMethod)) {
                // Get the request body
                InputStreamReader isr = new InputStreamReader(exchange.getRequestBody());
                BufferedReader br = new BufferedReader(isr);
                StringBuilder sb = new StringBuilder();
                String line;
              
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
                String requestBody = sb.toString();
                
                // Process the login request and check the credentials
                
                
                boolean loginSuccess = proccessLoginRequest(requestBody);

                int code;
                Object responseObj;
                if (loginSuccess) {
                    ObjectMapper objectMapper = new ObjectMapper();
                    JsonNode jsonNode = objectMapper.readTree(requestBody);

                    //decrypt attributes w/server priv key
                    
                    //get username & user-type
                    String username = jsonNode.get("Username").asText();
                    String userType = null ;
                    // Replace the database URL with your own
                    String url = "jdbc:sqlite:mydatabase.db";
                   

                    try (Connection conn = DriverManager.getConnection(url);
                         PreparedStatement stmt = conn.prepareStatement(
                                 "SELECT UserType FROM Employee WHERE Username = ?")) {
                        stmt.setString(1, username);
                        ResultSet rs = stmt.executeQuery();
                        if (rs.next()) {
                             userType = rs.getString("UserType");
                            System.out.println("User type for " + username + ": " + userType);
                        } else {
                            System.out.println("User " + username + " not found.");
                        }
                    } catch (SQLException e) {
                        System.out.println("Error retrieving user type: " + e.getMessage());
                    }
                    //Make JSON response
                    ObjectNode jsonResponse = objectMapper.createObjectNode();
                    
                    //Create token
                    String token = null;
                   
					token = helper.generateToken(username, userType);
						
					// 
                    jsonResponse.put("Auth-Token", token);
                    jsonResponse.put("status", "success");
                   
                    jsonResponse.put("message", "Login successful.");
                    
                    //encrypt w/client's pub key 
                    

                    //save token in database
                    try{
                        Connection connection = DriverManager.getConnection("jdbc:sqlite:mydatabase.db");
                        String sql = "UPDATE Employee SET Token = ? WHERE Username = ?";
                        PreparedStatement statement = connection.prepareStatement(sql);
                        statement.setString(1, token);
                        statement.setString(2, username);
                        statement.executeUpdate();


                    }catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                    responseObj = jsonResponse;
                    code = 200;
                } else {
                   
                    ObjectMapper objectMapper = new ObjectMapper();
                    ObjectNode jsonResponse = objectMapper.createObjectNode();
                    jsonResponse.put("status", "failure");
                    jsonResponse.put("message", "Invalid username or password");

                    responseObj = jsonResponse;
                    code = 401;
                }
                helper.sendJsonResponse(exchange, code, responseObj);

            } else {
                // return error response for unsupported method
                String response = "Unsupported request method: " + requestMethod;
                helper.sendErrorResponse(exchange, 400, response);
            }
        } catch (Exception e) {
            // handle any exceptions that occur while processing the request
            helper.sendErrorResponse(exchange, 500, e.getMessage());
        }
    }
	/**
     * <p>Processes a request to login.</p>
     * 
     * @param requestBody - Request body containing the log information
     * @return true if the user was successfully logged in, false otherwise
     * @author Kosta Katergaris
     * @version prototype
     */
    private boolean proccessLoginRequest(String requestBody) throws JsonMappingException, JsonProcessingException {
        LoginHandlerHelper helper = new LoginHandlerHelper();
        //decrypt attributes w/server priv key
        // check if requestBody is a JSON object
        //check if the json has the correct attributes
        // if they are in the database

        if (!helper.isJSON(requestBody)) {
            return false;
        }

        if (!helper.correctAttributes(requestBody)) {
            return false;
        }

        if (!helper.userMatches(requestBody)) {
            return false;
        }

        return true;
        
        
    }
}
/**
 * <p>An HTTP handler for a user registering for the application.</p>
 * @author Kosta Katergaris
 * @version prototype
 */

class RegisterContextHandler implements HttpHandler {
	//attributes
	private RegisterHandlerHelper helper = new RegisterHandlerHelper();
	/**
     * <p>Handles an HTTP exchange for registering a user. </p>
     * @author Kosta Katergaris
     * @param exchange - HTTP exchange to handle
     * @throws IOException if an I/O error occurs while handling the exchange
     * @version prototype
     */
	@Override
	public void handle(HttpExchange exchange) throws IOException {
		 try {
	            RegisterHandlerHelper helper = new RegisterHandlerHelper();
                DatabaseManager databaseManager = new DatabaseManager();
	            System.out.println("Register context handler started");
	            String requestMethod = exchange.getRequestMethod();
	            if ("POST".equals(requestMethod)) {
	                // Get the request body
	                InputStreamReader isr = new InputStreamReader(exchange.getRequestBody());
	                BufferedReader br = new BufferedReader(isr);
	                String requestBody = br.readLine();

	                // Process the register request and check the credentials
	                boolean registerSuccess = proccessRegisterRequest(requestBody);
	                System.out.println("Register success: " + registerSuccess);

	                ObjectMapper objectMapper = new ObjectMapper();
	                if (registerSuccess) {
	                    JsonNode jsonNode = objectMapper.readTree(requestBody);
                        ObjectNode jsonResponse = objectMapper.createObjectNode();
	                    // Retrieving values of each attribute 
	                    String username = jsonNode.get("Username").asText();
	                    String password = jsonNode.get("Password").asText();
	                    String firstName = jsonNode.get("First-Name").asText();
	                    String lastName = jsonNode.get("Last-Name").asText();
	                    String userType = jsonNode.get("User-Type").asText();
                        String employeeID = helper.generateEmployeeID();
                        
                        if(userType.equalsIgnoreCase("Employee")){
                            String managerID = jsonNode.get("ManagerID").asText();

                            System.out.println("Generating employee ID");
                            

                            databaseManager.insertNewEmployee(employeeID, firstName, lastName, 
                                                                username, password, userType, 
                                                                managerID, managerID);
                            
                        }else{
                            System.out.println("Generating manager ID");
                            databaseManager.insertNewManager(employeeID, firstName, lastName, username, password, userType);
                            jsonResponse.put("ManagerID", employeeID);
                        }
	                    System.out.println("Registering user with username: " + username);

	                   
	                    // Generate the key pair using the createKeys() method
	                    // Map<String, String> keys = helper.createKeys();
	                    // Add the keys to the JSON response
	                    
	                    jsonResponse.put("status", "success");
	                    jsonResponse.put("message", "Registration successful.");
	                    // //send server's public key
	                    // jsonResponse.put("public-key-server", keys.get("public-key"));
	                    // //send client's private key
	                    // jsonResponse.put("private-key-client", keys.get("private-key"));
	                    // System.out.println("Sending registration response to client");

	                    //save client public key in database
	                    String response = objectMapper.writeValueAsString(jsonResponse);
	                    helper.sendJsonResponse(exchange, 200, response);
	                } else {
	                    ObjectNode jsonResponse = objectMapper.createObjectNode();
	                    jsonResponse.put("status", "failure");
	                    jsonResponse.put("message", "Invalid username or password.");
	                    String response = objectMapper.writeValueAsString(jsonResponse);
	                    helper.sendJsonResponse(exchange, 400, response);
	                }
	            } else {
	                // return error response for unsupported method
	                String response = "Unsupported request method: " + requestMethod;
	                helper.sendErrorResponse(exchange, 400, response);
	            }
	    } catch (Exception e) {
	        // handle any exceptions that occur while processing the request
	        helper.sendErrorResponse(exchange, 500, e.getMessage());
	    }
	}
	/**
     * <p>Processes a request to register.</p>
     * 
     * @param requestBody - Request body containing the log information
     * @return true if the user was successfully registered, false otherwise
     * @author Kosta Katergaris
     * @version prototype
     */
    private boolean proccessRegisterRequest(String requestBody) throws JsonMappingException, JsonProcessingException {
        RegisterHandlerHelper helper = new RegisterHandlerHelper();
    	// check if requestBody is a JSON object
    	if(!helper.isJSON(requestBody))
    		return false;
    	if(!helper.correctAttributes(requestBody))
    		return false;
           
    	//check if there username exists in db
        if(!helper.checkIfUserExists(requestBody))
        	return false;
        
        return true;
    }
}
/**
 * <p>An HTTP handler for adding a log to the database.</p>
 * @author Kosta Katergaris
 * @version prototype
 */
class AddLogContextHandler implements HttpHandler {
	//attributes
	private HandlerHelpers helper = new HandlerHelpers();
	
    /**
     * <p>Handles an HTTP exchange for adding a log to the database.
     * To be implemented in the future</p>
     * 
     * @param exchange - HTTP exchange to handle
     * @throws IOException if an I/O error occurs while handling the exchange
     * @version prototype
     */
	@Override
	public void handle(HttpExchange exchange) throws IOException {
	    try {
	        String requestMethod = exchange.getRequestMethod();
	        if ("POST".equals(requestMethod)) {
	            // Get the request body
	            InputStreamReader isr = new InputStreamReader(exchange.getRequestBody());
	            BufferedReader br = new BufferedReader(isr);
	            String requestBody = br.readLine();

	            // Process the addLog request and check the credentials
	            boolean addLogSuccess = proccessAddLogRequest(requestBody);

	            // Send the response to the client & code
	            String response;
	            int code;
	            if (addLogSuccess) {
	            	//decrypt log attributes w/server private key
	            	
	                response = "Added log successfully!";
	                code = 200;
	            } else {
	            	code =500;
	                response = "Couldn't add log.";
	            }
	            helper.sendJsonResponse(exchange, code, response);
	        } else {
	            // return error response for unsupported method
	            String response = "Unsupported request method: " + requestMethod;
	            helper.sendErrorResponse(exchange, 400, response);
	        }
	    } catch (Exception e) {
	        // handle any exceptions that occur while processing the request
	        String response = "Error processing request: " + e.getMessage();
	        helper.sendErrorResponse(exchange, 500, response);
	    }
	}
    /**
     * <p>Processes a request to add a log to the database.
     * 	To be implemented in the future</p>
     * 
     * @param requestBody - Request body containing the log information
     * @return true if the log was added successfully, false otherwise
     * @author Kosta Katergaris
     * @version prototype
     * @throws Exception
     */
    private boolean proccessAddLogRequest(String requestBody) throws Exception {
        AddLogHandlerHelper helper = new AddLogHandlerHelper();
        //check if requestBody is a JSON object
    	if(!helper.isJSON(requestBody))
    		return false;
    	if(!helper.correctAttributes(requestBody))
    		return false;
    	//check if there username exists in db
        if(!helper.verifyToken(helper.getToken(requestBody)))
        	return false;
        return true;      	        
        
    }
}
/**
 * <p>An HTTP handler for editing a log to the server.</p>
 * @author Kosta Katergaris
 * @version prototype
 */
class EditLogContextHandler implements HttpHandler {
	//attributes
	private HandlerHelpers helper = new HandlerHelpers();
	 /**
     * <p>Handles an HTTP exchange for editing a log in the database.
     * To be implemented in the future</p>
     * 
     * @param exchange - HTTP exchange to handle
     * @throws IOException if an I/O error occurs while handling the exchange
     * @author Kosta Katergaris
     * @version prototype
     */
	@Override
	public void handle(HttpExchange exchange) throws IOException {
	    try {
	        String requestMethod = exchange.getRequestMethod();
	        if ("PUT".equals(requestMethod)) {
	            // Get the request body
	            InputStreamReader isr = new InputStreamReader(exchange.getRequestBody());
	            BufferedReader br = new BufferedReader(isr);
	            String requestBody = br.readLine();

	            // Process the editLog request and check the credentials
	            boolean editLogSuccess = proccessEditLogRequest(requestBody);

	            // Send the response to the client
	            String response;
	            int code;
	            if (editLogSuccess) {
	            	//decrypt log w/server priv key
	                response = "Editted log successfully!";
	                code = 200;
	            } else {
	                response = "Couldn't edit log.";
	                code = 500 ;
	            }
	            helper.sendJsonResponse(exchange, code, response);
	        } else {
	            // return error response for unsupported method
	            String response = "Unsupported request method: " + requestMethod;
	            helper.sendErrorResponse(exchange, 400, response);
	        }
	    } catch (Exception e) {
	        // handle any exceptions that occur while processing the request
	        String response = "Error processing request: " + e.getMessage();
	        helper.sendErrorResponse(exchange, 500, response);
	    }
	}
	 /**
     * <p>Processes a request to edit a log in the database.
     * 	To be implemented in the future</p>
     * 
     * @param requestBody - Request body containing the log information
     * @return true if the log was edited successfully, false otherwise
     * @author Kosta Katergaris
     * @version prototype
     */
    private boolean proccessEditLogRequest(String requestBody) {
      
        return true;
    }
}
/**
 * <p>An HTTP handler for deleting in the database.</p>
 * @author Kosta Katergaris
 * @version prototype
 */
class DeleteLogContextHandler implements HttpHandler {
	//attributes
	private HandlerHelpers helper = new HandlerHelpers();
	
	 /**
     * <p>Handles an HTTP exchange for deleting a log in the database.
     * To be implemented in the future</p>
     * 
     * @param exchange - HTTP exchange to handle
     * @throws IOException if an I/O error occurs while handling the exchange
     * @author Kosta Katergaris
     * @version prototype
     */
	@Override
	public void handle(HttpExchange exchange) throws IOException {
	    try {
	        String requestMethod = exchange.getRequestMethod();
	        if ("DELETE".equals(requestMethod)) {
	            // Get the request body
	            InputStreamReader isr = new InputStreamReader(exchange.getRequestBody());
	            BufferedReader br = new BufferedReader(isr);
	            String requestBody = br.readLine();

	            // Process the login request and check the credentials
	            boolean deleteLogSuccess = proccessDeleteLogRequest(requestBody);

	            // Send the response to the client
	            String response;
	            int code;
	            if (deleteLogSuccess) {
	                //decrypt w/ server priv key
	                response = "Deleted log successfully!";
	                code = 200;
	            } else {
	                response = "Couldn't delete log.";
	                code = 500;
	            }
	            helper.sendJsonResponse(exchange, code, response);
	        } else {
	            // return error response for unsupported method
	            helper.sendErrorResponse(exchange, 400, "Unsupported request method: " + requestMethod);
	        }
	    } catch (Exception e) {
	        // handle any exceptions that occur while processing the request
	        helper.sendErrorResponse(exchange, 500, "Error processing request: " + e.getMessage());
	    }
	}

	 /**
     * <p>Processes a request to delete a log in the database.
     * 	To be implemented in the future</p>
     * 
     * @param requestBody - Request body containing the log information
     * @return true if the log was deleted successfully, false otherwise
     * @author Kosta Katergaris
     * @version prototype
     */
    private boolean proccessDeleteLogRequest(String requestBody) {
        // Implement the logic to process the login request and check the credentials
        return true; // or false depending on whether the login was successful
    }
}
/**
 * <p>An HTTP handler for handling contexts that don't exist for our server.</p>
 * @author Kosta Katergaris
 * @version prototype
 */
class NotFoundHandler implements HttpHandler {
	HandlerHelpers helper = new HandlerHelpers();
	/**
     * <p>Handles an HTTP exchange for editing a log in the database.
     * To be implemented in the future</p>
     * 
     * @param exchange - HTTP exchange to handle
     * @throws IOException if an I/O error occurs while handling the exchange
     * @author Kosta Katergaris
     * @version prototype
     */
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        int code = 404;
        String response = "The requested resource could not be found.";
        helper.sendErrorResponse(exchange, code, response);
    }
}
/**
 * <p>An HTTP handler for getting a log from the database.</p>
 * @author Kosta Katergaris
 * @version prototype
 */
class GetLogContextHandler implements HttpHandler {
	private HandlerHelpers helper = new HandlerHelpers();
	/**
     * <p>Handles an HTTP exchange for getting a log in the database.
     * To be implemented in the future</p>
     * 
     * @param exchange - HTTP exchange to handle
     * @throws IOException if an I/O error occurs while handling the exchange
     * @author Kosta Katergaris
     * @version prototype
     */
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String requestMethod = exchange.getRequestMethod();
            if ("GET".equals(requestMethod)) {
                // Get the request body
                InputStreamReader isr = new InputStreamReader(exchange.getRequestBody());
                BufferedReader br = new BufferedReader(isr);
                String requestBody = br.readLine();

                // Process the get log request and check the credentials
                boolean getLogSuccess = processGetLogRequest(requestBody);

                if (getLogSuccess) {
                    // Send the log response to the client
                    String response = "Getting Log successful!";
                    helper.sendJsonResponse(exchange, 200, response);
                } else {
                    // Send error response for unsuccessful log retrieval
                    String response = "Get Log unsuccessful.";
                    helper.sendErrorResponse(exchange, 404, response);
                }
            } else {
                // Send error response for unsupported method
                String response = "Unsupported request method: " + requestMethod;
                helper.sendErrorResponse(exchange, 400, response);
            }
        } catch (Exception e) {
            // Send error response for any exceptions that occur while processing the request
            String response = "Error processing request: " + e.getMessage();
            helper.sendErrorResponse(exchange, 500, response);
        }
    }
    /**
     * <p>Processes a request to getting a log in the database.
     * 	To be implemented in the future</p>
     * 
     * @param requestBody - Request body containing the log information
     * @return true if the getting the log was successful, false otherwise
     * @author Kosta Katergaris
     * @version prototype
     */
    private boolean processGetLogRequest(String requestBody) {
        
    	//decrypt token w/server priv key
    	//decrypt token w/token priv key
    	// check credentials of user of what information they could access
    	//check if the body has the right information
    	//check if content is json
        return true; 
    }
}