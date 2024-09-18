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

import java.util.Arrays;

public class MongoDBConnectionFactory {

    private static String MONGODB_HOST = "10.40.112.5";
    private static String MONGODB_DATABASE = "win123club";
    private static String MONGODB_AUTH_DATABASE = "admin";
    private static String MONGODB_USERNAME = "admin";
    private static String MONGODB_PASSWORD = "MgqzAtRymcxyNoFnkwX7sGUlmj0YQI";
    private static int MONGODB_PORT = 27017;
    private static MongoClient mongoClient;


    public static void newConnection() {
        MongoClientOptions options = MongoClientOptions.builder()
                .connectionsPerHost(100)  // Default is 100
                .minConnectionsPerHost(30)  // Default is 20
                .maxConnectionIdleTime(60000) // Optional idle time in milliseconds
                .socketKeepAlive(true)
                .build();
        MongoCredential credential = MongoCredential.createCredential(MONGODB_USERNAME, MONGODB_AUTH_DATABASE, MONGODB_PASSWORD.toCharArray());
        mongoClient = new MongoClient(new ServerAddress(MONGODB_HOST, MONGODB_PORT), Arrays.asList(credential));
    }

    public static MongoDatabase getDB() {
        if (mongoClient == null) {
            MongoDBConnectionFactory.newConnection();
        }
        return mongoClient.getDatabase(MONGODB_DATABASE);
    }
}

