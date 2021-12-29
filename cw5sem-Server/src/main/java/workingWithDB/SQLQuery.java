package workingWithDB;

import appraisal.Contract;
import appraisal.Contract.Status;
import appraisal.AppraisalObject;
import appraisal.ChartData;
import org.mindrot.jbcrypt.BCrypt;
import personalInfo.Appraiser;
import personalInfo.User;
import personalInfo.User.Role;
import serverFiles.ServerMSG;

import java.sql.*;
import java.sql.Date;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.*;

public class SQLQuery {

    static private Connection connection;

    static {
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/cw5sem", "root", "123456");

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    static public ResultSet getAppraisers() throws SQLException {
        String sql = "SELECT users.login, users.passwordMask, users.firstName, users.surname, users.patronymic, users.phone, " +
                     "users.role, users.isBlocked, appraisers.salary FROM users\n" +
                     "INNER JOIN appraisers ON users.userID = appraisers.appraiserID\n" +
                     "WHERE users.role = 1;";
        Statement stmt = connection.createStatement();

        return stmt.executeQuery(sql);
    }

    static public void deleteAppraiser(String login) {
        try {
            String sql = "UPDATE users SET role = '0' WHERE (login = '" + login + "');";
            Statement stmt = connection.createStatement();
            stmt.execute(sql);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return;
    }

    static public void blockUser(String login) {
        try {
            String sql = "SELECT isBlocked FROM users WHERE (login = '" + login + "');";
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                if (rs.getBoolean("isBlocked")) {
                    sql = "UPDATE users SET isBlocked = '0' WHERE (login = '" + login + "');";
                } else {
                    sql = "UPDATE users SET isBlocked = '1' WHERE (login = '" + login + "');";
                }
            }
            stmt.execute(sql);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return;
    }

    static public void editAppraiser(Appraiser appraiser) {
        try {
            String sql = "SELECT isBlocked, userID FROM users WHERE (login = '" + appraiser.getLogin() + "');";
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            Integer userID = null;
            while (rs.next()) {
                sql = "UPDATE users SET isBlocked = " + appraiser.isBlocked() + " WHERE (login = '" + appraiser.getLogin() +
                      "');";
                userID = rs.getInt("userID");
            }
            stmt.execute(sql);
            sql = "UPDATE appraisers SET salary = '" + appraiser.getSalary() + "' WHERE (appraiserID = '" + userID.toString() +
                  "');";
            stmt.execute(sql);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    static public void addAppraiser(Appraiser appraiser) {
        try {
            String sql = "SELECT userID FROM users WHERE (login = '" + appraiser.getLogin() + "');";
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            Integer userID = null;
            while (rs.next()) {
                sql = "UPDATE users SET role = 1 WHERE (login = '" + appraiser.getLogin() + "');";
                userID = rs.getInt("userID");
            }
            stmt.execute(sql);
//            sql = "UPDATE appraisers SET salary = '" + appraiser.getSalary() + "' WHERE (appraiserID = '" + userID.toString() +
//                  "');";
            sql ="INSERT INTO appraisers (salary, appraiserID) VALUES ('" + appraiser.getSalary() + "', '" + userID.toString() +"');";
            stmt.execute(sql);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    static public ResultSet getConsumers() throws SQLException {
        String sql = "SELECT login, passwordMask, firstName, surname, patronymic, phone, " +
                     "role, isBlocked FROM users\n" +
                     "WHERE role = 0;";
        Statement stmt = connection.createStatement();
        return stmt.executeQuery(sql);
    }

    static public ResultSet getConsumersByFilter(User user, boolean any) throws SQLException {
        String sql;
        if (any) {
            sql = "SELECT login, passwordMask, firstName, surname, patronymic, phone, " +
                  "role, isBlocked FROM users\n" +
                  "WHERE login LIKE '%" + user.getLogin() + "%' AND " +
                  "firstName LIKE '%" + user.getFirstName() + "%' AND " +
                  "surname LIKE '%" + user.getSurname() + "%' AND " +
                  "phone LIKE '%" + user.getPhone() + "%' AND " +
                  "role = 0 AND " +
                  "patronymic LIKE '%" + user.getPatronymic() + "%';";
        } else {
            sql = "SELECT login, passwordMask, firstName, surname, patronymic, phone, " +
                  "role, isBlocked FROM users\n" +
                  "WHERE login LIKE '%" + user.getLogin() + "%' AND " +
                  "firstName LIKE '%" + user.getFirstName() + "%' AND " +
                  "surname LIKE '%" + user.getSurname() + "%' AND " +
                  "patronymic LIKE '%" + user.getPatronymic() + "%' AND " +
                  "phone LIKE '%" + user.getPhone() + "%' AND " +
                  "role = 0 AND " +
                  "isBlocked = " + user.isBlocked() + ";";
        }
        Statement stmt = connection.createStatement();
        return stmt.executeQuery(sql);

    }

    static public ResultSet getUsers() throws SQLException {
        String sql = "SELECT login, passwordMask, firstName, surname, patronymic, phone, " +
                     "role, isBlocked FROM users\n" +
                     "WHERE role = 0 OR role = 1;";
        Statement stmt = connection.createStatement();
        return stmt.executeQuery(sql);
    }

    static public void editUser(User user) {
        try {
            PreparedStatement st = connection.prepareStatement("UPDATE users SET login = ?," +
                                                               "passwordMask = ?, firstName = ?," +
                                                               "surname = ?, patronymic = ?, phone = ? " +
                                                               "WHERE login = '" + user.getLogin() + "';");

            String hashed = BCrypt.hashpw(user.getPasswordMask(), BCrypt.gensalt(12));

            st.setString(1, user.getLogin());
            st.setString(2, hashed);
            st.setString(3, user.getFirstName());
            st.setString(4, user.getSurname());
            st.setString(5, user.getPatronymic());
            st.setString(6, user.getPhone());
            st.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e);
        }
    }
    static public void addObjectType(String objectType, ServerMSG answer){
        try {
            PreparedStatement st = connection.prepareStatement(
                    "SELECT type FROM objectstypes WHERE type = ?;");
            st.setString(1, objectType);

            ResultSet rs = st.executeQuery();

            while(rs.next()){
                answer.setData("error");
                return;
            }

            st = connection.prepareStatement(
                    "INSERT INTO objectstypes (type) VALUES (?);");
            st.setString(1, objectType);
            st.executeUpdate();
        } catch (SQLException e) {
            answer.setData("error");
            System.out.println(e);
            return;
        }
    }
    static public void deleteObjectType(String objectType, ServerMSG answer){
        try {
            PreparedStatement st = connection.prepareStatement(
                    "SELECT type FROM objectstypes WHERE type = ?;");
            st.setString(1, objectType);

            ResultSet rs = st.executeQuery();

            if(!rs.next()){
                answer.setData("error");
                return;
            }

            st = connection.prepareStatement(
                    "DELETE FROM objectstypes WHERE type = ?;");

            st.setString(1, objectType);
            st.executeUpdate();
        } catch (SQLException e) {
            answer.setData("error");
            System.out.println(e);
            return;
        }
    }
    static public void addAppraisalObject(AppraisalObject appraisalObject, ServerMSG answer) {
        try {
            PreparedStatement st = connection.prepareStatement(
                    "SELECT typeID FROM objectstypes WHERE type = ?;");
            st.setString(1, appraisalObject.getName());

            ResultSet rs = st.executeQuery();
            Integer typeID = 0;
            while (rs.next()) {
                typeID = rs.getInt("typeID");
            }
            st = connection.prepareStatement(
                    "SELECT typeID, description FROM objectstoappraise WHERE typeID = ? AND description = ?;");
            st.setInt(1, typeID);
            st.setString(2, appraisalObject.getDescription());
            ResultSet rs1 = st.executeQuery();
            while(rs1.next()){
                answer.setData("error");
                return;
            }

            st = connection.prepareStatement(
                    "INSERT INTO objectstoappraise (typeID, description) VALUES (?, ?);");
            st.setInt(1, typeID);
            st.setString(2, appraisalObject.getDescription());
            st.executeUpdate();


        } catch (SQLException e) {
            answer.setData("error");
            System.out.println(e);
            return;
        }
    }
    static public ArrayList<String> getObjectsTypes(){
        ArrayList<String> objectsTypes = new ArrayList<>();
        try {
            String sql = "SELECT type FROM objectstypes;";
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                String objectsType = rs.getString("type");
                objectsTypes.add(objectsType);
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return objectsTypes;
    }

    static public Vector<AppraisalObject> getAppraisalObjects() {
        Vector<AppraisalObject> appraisalObjects = new Vector<>();
        try {
            String sql = "SELECT objectstoappraise.objectID, objectstypes.type," +
                    "objectstoappraise.description FROM objectstoappraise\n" +
                    "INNER JOIN objectstypes ON objectstoappraise.typeID = objectstypes.typeID;";
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                AppraisalObject appraisalObject = new AppraisalObject(rs.getInt("objectID"),
                                                                         0,
                                                                         rs.getString("type"),
                                                                         rs.getString("description"));

                appraisalObjects.add(appraisalObject);
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return appraisalObjects;
    }

    static public void deleteAppraisalObject(Integer objectID) {
        try {
            PreparedStatement st = connection.prepareStatement(
                    "DELETE FROM objectstoappraise WHERE objectID = ?;");
            st.setInt(1, objectID);
            st.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return;
    }

    static public Vector<AppraisalObject> getObjectsByFilter(AppraisalObject appraisalObject) {
        Vector<AppraisalObject> appraisalObjects = new Vector<>();
        try {
            PreparedStatement st;
            PreparedStatement st1;
            st = connection.prepareStatement(
                    "SELECT objectstoappraise.objectID, objectstypes.type,objectstoappraise.description FROM objectstoappraise\n" +
                     "INNER JOIN objectstypes ON objectstoappraise.typeID = objectstypes.typeID\n" +
                    "WHERE objectstypes.type LIKE ? AND objectstoappraise.description LIKE ?;");
            st.setString(1, "%" + appraisalObject.getName() + "%");
            st.setString(2, "%" + appraisalObject.getDescription() + "%");

            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                AppraisalObject bufAppraisalObject = new AppraisalObject(rs.getInt("objectID"),
                                                                            rs.getString("type"),
                                                                            rs.getString("description"));
                appraisalObjects.add(bufAppraisalObject);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return appraisalObjects;
    }

    static public void editAppraisalObject(AppraisalObject appraisalObject, ServerMSG answer) {
        try {

            PreparedStatement st = connection.prepareStatement(
                    "UPDATE objectstoappraise " +
                            "SET description = ? " +
                            "WHERE objectId = ?;");

            st.setString(1, appraisalObject.getDescription());
            st.setInt(2, appraisalObject.getID());
            st.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e);
            return;
        }
    }

    static public Vector<Contract> getConsumerContacts(ServerMSG answer) {
        Vector<Contract> contracts = new Vector<>();
        try {
            PreparedStatement st = connection.prepareStatement(
                    "SELECT * FROM appraisalagreement " +
                    "INNER JOIN objectstoappraise ON appraisalagreement.objectID = objectstoappraise.objectID " +
                    "INNER JOIN objectstypes ON objectstoappraise.typeID = objectstypes.typeID\n" +
                    "WHERE appraisalagreement.consumerID = ?;");

            st.setInt(1, getUserIDByLogin(answer.getUser().getLogin()));
            return getContracts(contracts, st);
        } catch (SQLException e) {
            System.out.println(e);
        }
        return null;
    }

    static public Vector<Contract> getAppraiserContacts(ServerMSG answer) {
        Vector<Contract> contracts = new Vector<>();
        try {
            PreparedStatement st = connection.prepareStatement(
                    "SELECT * FROM appraisalagreement " +
                    "INNER JOIN objectstoappraise ON appraisalagreement.objectID = objectstoappraise.objectID " +
                    "INNER JOIN objectstypes ON objectstoappraise.typeID = objectstypes.typeID\n" +
                    "WHERE currentAppraiserID IS NULL OR currentAppraiserID = ?;");
            st.setInt(1, getUserIDByLogin(answer.getUser().getLogin()));
            return getContracts(contracts, st);
        } catch (SQLException e) {
            System.out.println(e);
        }
        return null;
    }

    private static Vector<Contract> getContracts(Vector<Contract> contracts, PreparedStatement st) throws SQLException {
        ResultSet rs = st.executeQuery();
        while (rs.next()) {
            //get object
            AppraisalObject appraisalObject = new AppraisalObject(rs.getInt("objectID"),
                                                                     rs.getString("type"),
                                                                     rs.getString("description"));


            User consumer = getUserByID(rs.getInt("consumerID"));
            Appraiser appraiser = getAppraiserByID(rs.getInt("currentAppraiserID"));

            contracts.add(new Contract(rs.getInt("appraisalAgreementID"),
                                       appraisalObject, rs.getDouble("priceForAppraisal"),
                                       rs.getDouble("expectedPrice"),
                                       rs.getDouble("appraiserPrice"),
                                       rs.getString("dateOfSigning"),
                                       rs.getString("commentFromConsumer"),
                                       rs.getString("commentFromAppraiser"),
                                       Status.valueOf(rs.getInt("status")),
                                       consumer, appraiser));
        }
        return contracts;
    }



    static public User getUserByID(Integer userID) {
        try {
            PreparedStatement st = connection.prepareStatement("SELECT * FROM users WHERE userID = ?;");

            st.setInt(1, userID);
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                return new User(rs.getString("login"), rs.getString("passwordMask"),
                                rs.getString("firstName"), rs.getString("surname"),
                                rs.getString("patronymic"),rs.getString("phone"), Role.valueOf(rs.getInt("role")),
                                rs.getBoolean("isBlocked"));
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return null;
    }

    static public Integer getUserIDByLogin(String login) {
        try {
            PreparedStatement st = connection.prepareStatement(
                    "SELECT userID FROM users WHERE login = ?;");
            st.setString(1, login);
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                return rs.getInt("userID");
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return 0;
    }

    static public Appraiser getAppraiserByID(Integer userID) {
        try {
            PreparedStatement st = connection.prepareStatement(
                    "SELECT * FROM users\n" +
                    "INNER JOIN appraisers ON users.userID = appraisers.appraiserID\n" +
                    "WHERE users.role = 1 AND appraisers.appraiserID = ?;");

            st.setInt(1, userID);
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                return new Appraiser(rs.getString("login"), rs.getString("passwordMask"),
                                  rs.getString("firstName"), rs.getString("surname"),
                                  rs.getString("patronymic"),rs.getString("phone"), Role.valueOf(rs.getInt("role")),
                                  rs.getBoolean("isBlocked"), rs.getDouble("salary"));
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return null;
    }

    static public void newContract(Contract contract) {
        try {
            PreparedStatement st = connection.prepareStatement(
                    "INSERT INTO appraisalagreement (objectID, expectedPrice, " +
                    "status, commentFromConsumer, ConsumerID) VALUES (?, ?, ?, ?, ?);");

            Integer userID = getUserIDByLogin(contract.getConsumer().getLogin());
            st.setInt(1, contract.getAppraisalObject().getID());
            st.setDouble(2, contract.getExpectedPrice());
            st.setInt(3, contract.getStatus().getValue());
            st.setString(4, contract.getCommentFromConsumer());
            st.setInt(5, userID);
            st.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    static public void updateContract(Contract contract, Status status, Appraiser appraiser) {
        try {
            PreparedStatement st;
            Integer appraiserID = null;
            if (contract.getAppraiser() == null) {
//                if(status == Status.TERMINATED && appraiser != null){
                    st = connection.prepareStatement(
                            "UPDATE appraisalagreement SET objectID = ?, priceForAppraisal = ?, " +
                            "status = ?, commentFromConsumer = ?, dateOfSigning = ?, " +
                            "expectedPrice = ?, appraiserPrice = ?, commentFromAppraiser = ?, currentAppraiserID = ? " +
                            "WHERE appraisalagreementID = ?;");
                    st.setInt(1, contract.getAppraisalObject().getID());
                    st.setDouble(2, contract.getPriceForAppraisal());
                    st.setInt(3, status.getValue());
                    st.setString(4, contract.getCommentFromConsumer());
                    if (contract.getDateOfSigning() == null) {
                        st.setDate(5, null);
                    } else {
                        st.setDate(5, Date.valueOf(contract.getDateOfSigning()));
                    }
                    st.setDouble(6, contract.getExpectedPrice());
                    st.setDouble(7, contract.getAppraiserPrice());
                    st.setString(8, contract.getCommentFromAppraiser());
                    st.setInt(9, getUserIDByLogin(appraiser.getLogin()));
                    st.setInt(10, contract.getContractID());
//                }
//                else {
//                    st = connection.prepareStatement(
//                            "UPDATE appraisalagreement SET objectID = ?, priceForAppraisal = ?, " +
//                            "status = ?, commentFromConsumer = ?, dateOfSigning = ?, " +
//                            "expectedPrice = ?, appraiserPrice = ?, commentFromAppraiser = ? " +
//                            "WHERE appraisalagreementID = ?;");
//                    st.setInt(1, contract.getAppraisalObject().getID());
//                    st.setDouble(2, contract.getPriceForAppraisal());
//                    st.setInt(3, status.getValue());
//                    st.setString(4, contract.getCommentFromConsumer());
//                    if (contract.getDateOfSigning() == null) {
//                        st.setDate(5, null);
//                    } else {
//                        st.setDate(5, Date.valueOf(contract.getDateOfSigning()));
//                    }
//                    st.setDouble(6, contract.getExpectedPrice());
//                    st.setDouble(7, contract.getAppraiserPrice());
//                    st.setString(8, contract.getCommentFromAppraiser());
//                    st.setInt(9, contract.getContractID());
//                }
            } else {
                appraiserID = getUserIDByLogin(contract.getAppraiser().getLogin());

                    st = connection.prepareStatement(
                            "UPDATE appraisalagreement SET objectID = ?, priceForAppraisal = ?," +
                            "status = ?, commentFromConsumer = ?, dateOfSigning = ?, " +
                            "expectedPrice = ?, appraiserPrice = ?, commentFromAppraiser = ?, " +
                            "currentAppraiserID = ? " +
                            "WHERE appraisalagreementID = ?;");
                    st.setInt(1, contract.getAppraisalObject().getID());
                    st.setDouble(2, contract.getPriceForAppraisal());
                    st.setInt(3, status.getValue());
                    st.setString(4, contract.getCommentFromConsumer());
                    try {
                        st.setDate(5, Date.valueOf(contract.getDateOfSigning()));
                    } catch (Exception e) {
                        st.setDate(5, null);
                    }
                    st.setDouble(6, contract.getExpectedPrice());
                    st.setDouble(7, contract.getAppraiserPrice());
                    st.setString(8, contract.getCommentFromAppraiser());
                    st.setInt(9, appraiserID);
                    st.setInt(10, contract.getContractID());
                }

            st.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    static public ArrayList getConsumerStats(ServerMSG answer) {

        ArrayList<ChartData<Double, Double>> result = new ArrayList<>();
        try {
            PreparedStatement st = connection.prepareStatement(
                    "SELECT expectedPrice, appraiserPrice\n" +
                    "FROM appraisalagreement\n" +
                    "WHERE consumerID = ? AND dateOfSigning is not null AND status != 3;");
            Integer userID = getUserIDByLogin(answer.getUser().getLogin());
            st.setInt(1, userID);
            //st.setDate(2, Date.valueOf(dateStr));
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                ChartData<Double, Double> result1 = new ChartData<>();
                result1.setValue1(rs.getDouble("expectedPrice"));
                result1.setValue2(rs.getDouble("appraiserPrice"));
                result.add(result1);
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return result;
    }
    static public ArrayList getConsumerAppraisedObjectsStats(ServerMSG answer){
        ArrayList<Map<String,String>> result = new ArrayList<>();

        try {
            PreparedStatement st = connection.prepareStatement(
                    "SELECT objectstypes.type type, dateOfSigning dateOfSigning\n" +
                            "FROM appraisalagreement\n" +
                            "INNER JOIN objectstoappraise ON appraisalagreement.objectID = objectstoappraise.objectID\n" +
                            "INNER JOIN objectstypes ON objectstoappraise.typeID = objectstypes.typeID\n" +
                            "WHERE consumerID = ? AND dateOfSigning is not null AND status != 3;");
            Integer userID = getUserIDByLogin(answer.getUser().getLogin());
            st.setInt(1, userID);
            //st.setDate(2, Date.valueOf(dateStr));
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
//                result.setValue1(rs.getDouble("expectedPrice"));
//                result.setValue2(rs.getDouble("appraiserPrice"));
                Map<String,String> result1 = new HashMap<>();
                result1.put(rs.getString("type"), rs.getString("dateOfSigning"));
                result.add(result1);
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return result;
    }

    static public Vector<Contract> getContractsByFilter(Contract contract, ServerMSG serverMSG) {
        Vector<Contract> contracts = new Vector<>();
        User user = serverMSG.getUser();
        try {
            PreparedStatement st;
            String sql = null;
            if(user.getRole().equals(Role.CONSUMER)){
                sql = "SELECT * FROM appraisalagreement\n" +
                      "INNER JOIN objectstoappraise ON appraisalagreement.objectID = objectstoappraise.objectID\n" +
                      "INNER JOIN objectstypes ON objectstoappraise.typeID = objectstypes.typeID\n" +
                      "WHERE appraisalagreement.status = 4 AND objectstypes.type LIKE ? AND " +
                      "appraisalagreement.commentFromConsumer LIKE ? AND appraisalagreement.commentFromAppraiser LIKE ? " +
                      "AND appraisalagreement.consumerID = ?";
            }else if(user.getRole().equals(Role.APPRAISER)) {
                sql = "SELECT * FROM appraisalagreement\n" +
                      "INNER JOIN objectstoappraise ON appraisalagreement.objectID = objectstoappraise.objectID\n" +
                      "INNER JOIN objectstypes ON objectstoappraise.typeID = objectstypes.typeID\n" +
                      "WHERE appraisalagreement.status = 4 AND objectstypes.type LIKE ? AND " +
                      "appraisalagreement.commentFromConsumer LIKE ? AND appraisalagreement.commentFromAppraiser LIKE ? " +
                      "AND appraisalagreement.currentAppraiserID = ?";
            }
            if (contract.getPriceForAppraisal() == null) {
                sql += " AND appraisalagreement.priceForAppraisal is not null";
            } else {
                sql += " AND appraisalagreement.priceForAppraisal = ?";
            }
            if (contract.getAppraiserPrice() == null) {
                sql += " AND appraisalagreement.appraiserPrice is not null";
            } else {
                sql += " AND appraisalagreement.appraiserPrice = ?";
            }
            if (contract.getContractID() == null) {
                sql += " AND appraisalagreement.appraisalAgreementID is not null;";
            } else {
                sql += " AND appraisalagreement.appraisalAgreementID = ?;";
            }
            st = connection.prepareStatement(sql);
            st.setString(1, "%" + contract.getAppraisalObject().getName() + "%");
            st.setString(2, "%" + contract.getCommentFromConsumer() + "%");
            st.setString(3, "%" + contract.getCommentFromAppraiser() + "%");
            st.setInt(4, getUserIDByLogin(user.getLogin()));

            Integer count = 5;

            if (contract.getPriceForAppraisal() != null) {
                st.setDouble(count, contract.getPriceForAppraisal());
                count++;
            }
            if (contract.getAppraiserPrice() != null) {
                st.setDouble(count, contract.getAppraiserPrice());
                count++;
            }
            if (contract.getContractID() != null) {
                st.setInt(count, contract.getContractID());
                count++;
            }

            ResultSet rs = st.executeQuery();

            while (rs.next()) {

                User consumer = getUserByID(rs.getInt("consumerID"));
                Appraiser appraiser = getAppraiserByID(rs.getInt("currentAppraiserID"));

                AppraisalObject object = new AppraisalObject();
                object.setName(rs.getString("type"));
                contracts.add(new Contract(rs.getInt("appraisalAgreementID"),
                                           object, rs.getDouble("priceForAppraisal"),
                                           rs.getDouble("expectedPrice"),
                                           rs.getDouble("appraiserPrice"),
                                           rs.getString("dateOfSigning"),
                                           rs.getString("commentFromConsumer"),
                                           rs.getString("commentFromAppraiser"), Status.SIGNED, consumer, appraiser));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return contracts;
    }


    static public ArrayList<ChartData<Date, Double>> getLineChart(ServerMSG answer) throws ParseException {
        ArrayList<ChartData<Date, Double>> result = new ArrayList<>();

        try {
            int i = 6;
            while(i>0){

                ChartData<Date, Double> res = new ChartData<>();
                PreparedStatement st = connection.prepareStatement(
                        "SELECT SUM(priceForAppraisal) income\n" +
                                "FROM appraisalagreement\n" +
                                "WHERE priceForAppraisal != 0 AND dateOfSigning is not null AND MONTH(?) = MONTH(dateOfSigning) AND YEAR(?) = YEAR(dateOfSigning)" +
                                "AND status != 3;");
                st.setDate(1,Date.valueOf(LocalDate.now().minusMonths(i-1)));
                st.setDate(2, Date.valueOf(LocalDate.now().minusMonths(i-1)));
                ResultSet rs = st.executeQuery();
                while (rs.next()) {
                    res.setValue1(Date.valueOf(LocalDate.now().minusMonths(i-1)));
                    res.setValue2(rs.getDouble("income"));
                }
                result.add(res);
                i--;
            }

        } catch (SQLException e) {
            System.out.println(e);
        }
        return result;
    }

    static public Vector<ChartData<Appraiser, Integer>> getBestAppraiser() {
        Vector<ChartData<Appraiser, Integer>> appraiserStats = new Vector<>();
        try {
            PreparedStatement st = connection.prepareStatement(
                    "SELECT currentAppraiserID, count(*) AS stats FROM appraisalagreement\n" +
                    "where status = 4\n" +
                    "group by currentAppraiserID\n" +
                    "order by stats desc;");

            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                 appraiserStats.add(new ChartData<Appraiser, Integer>(getAppraiserByID(rs.getInt("currentAppraiserID")),
                                                                rs.getInt("stats")));
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return appraiserStats;
    }

}