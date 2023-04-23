package com.HTTPHandler.server;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.*;
import java.util.UUID;
/**
 * <p>
 * A helper class for the RegisterHandler that provides methods for validating
 * registration requests, generating RSA key pairs,
 * and checking if a user already exists in the database.
 * </p>
 * 
 * @extends HandlerHelpers
 * @version prototype
 * @author Kosta Katergaris
 */
public class RegisterHandlerHelper extends HandlerHelpers {
	/**
	 * <p>
	 * Checks if the attributes in the provided request body match the expected
	 * attributes for a registration request.
	 * </p>
	 * 
	 * @param requestBody the JSON string received in the HTTP request
	 * @return true if the request body contains the expected attributes, false
	 *         otherwise
	 * @throws JsonMappingException    if there is an error while mapping the
	 *                                 request body to a JsonNode
	 * @throws JsonProcessingException if there is an error while processing the
	 *                                 request body
	 * @version prototype
	 * @author Kosta Katergaris
	 */

	public boolean correctAttributes(String requestBody) throws JsonMappingException, JsonProcessingException {
	    // Assume requestBody is the JSON string received in the HTTP request
	    ObjectMapper mapper = new ObjectMapper();
	    JsonNode jsonNode = mapper.readTree(requestBody);
		if (jsonNode.has("Username")
        && jsonNode.has("Password")
        && jsonNode.has("First-Name")
        && jsonNode.has("Last-Name")
        && jsonNode.has("User-Type")) {
			
    String userType = jsonNode.get("User-Type").asText();
	System.out.println(userType);
    if (userType.equals("Employee") && !jsonNode.has("ManagerID")) {
		System.out.println("Incorrect attributes.");
        return false;
    }
	System.out.println("Coorect attributes.");
    return userType.equalsIgnoreCase("Employee") || userType.equalsIgnoreCase("Manager");
} else {
    System.out.println("Incorrect attributes.");
    return false;
}

		
	}


	/**
	 * <p>
	 * Generates a new RSA key pair with 512-bit key length, encodes the public and
	 * private keys as Base64 strings,
	 * and returns a map containing both key strings.
	 * </p>
	 * 
	 * @return a map containing the generated public and private key strings
	 * @throws NoSuchAlgorithmException if there is an error while initializing the
	 *                                  RSA key pair generator
	 * @version prototype
	 * @author Kosta Katergaris
	 */
	public Map<String, String> createKeys() throws NoSuchAlgorithmException {
		Map<String, String> keys = new HashMap<>();
		// Create a new instance of KeyPairGenerator for RSA
		KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
		// Initialize the key pair generator with a key size of 512 bits
		keyPairGen.initialize(512);

		// Generate the key pair
		KeyPair keyPair = keyPairGen.generateKeyPair();

		// Retrieve the public key from the key pair and encode it as a byte array
		PublicKey publicKey = keyPair.getPublic();
		byte[] bytePublic = publicKey.getEncoded();

		// Encode the public key as a Base64 string and add it to the map of keys with
		// the key name
		String publicKeyString = Base64.getEncoder().encodeToString(bytePublic);
		keys.put("public-key", publicKeyString);

		// Encode the private key as a Base64 string and add it to the map of keys with
		// the key name
		PrivateKey privateKey = keyPair.getPrivate();
		byte[] bytePrivate = privateKey.getEncoded();
		String privateKeyString = Base64.getEncoder().encodeToString(bytePrivate);
		keys.put("private-key", privateKeyString);

		// Return the map of keys
		return keys;

	}

	/**
	 * <p>
	 * Checks if a user with the provided username already exists in the database.
	 * </p>
	 * 
	 * @param requestBody the JSON string received in the HTTP request
	 * @return true if the username exists in the database, false otherwise
	 * @throws JsonMappingException    if there is an error while mapping the
	 *                                 request body to a JsonNode
	 * @throws JsonProcessingException if there is an error while processing the
	 *                                 request body
	 * @Author Arjun Ranjan
	 * @version prototype
	 */
	public boolean checkIfUserExists(String requestBody) throws JsonMappingException, JsonProcessingException{
	    ObjectMapper mapper = new ObjectMapper();
	    JsonNode jsonNode = mapper.readTree(requestBody);

	    // Retrieving values of each attribute
	    String username = jsonNode.get("Username").asText();

	    // check if username exists in database
	    try {
	        // initialize database connection
	        Connection conn = DriverManager.getConnection("jdbc:sqlite:mydatabase.db");

	        // create SQL statement
	        String sql = "SELECT COUNT(*) FROM Employee WHERE Username = ?";
	        PreparedStatement statement = conn.prepareStatement(sql);
	        statement.setString(1, username);

	        // execute query and get result
	        ResultSet rs = statement.executeQuery();

	        // get the count of the result set
	        rs.next();
	        int count = rs.getInt(1);

	        // close database connection and statement
	        rs.close();
	        statement.close();
	        conn.close();

	        // return false if user exists, true otherwise
	        System.out.println("check user " +(count ==0));
	        return (count == 0);
	    } catch (SQLException e) {
	        e.printStackTrace();
	        return false;
	    }
	}


    public String generateEmployeeID() {
         // Generate a new UUID
		 UUID uuid = UUID.randomUUID();
        
		 // Print the UUID
		 return uuid.toString();
    }

}
