package com.narawit.schedulesetter;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    private static Scene scene;
    @Override
    public void start(Stage stage) throws IOException {
        scene = new Scene(loadFXML("startpage/mainpage"), 480, 480);
        stage.setTitle("Vaccine Scheduler Setter");
        stage.getIcons().add(new Image(Main.class.getResourceAsStream("icon/icon.png")));
        stage.setScene(scene);
        stage.show();
    }

    public static void setRoot(String fxml) throws IOException{
        scene.setRoot(loadFXML(fxml));
    }
    
    public static Stage createStage(String resourcePath, String title, int sizeX, int sizeY) throws IOException{
        Stage stage = new Stage();
        stage.setTitle(title);
        stage.setScene(new Scene(loadFXML(resourcePath), sizeX, sizeY));
        return stage;
    }

    private static Parent loadFXML(String fxml) throws IOException{
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource(fxml+".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
    	launch();
    }
    
    public static void terminate() {
    	Platform.exit();
    }
}