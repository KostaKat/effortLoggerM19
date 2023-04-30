package com.Frontend.Controllers;

import com.Frontend.Log;
import com.Frontend.Main;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class EditLogManagerController {
    String authToken;
    ObservableList<Log> logs;
    private Log selectedLog;
    private int index;
    private String logID;
    private String startTime;
    private String endTime;
    private String date;

    @FXML
    private ChoiceBox<String> project_e, lifeCycleStep_e,
            effortCategory_e, effortDetail_e, select;
    @FXML
    private TextArea logDescription_e;
    @FXML
    private Button update, delete;
    @FXML
    private MenuItem createLog, viewLog;

    public EditLogManagerController(ObservableList<Log> logs, String authToken) {
        this.logs = logs;
        this.authToken = authToken;
    }

    @FXML
    private void initialize() {
        System.out.println(authToken);
        // Initialize the choice box
        for (Log log : logs) {
            select.getItems().add(log.toString());
        }

        // Add a listener to the logs list to update the select choice box
        logs.addListener((ListChangeListener<Log>) c -> {
            select.getItems().clear();
            for (Log log : logs) {
                select.getItems().add(log.toString());
            }
        });

        select.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                index = select.getSelectionModel().getSelectedIndex();
                selectedLog = logs.get(index);
                logID = selectedLog.getLogID();
                startTime = selectedLog.getStartTime();
                endTime = selectedLog.getEndTime();
                date = selectedLog.getDate();
                project_e.setValue(selectedLog.getProject());
                lifeCycleStep_e.setValue(selectedLog.getLifeCycleStep());
                effortCategory_e.setValue(selectedLog.getEffortCategory());
                effortDetail_e.setValue(selectedLog.getEffortDetail());
                logDescription_e.setText(selectedLog.getLogDescription());
            }
        });

        // This is where the delete button
        delete.setOnAction(event -> {
            if (!select.getSelectionModel().isEmpty()) {
                // confirmation page pop up
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                        "Are you sure you want to delete the select Log? THIS ACTION CANNOT BE RESTORE!",
                        ButtonType.YES, ButtonType.NO);
                alert.showAndWait();
                if (alert.getResult() == ButtonType.YES) {
                    try {
                        deleteDatabase();
                        select.getSelectionModel().clearSelection();
                        lifeCycleStep_e.setValue(null);
                        project_e.setValue(null);
                        effortCategory_e.setValue(null);
                        effortDetail_e.setValue(null);
                        logDescription_e.setText(null);

                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                }
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setContentText("You have to select on Log first!");
                alert.showAndWait();
            }
        });

        update.setOnAction(event -> {
            System.out.println("setOnAction update");
            if (!project_e.getSelectionModel().isEmpty() && !lifeCycleStep_e.getSelectionModel().isEmpty()
                    && !effortDetail_e.getSelectionModel().isEmpty() && !effortCategory_e.getSelectionModel().isEmpty()
                    && !select.getSelectionModel().isEmpty()) {
                System.out.println("update initilial");
                selectedLog.setProject(project_e.getValue());
                selectedLog.setLogDescription(logDescription_e.getText());
                selectedLog.setLifeCycleStep(lifeCycleStep_e.getValue());
                selectedLog.setEffortDetail(effortDetail_e.getValue());
                selectedLog.setEffortCategory(effortCategory_e.getValue());

                try {
                    System.out.println("try update");
                    updateDatabase();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setContentText("Edit Successful!");
                select.getSelectionModel().clearSelection();
                select.getItems().remove(selectedLog.toString());
                lifeCycleStep_e.setValue(null);
                project_e.setValue(null);
                effortCategory_e.setValue(null);
                effortDetail_e.setValue(null);
                logDescription_e.setText(null);
                alert.showAndWait();
                Stage stage = (Stage) update.getScene().getWindow();
                stage.close();
            }

        });

        // initialize the choiceBox property when selected
        project_e.getItems().addAll("Business Project", "Development Project");
        effortCategory_e.getItems().addAll("Plans", "Deliverables", "Interruptions", "Defects", "Others");
        project_e.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                if (newValue.equals("Business Project")) {
                    lifeCycleStep_e.getItems().clear();
                    lifeCycleStep_e.setValue(null);
                    lifeCycleStep_e.getItems().addAll("Planning", "Information Gathering", "Information Understanding",
                            "Verifying", "Outlining", "Drafting", "Finalizing", "Team Meeting", "Coach Meeting",
                            "Stakeholder Meeting");
                } else if (newValue.equals("Development Project")) {
                    lifeCycleStep_e.getItems().clear();
                    lifeCycleStep_e.setValue(null);
                    lifeCycleStep_e.getItems().addAll("Problem Understanding", "Conceptual Design Plan", "Requirements",
                            "Conceptual Design",
                            "Conceptual Design Review", "Detailed Design Plan", "Detailed Design/Prototype",
                            "Detailed Design Review", "Implementation Plan", "Test Case Generation",
                            "Solution Specification", "Solution Review", "Solution Implementation", "Unit/System Test",
                            "Reflection", "Repository Update");
                }
            }
        });

        effortCategory_e.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                switch (newValue) {
                    case "Plans":
                        effortDetail_e.getItems().clear();
                        effortDetail_e.setValue(null);
                        effortDetail_e.getItems().addAll("Project Plan", "Risk Management Plan",
                                "Conceptual Design Plan", "Detailed Design Plan", "Implementation Plan");
                        effortDetail_e.setValue("Project Plan");
                        break;
                    case "Deliverables":
                        effortDetail_e.getItems().clear();
                        effortDetail_e.setValue(null);
                        effortDetail_e.getItems().addAll("Conceptual Design", "Detailed Design", "Test Cases",
                                "Solution", "Reflection", "Outline", "Draft", "Report", "User Defined", "Other");
                        effortDetail_e.setValue("Conceptual Design");
                        break;
                    case "Interruptions":
                        effortDetail_e.getItems().clear();
                        effortDetail_e.setValue(null);
                        effortDetail_e.getItems().addAll("Break", "Phone", "Teammate", "Visitor", "Other");
                        effortDetail_e.setValue("Break");
                        break;
                    case "Defects":
                        effortDetail_e.getItems().clear();
                        effortDetail_e.setValue(null);
                        effortDetail_e.getItems().add("- no defect selected");
                        effortDetail_e.setValue("- no defect selected");
                        break;
                    case "Others":
                        effortDetail_e.getItems().clear();
                        effortDetail_e.setValue(null);
                        effortDetail_e.getItems().add("- specific in description");
                        effortDetail_e.setValue("- specific in description");
                        break;
                }
            }
        });

    }

    void deleteDatabase() throws IOException {
        String url = "http://localhost:8080/deleteLog";
        // Set the JSON data for the log request
        JSONObject logData = new JSONObject();
        logData.put("Token", authToken);
        logData.put("LogID", selectedLog.getLogID());

        String logDataString = logData.toString();

        HttpURLConnection logCon = (HttpURLConnection) new URL(url).openConnection();
        logCon.setRequestMethod("DELETE");

        logCon.setRequestProperty("Content-Type", "application/json");

        logCon.setDoOutput(true);

        try (OutputStream os = logCon.getOutputStream()) {
            byte[] logDataBytes = logDataString.getBytes(StandardCharsets.UTF_8);
            os.write(logDataBytes, 0, logDataBytes.length);
        }

        int logResponseCode = logCon.getResponseCode();
        String logResponseMessage = logCon.getResponseMessage();

        System.out.println("Log request code: " + logResponseCode);
        System.out.println("Log request message: " + logResponseMessage);
    }

    void updateDatabase() throws IOException {
        String logUrlEdit = "http://localhost:8080/editLog";
        System.out.println("In update update");

        // Set the JSON data for the log request
        JSONObject logDataEdit = new JSONObject();

        logDataEdit.put("Token", authToken);
        logDataEdit.put("LogID", logID);
        logDataEdit.put("Date", date);
        logDataEdit.put("StartTime", startTime);
        logDataEdit.put("EndTime", endTime);
        logDataEdit.put("Project", project_e.getValue());
        logDataEdit.put("EffortCategory", effortCategory_e.getValue());
        logDataEdit.put("EffortDetail", effortDetail_e.getValue());
        logDataEdit.put("LifeCycleStep", lifeCycleStep_e.getValue());

        // Convert the JSON object to a string
        String logDataEditString = logDataEdit.toString();
        System.out.println(logDataEditString);
        // Create a new HTTP connection for the log request
        HttpURLConnection logConEdit = (HttpURLConnection) new URL(logUrlEdit).openConnection();
        logConEdit.setRequestMethod("PUT");

        // Set the content type of the request to application/json
        logConEdit.setRequestProperty("Content-Type", "application/json");

        // Enable output on the connection to allow sending the request body
        logConEdit.setDoOutput(true);

        // Write the log request body to the connection's output stream
        try (OutputStream os = logConEdit.getOutputStream()) {
            byte[] logDataBytes = logDataEditString.getBytes(StandardCharsets.UTF_8);
            os.write(logDataBytes, 0, logDataBytes.length);
        }

        // Read the response from the log request's input stream
        int logResponseCodeEdit = logConEdit.getResponseCode();
        String logResponseMessageEdit = logConEdit.getResponseMessage() + "edit";

        // Print the response from the log request
        System.out.println("Log request code: " + logResponseCodeEdit);
        System.out.println("Log request message: " + logResponseMessageEdit);
    }

}
