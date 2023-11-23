/*
 * Decompiled with CFR 0.144.
 * 
 * Could not load the following classes:
 *  bitzero.util.common.business.CommonHandle
 *  org.json.JSONArray
 *  org.json.JSONException
 *  org.json.JSONObject
 */
package game.modules.cluster.util;

import bitzero.util.common.business.CommonHandle;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ClusterConfig {
    public JSONObject config = null;
    private static ClusterConfig ins = null;

    private ClusterConfig() {
        this.initconfig();
    }

    public static ClusterConfig instance() {
        if (ins == null) {
            ins = new ClusterConfig();
        }
        return ins;
    }

    private void initconfig() {
        String path = System.getProperty("user.dir");
        File file = new File(String.valueOf(path) + "/conf/cluster.json");
        StringBuffer contents = new StringBuffer();
        BufferedReader reader = null;
        try {
            InputStreamReader r = new InputStreamReader((InputStream)new FileInputStream(file), "UTF-8");
            reader = new BufferedReader(r);
            String text = null;
            while ((text = reader.readLine()) != null) {
                contents.append(text).append(System.getProperty("line.separator"));
            }
            this.config = new JSONObject(contents.toString());
        }
        catch (FileNotFoundException r) {
        }
        catch (IOException r) {
        }
        catch (JSONException r) {
            // empty catch block
        }
    }

    public int getNumberOfLines() {
        try {
            return this.config.getInt("numberOfLines");
        }
        catch (Exception e) {
            return 20;
        }
    }

    public int getSocketTimeout() {
        try {
            return this.config.getInt("socketTimeOut");
        }
        catch (Exception e) {
            return 5000;
        }
    }

    public int getBufferSizeLocalServer() {
        try {
            return this.config.getInt("bufferSizeLocalServer");
        }
        catch (Exception e) {
            return 5012;
        }
    }

    public JSONArray getSocketList() {
        try {
            return this.config.getJSONArray("serverList");
        }
        catch (Exception e) {
            CommonHandle.writeErrLog((Throwable)e);
            return null;
        }
    }

    public int getThisSocketPrefixId() {
        try {
            return this.config.getJSONObject("thisSocket").getInt("prefixId");
        }
        catch (JSONException e) {
            CommonHandle.writeErrLog((Throwable)e);
            return 0;
        }
    }

    public JSONObject getIndexServer() {
        try {
            return this.config.getJSONObject("index");
        }
        catch (Exception e) {
            CommonHandle.writeErrLog((Throwable)e);
            return null;
        }
    }

    public String getThisSocketIp() {
        try {
            return this.config.getJSONObject("thisSocket").getString("ip");
        }
        catch (Exception e) {
            CommonHandle.writeErrLog((Throwable)e);
            return "";
        }
    }

    public int getThisSocketPort() {
        try {
            return this.config.getJSONObject("thisSocket").getInt("port");
        }
        catch (Exception e) {
            CommonHandle.writeErrLog((Throwable)e);
            return 0;
        }
    }

    public String getThisSocketType() {
        try {
            return this.config.getJSONObject("thisSocket").getString("type");
        }
        catch (Exception e) {
            CommonHandle.writeErrLog((Throwable)e);
            return "";
        }
    }

    public boolean isManySockets() {
        try {
            return this.config.getBoolean("isManySockets");
        }
        catch (Exception e) {
            CommonHandle.writeErrLog((Throwable)e);
            return true;
        }
    }
}

