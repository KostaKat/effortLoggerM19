package com.HTTPHandler.TestServer;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import java.io.OutputStream;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.HTTPHandler.server.HandlerHelpers;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;





public class HandlerHelpersTest {
    private HandlerHelpers handlerHelper;
    private final String privateKeyString ="MIIBVAIBADANBgkqhkiG9w0BAQEFAASCAT4wggE6Ag" +
            "EAAkEAokrdUFq70u7hrRA9CUcNd3AKddo6iA6lgPoNN93V1k38svVsnq" +
            "kqHPwmFWW0P5h3eD+CvIf3N33BMp/XeR/rqwIDAQABAkAtA9rzKNekIEJU" +
            "gIaNhjnEAT3FhqxphLZ/WdxEvLFNaYyMR537aXOHs22Q7oz0s5u12kq5q9ySuu" +
            "N45wgqmdIxAiEA/RXKJm1vJ1TMxsOCcV23c3XUTbPmCIx7M6q1TwaUJV8CIQCkKWBracN" +
            "5td0cKtHpopUL4BWhh4MAr5SXHDsSjNMxNQIgD2aICpaWMPqEC4RSJ7vgMfJ1nNTZpqOFy7sTl" +
            "V99HFkCIQCC+NwLdqtVbIqHUYZ+T494lQTNBe+32V3EnepnZ2GLzQIgMSAFRbozog9FfkiLqf6ELUKic" +
            "sAvMUr5n2MGugNjUj4="; 
    private final String publicKeyString = "MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAKJK3VBa" +
            "u9Lu4a0QPQlHDXdwCnXaOogOpYD6DTfd1dZN/LL1bJ6pKhz8JhVlt" +
            "D+Yd3g/gryH9zd9wTKf13kf66sCAwEAAQ==";
    private final String originalMessage = "Hello, World!";
    
    @BeforeEach
    public void setUp() {
        handlerHelper = new HandlerHelpers();
      
    }

    @Test
    public void testIsJSON_validJson_returnsTrue() {
      
        boolean result = handlerHelper.isJSON("{\"name\": \"John\", \"age\": 30}");
        assertTrue(result);
    }

    @Test
    public void testIsJSON_invalidJson_returnsFalse() {
        HandlerHelpers helper = new HandlerHelpers();
        boolean result = helper.isJSON("{name: John, age: 30}");
        assertFalse(result);
    }

    @Test
    public void testIsJSON_nonJsonString_returnsFalse() {
        boolean result = handlerHelper.isJSON("This is not a JSON string");
        assertFalse(result);
    }
    @Test
    public void testGenerateToken() throws NoSuchAlgorithmException {
    	
        String username = "testuser";
        String userType = "admin";

        String token = handlerHelper.generateToken(username, userType);

        assertNotNull(token);
        assertTrue(token.length() > 0);
    }

    @Test
    public void testEncryptionAndDecryption() throws Exception {
        
        String encryptedMessage = handlerHelper.encryption(originalMessage, publicKeyString);
        String decryptedMessage = handlerHelper.decryption(encryptedMessage, privateKeyString);

        assertEquals(originalMessage, decryptedMessage);
    }
    @Test
    public void testSendErrorResponse() throws Exception {
        HttpExchange exchange = mock(HttpExchange.class);
        OutputStream outputStream = mock(OutputStream.class);

        when(exchange.getResponseHeaders()).thenReturn(new Headers());
        when(exchange.getResponseBody()).thenReturn(outputStream);

        String errorMessage = "Error: Something went wrong!";
        handlerHelper.sendErrorResponse(exchange, 400, errorMessage);

        verify(exchange, times(1)).getResponseHeaders();
        verify(exchange, times(1)).sendResponseHeaders(400, errorMessage.length());
        verify(outputStream, times(1)).write(errorMessage.getBytes());
        verify(outputStream, times(1)).close();
    }
   

    @Test
    public void testSendJsonResponse() throws IOException {
        // create a mock HttpExchange object
        HttpExchange exchange = mock(HttpExchange.class);

        // create a test JSON object
        Map<String, String> testObject = new HashMap<>();
        testObject.put("name", "Alice");
        testObject.put("age", "30");

        // mock the getResponseHeaders method to return a mocked Headers instance
        Headers headers = mock(Headers.class);
        when(exchange.getResponseHeaders()).thenReturn(headers);

        // mock the response body stream
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        when(exchange.getResponseBody()).thenReturn(out);

        // call the method being tested
        handlerHelper.sendJsonResponse(exchange, 200, testObject);

        // verify that the expected response headers were set
        verify(exchange, times(1)).getResponseHeaders();
        verify(headers, times(1)).set("Content-Type", "application/json; charset=UTF-8");

        // verify that the expected response body was sent
        String expectedBody = "{\"name\":\"Alice\",\"age\":\"30\"}";
        verify(exchange, times(1)).sendResponseHeaders(200, expectedBody.length());
        out.flush();
        assertEquals(expectedBody, out.toString());
    }






    

}
