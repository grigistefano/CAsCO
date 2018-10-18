package casco.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Casco extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
    	//crea finestra e lancia l'applicazione
        Parent parent = FXMLLoader.load(getClass().getResource("gui.fxml"));

        primaryStage.setTitle("CAsCO");
        Image image = new Image(getClass().getResourceAsStream("CAsCOicon.png"));
        primaryStage.getIcons().add(image);
        primaryStage.setScene(new Scene(parent));
        primaryStage.show();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}

