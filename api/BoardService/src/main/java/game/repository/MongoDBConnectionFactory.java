/*
 * Decompiled with CFR 0.144.
 *
 * Could not load the following classes:
 *  com.mongodb.MongoClient
 *  com.mongodb.MongoCredential
 *  com.mongodb.ServerAddress
 *  com.mongodb.client.MongoDatabase
 */
package game.repository;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class MongoDBConnectionFactory {

    @Value("${spring.data.mongodb.host}")
    private String MONGODB_HOST;
    @Value("${spring.data.mongodb.database}")
    private static String MONGODB_DATABASE;
    @Value("${spring.data.mongodb.auth_database}")
    private static String MONGODB_AUTH_DATABASE;
    @Value("${spring.data.mongodb.username}")
    private static String MONGODB_USERNAME;
    @Value("${spring.data.mongodb.password}")
    private static String MONGODB_PASSWORD;
    @Value("${spring.data.mongodb.port}")
    private static int MONGODB_PORT;
    private static MongoClient mongoClient;


    public void newConnection() {
        MongoClientOptions options = MongoClientOptions.builder()
                .connectionsPerHost(30)  // Default is 100
                .minConnectionsPerHost(5)  // Default is 20
                .maxConnectionIdleTime(360000) // Optional idle time in milliseconds
                .socketKeepAlive(true)
                .build();
        MongoCredential credential = MongoCredential.createCredential(MONGODB_USERNAME, MONGODB_AUTH_DATABASE, MONGODB_PASSWORD.toCharArray());
        mongoClient = new MongoClient(new ServerAddress(MONGODB_HOST, MONGODB_PORT), Arrays.asList(credential), options);
    }

    public  MongoDatabase getDB() {
        if (mongoClient == null) {
            newConnection();
        }
        return mongoClient.getDatabase(MONGODB_DATABASE);
    }
}

