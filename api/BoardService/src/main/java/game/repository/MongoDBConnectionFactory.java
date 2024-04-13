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
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;

import java.util.Arrays;

public class MongoDBConnectionFactory {
    private static String MONGODB_HOST = "45.76.178.154";
    private static String MONGODB_DATABASE = "win123club";
    private static String MONGODB_AUTH_DATABASE = "admin";
    private static String MONGODB_USERNAME = "admin";
    private static String MONGODB_PASSWORD = "Linh742@ASdfH";
    private static int MONGODB_PORT = 27017;
    private static MongoClient mongoClient;


    public static void newConnection() {
        MongoCredential credential = MongoCredential.createCredential(MONGODB_USERNAME, MONGODB_AUTH_DATABASE, MONGODB_PASSWORD.toCharArray());
        mongoClient = new MongoClient(new ServerAddress(MONGODB_HOST, MONGODB_PORT), Arrays.asList(new MongoCredential[]{credential}));
    }

    public static MongoDatabase getDB() {
        if (mongoClient == null) {
            MongoDBConnectionFactory.newConnection();
        }
        return mongoClient.getDatabase(MONGODB_DATABASE);
    }
}

