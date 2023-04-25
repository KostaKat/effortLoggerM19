package com.HTTPHandler.server;

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
}
