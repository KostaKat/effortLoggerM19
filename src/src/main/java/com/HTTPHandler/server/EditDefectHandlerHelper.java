package com.HTTPHandler.server;

import java.sql.SQLException;
import java.util.Map;

import com.Database.DatabaseManager;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class EditDefectHandlerHelper extends HandlerHelpers {
    public boolean correctAttributes(String requestBody) throws Exception {
        // Assume requestBody is the JSON string received in the HTTP request
        ObjectMapper mapper = new ObjectMapper();

        JsonNode jsonNode = mapper.readTree(requestBody);
        System.out.println(jsonNode.toString());

        boolean hasToken = jsonNode.has("Token");
        boolean hasFixStatus = jsonNode.has("fixStatus");
        boolean hasDescription = jsonNode.has("description");
        boolean hasDefectID = jsonNode.has("defectID");

        if (hasToken

                && hasFixStatus
                && hasDefectID
                && hasDescription) {

            return true;

        } else {
            return false;
        }
    }

    public boolean editDefectSuccess(String requestBody)
            throws SQLException, JsonMappingException, JsonProcessingException {
        DatabaseManager databaseManager = new DatabaseManager();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(requestBody);
        System.out.println("JSON node" + jsonNode.toString());
        String description = jsonNode.get("description").asText();
        String fixStatus = jsonNode.get("fixStatus").asText();
        String defectID = jsonNode.get("defectID").asText();

        Map<String, String> claims = this.getClaims(this.getToken(requestBody));
        String userName = claims.get("Username");
        String userType = claims.get("User-Type");
        String userID = databaseManager.getIDbyUsernameUserType(userName, userType);

        return databaseManager.editDefect(defectID, userID, userType, description, fixStatus);
    }

}