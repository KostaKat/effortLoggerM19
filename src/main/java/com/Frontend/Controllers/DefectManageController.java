package com.Frontend.Controllers;

import com.Frontend.Defect;
import com.Frontend.Log;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import com.Frontend.Defect;
import com.Frontend.Log;
import com.Frontend.Main;
import javafx.stage.Stage;

public class DefectManageController {

    private ObservableList<Defect> defects;
    private Defect selectedDefect;
    private int index;
    private String status, des;
    private String flag = null;

    @FXML
    private ChoiceBox<String> select, fixStatus;
    @FXML
    private Button update, delete;
    @FXML
    private TextArea description;

    public int getIndex() {
        return index;
    }

    public String getStatus() {
        return status;
    }

    public String getDes() {
        return des;
    }

    public String getFlag() {
        return flag;
    }

    public DefectManageController(ObservableList<Defect> defects){
        this.defects = defects;
    }

    @FXML
    public void initialize(){

        description.setWrapText(true);
        fixStatus.getItems().addAll("Open","Solved");

        if(defects.isEmpty()){
            System.out.println("No defects found.");
        }else{
            for (Defect defect: defects){
                select.getItems().add(defect.toString());
            }
            defects.addListener((ListChangeListener<Defect>) c -> {
                select.getItems().clear();
                for (Defect defect: defects){
                    select.getItems().add(defect.toString());
                }
            });
        }

        select.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                index = select.getSelectionModel().getSelectedIndex();
                selectedDefect = defects.get(index);
                fixStatus.setValue(selectedDefect.getFixStatus());
                description.setText(selectedDefect.getDescription());
            }
        });

        update.setOnAction(event -> {
            if(!select.getSelectionModel().isEmpty()){
                index = select.getSelectionModel().getSelectedIndex();
                status = fixStatus.getValue();
                des = description.getText();
                flag = "update";
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setContentText("Defect Log updated!!!");
                alert.showAndWait();
                Stage stage = (Stage) update.getScene().getWindow();
                stage.close();
            }else{
                Alert a = new Alert(Alert.AlertType.INFORMATION);
                a.setContentText("Please Select One Defect!!!");
                a.showAndWait();
            }

        });

        delete.setOnAction(event -> {
            if(!select.getSelectionModel().isEmpty()){
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                        "Are you sure you want to delete the select Log? THIS ACTION CANNOT BE RESTORE!",
                        ButtonType.YES, ButtonType.NO);
                alert.showAndWait();
                if (alert.getResult() == ButtonType.YES) {
                    index = select.getSelectionModel().getSelectedIndex();
                    flag = "delete";
                    Alert a = new Alert(Alert.AlertType.INFORMATION);
                    a.setContentText("Defect Log Deleted!!!");
                    a.showAndWait();
                    Stage stage = (Stage) update.getScene().getWindow();
                    stage.close();
                }
            }else{
                Alert a = new Alert(Alert.AlertType.INFORMATION);
                a.setContentText("Please Select One Defect!!!");
                a.showAndWait();
            }
        });




    }



}
