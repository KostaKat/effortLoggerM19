/*
Author : Yihui Wu
 */
package com.Frontend;

import com.Frontend.Controllers.CreateLogController;
import com.Frontend.Controllers.ViewLogController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;

import com.HTTPHandler.server.Server;
import javafx.stage.Window;

/**
 * JavaFX App
 */
//git
//test
public class Main extends Application {
    private static String fxmlPath = "/org/FXML/";
    private static Scene scene;
    public static ArrayList<User> users = new ArrayList<>();

    @Override
    public void start(Stage stage) throws IOException {
        scene = new Scene(loadLoginFXML());
        Server server = new Server(8086);
        try {
			server.startServer();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        stage.setScene(scene);
        stage.setMinHeight(400);
        stage.setMinWidth(500);
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
                stage.setMinHeight(400);
                stage.setMinWidth(500);
                break;
            case "login":
                scene.setRoot(loadLoginFXML());
                stage.setScene(scene);
                stage.setMinHeight(400);
                stage.setMinWidth(500);
                break;
        }
    }

    //switch scene
    public static void setRoot(String fxml, ArrayList<Log> logArrayList) throws IOException {
        Window window = scene.getWindow();
        Stage stage = (Stage) window;
        switch(fxml){
            case "ViewLog":
                scene.setRoot(loadViewLogFXML(logArrayList));
                stage.setScene(scene);
                stage.setMinHeight(400);
                stage.setMinWidth(684);
                break;
            case "CreateLog":
                scene.setRoot(loadCreateLogFXML(logArrayList));
                stage.setScene(scene);
                stage.setMinHeight(400);
                stage.setMinWidth(684);
                break;
        }
    }








    private static Parent loadCreateLogFXML(ArrayList<Log> logArrayList) throws IOException {
<<<<<<< HEAD
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource(fxmlPath+ "CreateLog.fxml"));
        PrimaryController temp = new PrimaryController(logArrayList);
=======
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/org/FXML/"+ "CreateLog.fxml"));
        CreateLogController temp = new CreateLogController(logArrayList);
>>>>>>> 0c54bde8583cb045dacc306bc0e56bc4721da948
        fxmlLoader.setController(temp);
        Parent root = fxmlLoader.load();
        return root;
    }
    private static Parent loadViewLogFXML(ArrayList<Log> logArrayList) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/org/FXML/"+"ViewLog.fxml"));
        ViewLogController temp = new ViewLogController(logArrayList);
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
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource(fxmlPath+"login.fxml"));
        Parent root = fxmlLoader.load();
        return root;
    }
<<<<<<< HEAD
    private static Parent loadViewLogFXML(ArrayList<Log> logArrayList) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource(fxmlPath+"ViewLog.fxml"));
        viewController temp = new viewController(logArrayList);
        fxmlLoader.setController(temp);
        Parent root = fxmlLoader.load();
        return root;
    }
    private static Parent loadSignUpFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource(fxmlPath+fxml+ ".fxml"));
        Parent root = fxmlLoader.load();
        return root;
    }

=======
>>>>>>> 0c54bde8583cb045dacc306bc0e56bc4721da948
    public static void main(String[] args) {
        launch();
    }

}
