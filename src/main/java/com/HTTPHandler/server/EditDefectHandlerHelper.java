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
        if (jsonNode.has("Token")
                && jsonNode.has("stepWhenInjected")
                && jsonNode.has("stepWhenRemoved")
                && jsonNode.has("defectCategory")
                && jsonNode.has("fixStatus")
                && jsonNode.has("name")
                && jsonNode.has("description")
                && jsonNode.has("defectID")) {

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
        String description = jsonNode.get("description").asText();
        String name = jsonNode.get("name").asText();
        String fixStatus = jsonNode.get("fixStatus").asText();
        String stepWhenInjected = jsonNode.get("stepWhenInjected").asText();
        String stepWhenRemoved = jsonNode.get("stepWhenRemoved").asText();
        String defectCategory = jsonNode.get("defectCategory").asText();
        String defectID = jsonNode.get("defectID").asText();
        Map<String, String> claims = this.getClaims(this.getToken(requestBody));
        String userName = claims.get("Username");
        String userType = claims.get("User-Type");
        String userID = databaseManager.getIDbyUsernameUserType(userName, userType);

        return databaseManager.editDefect(defectID, userID, userType, description, name, fixStatus,
                stepWhenInjected, stepWhenRemoved, defectCategory);
    }

}