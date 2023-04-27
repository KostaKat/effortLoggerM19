/*
Author : Yihui Wu
 */
package com.Frontend.Controllers;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import com.Frontend.Log;
import com.Frontend.Main;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.json.JSONObject;

public class CreateLogController {

    private Log taskLog;
    private ArrayList<Log> logs = new ArrayList<>();
    private final Alert alert = new Alert(AlertType.WARNING);

    private final String authToken;
    private String startTime;
    private String endTime;
    private String date;

    int startFlag = 0;

    @FXML
    private ChoiceBox<String> project, lifeCycleStep, effortCategory, effortDetail;
    @FXML
    private TextArea logDescription;
    @FXML
    private Button start, stop, interruption;
    @FXML
    private MenuItem viewLog, editLog, logOut;
    @FXML
    private Label warnL, clock, timeStart;

    public CreateLogController(ArrayList<Log> logArrayList, String authToken) {
        this.logs = logArrayList;
        this.authToken = authToken;
    }

    @FXML
    private void initialize() {
        /*
         * TODO retrieve all the logs, What I need is initialize the stored logs to the
         * arraylist name logs
         */
        start.setOnAction(event -> {
            if (lifeCycleStep.getValue() == null || effortDetail.getValue() == null
                    || logDescription.getText() == null) {
                alert.setTitle("Warning Dialog");
                alert.setContentText("Please fill all the box in order to start!");
                alert.show();
                warnL.setText("Please fill all the box in order to start!");
                warnL.setDisable(false);
            } else {
                // get the current date and time
                LocalDateTime currentDateTime = LocalDateTime.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
                startTime = currentDateTime.format(formatter);
                date = currentDateTime.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));

                // set the time
                clock.setText("TIME START ON:");
                timeStart.setText(startTime);
                taskLog = new Log();
                taskLog.setStartTime(startTime);
                taskLog.setDate(date);

                start.setDisable(true);
                warnL.setDisable(true);
                warnL.setText(null);
                startFlag = 1;
            }
        });

        stop.setOnAction(event -> {
            if (startFlag != 1) {
                alert.setTitle("Warning Dialog");
                alert.setContentText("Please press start first!");
                alert.show();
                warnL.setText("Please press start first!");
                warnL.setDisable(false);
            } else {
                clock.setText("CLOCK IS STOPPED");
                timeStart.setText(null);
                LocalDateTime currentDateTime = LocalDateTime.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
                endTime = currentDateTime.format(formatter);
                taskLog.setEndTime(endTime);
                taskLog.setProject(project.getValue());
                taskLog.setLifeCycleStep(lifeCycleStep.getValue());
                taskLog.setEffortCategory(effortCategory.getValue());
                taskLog.setEffortDetail(effortDetail.getValue());
                taskLog.setLogDescription(logDescription.getText());
                logs.add(taskLog);
                for (int i = 0; i < logs.size(); i++) {
                    Log temp = logs.get(i);
                }
                /*
                 * TODO This is where the add log feature happens to the database
                 * TODO all logs should be saved into a arraylist of log named logs
                 */
                try {
                    addDatabase();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                start.setDisable(false);
                startFlag = 0;
            }
        });

        logOut.setOnAction(event -> {
            if (startFlag == 1) {
                alert.setTitle("Warning Dialog");
                alert.setContentText("Please stop the Log first to log out!!!");
                alert.show();
            } else {
                try {
                    Main.setRoot("login");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        interruption.setOnAction(event -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/FXML/" + "interruption.fxml"));
                InterruptionController temp = new InterruptionController();
                loader.setController(temp);
                Parent root = loader.load();

                Stage popupStage = new Stage();
                popupStage.initModality(Modality.APPLICATION_MODAL);
                popupStage.setScene(new Scene(root));
                popupStage.setTitle("Pop-up Page");
                popupStage.showAndWait();
            } catch (IOException e) {

            }
        });

        editLog.setOnAction(event -> {
            if (startFlag == 1) {
                alert.setTitle("Warning Dialog");
                alert.setContentText("Please stop the Log to process to other page!!!");
                alert.show();
            } else {
                try {
                    Main.setRoot("EditLog", logs, authToken);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        viewLog.setOnAction(event -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/FXML/" + "ViewLog.fxml"));
                ViewLogController temp = new ViewLogController(logs, authToken);
                loader.setController(temp);
                Parent root = loader.load();

                Stage popupStage = new Stage();
                popupStage.initModality(Modality.APPLICATION_MODAL);
                popupStage.setScene(new Scene(root));
                popupStage.setTitle("Pop-up Page");
                popupStage.showAndWait();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        // This block of code is used to initilize the choice box property.
        // The choice box value may be dependent on other to change.
        logDescription.setWrapText(true);
        project.getItems().addAll("Business Project", "Development Project");
        project.setValue("Business Project");
        lifeCycleStep.getItems().addAll("Planning", "Information Gathering", "Information Understanding", "Verifying",
                "Outlining", "Drafting", "Finalizing", "Team Meeting", "Coach Meeting", "Stakeholder Meeting");
        effortCategory.getItems().addAll("Plans", "Deliverables", "Interruptions", "Defects", "Others");
        effortCategory.setValue("Plans");
        effortDetail.getItems().addAll("Project Plan", "Risk Management Plan", "Conceptual Design Plan",
                "Detailed Design Plan", "Implementation Plan");
        project.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.equals("Business Project")) {
                lifeCycleStep.getItems().clear();
                lifeCycleStep.setValue(null);
                lifeCycleStep.getItems().addAll("Planning", "Information Gathering", "Information Understanding",
                        "Verifying", "Outlining", "Drafting", "Finalizing", "Team Meeting", "Coach Meeting",
                        "Stakeholder Meeting");
            } else if (newValue.equals("Development Project")) {
                lifeCycleStep.getItems().clear();
                lifeCycleStep.setValue(null);
                lifeCycleStep.getItems().addAll("Problem Understanding", "Conceptual Design Plan", "Requirements",
                        "Conceptual Design",
                        "Conceptual Design Review", "Detailed Design Plan", "Detailed Design/Prototype",
                        "Detailed Design Review", "Implementation Plan", "Test Case Generation",
                        "Solution Specification", "Solution Review", "Solution Implementation", "Unit/System Test",
                        "Reflection", "Repository Update");
            }
        });
        effortCategory.valueProperty().addListener((observable, oldValue, newValue) -> {
            switch (newValue) {
                case "Plans":
                    effortDetail.getItems().clear();
                    effortDetail.setValue(null);
                    effortDetail.getItems().addAll("Project Plan", "Risk Management Plan", "Conceptual Design Plan",
                            "Detailed Design Plan", "Implementation Plan");
                    effortDetail.setValue("Project Plan");
                    break;
                case "Deliverables":
                    effortDetail.getItems().clear();
                    effortDetail.setValue(null);
                    effortDetail.getItems().addAll("Conceptual Design", "Detailed Design", "Test Cases", "Solution",
                            "Reflection", "Outline", "Draft", "Report", "User Defined", "Other");
                    effortDetail.setValue("Conceptual Design");
                    break;
                case "Interruptions":
                    effortDetail.getItems().clear();
                    effortDetail.setValue(null);
                    effortDetail.getItems().addAll("Break", "Phone", "Teammate", "Visitor", "Other");
                    effortDetail.setValue("Break");
                    break;
                case "Defects":
                    effortDetail.getItems().clear();
                    effortDetail.setValue(null);
                    effortDetail.getItems().add("- no defect selected");
                    effortDetail.setValue("- no defect selected");
                    break;
                case "Others":
                    effortDetail.getItems().clear();
                    effortDetail.setValue(null);
                    effortDetail.getItems().add("- specific in description");
                    effortDetail.setValue("- specific in description");
                    break;
            }
        });
    }

    void addDatabase() throws IOException {
        String url = "http://localhost:8080/addLog";

        // Set the JSON data for the log request
        JSONObject logData = new JSONObject();
        logData.put("Token", authToken);
        logData.put("Date", date);
        logData.put("StartTime", startTime);
        logData.put("EndTime", endTime);
        logData.put("Project", project.getValue());
        logData.put("EffortCategory", effortCategory.getValue());
        logData.put("EffortDetail", effortDetail.getValue());
        logData.put("LifeCycleStep", lifeCycleStep.getValue());

        String logDataString = logData.toString();

        HttpURLConnection logCon = (HttpURLConnection) new URL(url).openConnection();
        logCon.setRequestMethod("POST");

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
}
