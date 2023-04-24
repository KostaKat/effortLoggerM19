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
    String i_type;
    @FXML private Button start, stop;
    @FXML private TextArea description;
    long startTime;

    @FXML
    private void initialize(){
        interruptionType.getItems().addAll("Break","Phone","Teammate","Visitor","Other");
        interruptionType.setValue("Break");
        stop.setDisable(true);

        start.setOnAction(event -> {
            LocalDateTime currentDateTime = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            String formattedDateTime = currentDateTime.format(formatter);
            System.out.println(formattedDateTime);
            start.setDisable(true);
            stop.setDisable(false);
            startTime = System.nanoTime();
        });

        stop.setOnAction(event -> {
            LocalDateTime currentDateTime = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            String formattedDateTime = currentDateTime.format(formatter);
            long estimatedTime = System.nanoTime() - startTime;
            String formattedTime = String.format("%02d:%02d:%02d", TimeUnit.NANOSECONDS.toHours(estimatedTime),
                    TimeUnit.NANOSECONDS.toMinutes(estimatedTime) - TimeUnit.HOURS.toMinutes(TimeUnit.NANOSECONDS.toHours(estimatedTime)),
                    TimeUnit.NANOSECONDS.toSeconds(estimatedTime) - TimeUnit.MINUTES.toSeconds(TimeUnit.NANOSECONDS.toMinutes(estimatedTime)));
            i_type = interruptionType.getValue();
            System.out.println(formattedDateTime+"\n"+formattedTime+"\n"+i_type);
            start.setDisable(false);
            stop.setDisable(true);
            Stage stage = (Stage) start.getScene().getWindow();
            stage.close();
        });
    }

}
