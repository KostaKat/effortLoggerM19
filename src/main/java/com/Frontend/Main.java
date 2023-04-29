/*
Author : Yihui Wu
 */
package com.Frontend;

import com.Frontend.Controllers.CreateLogController;
import com.Frontend.Controllers.EditLogController;
import com.Frontend.Controllers.ManagerController;
import com.Frontend.Controllers.ViewLogController;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;

import com.HTTPHandler.server.EffortLoggerServer;
import javafx.stage.Window;

/**
 * JavaFX App
 */
public class Main extends Application {
    private static final String fxmlPath = "/org/FXML/";
    private static Scene scene;
    public static ArrayList<User> users = new ArrayList<>();

    @Override
    public void start(Stage stage) throws IOException {
        scene = new Scene(loadLoginFXML());
        EffortLoggerServer server = new EffortLoggerServer(8080, 8081);
        try {
            server.startHttpServer();
            server.startWebSocketServer();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        stage.setOnCloseRequest(event -> {
            server.stopHttpServer();
            server.stopWebSocketServer();
            System.exit(0); // This exits the application with status code 0 (successful exit)
        });
        stage.setScene(scene);
        stage.sizeToScene();
        stage.show();
    }

    // switch scene
    public static void setRoot(String fxml) throws IOException {
        Window window = scene.getWindow();
        Stage stage = (Stage) window;
        switch (fxml) {
            case "signUp":
                scene.setRoot(loadSignUpFXML());
                stage.setScene(scene);
                stage.sizeToScene();
                break;
            case "login":
                scene.setRoot(loadLoginFXML());
                stage.setScene(scene);
                stage.sizeToScene();
                break;
        }

    }

    public static void setManagerRoot(ObservableList<Log> logs, String authToken, String Id) throws IOException {
        Window window = scene.getWindow();
        Stage stage = (Stage) window;
        scene.setRoot(loadManagerFXML(logs, authToken, Id));
        stage.setScene(scene);
        stage.sizeToScene();
    }

    // switch scene
    public static void setRoot(String fxml, ObservableList<Log> logs, String authToken) throws IOException {
        Window window = scene.getWindow();
        Stage stage = (Stage) window;
        switch (fxml) {
            case "ViewLog":
                scene.setRoot(loadViewLogFXML(logs, authToken));
                stage.setScene(scene);
                stage.sizeToScene();
                break;
            case "CreateLog":
                scene.setRoot(loadCreateLogFXML(logs, authToken));
                stage.setScene(scene);
                stage.sizeToScene();
                break;
            case "EditLog":
                scene.setRoot(loadEditLogController(logs, authToken));
                stage.setScene(scene);
                stage.sizeToScene();
                break;
        }
    }

    private static Parent loadManagerFXML(ObservableList<Log> logs, String authToken, String ManagerId)
            throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/org/FXML/" + "Manager.fxml"));
        ManagerController temp = new ManagerController(logs, authToken, ManagerId);
        fxmlLoader.setController(temp);
        Parent root = fxmlLoader.load();
        return root;
    }

    private static Parent loadCreateLogFXML(ObservableList<Log> logs, String authToken) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/org/FXML/" + "CreateLog.fxml"));
        CreateLogController temp = new CreateLogController(logs, authToken);
        fxmlLoader.setController(temp);
        Parent root = fxmlLoader.load();
        return root;
    }

    private static Parent loadViewLogFXML(ObservableList<Log> logs, String authToken) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/org/FXML/" + "ViewLog.fxml"));
        ViewLogController temp = new ViewLogController(logs, authToken);
        fxmlLoader.setController(temp);
        Parent root = fxmlLoader.load();
        return root;
    }

    private static Parent loadEditLogController(ObservableList<Log> logs, String authToken) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/org/FXML/" + "editLog.fxml"));
        EditLogController temp = new EditLogController(logs, authToken);
        fxmlLoader.setController(temp);
        Parent root = fxmlLoader.load();
        return root;
    }

    private static Parent loadSignUpFXML() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/org/FXML/" + "signUp.fxml"));
        Parent root = fxmlLoader.load();
        return root;
    }

    private static Parent loadLoginFXML() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/org/FXML/" + "login.fxml"));
        Parent root = fxmlLoader.load();
        return root;
    }

    public static void main(String[] args) {
        launch();
    }

}
