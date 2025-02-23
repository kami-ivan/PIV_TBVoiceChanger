package rf.ivanpavlov.voicechanger.bot;

import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.objects.File;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class FileDownloader {
    private final TBVoiceChanger bot;

    FileManager fileManager = new FileManager();

    private static final List<String> ALLOWED_EXTENSIONS = List.of(".mp3", ".wav", ".ogg", ".oga");


    public FileDownloader(TBVoiceChanger bot) {
        this.bot = bot;
    }

    public Path handleDownloadFile(Message message) throws TelegramApiException {
        try {
            File file = null;
            // если отправили аудио
            if (message.hasAudio()) {
                // получаем файл аудио
                file = bot.execute(new GetFile(message.getAudio().getFileId()));

                // иначе если отправили документ
            } else if (message.hasDocument()) {
                // получаем документ
                file = bot.execute(new GetFile(message.getDocument().getFileId()));

            } else if (message.hasVoice()) {

                file = bot.execute(new GetFile(message.getVoice().getFileId()));
            }
            // проверяем расширение документа
            if (!isExtFileValid(file, ALLOWED_EXTENSIONS)) {
                bot.sendTextMessage(message.getChatId(), "Неверный формат файла. " +
                        "Я поддерживаю только .wav, .mp3, .ogg и .oga форматы.");
            }

            // получаем путь к файлу на скачивание
            Path filePathDownload = fileManager.getDownloadPath(message.getFrom(), file);

            // создаем путь
            Files.createDirectories(filePathDownload.getParent());

            // скачиваем файл
            bot.downloadFile(file.getFilePath(), filePathDownload.toFile());

            // говорим об успешной загрузке файла
            bot.sendTextMessage(message.getChatId(), "Файл успешно загружен!");

            return filePathDownload;

        } catch (Exception e) {
            bot.handleError(message.getChatId(),
                    "Ошибка при загрузке файла. Попробуйте еще раз.", e);
            return null;
        }
    }


    private boolean isExtFileValid(File file, List<String> allowedExtensions) {
        String extension = file.getFilePath().substring(file.getFilePath().lastIndexOf(".")).toLowerCase();
        return allowedExtensions.contains(extension);
    }

}
