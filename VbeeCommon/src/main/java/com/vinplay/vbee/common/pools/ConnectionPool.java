/*
 * Decompiled with CFR 0.144.
 * 
 * Could not load the following classes:
 *  snaq.db.ConnectionPoolManager
 */
package com.vinplay.vbee.common.pools;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import com.vinplay.vbee.common.config.VBeePath;
import snaq.db.ConnectionPoolManager;
 // todo : lớp connection pool của mysql
public class ConnectionPool {
    public static final String USER_POOL = "mysqlpoolname";
    public static final String MINIGAME_POOL = "mysqlpool_minigame";
    public static final String ADMIN_POOL = "mysqlpool_admin";
    public static final String GAMEBAI_POOL = "mysqlpool_gamebai";
    public static final String XC_GIFTCODE_POOL = "mysql_xc_giftcode";
    private static String CONFIG_FILE = "config/db_pool.properties";
    private static final int DEFAULT_TIMEOUT = 5000;
    private ConnectionPoolManager managerVinPlay = null;
    private static ConnectionPool _instance;

    public static ConnectionPool getInstance() {
        if (null == _instance) {
            _instance = new ConnectionPool();
        }
        return _instance;
    }

    public static void start(String configFile) {
        CONFIG_FILE = configFile;
    }

    private ConnectionPool() {
        try {
            this.init(VBeePath.basePath.concat(CONFIG_FILE));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    // todo : khởi tạo my sql pool connection từ file config
    public void init(String configFile) throws IOException {
        Properties prop = new Properties();
        FileInputStream input = new FileInputStream(configFile);
        prop.load(input);
        if (input != null) {
            ((InputStream)input).close();
        }
        ConnectionPoolManager.createInstance((Properties)prop);
        this.managerVinPlay = ConnectionPoolManager.getInstance();
    }

    public Connection getConnection(String poolName) {
        Connection conn = null;
        try {
            conn = this.managerVinPlay.getConnection(poolName, 5000L);
        }
        catch (SQLException e) {
            e.printStackTrace();
        }        
        return conn;
    }

    public static void releaseConnection(Connection conn) {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

