package controllers;

import gsonParser.GSONParser;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import personalInfo.User;
import serverMSG.ServerMSG;
import serverMSG.ServerMSG.CommandTypes;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EditCurrentUser {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private TextField loginLine;

    @FXML
    private TextField firstNameLine;

    @FXML
    private TextField surnameLine;

    @FXML
    private TextField patronymicLine;

    @FXML
    private TextField phoneLine;

    @FXML
    private PasswordField passwordLine;

    @FXML
    private Button saveButton;

    @FXML
    private Button cancelButton;

    @FXML
    private Label errorMsgText;
    private User loggedUser;
    private Client client;

    @FXML
    void closeDialog(ActionEvent event) {
        Stage dialogStage = (Stage) cancelButton.getScene().getWindow();
        dialogStage.close();
    }

    @FXML
    void saveUser(ActionEvent event) {
        try{
            GSONParser<User> parser = new GSONParser<>();
            loggedUser.setPasswordMask(passwordLine.getText());
            loggedUser.setFirstName(firstNameLine.getText());
            loggedUser.setSurname(surnameLine.getText());
            loggedUser.setPatronymic(patronymicLine.getText());
            loggedUser.setPhone(phoneLine.getText());
            ServerMSG serverMSG = new ServerMSG(loggedUser, CommandTypes.EDITUSER, parser.getString(loggedUser));
            client.sendMessage(serverMSG);
            client.sendMessage(serverMSG);
            closeDialog(event);
        }
        catch (Exception e) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Ошибка соединения с сервером");
            alert.setHeaderText(null);
            alert.setContentText("Пожалуйста, проверьте подключение к серверу.");
            alert.showAndWait();
        }
    }

    public void setPerson(Client client, User loggedUser) {
        this.client = client;
        this.loggedUser = loggedUser;
        loginLine.setDisable(true);
        loginLine.setText(loggedUser.getLogin());
        firstNameLine.setText(loggedUser.getFirstName());
        surnameLine.setText(loggedUser.getSurname());
        patronymicLine.setText(loggedUser.getPatronymic());
        phoneLine.setText(loggedUser.getPhone());
    }

    @FXML
    void initialize() {
        loginLine.textProperty().addListener((observable, oldValue, newValue) -> {
            checkRegistration();
        });
        passwordLine.textProperty().addListener((observable, oldValue, newValue) -> {
            checkRegistration();
        });
        firstNameLine.textProperty().addListener((observable, oldValue, newValue) -> {
            checkRegistration();
        });
        surnameLine.textProperty().addListener((observable, oldValue, newValue) -> {
            checkRegistration();
        });
        patronymicLine.textProperty().addListener((observable, oldValue, newValue) -> {
            checkRegistration();
        });
        phoneLine.textProperty().addListener((observable, oldValue, newValue) -> {
            checkRegistration();
        });
        errorMsgText.setWrapText(true);
    }

    private void checkRegistration() {
        Pattern pattern = Pattern.compile(
                "^[0-9a-zA-Z]+([0-9a-zA-Z]*[-._+])*[0-9a-zA-Z]+@[0-9a-zA-Z]+([-.][0-9a-zA-Z]+)*([0-9a-zA-Z]*[.])[a-zA-Z]{2,6}$");
        Matcher matcher = pattern.matcher(loginLine.getText());
        if (!matcher.matches()) {
            errorMsgText.setText("Некорректно введена почта! (не должна содержать пробелов)");
            saveButton.setDisable(true);
            return;
        } else {
            errorMsgText.setText("");
        }

        Pattern passwordPattern = Pattern.compile("[a-zA-Z0-9]{2,20}");
        matcher = passwordPattern.matcher(passwordLine.getText());
        if (!matcher.matches()) {
            errorMsgText.setText(
                    "Некорректно введен пароль! (не должен содержать пробелов, знаков препинания, должен содержать не менее 2 и не более 20 символов)");
            saveButton.setDisable(true);
            return;
        } else {
            errorMsgText.setText("");
        }

        Pattern personalInfoPattern = Pattern.compile("[А-я]{2,20}");
        matcher = personalInfoPattern.matcher(firstNameLine.getText());
        if (!matcher.matches()) {
            errorMsgText.setText(
                    "Некорректно введено имя! (должно содержать только символы русского алфавита,без пробелов и знаков препинания)");
            saveButton.setDisable(true);
            return;
        } else {
            errorMsgText.setText("");
        }

        matcher = personalInfoPattern.matcher(surnameLine.getText());
        if (!matcher.matches()) {
            errorMsgText.setText(
                    "Некорректно введена фамилия! (должна содержать только символы русского алфавита, без пробелов и знаков препинания)");
            saveButton.setDisable(true);
            return;
        } else {
            errorMsgText.setText("");
        }

        matcher = personalInfoPattern.matcher(patronymicLine.getText());
        if (!matcher.matches()) {
            errorMsgText.setText(
                    "Некорректно введено отчество! (должно содержать только символы русского алфавита, без пробелов и знаков препинания)");
            saveButton.setDisable(true);
            return;
        } else {
            errorMsgText.setText("");
        }

        Pattern phonePattern = Pattern.compile("[0-9]{11}");
        matcher = phonePattern.matcher(phoneLine.getText());
        if (!matcher.matches()) {
            errorMsgText.setText(
                    "Номер телефона не введено или введено неверно! (должен быть записан в формате *** *** ****: 11 цифр)");
            saveButton.setDisable(true);
            return;
        } else {
            errorMsgText.setText("");
        }
        saveButton.setDisable(false);
    }
}
