package com.Database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {

  private final String dbUrl;

  private Connection connection;

  public DatabaseManager() {
    this.dbUrl = "jdbc:sqlite:mydatabase.db";
   
  }

  public void connect() throws SQLException {
    connection = DriverManager.getConnection(dbUrl);
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

  public void insertNewEmployee(String employeeID, String firstName, String lastName, String username, String password, 
                                String userType, String managerID, String managerEmployeeID) throws SQLException, InvalidManagerException {
    connect();
    String sql = "INSERT INTO Employee (EmployeeID, FirstName, LastName, Username, Password, UserType, ManagerID) VALUES (?, ?, ?, ?, ?, ?, ?)";
    try (PreparedStatement statement = connection.prepareStatement(sql)) {
        statement.setString(1, employeeID);
        statement.setString(2, firstName);
        statement.setString(3, lastName);
        statement.setString(4, username);
        statement.setString(5, password);
        statement.setString(6, userType);
        statement.setString(7, managerID);
        int rowsInserted = statement.executeUpdate();
        if (rowsInserted > 0) {
          System.out.println("A new employee was inserted successfully!");
        } else {
          throw new InvalidManagerException("Invalid manager ID: " + managerID);
        }
        statement.close();
    
    // Add employee to manager's team
    if (managerEmployeeID != null) {
      String teamSql = "INSERT INTO Team (EmployeeID, ManagerID) VALUES (?, ?)";
      PreparedStatement teamStatement = connection.prepareStatement(teamSql);
      teamStatement.setString(1, employeeID);
      teamStatement.setString(2, managerEmployeeID);
      int teamRowsInserted = teamStatement.executeUpdate();
      if (teamRowsInserted > 0) {
        System.out.println("Employee added to manager's team successfully!");
      }
      teamStatement.close();
    }

    disconnect();
        }catch(Exception e ){
            System.out.println(e.getMessage());
        }

    }
    public void insertNewManager(String employeeID, String firstName, String lastName, 
        String username, String password, String userType) throws SQLException, InvalidManagerException {
        try { 
            connect();
            String sql = "INSERT INTO Employee (EmployeeID, FirstName, LastName, Username, Password, UserType) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, employeeID);
            statement.setString(2, firstName);
            statement.setString(3, lastName);
            statement.setString(4, username);
            statement.setString(5, password);
            statement.setString(6, userType);
            int rowsInserted = statement.executeUpdate();
            if (rowsInserted > 0) {
              System.out.println("A new manager was inserted successfully!");
            }
            statement.close();
            disconnect();
            
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
       
    }
}
class InvalidManagerException extends Exception {
    public InvalidManagerException(String message) {
        super(message);
    }
}

