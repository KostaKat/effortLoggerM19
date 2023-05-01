package com.HTTPHandler.server;

import java.sql.SQLException;
import java.util.Map;

import com.Database.DatabaseManager;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class DeleteDefectHandlerHelper extends HandlerHelpers {
    public boolean correctAttributes(String requestBody) throws Exception {
        // Assume requestBody is the JSON string received in the HTTP request
        ObjectMapper mapper = new ObjectMapper();

        JsonNode jsonNode = mapper.readTree(requestBody);
        System.out.println("Attributes JSON" + "" + jsonNode.toString());
        if (jsonNode.has("Token")
                && jsonNode.has("defectID")) {

            return true;

        } else {
            return false;
        }
    }

    public boolean deleteDefectSuccessful(String requestBody) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            DatabaseManager databaseManager = new DatabaseManager();

            JsonNode jsonNode = objectMapper.readTree(requestBody);
            String defectID = jsonNode.get("defectID").asText();
            Map<String, String> claims = this.getClaims(this.getToken(requestBody));
            String userName = claims.get("Username");
            String userType = claims.get("User-Type");
            String userID = databaseManager.getIDbyUsernameUserType(userName, userType);
            return databaseManager.deleteDefect(defectID, userID, userType);
        } catch (Exception e) {
            System.out.println("Error in delete defect handler helper");
            return false;
        }

    }

}