package in.rohan.webscraper;

import in.rohan.webscraper.config.ConfigManager;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class TelegramNotificationBot extends TelegramLongPollingBot {

    String chatID = ConfigManager.getKeys("CHAT_ID");  // bot send updates to this id
    String ownerChatId = ConfigManager.getKeys("OWNER_CHAT_ID"); // to restrict access to other users


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

        // Check if the update has a message & message has text, and It comes from the Owner
        // and restricts access to unauthorized users
        if(update.hasMessage() && update.getMessage().hasText()
                && update.getMessage().getChatId().toString().equals(ownerChatId)) {

            String userMessage = update.getMessage().getText();
            User user = update.getMessage().getFrom();

            String reply = "Please select Commands!";

            if (userMessage.equals("/start")) {
                reply = "Welcome, " + user.getFirstName() + "\n" +
                    "Now I will Start Sending Notification to the Channel.";

                Controller.configureRepeatableTasks(Controller.telegramNotificationBot);

            } else if (userMessage.equals("/restart")) {
                reply = "Restarting the Bot now...";

                Controller.main(null);
            }

            SendMessage message = new SendMessage();
            message.setChatId(update.getMessage().getChatId());
            message.setText(reply);

            try {
                execute(message);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        } else {
            // Sends warning message to unauthorized users
            SendMessage unauthorizedMessage = new SendMessage();
            unauthorizedMessage.setChatId(update.getMessage().getFrom().getId());
            unauthorizedMessage.setText("WARNING: Unauthorized access denied for " + update.getMessage().getFrom().getFirstName());

            try {
                execute(unauthorizedMessage);
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
