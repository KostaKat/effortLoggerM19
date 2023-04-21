package com.Database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {

    private Connection connection;
    
    public void createDatabase() {
    	
         try {
             Connection conn = DriverManager.getConnection("jdbc:sqlite:mydatabase.db");
             System.out.println("Database created successfully");
         } catch (SQLException e) {
             System.out.println(e.getMessage());
         }	
    }

    public void connect(String databaseUrl) throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite:" + databaseUrl);
        System.out.println("Connection to SQLite has been established.");
    }

    public void disconnect() throws SQLException {
        if (connection != null) {
            connection.close();
            System.out.println("Connection to SQLite has been closed.");
        }
    }

    public ResultSet executeQuery(String query) throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(query);
        return resultSet;
    }

    public int executeUpdate(String query) throws SQLException {
        Statement statement = connection.createStatement();
        int result = statement.executeUpdate(query);
        return result;
    }

}
