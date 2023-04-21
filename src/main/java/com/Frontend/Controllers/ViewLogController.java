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
    @FXML private TableColumn<Log, String> id, time,time1, project, effortCategory, lifeCycleStep, effortDetail;
    @FXML private Button logConsole, editLog;
    ArrayList<Log> logArrayList = null;

    public ViewLogController(ArrayList<Log> logArrayList){
        this.logArrayList = logArrayList;
    }

    @FXML
    private void initialize(){
        editLog.setOnAction(event -> {
            try {
                Main.setRoot("EditLog", logArrayList);
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

        ObservableList<Log> logList = FXCollections.observableArrayList(logArrayList);
        id.setCellValueFactory(new PropertyValueFactory<>("employee"));
        time.setCellValueFactory(new PropertyValueFactory<>("startTime"));
        time.setStyle("-fx-font-size: 8pt;");
        time1.setCellValueFactory(new PropertyValueFactory<>("endTime"));
        time1.setStyle("-fx-font-size: 8pt;");
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
