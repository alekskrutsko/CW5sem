package controllers;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import serverFiles.Server;

import java.net.URL;
import java.util.ArrayList;
import java.util.Map;
import java.util.ResourceBundle;

public class MainWindowController {
    private int port = 3000;
    private Thread thread;
    private Server server;
    private static ArrayList<Map<String, String>> usersConnected = new ArrayList<>();

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button serverOffButton;

    @FXML
    private Button updateButton;

    @FXML
    private TextArea newConnection;

    @FXML
    private Label portText;

    @FXML
    private Label serverStateText;

    @FXML
    private Label connectionDBText;

    public static void setUsersConnected(Map<String, String> usersConnected) {
        MainWindowController.usersConnected.add(usersConnected);
    }

    @FXML
    void stopServer(ActionEvent event) throws InterruptedException {
        server.stop();
        thread.interrupt();
        serverStateText.setText("Отключен!");
        serverStateText.setTextFill(Color.color(1, 0, 0));
        connectionDBText.setText("Нет соединения с БД!");
        connectionDBText.setTextFill(Color.color(1, 0, 0));
        portText.setText("Нет!");
        portText.setTextFill(Color.color(1, 0, 0));
        Stage dialogStage = (Stage) serverOffButton.getScene().getWindow();
        dialogStage.close();
        System.exit(0);
    }

    private void setTask() {
        Task task = new Task<Void>() {
            @Override
            public Void call() {
                server = new Server();
                server.start(port);
                return null;
            }
        };
        thread = new Thread(task);
        thread.start();
    }


    @FXML
    void updateServer(ActionEvent event) {
        if(usersConnected!=null){
            newConnection.clear();
           for(int i = 0; i < usersConnected.size(); i++){
               Map<String, String> ID = usersConnected.get(i);
               for (Map.Entry<String, String> userID : ID.entrySet()) {
                   if(userID.getKey() == "connected") {
                       newConnection.appendText("Пользователь с id " + userID.getValue() + " подключился, его ip-адрес: 127.0.0.1\n");
                   }else{
                       newConnection.appendText("Пользователь с id " + userID.getValue() + " отключился, его ip-адрес: 127.0.0.1\n");
                   }
           }
            }
        }
    }

    @FXML
    void initialize() {
        portText.setText(String.valueOf(port));
        setTask();
    }
}