package com.Frontend.Controllers;

import com.Frontend.Log;
import com.Frontend.Main;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;

import java.io.IOException;
import java.util.ArrayList;

public class EditLogController {
    private ArrayList<Log> logArrayList = null;
    private Log selectedLog;
    private int index;
    @FXML private ChoiceBox<String> project_e, lifeCycleStep_e, effortCategory_e, effortDetail_e, select;
    @FXML private TextArea logDescription_e;
    @FXML private Button viewLog, logConsole, update;

    @FXML
    private void initialize(){



        for (Log log : logArrayList) {
            select.getItems().add(log.toString());
        }

        select.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                index = select.getSelectionModel().getSelectedIndex();
                selectedLog = logArrayList.get(index);
                project_e.setValue(selectedLog.getProject());
                lifeCycleStep_e.setValue(selectedLog.getLifeCycleStep());
                effortCategory_e.setValue(selectedLog.getEffortCategory());
                effortDetail_e.setValue(selectedLog.getEffortDetail());
                logDescription_e.setText(selectedLog.getLogDescription());
            }
        });

        update.setOnAction(event -> {
            if(!project_e.getSelectionModel().isEmpty() && !lifeCycleStep_e.getSelectionModel().isEmpty() && !effortDetail_e.getSelectionModel().isEmpty() && !effortCategory_e.getSelectionModel().isEmpty() && !select.getSelectionModel().isEmpty()){
                selectedLog.setProject(project_e.getValue());
                selectedLog.setLogDescription(logDescription_e.getText());
                selectedLog.setLifeCycleStep(lifeCycleStep_e.getValue());
                selectedLog.setEffortDetail(effortDetail_e.getValue());
                selectedLog.setEffortCategory(effortCategory_e.getValue());
                logArrayList.set(index, selectedLog);
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setContentText("Edit Successful!");
                select.getItems().clear();
                for (Log log : logArrayList) {
                    select.getItems().add(log.toString());
                }
                lifeCycleStep_e.setValue(null);
                project_e.setValue(null);
                effortCategory_e.setValue(null);
                effortDetail_e.setValue(null);
                alert.showAndWait();
            }else{
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setContentText("Please select all the box!");
                alert.showAndWait();
            }
        });


        viewLog.setOnAction(event -> {
            try {
                Main.setRoot("ViewLog", logArrayList);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        logConsole.setOnAction(event -> {
            try {
                Main.setRoot("CreateLog", logArrayList);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        //initialize the choiceBox property when selected
        project_e.getItems().addAll("Business Project", "Development Project");
        effortCategory_e.getItems().addAll("Plans","Deliverables","Interruptions","Defects","Others");
        project_e.valueProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue != null){
                if (newValue.equals("Business Project")) {
                    lifeCycleStep_e.getItems().clear();
                    lifeCycleStep_e.setValue(null);
                    lifeCycleStep_e.getItems().addAll("Planning","Information Gathering","Information Understanding","Verifying","Outlining","Drafting","Finalizing","Team Meeting","Coach Meeting","Stakeholder Meeting");
                } else if (newValue.equals("Development Project")) {
                    lifeCycleStep_e.getItems().clear();
                    lifeCycleStep_e.setValue(null);
                    lifeCycleStep_e.getItems().addAll("Problem Understanding","Conceptual Design Plan","Requirements","Conceptual Design",
                            "Conceptual Design Review","Detailed Design Plan","Detailed Design/Prototype","Detailed Design Review","Implementation Plan","Test Case Generation",
                            "Solution Specification","Solution Review","Solution Implementation","Unit/System Test","Reflection","Repository Update");
                }
            }
        });

        effortCategory_e.valueProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue != null){
                switch (newValue) {
                    case "Plans":
                        effortDetail_e.getItems().clear();
                        effortDetail_e.setValue(null);
                        effortDetail_e.getItems().addAll("Project Plan","Risk Management Plan","Conceptual Design Plan","Detailed Design Plan","Implementation Plan");
                        effortDetail_e.setValue("Project Plan");
                        break;
                    case "Deliverables":
                        effortDetail_e.getItems().clear();
                        effortDetail_e.setValue(null);
                        effortDetail_e.getItems().addAll("Conceptual Design","Detailed Design","Test Cases","Solution","Reflection","Outline","Draft","Report","User Defined","Other");
                        effortDetail_e.setValue("Conceptual Design");
                        break;
                    case "Interruptions":
                        effortDetail_e.getItems().clear();
                        effortDetail_e.setValue(null);
                        effortDetail_e.getItems().addAll("Break","Phone","Teammate","Visitor","Other");
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


    public EditLogController(ArrayList<Log> logArrayList) {
        this.logArrayList = logArrayList;
    }
}
