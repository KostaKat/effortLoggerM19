package com.HTTPHandler.server;

import com.HTTPHandler.PasswordUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.*;

/**
 * 
 * <p>
 * A helper class for the LoginContextHandler that contains methods for
 * validating request attributes and checking user credentials.
 * </p>
 * 
 * @extends HandlerHelpers
 * @version prototype
 * @author Kosta Katergaris
 */
public class LoginHandlerHelper extends HandlerHelpers {
	/**
	 * <p>
	 * Determines whether the request body has the correct attributes for a login
	 * request.
	 * </p>
	 * 
	 * @param requestBody the JSON string received in the HTTP request body
	 * @return true if the request body has the required attributes, false otherwise
	 * @throws JsonMappingException    if there is an error mapping the JSON string
	 *                                 to a JsonNode
	 * @throws JsonProcessingException if there is an error processing the JSON
	 *                                 string
	 * @version prototype
	 * @author Kosta Katergaris
	 * 
	 */
	public boolean correctAttributes(String requestBody) throws JsonMappingException, JsonProcessingException {
		// Assume requestBody is the JSON string received in the HTTP request
		ObjectMapper mapper = new ObjectMapper();

		JsonNode jsonNode = mapper.readTree(requestBody);
		System.out.println(jsonNode.toString());
		if (jsonNode.has("Username")
				&& jsonNode.has("Password")) {

			return true;

		} else {

			return false;
		}
	}

	/**
	 * <p>
	 * Checks whether the username, password, and user type provided in the request
	 * body match an entry in the database.
	 * </p>
	 * 
	 * @param requestBody the JSON string received in the HTTP request body
	 * @return true if the provided credentials match an entry in the database,
	 *         false otherwise
	 * @throws JsonMappingException    if there is an error mapping the JSON string
	 *                                 to a JsonNode
	 * @throws JsonProcessingException if there is an error processing the JSON
	 *                                 string
	 * 
	 * @author Arjub
	 * @version prototype
	 */
	public boolean userMatches(String requestBody) throws JsonMappingException, JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		JsonNode jsonNode = mapper.readTree(requestBody);
		PasswordUtils passwordUtils = new PasswordUtils();
		// Retrieving values of each attribute
		String username = jsonNode.get("Username").asText();
		String salt = null;

		try {
			Connection conn = DriverManager.getConnection("jdbc:sqlite:mydatabase.db");
			PreparedStatement stmt = conn.prepareStatement("SELECT Salt FROM Employee WHERE Username = ?");
			stmt.setString(1, username);

			ResultSet rs = stmt.executeQuery();
			salt = rs.getString("Salt"); // retrieve the salt value from the result set
			rs.close();
			stmt.close();
			conn.close();

		} catch (SQLException e) {
			System.out.println("Exception caught");

		}
		if (salt == null) {
			return false;
		}

		String password = passwordUtils.hashPassword(jsonNode.get("Password").asText(), salt);
		// check if in the database
		try {
			Connection conn = DriverManager.getConnection("jdbc:sqlite:mydatabase.db");
			PreparedStatement stmt = conn
					.prepareStatement("SELECT COUNT(*) FROM Employee WHERE Username = ? AND Password = ?");
			stmt.setString(1, username);

			stmt.setString(2, password);
			ResultSet rs = stmt.executeQuery();
			rs.next();
			int count = rs.getInt(1);
			rs.close();
			stmt.close();
			conn.close();

			return (count > 0);
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
}
