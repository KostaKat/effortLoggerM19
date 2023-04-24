package com.HTTPHandler.server;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import com.sun.net.httpserver.HttpExchange;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import com.Database.DatabaseManager;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * <p>
 * This is the HandlerHelpers class for the HTTP server application.
 * </p>
 * 
 * @version prototype
 * @author Kosta Katergaris
 */

public class HandlerHelpers {
    /**
     * <p>
     * Checks if the input string is valid JSON
     * </p>
     * 
     * @param requestBody the input string to check
     * @return true if the input string is valid JSON, false otherwise
     * @version prototype
     * @author Kosta Katergaris
     */
    public boolean isJSON(String requestBody) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            @SuppressWarnings("unused")
            JsonNode jsonNode = mapper.readTree(requestBody);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * <p>
     * Decrypts an encrypted message using the provided private key.
     * </p>
     * 
     * @param encrypted_message the message to be decrypted
     * @param privateKeyString  the private key used for decryption, in string
     *                          format
     * @return the decrypted message
     * 
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     * @throws NoSuchPaddingException
     * @throws InvalidKeyException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     * 
     * @author Yihui Wu
     * @version prototype 2.0
     */
    public String decryption(String encrypted_message, String privateKeyString)
            throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidKeyException,
            IllegalBlockSizeException, BadPaddingException {

        // This block of code was to retrieve the private key from a string
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        byte[] privateKeyBytes = Base64.getDecoder().decode(privateKeyString);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(privateKeyBytes);
        PrivateKey privateKey = keyFactory.generatePrivate(spec);

        // This block of code was used to decrypt the message use the public key
        byte[] encryptedMessage = Base64.getDecoder().decode(encrypted_message);
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] decryptedMessage = cipher.doFinal(encryptedMessage);
        String decryptedMessageString = new String(decryptedMessage);

        return decryptedMessageString;
    }

    /**
     * Encrypts a message using the provided public key.
     * 
     * @param encrypted_message the message to be encrypted
     * @param publicKeyString   the public key used for encryption, in string format
     * 
     * @return the encrypted message
     * 
     * @author Yihui Wu
     * @version prototype 2.0
     */
    public String encryption(String encrypted_message, String publicKeyString) {

        try {
            // retrieve the public key from the preset string
            byte[] bytePublic = Base64.getDecoder().decode(publicKeyString);
            KeyFactory factory = KeyFactory.getInstance("RSA");
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(bytePublic);
            PublicKey Key = factory.generatePublic(keySpec);
            // encrypt the message
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, Key);
            encrypted_message = Base64.getEncoder()
                    .encodeToString(cipher.doFinal(encrypted_message.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException | InvalidKeySpecException | NoSuchPaddingException
                | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            System.out.println(e);
        }
        // return the encrypt message
        return encrypted_message;
    }

    public String decryptToken(String token) {
        // decrypt w/server pub key

        // decrypt w/tokenPrivateKey
        return "";
    }

    /**
     * <p>
     * Sends a JSON response with the given code and response object to the client.
     * </p>
     *
     * @param exchange       the HttpExchange object
     * @param code           the response code
     * @param responseObject the response object to be sent
     * @throws IOException if an I/O error occurs
     * 
     * @author Kosta Katergaris
     * @version prototype
     */
    public void sendJsonResponse(HttpExchange exchange, int code, Object responseObject) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        String response = mapper.writeValueAsString(responseObject);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.sendResponseHeaders(code, response.length());

        OutputStream os = exchange.getResponseBody();
        if (os != null) {
            os.write(response.getBytes());
            os.close();
        }
    }

    /**
     * <p>
     * Sends an error response with the given code and error message to the client.
     * </p>
     *
     * @param exchange     the HttpExchange object
     * @param code         the response code
     * @param errorMessage the error message to be sent
     * @throws IOException if an I/O error occurs
     * 
     * @author Kosta Katergaris
     * @version prototype
     */

    public void sendErrorResponse(HttpExchange exchange, int code, String errorMessage) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "text/plain; charset=UTF-8"); // Add this line
        exchange.sendResponseHeaders(code, errorMessage.length());
        OutputStream os = exchange.getResponseBody();
        os.write(errorMessage.getBytes());
        os.close();
    }

    // we need methods for these
    public String getPublicKey() {
        return "";
    }

    public String getPrivateKey() {
        return "";
    }

    /**
     * <p>
     * Generates a token for the given username and user type using RSA-256
     * algorithm.
     * </p>
     * 
     * @param username the username
     * @param userType the user type
     * @return the generated token
     * @throws NoSuchAlgorithmException if the RSA algorithm is not available
     * @author Kosta Katergaris
     * @version prototype
     */
    public String generateToken(String username, String userType) throws NoSuchAlgorithmException {

        RegisterHandlerHelper helper = new RegisterHandlerHelper();

        Map<String, Object> keys = new HashMap<>();
        try (InputStream publicStream = HandlerHelpers.class.getResourceAsStream("/org/keys/token_public_key.der");
                InputStream privateStream = HandlerHelpers.class
                        .getResourceAsStream("/org/keys/token_private_key.der")) {
            keys = loadKeys(publicStream, privateStream);
        } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
        }

        RSAPublicKey publicKey = (RSAPublicKey) keys.get("public-key");
        RSAPrivateKey privateKey = (RSAPrivateKey) keys.get("private-key");

        Algorithm algorithm = Algorithm.RSA256(publicKey, privateKey);

        String token = null;
        try {
            token = JWT.create()
                    .withIssuer("auth0")
                    .withClaim("Username", username)
                    .withClaim("User-Type", userType)
                    .sign(algorithm);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return token;
    }

    public Map<String, Object> loadKeys(InputStream publicStream, InputStream privateStream)
            throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        try {
            Map<String, Object> keys = new HashMap<>();
            System.out.println("In load ");

            // Load public key from stream
            byte[] publicBytes = publicStream.readAllBytes();
            X509EncodedKeySpec publicSpec = new X509EncodedKeySpec(publicBytes);
            RSAPublicKey publicKey = (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(publicSpec);
            keys.put("public-key", publicKey);

            // Load private key from stream
            byte[] privateBytes = privateStream.readAllBytes();
            PKCS8EncodedKeySpec privateSpec = new PKCS8EncodedKeySpec(privateBytes);
            RSAPrivateKey privateKey = (RSAPrivateKey) KeyFactory.getInstance("RSA").generatePrivate(privateSpec);
            keys.put("private-key", privateKey);
            return keys;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    public boolean verifyToken(String token) throws Exception {
        System.out.println("In  verify");

        DatabaseManager databaseManager = new DatabaseManager();
        try (InputStream publicStream = HandlerHelpers.class.getResourceAsStream("/org/keys/token_public_key.der");
                InputStream privateStream = HandlerHelpers.class
                        .getResourceAsStream("/org/keys/token_private_key.der")) {

            Map<String, Object> keys = loadKeys(publicStream, privateStream);
            RSAPublicKey publicKey = (RSAPublicKey) keys.get("public-key");
            RSAPrivateKey privateKey = (RSAPrivateKey) keys.get("private-key");

            JWTVerifier verifier = JWT.require(Algorithm.RSA256(publicKey, privateKey))
                    .withIssuer("auth0")
                    .build();

            DecodedJWT decodedJWT = verifier.verify(token);

            // extract the claims
            String username = decodedJWT.getClaim("Username").asString();
            String userType = decodedJWT.getClaim("User-Type").asString();

            // check expiration
            Date expiresAt = decodedJWT.getExpiresAt();
            if (expiresAt != null && expiresAt.before(new Date())) {
                throw new Exception("Token expired!");
            }

            // check trusted issuer
            String issuer = decodedJWT.getIssuer();
            if (!"auth0".equals(issuer)) {
                throw new Exception("Token issued by untrusted issuer!");
            }

            // check valid user
            // query the database to see if the username and userType match a record in the
            // Employee table
            if (!databaseManager.isUserTokenValid(token, username, userType)) {
                throw new Exception("Invalid user!");
            }
            return true;
        } catch (JWTVerificationException | IOException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new Exception("Error verifying token: " + e.getMessage());
        }
    }

    public String getToken(String requestBody) throws JsonMappingException, JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        System.out.println("In get token");

        JsonNode jsonNode = mapper.readTree(requestBody);
        if (jsonNode.has("Token")) {
            return jsonNode.get("Token").asText();
        } else {
            return null;
        }
    }

    public Map<String, String> getClaims(String token) {
        System.out.println("In get cliamd");

        Map<String, String> claims = new HashMap<>();
        try {
            DecodedJWT decodedJWT = JWT.decode(token);
            claims.put("Username", decodedJWT.getClaim("Username").asString());
            claims.put("User-Type", decodedJWT.getClaim("User-Type").asString());
        } catch (JWTDecodeException e) {
            e.printStackTrace();
        }
        return claims;
    }

}