package com.HTTPHandler.server;

import java.sql.SQLException;
import java.util.Map;

import com.Database.DatabaseManager;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class DeleteLogHandlerHelper extends HandlerHelpers {
    public boolean correctAttributes(String requestBody) throws Exception {
        // Assume requestBody is the JSON string received in the HTTP request
        ObjectMapper mapper = new ObjectMapper();

        JsonNode jsonNode = mapper.readTree(requestBody);
        System.out.println(jsonNode.toString());
        if (jsonNode.has("Token")
                && jsonNode.has("LogID")) {

            return true;

        } else {
            return false;
        }
    }

    public boolean deleteLogSuccessful(String requestBody)
            throws JsonMappingException, JsonProcessingException, SQLException {
        ObjectMapper objectMapper = new ObjectMapper();
        DatabaseManager databaseManager = new DatabaseManager();

        JsonNode jsonNode = objectMapper.readTree(requestBody);
        String logID = jsonNode.get("LogID").asText();
        Map<String, String> claims = this.getClaims(this.getToken(requestBody));
        String userName = claims.get("Username");
        String userType = claims.get("User-Type");
        String userID = databaseManager.getIDbyUsernameUserType(userName, userType);
        return databaseManager.deleteLog(logID, userID);

    }
}
