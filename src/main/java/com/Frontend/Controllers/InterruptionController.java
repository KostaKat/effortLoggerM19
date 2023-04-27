package com.Frontend.Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

public class InterruptionController {
    @FXML private ChoiceBox<String> interruptionType;
    private String i_type = null;
    private String startFormattedTime, endFormattedDateTime, des;
    @FXML private Button start, stop;
    @FXML private TextArea description;


    public String getDes() {
        return des;
    }

    public String getEndFormattedDateTime() {
        return endFormattedDateTime;
    }

    public String getI_type() {
        return i_type;
    }

    public String getStartFormattedTime() {
        return startFormattedTime;
    }



    @FXML
    private void initialize(){
        interruptionType.getItems().addAll("Break","Phone","Teammate","Visitor","Other");
        interruptionType.setValue("Break");
        stop.setDisable(true);

        start.setOnAction(event -> {
            LocalDateTime currentDateTime = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
            startFormattedTime = currentDateTime.format(formatter);
            start.setDisable(true);
            stop.setDisable(false);
        });

        stop.setOnAction(event -> {
            LocalDateTime currentDateTime = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
            endFormattedDateTime = currentDateTime.format(formatter);

            i_type = interruptionType.getValue();
            des = description.getText();

            start.setDisable(false);
            stop.setDisable(true);
            Stage stage = (Stage) start.getScene().getWindow();
            stage.close();
        });
    }

}
