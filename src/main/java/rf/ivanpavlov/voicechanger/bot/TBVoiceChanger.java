package rf.ivanpavlov.voicechanger.bot;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendVoice;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;

public class TBVoiceChanger extends TelegramLongPollingBot {
    final String botUsername;

    FileDownloader fileDownloader = new FileDownloader(this);
    FileSender fileSender = new FileSender(this);


    TBVoiceChanger(String botUsername, String botToken) {
        super(botToken);
        this.botUsername = botUsername;
    }

    @Override
    public void onUpdateReceived(Update update) {
        // Проверяем, есть ли сообщение
        if (update.hasMessage()) {
            // если сообщение это текст
            if (update.getMessage().hasText()) {
                String messageText = update.getMessage().getText();
                long chatId = update.getMessage().getChatId();

                switch (messageText) {
                    case "/start":
                        sendTextMessage(chatId, "Привет! Я простой бот. Напиши что-нибудь. \n" +
                                "/voice\n" +
                                "/getdoc");
                        break;
                    case "/voice":
                        File voiceFile = new File("file_to_send/kami_ini_909045782/test_recording_my_voice.ogg");
                        sendVoiceMessage(chatId, voiceFile);
                        break;
                    case "/getdoc":
                        fileSender.sendFile(update.getMessage(),
                                new File("file_to_send/kami_ini_909045782/test_recording_my_voice.ogg"));
                        break;
                    default:
                        sendTextMessage(chatId, "Вы сказали: " + messageText);
                        break;

                }
            }
            // если сообщение это файл
            else if (update.getMessage().hasDocument() || update.getMessage().hasAudio()) {
                try {
                    fileDownloader.handleDownloadFile(update.getMessage());
                } catch (TelegramApiException e) {
                    handleError(update.getMessage().getChatId(), "Ошибка при загрузке файла. Попробуйте позднее.", e);
                }
            }
        }
    }


    public void sendTextMessage(long chatId, String text) {
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
        sendTextMessage(chatId, message + "\nКод ошибки: " + e.getMessage());
        e.printStackTrace();
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

}