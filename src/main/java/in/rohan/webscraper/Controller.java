package in.rohan.webscraper;

import in.rohan.webscraper.config.ConfigFile;
import in.rohan.webscraper.config.ConfigManager;
import in.rohan.webscraper.database.SqlManager;
import in.rohan.webscraper.database.Sqlite;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.*;

public class Controller {
    private static Set<Post> hashSetOfPosts = new HashSet<>();
    private static Boolean isSent = false;
    static final long DATABASE_ADD_PERIOD = 10000L; //7200 * 1000L; // 2 hours

    // after this many millisecond the program will check for new internships
    static final Long SCRAPE_PERIOD = 5000L; //600 * 1000L; // 10 minutes

    // after this many ms the program will check trigger a query that will reduce the number of rows in posts table.
    static final Long CLEAN_PERIOD = 10800 * 1000L; // 3 hours


    public static void main(String[] args) {

        ConfigFile.loadConfig();
        Sqlite.setConnection();
        SqlManager.createTable();

        TelegramNotificationBot telegramNotificationBot = new TelegramNotificationBot();
        System.out.println(ConfigManager.getKeys("BOT_NAME") + " is Starting...");

        try {
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            telegramBotsApi.registerBot(telegramNotificationBot);

        }catch (TelegramApiException e) {
            e.printStackTrace();
        }
        configureRepeatableTasks(telegramNotificationBot);
    }

    /**
     * Configures tasks that are executed periodically.
     * @param bot the bot for which the tasks should be configured
     */
    private static void configureRepeatableTasks(TelegramNotificationBot bot) {
        Timer timer = new Timer("Timer");
        TimerTask scraperTask = new TimerTask() {
            @Override
            public void run() {

                System.out.println("\n-------------------------\n\nScraping Performed on: " + new Date());

                Post posts = JsoupScraper.scrape();
                System.out.println("\n" + posts);


                if (hashSetOfPosts.isEmpty()) {  ///
                    System.out.println("\nHash Set is Empty ");
                    System.out.println("\nHash Set contains post = " + hashSetOfPosts.contains(posts));
                    bot.sendToUser(posts.toMessageString());
                    System.out.println("\npost sent to user");
                } else {
                    for (Post post : hashSetOfPosts) {
                        boolean postExists = hashSetOfPosts.contains(post);
                        System.out.println("\nHash Set contains post = " + postExists);
                        if (!postExists) {
                            bot.sendToUser(post.toMessageString());
                            System.out.println("\npost sent to user");
                        } else {
                            System.out.println("\nPost not sent to user. Post already exists in hashset.");
                        }
                    }
                }

                hashSetOfPosts.add(posts);
                System.out.println("\nposts added in Hash Set");
                System.out.println("\nSize of Hash Set = " + hashSetOfPosts.size());
            }
        };

        TimerTask databaseAddTask = new TimerTask() {
            public void run() {
                System.out.println("Database Add Task performed on: " + new Date());
                for (Post post: hashSetOfPosts) {
                    String title = post.getTitle();
                    String url = post.getUrl();
                    String content = post.getContent();
                    SqlManager.setPostValuesInDatabase(title, url, content, false);
                }
                hashSetOfPosts.clear();
                System.out.println("Hash Set Cleaning performed on: " + new Date());
            }
        };


        TimerTask databaseCleanerTask = new TimerTask() {
            public void run() {
                System.out.println("Cleaning performed on: " + new Date());
                SqlManager.deleteOldValuesFromDatabase();
            }
        };

        timer.scheduleAtFixedRate(scraperTask, 0, SCRAPE_PERIOD);
        timer.scheduleAtFixedRate(databaseAddTask, DATABASE_ADD_PERIOD, DATABASE_ADD_PERIOD);
        timer.scheduleAtFixedRate(databaseCleanerTask, CLEAN_PERIOD, CLEAN_PERIOD);
    }
}