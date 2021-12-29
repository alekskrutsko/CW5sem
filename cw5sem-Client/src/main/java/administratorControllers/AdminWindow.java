package administratorControllers;

import com.google.gson.reflect.TypeToken;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import controllers.Client;
import controllers.EditCurrentUser;
import gsonParser.GSONParser;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import personalInfo.Appraiser;
import personalInfo.User;
import personalInfo.User.Role;
import personalInfo.UserTable;
import serverMSG.ChartData;
import serverMSG.ServerMSG;
import serverMSG.ServerMSG.CommandTypes;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URL;
import java.sql.Date;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

public class AdminWindow {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private AnchorPane reportPane;

    LineChart<String, Number> incomeLineChart;

    @FXML
    private Button makeReportButton;

    @FXML
    private TableView<Appraiser> appraiserTable;

    @FXML
    private TableColumn<Appraiser, String> firstNameColumn;

    @FXML
    private TableColumn<Appraiser, String> surnameColumn;

    @FXML
    private TableColumn<Appraiser, String> patronymicColumn;

    @FXML
    private TableColumn<Appraiser, String> phoneColumn;

    @FXML
    private TableColumn<Appraiser, Double> salaryColumn;

    @FXML
    private Label statusText;

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
    private Label salaryText;

    @FXML
    private Button addButton;

    @FXML
    private Button editButton;

    @FXML
    private Button deleteButton;

    @FXML
    private Label roleText;

    @FXML
    private TableView<UserTable> usersTable;

    @FXML
    private TableColumn<UserTable, String> loginColumn;

    @FXML
    private TableColumn<UserTable, String> roleColumn;

    @FXML
    private TableColumn<UserTable, String> statusColumn;

    @FXML
    private TableColumn<UserTable, String> firstNameColumn1;

    @FXML
    private TableColumn<UserTable, String> surnameColumn1;

    @FXML
    private TableColumn<UserTable, String> patronymicColumn1;

    @FXML
    private TableColumn<UserTable, String> phoneColumn1;


    @FXML
    private Label loginText1;

    @FXML
    private Label firstNameText1;

    @FXML
    private Label surnameText1;

    @FXML
    private Label patronymicText1;

    @FXML
    private Label phoneText1;

    @FXML
    private Button blockButton;

    @FXML
    private Button editDataButton;

    @FXML
    private Label bestAppraiserText;


    private User loggedUser;

    private Client client;


    public void setLoggedUser(User loggedUser) {
        this.loggedUser = loggedUser;
        loginText1.setText(loggedUser.getLogin());
        firstNameText1.setText(loggedUser.getFirstName());
        surnameText1.setText(loggedUser.getSurname());
        patronymicText1.setText(loggedUser.getPatronymic());
        phoneText1.setText(loggedUser.getPhone());
        roleText.setText("Администратор");
        showStatistics();
    }

    public User getLoggedUser() {
        return loggedUser;
    }

    @FXML
    void makeReport(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Сохранить статистику");
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("PDF files (*.pdf)", "*.pdf");
        fileChooser.getExtensionFilters().add(extFilter);
        fileChooser.setInitialFileName("CompanyStatistics");
        File file = fileChooser.showSaveDialog(makeReportButton.getScene().getWindow());
        if (file != null) {
            try {
                GSONParser<Vector<ChartData<Appraiser, Integer>>> gsonParser1 = new GSONParser<>();
                Type type = new TypeToken<Vector<ChartData<Appraiser, Integer>>>() {
                }.getType();
                ServerMSG serverMSG = new ServerMSG(loggedUser, CommandTypes.BESTAPPRAISER, null);
                ServerMSG answer = client.sendMessage(serverMSG);
                Vector<ChartData<Appraiser, Integer>> appraisers = gsonParser1.getObject(answer.getData(), type);


                GSONParser<Vector<ChartData<Date, Double>>> gsonParser2 = new GSONParser<>();
                type = new TypeToken<Vector<ChartData<Date, Double>>>() {
                }.getType();

                serverMSG = new ServerMSG(loggedUser, CommandTypes.LINECHART, null);
                answer = client.sendMessage(serverMSG);
                Vector<ChartData<Date, Double>> lineChart = gsonParser2.getObject(answer.getData(), type);

                Document document = new Document();
                PdfWriter.getInstance(document, new FileOutputStream(file.getAbsolutePath()));
                document.open();

                Font font = FontFactory.getFont("./src/main/resources/fonts/NotoSans-Regular.ttf", "cp1251", BaseFont.EMBEDDED, 10);
                Font headerFont = FontFactory.getFont("./src/main/resources/fonts/NotoSans-Regular.ttf", "cp1251", BaseFont.EMBEDDED, 14);

//                BaseFont bf=BaseFont.createFont("./src/main/resources/fonts/NotoSans-Regular.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
//                Font font=new Font(bf,10,Font.NORMAL);
//                Font headerFont=new Font(bf,14,Font.NORMAL);


                Paragraph header = new Paragraph("ОТЧЕТ ПО ДЕЯТЕЛЬНОСТИ КОМПАНИИ ПО ОЦЕНКЕ ОБЪЕКТОВ НЕДВИЖИМОСТИ", headerFont);
                header.add(new Paragraph(" "));
                header.setAlignment(Element.ALIGN_CENTER);
                document.add(header);


                LocalDate currentDate = LocalDate.now();
                java.util.Date current = null;
                try{
                    current = new SimpleDateFormat("yyyy-MM-dd").parse(String.valueOf(currentDate));
                }
                catch (Exception e){
                    //ignore
                }

                String currentDateStr = DateFormat.getDateInstance(SimpleDateFormat.LONG, new Locale("ru")).format(current);
                Paragraph created = new Paragraph("Сгенерирован " + currentDateStr, font);
                created.add(new Paragraph(" "));
                created.setAlignment(Element.ALIGN_CENTER);
                document.add(created);



                Paragraph header1 = new Paragraph("1. СТАТИСТИКА ПО ДОХОДАМ ЗА ПОЛУГОДИЕ", headerFont);
                header1.add(new Paragraph(" "));
                header1.setAlignment(Element.ALIGN_CENTER);
                document.add(header1);

                PdfPTable table = new PdfPTable(2);

                PdfPCell cell1 = new PdfPCell(new Paragraph("Месяц", font));
                PdfPCell cell2 = new PdfPCell(new Paragraph("Прибыль, руб.", font));
                cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell2.setHorizontalAlignment(Element.ALIGN_CENTER);

                table.addCell(cell1);
                table.addCell(cell2);

                for(ChartData<Date, Double> data : lineChart){
                    String month = null;
                    try {
                        java.util.Date bufDate = new SimpleDateFormat("yyyy-MM-dd").parse(String.valueOf(data.getValue1()));
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMMM", new Locale("ru"));
                        month = simpleDateFormat.format(bufDate);
                    } catch (Exception e) {
                        //ignore
                    }
                    PdfPCell bufCell1 = new PdfPCell(new Paragraph(month, font));
                    PdfPCell bufCell2 = new PdfPCell(new Paragraph(data.getValue2().toString(), font));
                    bufCell1.setHorizontalAlignment(Element.ALIGN_CENTER);
                    bufCell2.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(bufCell1);
                    table.addCell(bufCell2);
                }
                document.add(table);
                document.add(new Paragraph("\n"));


                Paragraph header2 = new Paragraph("2. СТАТИСТИКА ОЦЕНЩИКОВ КОМПАНИИ", headerFont);
                header2.add(new Paragraph(" "));
                header2.setAlignment(Element.ALIGN_CENTER);
                document.add(header2);

                PdfPTable table3 = new PdfPTable(2);

                cell1 = new PdfPCell(new Paragraph("Оценщик, ФИО", font));
                cell2 = new PdfPCell(new Paragraph("Количество успешно заключенных договоров, шт.", font));
                cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell2.setHorizontalAlignment(Element.ALIGN_CENTER);

                table3.addCell(cell1);
                table3.addCell(cell2);

                for(ChartData<Appraiser, Integer> data : appraisers){
                    PdfPCell bufCell1 = new PdfPCell(new Paragraph(data.getValue1().getSurname() + " " +
                            data.getValue1().getFirstName() + " " +
                            data.getValue1().getPatronymic(), font));
                    PdfPCell bufCell2 = new PdfPCell(new Paragraph(data.getValue2().toString(), font));
                    bufCell1.setHorizontalAlignment(Element.ALIGN_CENTER);
                    bufCell2.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table3.addCell(bufCell1);
                    table3.addCell(bufCell2);
                }

                document.add(table3);
                document.close();
            } catch (IOException | DocumentException ex) {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Ошибка соединения с сервером");
                alert.setHeaderText(null);
                alert.setContentText("Пожалуйста, проверьте подключение к серверу.");
                alert.showAndWait();
            }
        }
    }

    @FXML
    void addAppraiser(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(AdminWindow.class.getResource("/admin/addAppraiser.fxml"));
            Parent root = loader.load();

            AddAppraiser editWindow = loader.getController();
            editWindow.setPerson(client, loggedUser);
            loader.setController(editWindow);

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Добавление оценщика");
            dialogStage.setResizable(false);
            dialogStage.initOwner(addButton.getScene().getWindow());
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.setScene(new Scene(root, 710, 486));
            dialogStage.showAndWait();

            appraiserTable.getItems().clear();
            fillInTable();
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    @FXML
    void deleteAppraiser(ActionEvent event) {
        try{
            int selectedIndex = appraiserTable.getSelectionModel().getSelectedIndex();
            if (selectedIndex >= 0) {
                String login = appraiserTable.getItems().get(selectedIndex).getLogin();
                Alert alert = new Alert(AlertType.CONFIRMATION);
                alert.setTitle("Подтверждение удаления");
                alert.setHeaderText(null);
                alert.setContentText("Вы действительно хотите удалить оценщика?");
                Optional<ButtonType> result = alert.showAndWait();
                if (result.get() == ButtonType.OK) {
                    ServerMSG serverMSG = new ServerMSG(loggedUser, CommandTypes.DELETEAPPRAISER, login);
                    client.sendMessage(serverMSG);
                    fillInTable();

                } else {
                    // ... user chose CANCEL or closed the dialog
                }
            } else {
                // Ничего не выбрано.
                Alert alert = new Alert(AlertType.WARNING);
                alert.setTitle("Ошибка удаления");
                alert.setHeaderText("Не выбрана запись!");
                alert.setContentText("Пожалуйста, выберите оценщика из таблицы.");
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
    void editAppraiser(ActionEvent event) {
        try{
            int selectedIndex = appraiserTable.getSelectionModel().getSelectedIndex();
            if (selectedIndex >= 0) {
                try {

                    FXMLLoader loader = new FXMLLoader();
                    loader.setLocation(AdminWindow.class.getResource("/admin/editAppraiser.fxml"));
                    Parent root = (Parent) loader.load();

                    EditAppraiser editWindow = (EditAppraiser) loader.getController();
                    editWindow.setPerson(appraiserTable.getSelectionModel().getSelectedItem(), client, loggedUser);
                    loader.setController(editWindow);

                    Stage dialogStage = new Stage();
                    dialogStage.setTitle("Редактирование оценщика");
                    dialogStage.setResizable(false);
                    dialogStage.initOwner(editButton.getScene().getWindow());
                    dialogStage.initModality(Modality.WINDOW_MODAL);
                    dialogStage.setScene(new Scene(root, 510, 225));
                    dialogStage.showAndWait();

                    appraiserTable.getItems().clear();
                    fillInTable();
                } catch (IOException e) {
                    System.out.println(e);
                }
            } else {
                // Ничего не выбрано.
                Alert alert = new Alert(AlertType.WARNING);
                alert.setTitle("Ошибка изменения");
                alert.setHeaderText("Не выбрана запись!");
                alert.setContentText("Пожалуйста, выберите оценщика из таблицы.");
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

    private void showAppraiserDetails(Appraiser appraiser) {
        editButton.setDisable(appraiser == null);
        deleteButton.setDisable(appraiser == null);
        if (appraiser != null) {
            loginText.setText(appraiser.getLogin());
            firstNameText.setText(appraiser.getFirstName());
            surnameText.setText(appraiser.getSurname());
            patronymicText.setText(appraiser.getPatronymic());
            phoneText.setText(appraiser.getPhone());
            salaryText.setText(String.valueOf(appraiser.getSalary()));
            if (appraiser.isBlocked()) {
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
            salaryText.setText("");
            statusText.setText("");
        }
    }

    private void fillInTable() {
        try{
            fillUserTable();
            appraiserTable.getItems().clear();
            ServerMSG serverMSG = new ServerMSG(loggedUser, CommandTypes.GETAPPRAISERS, "");
            ServerMSG answer = client.sendMessage(serverMSG);
            GSONParser<Vector<Appraiser>> parser = new GSONParser<Vector<Appraiser>>();
            Vector<Appraiser> appraisers = new Vector<Appraiser>();

            Type type = new TypeToken<Vector<Appraiser>>() {
            }.getType();

            appraisers.addAll(parser.getObject(answer.getData(), type));

            if (appraisers == null) {
                return;
            }

            for (Appraiser appraiser : appraisers) {
                appraiserTable.getItems().add(new Appraiser(appraiser.getLogin(), appraiser.getPasswordMask(),
                                                      appraiser.getFirstName(), appraiser.getSurname(),
                                                      appraiser.getPatronymic(), appraiser.getPhone(), appraiser.getRole(),
                                                      appraiser.isBlocked(), appraiser.getSalary()));
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

    private void fillUserTable() {
        try{
            usersTable.getItems().clear();
            ServerMSG serverMSG = new ServerMSG(loggedUser, CommandTypes.GETUSERS, "");
            ServerMSG answer = client.sendMessage(serverMSG);
            GSONParser<Vector<User>> parser = new GSONParser<>();
            Vector<User> users = new Vector<>();

            Type type = new TypeToken<Vector<User>>() {
            }.getType();

            users.addAll(parser.getObject(answer.getData(), type));
            if (users == null) {
                return;
            }

            for (User user : users) {
                String role = null, status = null;
                if (user.getRole().equals(Role.CONSUMER)) {
                    role = "Заказчик";
                } else if (user.getRole().equals(Role.APPRAISER)) {
                    role = "Оценщик";
                }
                if (user.isBlocked()) {
                    status = "Заблокирован";
                } else {
                    status = "Активный";
                }

                usersTable.getItems().add(new UserTable(user.getLogin(),
                                                        user.getFirstName(), user.getSurname(),
                                                        user.getPatronymic(), user.getPhone(), role, status));
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

    private void startConnection() {
        try{
            client = new Client();
            client.startConnection("127.0.0.1", 3000);
        }
        catch (Exception e){
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Ошибка соединения с сервером");
            alert.setHeaderText(null);
            alert.setContentText("Пожалуйста, проверьте подключение к серверу.");
            alert.showAndWait();
        }
    }

    private void showStatistics() {
        reportPane.getChildren().remove(incomeLineChart);
        drawLineChart();
        showBestAppraiser();
    }

    private void drawLineChart() {
        try {
            final CategoryAxis xAxis = new CategoryAxis();
            final NumberAxis yAxis = new NumberAxis();
            xAxis.setLabel("Месяц");
            yAxis.setLabel("Прибыль, руб.");

            incomeLineChart = new LineChart<String, Number>(xAxis, yAxis);

            incomeLineChart.setTranslateX(250);
            incomeLineChart.setTitle("Мониторинг прибыли компании за последние 6 месяцев");

            XYChart.Series series = new XYChart.Series();
            series.setName("Кривая прибыли");

            GSONParser<Vector<ChartData<Date, Double>>> gsonParser = new GSONParser<>();
            Type type = new TypeToken<Vector<ChartData<Date, Double>>>() {
            }.getType();
            Vector<ChartData<Date, Double>> result;
            ServerMSG serverMSG = new ServerMSG(loggedUser, CommandTypes.LINECHART, null);
            ServerMSG answer = client.sendMessage(serverMSG);

            result = gsonParser.getObject(answer.getData(), type);

            for (ChartData<Date, Double> entry : result) {
                String month = null;
                try {
                    java.util.Date bufDate = new SimpleDateFormat("yyyy-MM-dd").parse(String.valueOf(entry.getValue1()));
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM yyyy", new Locale("ru"));
                    month = simpleDateFormat.format(bufDate);
                }catch (Exception e) {
                    //ignore
                }
                series.getData().add(new XYChart.Data(month, entry.getValue2()));
            }

            incomeLineChart.getData().add(series);
            reportPane.getChildren().add(incomeLineChart);
        }
        catch (Exception e){
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Ошибка соединения с сервером");
            alert.setHeaderText(null);
            alert.setContentText("Пожалуйста, проверьте подключение к серверу.");
            alert.showAndWait();
        }
    }


    private void showBestAppraiser() {
        try{
            GSONParser<Vector<ChartData<Appraiser, Integer>>> gsonParser = new GSONParser<>();
            Type type = new TypeToken<Vector<ChartData<Appraiser, Integer>>>() {
            }.getType();
            ServerMSG serverMSG = null;
            ServerMSG answer = null;

            serverMSG = new ServerMSG(loggedUser, CommandTypes.BESTAPPRAISER, null);

            answer = client.sendMessage(serverMSG);
            Vector<ChartData<Appraiser, Integer>> bestAppraisers = gsonParser.getObject(answer.getData(), type);
            Appraiser bestAppraiser = new Appraiser();
            Integer max = 0;

            for (ChartData<Appraiser, Integer> entry : bestAppraisers) {
                if (max < entry.getValue2()) {
                    max = entry.getValue2();
                    bestAppraiser = entry.getValue1();
                }
            }

            bestAppraiserText.setText(bestAppraiser.getSurname() + " " + bestAppraiser.getFirstName() + " " +
                                   bestAppraiser.getPatronymic() + ", количество заключенных договоров: " + max);
        } catch (Exception e) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Ошибка соединения с сервером");
            alert.setHeaderText(null);
            alert.setContentText("Пожалуйста, проверьте подключение к серверу.");
            alert.showAndWait();
        }
    }

    @FXML
    void block(ActionEvent event) {
        try{
            int selectedIndex = usersTable.getSelectionModel().getSelectedIndex();
            if (selectedIndex >= 0) {
                String login = usersTable.getItems().get(selectedIndex).getLogin();
                Alert alert = new Alert(AlertType.CONFIRMATION);
                alert.setTitle("Подтверждение блокировки/разблокировки");
                alert.setHeaderText(null);
                alert.setContentText("Вы действительно хотите блокировать/разблокировать пользователя?");
                Optional<ButtonType> result = alert.showAndWait();
                if (result.get() == ButtonType.OK) {
                    ServerMSG serverMSG = new ServerMSG(loggedUser, CommandTypes.BLOCKUSER, login);
                    client.sendMessage(serverMSG);
                    fillInTable();
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
    void editData(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(AdminWindow.class.getResource("/admin/editCurrentUser.fxml"));
        Parent root = (Parent) loader.load();

        EditCurrentUser editWindow = (EditCurrentUser) loader.getController();
        editWindow.setPerson(client, loggedUser);
        loader.setController(editWindow);

        Stage dialogStage = new Stage();
        dialogStage.setTitle("Редактирование пользователя");
        dialogStage.setResizable(false);
        dialogStage.initOwner(editDataButton.getScene().getWindow());
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.setScene(new Scene(root, 600, 318));
        dialogStage.showAndWait();
        fillInTable();
        loginText1.setText(loggedUser.getLogin());
        firstNameText1.setText(loggedUser.getFirstName());
        surnameText1.setText(loggedUser.getSurname());
        patronymicText1.setText(loggedUser.getPatronymic());
        phoneText1.setText(loggedUser.getPhone());
        roleText.setText("Администратор");
    }

    @FXML
    void initialize() throws SQLException {
        startConnection();

        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        surnameColumn.setCellValueFactory(new PropertyValueFactory<>("surname"));
        patronymicColumn.setCellValueFactory(new PropertyValueFactory<>("patronymic"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
        firstNameColumn1.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        surnameColumn1.setCellValueFactory(new PropertyValueFactory<>("surname"));
        patronymicColumn1.setCellValueFactory(new PropertyValueFactory<>("patronymic"));
        phoneColumn1.setCellValueFactory(new PropertyValueFactory<>("phone"));
        salaryColumn.setCellValueFactory(new PropertyValueFactory<>("salary"));
        roleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        loginColumn.setCellValueFactory(new PropertyValueFactory<>("login"));

        fillInTable();
        showAppraiserDetails(null);
        appraiserTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> showAppraiserDetails(newValue));

        checkActiveUser(null);

        usersTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> checkActiveUser(newValue));

//        radioGroup = new ToggleGroup();
//        lastMonthButton.setToggleGroup(radioGroup);
//        allMonthButton.setToggleGroup(radioGroup);
//        lastMonthButton.setSelected(true);
//
//        radioGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
//            showStatistics();
//        });

        bestAppraiserText.setWrapText(true);
    }

    private void checkActiveUser(UserTable newValue) {
        blockButton.setDisable(newValue == null);
    }
}
