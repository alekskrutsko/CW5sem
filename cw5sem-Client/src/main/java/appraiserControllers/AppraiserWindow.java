package appraiserControllers;

import administratorControllers.AdminWindow;
import appraisal.AppraisalObject;
import appraisal.Contract;
import gsonParser.GSONParser;
import com.google.gson.reflect.TypeToken;
import controllers.Client;
import controllers.EditCurrentUser;
import appraisal.Contract.Status;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import personalInfo.User;
import personalInfo.User.Role;
import personalInfo.UserTable;
import serverMSG.ServerMSG;
import serverMSG.ServerMSG.CommandTypes;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Optional;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AppraiserWindow {

//    @FXML
//    private ResourceBundle resources;
//
//    @FXML
//    private URL location;

    @FXML
    private TableView<Contract> contractsTable;

    @FXML
    private TableColumn<Contract, String> appraisalObjectColumn;

    @FXML
    private TableColumn<Contract, Double> expectedPriceColumn;

    @FXML
    private TableColumn<Contract, Double> priceForAppraisalColumn;

    @FXML
    private TableColumn<Contract, String> contractStatusColumn;

    @FXML
    private Button rejectContractButton;

    @FXML
    private Button processContractButton;

//    @FXML
//    private Label contractIDText;

    @FXML
    private Label appraisalObjectText;

    @FXML
    private Label expectedPriceText;

    @FXML
    private Label appraiserPriceText;

//    @FXML
//    private Label expectedIncomeText;

    @FXML
    private Label priceForAppraisalText;

    @FXML
    private Label dateOfSigningText;

    @FXML
    private Label statusText;

    @FXML
    private Label consumerText;

    @FXML
    private Label appraiserText;

    @FXML
    private Label commentFromConsumerText;

    @FXML
    private Label commentFromAppraiserText;

    @FXML
    private TableView<AppraisalObject> objectsToAppraiseTable;

    @FXML
    private TableColumn<AppraisalObject, String> objectNameColumn;

//    @FXML
//    private TableColumn<AppraisalObject, Integer> strategyNumberColumn;

    @FXML
    private TableColumn<AppraisalObject, String> descriptionColumn;

    @FXML
    private TextField objectNameLine;

    @FXML
    private TextArea descriptionLine;

    @FXML
    private Button deleteObjectButton;

    @FXML
    private Button editObjectButton;

    @FXML
    private Button addObjectButton;

    @FXML
    private Button addTypeButton;

    @FXML
    private Button deleteTypeButton;

    @FXML
    private Label currentLoginText;

    @FXML
    private Label currentFirstNameText;

    @FXML
    private Label currentSurnameText;

    @FXML
    private Label currentPatronymicText;

    @FXML
    private Label currentPhoneText;

    @FXML
    private Label currentRoleText;

    @FXML
    private Button editPersonalInfoButton;

    @FXML
    private TableView<UserTable> appraisersTable;

    @FXML
    private TableColumn<UserTable, String> appraiserLoginColumn;

    @FXML
    private TableColumn<UserTable, String> appraiserFirstNameColumn;

    @FXML
    private TableColumn<UserTable, String> appraiserSurnameColumn;

    @FXML
    private TableColumn<UserTable, String> appraiserPatronymicColumn;

    @FXML
    private TableColumn<UserTable, String> appraiserPhoneColumn;

    @FXML
    private TableColumn<UserTable, String> appraiserStatusColumn;

    @FXML
    private TextField filterLogin;

    @FXML
    private TextField filterFirstName;

    @FXML
    private TextField filterSurname;

    @FXML
    private TextField filterPatronymic;

    @FXML
    private TextField filterPhone;

    @FXML
    private ChoiceBox<String> filterStatus;

    @FXML
    private TextField filterContractObject;

    @FXML
    private TextField filterAppraiserPrice;

    @FXML
    private TextField filterPriceForAppraisal;

//    @FXML
//    private Button toFileButton;

    @FXML
    private TableColumn<Contract, String> filterObjectColumn;

    @FXML
    private TableView<Contract> viewContractsTable;

    @FXML
    private TableColumn<Contract, Double> filterExpectedPriceColumn;

    @FXML
    private TableColumn<Contract, Double> filterAppraiserPriceColumn;

    @FXML
    private TableColumn<Contract, String> filterPriceForAppraisalColumn;

    @FXML
    private TableColumn<Contract, String> filterDateOfSigningColumn;

    @FXML
    private Button makeReportButton;

    Label caption;

    ToggleGroup radioGroup;

    @FXML
    private Button blockButton;

    private User loggedUser;

    private Client client;

    public void setLoggedUser(User loggedUser) {
        this.loggedUser = loggedUser;
        currentLoginText.setText(loggedUser.getLogin());
        currentFirstNameText.setText(loggedUser.getFirstName());
        currentSurnameText.setText(loggedUser.getSurname());
        currentPatronymicText.setText(loggedUser.getPatronymic());
        currentPhoneText.setText(loggedUser.getPatronymic());
        currentRoleText.setText("Специалист по оценке недвижимости");
        fillInTables();
    }

    public User getLoggedUser() {
        return loggedUser;
    }

    @FXML
    void blockAppraiser(ActionEvent event) {
        try {
            int selectedIndex = appraisersTable.getSelectionModel().getSelectedIndex();
            if (selectedIndex >= 0) {
                String login = appraisersTable.getItems().get(selectedIndex).getLogin();
                Alert alert = new Alert(AlertType.CONFIRMATION);
                alert.setTitle("Подтверждение блокировки/разблокировки");
                alert.setHeaderText(null);
                alert.setContentText("Вы действительно хотите блокировать/разблокировать пользователя?");
                Optional<ButtonType> result = alert.showAndWait();
                if (result.get() == ButtonType.OK) {
                    ServerMSG serverMSG = new ServerMSG(loggedUser, CommandTypes.BLOCKUSER, login);
                    client.sendMessage(serverMSG);
                    fillInTables();
                } else {
                    // ... user chose CANCEL or closed the dialog
                }
            } else {
                // Ничего не выбрано.
                Alert alert = new Alert(AlertType.WARNING);
                alert.setTitle("Ошибка блокировки/разблокировки");
                alert.setHeaderText("Не выбрана запись!");
                alert.setContentText("Пожалуйста, выберите пользователя из таблицы.");
                alert.showAndWait();
            }
        } catch (Exception e) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Ошибка соединения с сервером");
            alert.setHeaderText(null);
            alert.setContentText("Пожалуйста, проверьте подключение к серверу.");
            alert.showAndWait();
        }
    }

    @FXML
    void editPersonalInfo(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(AdminWindow.class.getResource("/admin/editCurrentUser.fxml"));
        Parent root = loader.load();

        EditCurrentUser editWindow = loader.getController();
        editWindow.setPerson(client, loggedUser);
        loader.setController(editWindow);

        Stage dialogStage = new Stage();
        dialogStage.setTitle("Редактирование пользователя");
        dialogStage.setResizable(false);
        dialogStage.initOwner(editPersonalInfoButton.getScene().getWindow());
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.setScene(new Scene(root, 600, 318));
        dialogStage.showAndWait();
        fillInTables();
        currentLoginText.setText(loggedUser.getLogin());
        currentFirstNameText.setText(loggedUser.getFirstName());
        currentSurnameText.setText(loggedUser.getSurname());
        currentPatronymicText.setText(loggedUser.getPatronymic());
        currentPhoneText.setText(loggedUser.getPhone());
        currentRoleText.setText("Специалист по оценочным операциям");
    }

    @FXML
    void addObject(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(AppraiserWindow.class.getResource("/appraiser/newAppraisalObject.fxml"));
        Parent root = loader.load();

        NewAppraisalObject editWindow = loader.getController();
        editWindow.setClient(client, loggedUser);
        loader.setController(editWindow);

        Stage dialogStage = new Stage();
        dialogStage.setResizable(false);
        dialogStage.setTitle("Добавление объекта для оценки");
        dialogStage.initOwner(addObjectButton.getScene().getWindow());
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.setScene(new Scene(root, 681, 426));
        dialogStage.showAndWait();

        fillInTables();
    }

    @FXML
    void addType(ActionEvent event) throws IOException {
        try {
                TextInputDialog dialog = new TextInputDialog("");
                dialog.setTitle("Ввод нового типа");
                dialog.setHeaderText(null);
                dialog.setContentText("Пожалуйста, введите новый тип объекта недвижимости:");

                Optional<String> result = dialog.showAndWait();

            if (result.isPresent()) {
                if (objectTypeIsCorrect(result.get())) {
                    ServerMSG serverMSG =
                            new ServerMSG(loggedUser, CommandTypes.NEWOBJECTTYPE,  result.get());
                    ServerMSG answer = client.sendMessage(serverMSG);
                    if (answer.getData().equals("error")) {
                        Alert alert = new Alert(AlertType.WARNING);
                        alert.setTitle("Ошибка добаления типа объекта");
                        alert.setHeaderText(null);
                        alert.setContentText("Такой тип уже существует");
                        alert.showAndWait();
                    }
                } else {
                    Alert alert = new Alert(AlertType.WARNING);
                    alert.setTitle("Ошибка добаления типа объекта");
                    alert.setHeaderText("Некорректно введён тип!");
                    alert.setContentText("Пожалуйста, вводите только русские символы от 2 до 30.");
                    alert.showAndWait();
                }
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
    void deleteType(ActionEvent event) throws IOException {
        try {
            TextInputDialog dialog = new TextInputDialog("");
            dialog.setTitle("Ввод типа для удаления");
            dialog.setHeaderText(null);
            dialog.setContentText("Пожалуйста, введите тип объекта недвижимости для удаления:");

            Optional<String> result = dialog.showAndWait();

            if (result.isPresent()) {
                if (objectTypeIsCorrect(result.get())) {
                    ServerMSG serverMSG =
                            new ServerMSG(loggedUser, CommandTypes.DELETEOBJECTTYPE,  result.get());
                    ServerMSG answer = client.sendMessage(serverMSG);
                    if (answer.getData().equals("error")) {
                        Alert alert = new Alert(AlertType.WARNING);
                        alert.setTitle("Ошибка удаления типа объекта");
                        alert.setHeaderText(null);
                        alert.setContentText("Такой тип не существует");
                        alert.showAndWait();
                    }
                } else {
                    Alert alert = new Alert(AlertType.WARNING);
                    alert.setTitle("Ошибка добаления типа объекта");
                    alert.setHeaderText("Некорректно введён тип!");
                    alert.setContentText("Пожалуйста, вводите только русские символы от 2 до 30.");
                    alert.showAndWait();
                }
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
    private boolean objectTypeIsCorrect(String type) {
        Pattern pattern = Pattern.compile("[А-я]{2,30}");
        Matcher matcher = pattern.matcher(type);
        return matcher.matches();
    }

    private void startConnection() {
        try {
            client = new Client();
            client.startConnection("127.0.0.1", 3000);
        } catch (Exception e) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Ошибка соединения с сервером");
            alert.setHeaderText(null);
            alert.setContentText("Пожалуйста, проверьте подключение к серверу.");
            alert.showAndWait();
        }
    }

    @FXML
    void deleteObject(ActionEvent event) {
        try {
            int selectedIndex = objectsToAppraiseTable.getSelectionModel().getSelectedIndex();
            if (selectedIndex >= 0) {
                Integer objectID = objectsToAppraiseTable.getItems().get(selectedIndex).getID();
                Alert alert = new Alert(AlertType.CONFIRMATION);
                alert.setTitle("Подтверждение удаления");
                alert.setHeaderText(null);
                alert.setContentText("Вы действительно хотите удалить объект?");
                Optional<ButtonType> result = alert.showAndWait();
                if (result.get() == ButtonType.OK) {
                    ServerMSG serverMSG =
                            new ServerMSG(loggedUser, CommandTypes.DELETEOBJECT, String.valueOf(objectID));
                    client.sendMessage(serverMSG);
                    fillInTables();
                } else {
                    // ... user chose CANCEL or closed the dialog
                }
            } else {
                // Ничего не выбрано.
                Alert alert = new Alert(AlertType.WARNING);
                alert.setTitle("Ошибка удаления");
                alert.setHeaderText("Не выбрана запись!");
                alert.setContentText("Пожалуйста, выберите объект из таблицы.");
                alert.showAndWait();
            }
        } catch (Exception e) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Ошибка соединения с сервером");
            alert.setHeaderText(null);
            alert.setContentText("Пожалуйста, проверьте подключение к серверу.");
            alert.showAndWait();
        }
    }

    @FXML
    void editObject(ActionEvent event) throws IOException {
        try {
            int selectedIndex = objectsToAppraiseTable.getSelectionModel().getSelectedIndex();
            if (selectedIndex >= 0) {
                try {

                    FXMLLoader loader = new FXMLLoader();
                    loader.setLocation(AppraiserWindow.class.getResource("/appraiser/newAppraisalObject.fxml"));
                    Parent root =  loader.load();

                    NewAppraisalObject editWindow = loader.getController();
                    editWindow.setClient(client, loggedUser);
                    editWindow.setObject(objectsToAppraiseTable.getSelectionModel().getSelectedItem());
                    loader.setController(editWindow);

                    Stage dialogStage = new Stage();
                    dialogStage.setResizable(false);
                    dialogStage.setTitle("Редактирование объекта для оценки");
                    dialogStage.initOwner(addObjectButton.getScene().getWindow());
                    dialogStage.initModality(Modality.WINDOW_MODAL);
                    dialogStage.setScene(new Scene(root, 681, 426));
                    dialogStage.showAndWait();

                    fillInTables();
                } catch (IOException e) {
                    System.out.println(e);
                }
            } else {
                // Ничего не выбрано.
                Alert alert = new Alert(AlertType.WARNING);
                alert.setTitle("Ошибка изменения");
                alert.setHeaderText("Не выбрана запись!");
                alert.setContentText("Пожалуйста, выберите объект из таблицы.");
                alert.showAndWait();
            }
        } catch (Exception e) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Ошибка соединения с сервером");
            alert.setHeaderText(null);
            alert.setContentText("Пожалуйста, проверьте подключение к серверу.");
            alert.showAndWait();
        }
    }

    @FXML
    void processContract(ActionEvent event) {
        try {
            int selectedIndex = contractsTable.getSelectionModel().getSelectedIndex();
            if (selectedIndex >= 0) {
                try {
                    FXMLLoader loader = new FXMLLoader();
                    loader.setLocation(AppraiserWindow.class.getResource("/appraiser/processContract.fxml"));
                    Parent root = loader.load();

                    ProcessContract editWindow = loader.getController();
                    editWindow.setData(contractsTable.getSelectionModel().getSelectedItem(), client, loggedUser);
                    loader.setController(editWindow);

                    Stage dialogStage = new Stage();
                    dialogStage.setResizable(false);
                    dialogStage.setTitle("Обработка договора на оценку недвижимости");
                    dialogStage.initOwner(processContractButton.getScene().getWindow());
                    dialogStage.initModality(Modality.WINDOW_MODAL);
                    dialogStage.setScene(new Scene(root, 860, 580));
                    dialogStage.showAndWait();
                    fillInTables();
                } catch (IOException e) {
                    System.out.println(e);
                }
            } else {
                // Ничего не выбрано.
                Alert alert = new Alert(AlertType.WARNING);
                alert.setTitle("Ошибка обработки договора на оценку недвижимости");
                alert.setHeaderText("Не выбрана запись!");
                alert.setContentText("Пожалуйста, выберите договор на оценку недвижимости из таблицы.");
                alert.showAndWait();
            }
        } catch (Exception e) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Ошибка соединения с сервером");
            alert.setHeaderText(null);
            alert.setContentText("Пожалуйста, проверьте подключение к серверу.");
            alert.showAndWait();
        }
    }

    @FXML
    void rejectContract(ActionEvent event) {
        try {
            int selectedIndex = contractsTable.getSelectionModel().getSelectedIndex();
            if (selectedIndex >= 0) {
                TextInputDialog dialog = new TextInputDialog("");
                dialog.setTitle("Подтверждение отклонения заявки на договор");
                dialog.setHeaderText("Вы действительно хотите отклонить заявку?");
                dialog.setContentText("Пожалуйста, укажите причину:");

                Optional<String> result = dialog.showAndWait();
                if (result.isPresent()) {
                    contractsTable.getSelectionModel().getSelectedItem().setCommentFromAppraiser(result.get());
                    GSONParser<Contract> parser = new GSONParser<>();
                    ServerMSG serverMSG =
                            new ServerMSG(loggedUser, CommandTypes.CONTRACTTERMINATED,
                                    parser.getString(contractsTable.getSelectionModel().getSelectedItem()));
                    client.sendMessage(serverMSG);
                    fillInTables();
                }
            } else {
                // Ничего не выбрано.
                Alert alert = new Alert(AlertType.WARNING);
                alert.setTitle("Ошибка обработки договора на оценку недвижимости");
                alert.setHeaderText("Не выбрана запись!");
                alert.setContentText("Пожалуйста, выберите договор на оценку недвижимости из таблицы.");
                alert.showAndWait();
            }
        } catch (Exception e) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Ошибка соединения с сервером");
            alert.setHeaderText(null);
            alert.setContentText("Пожалуйста, проверьте подключение к серверу.");
            alert.showAndWait();
        }
    }



    private void filterContracts() {
        try {
            viewContractsTable.getItems().clear();
            AppraisalObject appraisalObject = new AppraisalObject();
            appraisalObject.setName(filterContractObject.getText());
            Integer contractID = null;
            Double priceForAppraisal = null;
            Double expectedPrice = null;

            try {

                if (!filterAppraiserPrice.getText().isEmpty()) {
                    expectedPrice = Double.parseDouble(filterAppraiserPrice.getText());
                }

                if (!filterPriceForAppraisal.getText().isEmpty()) {
                    priceForAppraisal = Double.parseDouble(filterPriceForAppraisal.getText());
                }
            } catch (Exception e) {
                return;
            }
            Contract filter = new Contract(contractID,
                    appraisalObject, expectedPrice, priceForAppraisal,
                    commentFromConsumerText.getText(),
                    commentFromAppraiserText.getText(),
                    Status.SIGNED);
            GSONParser<Contract> parser = new GSONParser<>();
            ServerMSG serverMSG =
                    new ServerMSG(loggedUser, CommandTypes.FILTERCONTRACTS, parser.getString(filter));

            ServerMSG answer = client.sendMessage(serverMSG);
            GSONParser<Vector<Contract>> filterParser = new GSONParser<>();
            Vector<Contract> contractVector = new Vector<>();
            Type type = new TypeToken<Vector<Contract>>() {
            }.getType();
            contractVector.addAll(filterParser.getObject(answer.getData(), type));

            if (contractVector == null) {
                return;
            }

            for (Contract contract : contractVector) {
                viewContractsTable.getItems().add(contract);
            }
        } catch (Exception e) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Ошибка соединения с сервером");
            alert.setHeaderText(null);
            alert.setContentText("Пожалуйста, проверьте подключение к серверу.");
            alert.showAndWait();
        }
    }

    @FXML
    void initialize() {
        startConnection();

        objectNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        //strategyNumberColumn.setCellValueFactory(new PropertyValueFactory<>("strategiesNumber"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));

        objectNameLine.textProperty().addListener((observable, oldValue, newValue) -> {
            filterObjects();
        });

        descriptionLine.textProperty().addListener((observable, oldValue, newValue) -> {
            filterObjects();
        });

        appraiserLoginColumn.setCellValueFactory(new PropertyValueFactory<>("login"));
        appraiserFirstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        appraiserSurnameColumn.setCellValueFactory(new PropertyValueFactory<>("surname"));
        appraiserPatronymicColumn.setCellValueFactory(new PropertyValueFactory<>("patronymic"));
        appraiserPhoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
        appraiserStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));


        appraisalObjectColumn.setCellValueFactory(new PropertyValueFactory<>("objectName"));
        expectedPriceColumn.setCellValueFactory(new PropertyValueFactory<>("expectedPrice"));
        priceForAppraisalColumn.setCellValueFactory(new PropertyValueFactory<>("priceForAppraisal"));
        contractStatusColumn.setCellValueFactory(new PropertyValueFactory<>("strStatus"));

        ObservableList<String> availableChoices =
                FXCollections.observableArrayList("Любой", "Активный", "Заблокирован");
        filterStatus.setItems(availableChoices);
        filterStatus.getSelectionModel().selectFirst();

        filterLogin.textProperty().addListener((observable, oldValue, newValue) -> {
            filterUsers();
        });
        filterFirstName.textProperty().addListener((observable, oldValue, newValue) -> {
            filterUsers();
        });
        filterSurname.textProperty().addListener((observable, oldValue, newValue) -> {
            filterUsers();
        });
        filterPatronymic.textProperty().addListener((observable, oldValue, newValue) -> {
            filterUsers();
        });
        filterPhone.textProperty().addListener((observable, oldValue, newValue) -> {
            filterUsers();
        });
        filterStatus.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            filterUsers();
        });

        filterObjectColumn.setCellValueFactory(new PropertyValueFactory<>("objectName"));
        filterExpectedPriceColumn.setCellValueFactory(new PropertyValueFactory<>("expectedPrice"));
        filterAppraiserPriceColumn.setCellValueFactory(new PropertyValueFactory<>("appraiserPrice"));
        filterPriceForAppraisalColumn.setCellValueFactory(new PropertyValueFactory<>("priceForAppraisal"));
        filterDateOfSigningColumn.setCellValueFactory(new PropertyValueFactory<>("dateOfSigning"));


        filterContractObject.textProperty().addListener((observable, oldValue, newValue) -> {
            filterContracts();
        });
        filterAppraiserPrice.textProperty().addListener((observable, oldValue, newValue) -> {
            filterContracts();
        });
        filterPriceForAppraisal.textProperty().addListener((observable, oldValue, newValue) -> {
            filterContracts();
        });

        checkSelectedAppraiser(null);

        appraisersTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> checkSelectedAppraiser(newValue));

        showContractDescription(null);
        contractsTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> showContractDescription(newValue));

        checkSelectedObject(null);
        objectsToAppraiseTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> checkSelectedObject(newValue));

    }

    private void checkSelectedObject(AppraisalObject newValue) {
        deleteObjectButton.setDisable(newValue == null);
        editObjectButton.setDisable(newValue == null);
    }

    private void checkSelectedAppraiser(UserTable newValue) {
        blockButton.setDisable(newValue == null);
    }

    private void filterObjects() {
        try {
            objectsToAppraiseTable.getItems().clear();

            AppraisalObject investmentObject = new AppraisalObject(objectNameLine.getText(),
                    descriptionLine.getText());
            GSONParser<AppraisalObject> parser = new GSONParser<>();
            ServerMSG serverMSG =
                    new ServerMSG(loggedUser, CommandTypes.FILTEROBJECTS, parser.getString(investmentObject));
            ServerMSG answer = client.sendMessage(serverMSG);

            GSONParser<Vector<AppraisalObject>> answerParser = new GSONParser<>();
            Vector<AppraisalObject> appraisalObjects = new Vector<>();

            Type type = new TypeToken<Vector<AppraisalObject>>() {
            }.getType();

            appraisalObjects = answerParser.getObject(answer.getData(), type);

            if (appraisalObjects == null) {
                return;
            }

            for (AppraisalObject bufAppraisalObject : appraisalObjects) {
                objectsToAppraiseTable.getItems().add(bufAppraisalObject);
            }
        } catch (Exception e) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Ошибка соединения с сервером");
            alert.setHeaderText(null);
            alert.setContentText("Пожалуйста, проверьте подключение к серверу.");
            alert.showAndWait();
        }
    }

    private void fillContractsTable() {
        try{
            contractsTable.getItems().clear();

            ServerMSG serverMSG = new ServerMSG(loggedUser, CommandTypes.GETALLCONTRACTS, null);
            ServerMSG answer = client.sendMessage(serverMSG);

            GSONParser<Vector<Contract>> answerParser = new GSONParser<>();
            Vector<Contract> contracts = new Vector<>();

            Type type = new TypeToken<Vector<Contract>>() {
            }.getType();

            contracts = answerParser.getObject(answer.getData(), type);
            if (contracts == null) {
                return;
            }
            if (contracts.isEmpty()) {
                return;
            }
            for (Contract contract : contracts) {
                contractsTable.getItems().add(contract);
            }
        }catch (Exception e) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Ошибка соединения с сервером");
            alert.setHeaderText(null);
            alert.setContentText("Пожалуйста, проверьте подключение к серверу.");
            alert.showAndWait();
        }
    }

    private void fillInTables() {
        filterObjects();
        filterUsers();
        fillContractsTable();
        filterContracts();
        fillAppraisalObjects();
    }

    private void fillAppraisalObjects() {
        try{
            objectsToAppraiseTable.getItems().clear();
            ServerMSG serverMSG = new ServerMSG(loggedUser, CommandTypes.GETOBJECTS, null);
            ServerMSG answer = client.sendMessage(serverMSG);
            GSONParser<Vector<AppraisalObject>> parser = new GSONParser<>();
            Vector<AppraisalObject> appraisalObjects = new Vector<>();

            Type type = new TypeToken<Vector<AppraisalObject>>() {
            }.getType();

            appraisalObjects = parser.getObject(answer.getData(), type);
            for (AppraisalObject appraisalObject : appraisalObjects) {
                objectsToAppraiseTable.getItems().add(appraisalObject);
            }
        }catch (Exception e) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Ошибка соединения с сервером");
            alert.setHeaderText(null);
            alert.setContentText("Пожалуйста, проверьте подключение к серверу.");
            alert.showAndWait();
        }
    }

    private void filterUsers() {
        try{
            appraisersTable.getItems().clear();
//            String login, firstName, surname, patronymic;
            boolean status = false;
            boolean any = false;
            if (filterStatus.getSelectionModel().getSelectedItem().equals("Активный")) {
                status = false;
            } else if (filterStatus.getSelectionModel().getSelectedItem().equals("Заблокирован")) {
                status = true;
            } else {
                any = true;
            }

            User filter = new User(filterLogin.getText(), "", filterFirstName.getText(),
                    filterSurname.getText(),
                    filterPatronymic.getText(), filterPhone.getText(), Role.CONSUMER, status);

            HashMap<Boolean, User> filterAndUser = new HashMap<>();
            filterAndUser.put(any, filter);

            GSONParser<HashMap<Boolean, User>> parser = new GSONParser<>();
            ServerMSG serverMSG =
                    new ServerMSG(loggedUser, CommandTypes.FILTERUSERS, parser.getString(filterAndUser));

            ServerMSG answer = client.sendMessage(serverMSG);
            GSONParser<Vector<User>> filterParser = new GSONParser<>();
            Vector<User> userVector = new Vector<>();
            Type type = new TypeToken<Vector<User>>() {
            }.getType();
            userVector.addAll(filterParser.getObject(answer.getData(), type));

            if (userVector == null) {
                return;
            }

            for (User user : userVector) {
                String status1;
                if (user.isBlocked()) {
                    status1 = "Заблокирован";
                } else {
                    status1 = "Активный";
                }
                appraisersTable.getItems().add(new UserTable(user.getLogin(),
                        user.getFirstName(), user.getSurname(),
                        user.getPatronymic(), user.getPhone(), "Заказчик",
                        status1));
            }
        } catch (Exception e) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Ошибка соединения с сервером");
            alert.setHeaderText(null);
            alert.setContentText("Пожалуйста, проверьте подключение к серверу.");
            alert.showAndWait();
        }
    }

    private void showContractDescription(Contract contract) {
        if (contract == null) {
            appraisalObjectText.setText("");
            expectedPriceText.setText("");
            appraiserPriceText.setText("");
            priceForAppraisalText.setText("");
            dateOfSigningText.setText("");
            statusText.setText("");
            consumerText.setText("");
            appraiserText.setText("");
            commentFromConsumerText.setText("");
            commentFromAppraiserText.setText("");
            processContractButton.setDisable(true);
            rejectContractButton.setDisable(true);
        } else {
            appraisalObjectText.setText(contract.getAppraisalObject().getName());
            expectedPriceText.setText(String.valueOf(contract.getExpectedPrice()));

            if (contract.getAppraiserPrice() == 0.0) {
                appraiserPriceText.setText("Нет");
            } else {
                appraiserPriceText.setText(String.valueOf(contract.getAppraiserPrice()));
            }

            if (contract.getExpectedPrice() == null) {
                priceForAppraisalText.setText("Нет");
            } else {
                priceForAppraisalText.setText(String.valueOf(contract.getExpectedPrice()));
            }

            if (contract.getDateOfSigning() == null) {
                dateOfSigningText.setText("Нет");
            } else {
                dateOfSigningText.setText(contract.getDateOfSigning());
            }


            consumerText.setText(
                    contract.getConsumer().getSurname() + " " + contract.getConsumer().getFirstName() + " " +
                            contract.getConsumer().getPatronymic());

            if (contract.getAppraiser() == null) {
                appraiserText.setText("Нет");
            } else {
                appraiserText.setText(
                        contract.getAppraiser().getSurname() + " " + contract.getAppraiser().getFirstName() + " " +
                                contract.getAppraiser().getPatronymic());
            }

            if (contract.getCommentFromConsumer() == null) {
                commentFromConsumerText.setText("Нет");
            } else {
                commentFromConsumerText.setText(contract.getCommentFromConsumer());
            }

            if (contract.getCommentFromAppraiser() == null) {
                commentFromAppraiserText.setText("Нет");
            } else {
                commentFromAppraiserText.setText(contract.getCommentFromAppraiser());
            }
            statusText.setText(contract.getStrStatus());
            if (contract.getStatus().equals(Status.TERMINATED)) {
                statusText.setTextFill(Color.color(1, 0, 0));
            } else if (contract.getStatus().equals(Status.SIGNED)) {
                statusText.setTextFill(Color.color(0, 1, 0));
            } else if (contract.getStatus().equals(Status.WAITFORCONSUMER)) {
                statusText.setTextFill(Color.color(0, 0, 1));
            } else if (contract.getStatus().equals(Status.NEW)) {
                statusText.setTextFill(Color.color(0, 0, 1));
            }
            if (contract.getStatus().equals(Status.TERMINATED) ||
                    contract.getStatus().equals(Status.SIGNED)) {
                rejectContractButton.setDisable(true);
                processContractButton.setDisable(true);
                return;
            } else if (contract.getStatus().equals(Status.WAITFORCONSUMER)) {
                rejectContractButton.setDisable(false);
                processContractButton.setDisable(true);
                return;
            }

            rejectContractButton.setDisable(false);
            processContractButton.setDisable(false);
        }
    }
}
