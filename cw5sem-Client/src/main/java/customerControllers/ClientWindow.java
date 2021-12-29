package customerControllers;

import administratorControllers.AdminWindow;
import appraisal.AppraisalObject;
import appraisal.Contract;
import appraisal.Contract.Status;
import appraiserControllers.AppraiserWindow;
import controllers.EditCurrentUser;
import gsonParser.GSONParser;
import com.google.gson.reflect.TypeToken;
import controllers.Client;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import serverMSG.ChartData;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;
import personalInfo.User;
import serverMSG.ServerMSG;
import serverMSG.ServerMSG.CommandTypes;

import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.*;

public class ClientWindow {
    

    @FXML
    private TableView<Contract> contractsTable;

    @FXML
    private TableColumn<Contract, Integer> contractIDColumn;

    @FXML
    private TableColumn<Contract, String> objectColumn;

    @FXML
    private TextField filterExpectedPrice;

    @FXML
    private TableColumn<Contract, Double> expectedPriceColumn;

    @FXML
    private TableColumn<Contract, String> statusColumn;


    @FXML
    private Label contractObjectText;



    @FXML
    private Label dateOfSigningText;
    

    @FXML
    private Label expectedPriceText;

    @FXML
    private Label statusText;

    @FXML
    private Button newContractButton;

    @FXML
    private Button terminateContractButton;

    private User loggedUser;

    private Client client;

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
    private Label currentStatusText;

    @FXML
    private Button editPersonalDataButton;

    @FXML
    private Label appraiserPriceText;

    @FXML
    private Label appraiserText;

    @FXML
    private Label priceForAppraisalText;

    @FXML
    private Label commentFromAppraiserText;

    @FXML
    private Label commentFromConsumerText;

    @FXML
    private AnchorPane statisticsPane;


    @FXML
    private Button signingButton;

//    @FXML
//    private RadioButton lastMonthButton;
//
//    @FXML
//    private RadioButton lastTwoMonthButton;
//
//    @FXML
//    private RadioButton lastHalfYearButton;


    @FXML
    private TableView<Contract> contractToFileTable;



    @FXML
    private TableColumn<Contract, String> filterObjectColumn;



    @FXML
    private TableColumn<Contract, Double> filterExpectedPriceColumn;

    @FXML
    private TableColumn<Contract, Double> filterAppraiserPriceColumn;

    @FXML
    private TableColumn<Contract, Double> filterPriceForAppraisalColumn;

    @FXML
    private TableColumn<Contract, String> filterDateOfSigningColumn;

    @FXML
    private Button toFileButton;

    @FXML
    private TextField filterContractObject;

    @FXML
    private TextField filterPriceForAppraisal;



    @FXML
    private Button makeReportButton;

    @FXML
    private BarChart<String, Number> objectsPricesChart;

    ToggleGroup radioGroup;

//    @FXML
//    void makeReport(ActionEvent event) {
//        try{
//            FileChooser fileChooser = new FileChooser();
//            fileChooser.setTitle("Сохранить статистику");
//            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("PDF files (*.pdf)", "*.pdf");
//            fileChooser.getExtensionFilters().add(extFilter);
//            fileChooser.setInitialFileName("appraisedObjectsStatistics");
//            File file = fileChooser.showSaveDialog(makeReportButton.getScene().getWindow());
//            if (file != null) {
//                try {
//                    GSONParser<ChartData<Double, Double>> gsonParser = new GSONParser<>();
//                    Type type = new TypeToken<ChartData<Double, Double>>() {}.getType();
//                    Vector<ChartData<Double, Double>> result = new Vector<>();
//                    ServerMSG serverMSG = null;
//                    ServerMSG answer = null;
//                    ChartData<Double, Double> bufResult = null;
//
////                    LocalDate date = LocalDate.now();
////
////                    if (lastHalfYearButton.isSelected()) {
////                        serverMSG = new ServerMSG(loggedUser, CommandTypes.CONSUMERDIAGRAM,
////                                String.valueOf(LocalDate.now().minusMonths(5)));
////                        answer = client.sendMessage(serverMSG);
////                        bufResult = gsonParser.getObject(answer.getData(), type);
////                        result.add(bufResult);
////                        date = date.minusMonths(5);
////                    }
////                    if (lastTwoMonthButton.isSelected() || lastHalfYearButton.isSelected()) {
////                        serverMSG = new ServerMSG(loggedUser, CommandTypes.CONSUMERDIAGRAM,
////                                String.valueOf(LocalDate.now().minusMonths(1)));
////                        answer = client.sendMessage(serverMSG);
////                        bufResult = gsonParser.getObject(answer.getData(), type);
////                        result.add(bufResult);
////                        if(lastTwoMonthButton.isSelected()){
////                            date = date.minusMonths(1);
////                        }
////                    }
//
//                    serverMSG = new ServerMSG(loggedUser, CommandTypes.CONSUMERDIAGRAM,
//                            String.valueOf(LocalDate.now()));
//                    answer = client.sendMessage(serverMSG);
//                    bufResult = gsonParser.getObject(answer.getData(), type);
//                    result.add(bufResult);
//
//                    Vector<ChartData<String, Double>> toFile = new Vector<>();
//
//                    for (ChartData<Double, Double> data: result) {
//                        String month = null;
////                        try {
//////                            Date bufDate = new SimpleDateFormat("yyyy-MM-dd").parse(String.valueOf(date));
//////                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM yyyy", new Locale("ru"));
//////                            month = simpleDateFormat.format(bufDate);
////                        } catch (Exception e) {
////                            //ignore
////                        }
//
//
//
//                        ////ВМЕСТО MONTH ВСТАВИТЬ НАЗВАНИЕ ОЦЕНЁННОГО ОБЪЕКТА
//                        toFile.add(new ChartData<>(month, Precision.round(data.getValue1(), 2)));
//                        toFile.add(new ChartData<>(month, Precision.round(data.getValue2(), 2)));
////                        date = date.plusMonths(1);
//                    }
//
//                    String filter = null;
//
////                    if (lastMonthButton.isSelected()) {
////                        filter = "ПОСЛЕДНИЙ МЕСЯЦ";
////                    } else if (lastTwoMonthButton.isSelected()) {
////                        filter = "ПОСЛЕДНИЕ ДВА МЕСЯЦА";
////                    } else if (lastThreeMonthButton.isSelected()) {
////                        filter = "ПОСЛЕДНИЕ ТРИ МЕСЯЦА";
////                    }
//
//                    StatisticsReport pdfFile = new StatisticsReport();
//                    pdfFile.setLineStats(toFile);
////                    pdfFile.setFilter(filter);
//                    pdfFile.exportConsumerStats(file.getAbsolutePath());
//                } catch (IOException | DocumentException | ParseException ex) {
//                    System.out.println(ex.getMessage());
//                }
//            }
//        }
//        catch (Exception e) {
//            Alert alert = new Alert(AlertType.ERROR);
//            alert.setTitle("Ошибка соединения с сервером");
//            alert.setHeaderText(null);
//            alert.setContentText("Пожалуйста, проверьте подключение к серверу.");
//            alert.showAndWait();
//        }
//    }

    @FXML
    void signContract(ActionEvent event) {
        try{
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Подтверждение соглашения с условиями договора");
            alert.setHeaderText(null);
            alert.setContentText("Вы действительно хотите заключить договор?");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK) {
                LocalDate date = LocalDate.now();
                contractsTable.getSelectionModel().getSelectedItem().setDateOfSigning(String.valueOf(date));
                GSONParser<Contract> parser = new GSONParser<>();
                ServerMSG serverMSG = new ServerMSG(loggedUser, CommandTypes.CONTRACTSIGNED,
                        parser.getString(contractsTable.getSelectionModel().getSelectedItem()));
                client.sendMessage(serverMSG);
                fillInTables();
            } else {
                // ... user chose CANCEL or closed the dialog
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
    void editPersonalData(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(AdminWindow.class.getResource("/admin/editCurrentUser.fxml"));
        Parent root = loader.load();

        EditCurrentUser editWindow = loader.getController();
        editWindow.setPerson(client, loggedUser);
        loader.setController(editWindow);

        Stage dialogStage = new Stage();
        dialogStage.setResizable(false);
        dialogStage.setTitle("Редактирование пользователя");
        dialogStage.initOwner(editPersonalDataButton.getScene().getWindow());
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.setScene(new Scene(root, 600, 318));
        dialogStage.showAndWait();
        fillInTables();
        currentLoginText.setText(loggedUser.getLogin());
        currentFirstNameText.setText(loggedUser.getFirstName());
        currentSurnameText.setText(loggedUser.getSurname());
        currentPatronymicText.setText(loggedUser.getPatronymic());
        currentPhoneText.setText(loggedUser.getPhone());
        currentStatusText.setText("Заказчик");
    }

    private void startConnection() {
        try{
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
    void newContract(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(AppraiserWindow.class.getResource("/customer/newContract.fxml"));
        Parent root = loader.load();

        NewContract editWindow = loader.getController();
        editWindow.setPerson(loggedUser, client);
        loader.setController(editWindow);

        Stage dialogStage = new Stage();
        dialogStage.setResizable(false);
        dialogStage.setTitle("Новый договор на оценку объекта недвижимости");
        dialogStage.initOwner(newContractButton.getScene().getWindow());
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.setScene(new Scene(root, 1000, 600));
        dialogStage.showAndWait();
        fillInTables();
    }

    @FXML
    void terminateContract(ActionEvent event) {
        try {
            int selectedIndex = contractsTable.getSelectionModel().getSelectedIndex();
            if (selectedIndex >= 0) {
                TextInputDialog dialog = new TextInputDialog("");
                dialog.setTitle("Подтверждение расторжения договора");
                dialog.setHeaderText("Вы действительно хотите расторгнуть договор?");
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
                alert.setTitle("Ошибка обработки инвестиционного договора");
                alert.setHeaderText("Не выбрана запись!");
                alert.setContentText("Пожалуйста, выберите инвестиционный договор из таблицы.");
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

    public void setLoggedUser(User loggedUser) {
        this.loggedUser = loggedUser;
        currentLoginText.setText(loggedUser.getLogin());
        currentFirstNameText.setText(loggedUser.getFirstName());
        currentSurnameText.setText(loggedUser.getSurname());
        currentPatronymicText.setText(loggedUser.getPatronymic());
        currentPhoneText.setText(loggedUser.getPhone());
        currentStatusText.setText("Заказчик");
        fillInTables();
    }

    public User getLoggedUser() {
        return loggedUser;
    }

    private void fillInTables() {
        fillContractsTable();
        filterContracts();
        showDiagram();
    }

    //using iterator
    private void fillContractsTable() {
        try{
            contractsTable.getItems().clear();

            ServerMSG serverMSG = new ServerMSG(loggedUser, CommandTypes.GETUSERCONTRACTS, null);
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


            Iterator<Contract> it = contracts.iterator();

            while(it.hasNext()) {
                contractsTable.getItems().add(it.next());
            }

//            for (Contract contract : contracts) {
//                contractsTable.getItems().add(contract);
//            }

        } catch (Exception e) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Ошибка соединения с сервером");
            alert.setHeaderText(null);
            alert.setContentText("Пожалуйста, проверьте подключение к серверу.");
            alert.showAndWait();
        }
    }

    private void showDiagram() {
        try{
            LocalDate date = LocalDate.now();
            statisticsPane.getChildren().remove(objectsPricesChart);
            GSONParser< ArrayList<Map<String, String>>> gsonParser = new GSONParser<>();
            GSONParser<ArrayList<ChartData<Double, Double>>> gsonParser1 = new GSONParser<>();
            Type type = new TypeToken<ArrayList<Map<String, String>>>() {
            }.getType();
            Type type1 = new TypeToken<ArrayList<ChartData<Double, Double>>>() {
            }.getType();
            ArrayList<Map<String, String>> result = new ArrayList<>();
            ArrayList<ChartData<Double,Double>> result1 = new ArrayList<>();
            ServerMSG serverMSG = null;
            ServerMSG answer = null;

            serverMSG = new ServerMSG(loggedUser, CommandTypes.CONSUMERDIAGRAM, null);
            answer = client.sendMessage(serverMSG);
            result.addAll(gsonParser.getObject(answer.getData(), type));

            serverMSG = new ServerMSG(loggedUser, CommandTypes.CONSUMERDIAGRAM, "1");
            answer = client.sendMessage(serverMSG);
            result1.addAll(gsonParser1.getObject(answer.getData(), type1));



            final CategoryAxis xAxis = new CategoryAxis();
            final NumberAxis yAxis = new NumberAxis();
            objectsPricesChart = new BarChart<String, Number>(xAxis, yAxis);
            objectsPricesChart.setTitle("Разница между ожидаемой стоимостью и\n стоимостью предложенной оценщиком");
            xAxis.setLabel("Названия объектов");
            yAxis.setLabel("Денежные средства, руб.");

            XYChart.Series series1 = new XYChart.Series();
            series1.setName("Ожидаемая цена, руб.");
            XYChart.Series series2 = new XYChart.Series();
            series2.setName("Цена предложенная оценщиком, руб.");

            Iterator<Map<String, String>> iterator = result.iterator();

            ArrayList<String> sr1 = new ArrayList<>();
            ArrayList<Double> sr2 = new ArrayList<>();

            for (ChartData<Double, Double> chartData : result1) {
                if(iterator.hasNext()){
                    Map<String, String> map = new HashMap<>();
                    map = iterator.next();
                    String appraisalObjectNameAndDate = String.valueOf(map.keySet());
                    StringBuffer stringBuffer = new StringBuffer(appraisalObjectNameAndDate);
                    stringBuffer.delete(0,1);
                    stringBuffer.delete(stringBuffer.length()-1,stringBuffer.length());
                    appraisalObjectNameAndDate = stringBuffer.toString();

                    Double expectedPrice = chartData.getValue1();
                    Double appraiserPrice = chartData.getValue2();
                    series1.getData().add(new XYChart.Data(appraisalObjectNameAndDate+ "\nДата заключения\nдоговора:\n" + map.get(appraisalObjectNameAndDate), expectedPrice));
                    series2.getData().add(new XYChart.Data(appraisalObjectNameAndDate + "\nДата заключения\nдоговора:\n" + map.get(appraisalObjectNameAndDate), appraiserPrice));

//                    sr1.add(appraisalObjectNameAndDate+ "\nДата заключения\nдоговора:\n" + map.get(appraisalObjectNameAndDate));
//                    sr1.add(appraisalObjectNameAndDate+ "\nДата заключения\nдоговора:\n" + map.get(appraisalObjectNameAndDate));
//                    sr2.add(expectedPrice);
//                    sr2.add(appraiserPrice);

                }
            }


//            Iterator<String> it1 = sr1.iterator();
//            Iterator<Double> it2 = sr2.iterator();
//            while(it1.hasNext() && it2.hasNext()){
//                series1.getData().add(new XYChart.Data( it1.next(), it2.next()));
//                series2.getData().add(new XYChart.Data( it1.next(), it2.next()));
//            }

            objectsPricesChart.getData().addAll(series1, series2);
            statisticsPane.getChildren().addAll(objectsPricesChart);
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
        startConnection();

        radioGroup = new ToggleGroup();
//        lastMonthButton.setToggleGroup(radioGroup);
//        lastTwoMonthButton.setToggleGroup(radioGroup);
//        lastThreeMonthButton.setToggleGroup(radioGroup);
//        lastMonthButton.setSelected(true);

        radioGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            showDiagram();
        });

        objectColumn.setCellValueFactory(new PropertyValueFactory<>("objectName"));
        expectedPriceColumn.setCellValueFactory(new PropertyValueFactory<>("expectedPrice"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("strStatus"));

        filterObjectColumn.setCellValueFactory(new PropertyValueFactory<>("objectName"));
        filterExpectedPriceColumn.setCellValueFactory(new PropertyValueFactory<>("expectedPrice"));
        filterAppraiserPriceColumn.setCellValueFactory(new PropertyValueFactory<>("appraiserPrice"));
        filterPriceForAppraisalColumn.setCellValueFactory(new PropertyValueFactory<>("priceForAppraisal"));
        filterDateOfSigningColumn.setCellValueFactory(new PropertyValueFactory<>("dateOfSigning"));


        filterContractObject.textProperty().addListener((observable, oldValue, newValue) -> {
            filterContracts();
        });
        filterExpectedPrice.textProperty().addListener((observable, oldValue, newValue) -> {
            filterContracts();
        });
        filterPriceForAppraisal.textProperty().addListener((observable, oldValue, newValue) -> {
            filterContracts();
        });

        showContractDescription(null);
        contractsTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> showContractDescription(newValue));
//
//        toFileButton.setDisable(true);
//
//        contractToFileTable.getSelectionModel().selectedItemProperty().addListener(
//                (observable, oldValue, newValue) -> checkSelectedContract(newValue));

    }

    private void checkSelectedContract(Contract contract) {
        toFileButton.setDisable(contract == null);
    }

    private void filterContracts() {
        try{
            contractToFileTable.getItems().clear();
            AppraisalObject appraisalObject = new AppraisalObject();
            appraisalObject.setName(filterContractObject.getText());
            Integer contractID = null;
            Double interestRate = null;
            Double investments = null;

            try {

                if (!filterExpectedPrice.getText().isEmpty()) {
                    investments = Double.parseDouble(filterExpectedPrice.getText());
                }

                if (!filterPriceForAppraisal.getText().isEmpty()) {
                    interestRate = Double.parseDouble(filterPriceForAppraisal.getText());
                }
            } catch (Exception e) {
                return;
            }
            Contract filter = new Contract(contractID,
                    appraisalObject, investments, interestRate,
                    commentFromConsumerText.getText(),
                    commentFromAppraiserText.getText(),
                    Contract.Status.SIGNED);
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
                contractToFileTable.getItems().add(contract);
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

    private void showContractDescription(Contract contract) {
        if (contract == null) {
            contractObjectText.setText("");
            expectedPriceText.setText("");
            appraiserPriceText.setText("");
            priceForAppraisalText.setText("");
            dateOfSigningText.setText("");
            statusText.setText("");
            commentFromAppraiserText.setText("");
            commentFromConsumerText.setText("");
            appraiserText.setText("");
            terminateContractButton.setDisable(true);
            signingButton.setDisable(true);
        } else {
            if (contract.getStatus().equals(Status.SIGNED) || contract.getStatus().equals(Status.WAITFORAPPRAISER) ||
                    contract.getStatus().equals(Status.NEW)) {
                terminateContractButton.setDisable(false);
                signingButton.setDisable(true);
            } else if (contract.getStatus().equals(Status.TERMINATED)) {
                terminateContractButton.setDisable(true);
                signingButton.setDisable(true);
            } else if (contract.getStatus().equals(Status.WAITFORCONSUMER)) {
                terminateContractButton.setDisable(false);
                signingButton.setDisable(false);
            }

            contractObjectText.setText(contract.getAppraisalObject().getName());
            expectedPriceText.setText(String.valueOf(contract.getExpectedPrice()));
            priceForAppraisalText.setText(String.valueOf(contract.getPriceForAppraisal()));

            if (contract.getAppraiserPrice() == 0) {
                appraiserPriceText.setText("Нет");
            } else {
                appraiserPriceText.setText(String.valueOf(contract.getAppraiserPrice()));
            }

            if (contract.getDateOfSigning() == null) {
                dateOfSigningText.setText("Нет");
            } else {
                dateOfSigningText.setText(contract.getDateOfSigning());
            }

            statusText.setText(contract.getStrStatus());
            if (contract.getStatus().equals(Status.TERMINATED)) {
                statusText.setTextFill(Color.color(1, 0, 0));
            } else if (contract.getStatus().equals(Status.SIGNED)) {
                statusText.setTextFill(Color.color(0, 1, 0));
            } else if (contract.getStatus().equals(Status.WAITFORAPPRAISER) ||
                    contract.getStatus().equals(Status.WAITFORCONSUMER)) {
                statusText.setTextFill(Color.color(0, 0, 1));
            } else if (contract.getStatus().equals(Status.NEW)) {
                statusText.setTextFill(Color.color(0, 0, 0));
            }

//            if (contract.getPriceForAppraisal() == 0) {
//                expectedPriceText.setText("Не установлена цена за оценку объекта недвижимости!");
//            } else {
//                expectedPriceText.setText(String.valueOf(contract.getExpectedPrice()));
//            }
            try {
                appraiserText.setText(
                        contract.getAppraiser().getSurname() + " " + contract.getAppraiser().getFirstName() + " " +
                                contract.getAppraiser().getPatronymic());
            } catch (Exception e) {
                appraiserText.setText("Нет");
            }
            commentFromConsumerText.setText(contract.getCommentFromConsumer());
            commentFromAppraiserText.setText(contract.getCommentFromAppraiser());
        }
    }
}

