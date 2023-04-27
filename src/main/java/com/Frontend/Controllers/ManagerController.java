package com.Frontend.Controllers;

import com.Frontend.Log;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.util.ArrayList;

public class ManagerController {
    private String authToken, ManagerId;
    private ArrayList<Log> logs = new ArrayList<>();
    @FXML
    private Label managerId;

    @FXML
    public void initialize(){
        managerId.setText(ManagerId);
    }

    public ManagerController(ArrayList<Log> logArrayList, String authToken, String ManagerId){
        this.authToken = authToken;
        this.logs = logArrayList;
        this.ManagerId = ManagerId;
    }





}
