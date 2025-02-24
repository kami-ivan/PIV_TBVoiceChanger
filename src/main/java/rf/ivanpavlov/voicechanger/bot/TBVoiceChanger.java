package rf.ivanpavlov.voicechanger.bot;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendVoice;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

public class TBVoiceChanger extends TelegramLongPollingBot {
    final String botUsername;

    private static final List<String> ALLOWED_EXTENSIONS = List.of(".mp3", ".wav", ".ogg", ".oga");


    FileDownloader fileDownloader = new FileDownloader();
    FileSender fileSender = new FileSender();
    FileAPI fileAPI = new FileAPI();
    JaveConverter javeConverter = new JaveConverter();
    FileManager fileManager = new FileManager();


    TBVoiceChanger(String botUsername, String botToken) {
        super(botToken);
        this.botUsername = botUsername;
    }

    @Override
    public void onUpdateReceived(Update update) {
        // Проверяем, есть ли сообщение
        if (update.hasMessage()) {

            long chatId = update.getMessage().getChatId();

            // если сообщение это текст
            if (update.getMessage().hasText()) {
                String messageText = update.getMessage().getText();

                switch (messageText) {
                    case "/start":
                        sendTextMessage(chatId, "Привет! Я простой бот. Напиши что-нибудь. \n" +
                                "/uploadzip\n");
                        break;
                    case "/uploadzip":
                        File dir = new File("models_zip");
                        File[] list = dir.listFiles();
                        if (list.length == 0) {
                            sendTextMessage(chatId, "Новых моделей пока нет");
                            break;
                        } else {
                            for (File file : list) {
                                String res = fileAPI.add_zip(file);
                                sendTextMessage(chatId, res);
                                file.delete();
                            }
                        }
                        break;
                    default:
                        sendTextMessage(chatId, "Вы сказали: " + messageText);
                        break;

                }
            }
            // если сообщение это файл
            else if (update.getMessage().hasDocument()) {

                Path path = fileManager.getDownloadPath(update.getMessage().getFrom(),
                        update.getMessage().getDocument().getFileName());

                try {
                    org.telegram.telegrambots.meta.api.objects.File fileFromTG =
                            execute(new GetFile(update.getMessage().getDocument().getFileId()));

                    File file = downloadFile(fileFromTG, path.toFile());

                    try {

                        fullConversionFile(update, file);

                    } catch (Exception e) {

                        handleError(chatId, "Во время обработки файла произошла ошибка.", e);
                    }

                } catch (TelegramApiException e) {
                    handleError(chatId, "Ошибка скачивания файла.", e);
                }


            } else if (update.getMessage().hasAudio()) {

                Path path = fileManager.getDownloadPath(update.getMessage().getFrom(),
                        update.getMessage().getAudio().getFileName());

                try {
                    org.telegram.telegrambots.meta.api.objects.File fileFromTG =
                            execute(new GetFile(update.getMessage().getAudio().getFileId()));

                    File file = downloadFile(fileFromTG, path.toFile());

                    try {

                        fullConversionFile(update, file);

                    } catch (Exception e) {

                        handleError(chatId, "Во время обработки файла произошла ошибка.", e);
                    }

                } catch (TelegramApiException e) {
                    handleError(chatId, "Ошибка скачивания файла.", e);
                }

            } else if (update.getMessage().hasVoice()) {

                Path path = fileManager.getDownloadPath(update.getMessage().getFrom(),
                        update.getMessage().getVoice().getFileUniqueId().substring(5, 10) + ".oga");

                try {
                    org.telegram.telegrambots.meta.api.objects.File fileFromTG =
                            execute(new GetFile(update.getMessage().getVoice().getFileId()));

                    File file = downloadFile(fileFromTG, path.toFile());

                    try {

                        fullConversionFile(update, file);

                    } catch (Exception e) {

                        handleError(chatId, "Во время обработки файла произошла ошибка.", e);
                    }

                } catch (TelegramApiException e) {
                    handleError(chatId, "Ошибка скачивания файла.", e);
                }
            }
        }
    }

    private void fullConversionFile(Update update, File file) throws Exception {

        sendTextMessage(update.getMessage().getChatId(), "Мы начали обрабатывать ваш файл!");

        if (!isExtFileValid(file)) {
            throw new Exception("Неправильный формат файла");
        }
        if (!getExtFile(file).equals("wav")) {
            file = javeConverter.convertToWav(file);
        }

        File fileResult = fileAPI.convert(file);

        file.delete();

        execute(fileSender.sendDocument(update.getMessage(), fileResult));

        fileResult.delete();

    }


    public String getExtFile(File file) {
        return file.getName().substring(file.getName().lastIndexOf(".") + 1);

    }

    public boolean isExtFileValid(File file) {
        String extension = file.getName().substring(file.getName().lastIndexOf("."));
        return ALLOWED_EXTENSIONS.contains(extension);
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