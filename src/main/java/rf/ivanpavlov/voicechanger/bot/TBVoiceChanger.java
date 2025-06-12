package rf.ivanpavlov.voicechanger.bot;

import org.apache.tika.Tika;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TBVoiceChanger extends TelegramLongPollingBot {
    final String botUsername;
    private static final List<String> ALLOWED_TYPES = List.of("audio/mpeg", "audio/mp3", "audio/x-wav", "audio/vnd.wav", "audio/wave", "audio/vnd.wave", "audio/ogg", "application/ogg");
    private static final List<String> TYPES_WAV = List.of("audio/x-wav", "audio/vnd.wav", "audio/vnd.wave", "audio/wave");

    FileSender fileSender = new FileSender();
    FileAPI fileAPI = new FileAPI();
    AudioConverter converter = new AudioConverter();
    Menu menu = new Menu();

    private boolean isChoosingPitch = false;
    private boolean isChoosingModel = false;
    private Map<String, String> settings = new HashMap<>();
    private List<String> listAllModels = getSubfolderNames("C:\\\\Users\\\\HONOR\\\\Desktop\\\\RVC-GUI-pkg\\\\models");


    TBVoiceChanger(String botUsername, String botToken) {
        super(botToken);
        this.botUsername = botUsername;
    }

    @Override
    public void onUpdateReceived(Update update) {

        if (update.hasCallbackQuery()) {
            handeleCallback(update.getCallbackQuery());
        } else if (update.hasMessage()) {
            Message message = update.getMessage();
            long chatId = update.getMessage().getChatId();
            // если сообщение это текст
            if (message.hasText()) {
                handleMessage(chatId, message.getText());
                // если сообщение это файл
            } else if ((message.hasDocument() || message.hasAudio() || message.hasVoice())) {

                File file = getFileFromTG(message);
                File result = null;
                try {
                    if (file == null) {
                        throw new Exception("Нам не удалось скачать ваш файл.");
                    }
                    if (!isTypeValid(file)) {
                        throw new Exception("Неправильный формат файла.");
                    }
                    if (settings.get("model") == null || settings.get("pitch") == null || settings.get("algorithm") == null) {
                        throw new Exception("Пожалуйста, укажите все параметры в меню. /menu");
                    }
                    result = fileAPI.convert(file, settings);
                    sendFile(chatId, result);

                } catch (Exception e) {
                    handleError(chatId, "Во время обработки файла произошла ошибка.", e);
                } finally {
                    file.delete();
                    result.delete();
                    sendMainMenu(chatId);
                }
            }
        }
    }

    private void handleMessage(long chatId, String text) {
        if (isChoosingPitch) {
            if (text.matches("-?\\d+")) {
                settings.replace("pitch", text);
            }
            isChoosingPitch = false;
            sendMainMenu(chatId);
        } else if (isChoosingModel) {
            if (Integer.parseInt(text) <= listAllModels.size() && Integer.parseInt(text) > 0) {
                settings.replace("model", listAllModels.get(Integer.parseInt(text) - 1));
                isChoosingModel = false;
                sendMainMenu(chatId);
            } else {
                handleError(chatId, "Ошибка", new Exception("вы ввели неккоректное имя модели"));
            }
        } else {
            switch (text) {
                case "/start":
                    createDefaultSettings();
                    sendMainMenu(chatId);
                    break;
                case "/menu":
                    sendMainMenu(chatId);
                    break;
                case "/menu_type":
                    sendMenuTypeFile(chatId, settings.get("type"));
                    break;
            /*case "/uploadzip":
                try {
                    uploadZipFile(chatId);
                } catch (Exception e) {
                    handleError(chatId, "Ошибка при добавлении моделей.", e);
                }
                break;*/
                default:
                    sendTextMessage(chatId, "Простите, я не знаю такой команды. " +
                            "Может вы хотите перейти в главное меню? /menu");
            }
        }
    }

    private void handeleCallback(CallbackQuery callbackQuery) {
        String callbackData = callbackQuery.getData();
        int messageId = callbackQuery.getMessage().getMessageId();
        long chatId = callbackQuery.getMessage().getChatId();

        EditMessageText editMessage = new EditMessageText();
        editMessage.setChatId(chatId);
        editMessage.setMessageId(messageId);
        try {
            switch (callbackData) {
                case "main_menu":
                    sendEditMainMenu(editMessage);
                    break;
                case "menu_models":
                    sendEditMenuModels(editMessage, settings.get("model"));
                    break;
                case "model_man":
                    settings.put("model", "man");
                    sendEditMenuModels(editMessage, "man");
                    break;
                case "model_woman":
                    settings.put("model", "woman");
                    sendEditMenuModels(editMessage, "woman");
                    break;
                case "model_all":
                    isChoosingModel = true;
                    sendEditMenuAllModels(editMessage);
                    break;
                case "menu_pitch":
                    if (!settings.get("pitch").equals("12") &&
                            !settings.get("pitch").equals("0") &&
                            !settings.get("pitch").equals("-12")) {
                        sendEditMenuPitch(editMessage, "personal");
                    } else {
                        sendEditMenuPitch(editMessage, settings.get("pitch"));
                    }
                    break;
                case "pitch+12":
                    settings.put("pitch", "12");
                    sendEditMenuPitch(editMessage, "12");
                    break;
                case "pitch0":
                    settings.put("pitch", "0");
                    sendEditMenuPitch(editMessage, "0");
                    break;
                case "pitch-12":
                    settings.put("pitch", "-12");
                    sendEditMenuPitch(editMessage, "-12");
                    break;
                case "pitch_personal":
                    isChoosingPitch = true;
                    sendEditMenuPitch(editMessage, "personal");
                    sendTextMessage(chatId, "Введите свое значение от -12 до 12.");
                    break;
                case "menu_algorithm":
                    sendEditMenuAlgorithm(editMessage, settings.get("algorithm"));
                    break;
                case "algorithm_low":
                    settings.put("algorithm", "low");
                    sendEditMenuAlgorithm(editMessage, "low");
                    break;
                case "algorithm_medium":
                    settings.put("algorithm", "medium");
                    sendEditMenuAlgorithm(editMessage, "medium");
                    break;
                case "algorithm_high":
                    settings.put("algorithm", "high");
                    sendEditMenuAlgorithm(editMessage, "high");
                    break;
                case "algorithm_super":
                    settings.put("algorithm", "super");
                    sendEditMenuAlgorithm(editMessage, "super");
                    break;
                case "output_wav":
                    settings.put("type", "wav");
                    sendEditMenuTypeFile(editMessage, "wav");
                    break;
                case "output_mp3":
                    settings.put("type", "mp3");
                    sendEditMenuTypeFile(editMessage, "mp3");
                    break;
                case "output_oga":
                    settings.put("type", "oga");
                    sendEditMenuTypeFile(editMessage, "oga");
                    break;
            }
        } catch (Exception e) {
            handleError(chatId, "Ошибка \n", e);
        }
    }

    private File getFileFromTG(Message message) {
        File file = null;
        if (message.hasDocument()) {
            try {
                file = downloadFile(execute(new GetFile(message.getDocument().getFileId())));
            } catch (TelegramApiException e) {
                handleError(message.getChatId(), "Ошибка скачивания файла.", e);
            }
        } else if (message.hasAudio()) {
            try {
                file = downloadFile(execute(new GetFile(message.getAudio().getFileId())));
            } catch (TelegramApiException e) {
                handleError(message.getChatId(), "Ошибка скачивания файла.", e);
            }
        } else if (message.hasVoice()) {
            try {
                file = downloadFile(execute(new GetFile(message.getVoice().getFileId())));
            } catch (TelegramApiException e) {
                handleError(message.getChatId(), "Ошибка скачивания файла.", e);
            }
        }
        return file;
    }

    private void sendMainMenu(long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(menu.getTextMainMenu(settings));
        message.setReplyMarkup(menu.getMarkupMainMenu());

        try {
            execute(message);
        } catch (TelegramApiException e) {
            handleError(chatId, "Ошибка при отправлении меню. \n", e);
        }
    }

    private void sendEditMainMenu(EditMessageText editMessage) throws Exception {
        editMessage.setText(menu.getTextMainMenu(settings));
        editMessage.setReplyMarkup(menu.getMarkupMainMenu());

        try {
            execute(editMessage);
        } catch (TelegramApiException ignored) {
        }
    }

    private void sendEditMenuModels(EditMessageText editMessage, String value) throws Exception {
        editMessage.setText(menu.getTextMenuModels());
        editMessage.setReplyMarkup(menu.getMarkupMenuModels(value));
        try {
            execute(editMessage);
        } catch (TelegramApiException ignored) {
        }
    }

    private void sendEditMenuAllModels(EditMessageText editMessage) throws Exception {
        editMessage.setText(menu.getTextMenuAllModels(listAllModels));
        editMessage.setReplyMarkup(menu.getMarkupMenuAllModels());
        try {
            execute(editMessage);
        } catch (TelegramApiException ignored) {
        }
    }

    private void sendEditMenuPitch(EditMessageText editMessage, String value) throws Exception {
        editMessage.setText(menu.getTextMenuPitch());
        editMessage.setReplyMarkup(menu.getMarkupMenuPitch(value));
        try {
            execute(editMessage);
        } catch (TelegramApiException ignored) {
        }
    }

    private void sendEditMenuAlgorithm(EditMessageText editMessage, String value) throws Exception {
        editMessage.setText(menu.getTextMenuAlgorithm());
        editMessage.setReplyMarkup(menu.getMarkupMenuAlgorithm(value));
        try {
            execute(editMessage);
        } catch (TelegramApiException ignored) {
        }
    }

    private void sendMenuTypeFile(long chatId, String value) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(menu.getTextMenuTypeFile());
        message.setReplyMarkup(menu.getMarkupMenuTypeFile(value));

        try {
            execute(message);
        } catch (TelegramApiException e) {
            handleError(chatId, "Ошибка при отправлении меню. \n", e);
        }
    }

    private void sendEditMenuTypeFile(EditMessageText editMessage, String value) throws Exception {
        editMessage.setText(menu.getTextMenuTypeFile());
        editMessage.setReplyMarkup(menu.getMarkupMenuTypeFile(value));
        try {
            execute(editMessage);
        } catch (TelegramApiException ignored) {
        }
    }

    public List<String> getSubfolderNames(String folderPath) {
        try {
            return Files.list(Paths.get(folderPath)) // Получаем поток путей
                    .filter(Files::isDirectory) // Фильтруем только директории
                    .map(Path::getFileName) // Получаем имена папок
                    .map(Path::toString) // Преобразуем в строку
                    .collect(Collectors.toList()); // Собираем в список
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private void createDefaultSettings() {
        settings.put("pitch", "0");
        settings.put("algorithm", "low");
        settings.put("model", "man");
        settings.put("type", "wav");
    }

    private void uploadZipFile(long chatId) throws Exception {
        File dir = new File("models_zip");
        File[] list = dir.listFiles();
        if (list.length == 0) {
            throw new Exception("Новых моделей пока нет");
        } else {
            for (File file : list) {
                String res = fileAPI.add_zip(file);
                sendTextMessage(chatId, res);
                file.delete();

            }
        }
    }

    private boolean isTypeValid(File file) throws Exception {
        Tika tika = new Tika();
        try {
            String mimeType = tika.detect(file);
            return ALLOWED_TYPES.contains(mimeType);
        } catch (IOException e) {
            throw new Exception("Ошибка считывания формата файла. \n" + e);
        }
    }

    private String getTypeFile(File file) throws Exception {
        Tika tika = new Tika();
        try {
            return tika.detect(file);
        } catch (IOException e) {
            throw new Exception("Ошибка считывания формата файла. \n" + e.getMessage());
        }
    }

    private void sendVoice(long chatId, File file) throws Exception {
        try {
            execute(fileSender.getSendVoice(chatId, file));
        } catch (TelegramApiException e) {
            throw new Exception("Ошибка отправки файла");
        }
    }

    private void sendFile(long chatId, File file) throws Exception {
        try {
            execute(fileSender.getSendDocument(chatId, file));
        } catch (TelegramApiException e) {
            throw new Exception("Ошибка отправки файла");
        }
    }

    public void sendTextMessage(long chatId, String text) {
        try {
            execute(fileSender.getSendText(chatId, text)); // Отправляем сообщение
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void handleError(long chatId, String message, Exception e) {
        sendTextMessage(chatId, message + "\nКод ошибки:\n" + e.getMessage());
        e.printStackTrace();
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }
}