package in.rohan.webscraper.database;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SqlManager {
    private static final String OLDEST_VALUE = "2"; /// Removes oldest given value from table

    public static void createTable() {
        try {
            deleteTable();
        } catch (Exception ignored) {
            // table probably does not exist
        }
        Sqlite.onUpdate("CREATE TABLE IF NOT EXISTS posts(ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, title VARCHAR, url VARCHAR, content VARCHAR)");
        System.out.println("New Table posts Created!");
    }

    private static void deleteTable() {
        Sqlite.onUpdate("DROP TABLE posts");
        System.out.println("posts Table Deleted!");
    }

    public static void getValues() {

        ResultSet resultSet = Sqlite.onQuery("SELECT * FROM posts");

        try {
            while (resultSet.next()) {
                String title = resultSet.getString("title");
                String url = resultSet.getString("url");
                String content = resultSet.getString("content");

                System.out.println("Title = " + title);
                System.out.println("URL = " + url);
                System.out.println("Content = " + content);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void setValues(String title, String url, String content) {

        Sqlite.onUpdate("INSERT INTO posts(title, url, content) VALUES('" +
                title +"', '" +
                url +"', '" +
                content + "')");
    }

    public static void deleteOldValues() {
        Sqlite.onUpdate("DELETE FROM posts WHERE id IN (SELECT id FROM posts ORDER BY id ASC LIMIT " +
                OLDEST_VALUE +")");
    }
}
