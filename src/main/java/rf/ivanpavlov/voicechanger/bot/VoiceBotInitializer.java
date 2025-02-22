package rf.ivanpavlov.voicechanger.bot;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Configuration
public class VoiceBotInitializer {

    public VoiceBotInitializer(@Value("${telegram.bot.token}") String botToken,
                               @Value("${telegram.bot.username}") String botUsername) throws TelegramApiException {
        TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
        botsApi.registerBot(new TBVoiceChanger(botUsername, botToken));
    }
}
