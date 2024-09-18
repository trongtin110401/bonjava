/*
 * Decompiled with CFR 0.144.
 *
 * Could not load the following classes:
 *  com.mongodb.MongoClient
 *  com.mongodb.MongoCredential
 *  com.mongodb.ServerAddress
 *  com.mongodb.client.MongoDatabase
 */
package com.vinplay.vbee.common.mongodb;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;
import com.vinplay.vbee.common.config.VBeePath;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class MongoDBConnectionFactory {
    private static String MONGODB_HOST = "localhost";
    private static String MONGODB_DATABASE = "vinplay";
    private static String MONGODB_AUTH_DATABASE = "admin";
    private static String MONGODB_USERNAME = "vinplay";
    private static String MONGODB_PASSWORD = "vinplay@123";
    private static int MIN_CONNECTION = 20;
    private static int MAX_CONNECTION = 100;
    private static int MONGODB_PORT = 27017;
    private static MongoClient mongoClient;

    public static void init() throws IOException {
        Properties prop = new Properties();
        FileInputStream input = new FileInputStream(VBeePath.basePath.concat("config/mongo.properties"));
        prop.load(input);
        MONGODB_HOST = prop.getProperty("host");
        MONGODB_DATABASE = prop.getProperty("database");
        MONGODB_AUTH_DATABASE = prop.getProperty("auth_database");
        MONGODB_PORT = Integer.parseInt(prop.getProperty("port"));
        MONGODB_USERNAME = prop.getProperty("username");
        MONGODB_PASSWORD = prop.getProperty("password");
        MIN_CONNECTION = Integer.parseInt(prop.getProperty("min_connection", "10"));
        MAX_CONNECTION = Integer.parseInt(prop.getProperty("max_conneciton", "30"));
        MongoDBConnectionFactory.newConnection();
    }


    public static void newConnection() {
        MongoCredential credential = MongoCredential.createCredential((String) MONGODB_USERNAME, (String) MONGODB_AUTH_DATABASE, (char[]) MONGODB_PASSWORD.toCharArray());

        MongoClientOptions options = MongoClientOptions.builder()
                .connectionsPerHost(MAX_CONNECTION)  // Default is 100
                .minConnectionsPerHost(MIN_CONNECTION)  // Default is 20
                .maxConnectionIdleTime(60000) // Optional idle time in milliseconds
                .socketKeepAlive(true)
                .build();

        mongoClient = new MongoClient(new ServerAddress(MONGODB_HOST, MONGODB_PORT), Arrays.asList(new MongoCredential[]{credential}), options);
    }

    public static MongoDatabase getDB() {
        if (mongoClient == null) {
            MongoDBConnectionFactory.newConnection();
        }
        return mongoClient.getDatabase(MONGODB_DATABASE);
    }
}

