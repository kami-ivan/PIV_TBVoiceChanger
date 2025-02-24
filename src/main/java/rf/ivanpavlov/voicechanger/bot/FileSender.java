package rf.ivanpavlov.voicechanger.bot;

import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.io.File;
import java.nio.file.Path;

public class FileSender {

    private final FileManager fileManager = new FileManager();


    public SendDocument sendDocument(Message message, File file) {

        SendDocument sendDoc = new SendDocument();

        sendDoc.setChatId(message.getChatId());

        Path localPath = fileManager.getSendFilePath(message.getFrom(), file);

        InputFile inputFile = new InputFile(localPath.toFile(), localPath.getFileName().toString());

        sendDoc.setDocument(inputFile);

        return sendDoc;


    }


}
