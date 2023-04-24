package com.HTTPHandler.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class AddLogHandlerHelper extends HandlerHelpers{
    public boolean correctAttributes(String requestBody) throws Exception {
        // Assume requestBody is the JSON string received in the HTTP request
        ObjectMapper mapper = new ObjectMapper();
    
        JsonNode jsonNode = mapper.readTree(requestBody);
        System.out.println(jsonNode.toString());
        if (jsonNode.has("Token")
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
    
    
}
