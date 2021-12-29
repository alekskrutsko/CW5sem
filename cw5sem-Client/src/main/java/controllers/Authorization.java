package controllers;

import administratorControllers.AdminWindow;
import appraiserControllers.AppraiserWindow;
import customerControllers.ClientWindow;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import personalInfo.User;
import personalInfo.User.Role;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import serverMSG.ServerMSG;
import serverMSG.ServerMSG.CommandTypes;

import java.io.IOException;

public class Authorization {

    @FXML
    private TextField loginLine;

    @FXML
    private PasswordField passwordLine;

    @FXML
    private Button signInButton;

    @FXML
    private Button registerButton;

    @FXML
    private Label errorMsgText;

    private User loggedUser;

    public void setLoggedUser(User loggedUser) {
        this.loggedUser = loggedUser;
    }

    public User getLoggedUser() {
        return loggedUser;
    }

    @FXML
    void signIn(ActionEvent event) throws IOException {
       try{
            User user = new User();
            user.setLogin(loginLine.getText());
            user.setPasswordMask(passwordLine.getText());
            ServerMSG serverMSG = new ServerMSG(user, CommandTypes.SIGNIN, "");
            Client client = new Client();
            client.startConnection("127.0.0.1", 3000);
            ServerMSG answer = null;
            answer = client.sendMessage(serverMSG);
            if(answer.getData().equals("blocked")){
                errorMsgText.setText("Данный пользователь был заблокирован!");
            }
            else if (answer.getData().equals("success")) {
                errorMsgText.setText("");
                loggedUser = answer.getUser();
                Stage dialogStage = new Stage();
                dialogStage.setResizable(false);
                dialogStage.initOwner(signInButton.getScene().getWindow());
                dialogStage.initModality(Modality.WINDOW_MODAL);
                if(answer.getUser().getRole().equals(Role.ADMINISTRATOR)){
                    FXMLLoader loader = new FXMLLoader();
                    loader.setLocation(Authorization.class.getResource("/admin/adminWindow.fxml"));
                    Parent root = loader.load();
                    AdminWindow window = loader.getController();
                    window.setLoggedUser(loggedUser);
                    loader.setController(window);
                    dialogStage.setTitle("Меню администратора");
                    dialogStage.setScene(new Scene(root, 1000, 600));
                }
                else if(answer.getUser().getRole().equals(Role.APPRAISER)){
                    FXMLLoader loader = new FXMLLoader();
                    loader.setLocation(Authorization.class.getResource("/appraiser/appraiserWindow.fxml"));
                    Parent root = loader.load();
                    AppraiserWindow window = loader.getController();
                    window.setLoggedUser(loggedUser);
                    loader.setController(window);
                    dialogStage.setTitle("Меню оценщика");
                    dialogStage.setScene(new Scene(root, 1156, 600));
                }
                else if(answer.getUser().getRole().equals(Role.CONSUMER)){
                    FXMLLoader loader = new FXMLLoader();
                    loader.setLocation(Authorization.class.getResource("/customer/customerMain.fxml"));
                    Parent root = loader.load();
                    ClientWindow window = loader.getController();
                    window.setLoggedUser(loggedUser);
                    loader.setController(window);
                    dialogStage.setTitle("Меню клиента");
                    dialogStage.setScene(new Scene(root, 1000, 600));
                }
                dialogStage.setOnCloseRequest(event1 -> {
                    client.stopConnection();
                });
                dialogStage.show();
            } else if (answer.getData().equals("password")) {
                errorMsgText.setText("Введён неверный пароль!");
            } else if (answer.getData().equals("login")) {
                errorMsgText.setText("Пользователя с таким логином не существует!");
            }
        }
        catch (Exception e) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Ошибка соединения с сервером");
            alert.setHeaderText(null);
            alert.setContentText("Пожалуйста, проверьте подключение к серверу.");
            alert.showAndWait();
        }

    }

    @FXML
    void signUp(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/authorization/signUp.fxml"));
        Stage dialogStage = new Stage();
        dialogStage.setResizable(false);
        dialogStage.setTitle("Регистрация пользователя");
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.setScene(new Scene(root, 600, 358));
        dialogStage.showAndWait();
    }

    @FXML
    void initialize() {
        loggedUser = new User();
    }
}
