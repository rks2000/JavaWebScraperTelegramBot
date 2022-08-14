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

    // after this much time the program will check for new postings
    static final Long SCRAPE_PERIOD = 60000L; // 1 min

    // after this much time the program will reduce the number of rows in posts table.
    static final Long CLEAN_PERIOD = 21600 * 1000L; // 6 hours


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

                ArrayList<Post> posts = JsoupScraper.scrape();
                for (Post post: posts) {
                    SqlManager.setPostValuesInDatabase(post.getTitle(), post.getUrl(), post.getContent(), false);
                }

                posts = SqlManager.getValuesFromDatabase();

                for (Post post: posts) {
                    bot.sendToUser(post.toMessageString());
                }
            }
        };

        TimerTask databaseCleanerTask = new TimerTask() {
            public void run() {
                System.out.println("Cleaning performed on: " + new Date());
                SqlManager.deleteOldValuesFromDatabase();
            }
        };

        timer.scheduleAtFixedRate(scraperTask, 0, SCRAPE_PERIOD);
        timer.scheduleAtFixedRate(databaseCleanerTask, CLEAN_PERIOD, CLEAN_PERIOD);
    }
}