package administratorControllers;

import controllers.Client;
import gsonParser.GSONParser;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import personalInfo.Appraiser;
import personalInfo.User;
import serverMSG.ServerMSG;
import serverMSG.ServerMSG.CommandTypes;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EditAppraiser {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private ChoiceBox<String> blockList;

    @FXML
    private TextField salaryText;

    @FXML
    private Label errorMsg;

    @FXML
    private Button cancelButton;

    @FXML
    private Button saveButton;

    private Appraiser appraiser;

    private Client client;

    private User loggedUser;

    @FXML
    void cancel(ActionEvent event) {
        Stage dialogStage = (Stage) cancelButton.getScene().getWindow();
        dialogStage.close();
    }

    @FXML
    void saveEditedAppraiser(ActionEvent event) {
        try{
            GSONParser<Appraiser> parser = new GSONParser<Appraiser>();
            appraiser.setSalary(Double.valueOf(salaryText.getText()));
            if(blockList.getSelectionModel().getSelectedItem().equals("Заблокирован")){
                appraiser.setBlocked(true);
            }
            else{
                appraiser.setBlocked(false);
            }
            ServerMSG serverMSG = new ServerMSG(loggedUser, CommandTypes.EDITAPPRAISER, parser.getString(appraiser));
            client.sendMessage(serverMSG);
            ServerMSG answer = client.sendMessage(serverMSG);
            cancel(event);
        }
        catch (Exception e) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Ошибка соединения с сервером");
            alert.setHeaderText(null);
            alert.setContentText("Пожалуйста, проверьте подключение к серверу.");
            alert.showAndWait();
        }
    }

    public void setPerson(Appraiser appraiser, Client client, User loggedUser){
        this.appraiser = appraiser;
        this.client = client;
        this.loggedUser = loggedUser;
        salaryText.setText(String.valueOf(appraiser.getSalary()));
        if(appraiser.isBlocked()){
            blockList.getSelectionModel().selectLast();
        }
        else{
            blockList.getSelectionModel().selectFirst();
        }
    }

    private void checkSalary(){
        Pattern pattern = Pattern.compile(
                "^[0-9]{1,10}+[.,]?[0-9]{0,2}+$");
        Matcher matcher = pattern.matcher(salaryText.getText());
        if (!matcher.matches()) {
            errorMsg.setText("Некорректно введена зарплата! (число должно быть дробным с 2 знаками после запятой)");
            saveButton.setDisable(true);
            return;
        } else {
            errorMsg.setText("");
        }
        saveButton.setDisable(false);
    }

    @FXML
    void initialize() {
        ObservableList<String> availableChoices = FXCollections.observableArrayList("Активный", "Заблокирован");
        blockList.setItems(availableChoices);
        salaryText.textProperty().addListener((observable, oldValue, newValue) -> {
            checkSalary();
        });
    }
}
