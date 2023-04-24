/*
Author : Yihui Wu
 */
package com.Frontend;

import com.Frontend.Controllers.CreateLogController;
import com.Frontend.Controllers.EditLogController;
import com.Frontend.Controllers.ViewLogController;
import javafx.application.Application;
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
        EffortLoggerServer server = new EffortLoggerServer(8080,8081);
        try {
			server.startHttpServer();
			server.startWebSocketServer();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        stage.setScene(scene);
        stage.sizeToScene();
        stage.show();
    }

    //switch scene
    public static void setRoot(String fxml) throws IOException {
        Window window = scene.getWindow();
        Stage stage = (Stage) window;
        switch(fxml){
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

    //switch scene
    public static void setRoot(String fxml, ArrayList<Log> logArrayList, String authToken) throws IOException {
        Window window = scene.getWindow();
        Stage stage = (Stage) window;
        switch(fxml){
            case "ViewLog":
                scene.setRoot(loadViewLogFXML(logArrayList, authToken));
                stage.setScene(scene);
                stage.sizeToScene();
                break;
            case "CreateLog":
                scene.setRoot(loadCreateLogFXML(logArrayList, authToken));
                stage.setScene(scene);
                stage.sizeToScene();
                break;
            case "EditLog":
                scene.setRoot(loadEditLogController(logArrayList, authToken));
                stage.setScene(scene);
                stage.sizeToScene();
                break;
        }
    }


    private static Parent loadCreateLogFXML(ArrayList<Log> logArrayList, String authToken) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/org/FXML/"+ "CreateLog.fxml"));
        CreateLogController temp = new CreateLogController(logArrayList, authToken);
        fxmlLoader.setController(temp);
        Parent root = fxmlLoader.load();
        return root;

    }
    private static Parent loadViewLogFXML(ArrayList<Log> logArrayList, String authToken) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/org/FXML/"+"ViewLog.fxml"));
        ViewLogController temp = new ViewLogController(logArrayList, authToken);
        fxmlLoader.setController(temp);
        Parent root = fxmlLoader.load();
        return root;
    }
    private static Parent loadEditLogController(ArrayList<Log> logArrayList, String authToken) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/org/FXML/"+"editLog.fxml"));
        EditLogController temp = new EditLogController(logArrayList, authToken);
        fxmlLoader.setController(temp);
        Parent root = fxmlLoader.load();
        return root;
    }
    private static Parent loadSignUpFXML() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/org/FXML/"+"signUp.fxml"));
        Parent root = fxmlLoader.load();
        return root;
    }
    private static Parent loadLoginFXML() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/org/FXML/"+"login.fxml"));
        Parent root = fxmlLoader.load();
        return root;
    }



    public static void main(String[] args) {
        launch();
    }

}
