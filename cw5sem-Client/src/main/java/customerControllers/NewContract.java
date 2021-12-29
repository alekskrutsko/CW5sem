package customerControllers;

import appraisal.AppraisalObject;
import appraisal.Contract;
import com.google.gson.reflect.TypeToken;
import appraisal.Contract.Status;
import controllers.Client;
import gsonParser.GSONParser;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import personalInfo.User;
import serverMSG.ServerMSG;
import serverMSG.ServerMSG.CommandTypes;

import java.lang.reflect.Type;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NewContract {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private TextField expectedPriceLine;


    @FXML
    private TextArea commentFromConsumer;

    @FXML
    private Label objectText;

    @FXML
    private Label errorMSG;

    @FXML
    private Button cancelButton;

    @FXML
    private Button saveButton;

    @FXML
    private TableView<AppraisalObject> objectsTable;

//    @FXML
//    private TableColumn<InvestmentObject, Integer> objectIDColumn;

    @FXML
    private TableColumn<AppraisalObject, String> objectNameColumn;

    @FXML
    private TableColumn<AppraisalObject, String> descriptionColumn;

    private AppraisalObject appraisalObject;

    private User loggedUser;

    private Client client;

    @FXML
    void cancel(ActionEvent event) {
        Stage dialogStage = (Stage) cancelButton.getScene().getWindow();
        dialogStage.close();
    }

    @FXML
    void save(ActionEvent event) {
        try{
            Contract contract = new Contract(appraisalObject, Double.valueOf(expectedPriceLine.getText()),
                                             String.valueOf(commentFromConsumer.getText()),
                                             Status.NEW, loggedUser);

            GSONParser<Contract> parser = new GSONParser<>();
            ServerMSG serverMSG = new ServerMSG(loggedUser, CommandTypes.NEWCONTRACT, parser.getString(contract));
            client.sendMessage(serverMSG);

            cancel(event);
        } catch (Exception e) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Ошибка соединения с сервером");
            alert.setHeaderText(null);
            alert.setContentText("Пожалуйста, проверьте подключение к серверу.");
            alert.showAndWait();
        }
    }

    public void setPerson(User loggedUser, Client client) {
        this.loggedUser = loggedUser;
        this.client = client;
        fillContractsTable();
    }

    private void showObject(AppraisalObject appraisalObject) {
        if (appraisalObject != null) {
            this.appraisalObject = appraisalObject;
            objectText.setText(appraisalObject.getName());
            checkObjectToAppraise();
        } else {
            objectText.setText("");
        }
    }

    private void fillContractsTable() {
        try {
            objectsTable.getItems().clear();

            ServerMSG serverMSG = new ServerMSG(loggedUser, CommandTypes.GETOBJECTS, null);
            ServerMSG answer = client.sendMessage(serverMSG);

            GSONParser<Vector<AppraisalObject>> answerParser = new GSONParser<>();
            Vector<AppraisalObject> appraisalObjects = new Vector<>();

            Type type = new TypeToken<Vector<AppraisalObject>>() {
            }.getType();

            appraisalObjects = answerParser.getObject(answer.getData(), type);

            if (appraisalObjects.isEmpty()) {
                return;
            }

            for (AppraisalObject appraisalObject : appraisalObjects) {
                objectsTable.getItems().add(appraisalObject);
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
    void initialize() {
        errorMSG.setWrapText(true);
        saveButton.setDisable(true);

        objectNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));

        showObject(null);
        objectsTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> showObject(newValue));

        expectedPriceLine.textProperty().addListener((observable, oldValue, newValue) -> {
            checkObjectToAppraise();
        });

//        dateOfRealisationLine.valueProperty().addListener((observable, oldValue, newValue) -> {
//            checkDate();
//        });

//        dateOfRealisationLine.setEditable(false);
//
//        dateOfRealisationLine.setValue(LocalDate.now().plusDays(30));

    }

//    private void checkDate() {
//        LocalDate date = LocalDate.now();
//        if (dateOfRealisationLine.getValue().isBefore(date.plusDays(30))) {
//            errorMSG.setText("Дата срока инвестирования не может быть раньше текущей даты + 30 дней!");
//            return;
//        }
//        errorMSG.setText("");
//    }

    private void checkObjectToAppraise() {
        Pattern pattern = Pattern.compile(
                "^[0-9]{1,10}+[.,]?[0-9]{0,2}+$");
        Matcher matcher = pattern.matcher(expectedPriceLine.getText());
        if (!matcher.matches()) {
            errorMSG.setText("Неккоректно введены ценовые ожидания заказчика!");
            saveButton.setDisable(true);
            return;
        }
        errorMSG.setText("");
        if (appraisalObject != null) {
            saveButton.setDisable(false);
        } else {
            errorMSG.setText("Не выбран объект для оценки!");
        }
    }
}
