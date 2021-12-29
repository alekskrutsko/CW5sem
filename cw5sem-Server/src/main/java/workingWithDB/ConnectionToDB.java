package workingWithDB;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class ConnectionToDB {
    static private Connection conn;

    public String connect() throws SQLException {
        String msg = new String();
        try {

            String url = "jdbc:mysql://localhost:3306/cw5sem";
            String username = "root";
            String password = "123456";
            // create a connection to the database
            try{
                conn = (Connection) DriverManager.getConnection(url, username, password);
            }
            catch (SQLException e){
                conn = (Connection) DriverManager.getConnection("jdbc:mysql://localhost:3306/mysql", "root", "123456");
                Statement s = conn.createStatement();
                s.executeUpdate("CREATE DATABASE `cw5sem` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;");
            }
            msg = "Соединение с базой данных установлено!";

            createTables();

        } catch (SQLException e) {
            //ignore
        } finally {
            try {
                if (conn != null) {
                    ((Connection) conn).close();
                }
            } catch (SQLException ex) {
                msg = ex.getMessage();
            }
        }
        System.out.println(ConnectionToDB.isConnected());
        return msg;
    }

    public String disconnect() {
        if (conn != null) {
            try {
                conn.close();
                conn = null;
            } catch (SQLException e) {
                //ignore
            }
        }
        System.out.println(ConnectionToDB.isConnected());
        return "Нет соединения с БД!";
    }

    static public Boolean isConnected() {
        return conn != null;
    }

    static public Connection getConn() {
        return conn;
    }

    public void createTables() {
        String sql = "CREATE TABLE IF NOT EXISTS `users` (\n" +
                     "  `userID` int NOT NULL AUTO_INCREMENT,\n" +
                     "  `login` varchar(50) NOT NULL,\n" +
                     "  `passwordMask` varchar(200) NOT NULL,\n" +
                     "  `firstName` varchar(50) NOT NULL,\n" +
                     "  `surname` varchar(50) NOT NULL,\n" +
                     "  `patronymic` varchar(50) NOT NULL,\n" +
                     "  `phone` varchar(50) NOT NULL,\n" +
                     "  `role` int NOT NULL DEFAULT '0',\n" +
                     "  `isBlocked` tinyint NOT NULL DEFAULT '0',\n" +
                     "  PRIMARY KEY (`userID`),\n" +
                     "  UNIQUE KEY `login_UNIQUE` (`login`)\n" +
                     ");";
        try {
            Statement stmt = conn.createStatement();
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        sql = "CREATE TABLE IF NOT EXISTS `appraisers` (\n" +
              "  `appraiserID` int NOT NULL,\n" +
              "  `salary` double DEFAULT '0',\n" +
              "  PRIMARY KEY (`appraiserID`),\n" +
              "  CONSTRAINT `appraiserID` FOREIGN KEY (`appraiserID`) REFERENCES `users` (`userID`) ON DELETE CASCADE ON UPDATE CASCADE\n" +
              ");";
        try {
            Statement stmt = conn.createStatement();
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        sql = "CREATE TABLE IF NOT EXISTS `objectstypes` (\n" +
                "  `typeID` int NOT NULL AUTO_INCREMENT,\n" +
                "  `type` varchar(50) NOT NULL,\n" +
                "  PRIMARY KEY (`typeID`)\n" +
                ");";
        try {
            Statement stmt = conn.createStatement();
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }


        sql = "CREATE TABLE IF NOT EXISTS `objectstoappraise` (\n" +
              "  `objectId` int NOT NULL AUTO_INCREMENT,\n" +
              "  `objectTypeId` int NOT NULL,\n" +
              "  `name` varchar(50) NOT NULL,\n" +
              "  `description` varchar(100) DEFAULT NULL,\n" +
              "  PRIMARY KEY (`objectId`),\n" +
              "  UNIQUE KEY `name_UNIQUE` (`name`),\n" +
              "  CONSTRAINT `objectTypeId` FOREIGN KEY (`objectTypeId`) REFERENCES `objectstypes` (`typeID`)\n" +
              ");";
        try {
            Statement stmt = conn.createStatement();
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        sql = "CREATE TABLE IF NOT EXISTS `appraisalagreement` (\n" +
                "  `appraisalAgreementID` int NOT NULL AUTO_INCREMENT,\n" +
                "  `dateOfSigning` date DEFAULT NULL,\n" +
                "  `objectID` int NOT NULL,\n" +
                "  `priceForAppraisal` double DEFAULT NOT NULL,\n" +
                "  `expectedPrice` double DEFAULT NULL,\n" +
                "  `appraiserPrice` double DEFAULT NULL,\n" +
                "  `status` int NOT NULL DEFAULT '0',\n" +
                "  `commentFromConsumer` varchar(100) DEFAULT NULL,\n" +
                "  `commentFromAppraiser` varchar(100) DEFAULT NULL,\n" +
                "  `consumerID` int NOT NULL,\n" +
                "  `currentAppraiserID` int DEFAULT NULL,\n" +
                "  PRIMARY KEY (`appraisalAgreementID`),\n" +
                "  CONSTRAINT `appraiserIDFK` FOREIGN KEY (`currentAppraiserID`) REFERENCES `appraisers` (`appraiserID`) ON DELETE CASCADE ON UPDATE CASCADE,\n" +
                "  CONSTRAINT `object` FOREIGN KEY (`objectID`) REFERENCES `objectstoappraise` (`objectId`) ON DELETE CASCADE ON UPDATE CASCADE,\n" +
                "  CONSTRAINT `userIDFK` FOREIGN KEY (`consumerID`) REFERENCES `users` (`userID`) ON DELETE CASCADE ON UPDATE CASCADE\n" +
                ");";
        try {
            Statement stmt = conn.createStatement();
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }



    }
}

