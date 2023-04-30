/*
Author : Yihui Wu
 */
package com.Frontend.Controllers;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;

import com.Frontend.Defect;
import com.Frontend.Log;
import com.Frontend.LogWebSocketClient;
import com.Frontend.Main;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class ViewLogController {
    @FXML
    private TableView<Log> logTable;
    @FXML
    private TableView<Defect> defectTable;
    @FXML
    private TableColumn<Log, String> date, startTime, endTime, project, effortCategory, lifeCycleStep, effortDetail;
    @FXML
    private TableColumn<Defect, String> name, fixStatus, stepWhenInjected, stepWhenRemoved, defectCategory;
    private ObservableList<Log> logs;
    private ObservableList<Defect> defects;
    private String authToken;

    public ViewLogController(ObservableList<Log> logs, ObservableList<Defect> defects, String authToken) {
        this.logs = logs;
        this.defects = defects;
        this.authToken = authToken;
    }

    @FXML
    private void initialize() {
        date.setCellValueFactory(new PropertyValueFactory<>("date"));
        startTime.setCellValueFactory(new PropertyValueFactory<>("startTime"));
        endTime.setCellValueFactory(new PropertyValueFactory<>("endTime"));
        project.setCellValueFactory(new PropertyValueFactory<>("project"));
        effortCategory.setCellValueFactory(new PropertyValueFactory<>("effortCategory"));
        lifeCycleStep.setCellValueFactory(new PropertyValueFactory<>("lifeCycleStep"));
        effortDetail.setCellValueFactory(new PropertyValueFactory<>("effortDetail"));
        logTable.setItems(logs);

        name.setCellValueFactory(new PropertyValueFactory<>("name"));
        fixStatus.setCellValueFactory(new PropertyValueFactory<>("fixStatus"));
        stepWhenInjected.setCellValueFactory(new PropertyValueFactory<>("stepWhenInjected"));
        stepWhenRemoved.setCellValueFactory(new PropertyValueFactory<>("stepWhenRemoved"));
        defectCategory.setCellValueFactory(new PropertyValueFactory<>("defectCategory"));

        logTable.setOnMouseClicked(event -> {
            Log current = logTable.getSelectionModel().getSelectedItem();
            System.out.println(current);
        });

    }

}
