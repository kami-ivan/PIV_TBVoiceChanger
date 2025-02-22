package rf.ivanpavlov.voicechanger;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@SpringBootApplication
public class VoiceBotApplication {

	public static void main(String[] args) throws TelegramApiException {
		SpringApplication.run(VoiceBotApplication.class, args);
	}
}
