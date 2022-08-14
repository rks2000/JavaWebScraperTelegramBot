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
        Sqlite.onUpdate("CREATE TABLE IF NOT EXISTS posts(ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "title VARCHAR, url VARCHAR, content VARCHAR, is_sent BOOLEAN, " +
                "UNIQUE(title, url))");      ///// using UNIQUE to ignore repeated values
        System.out.println("New Table posts Created!");
    }

    private static void deleteTable() {
        Sqlite.onUpdate("DROP TABLE posts");
        System.out.println("posts Table Deleted!");
    }

    public static void getValuesFromDatabase() {

        ResultSet resultSet = Sqlite.onQuery("SELECT * FROM posts WHERE is_sent = 'false'");

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
        String updateIsSentQuery = "UPDATE posts SET is_sent = true";
        Sqlite.onUpdate(updateIsSentQuery);
    }

    public static void setPostValuesInDatabase(String title, String url, String content, boolean is_sent) {

        Sqlite.onUpdate("INSERT OR IGNORE INTO posts(title, url, content, is_sent) VALUES('" +
                title +"', '" +
                url +"', '" +
                content + "', '" +
                is_sent + "')");  /// Ignores if values are not unique
    }

    public static void deleteOldValuesFromDatabase() {
        Sqlite.onUpdate("DELETE FROM posts WHERE id IN (SELECT id FROM posts ORDER BY id ASC LIMIT " +
                OLDEST_VALUE +")");
    }
}
