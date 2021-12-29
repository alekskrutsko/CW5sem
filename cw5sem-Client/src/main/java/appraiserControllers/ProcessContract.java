package appraiserControllers;

import com.google.gson.reflect.TypeToken;
import controllers.Client;
import gsonParser.GSONParser;
import appraisal.Contract;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import personalInfo.Appraiser;
import personalInfo.User;
import serverMSG.ServerMSG;
import serverMSG.ServerMSG.CommandTypes;

import java.lang.reflect.Type;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProcessContract {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Label appraisalObjectText;

    @FXML
    private Label expectedPriceText;

    @FXML
    private Label consumerText;

    @FXML
    private Label appraiserText;

    @FXML
    private Label commentFromConsumerText;

    @FXML
    private TextArea commentFromAppraiserLine;

    @FXML
    private TextField appraiserPriceLine;

    @FXML
    private TextField priceForAppraisalLine;

    @FXML
    private Button cancelButton;

    @FXML
    private Button saveButton;

    @FXML
    private Label errorMSG;

    private Client client;

    private User loggedUser;

    private Contract contract;

    Appraiser appraiser;

    public void setData(Contract contract, Client client, User loggedUser) {
        try{
            this.client = client;
            this.loggedUser = loggedUser;
            this.contract = contract;

            appraisalObjectText.setText(contract.getAppraisalObject().getName());
            expectedPriceText.setText(String.valueOf(contract.getExpectedPrice()));


            consumerText
                    .setText(contract.getConsumer().getSurname() + " " + contract.getConsumer().getFirstName() + " " +
                             contract.getConsumer().getPatronymic());

            appraiserText.setText(loggedUser.getSurname() + " " + loggedUser.getFirstName() + " " +
                               loggedUser.getPatronymic());

            if (contract.getCommentFromConsumer() == null) {
                commentFromConsumerText.setText("Нет");
            } else {
                commentFromConsumerText.setText(contract.getCommentFromConsumer());
            }

            ServerMSG serverMSG = new ServerMSG(loggedUser, CommandTypes.GETAPPRAISERS, null);
            ServerMSG answer = client.sendMessage(serverMSG);
            GSONParser<Vector<Appraiser>> parser = new GSONParser<>();
            Vector<Appraiser> appraisers = new Vector<>();

            Type type = new TypeToken<Vector<Appraiser>>() {}.getType();

            appraisers.addAll(parser.getObject(answer.getData(), type));

            for (Appraiser bufAppraiser : appraisers) {
                if (bufAppraiser.getLogin().equals(loggedUser.getLogin())) {
                    appraiser = bufAppraiser;
                    break;
                }
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
    void cancel(ActionEvent event) {
        Stage dialogStage = (Stage) cancelButton.getScene().getWindow();
        dialogStage.close();
    }

    @FXML
    void save(ActionEvent event) {
        try{
            contract.setAppraiserPrice(Double.valueOf(appraiserPriceLine.getText()));
            contract.setPriceForAppraisal(Double.valueOf(priceForAppraisalLine.getText()));
            contract.setCommentFromAppraiser(commentFromAppraiserLine.getText());

            GSONParser<Contract> parser = new GSONParser<>();
            ServerMSG serverMSG =
                    new ServerMSG(loggedUser, CommandTypes.CONTRACTWAITFORCONSUMER, parser.getString(contract));
            client.sendMessage(serverMSG);
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

    @FXML
    void initialize() {
        appraiserPriceLine.textProperty().addListener((observable, oldValue, newValue) -> {
            check();
        });
        priceForAppraisalLine.textProperty().addListener((observable, oldValue, newValue) -> {
            check1();
        });
    }

    private void check() {
        //interest rate check
        Pattern pattern = Pattern.compile(
                "^[0-9]{1,10}+[.]?[0-9]{0,2}+$");
        Matcher matcher = pattern.matcher(appraiserPriceLine.getText());
        if (!matcher.matches()) {
            errorMSG.setText(
                    "Некорректно введена стоимость предложенная оценщиком!");
            saveButton.setDisable(true);
            return;
        }

        contract.setAppraiserPrice(Double.valueOf(appraiserPriceLine.getText()));
        saveButton.setDisable(false);
        errorMSG.setText("");
    }

    private void check1() {
        //interest rate check
        Pattern pattern = Pattern.compile(
                "^[0-9]{1,10}+[.]?[0-9]{0,2}+$");
        Matcher matcher = pattern.matcher(priceForAppraisalLine.getText());
        if (!matcher.matches()) {
            errorMSG.setText(
                    "Некорректно введена стоимость за оценку объекта!");
            saveButton.setDisable(true);
            return;
        }

        contract.setAppraiserPrice(Double.valueOf(priceForAppraisalLine.getText()));
        saveButton.setDisable(false);
        errorMSG.setText("");
    }

    public void setContract(Contract contract) {
        this.contract = contract;
        contract.setAppraiser(appraiser);
        check();
    }
}
