/*
Author : Yihui Wu
 */
package com.Frontend.Controllers;
import java.io.IOException;
import java.util.ArrayList;

import com.Frontend.Log;
import com.Frontend.Main;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class ViewLogController {
    @FXML private TableView logTable;
    @FXML private TableColumn<Log, String> date, startTime,endTime, project, effortCategory, lifeCycleStep, effortDetail;
    private ArrayList<Log> logArrayList = null;
    private String authToken;

    public ViewLogController(ArrayList<Log> logArrayList, String authToken){
        this.logArrayList = logArrayList;
        this.authToken = authToken;
    }

    @FXML
    private void initialize(){
        ObservableList<Log> logList = FXCollections.observableArrayList(logArrayList);
        date.setCellValueFactory(new PropertyValueFactory<>("date"));
        startTime.setCellValueFactory(new PropertyValueFactory<>("startTime"));
        endTime.setCellValueFactory(new PropertyValueFactory<>("endTime"));
        project.setCellValueFactory(new PropertyValueFactory<>("project"));
        effortCategory.setCellValueFactory(new PropertyValueFactory<>("effortCategory"));
        lifeCycleStep.setCellValueFactory(new PropertyValueFactory<>("lifeCycleStep"));
        effortDetail.setCellValueFactory(new PropertyValueFactory<>("effortDetail"));

        logTable.setItems(logList);
        logTable.setOnMouseClicked(event -> {
            Log current = (Log) logTable.getSelectionModel().getSelectedItem();
            System.out.println(current);
        });
    }

}
