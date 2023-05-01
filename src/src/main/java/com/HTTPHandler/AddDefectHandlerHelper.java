package com.HTTPHandler;

import com.HTTPHandler.server.HandlerHelpers;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class AddDefectHandlerHelper extends HandlerHelpers {
    public boolean correctAttributes(String requestBody) throws Exception {
        // Assume requestBody is the JSON string received in the HTTP request
        ObjectMapper mapper = new ObjectMapper();

        JsonNode jsonNode = mapper.readTree(requestBody);

        if (jsonNode.has("Token")
                && jsonNode.has("stepWhenInjected")
                && jsonNode.has("stepWhenRemoved")
                && jsonNode.has("defectCategory")
                && jsonNode.has("fixStatus")
                && jsonNode.has("name")
                && jsonNode.has("description")) {
            System.out.println("correct attributes");

            return true;

        } else {
            System.out.println("incorrect attributes");
            return false;
        }
    }

}
