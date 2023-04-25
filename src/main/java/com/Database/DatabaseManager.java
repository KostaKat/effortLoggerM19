package com.Database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

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
    } catch (Exception e) {
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
    String employeeID = null;
    try {
      String sql = "SELECT EmployeeID FROM Employee WHERE Username = ? AND UserType = ?";
      PreparedStatement statement = connection.prepareStatement(sql);
      statement.setString(1, username);
      statement.setString(2, userType);
      ResultSet resultSet = statement.executeQuery();
      employeeID = resultSet.getString(1);
      statement.close();
      disconnect();
    } catch (Exception e) {
      System.out.println(e.getMessage());
    }

    return employeeID;
  }

  public String getLogsEmployee(String employeeID) throws SQLException {
    // Create a JSON array to hold the logs data
    JSONArray logsArray = new JSONArray();
    try {

      connect();
      String sql = "SELECT * FROM Logs WHERE EmployeeID = ?";
      PreparedStatement statement = connection.prepareStatement(sql);
      statement.setString(1, employeeID);

      ResultSet rs = statement.executeQuery();

      // Loop through the result set and add each log to the JSON array
      while (rs.next()) {
        JSONObject logObject = new JSONObject();
        logObject.put("LogID", rs.getInt("LogID"));
        logObject.put("Date", rs.getString("Date"));
        logObject.put("StartTime", rs.getString("StartTime"));
        logObject.put("EndTime", rs.getString("EndTime"));
        logObject.put("Project", rs.getInt("Project"));
        logObject.put("EffortCategory", rs.getString("EffortCategory"));
        logObject.put("EffortDetail", rs.getString("EffortDetail"));
        logObject.put("LifeCycleStep", rs.getString("LifeCycleStep"));
        logsArray.put(logObject);
      }

      // Convert the JSON array to a string and send it back to the client

    } catch (Exception e) {
      System.out.println(e.getMessage());
    }

    return logsArray.toString();

  }

  public String getLogsManager(String managerID) throws SQLException {
    // Create a JSON array to hold the logs data
    JSONArray logsArray = new JSONArray();

    try {
      connect();

      // Retrieve the employee IDs of all team members under the given manager
      String teamMembersSql = "SELECT EmployeeID FROM Team WHERE ManagerID = ?";
      PreparedStatement teamMembersStatement = connection.prepareStatement(teamMembersSql);
      teamMembersStatement.setString(1, managerID);
      ResultSet teamMembersResultSet = teamMembersStatement.executeQuery();
      List<String> teamMemberIds = new ArrayList<>();

      while (teamMembersResultSet.next()) {
        teamMemberIds.add(teamMembersResultSet.getString("EmployeeID"));
      }

      // Include the manager's employee ID as well
      teamMemberIds.add(managerID);

      // Retrieve logs for each team member, including the manager
      String logsSql = "SELECT * FROM Logs WHERE EmployeeID = ?";

      for (String employeeID : teamMemberIds) {
        PreparedStatement logsStatement = connection.prepareStatement(logsSql);
        logsStatement.setString(1, employeeID);
        ResultSet logsResultSet = logsStatement.executeQuery();

        // Loop through the result set and add each log to the JSON array
        while (logsResultSet.next()) {
          JSONObject logObject = new JSONObject();
          logObject.put("LogID", logsResultSet.getInt("LogID"));
          logObject.put("EmployeeID", employeeID);
          logObject.put("Date", logsResultSet.getString("Date"));
          logObject.put("StartTime", logsResultSet.getString("StartTime"));
          logObject.put("EndTime", logsResultSet.getString("EndTime"));
          logObject.put("Project", logsResultSet.getInt("Project"));
          logObject.put("EffortCategory", logsResultSet.getString("EffortCategory"));
          logObject.put("EffortDetail", logsResultSet.getString("EffortDetail"));
          logObject.put("LifeCycleStep", logsResultSet.getString("LifeCycleStep"));
          logsArray.put(logObject);
        }

        logsStatement.close();
      }

      teamMembersStatement.close();
      teamMembersResultSet.close();
      disconnect();

    } catch (Exception e) {
      System.out.println(e.getMessage());
    }

    return logsArray.toString();
  }
}

class InvalidManagerException extends Exception {
  public InvalidManagerException(String message) {
    super(message);
  }
}
