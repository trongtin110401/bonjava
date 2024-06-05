package game;


import game.service.CacheService;
import game.service.impl.CacheServiceImpl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;


@SpringBootApplication
@EnableScheduling
public class GameApplication {

    public static void main(String[] args) {
        SpringApplication.run(GameApplication.class, args);
        try {
            init();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void init() throws IOException {
        Properties prop = new Properties();
        FileInputStream input = new FileInputStream("config/cluster.properties");
        prop.load(input);
        CacheService service = new CacheServiceImpl();
        service.setValue("url_leader_board", prop.getProperty("url_leader_board"));
    }


}
