package in.rohan.webscraper.config;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

public class ConfigManager {

    public static String getKeys(String key) {

        Properties properties = new Properties();

        try {
            InputStream inputStream = new FileInputStream("config.yml");
            properties.load(inputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return properties.getProperty(key); // Returning BOT_TOKEN & BOT_NAME property keys
    }
}
