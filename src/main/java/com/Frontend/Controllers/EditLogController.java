package com.Frontend.Controllers;

import com.Frontend.Log;
import com.Frontend.Main;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

import java.io.IOException;
import java.util.ArrayList;

public class EditLogController {
    ArrayList<Log> logArrayList = null;
    @FXML
    private Button viewLog, logConsole;
    @FXML
    private void initialize(){
        viewLog.setOnAction(event -> {
            try {
                Main.setRoot("ViewLog", logArrayList);
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

    }


    public EditLogController(ArrayList<Log> logArrayList) {
        this.logArrayList = logArrayList;
    }
}
