package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import personalInfo.User;
import serverMSG.ServerMSG;
import personalInfo.User.Role;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUpController {

    @FXML
    private Label errorMsgText;

    @FXML
    private TextField surname;

    @FXML
    private TextField firstName;

    @FXML
    private TextField patronymic;

    @FXML
    private TextField phone;

    @FXML
    private TextField loginLine;

    @FXML
    private PasswordField passwordLine;

    @FXML
    private Button signUpButton;

    @FXML
    private Button cancelButton;

    @FXML
    void closeDialog(ActionEvent event) {
        Stage dialogStage = (Stage) cancelButton.getScene().getWindow();
        dialogStage.close();
    }

    @FXML
    void saveUser(ActionEvent event) {
        try {
            //if (login.isEmpty()) {
            ServerMSG serverMSG = new ServerMSG();
            User user = new User(loginLine.getText(), passwordLine.getText(), firstName.getText(),
                    surname.getText(), patronymic.getText(), phone.getText(), Role.CONSUMER, false);
            serverMSG.setUser(user);
            serverMSG.setCommandType(ServerMSG.CommandTypes.SIGNUP);

            Client client = new Client();
            client.startConnection("127.0.0.1", 3000);
            ServerMSG answer = client.sendMessage(serverMSG);

            if (answer.getData().equals("login exists")) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Ошибка");
                alert.setHeaderText(null);
                alert.setContentText("Пользователь с таким логином уже существует!");
                alert.showAndWait();
            } else {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Информационное окно");
                alert.setHeaderText(null);
                alert.setContentText("Успешная регистрация пользователя!");
                alert.showAndWait();

                Stage dialogStage = (Stage) signUpButton.getScene().getWindow();
                dialogStage.close();
            }
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Ошибка соединения с сервером");
            alert.setHeaderText(null);
            alert.setContentText("Пожалуйста, проверьте подключение к серверу.");
            alert.showAndWait();
        }
    }

    @FXML
    void initialize() {
        loginLine.textProperty().addListener((observable, oldValue, newValue) -> {
            checkRegistration();
        });
        passwordLine.textProperty().addListener((observable, oldValue, newValue) -> {
            checkRegistration();
        });
        firstName.textProperty().addListener((observable, oldValue, newValue) -> {
            checkRegistration();
        });
        surname.textProperty().addListener((observable, oldValue, newValue) -> {
            checkRegistration();
        });
        patronymic.textProperty().addListener((observable, oldValue, newValue) -> {
            checkRegistration();
        });
        errorMsgText.setWrapText(true);
    }

    private void checkRegistration() {
        Pattern personalInfoPattern = Pattern.compile("[А-я]{2,20}");
        Matcher matcher = personalInfoPattern.matcher(surname.getText());
        if (!matcher.matches()) {
            errorMsgText.setText(
                    "Фамилия не введена или введена неверно! (должна содержать только символы русского алфавита, без пробелов и знаков препинания, количество символов: от 2 до 20)");
            signUpButton.setDisable(true);
            return;
        } else {
            errorMsgText.setText("");
        }


        matcher = personalInfoPattern.matcher(firstName.getText());
        if (!matcher.matches()) {
            errorMsgText.setText(
                    "Имя не введено или введено неверно! (должно содержать только символы русского алфавита, без пробелов и знаков препинания, количество символов: от 2 до 20)");
            signUpButton.setDisable(true);
            return;
        } else {
            errorMsgText.setText("");
        }

        matcher = personalInfoPattern.matcher(patronymic.getText());
        if (!matcher.matches()) {
            errorMsgText.setText(
                    "Отчество не введено или введено неверно! (должно содержать только символы русского алфавита, без пробелов и знаков препинания, количество символов: от 2 до 20)");
            signUpButton.setDisable(true);
            return;
        } else {
            errorMsgText.setText("");
        }

        Pattern phonePattern = Pattern.compile("[0-9]{11}");
        matcher = phonePattern.matcher(phone.getText());
        if (!matcher.matches()) {
            errorMsgText.setText(
                    "Номер телефона не введено или введено неверно! (должен быть записан в формате *** *** ****: 11 цифр)");
            signUpButton.setDisable(true);
            return;
        } else {
            errorMsgText.setText("");
        }


        Pattern pattern = Pattern.compile(
                "^[0-9a-zA-Z]+([0-9a-zA-Z]*[-._+])*[0-9a-zA-Z]+@[0-9a-zA-Z]+([-.][0-9a-zA-Z]+)*([0-9a-zA-Z]*[.])[a-zA-Z]{2,6}$");
        matcher = pattern.matcher(loginLine.getText());
        if (!matcher.matches()) {
            errorMsgText.setText("Логин не введен или введен неверно! (введите электронную почту)");
            signUpButton.setDisable(true);
            return;
        } else {
            errorMsgText.setText("");
        }

        Pattern passwordPattern = Pattern.compile("[a-zA-Z0-9]{2,20}");
        matcher = passwordPattern.matcher(passwordLine.getText());
        if (!matcher.matches()) {
            errorMsgText.setText(
                    "Пароль не введен или введен неверно! (не должен содержать пробелов, знаков препинания, допускаются буквы латиницы, должен содержать не менее 2 и не более 20 символов)");
            signUpButton.setDisable(true);
            return;
        } else {
            errorMsgText.setText("");
        }
        signUpButton.setDisable(false);
    }

}
