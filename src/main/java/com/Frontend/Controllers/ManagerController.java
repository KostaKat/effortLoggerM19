package com.Frontend.Controllers;

import com.Frontend.Defect;
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
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class ManagerController {
    private String authToken, ManagerId;
    private ObservableList<Log> logs;
    private ObservableList<Defect> defects;
    private Defect defectTemp = new Defect();
    @FXML
    private TableView<Log> logTable;
    @FXML
    private TableView<Defect> defectTable;
    @FXML
    private TableColumn<Log, String> date, startTime, endTime, project, effortCategory, lifeCycleStep, effortDetail;
    @FXML
    private TableColumn<Defect, String> name, fixStatus, stepWhenInjected, stepWhenRemoved, defectCategory;
    @FXML
    private TextField managerId;
    @FXML
    private Button logOut, edit, defectManage;

    @FXML
    public void initialize() {
        System.out.println(authToken);
        managerId.setText(ManagerId);
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
        defectTable.setItems(defects);

        logOut.setOnAction(event -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                    "Are you show you wan to log out?",
                    ButtonType.YES, ButtonType.NO);
            alert.showAndWait();
            if (alert.getResult() == ButtonType.YES) {
                try {
                    Main.setRoot("login");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        defectManage.setOnAction(event -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/FXML/" + "DefectManage.fxml"));
                DefectManageController temp = new DefectManageController(defects);
                loader.setController(temp);
                Parent root = loader.load();

                Stage popupStage = new Stage();
                popupStage.initModality(Modality.APPLICATION_MODAL);
                popupStage.setScene(new Scene(root));
                popupStage.setTitle("Pop-up Page");
                popupStage.showAndWait();

                DefectManageController defectC = loader.getController();

                if (defectC.getFlag() != null) {
                    if (defectC.getFlag().equals("update")) {
                        System.out.println(defectC.getStatus() + defectC.getDes());
                        defectTemp.setFixStatus(defectC.getStatus());
                        defectTemp.setDescription(defectC.getDes());
                        defectTemp.setDefectID(defectC.getDefectID());
                        defectUpdate();
                    } else if (defectC.getFlag().equals("delete")) {
                        defects.remove(defectC.getIndex());
                        /*
                         * TODO delete function
                         */
                    } else {

                    }
                }

            } catch (IOException e) {
                System.out.println("exception caught in the defect pop up page");
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
                logTable.refresh();
            } catch (IOException e) {

            }
        });
        logTable.setOnMouseClicked(event -> {
            Log current = logTable.getSelectionModel().getSelectedItem();
            System.out.println(current);
        });
    }

    public ManagerController(ObservableList<Log> logs, ObservableList<Defect> defects, String authToken, String ManagerId) {
        this.authToken = authToken;
        this.defects = defects;
        this.logs = logs;
        this.ManagerId = ManagerId;
    }

    void defectUpdate() throws IOException {
        String serverUrl = "http://localhost:8080/editDefect"; // Replace with your server's URL
        URL DefectUrl = new URL(serverUrl);
        HttpURLConnection con = (HttpURLConnection) DefectUrl.openConnection();

        // Set request method and headers
        con.setRequestMethod("PUT");
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestProperty("Accept", "application/json");
        con.setDoOutput(true);

        // Create JSON payload

        JSONObject defectData = new JSONObject();
        defectData.put("Token", authToken);
        defectData.put("defectID", defectTemp.getDefectID());
        defectData.put("fixStatus", defectTemp.getFixStatus());
        defectData.put("description", defectTemp.getDescription());
        System.out.println("JSON Payload: " + defectData.toString());

        String defectAddString = defectData.toString();
        StringBuilder responseBuilder = new StringBuilder();
        // Send the request
        try (OutputStream os = con.getOutputStream()) {
            byte[] input = defectAddString.getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        // Read the response
        try (BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = in.readLine()) != null) {
                responseBuilder.append(line);
            }
        } catch (Exception e) {
            // If there is an exception, read the response from the error stream instead
            try (BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getErrorStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = in.readLine()) != null) {
                    responseBuilder.append(line);
                }
            }
        }
        System.out.println(responseBuilder.toString());
    }

}
