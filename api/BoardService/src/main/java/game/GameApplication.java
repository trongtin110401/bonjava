package game;

import game.repository.MongoDBConnectionFactory;
import game.tele.TeleAuthentication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@SpringBootApplication
@EnableScheduling
@EnableMongoRepositories
@Configuration
public class GameApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(GameApplication.class, args);
        ContextHolder.applicationContext = context;
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(new TeleAuthentication());

            context.getBean(MongoDBConnectionFactory.class).newConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}
