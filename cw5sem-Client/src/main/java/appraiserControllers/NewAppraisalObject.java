package appraiserControllers;

import appraisal.AppraisalObject;
import com.google.gson.reflect.TypeToken;
import controllers.Client;
import gsonParser.GSONParser;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import personalInfo.User;
import serverMSG.ServerMSG;
import serverMSG.ServerMSG.CommandTypes;

import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NewAppraisalObject {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private ChoiceBox<String> blockList;

//    @FXML
//    private Spinner<Integer> strategyNumberLine;
//    private TextField objectNameLine;

    @FXML
    private TextArea descriptionLine;

    @FXML
    private Button cancelButton;

    @FXML
    private Button saveButton;

    @FXML
    private Button strategiesButton;

    @FXML
    private Label errorMSG;

    private Client client;

    private User loggedUser;

    private AppraisalObject appraisalObject;

    private boolean isUpdate = false;

    private boolean wasEdited = true;

    //private Vector<Strategy> strategies = new Vector<Strategy>();

    public void setObject(AppraisalObject appraisalObject) {
        this.appraisalObject = appraisalObject;
        blockList.getSelectionModel().select(appraisalObject.getName());
        descriptionLine.setText(appraisalObject.getDescription());

        blockList.setDisable(true);

        errorMSG.setText("");

        saveButton.setDisable(false);

        isUpdate = true;

    }

    @FXML
    void cancel(ActionEvent event) {
        Stage dialogStage = (Stage) cancelButton.getScene().getWindow();
        dialogStage.close();
    }

    @FXML
    void saveObject(ActionEvent event) {
        try {
            if(isUpdate) {

                appraisalObject.setName(blockList.getSelectionModel().getSelectedItem());
                appraisalObject.setDescription(descriptionLine.getText());
            }
            else {
                appraisalObject = new AppraisalObject(blockList.getSelectionModel().getSelectedItem(), descriptionLine.getText());
            }
            GSONParser<AppraisalObject> parser = new GSONParser<>();
            ServerMSG serverMSG;
            if (isUpdate) {
                serverMSG = new ServerMSG(loggedUser, CommandTypes.EDITOBJECT, parser.getString(appraisalObject));
            } else {
                serverMSG = new ServerMSG(loggedUser, CommandTypes.ADDOBJECT, parser.getString(appraisalObject));
            }
            ServerMSG answer = client.sendMessage(serverMSG);
            if (answer.getData().equals("error")) {
                errorMSG.setText("Объект с таким названием и описанием уже существует!");
                return;
            }
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

    public void check() {
        Pattern pattern = Pattern.compile("[0-9а-яА-Я.() ]{3,100}");
        Matcher matcher = pattern.matcher(descriptionLine.getText());
        if (!matcher.matches()) {
            errorMSG.setText(
                    "Некорректно введено название объекта для оценки! (допускаются символы русского алфавита, количество символов - от 3 до 100)");
            saveButton.setDisable(true);
            return;
        }
        errorMSG.setText("");
        saveButton.setDisable(false);
    }

    public void setClient(Client client, User loggedUser) {
        this.client = client;
        this.loggedUser = loggedUser;
    }

    @FXML
    void initialize() {
        descriptionLine.textProperty().addListener((observable, oldValue, newValue) -> {
            check();
        });
        Client client = new Client();
        client.startConnection("127.0.0.1", 3000);
        ServerMSG serverMSG =
                new ServerMSG(loggedUser, CommandTypes.GETOBJECTSTYPES, "");

        ServerMSG answer = client.sendMessage(serverMSG);
        GSONParser<ArrayList<String>> parser = new GSONParser<>();
        ArrayList<String> objectsTypes = new ArrayList<>();
        Type type = new TypeToken<ArrayList<String>>() {
        }.getType();
        objectsTypes.addAll(parser.getObject(answer.getData(), type));

        ObservableList<String> availableChoices =
                FXCollections.observableArrayList();

        availableChoices.addAll(objectsTypes);
        blockList.setItems(availableChoices);
        blockList.getSelectionModel().selectFirst();
//        blockList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
//            ServerMSG serverMSG1 = new ServerMSG(loggedUser, CommandTypes.ADDOBJECTTYPE, blockList.getSelectionModel().getSelectedItem());
//            client.sendMessage(serverMSG1);
//        });

        errorMSG.setWrapText(true);
    }
}
