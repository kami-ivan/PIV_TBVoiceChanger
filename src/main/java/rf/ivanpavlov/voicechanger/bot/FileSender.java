package rf.ivanpavlov.voicechanger.bot;

import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendVoice;
import org.telegram.telegrambots.meta.api.objects.InputFile;

import java.io.File;

public class FileSender {

    public SendDocument getSendDocument(long chatId, File file) {
        SendDocument sendDoc = new SendDocument();
        sendDoc.setChatId(chatId);
        InputFile inputFile = new InputFile(file, file.getName());
        sendDoc.setDocument(inputFile);
        return sendDoc;
    }

    public SendVoice getSendVoice(long chatId, File voiceFile) {
        SendVoice voiceMessage = new SendVoice();
        voiceMessage.setChatId(chatId);        // указываем путь к файлу
        voiceMessage.setVoice(new InputFile(voiceFile));        // отправляем голосовое сообщение
        return voiceMessage;
    }

    public SendMessage getSendText(long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);
        return message;
    }

}
