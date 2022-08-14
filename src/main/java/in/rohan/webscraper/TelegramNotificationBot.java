package in.rohan.webscraper;

import in.rohan.webscraper.config.ConfigManager;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class TelegramNotificationBot extends TelegramLongPollingBot {

    String chatID = ConfigManager.getKeys("CHAT_ID");


    @Override
    public String getBotUsername() {
        return ConfigManager.getKeys("BOT_NAME");
    }

    @Override
    public String getBotToken() {
        return ConfigManager.getKeys("BOT_TOKEN");
    }

    @Override
    public void onUpdateReceived(Update update) {

        // Check if the update has a message and message has text
        if(update.hasMessage() && update.getMessage().hasText()) {

            String userMessage = update.getMessage().getText();
            User user = update.getMessage().getFrom();

            String reply = "_";

            if (userMessage.equals("/start")) {
                reply = "Welcome, " + user.getFirstName();

            }

            SendMessage message = new SendMessage();
            message.setChatId(update.getMessage().getChatId());
            message.setText(reply);

            try {
                execute(message);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendToUser(String messageString) {

        SendMessage message = new SendMessage();
        message.setChatId(chatID);
        message.setText(messageString);

        try {
            execute(message);
        }
        catch (TelegramApiException e) {
            if (e.toString().contains("[403]")) {              // user stopped the bot
                System.out.println("User Stopped the Bot");
            } else {
                System.err.println(">>>COULD NOT SEND POSTS TO " + chatID);
                e.printStackTrace();
            }
        }
    }
}
