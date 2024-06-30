/*
 * Decompiled with CFR 0.144.
 *
 * Could not load the following classes:
 *  bitzero.util.common.business.Debug
 */
package game.util;

import bitzero.util.common.business.Debug;
import com.vinplay.usercore.dao.impl.GameConfigDaoImpl;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import scala.Int;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigGame {
    private static final String CONFIG_GAME_PATH = "config/config_game.properties";
    public static final String ENABLE_ALERT_MSG = "enable_alert_msg";
    public static final String UPDATE_JACKPOT_TIME = "update_jackpot_time";
    public static final String UPDATE_LOG_CCU = "update_log_ccu";
    public static final String MAX_PRIZE_FREE_DAILY = "max_prize_free_daily";
    public static final String KHO_BAU_SO_TIEN_NAP_TOI_THIEU_NO_HU_10K = "KhoBau_so_tien_nap_toi_thieu_no_hu_10k";
    public static final String KHO_BAU_INIT_POT_VALUES = "KhoBau_init_pot_values";
    public static final String KHO_BAU_TIME_X2 = "KhoBau_time_x2";
    public static final String KHO_BAU_DAYS_X2 = "KhoBau_days_x2";
    public static final String KHO_BAU_SO_LAN_NO_HU = "KhoBau_so_lan_no_hu";
    public static final String KHO_BAU_LAST_DAY_GIO_VANG = "KhoBau_last_day_gio_vang";
    public static final String KHO_BAU_BOT_100 = "KhoBau_bot_100";
    public static final String KHO_BAU_BOT_1000 = "KhoBau_bot_1000";
    public static final String KHO_BAU_BOT_10000 = "KhoBau_bot_10000";
    public static final String KHO_BAU_NUM_BOT_100 = "KhoBau_num_bot_100";
    public static final String KHO_BAU_NUM_BOT_1000 = "KhoBau_num_bot_1000";
    public static final String KHO_BAU_NUM_BOT_10000 = "KhoBau_num_bot_10000";
    public static final String NU_DIEP_VIEN_SO_TIEN_NAP_TOI_THIEU_NO_HU_10K = "NuDiepVien_so_tien_nap_toi_thieu_no_hu_10k";
    public static final String NU_DIEP_VIEN_INIT_POT_VALUES = "NuDiepVien_init_pot_values";
    public static final String NU_DIEP_VIEN_TIME_X2 = "NuDiepVien_time_x2";
    public static final String NU_DIEP_VIEN_DAYS_X2 = "NuDiepVien_days_x2";
    public static final String NU_DIEP_VIEN_SO_LAN_NO_HU = "NuDiepVien_so_lan_no_hu";
    public static final String NU_DIEP_VIEN_SO_LAN_NO_HU_100 = "NuDiepVien_so_lan_no_hu_100";
    public static final String NU_DIEP_VIEN_SO_LAN_NO_HU_1000 = "NuDiepVien_so_lan_no_hu_1000";
    public static final String NU_DIEP_VIEN_SO_LAN_NO_HU_10000 = "NuDiepVien_so_lan_no_hu_10000";
    public static final String NU_DIEP_VIEN_LAST_DAY_GIO_VANG = "NuDiepVien_last_day_gio_vang";
    public static final String NU_DIEP_VIEN_BOT_100 = "NuDiepVien_bot_100";
    public static final String NU_DIEP_VIEN_BOT_1000 = "NuDiepVien_bot_1000";
    public static final String NU_DIEP_VIEN_BOT_10000 = "NuDiepVien_bot_10000";
    public static final String NU_DIEP_VIEN_NUM_BOT_100 = "NuDiepVien_num_bot_100";
    public static final String NU_DIEP_VIEN_NUM_BOT_1000 = "NuDiepVien_num_bot_1000";
    public static final String NU_DIEP_VIEN_NUM_BOT_10000 = "NuDiepVien_num_bot_10000";
    // Audition
    public static final String AUDITION_SO_TIEN_NAP_TOI_THIEU_NO_HU_10K = "Audition_so_tien_nap_toi_thieu_no_hu_10k";
    public static final String AUDITION_INIT_POT_VALUES = "Audition_init_pot_values";
    public static final String AUDITION_TIME_X2 = "Audition_time_x2";
    public static final String AUDITION_DAYS_X2 = "Audition_days_x2";
    public static final String AUDITION_SO_LAN_NO_HU = "Audition_so_lan_no_hu";
    public static final String AUDITION_SO_LAN_NO_HU_100 = "Audition_so_lan_no_hu_100";
    public static final String AUDITION_SO_LAN_NO_HU_1000 = "Audition_so_lan_no_hu_1000";
    public static final String AUDITION_SO_LAN_NO_HU_5000 = "Audition_so_lan_no_hu_5000";
    public static final String AUDITION_SO_LAN_NO_HU_10000 = "Audition_so_lan_no_hu_10000";
    public static final String AUDITION_LAST_DAY_GIO_VANG = "Audition_last_day_gio_vang";
    public static final String AUDITION_BOT_100 = "Audition_bot_100";
    public static final String AUDITION_BOT_1000 = "Audition_bot_1000";
    public static final String AUDITION_BOT_5000 = "Audition_bot_5000";
    public static final String AUDITION_BOT_10000 = "Audition_bot_10000";
    public static final String AUDITION_NUM_BOT_100 = "Audition_num_bot_100";
    public static final String AUDITION_NUM_BOT_1000 = "Audition_num_bot_1000";
    public static final String AUDITION_NUM_BOT_5000 = "Audition_num_bot_5000";
    public static final String AUDITION_NUM_BOT_10000 = "Audition_num_bot_10000";
    // End Audition
    //private static Properties prop = new Properties();
    private static JSONObject json = new JSONObject();

    public static void reload() {
//        try {
//            FileInputStream input = new FileInputStream(CONFIG_GAME_PATH);
//            prop.load(input);
//        }
//        catch (FileNotFoundException e) {
//            Debug.trace((Object)"File config/config_game.properties not found");
//        }
//        catch (IOException e2) {
//            Debug.trace((Object)"Load config/config_game.properties error");
//        }
        try {
            GameConfigDaoImpl dao = new GameConfigDaoImpl();
            String partnerConfig = dao.getGameCommon("slot");
            json = (JSONObject) new JSONParser().parse(partnerConfig);
        } catch (Exception ex) {
            Debug.trace(ex.getMessage());
        }
    }

    public static String getValueString(String key) {
        return String.valueOf(json.get(key));
        //return prop.getProperty(key);
    }

    public static int getIntValue(String key) {
        return Integer.parseInt(String.valueOf(json.get(key)));
        //return Integer.parseInt(ConfigGame.getValueString(key));
    }

    public static int getIntValue(String key, int defaultValue) {
//        if (prop.containsKey(key)) {
//            return ConfigGame.getIntValue(key);
//        }
//        return defaultValue;
        if (json.get(key) != null) {
            return ConfigGame.getIntValue(key);
        }
        return defaultValue;
    }

    /**
     * Get float value from config_game.properties
     *
     * @param key          key
     * @param defaultValue default value
     * @return float value
     */
    public static float getFloatValue(String key, float defaultValue) {
        try {
            return Float.parseFloat(ConfigGame.getValueString(key));
        } catch (Exception ex) {
            return defaultValue;
        }
    }
}

