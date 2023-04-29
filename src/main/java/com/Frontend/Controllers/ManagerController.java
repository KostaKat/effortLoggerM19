package com.Frontend.Controllers;

import com.Frontend.Log;

import com.Frontend.Main;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;

public class ManagerController {
    private String authToken, ManagerId;
    private ObservableList<Log> logs = FXCollections.observableArrayList();
    @FXML
    private TableView<Log> logTable;
    @FXML
    private TableColumn<Log, String> date, startTime, endTime, project, effortCategory, lifeCycleStep, effortDetail;
    @FXML
    private TextField managerId;
    @FXML
    private TextArea description;
    @FXML
    private Button logOut, edit;

    @FXML
    public void initialize() {
        managerId.setText(ManagerId);
        date.setCellValueFactory(new PropertyValueFactory<>("date"));
        startTime.setCellValueFactory(new PropertyValueFactory<>("startTime"));
        endTime.setCellValueFactory(new PropertyValueFactory<>("endTime"));
        project.setCellValueFactory(new PropertyValueFactory<>("project"));
        effortCategory.setCellValueFactory(new PropertyValueFactory<>("effortCategory"));
        lifeCycleStep.setCellValueFactory(new PropertyValueFactory<>("lifeCycleStep"));
        effortDetail.setCellValueFactory(new PropertyValueFactory<>("effortDetail"));
        logTable.setItems(logs);
        logOut.setOnAction(event -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                    "Are you show you wan to log out?",
                    ButtonType.YES, ButtonType.NO);
            alert.showAndWait();
            if (alert.getResult() == ButtonType.YES){
                try {
                    Main.setRoot("login");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        edit.setOnAction(event -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/FXML/" + "editLogM.fxml"));
                EditLogManagerController temp = new EditLogManagerController(logs, authToken);
                loader.setController(temp);
                Parent root = loader.load();

                Stage popupStage = new Stage();
                popupStage.initModality(Modality.APPLICATION_MODAL);
                popupStage.setScene(new Scene(root));
                popupStage.setTitle("Pop-up Page");
                popupStage.showAndWait();

                EditLogManagerController e = loader.getController();
                for (Log log : e.logs) {
                    System.out.println("p"+ log);
                }
                logTable.refresh();
            }catch(IOException e){

            }
        });
        logTable.setOnMouseClicked(event -> {
            Log current = logTable.getSelectionModel().getSelectedItem();
            description.setText(current.getLogDescription());
            description.setWrapText(true);
            System.out.println(current);
        });
    }

    public ManagerController(ObservableList<Log> logs, String authToken, String ManagerId) {
        this.authToken = authToken;
        this.logs = logs;
        this.ManagerId = ManagerId;
    }



}
