package in.rohan.webscraper;

import in.rohan.webscraper.config.ConfigManager;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

public class TelegramNotificationBot extends TelegramLongPollingBot {

    String chatID = "845726056";


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
//            String chatID = update.getMessage().getChatId().toString();  // for storing in database

            String reply = "_";

            if (userMessage.equals("/start")) {
                reply = "Welcome, " + user.getFirstName();

                //Performing Async DB request  (sync causes  the reply to be  sent late)
//                CompletableFuture.supplyAsync(() -> DatabaseManager.addUser(chatID, user.getFirstName()));
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

//        ArrayList<String> chatIDs = DatabaseManager.getChatIDs();


        SendMessage message = new SendMessage();
        message.setChatId(chatID);
        message.setText(messageString);

        try {
            execute(message);
//            Thread.sleep(1000); // to avoid Telegram Bot block (due to flooding).
        }
        catch (TelegramApiException e) {
            if (e.toString().contains("[403]")) { // user stopped the bot
                System.out.println("User Stopped the Bot");
//                CompletableFuture.supplyAsync(() -> DatabaseManager.deleteUser(id));
            } else {
                System.err.println(">>>COULD NOT SEND POSTS TO " + chatID);
                e.printStackTrace();
            }
        }
    }


    /**
     * Sends the given string to all users.
     * @param messageString messageString
     */
//    public void sendToAllUsers(String messageString) {
//
//        ArrayList<String> chatIDs = DatabaseManager.getChatIDs();
//
//        for (String id : chatIDs) {
//            SendMessage message = new SendMessage();
//            message.setChatId(id);
//            message.setText(messageString);
//            message.setParseMode("html");
//
//            try {
//                execute(message);
//                Thread.sleep(1000); // to avoid Telegram Bot block (due to flooding).
//            }
//            catch (TelegramApiException | InterruptedException e) {
//                if (e.toString().contains("[403]")) { // user stopped the bot
////                    System.out.println("User Stopped the Bot");
//                    CompletableFuture.supplyAsync(() -> DatabaseManager.deleteUser(id));
//                }
//                else {
//                    System.err.println(">>>COULD NOT SEND POSTS TO " + id);
//                    e.printStackTrace();
//                }
//            }
//        }
//    }
}
