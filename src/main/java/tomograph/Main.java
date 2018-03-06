package tomograph;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("/window.fxml"));
        primaryStage.setTitle("Symulator tomografu");

        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/style.css").toString());
        primaryStage.setScene(scene);
//        primaryStage.setWidth(650);
//        primaryStage.setHeight(590);
        primaryStage.setResizable(false);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
