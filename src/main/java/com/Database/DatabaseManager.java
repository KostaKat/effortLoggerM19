package com.Database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.json.Json;

import org.json.JSONArray;
import org.json.JSONObject;

import com.WebSocket.WebSocket;
import com.google.gson.JsonObject;

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
      String userType, String managerID, String salt)
      throws SQLException, InvalidManagerException {
    connect();
    int count = 0;
    String sql = "SELECT COUNT(*) FROM Employee WHERE EmployeeID = ? AND UserType = 'Manager'";
    try (PreparedStatement statement = connection.prepareStatement(sql)) {
      statement.setString(1, managerID);
      ResultSet result = statement.executeQuery();
      result.next();
      count = result.getInt(1);
      statement.close();
    }
    if (count > 0) {
      // manager exists
    } else {
      // manager does not exist
    }
    sql = "INSERT INTO Employee (EmployeeID, FirstName, LastName, Username, Password, UserType, ManagerID, Salt) VALUES (?, ?, ?, ?, ?, ?, ?,?)";
    try (PreparedStatement statement = connection.prepareStatement(sql)) {
      statement.setString(1, employeeID);
      statement.setString(2, firstName);
      statement.setString(3, lastName);
      statement.setString(4, username);
      statement.setString(5, password);
      statement.setString(6, userType);
      statement.setString(7, managerID);
      statement.setString(8, salt);
      int rowsInserted = statement.executeUpdate();
      if (rowsInserted > 0) {
        System.out.println("A new employee was inserted successfully!");
      } else {
        throw new InvalidManagerException("Invalid manager ID: " + managerID);
      }
      statement.close();

      // Add employee to manager's team
      if (managerID != null) {
        String teamSql = "INSERT INTO Team (EmployeeID, ManagerID) VALUES (?, ?)";
        PreparedStatement teamStatement = connection.prepareStatement(teamSql);
        teamStatement.setString(1, employeeID);
        teamStatement.setString(2, managerID);
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
      String username, String password, String userType, String salt) throws SQLException, InvalidManagerException {
    try {
      connect();
      String sql = "INSERT INTO Employee (EmployeeID, FirstName, LastName, Username, Password, UserType, Salt) VALUES (?, ?, ?, ?, ?, ?,?)";
      PreparedStatement statement = connection.prepareStatement(sql);
      statement.setString(1, employeeID);
      statement.setString(2, firstName);
      statement.setString(3, lastName);
      statement.setString(4, username);
      statement.setString(5, password);
      statement.setString(6, userType);
      statement.setString(7, salt);
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
    String addLogSql = "INSERT INTO Logs (LogID,EmployeeID, Date, StartTime, EndTime, Project, EffortCategory, EffortDetail, LifeCycleStep) VALUES (?, ?, ?, ?, ?, ?, ?, ?,?)";
    PreparedStatement addLogStatement = connection.prepareStatement(addLogSql);
    String logID = UUID.randomUUID().toString();
    addLogStatement.setString(1, logID);
    addLogStatement.setString(2, employeeID);
    addLogStatement.setString(3, date);
    addLogStatement.setString(4, startTime);
    addLogStatement.setString(5, endTime);
    addLogStatement.setString(6, project);
    addLogStatement.setString(7, effortCategory);
    addLogStatement.setString(8, effortDetail);
    addLogStatement.setString(9, lifeCycleStep);

    int rowsInserted = addLogStatement.executeUpdate();
    addLogStatement.close();

    disconnect();
    JSONObject logObject = new JSONObject();
    logObject.put("action", "addLog");
    logObject.put("logID", logID);
    logObject.put("date", date);
    logObject.put("startTime", startTime);
    logObject.put("endTime", endTime);
    logObject.put("project", project);
    logObject.put("effortCategory", effortCategory);
    logObject.put("effortDetail", effortDetail);
    logObject.put("lifeCycleStep", lifeCycleStep);

    WebSocket.sendUpdate(employeeID, logObject.toString());
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

  public String getManagerID(String employeeID) throws SQLException {
    connect();
    String managerID = null;
    try {
      String sql = "SELECT ManagerID FROM Team WHERE EmployeeID = ? ";
      PreparedStatement statement = connection.prepareStatement(sql);
      statement.setString(1, employeeID);
      ResultSet resultSet = statement.executeQuery();
      if (resultSet.next()) {
        managerID = resultSet.getString("ManagerID");
      }
      System.out.println("ManagerID: " + managerID);
      statement.close();
      disconnect();
    } catch (Exception e) {
      System.out.println(e.getMessage());
    }

    return managerID;
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
        logObject.put("logID", rs.getString("LogID"));
        logObject.put("date", rs.getString("Date"));
        logObject.put("startTime", rs.getString("StartTime"));
        logObject.put("endTime", rs.getString("EndTime"));
        logObject.put("project", rs.getString("Project"));
        logObject.put("effortCategory", rs.getString("EffortCategory"));
        logObject.put("effortDetail", rs.getString("EffortDetail"));
        logObject.put("lifeCycleStep", rs.getString("LifeCycleStep"));
        logsArray.put(logObject);
      }
      disconnect();
      // Convert the JSON array to a string and send it back to the client
      JSONObject message = new JSONObject();
      message.put("action", "getLogs");
      logsArray.put(message);
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
          logObject.put("logID", logsResultSet.getString("LogID"));
          logObject.put("date", logsResultSet.getString("Date"));
          logObject.put("startTime", logsResultSet.getString("StartTime"));
          logObject.put("endTime", logsResultSet.getString("EndTime"));
          logObject.put("project", logsResultSet.getString("Project"));
          logObject.put("effortCategory", logsResultSet.getString("EffortCategory"));
          logObject.put("effortDetail", logsResultSet.getString("EffortDetail"));
          logObject.put("lifeCycleStep", logsResultSet.getString("LifeCycleStep"));
          logsArray.put(logObject);
        }

        logsStatement.close();
      }

      teamMembersStatement.close();
      teamMembersResultSet.close();
      JSONObject message = new JSONObject();
      message.put("action", "getLogs");
      logsArray.put(message);
      System.out.println(logsArray.toString());
      disconnect();

    } catch (Exception e) {
      System.out.println(e.getMessage());
    }

    return logsArray.toString();
  }

  public boolean editLog(String logID, String date, String startTime, String endTime, String project,
      String effortCategory, String effortDetail, String lifeCycleStep, String userID, String userType)
      throws SQLException {

    connect();
    String tempID = userID;
    String checkLogSql = "SELECT COUNT(*), EmployeeID FROM Logs WHERE LogID = ? GROUP BY EmployeeID";
    PreparedStatement checkLogStatement = connection.prepareStatement(checkLogSql);
    checkLogStatement.setString(1, logID);
    ResultSet checkLogResultSet = checkLogStatement.executeQuery();
    int logCount = 0;
    String employeeID = null;
    if (checkLogResultSet.next()) {
      logCount = checkLogResultSet.getInt(1);
      employeeID = checkLogResultSet.getString(2);
    }
    checkLogStatement.close();
    checkLogResultSet.close();

    if (logCount == 0) {
      disconnect();
      return false;
    }
    System.out.println("LOG USER ID" + employeeID);
    if (userType.equals("Manager") && !employeeID.equals(userID)) {
      String checkManagerSql = "SELECT COUNT(*) FROM Team e JOIN Employee m ON e.ManagerID = m.EmployeeID WHERE e.EmployeeID = ? AND m.EmployeeID = ? AND m.UserType = 'Manager'";
      PreparedStatement checkManagerStatement = connection.prepareStatement(checkManagerSql);
      checkManagerStatement.setString(1, employeeID);
      checkManagerStatement.setString(2, userID);
      ResultSet checkManagerResultSet = checkManagerStatement.executeQuery();
      int managerCount = checkManagerResultSet.getInt(1);
      checkManagerStatement.close();
      checkManagerResultSet.close();

      if (managerCount == 0) {
        disconnect();
        return false;
      }
      userID = employeeID;

    }
    // Update the log in the Logs table
    String updateLogSql = "UPDATE Logs SET Date=?, StartTime=?, EndTime=?, Project=?, EffortCategory=?, EffortDetail=?, LifeCycleStep=? WHERE LogID=? AND EmployeeID=?";
    PreparedStatement updateLogStatement = connection.prepareStatement(updateLogSql);
    updateLogStatement.setString(1, date);
    updateLogStatement.setString(2, startTime);
    updateLogStatement.setString(3, endTime);
    updateLogStatement.setString(4, project);
    updateLogStatement.setString(5, effortCategory);
    updateLogStatement.setString(6, effortDetail);
    updateLogStatement.setString(7, lifeCycleStep);
    updateLogStatement.setString(8, logID);
    updateLogStatement.setString(9, userID);
    int rowsUpdated = updateLogStatement.executeUpdate();
    updateLogStatement.close();

    disconnect();
    JSONObject logObject = new JSONObject();
    logObject.put("action", "editLog");
    logObject.put("logID", logID);
    logObject.put("date", date);
    logObject.put("startTime", startTime);
    logObject.put("endTime", endTime);
    logObject.put("project", project);
    logObject.put("effortCategory", effortCategory);
    logObject.put("effortDetail", effortDetail);
    logObject.put("lifeCycleStep", lifeCycleStep);

    WebSocket.sendUpdate(tempID, logObject.toString());
    return rowsUpdated > 0;
  }

  public boolean deleteLog(String logID, String userID, String userType) throws SQLException {
    connect();
    String tempID = userID;
    String checkLogSql = "SELECT COUNT(*), EmployeeID FROM Logs WHERE LogID = ? GROUP BY EmployeeID";
    PreparedStatement checkLogStatement = connection.prepareStatement(checkLogSql);
    checkLogStatement.setString(1, logID);
    ResultSet checkLogResultSet = checkLogStatement.executeQuery();
    int logCount = 0;
    String employeeID = null;
    if (checkLogResultSet.next()) {
      logCount = checkLogResultSet.getInt(1);
      employeeID = checkLogResultSet.getString(2);
    }
    checkLogStatement.close();
    checkLogResultSet.close();

    if (logCount == 0) {
      disconnect();
      return false;
    }

    if (userType.equals("Manager") && !employeeID.equals(userID)) {
      String checkManagerSql = "SELECT COUNT(*) FROM Team e JOIN Employee m ON e.ManagerID = m.EmployeeID WHERE e.EmployeeID = ? AND m.EmployeeID = ? AND m.UserType = 'Manager'";
      PreparedStatement checkManagerStatement = connection.prepareStatement(checkManagerSql);
      checkManagerStatement.setString(1, employeeID);
      checkManagerStatement.setString(2, userID);
      ResultSet checkManagerResultSet = checkManagerStatement.executeQuery();
      int managerCount = checkManagerResultSet.getInt(1);
      checkManagerStatement.close();
      checkManagerResultSet.close();

      if (managerCount == 0) {
        disconnect();
        return false;
      }
      userID = employeeID;

    }
    // Delete the log from the Logs table
    String deleteLogSql = "DELETE FROM Logs WHERE LogID = ? AND EmployeeID = ?";
    PreparedStatement deleteLogStatement = connection.prepareStatement(deleteLogSql);
    deleteLogStatement.setString(2, userID);
    deleteLogStatement.setString(1, logID);

    int rowsDeleted = deleteLogStatement.executeUpdate();
    deleteLogStatement.close();

    disconnect();
    JSONObject logObject = new JSONObject();
    logObject.put("action", "deleteLog");
    logObject.put("logID", logID);
    WebSocket.sendUpdate(tempID, logObject.toString());
    return rowsDeleted > 0;
  }

  public boolean addDefect(String employeeID, String description, String name, String fixStatus,
      String stepWhenInjected, String stepWhenRemoved, String defectCategory) throws SQLException {
    System.out.println("In add Defect");

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

    // Add the defect to the Defects table
    String addDefectSql = "INSERT INTO Defects (DefectID, EmployeeID, Description, Name, FixStatus, StepWhenInjected, StepWhenRemoved, DefectCategory) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    PreparedStatement addDefectStatement = connection.prepareStatement(addDefectSql);
    String defectID = UUID.randomUUID().toString();
    addDefectStatement.setString(1, defectID);
    addDefectStatement.setString(2, employeeID);
    addDefectStatement.setString(3, description);
    addDefectStatement.setString(4, name);
    addDefectStatement.setString(5, fixStatus);
    addDefectStatement.setString(6, stepWhenInjected);
    addDefectStatement.setString(7, stepWhenRemoved);
    addDefectStatement.setString(8, defectCategory);

    int rowsInserted = addDefectStatement.executeUpdate();
    addDefectStatement.close();

    disconnect();
    JSONObject defectObject = new JSONObject();
    defectObject.put("action", "addDefect");
    defectObject.put("defectID", defectID);
    defectObject.put("description", description);
    defectObject.put("name", name);
    defectObject.put("fixStatus", fixStatus);
    defectObject.put("stepWhenInjected", stepWhenInjected);
    defectObject.put("stepWhenRemoved", stepWhenRemoved);
    defectObject.put("defectCategory", defectCategory);

    WebSocket.sendUpdate(employeeID, defectObject.toString());
    return rowsInserted > 0;
  }

  public boolean editDefect(String defectID, String userID, String userType, String description, String fixStatus)
      throws SQLException {
    connect();

    String tempID = userID;
    String checkDefectSql = "SELECT COUNT(*), EmployeeID FROM Defects WHERE DefectID = ? GROUP BY EmployeeID";
    PreparedStatement checkDefectStatement = connection.prepareStatement(checkDefectSql);
    checkDefectStatement.setString(1, defectID);
    ResultSet checkDefectResultSet = checkDefectStatement.executeQuery();
    int defectCount = 0;
    String employeeID = null;
    if (checkDefectResultSet.next()) {
      defectCount = checkDefectResultSet.getInt(1);
      employeeID = checkDefectResultSet.getString(2);
    }
    checkDefectStatement.close();
    checkDefectResultSet.close();
    System.out.println("DEFECT COUNT" + defectCount);
    if (defectCount == 0) {
      disconnect();
      return false;
    }
    System.out.println("DEFECT USER ID" + employeeID);

    // Update the defect in the Defects table
    String updateDefectSql = "UPDATE Defects SET Description=?, FixStatus=? WHERE DefectID=? AND EmployeeID=?";
    PreparedStatement updateDefectStatement = connection.prepareStatement(updateDefectSql);
    updateDefectStatement.setString(1, description);
    updateDefectStatement.setString(2, fixStatus);
    updateDefectStatement.setString(3, defectID);
    updateDefectStatement.setString(4, userID);
    int rowsUpdated = updateDefectStatement.executeUpdate();
    updateDefectStatement.close();

    disconnect();
    JSONObject defectObject = new JSONObject();
    defectObject.put("action", "editDefect");
    defectObject.put("defectID", defectID);
    defectObject.put("description", description);
    defectObject.put("fixStatus", fixStatus);

    WebSocket.sendUpdate(tempID, defectObject.toString());
    return rowsUpdated > 0;
  }

  public boolean deleteDefect(String defectID, String userID, String userType) {
    try {
      connect();
      String tempID = userID;
      String checkDefectSql = "SELECT COUNT(*), EmployeeID FROM Defects WHERE DefectID = ? GROUP BY EmployeeID";
      PreparedStatement checkDefectStatement = connection.prepareStatement(checkDefectSql);
      checkDefectStatement.setString(1, defectID);
      ResultSet checkDefectResultSet = checkDefectStatement.executeQuery();
      int defectCount = 0;
      String employeeID = null;
      if (checkDefectResultSet.next()) {
        defectCount = checkDefectResultSet.getInt(1);
        employeeID = checkDefectResultSet.getString(2);
      }
      checkDefectStatement.close();
      checkDefectResultSet.close();
      System.out.println("DEFECT COUNT" + defectCount);
      if (defectCount == 0) {
        disconnect();
        return false;
      }

      if (userType.equals("Manager") && !employeeID.equals(userID)) {
        String checkManagerSql = "SELECT COUNT(*) FROM Team e JOIN Employee m ON e.ManagerID = m.EmployeeID WHERE e.EmployeeID = ? AND m.EmployeeID = ? AND m.UserType = 'Manager'";
        PreparedStatement checkManagerStatement = connection.prepareStatement(checkManagerSql);
        checkManagerStatement.setString(1, employeeID);
        checkManagerStatement.setString(2, userID);
        ResultSet checkManagerResultSet = checkManagerStatement.executeQuery();
        int managerCount = checkManagerResultSet.getInt(1);
        checkManagerStatement.close();
        checkManagerResultSet.close();

        if (managerCount == 0) {
          disconnect();
          return false;
        }
        userID = employeeID;

      }
      // Delete the defect from the Defects table
      String deleteDefectSql = "DELETE FROM Defects WHERE DefectID = ? AND EmployeeID = ?";
      PreparedStatement deleteDefectStatement = connection.prepareStatement(deleteDefectSql);
      deleteDefectStatement.setString(1, defectID);
      deleteDefectStatement.setString(2, userID);

      int rowsDeleted = deleteDefectStatement.executeUpdate();
      deleteDefectStatement.close();
      System.out.println("ROWS DELETED" + rowsDeleted);
      disconnect();
      JSONObject defectObject = new JSONObject();
      defectObject.put("action", "deleteDefect");
      defectObject.put("defectID", defectID);
      WebSocket.sendUpdate(tempID, defectObject.toString());
      return rowsDeleted > 0;
    } catch (Exception e) {
      System.out.println(e.getMessage());
      return false;
    }
  }

  public String getDefectsManager(String managerID) throws SQLException {
    // Create a JSON array to hold the defects data
    JSONArray defectsArray = new JSONArray();

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

      // Retrieve defects for each team member, including the manager
      String defectsSql = "SELECT * FROM Defects WHERE EmployeeID = ?";

      for (String employeeID : teamMemberIds) {
        PreparedStatement defectsStatement = connection.prepareStatement(defectsSql);
        defectsStatement.setString(1, employeeID);
        ResultSet defectsResultSet = defectsStatement.executeQuery();

        // Loop through the result set and add each defect to the JSON array
        while (defectsResultSet.next()) {
          JSONObject defectObject = new JSONObject();
          defectObject.put("defectID", defectsResultSet.getString("DefectID"));
          defectObject.put("description", defectsResultSet.getString("Description"));
          defectObject.put("name", defectsResultSet.getString("Name"));
          defectObject.put("fixStatus", defectsResultSet.getString("FixStatus"));
          defectObject.put("stepWhenInjected", defectsResultSet.getString("StepWhenInjected"));
          defectObject.put("stepWhenRemoved", defectsResultSet.getString("StepWhenRemoved"));
          defectObject.put("defectCategory", defectsResultSet.getString("DefectCategory"));
          defectsArray.put(defectObject);
        }

        defectsStatement.close();
      }

      teamMembersStatement.close();
      teamMembersResultSet.close();
      disconnect();
      JSONObject message = new JSONObject();
      message.put("action", "getDefects");
      defectsArray.put(message);
    } catch (Exception e) {
      System.out.println(e.getMessage());
    }

    return defectsArray.toString();
  }

  public String getDefectsEmployee(String employeeID) throws SQLException {
    // Create a JSON array to hold the defects data
    JSONArray defectsArray = new JSONArray();
    try {

      connect();
      String sql = "SELECT * FROM Defects WHERE EmployeeID = ?";
      PreparedStatement statement = connection.prepareStatement(sql);
      statement.setString(1, employeeID);

      ResultSet rs = statement.executeQuery();

      // Loop through the result set and add each defect to the JSON array
      while (rs.next()) {
        JSONObject defectObject = new JSONObject();
        defectObject.put("defectID", rs.getString("DefectID"));
        defectObject.put("description", rs.getString("Description"));
        defectObject.put("name", rs.getString("Name"));
        defectObject.put("fixStatus", rs.getString("FixStatus"));
        defectObject.put("stepWhenInjected", rs.getString("StepWhenInjected"));
        defectObject.put("stepWhenRemoved", rs.getString("StepWhenRemoved"));
        defectObject.put("defectCategory", rs.getString("DefectCategory"));
        defectsArray.put(defectObject);
      }
      JSONObject message = new JSONObject();
      message.put("action", "getDefects");
      defectsArray.put(message);
      // Convert the JSON array to a string and send it back to the client

    } catch (Exception e) {
      System.out.println(e.getMessage());
    }

    return defectsArray.toString();

  }

}

class InvalidManagerException extends Exception {
  public InvalidManagerException(String message) {
    super(message);
  }
}
