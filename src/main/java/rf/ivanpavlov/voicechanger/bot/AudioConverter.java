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
        // Сохранение в OGA-файл
        File outputFile = new File(inputWav.getPath().substring(0, inputWav.getPath().lastIndexOf(".")) + ".oga");
        AudioSystem.write(vorbisStream, new AudioFileFormat.Type("OGG", "oga"), outputFile);

        wavStream.close();
        vorbisStream.close();
        inputWav.delete();

        return outputFile;
    }


}
