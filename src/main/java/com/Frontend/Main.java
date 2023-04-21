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

/**
 * JavaFX App
 */
//git
//test
public class Main extends Application {

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
        
        stage.show();
    }

    public static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadSignUpFXML(fxml));
    }

    public static void setRoot(String fxml, ArrayList<Log> logArrayList) throws IOException {
        switch(fxml){
            case "ViewLog":
                scene.setRoot(loadViewLogFXML(logArrayList));
                break;
            case "CreateLog":
                scene.setRoot(loadCreateLogFXML(logArrayList));
                break;
        }
    }

    private static Parent loadCreateLogFXML(ArrayList<Log> logArrayList) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/org/FXML/"+ "CreateLog.fxml"));
        CreateLogController temp = new CreateLogController(logArrayList);
        fxmlLoader.setController(temp);
        Parent root = fxmlLoader.load();
        return root;
    }
    private static Parent loadLoginFXML() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/org/FXML/"+"login.fxml"));
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
    private static Parent loadSignUpFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/org/FXML/"+fxml+ ".fxml"));
        Parent root = fxmlLoader.load();
        return root;
    }

    public static void main(String[] args) {
        launch();
    }

}
