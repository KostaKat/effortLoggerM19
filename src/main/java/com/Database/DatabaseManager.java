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
    public boolean isUserTokenValid(String token, String username, String userType) throws SQLException {
      // Query database for matching user record with matching token
      
      connect();
      String sql = "SELECT COUNT(*) FROM Employee WHERE Username = ? AND UserType = ? AND Token = ?";
      PreparedStatement statement = connection.prepareStatement(sql);
      statement.setString(1, username);
      statement.setString(2, userType);
      statement.setString(3, token);
      ResultSet resultSet = statement.executeQuery();
      int count = resultSet.getInt(1);
      statement.close();
      disconnect();
  
      return count > 0;
  }
  
  public boolean addLog(String username, String userType, String employeeID, String date,
                      String startTime, String endTime, String project, String effortCategory,
                      String effortDetail, String lifeCycleStep) throws SQLException {
   
    System.out.println("In add Log");
    
    connect();
    
    // Check that the employee exists in the Employee table
    String checkEmployeeSql = "SELECT COUNT(*) FROM Employee WHERE EmployeeID = ?";
    PreparedStatement checkEmployeeStatement = connection.prepareStatement(checkEmployeeSql);
    checkEmployeeStatement.setString(1, employeeID);
    ResultSet checkEmployeeResultSet = checkEmployeeStatement.executeQuery();
    int employeeCount = checkEmployeeResultSet.getInt(1);
    checkEmployeeStatement.close();
    checkEmployeeResultSet.close();
    
    if (employeeCount == 0) {
        disconnect();
        return false;
    }
    
    // Add the log to the Logs table
    String addLogSql = "INSERT INTO Logs (EmployeeID, Date, StartTime, EndTime, Project, EffortCategory, EffortDetail, LifeCycleStep) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    PreparedStatement addLogStatement = connection.prepareStatement(addLogSql);
    addLogStatement.setString(1, employeeID);
    addLogStatement.setString(2, date);
    addLogStatement.setString(3, startTime);
    addLogStatement.setString(4, endTime);
    addLogStatement.setString(5, project);
    addLogStatement.setString(6, effortCategory);
    addLogStatement.setString(7, effortDetail);
    addLogStatement.setString(8, lifeCycleStep);
    int rowsInserted = addLogStatement.executeUpdate();
    addLogStatement.close();
    
    disconnect();
    
    return rowsInserted > 0;
}
  public String getIDbyUsernameUserType(String username, String userType) throws SQLException {
    connect();
    String sql = "SELECT EmployeeID FROM Employee WHERE Username = ? AND UserType = ?";
    PreparedStatement statement = connection.prepareStatement(sql);
    statement.setString(1, username);
    statement.setString(2, userType);
    ResultSet resultSet = statement.executeQuery();
    String employeeID = resultSet.getString(1);
    statement.close();
    disconnect();
    return employeeID;
  }
  
}
class InvalidManagerException extends Exception {
    public InvalidManagerException(String message) {
        super(message);
    }
}

