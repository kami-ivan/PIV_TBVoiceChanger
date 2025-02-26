package rf.ivanpavlov.voicechanger.bot;


import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class AudioConverter {
    public static File convertWawToOga(File inputWav) throws UnsupportedAudioFileException, IOException, LineUnavailableException {
        // Чтение WAV-файла
        AudioInputStream wavStream = AudioSystem.getAudioInputStream(inputWav);
        AudioFormat sourceFormat = wavStream.getFormat();
        // Параметры для Ogg Vorbis
        AudioFormat vorbisFormat = new AudioFormat(new AudioFormat.Encoding("VORBIS"), // Используем Vorbis
                sourceFormat.getSampleRate(), -1, // Размер сэмпла
                sourceFormat.getChannels(), -1, -1, false);
        // Конвертация
        AudioInputStream vorbisStream = AudioSystem.getAudioInputStream(vorbisFormat, wavStream);
        inputWav.delete();
        // Сохранение в OGA-файл
        AudioSystem.write(vorbisStream, new AudioFileFormat.Type("OGG", "oga"), new File("temp.oga"));

        wavStream.close();
        vorbisStream.close();
        return new File("temp.oga");
    }

}
