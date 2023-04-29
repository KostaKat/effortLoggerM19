/*
Author : Yihui Wu
 */
package com.Frontend.Controllers;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;

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
    private TableColumn<Log, String> date, startTime, endTime, project, effortCategory, lifeCycleStep, effortDetail;
    private ObservableList<Log> logs;
    private String authToken;

    public ViewLogController(ObservableList<Log> logs, String authToken) {
        this.logs = logs;
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
        logTable.setOnMouseClicked(event -> {
            Log current = logTable.getSelectionModel().getSelectedItem();
            System.out.println(current);
        });

    }

}
