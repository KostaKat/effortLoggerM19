package com.Frontend.Controllers;

import com.Frontend.Log;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.util.ArrayList;

public class ManagerController {
    private String authToken, ManagerId;
    private ObservableList<Log> logs = FXCollections.observableArrayList();
    @FXML
    private Label managerId;

    @FXML
    public void initialize() {
        managerId.setText(ManagerId);
    }

    public ManagerController(ObservableList<Log> logs, String authToken, String ManagerId) {
        this.authToken = authToken;
        this.logs = logs;
        this.ManagerId = ManagerId;
    }

}
