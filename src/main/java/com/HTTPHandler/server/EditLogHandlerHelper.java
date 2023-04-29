package com.HTTPHandler.server;

import java.sql.SQLException;
import java.util.Map;

import com.Database.DatabaseManager;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class EditLogHandlerHelper extends HandlerHelpers {
    public boolean correctAttributes(String requestBody) throws Exception {
        // Assume requestBody is the JSON string received in the HTTP request
        ObjectMapper mapper = new ObjectMapper();

        JsonNode jsonNode = mapper.readTree(requestBody);
        System.out.println(jsonNode.toString());
        if (jsonNode.has("Token")
                && jsonNode.has("LogID")
                && jsonNode.has("Date")
                && jsonNode.has("StartTime")
                && jsonNode.has("EndTime")
                && jsonNode.has("Project")
                && jsonNode.has("EffortCategory")
                && jsonNode.has("EffortDetail")
                && jsonNode.has("LifeCycleStep")) {

            return true;

        } else {
            return false;
        }
    }

    public boolean editLogSuccess(String requestBody)
            throws SQLException, JsonMappingException, JsonProcessingException {
        DatabaseManager databaseManager = new DatabaseManager();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(requestBody);
        String date = jsonNode.get("Date").asText();
        String startTime = jsonNode.get("StartTime").asText();
        String endTime = jsonNode.get("EndTime").asText();
        String project = jsonNode.get("Project").asText();
        String effortCategory = jsonNode.get("EffortCategory").asText();
        String effortDetail = jsonNode.get("EffortDetail").asText();
        String lifeCycleStep = jsonNode.get("LifeCycleStep").asText();
        String logID = jsonNode.get("LogID").asText();
        Map<String, String> claims = this.getClaims(this.getToken(requestBody));
        String userName = claims.get("Username");
        String userType = claims.get("User-Type");
        String userID = databaseManager.getIDbyUsernameUserType(userName, userType);

        return databaseManager.editLog(logID, date, startTime, endTime, project, effortCategory, effortDetail,
                lifeCycleStep, userID);
    }
}
