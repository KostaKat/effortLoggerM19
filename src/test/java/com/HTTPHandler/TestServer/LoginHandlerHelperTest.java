package com.HTTPHandler.TestServer;


import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.HTTPHandler.server.LoginHandlerHelper;
public class LoginHandlerHelperTest {

    @Test
    public void testCorrectAttributes() throws Exception {
        LoginHandlerHelper helper = new LoginHandlerHelper();
        String requestBody = "{\"Username\":\"john.doe\",\"Password\":\"password\",\"User-Type\":\"customer\"}";
        assertTrue(helper.correctAttributes(requestBody));
    }
    
    @Test
    public void testIncorrectAttributes() throws Exception {
        LoginHandlerHelper helper = new LoginHandlerHelper();
        String requestBody = "{\"Username\":\"john.doe\",\"User-Type\":\"customer\"}";
        assertFalse(helper.correctAttributes(requestBody));
    }
}
