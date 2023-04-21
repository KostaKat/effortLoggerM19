package com.HTTPHandler.TestServer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.security.NoSuchAlgorithmException;
import com.HTTPHandler.server.RegisterHandlerHelper;

public class RegisterHandlerHelperTest {

    private RegisterHandlerHelper helper;

    @BeforeEach
    public void setUp() throws Exception {
        helper = new RegisterHandlerHelper();
    }

    @Test
    public void testCorrectAttributes() throws Exception {
        String requestBody = "{\"Username\":\"testuser\", \"Password\":\"password\", \"First-Name\":\"John\", \"Last-Name\":\"Doe\", \"User-Type\":\"client\"}";
        assertTrue(helper.correctAttributes(requestBody));
    }

    @Test
    public void testIncorrectAttributes() throws Exception {
        String requestBody = "{\"Username\":\"testuser\", \"Password\":\"password\", \"User-Type\":\"client\"}";
        assertFalse(helper.correctAttributes(requestBody));
    }

    @Test
    public void testCreateKeys() throws NoSuchAlgorithmException {
        assertNotNull(helper.createKeys().get("public-key"));
        assertNotNull(helper.createKeys().get("private-key"));
    }

    @Test
    public void testCheckIfUserExists() throws Exception {
        String requestBody = "{\"Username\":\"testuser\"}";
        assertTrue(helper.checkIfUserExists(requestBody));
    }


    @Test
    public void testGenerateToken() throws NoSuchAlgorithmException {
        assertNotNull(helper.generateToken("testuser", "client"));
    }

}