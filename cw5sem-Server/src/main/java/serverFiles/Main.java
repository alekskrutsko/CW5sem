package serverFiles;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("/FXMLFiles/mainWindow.fxml"));
        primaryStage.setResizable(false);
        primaryStage.setTitle("Сервер");
        primaryStage.setScene(new Scene(root, 440, 340));

        primaryStage.setOnCloseRequest(event -> {
            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle("Сервер не может быть выключен таким спопобом!");
            alert.setHeaderText(null);
            alert.setContentText("Пожалуйста, выключите сервер кнопкой \"Отключить сервер\".");
            alert.showAndWait();
            event.consume();
        });
        primaryStage.show();
    }

    public static void main(String[] args) { launch(args); }
}
