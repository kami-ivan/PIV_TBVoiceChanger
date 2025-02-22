package rf.ivanpavlov.voicechanger.bot;

import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.nio.file.Path;

public class FileSender {
    private final TBVoiceChanger bot;
    private FileManager fileManager = new FileManager();


    public FileSender(TBVoiceChanger bot) {
        this.bot = bot;
    }

    public void sendFile(Message message, File file) {
        try {
            SendDocument sendDocument = new SendDocument();

            sendDocument.setChatId(message.getChatId());

            Path localPath = fileManager.getSendFilePath(message.getFrom(), file);

            InputFile inputFile = new InputFile(localPath.toFile(), localPath.getFileName().toString());

            sendDocument.setDocument(inputFile);

            bot.sendTextMessage(message.getChatId(), "Ваш файл отправляется.");

            bot.execute(sendDocument);

        } catch (TelegramApiException e) {
            bot.handleError(message.getChatId(), "Ошибка при отправки файла.\n" +
                    "Пожалуйста, запросите файл еще раз.", e);
        }

    }


}
