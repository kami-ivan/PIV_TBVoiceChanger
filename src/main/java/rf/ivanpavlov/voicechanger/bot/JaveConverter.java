package rf.ivanpavlov.voicechanger.bot;


import ws.schild.jave.Encoder;
import ws.schild.jave.EncoderException;
import ws.schild.jave.InputFormatException;
import ws.schild.jave.MultimediaObject;
import ws.schild.jave.encode.AudioAttributes;
import ws.schild.jave.encode.EncodingAttributes;

import java.io.File;


public class JaveConverter {

    public File convertToWav(File source) throws InputFormatException, EncoderException {
        AudioAttributes audio = new AudioAttributes();
        audio.setCodec("pcm_s16le"); // Формат WAV
        audio.setSamplingRate(44100); // Частота дискретизации
        audio.setChannels(2); // Стерео

        EncodingAttributes encoding = new EncodingAttributes();
        encoding.setOutputFormat("wav");
        encoding.setAudioAttributes(audio);

        File target = new File(source.getPath().substring(0, source.getPath().lastIndexOf(".")) +
                "_converted.wav");

        Encoder encoder = new Encoder();
        encoder.encode(new MultimediaObject(source), target, encoding);

        source.delete();

        return target;
    }

}
