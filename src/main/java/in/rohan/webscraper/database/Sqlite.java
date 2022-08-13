package in.rohan.webscraper.database;

import java.io.File;
import java.sql.*;

public class Sqlite {

    private static Connection connection;
    private static Statement statement;

    public static void setConnection() {
        connection = null;
        try {
            File file = new File("database.db");
            if (!file.exists()) {
                file.createNewFile();
            }
            String url = "jdbc:sqlite:" + file.getPath();
            connection = DriverManager.getConnection(url);
            statement = connection.createStatement();
            System.out.println("Connected to Database");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void onUpdate(String sql) {
        if (statement != null) {
            try {
                statement.execute(sql);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static ResultSet onQuery(String sql) {
        if (statement != null) {
            try {
                return statement.executeQuery(sql);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
