package serverFiles;


import appraisal.AppraisalObject;
import appraisal.ChartData;
import appraisal.Contract;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import controllers.MainWindowController;
import gsonParser.GSONParser;
import appraisal.Contract.Status;
import org.mindrot.jbcrypt.BCrypt;
import personalInfo.Appraiser;
import personalInfo.User;
import personalInfo.User.Role;
import serverFiles.ServerMSG.CommandTypes;
import workingWithDB.SQLQuery;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.*;
import java.sql.Date;
import java.util.*;

public class Server {
    private ServerSocket serverSocket;

    public Server() {
    }

    public Server(int port) {
        start(port);
    }

    public void start(int port) {
        try {
            serverSocket = new ServerSocket(port);
            while (true) {
                try {
                    new EchoClientHandler(serverSocket.accept()).start();
                }
                catch (Exception e){
                    //ignore
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class EchoClientHandler extends Thread {
        private Socket clientSocket;
        private PrintWriter out;
        private BufferedReader in;

        public EchoClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        public void run() {
            ServerMSG answer = null;
            try {
                out = new PrintWriter(clientSocket.getOutputStream(), true);
                in = new BufferedReader(
                        new InputStreamReader(clientSocket.getInputStream()));
                String inputLine;
                //ConnectionToDB connectionToDB = new ConnectionToDB();
                //connectionToDB.connect();

                while ((inputLine = in.readLine()) != null) {

                    ServerMSG serverMSG = fromStringToServerCommand(inputLine);
                    answer = new ServerMSG();

                    if (serverMSG.getCommandType() == CommandTypes.SIGNIN) {
                        answer.getUser().setLogin(serverMSG.getUser().getLogin());
                        answer.getUser().setPasswordMask(serverMSG.getUser().getPasswordMask());
                        correctSignIn(answer);
                        if(answer.getData() == "success"){
                            Map usersConnected = new HashMap<>();
                            usersConnected.put("connected", answer.getUser().getLogin());
                            MainWindowController.setUsersConnected(usersConnected);
                        }
                        out.println(fromServerCommandToString(answer));
                    } else if (serverMSG.getCommandType() == CommandTypes.SIGNUP) {
                        newUser(serverMSG);
                        out.println(fromServerCommandToString(serverMSG));
                    } else if (serverMSG.getCommandType() == CommandTypes.GETAPPRAISERS) {
                        ResultSet rs = SQLQuery.getAppraisers();
                        Vector<Appraiser> appraisers = new Vector<>();
                        while (rs.next()) {
                            appraisers.add(new Appraiser(rs.getString("login"), rs.getString("passwordMask"),
                                    rs.getString("firstName"), rs.getString("surname"),
                                    rs.getString("patronymic"), rs.getString("phone"),
                                    Role.valueOf(rs.getInt("role")),
                                    rs.getBoolean("isBlocked"), rs.getDouble("salary")));
                        }
                        GSONParser<Vector<Appraiser>> gsonParser = new GSONParser<>();
                        serverMSG.setData(gsonParser.getString(appraisers));
                        out.println(fromServerCommandToString(serverMSG));
                    } else if (serverMSG.getCommandType() == CommandTypes.DELETEAPPRAISER) {
                        SQLQuery.deleteAppraiser(serverMSG.getData());
                        out.println(fromServerCommandToString(serverMSG));
                    } else if (serverMSG.getCommandType() == CommandTypes.BLOCKUSER) {
                        SQLQuery.blockUser(serverMSG.getData());
                        out.println(fromServerCommandToString(serverMSG));
                    } else if (serverMSG.getCommandType() == CommandTypes.EDITAPPRAISER) {
                        GSONParser<Appraiser> gsonParser = new GSONParser<>();
                        Type type = new TypeToken<Appraiser>() {
                        }.getType();
                        Appraiser appraiser = gsonParser.getObject(serverMSG.getData(), type);
                        SQLQuery.editAppraiser(appraiser);
                        out.println(fromServerCommandToString(serverMSG));
                    } else if (serverMSG.getCommandType() == CommandTypes.NEWAPPRAISER) {
                        GSONParser<Appraiser> gsonParser = new GSONParser<>();
                        Type type = new TypeToken<Appraiser>() {
                        }.getType();
                        Appraiser appraiser = gsonParser.getObject(serverMSG.getData(), type);
                        SQLQuery.addAppraiser(appraiser);
                        out.println(fromServerCommandToString(serverMSG));
                    } else if (serverMSG.getCommandType() == CommandTypes.GETCONSUMERS) {
                        ResultSet rs = SQLQuery.getConsumers();
                        Vector<User> users = new Vector<>();
                        while (rs.next()) {
                            users.add(new User(rs.getString("login"), rs.getString("passwordMask"),
                                               rs.getString("firstName"), rs.getString("surname"),
                                               rs.getString("patronymic"), rs.getString("phone"),
                                               Role.valueOf(rs.getInt("role")),
                                               rs.getBoolean("isBlocked")));
                        }
                        GSONParser<Vector<User>> gsonParser = new GSONParser<>();
                        serverMSG.setData(gsonParser.getString(users));
                        out.println(fromServerCommandToString(serverMSG));
                    } else if (serverMSG.getCommandType() == CommandTypes.FILTERUSERS) {
                        GSONParser<HashMap<Boolean, User>> gsonParser = new GSONParser<>();
                        Type type = new TypeToken<HashMap<Boolean, User>>() {
                        }.getType();

                        HashMap<Boolean, User> filterUser = gsonParser.getObject(serverMSG.getData(), type);
                        Set<Boolean> set = filterUser.keySet();
                        ResultSet rs = null;
                        for (Boolean bool : set) {
                            rs = SQLQuery.getConsumersByFilter(filterUser.get(bool), bool);
                        }
                        Vector<User> users = new Vector<>();
                        while (rs.next()) {
                            users.add(new User(rs.getString("login"), rs.getString("passwordMask"),
                                    rs.getString("firstName"), rs.getString("surname"),
                                    rs.getString("patronymic"),rs.getString("phone"),
                                    Role.valueOf(rs.getInt("role")),
                                    rs.getBoolean("isBlocked")));
                        }
                        GSONParser<Vector<User>> vectorGSONParser = new GSONParser<>();
                        serverMSG.setData(vectorGSONParser.getString(users));
                        out.println(fromServerCommandToString(serverMSG));
                    } else if (serverMSG.getCommandType() == CommandTypes.GETUSERS) {
                        ResultSet rs = SQLQuery.getUsers();
                        Vector<User> users = new Vector<>();
                        while (rs.next()) {
                            users.add(new User(rs.getString("login"), rs.getString("passwordMask"),
                                               rs.getString("firstName"), rs.getString("surname"),
                                               rs.getString("patronymic"),rs.getString("phone"),
                                               Role.valueOf(rs.getInt("role")),
                                               rs.getBoolean("isBlocked")));
                        }
                        GSONParser<Vector<User>> gsonParser = new GSONParser<>();
                        serverMSG.setData(gsonParser.getString(users));
                        out.println(fromServerCommandToString(serverMSG));
                    } else if (serverMSG.getCommandType() == CommandTypes.EDITUSER) {
                        GSONParser<User> gsonParser = new GSONParser<>();
                        Type type = new TypeToken<User>() {
                        }.getType();
                        User user = gsonParser.getObject(serverMSG.getData(), type);
                        SQLQuery.editUser(user);
                        out.println(fromServerCommandToString(serverMSG));
                    }
                    else if (serverMSG.getCommandType() == CommandTypes.ADDOBJECT) {
                        GSONParser<AppraisalObject> gsonParser = new GSONParser<>();
                        Type type = new TypeToken<AppraisalObject>() {
                        }.getType();
                        AppraisalObject appraisalObject = gsonParser.getObject(serverMSG.getData(), type);
                        SQLQuery.addAppraisalObject(appraisalObject, serverMSG);
                        out.println(fromServerCommandToString(serverMSG));
                    } else if (serverMSG.getCommandType() == CommandTypes.GETOBJECTS) {
                        GSONParser<Vector<AppraisalObject>> parser = new GSONParser<>();
                        Vector<AppraisalObject> appraisalObjects = SQLQuery.getAppraisalObjects();
                        serverMSG.setData(parser.getString(SQLQuery.getAppraisalObjects()));
                        out.println(fromServerCommandToString(serverMSG));
                    } else if (serverMSG.getCommandType() == CommandTypes.DELETEOBJECT) {
                        SQLQuery.deleteAppraisalObject(Integer.parseInt(serverMSG.getData()));
                        out.println(fromServerCommandToString(serverMSG));
                    } else if (serverMSG.getCommandType() == CommandTypes.FILTEROBJECTS) {
                        GSONParser<AppraisalObject> parser = new GSONParser<>();
                        GSONParser<Vector<AppraisalObject>> answerParser = new GSONParser<>();
                        Type type = new TypeToken<AppraisalObject>() {
                        }.getType();
                        Vector<AppraisalObject> appraisalObjects =
                                SQLQuery.getObjectsByFilter(parser.getObject(serverMSG.getData(), type));
                        serverMSG.setData(answerParser.getString(appraisalObjects));
                        out.println(fromServerCommandToString(serverMSG));
                    } else if (serverMSG.getCommandType() == CommandTypes.EDITOBJECT) {
                        GSONParser<AppraisalObject> gsonParser = new GSONParser<>();
                        Type type = new TypeToken<AppraisalObject>() {
                        }.getType();
                        AppraisalObject appraisalObject = gsonParser.getObject(serverMSG.getData(), type);
                        SQLQuery.editAppraisalObject(appraisalObject, serverMSG);
                        out.println(fromServerCommandToString(serverMSG));
                    } else if (serverMSG.getCommandType() == CommandTypes.NEWOBJECTTYPE) {
                        GSONParser<String> gsonParser = new GSONParser<>();
                        Type type = new TypeToken<String>() {
                        }.getType();
                        String objectType = gsonParser.getObject(serverMSG.getData(), type);
                        SQLQuery.addObjectType(objectType, serverMSG);
                        out.println(fromServerCommandToString(serverMSG));
                    }else if (serverMSG.getCommandType() == CommandTypes.DELETEOBJECTTYPE) {
                        GSONParser<String> gsonParser = new GSONParser<>();
                        Type type = new TypeToken<String>() {
                        }.getType();
                        String objectType = gsonParser.getObject(serverMSG.getData(), type);
                        SQLQuery.deleteObjectType(objectType, serverMSG);
                        out.println(fromServerCommandToString(serverMSG));
                    }
                    else if (serverMSG.getCommandType() == CommandTypes.GETOBJECTSTYPES) {
                        GSONParser<ArrayList<String>> parser = new GSONParser<>();
                        serverMSG.setData(parser.getString(SQLQuery.getObjectsTypes()));
                        out.println(fromServerCommandToString(serverMSG));
                    }
                    else if (serverMSG.getCommandType() == CommandTypes.GETUSERCONTRACTS ||
                               serverMSG.getCommandType() == CommandTypes.GETALLCONTRACTS) {
                        GSONParser<Vector<Contract>> gsonParser = new GSONParser<>();
                        Vector<Contract> contracts;
                        if (serverMSG.getCommandType().equals(CommandTypes.GETUSERCONTRACTS)) {
                            contracts = SQLQuery.getConsumerContacts(serverMSG);
                        } else {
                            contracts = SQLQuery.getAppraiserContacts(serverMSG);
                        }
                        serverMSG.setData(gsonParser.getString(contracts));
                        out.println(fromServerCommandToString(serverMSG));
                    } else if (serverMSG.getCommandType() == CommandTypes.NEWCONTRACT) {
                        GSONParser<Contract> gsonParser = new GSONParser<>();
                        Type type = new TypeToken<Contract>() {
                        }.getType();
                        Contract contract = gsonParser.getObject(serverMSG.getData(), type);
                        SQLQuery.newContract(contract);
                        out.println(fromServerCommandToString(serverMSG));
                    } else if (serverMSG.getCommandType() == CommandTypes.CONTRACTSIGNED ||
                               serverMSG.getCommandType() == CommandTypes.CONTRACTTERMINATED ||
                               serverMSG.getCommandType() == CommandTypes.CONTRACTWAITFORAPPRAISER ||
                               serverMSG.getCommandType() == CommandTypes.CONTRACTWAITFORCONSUMER) {

                        GSONParser<Contract> gsonParser = new GSONParser<>();
                        Type type = new TypeToken<Contract>() {
                        }.getType();
                        Status status = null;
                        if (serverMSG.getCommandType() == CommandTypes.CONTRACTSIGNED) {
                            status = Status.SIGNED;
                        } else if (serverMSG.getCommandType() == CommandTypes.CONTRACTTERMINATED) {
                            status = Status.TERMINATED;
                        } else if (serverMSG.getCommandType() == CommandTypes.CONTRACTWAITFORCONSUMER) {
                            status = Status.WAITFORCONSUMER;
                        } else if (serverMSG.getCommandType() == CommandTypes.CONTRACTWAITFORAPPRAISER) {
                            status = Status.WAITFORAPPRAISER;
                        }
                        Contract contract = gsonParser.getObject(serverMSG.getData(), type);
                        Appraiser appraiser = SQLQuery.getAppraiserByID(SQLQuery.getUserIDByLogin(serverMSG.getUser().getLogin()));
                        SQLQuery.updateContract(contract, status, appraiser);
                        out.println(fromServerCommandToString(serverMSG));
                    } else if (serverMSG.getCommandType() == CommandTypes.CONSUMERDIAGRAM) {
                        if(serverMSG.getData() == null){
                            GSONParser<ArrayList<Map<String, String>>> gsonParser = new GSONParser<>();
                            serverMSG.setData(gsonParser.getString(SQLQuery.getConsumerAppraisedObjectsStats(serverMSG)));
                            out.println(fromServerCommandToString(serverMSG));
                        }else{
                            GSONParser<ArrayList<ChartData<Double, Double>>> gsonParser = new GSONParser<>();
                            serverMSG.setData(gsonParser.getString(SQLQuery.getConsumerStats(serverMSG)));
                            out.println(fromServerCommandToString(serverMSG));
                        }
                    } else if (serverMSG.getCommandType() == CommandTypes.FILTERCONTRACTS) {
                        GSONParser<Contract> parser = new GSONParser<>();
                        GSONParser<Vector<Contract>> answerParser = new GSONParser<>();
                        Type type = new TypeToken<Contract>() {
                        }.getType();
                        Vector<Contract> contractVector =
                                SQLQuery
                                        .getContractsByFilter(parser.getObject(serverMSG.getData(), type), serverMSG);
                        serverMSG.setData(answerParser.getString(contractVector));
                        out.println(fromServerCommandToString(serverMSG));
                    }
//                    else if (serverMSG.getCommandType() == CommandTypes.ADMINPIECHART) {
////                        GSONParser<HashMap<String, Double>> gsonParser = new GSONParser<>();
////                        serverMSG.setData(gsonParser.getString(SQLStatement.getPieChart(serverMSG)));
////                        out.println(fromServerCommandToString(serverMSG));
//                    }
                    else if (serverMSG.getCommandType() == CommandTypes.LINECHART) {
                        GSONParser<ArrayList<ChartData<Date, Double>>> gsonParser = new GSONParser<>();
                        serverMSG.setData(gsonParser.getString(SQLQuery.getLineChart(serverMSG)));
                        out.println(fromServerCommandToString(serverMSG));
                    } else if (serverMSG.getCommandType() == CommandTypes.BESTAPPRAISER) {
                        GSONParser<Vector<ChartData<Appraiser, Integer>>> gsonParser = new GSONParser<>();
                        serverMSG.setData(gsonParser.getString(SQLQuery.getBestAppraiser()));
                        out.println(fromServerCommandToString(serverMSG));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                in.close();
                out.close();
                clientSocket.close();
                Map usersConnected = new HashMap<>();
                usersConnected.put("disconnected", answer.getUser().getLogin());
                MainWindowController.setUsersConnected(usersConnected);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void correctSignIn(ServerMSG answer) throws SQLException {
            Connection connection =
                    DriverManager.getConnection("jdbc:mysql://localhost:3306/cw5sem", "root", "123456");
            String sql = "SELECT login, passwordMask, firstName, surname, patronymic, phone, role, isBlocked FROM users\n" +
                         "WHERE login = '" + answer.getUser().getLogin() + "';";
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                User user = new User(rs.getString("login"), rs.getString("passwordMask"),
                                     rs.getString("firstName"), rs.getString("surname"),
                                     rs.getString("patronymic"), rs.getString("phone"), Role.valueOf(rs.getInt("role")),
                                     rs.getBoolean("isBlocked"));

                String checkingPassword = answer.getUser().getPasswordMask();

                answer.setUser(user);
                answer.setCommandType(CommandTypes.SIGNIN);
                if (user.isBlocked()) {
                    answer.setData("blocked");
                } else if (BCrypt.checkpw(checkingPassword, answer.getUser().getPasswordMask())) {
                    answer.setData("success");
                } else {
                    answer.setData("password");
                }
                return;
            }
            answer.setUser(null);
            answer.setCommandType(CommandTypes.SIGNIN);
            answer.setData("login");
            return;
        }

        private ServerMSG fromStringToServerCommand(String json) {
            GsonBuilder builder = new GsonBuilder();
            Gson gson = builder.create();
            ServerMSG serverMSG = gson.fromJson(json, ServerMSG.class);
            System.out.println(json);
            return serverMSG;
        }

        private String fromServerCommandToString(ServerMSG serverMSG) {
            Gson gson = new Gson();
            String json = gson.toJson(serverMSG);
            return json;
        }

        private void newUser(ServerMSG answer) {
            String sql = "INSERT INTO users (login, passwordMask, firstName, surname, patronymic, phone, role)" +
                         " VALUES(?,?,?,?,?,?,?)";
            try {
                Connection connection =
                        DriverManager.getConnection("jdbc:mysql://localhost:3306/cw5sem", "root", "123456");
                String hashed = BCrypt.hashpw(answer.getUser().getPasswordMask(), BCrypt.gensalt(12));
                answer.getUser().setPasswordMask(hashed);

                Role roleType = Role.ADMINISTRATOR;
                int roleTypeID = answer.getUser().getRole().getValue();

                PreparedStatement statement = connection.prepareStatement(sql);
                statement.setString(1, answer.getUser().getLogin());
                statement.setString(2, answer.getUser().getPasswordMask());
                statement.setString(3, answer.getUser().getFirstName());
                statement.setString(4, answer.getUser().getSurname());
                statement.setString(5, answer.getUser().getPatronymic());
                statement.setString(6, answer.getUser().getPhone());
                statement.setInt(7, roleTypeID);
                statement.executeUpdate();
                answer.setData("success");
                return;
            } catch (SQLException throwables) {
                answer.setData("login exists");
                return;
            }
        }
    }
}
