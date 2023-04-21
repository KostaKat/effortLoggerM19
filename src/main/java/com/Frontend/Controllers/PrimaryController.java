/*
Author : Yihui Wu
 */
package com.Frontend.Controllers;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import com.Frontend.Log;
import com.Frontend.Main;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class PrimaryController {

    private Log taskLog;
    private ArrayList<Log> logs = new ArrayList<>();
    int startFlag = 0;


    @FXML private ChoiceBox<String> project, lifeCycleStep, effortCategory, effortDetail;
    @FXML private TextArea logDescription;
    @FXML private Button start, stop, viewLog;
    @FXML private Label warnL, clock, timeStart;
    Alert alert = new Alert(AlertType.WARNING);

    public PrimaryController(ArrayList<Log> logArrayList){
        this.logs = logArrayList;
    }

    @FXML
    private void initialize(){
        logDescription.setWrapText(true);
        project.getItems().addAll("Business Project", "Development Project");
        project.setValue("Business Project");
        lifeCycleStep.getItems().addAll("Planning","Information Gathering","Information Understanding","Verifying","Outlining","Drafting","Finalizing","Team Meeting","Coach Meeting","Stakeholder Meeting");
        effortCategory.getItems().addAll("Plans","Deliverables","Interruptions","Defects","Others");
        effortCategory.setValue("Plans");
        effortDetail.getItems().addAll("Project Plan","Risk Management Plan","Conceptual Design Plan","Detailed Design Plan","Implementation Plan");

        project.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.equals("Business Project")) {
                lifeCycleStep.getItems().clear();
                lifeCycleStep.setValue(null);
                lifeCycleStep.getItems().addAll("Planning","Information Gathering","Information Understanding","Verifying","Outlining","Drafting","Finalizing","Team Meeting","Coach Meeting","Stakeholder Meeting");
            } else if (newValue.equals("Development Project")) {
                lifeCycleStep.getItems().clear();
                lifeCycleStep.setValue(null);
                lifeCycleStep.getItems().addAll("Problem Understanding","Conceptual Design Plan","Requirements","Conceptual Design",
                        "Conceptual Design Review","Detailed Design Plan","Detailed Design/Prototype","Detailed Design Review","Implementation Plan","Test Case Generation",
                        "Solution Specification","Solution Review","Solution Implementation","Unit/System Test","Reflection","Repository Update");
            }
        });

        effortCategory.valueProperty().addListener((observable, oldValue, newValue) -> {
            switch (newValue) {
                case "Plans":
                    effortDetail.getItems().clear();
                    effortDetail.setValue(null);
                    effortDetail.getItems().addAll("Project Plan","Risk Management Plan","Conceptual Design Plan","Detailed Design Plan","Implementation Plan");
                    effortDetail.setValue("Project Plan");
                    break;
                case "Deliverables":
                    effortDetail.getItems().clear();
                    effortDetail.setValue(null);
                    effortDetail.getItems().addAll("Conceptual Design","Detailed Design","Test Cases","Solution","Reflection","Outline","Draft","Report","User Defined","Other");
                    effortDetail.setValue("Conceptual Design");
                    break;
                case "Interruptions":
                    effortDetail.getItems().clear();
                    effortDetail.setValue(null);
                    effortDetail.getItems().addAll("Break","Phone","Teammate","Visitor","Other");
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

    @FXML
    void lifeCycleChange(){
        System.out.println("111");
    }

    @FXML
    void start(){
        if(lifeCycleStep.getValue() == null || effortDetail.getValue() == null || logDescription.getText() == null){
            alert.setTitle("Warning Dialog");
            alert.setContentText("Please fill all the box in order to start!");
            alert.show();
            warnL.setText("Please fill all the box in order to start!");
            warnL.setDisable(false);
        }else{
            LocalDateTime currentDateTime = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String formattedDateTime = currentDateTime.format(formatter);
            clock.setText("TIME START ON:");
            timeStart.setText(formattedDateTime);

            taskLog = new Log();
            taskLog.setStartTime(formattedDateTime);
            taskLog.setFirstName(SignUpController.firstT);
            taskLog.setLastName(SignUpController.lastT);

            taskLog.setEmployee(1);
            start.setDisable(true);
            warnL.setDisable(true);
            warnL.setText(null);
            startFlag = 1;
        }
    }

    @FXML
    void end(){
        if(startFlag != 1){
            alert.setTitle("Warning Dialog");
            alert.setContentText("Please press start first!");
            alert.show();
            warnL.setText("Please press start first!");
            warnL.setDisable(false);
        }else{
            clock.setText("CLOCK IS STOPPED");
            timeStart.setText(null);
            LocalDateTime currentDateTime = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String formattedDateTime = currentDateTime.format(formatter);
            taskLog.setEndTime(formattedDateTime);
            taskLog.setProject(project.getValue());
            taskLog.setLifeCycleStep(lifeCycleStep.getValue());
            taskLog.setEffortCategory(effortCategory.getValue());
            taskLog.setEffortDetail(effortDetail.getValue());
            taskLog.setLogDescription(logDescription.getText());
            logs.add(taskLog);
            for (int i = 0; i < logs.size(); i++) {
                Log temp = logs.get(i);
            }
            start.setDisable(false);
            startFlag = 0;
        }
    }

    @FXML
    void changeState() throws IOException {
        if(startFlag == 1){
            alert.setTitle("Warning Dialog");
            alert.setContentText("The Activity is running! Cannot change page!");
            alert.show();
        }else{
            Main.setRoot("ViewLog", logs);
        }
    }
}
