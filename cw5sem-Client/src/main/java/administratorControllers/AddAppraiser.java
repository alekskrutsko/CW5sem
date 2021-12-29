package administratorControllers;

import com.google.gson.reflect.TypeToken;
import controllers.Client;
import gsonParser.GSONParser;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import personalInfo.Appraiser;
import personalInfo.User;
import personalInfo.User.Role;
import serverMSG.ServerMSG;
import serverMSG.ServerMSG.CommandTypes;

import java.lang.reflect.Type;
import java.net.URL;
import java.util.HashMap;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddAppraiser {

    private Client client;

    private User loggedUser;

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private TableView<User> users;

    @FXML
    private TableColumn<User, String> firstNameColumn;

    @FXML
    private TableColumn<User, String> surnameColumn;

    @FXML
    private TableColumn<User, String> patronymicColumn;

    @FXML
    private TableColumn<User, String> phoneColumn;

    @FXML
    private Button addButton;

    @FXML
    private Button cancelButton;

    @FXML
    private Label loginText;

    @FXML
    private Label firstNameText;

    @FXML
    private Label surnameText;

    @FXML
    private Label patronymicText;

    @FXML
    private Label phoneText;

    @FXML
    private Label statusText;

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
    private ChoiceBox<String> blockList;

    @FXML
    void addAppraiser(ActionEvent event) {
        try {
            int selectedIndex = users.getSelectionModel().getSelectedIndex();
            if (selectedIndex >= 0) {
                TextInputDialog dialog = new TextInputDialog("");
                dialog.setTitle("Ввод зарплаты сотрудника");
                dialog.setHeaderText(null);
                dialog.setContentText("Пожалуйста, введите зарплату сотрудника:");

                Optional<String> result = dialog.showAndWait();
                if (result.isPresent()) {
                    if (salaryIsCorrect(result.get())) {
                        Appraiser appraiser = new Appraiser();
                        appraiser.setUser(users.getSelectionModel().getSelectedItem());
                        appraiser.setSalary(Double.valueOf(result.get()));

                        GSONParser<Appraiser> parser = new GSONParser<Appraiser>();
                        ServerMSG serverMSG =
                                new ServerMSG(loggedUser, CommandTypes.NEWAPPRAISER, parser.getString(appraiser));
                        client.sendMessage(serverMSG);
                    } else {
                        Alert alert = new Alert(AlertType.WARNING);
                        alert.setTitle("Ошибка добаления оценщика");
                        alert.setHeaderText("Некорректная заработная плата!");
                        alert.setContentText("Пожалуйста, введите корректную заработную плату.");
                        alert.showAndWait();
                    }
                }
                users.getItems().clear();
                getUsers();
            } else {
                // Ничего не выбрано.
                Alert alert = new Alert(AlertType.WARNING);
                alert.setTitle("Ошибка добаления оценщика");
                alert.setHeaderText("Не выбрана запись!");
                alert.setContentText("Пожалуйста, выберите пользователя из таблицы.");
                alert.showAndWait();
            }
        }
        catch (Exception e){
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Ошибка соединения с сервером");
            alert.setHeaderText(null);
            alert.setContentText("Пожалуйста, проверьте подключение к серверу.");
            alert.showAndWait();
        }
    }

    @FXML
    void cancelClicked(ActionEvent event) {
        Stage dialogStage = (Stage) cancelButton.getScene().getWindow();
        dialogStage.close();
    }

    private void getUsers() {
        try {
            ServerMSG serverMSG = new ServerMSG(loggedUser, CommandTypes.GETCONSUMERS, null);
            filter();
        }
        catch (Exception e){
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Ошибка соединения с сервером");
            alert.setHeaderText(null);
            alert.setContentText("Пожалуйста, проверьте подключение к серверу.");
            alert.showAndWait();
        }
    }

    private void filter() {
        try {
//            String login, firstName, surname, patronymic;
            boolean status = false;
            boolean any = false;
            if (blockList.getSelectionModel().getSelectedItem().equals("Активный")) {
                status = false;
            } else if (blockList.getSelectionModel().getSelectedItem().equals("Заблокирован")) {
                status = true;
            } else {
                any = true;
            }

            User filter = new User(loginLine.getText(), "", this.firstNameLine.getText(), surnameLine.getText(),
                                   this.patronymicLine.getText(), this.phoneLine.getText(), Role.CONSUMER, status);

            HashMap<Boolean, User> filterAndUser = new HashMap<>();
            filterAndUser.put(any, filter);

            GSONParser<HashMap<Boolean, User>> parser = new GSONParser<>();
            ServerMSG serverMSG =
                    new ServerMSG(loggedUser, CommandTypes.FILTERUSERS, parser.getString(filterAndUser));
            fillInTable(serverMSG);
        }
        catch (Exception e){
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Ошибка соединения с сервером");
            alert.setHeaderText(null);
            alert.setContentText("Пожалуйста, проверьте подключение к серверу.");
            alert.showAndWait();
        }
    }

    private void fillInTable(ServerMSG serverMSG) {
        try {
            users.getItems().clear();
            ServerMSG answer = client.sendMessage(serverMSG);
            GSONParser<Vector<User>> filterParser = new GSONParser<>();
            Vector<User> userVector = new Vector<>();
            Type type = new TypeToken<Vector<User>>() {}.getType();
            userVector.addAll(filterParser.getObject(answer.getData(), type));
            if(userVector == null){
                return;
            }
            for (User user : userVector) {
                users.getItems().add(new User(user.getLogin(), user.getPasswordMask(),
                                              user.getFirstName(), user.getSurname(),
                                              user.getPatronymic(), user.getPhone(), user.getRole(),
                                              user.isBlocked()));
            }
        }
        catch (Exception e){
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Ошибка соединения с сервером");
            alert.setHeaderText(null);
            alert.setContentText("Пожалуйста, проверьте подключение к серверу.");
            alert.showAndWait();
        }
    }

    private boolean salaryIsCorrect(String salary) {
        Pattern pattern = Pattern.compile(
                "^[0-9]{1,10}+[.]?[0-9]{0,2}+$");
        Matcher matcher = pattern.matcher(salary);
        return matcher.matches();
    }

    public void setPerson(Client client, User loggedUser) {
        this.client = client;
        this.loggedUser = loggedUser;
        getUsers();
    }

    private void showUserDetails(User user) {
        addButton.setDisable(user == null);
        if (user != null) {
            loginText.setText(user.getLogin());
            firstNameText.setText(user.getFirstName());
            surnameText.setText(user.getSurname());
            patronymicText.setText(user.getPatronymic());
            phoneText.setText(user.getPhone());
            if (user.isBlocked()) {
                statusText.setText("Заблокирован");
                statusText.setTextFill(Color.color(1, 0, 0));
            } else {
                statusText.setText("Активный");
                statusText.setTextFill(Color.color(0, 1, 0));
            }

        } else {
            loginText.setText("");
            firstNameText.setText("");
            surnameText.setText("");
            patronymicText.setText("");
            phoneText.setText("");
            statusText.setText("");
        }
    }

    @FXML
    void initialize() {
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        surnameColumn.setCellValueFactory(new PropertyValueFactory<>("surname"));
        patronymicColumn.setCellValueFactory(new PropertyValueFactory<>("patronymic"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));

        showUserDetails(null);

        ObservableList<String> availableChoices =
                FXCollections.observableArrayList("Любой", "Активный", "Заблокирован");
        blockList.setItems(availableChoices);
        blockList.getSelectionModel().selectFirst();
        loginLine.textProperty().addListener((observable, oldValue, newValue) -> {
            filter();
        });
        firstNameLine.textProperty().addListener((observable, oldValue, newValue) -> {
            filter();
        });
        surnameLine.textProperty().addListener((observable, oldValue, newValue) -> {
            filter();
        });
        patronymicLine.textProperty().addListener((observable, oldValue, newValue) -> {
            filter();
        });
        phoneLine.textProperty().addListener((observable, oldValue, newValue) -> {
            filter();
        });
        blockList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            filter();
        });
        users.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> showUserDetails(newValue));
    }
}
