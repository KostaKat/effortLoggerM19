package com.Frontend.Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class DefectController {

    private String nameS, descriptionS, stepWhenInjectedS, stepWhenRemovedS, defectCategoryS;
    @FXML
    private Button add;
    @FXML
    private TextField name;
    @FXML
    private TextArea description;
    @FXML
    private ChoiceBox<String> stepWhenInjected, stepWhenRemoved, defectCategory;

    public String getNameS() {
        return nameS;
    }

    public String getDescriptionS() {
        return descriptionS;
    }

    public String getStepWhenInjectedS() {
        return stepWhenInjectedS;
    }

    public String getStepWhenRemovedS() {
        return stepWhenRemovedS;
    }

    public String getDefectCategoryS() {
        return defectCategoryS;
    }

    @FXML
    public void initialize() {
        description.setWrapText(true);

        stepWhenInjected.getItems().addAll("Planning", "Information Gathering", "Information Understanding",
                "Verifying", "Outlining", "Drafting", "Finalizing", "Team Meeting", "Coach Meeting",
                "Stakeholder Meeting");
        stepWhenRemoved.getItems().addAll("Planning", "Information Gathering", "Information Understanding", "Verifying",
                "Outlining", "Drafting", "Finalizing", "Team Meeting", "Coach Meeting", "Stakeholder Meeting");
        defectCategory.getItems().addAll("Not specified", "10 Documentation", "20 Syntax", "30 Build, Package",
                "40 Assignment", "50 Interface", "60 Checking", "70 Data", "80 Function", "90 System",
                "100 Environment");

        defectCategory.setValue("Not specified");

        add.setOnAction(event -> {
            if (!description.getText().isEmpty() && !stepWhenRemoved.getSelectionModel().isEmpty()
                    && !stepWhenInjected.getSelectionModel().isEmpty() && !defectCategory.getSelectionModel().isEmpty()
                    && !name.getText().isEmpty()) {
                nameS = name.getText();
                descriptionS = description.getText();
                stepWhenInjectedS = stepWhenInjected.getValue();
                stepWhenRemovedS = stepWhenRemoved.getValue();
                defectCategoryS = defectCategory.getValue();
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setContentText("New Defect Log added!!!");
                alert.showAndWait();
                Stage stage = (Stage) add.getScene().getWindow();
                stage.close();
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setContentText("You have to fill all the box!!!");
                alert.showAndWait();
            }
        });
    }

}
