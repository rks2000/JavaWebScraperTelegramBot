package in.rohan.webscraper.database;

import in.rohan.webscraper.Post;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class SqlManager {
    private static final String OLDEST_VALUE = "2"; /// Removes specified number of older posts from the table

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

    public static ArrayList<Post> getValuesFromDatabase() {

        ArrayList<Post> posts = new ArrayList<>();

        ResultSet resultSet = Sqlite.onQuery("SELECT * FROM posts WHERE is_sent = 'false'");

        try {
            while (resultSet.next()) {
                String title = resultSet.getString("title");
                String url = resultSet.getString("url");
                String content = resultSet.getString("content");

                posts.add(new Post(title, url, content));

                System.out.println("Getting Post from Database: - \n" + title + "\n" + url + "\n" + content + "\n");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        String updateIsSentQuery = "UPDATE posts SET is_sent = true";
        Sqlite.onUpdate(updateIsSentQuery);

        return posts;
    }

    public static void setPostValuesInDatabase(String title, String url, String content, boolean is_sent) {

        Sqlite.onUpdate("INSERT OR IGNORE INTO posts(title, url, content, is_sent) VALUES('" +
                title +"', '" +
                url +"', '" +
                content + "', '" +
                is_sent + "')");  /// Ignores if values are not unique
        System.out.println("Post added in Database: - \n" + title + "\n" + url + "\n" + content + "\n");
    }

    public static void deleteOldValuesFromDatabase() {
        Sqlite.onUpdate("DELETE FROM posts WHERE id IN (SELECT id FROM posts ORDER BY id ASC LIMIT " +
                OLDEST_VALUE +")");
        System.out.println("\nDeleted Old Posts From Database!");
    }
}
