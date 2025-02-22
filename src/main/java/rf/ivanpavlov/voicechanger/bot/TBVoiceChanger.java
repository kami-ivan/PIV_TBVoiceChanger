package rf.ivanpavlov.voicechanger.bot;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendVoice;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.nio.file.Path;

public class TBVoiceChanger extends TelegramLongPollingBot {
    final String botUsername;
    FileDownloader fileDownloader = new FileDownloader(this);
    FileManager fileManager = new FileManager();


    TBVoiceChanger(String botUsername, String botToken) {
        super(botToken);
        this.botUsername = botUsername;
    }

    @Override
    public void onUpdateReceived(Update update) {
        // Проверяем, есть ли сообщение и текст
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            // Обработка команды /start
            if (messageText.equals("/start")) {
                sendMessage(chatId, "Привет! Я простой бот. Напиши что-нибудь. \n" + "P.S. попробуй команду /voice");

            } else if (messageText.equals("/voice")) {
                File voiceFile = new File("file_to_send/kami_ini/тестовая-запись-моего-голоса.ogg");
                sendVoiceMessage(chatId, voiceFile);

            } else {
                // Повторяем сообщение пользователя
                sendMessage(chatId, "Вы сказали: " + messageText);
            }
        }
        if (update.hasMessage() && (update.getMessage().hasDocument() || update.getMessage().hasAudio())) {
            try {
                fileDownloader.handleDownloadFile(update.getMessage());
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
        }
    }


    public void sendMessage(long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(text);

        try {
            execute(message); // Отправляем сообщение
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void sendVoiceMessage(long chatId, java.io.File voiceFile) {
        SendVoice voiceMessage = new SendVoice();
        voiceMessage.setChatId(chatId);

        // указываем путь к файлу
        InputFile inputFile = new InputFile(voiceFile);
        voiceMessage.setVoice(inputFile);

        // отправляем голосовое сообщение
        try {
            execute(voiceMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void handleError(long chatId, String message, Exception e) {
        sendMessage(chatId, message + ": " + e.getMessage());
        e.printStackTrace();
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

}