package com.HTTPHandler.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.Database.DatabaseManager;
import com.HTTPHandler.AddDefectHandlerHelper;
import com.HTTPHandler.PasswordUtils;
import com.WebSocket.WebSocket;
import com.WebSocket.WebSocketManager;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.sql.*;

/**
 * <p>
 * A factory class that creates HttpHandler objects based on the given path.
 * </p>
 * 
 * @author Kosta Katergaris
 * @version prototype
 */
public class ContextHandlerFactory {
    /**
     * <p>
     * Returns an HttpHandler object for the given path.
     * </p>
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
            case "/addDefect":
                return new AddDefectContextHandler();
            case "/editDefect":
                return new EditDefectContextHandler();
            case "/deleteDefect":
                return new DeleteDefectContextHandler();
            default:
                return new NotFoundHandler();
        }
    }
}

/**
 * <p>
 * An HTTP handler for a user logging in to the application.
 * </p>
 * 
 * @author Kosta Katergaris
 * @version prototype
 */

class LoginContextHandler implements HttpHandler {
    private LoginHandlerHelper helper = new LoginHandlerHelper();
    private DatabaseManager dbManager = new DatabaseManager();

    /**
     * <p>
     * Handles an HTTP exchange for a user logging in
     * </p>
     * 
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

                    // decrypt attributes w/server priv key

                    // get username & user-type
                    String username = jsonNode.get("Username").asText();
                    String userType = null;
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
                    // Make JSON response
                    ObjectNode jsonResponse = objectMapper.createObjectNode();

                    // Create token
                    String token = null;

                    token = helper.generateToken(username, userType);

                    //
                    jsonResponse.put("Token", token);
                    jsonResponse.put("UserType", userType);
                    jsonResponse.put("status", "success");

                    jsonResponse.put("message", "Login successful.");

                    if (userType.equals("Manager")) {
                        jsonResponse.put("ManagerID", dbManager.getIDbyUsernameUserType(
                                helper.getClaims(token).get("Username"), helper.getClaims(token).get("User-Type")));
                        // encrypt w/client's pub key
                    }
                    // save token in database
                    try {
                        Connection connection = DriverManager.getConnection("jdbc:sqlite:mydatabase.db");
                        String sql = "UPDATE Employee SET Token = ? WHERE Username = ?";
                        PreparedStatement statement = connection.prepareStatement(sql);
                        statement.setString(1, token);
                        statement.setString(2, username);
                        statement.executeUpdate();

                    } catch (Exception e) {
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
            helper.sendErrorResponse(exchange, 500, e.getMessage() + "login");
        }
    }

    /**
     * <p>
     * Processes a request to login.
     * </p>
     * 
     * @param requestBody - Request body containing the log information
     * @return true if the user was successfully logged in, false otherwise
     * @author Kosta Katergaris
     * @version prototype
     */
    private boolean proccessLoginRequest(String requestBody) throws JsonMappingException, JsonProcessingException {
        LoginHandlerHelper helper = new LoginHandlerHelper();
        // decrypt attributes w/server priv key
        // check if requestBody is a JSON object
        // check if the json has the correct attributes
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
 * <p>
 * An HTTP handler for a user registering for the application.
 * </p>
 * 
 * @author Kosta Katergaris
 * @version prototype
 */

class RegisterContextHandler implements HttpHandler {
    // attributes
    private RegisterHandlerHelper helper = new RegisterHandlerHelper();

    /**
     * <p>
     * Handles an HTTP exchange for registering a user.
     * </p>
     * 
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
                    PasswordUtils passwordUtils = new PasswordUtils();
                    String salt = passwordUtils.generateSalt();
                    password = passwordUtils.hashPassword(password, salt);
                    if (userType.equalsIgnoreCase("Employee")) {
                        String managerID = jsonNode.get("ManagerID").asText();

                        System.out.println("Generating employee ID");

                        databaseManager.insertNewEmployee(employeeID, firstName, lastName,
                                username, password, userType,
                                managerID, salt);

                    } else {
                        System.out.println("Generating manager ID");
                        databaseManager.insertNewManager(employeeID, firstName, lastName,
                                username, password, userType, salt);
                        jsonResponse.put("ManagerID", employeeID);
                    }
                    System.out.println("Registering user with username: " + username);

                    jsonResponse.put("status", "success");
                    jsonResponse.put("message", "Registration successful.");

                    // save client public key in database
                    String response = objectMapper.writeValueAsString(jsonResponse);
                    helper.sendJsonResponse(exchange, 200, response);
                } else {
                    ObjectNode jsonResponse = objectMapper.createObjectNode();
                    jsonResponse.put("status", "failure");
                    jsonResponse.put("message", "Username already exists.");
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
     * <p>
     * Processes a request to register.
     * </p>
     * 
     * @param requestBody - Request body containing the log information
     * @return true if the user was successfully registered, false otherwise
     * @author Kosta Katergaris
     * @version prototype
     */
    private boolean proccessRegisterRequest(String requestBody) throws JsonMappingException, JsonProcessingException {
        RegisterHandlerHelper helper = new RegisterHandlerHelper();
        // check if requestBody is a JSON object
        if (!helper.isJSON(requestBody))
            return false;
        if (!helper.correctAttributes(requestBody))
            return false;

        // check if there username exists in db
        if (!helper.checkIfUserExists(requestBody))
            return false;

        return true;
    }
}

/**
 * <p>
 * An HTTP handler for adding a log to the database.
 * </p>
 * 
 * @author Kosta Katergaris
 * @version prototype
 */
class AddLogContextHandler implements HttpHandler {
    // attributes
    private AddLogHandlerHelper helper = new AddLogHandlerHelper();
    private DatabaseManager databaseManager = new DatabaseManager();

    /**
     * <p>
     * Handles an HTTP exchange for adding a log to the database.
     * To be implemented in the future
     * </p>
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
                    ObjectMapper objectMapper = new ObjectMapper();
                    JsonNode jsonNode = objectMapper.readTree(requestBody);
                    String date = jsonNode.get("Date").asText();
                    String startTime = jsonNode.get("StartTime").asText();
                    String endTime = jsonNode.get("EndTime").asText();
                    String project = jsonNode.get("Project").asText();
                    String effortCategory = jsonNode.get("EffortCategory").asText();
                    String effortDetail = jsonNode.get("EffortDetail").asText();
                    String lifeCycleStep = jsonNode.get("LifeCycleStep").asText();
                    Map<String, String> claims = helper.getClaims(helper.getToken(requestBody));
                    String userName = claims.get("Username");
                    String userType = claims.get("User-Type");
                    String userID = databaseManager.getIDbyUsernameUserType(userName, userType);

                    databaseManager.addLog(userName, userType, userID, date, startTime, endTime,
                            project, effortCategory, effortDetail,
                            lifeCycleStep);

                    response = "Added log successfully!";
                    code = 200;
                } else {
                    code = 500;
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
     * <p>
     * Processes a request to add a log to the database.
     * To be implemented in the future
     * </p>
     * 
     * @param requestBody - Request body containing the log information
     * @return true if the log was added successfully, false otherwise
     * @author Kosta Katergaris
     * @version prototype
     * @throws Exception
     */
    private boolean proccessAddLogRequest(String requestBody) throws Exception {

        // check if requestBody is a JSON object
        if (!helper.isJSON(requestBody))
            return false;
        if (!helper.correctAttributes(requestBody))
            return false;
        // check if there username exists in db
        if (!helper.verifyToken(helper.getToken(requestBody)))
            return false;
        return true;

    }
}

/**
 * <p>
 * An HTTP handler for editing a log to the server.
 * </p>
 * 
 * @author Kosta Katergaris
 * @version prototype
 */
class EditLogContextHandler implements HttpHandler {
    // attributes

    private DatabaseManager databaseManager = new DatabaseManager();
    private EditLogHandlerHelper helper = new EditLogHandlerHelper();

    /**
     * <p>
     * Handles an HTTP exchange for editing a log in the database.
     * To be implemented in the future
     * </p>
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
                    // decrypt log w/server priv key

                    response = "Editted log successfully!";
                    code = 200;
                } else {
                    response = "Couldn't edit log.";
                    code = 500;
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
            helper.sendErrorResponse(exchange, 500, response + "edit");
        }
    }

    /**
     * <p>
     * Processes a request to edit a log in the database.
     * To be implemented in the future
     * </p>
     * 
     * @param requestBody - Request body containing the log information
     * @return true if the log was edited successfully, false otherwise
     * @author Kosta Katergaris
     * @version prototype
     */
    private boolean proccessEditLogRequest(String requestBody) throws Exception {

        // check if requestBody is a JSON object
        if (!helper.isJSON(requestBody))
            return false;
        if (!helper.correctAttributes(requestBody))
            return false;
        // check if there username exists in db
        if (!helper.verifyToken(helper.getToken(requestBody)))
            return false;

        if (!helper.editLogSuccess(requestBody))
            return false;

        return true;

    }
}

/**
 * <p>
 * An HTTP handler for deleting in the database.
 * </p>
 * 
 * @author Kosta Katergaris
 * @version prototype
 */
class DeleteLogContextHandler implements HttpHandler {
    // attributes
    private DeleteLogHandlerHelper helper = new DeleteLogHandlerHelper();
    private DatabaseManager databaseManager = new DatabaseManager();

    /**
     * <p>
     * Handles an HTTP exchange for deleting a log in the database.
     * To be implemented in the future
     * </p>
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
                    // decrypt w/ server priv key
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
            helper.sendErrorResponse(exchange, 500, "Error processing request: " + e.getMessage() + "delete");
        }
    }

    /**
     * <p>
     * Processes a request to delete a log in the database.
     * To be implemented in the future
     * </p>
     * 
     * @param requestBody - Request body containing the log information
     * @return true if the log was deleted successfully, false otherwise
     * @author Kosta Katergaris
     * @version prototype
     * @throws Exception
     */
    private boolean proccessDeleteLogRequest(String requestBody) throws Exception {

        // check if requestBody is a JSON object
        if (!helper.isJSON(requestBody))
            return false;
        if (!helper.correctAttributes(requestBody))
            return false;
        // check if there username exists in db
        if (!helper.verifyToken(helper.getToken(requestBody)))
            return false;

        if (!helper.deleteLogSuccessful(requestBody))
            return false;
        return true;

    }
}

/**
 * <p>
 * An HTTP handler for handling contexts that don't exist for our server.
 * </p>
 * 
 * @author Kosta Katergaris
 * @version prototype
 */
class NotFoundHandler implements HttpHandler {
    HandlerHelpers helper = new HandlerHelpers();

    /**
     * <p>
     * Handles an HTTP exchange for editing a log in the database.
     * To be implemented in the future
     * </p>
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

class AddDefectContextHandler implements HttpHandler {
    // attributes
    private AddDefectHandlerHelper helper = new AddDefectHandlerHelper();
    private DatabaseManager databaseManager = new DatabaseManager();

    /**
     * <p>
     * Handles an HTTP exchange for adding a log to the database.
     * To be implemented in the future
     * </p>
     * 
     * @param exchange - HTTP exchange to handle
     * @throws IOException if an I/O error occurs while handling the exchange
     * @version prototype
     */
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        System.out.println("AddDefectContextHandler");
        try {
            String requestMethod = exchange.getRequestMethod();
            if ("POST".equals(requestMethod)) {
                // Get the request body
                InputStreamReader isr = new InputStreamReader(exchange.getRequestBody());
                BufferedReader br = new BufferedReader(isr);
                String requestBody = br.readLine();
                System.out.println(requestBody);
                // Process the addLog request and check the credentials
                boolean addDefectSuccesful = proccessAddDefectRequest(requestBody);
                System.out.println(addDefectSuccesful);
                // Send the response to the client & code
                String response;
                int code;
                if (addDefectSuccesful) {
                    ObjectMapper objectMapper = new ObjectMapper();
                    JsonNode jsonNode = objectMapper.readTree(requestBody);
                    String StepWhenInjected = jsonNode.get("stepWhenInjected").asText();
                    String StepWhenRemoved = jsonNode.get("stepWhenRemoved").asText();
                    String DefectCategory = jsonNode.get("defectCategory").asText();
                    String FixStatus = jsonNode.get("fixStatus").asText();
                    String Name = jsonNode.get("name").asText();
                    String Description = jsonNode.get("description").asText();
                    Map<String, String> claims = helper.getClaims(helper.getToken(requestBody));
                    String userName = claims.get("Username");
                    String userType = claims.get("User-Type");
                    String userID = databaseManager.getIDbyUsernameUserType(userName, userType);
                    databaseManager.addDefect(userID, Description, Name, FixStatus, StepWhenInjected,
                            StepWhenRemoved, DefectCategory);

                    response = "Added Defect successfully!";
                    code = 200;
                } else {
                    code = 500;
                    response = "Couldn't Defect log.";
                }
                helper.sendJsonResponse(exchange, code, response);
            } else {
                // return error response for unsupported method
                String response = "Unsupported request method: " + requestMethod;
                helper.sendErrorResponse(exchange, 400, response);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            // handle any exceptions that occur while processing the request
            String response = "Error processing request: " + e.getMessage();
            helper.sendErrorResponse(exchange, 500, response);
        }
    }

    /**
     * <p>
     * Processes a request to add a log to the database.
     * To be implemented in the future
     * </p>
     * 
     * @param requestBody - Request body containing the log information
     * @return true if the log was added successfully, false otherwise
     * @author Kosta Katergaris
     * @version prototype
     * @throws Exception
     */
    private boolean proccessAddDefectRequest(String requestBody) throws Exception {

        // check if requestBody is a JSON object
        if (!helper.isJSON(requestBody)) {
            System.out.println("not json");
            return false;
        }

        if (!helper.correctAttributes(requestBody)) {
            System.out.println("not correct attributes");
            return false;
        }

        // check if there username exists in db
        if (!helper.verifyToken(helper.getToken(requestBody))) {
            System.out.println("not verified token");
            return false;
        }

        return true;

    }
}

/**
 * <p>
 * An HTTP handler for editing a log to the server.
 * </p>
 * 
 * @author Kosta Katergaris
 * @version prototype
 */
class EditDefectContextHandler implements HttpHandler {
    // attributes

    private DatabaseManager databaseManager = new DatabaseManager();
    private EditDefectHandlerHelper helper = new EditDefectHandlerHelper();

    /**
     * <p>
     * Handles an HTTP exchange for editing a log in the database.
     * To be implemented in the future
     * </p>
     * 
     * @param exchange - HTTP exchange to handle
     * @throws IOException if an I/O error occurs while handling the exchange
     * @author Kosta Katergaris
     * @version prototype
     */
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            System.out.println("EditDefectContextHandler");
            String requestMethod = exchange.getRequestMethod();
            if ("PUT".equals(requestMethod)) {
                // Get the request body

                InputStreamReader isr = new InputStreamReader(exchange.getRequestBody());
                BufferedReader br = new BufferedReader(isr);
                String requestBody = br.readLine();

                // Process the editLog request and check the credentials
                boolean editDefectSuccess = proccessEditDefectRequest(requestBody);
                System.out.println("edit succes: " + editDefectSuccess);
                // Send the response to the client
                String response;
                int code;
                if (editDefectSuccess) {
                    // decrypt log w/server priv key

                    response = "Editted log successfully!";
                    code = 200;
                } else {
                    response = "Couldn't edit log.";
                    code = 500;
                }
                helper.sendJsonResponse(exchange, code, response);
            } else {
                // return error response for unsupported method
                String response = "Unsupported request method: " + requestMethod;
                helper.sendErrorResponse(exchange, 400, response);
            }
        } catch (Exception e) {
            // handle any exceptions that occur while processing the request
            String response = "Error processing request: " + e;
            helper.sendErrorResponse(exchange, 500, response + "edit");
        }
    }

    /**
     * <p>
     * Processes a request to edit a log in the database.
     * To be implemented in the future
     * </p>
     * 
     * @param requestBody - Request body containing the log information
     * @return true if the log was edited successfully, false otherwise
     * @author Kosta Katergaris
     * @version prototype
     */
    private boolean proccessEditDefectRequest(String requestBody) throws Exception {

        // check if requestBody is a JSON object
        if (!helper.isJSON(requestBody))
            return false;
        if (!helper.correctAttributes(requestBody)) {
            System.out.println("not correct attributes");
            return false;
        }
        // check if there username exists in db
        if (!helper.verifyToken(helper.getToken(requestBody))) {
            System.out.println("token not verified");
            return false;
        }

        if (!helper.editDefectSuccess(requestBody)) {
            System.out.println("not edit defect success");
            return false;
        }

        return true;

    }

}

class DeleteDefectContextHandler implements HttpHandler {
    // attributes
    private DeleteDefectHandlerHelper helper = new DeleteDefectHandlerHelper();
    private DatabaseManager databaseManager = new DatabaseManager();

    /**
     * <p>
     * Handles an HTTP exchange for deleting a log in the database.
     * To be implemented in the future
     * </p>
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
                System.out.println("DeleteDefectContextHandler");
                // Get the request body
                InputStreamReader isr = new InputStreamReader(exchange.getRequestBody());
                BufferedReader br = new BufferedReader(isr);
                String requestBody = br.readLine();

                // Process the login request and check the credentials
                boolean deleteDefectSuccess = proccessDeleteDefectRequest(requestBody);

                // Send the response to the client
                String response;
                int code;
                if (deleteDefectSuccess) {
                    // decrypt w/ server priv key
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
            helper.sendErrorResponse(exchange, 500, "Error processing request: " + e.getMessage() + "delete");
        }
    }

    /**
     * <p>
     * Processes a request to delete a log in the database.
     * To be implemented in the future
     * </p>
     * 
     * @param requestBody - Request body containing the log information
     * @return true if the log was deleted successfully, false otherwise
     * @author Kosta Katergaris
     * @version prototype
     * @throws Exception
     */
    private boolean proccessDeleteDefectRequest(String requestBody) throws Exception {

        // check if requestBody is a JSON object
        if (!helper.isJSON(requestBody))
            return false;
        if (!helper.correctAttributes(requestBody)) {
            System.out.println("not correct attributes");
            return false;
        }

        // check if there username exists in db
        if (!helper.verifyToken(helper.getToken(requestBody)))
            return false;

        if (!helper.deleteDefectSuccessful(requestBody)) {
            System.out.println("not delete defect success");
            return false;
        }

        return true;

    }
}
