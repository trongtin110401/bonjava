/*
 * Decompiled with CFR 0.144.
 * 
 * Could not load the following classes:
 *  com.hazelcast.core.HazelcastInstance
 *  com.hazelcast.core.IMap
 *  com.vinplay.vbee.common.exceptions.KeyNotFoundException
 *  com.vinplay.vbee.common.hazelcast.HazelcastClientFactory
 *  org.json.JSONArray
 *  org.json.JSONException
 *  org.json.JSONObject
 */
package com.vinplay.usercore.utils;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.vinplay.brandname.dao.impl.BrandnameDaoImpl;
import com.vinplay.usercore.dao.impl.GameConfigDaoImpl;
import com.vinplay.usercore.dao.impl.LuckyDaoImpl;
import com.vinplay.usercore.entities.IAPModel;
import com.vinplay.usercore.utils.LuckyUtils;
import com.vinplay.vbee.common.exceptions.KeyNotFoundException;
import com.vinplay.vbee.common.hazelcast.HazelcastClientFactory;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
 // todo : nhe cache rat ngu
public class GameCommon {
    public static final String SUCCESS = "1";
    public static final String ERROR = "-1";
    public static final String SERVICE_ID = "8041";
    public static final String VIN = "VIN";
    public static final String VIN_OTP = "OZZ OTP";
    public static final String VIN_APP = "OZZ APP";
    public static final String VIN_ODP = "OZZ ODP";
    public static final String OTP_CMD = "OZZ";
    public static final String CMD_OTP = "OZZ OTP";
    public static final String CMD_APP = "OZZ APP";
    public static final String CMD_ODP = "OZZ ODP";
    public static final String MESSAGE_TYPE = "1";
    public static final String TOTAL_MESSAGE = "1";
    public static final String MESSAGE_INDEX = "1";
    public static final String IS_MORE = "0";
    public static final String CONTENT_TYPE = "0";
    public static final int OTP_SUCCESS = 0;
    public static final int OTP_INVALID = 3;
    public static final int OTP_TIMEOUT = 4;
    public static final int OPEN = 0;
    public static final int CLOSE = 1;
    public static final int ON = 1;
    public static final int OFF = 0;
    public static String OTP_URL_SEND_MT = "";
    public static String OTP_IP_FILTER = "";
    public static String OTP_URL_RECEIVE_MO = "";
    public static int OTP_DELAY_SEND_MT = 5000;
    public static String MESSAGE_OTP_SUCCESS = "";
    public static String MESSAGE_ODP_SUCCESS = "";
    public static String MESSAGE_APP_SUCCESS = "";
    public static String MESSAGE_ERROR_MOBILE = "";
    public static String MESSAGE_ERROR_SYNTAX = "";
    public static String SMSPLUS_SUCCESS = "";
    public static String SMSPLUS_ERROR_NICKNAME = "";
    public static String SMSPLUS_ERROR_SYNTAX = "";
    public static String SMSPLUS_ERROR_SYSTEM = "";
    public static String SMSPLUS_ERROR_LOGIN = "";
    public static String SMSPLUS_ERROR_AMOUNT = "";
    public static String BRANDNAME_SENDER = "";
    public static String BRANDNAME_USER = "";
    public static String BRANDNAME_PASS = "";
    public static String BRANDNAME_URL = "";
    public static int BRANDNAME_CLIENT_ID = 4;
    public static String BRANDNAME_CLIENT_USER = "";
    public static String BRANDNAME_CLIENT_PASS = "";
    public static String BRANDNAME_URL_REPORT_FROM_ST = "";
    public static Map<Integer, IAPModel> iapPackages = new HashMap<Integer, IAPModel>();

    public static void init() throws SQLException, JSONException, ParseException {
        HazelcastInstance instance = HazelcastClientFactory.getInstance();
        IMap map = instance.getMap("cacheConfig");
        BrandnameDaoImpl bndao = new BrandnameDaoImpl();
        long brandNameID = bndao.getLastRequestId();
        map.put("BRAND_NAME_ID", (Object)String.valueOf(brandNameID));
        LuckyDaoImpl lkdao = new LuckyDaoImpl();
        long lkVipID = lkdao.getLuckyVipLastReferenceId();
        map.put("LUCKY_VIP_ID", (Object)String.valueOf(lkVipID));
        GameConfigDaoImpl dao = new GameConfigDaoImpl();
        Map<String, String> mapConfig = dao.getGameConfig();
        JSONObject cfObj = new JSONObject(mapConfig.get("web"));
        map.put("STATUS_GAME", (Object)String.valueOf(cfObj.getInt("status_game")));
        map.put("ADMIN", (Object)dao.getGameCommon("admin"));
        String commons = dao.getGameCommon("game_common");
        map.put("COMMONS", (Object)commons);

        String comclient = dao.getGameCommon("gameclient");
        map.put("COMCLIENT", (Object)comclient);

        String nhiemvuClient = dao.getGameCommon("nhiemvu");
        map.put("NHIEMVU", (Object)nhiemvuClient);

        String tintucClient = dao.getGameCommon("tintuc");
        map.put("TINTUC", (Object)tintucClient);

        String okclient = dao.getGameCommon("bankok88");
        map.put("OK88B", (Object)okclient);

        String okclient99 = dao.getGameCommon("bankok99");
        map.put("OK99B", (Object)okclient99);

        JSONObject commonObj = new JSONObject(commons);
        String hotline = commonObj.getString("hotline");
        String email = commonObj.getString("email");
        String facebook = commonObj.getString("facebook");
        String web = commonObj.getString("web");

        map.put("HOT_LINE", (Object)hotline);
        map.put("EMAIL", (Object)email);
        map.put("FACEBOOK", (Object)facebook);
        map.put("WEB", (Object)web);
        map.put("SMS_OTP", (Object)commonObj.getString("sms_otp"));
        map.put("BANNER", (Object)commonObj.getJSONArray("banner").toString());
        map.put("BANNER_TOUR", (Object)commonObj.getJSONArray("banner_tour").toString());
        map.put("PASSWORD_DEFAULT", (Object)commonObj.getString("password_default"));
        map.put("IAP_KEY", (Object)commonObj.getString("iap_key"));
        map.put("UPDATE_BOT_VIN", (Object)String.valueOf(commonObj.getInt("bot_vin")));
        map.put("UPDATE_BOT_XU", (Object)String.valueOf(commonObj.getInt("bot_xu")));
        map.put("UPDATE_USER_VIN", (Object)String.valueOf(commonObj.getInt("user_vin")));
        map.put("UPDATE_USER_XU", (Object)String.valueOf(commonObj.getInt("user_xu")));
        map.put("VIN_PLUS", (Object)dao.getGameCommon("vin_plus"));


        String billing = dao.getGameCommon("billing");
        map.put("BILLING", (Object)billing);

        String billingCodepay = dao.getGameCommon("billingcodepay");
        map.put("BILLINGCP", (Object)billingCodepay);

        JSONObject blObj = new JSONObject(billing);
        map.put("IS_NAP_MEGA_CARD", (Object)blObj.getString("is_nap_mega_card"));
        map.put("RATIO_NAP_MEGA_CARD", (Object)blObj.getString("ratio_nap_mega_card"));
        map.put("IS_RECHARGE_CARD", (Object)String.valueOf(blObj.getInt("is_nap_the")));
        map.put("IS_RECHARGE_VIN_CARD", (Object)String.valueOf(blObj.getInt("is_nap_vin_card")));
        map.put("IS_RECHARGE_BANK", (Object)String.valueOf(blObj.getInt("is_nap_vin_nh")));
        map.put("IS_RECHARGE_IAP", (Object)String.valueOf(blObj.getInt("is_nap_vin_iap")));
        map.put("IS_NAP_XU", (Object)String.valueOf(blObj.getInt("is_nap_xu")));
        map.put("IS_TRANSFER_MONEY", (Object)String.valueOf(blObj.getInt("is_chuyen_vin")));
        map.put("IS_CASHOUT_CARD", (Object)String.valueOf(blObj.getInt("is_mua_the")));
        map.put("IS_CASHOUT_TOPUP", (Object)String.valueOf(blObj.getInt("is_nap_dt")));
        map.put("IS_CASHOUT_BANK", (Object)String.valueOf(blObj.getInt("is_cashout_bank")));
        map.put("IS_CASHOUT_MOMO", (Object)String.valueOf(blObj.getInt("is_cashout_momo")));
        map.put("RATIO_NAP_XU", (Object)String.valueOf(blObj.getDouble("ratio_xu")));
        map.put("RATIO_RECHARGE_CARD", (Object)String.valueOf(blObj.getDouble("ratio_nap_the")));
        map.put("RATIO_RECHARGE_VIN_CARD", (Object)String.valueOf(blObj.getDouble("ratio_nap_vin_card")));
        map.put("RATIO_RECHARGE_BANK", (Object)String.valueOf(blObj.getDouble("ratio_nap_vin_nh")));
      // ti mo thang nay ra luc commit code clxx de ti de tim :
        map.put("RATIO_RECHARGE_BANK_TL", (Object)String.valueOf(blObj.getDouble("ratio_nap_vin_nh_tl")));
        map.put("RATIO_RECHARGE_MOMO", (Object)String.valueOf(blObj.getDouble("ratio_nap_vin_momo")));
        map.put("RATIO_RECHARGE_SMS", (Object)String.valueOf(blObj.getDouble("ratio_nap_sms")));
        map.put("RATIO_CASHOUT_CARD", (Object)String.valueOf(blObj.getDouble("ratio_mua_the")));
        map.put("RATIO_CASHOUT_TOPUP", (Object)String.valueOf(blObj.getDouble("ratio_nap_dt")));
        map.put("RATIO_TRANSFER", (Object)String.valueOf(blObj.getDouble("ratio_chuyen")));
        map.put("RATIO_CASHOUT_BANK", (Object)String.valueOf(blObj.getDouble("ratio_cashout_bank")));
        map.put("RATIO_CASHOUT_MOMO", (Object)String.valueOf(blObj.getDouble("ratio_cashout_momo")));
        map.put("TRANSFER_MONEY_MIN", (Object)String.valueOf(blObj.getInt("chuyen_vin_min")));
        map.put("CASHOUT_BANK_MIN", (Object)String.valueOf(blObj.getLong("cashout_bank_min")));
        map.put("CASHOUT_MOMO_MIN", (Object)String.valueOf(blObj.getLong("cashout_momo_min")));
        map.put("CASHOUT_LIMIT_USER", (Object)String.valueOf(blObj.getLong("cashout_limit_user")));
        map.put("CASHOUT_LIMIT_SYSTEM", (Object)String.valueOf(blObj.getLong("cashout_limit_system")));
        map.put("NUM_RECHARGE_FAIL", (Object)String.valueOf(blObj.getInt("num_recharge_fail")));
        map.put("NUM_CASHOUT_CARD", (Object)String.valueOf(blObj.getInt("num_doi_the")));
        map.put("CASHOUT_TIME_BLOCK", (Object)String.valueOf(blObj.getInt("cashout_time_block")));
        map.put("SUPER_ADMIN", (Object)blObj.getString("super_admin"));
        map.put("SUPER_AGENT", (Object)blObj.getString("super_agent"));
        map.put("CASHOUT_BANK_MAX", (Object)String.valueOf(blObj.getInt("cashout_bank_max")));
        map.put("CASHOUT_MOMO_MAX", (Object)String.valueOf(blObj.getInt("cashout_momo_max")));
        map.put("RATIO_REFUND_FEE_1", (Object)String.valueOf(blObj.getDouble("ratio_refund_fee_1")));
        map.put("RATIO_REFUND_FEE_2", (Object)String.valueOf(blObj.getDouble("ratio_refund_fee_2")));
        map.put("RATIO_REFUND_FEE_2_MORE", (Object)String.valueOf(blObj.getDouble("ratio_refund_fee_2_more")));
        map.put("REFUND_FEE_2_MORE", (Object)String.valueOf(blObj.getLong("refund_fee_2_more")));
        map.put("RATIO_TRANSFER_DL_1", (Object)String.valueOf(blObj.getDouble("ratio_transfer_dl_1")));
        map.put("DL1_TO_SUPER_MIN", (Object)String.valueOf(blObj.getLong("dl1_to_super_min")));
        map.put("DL1_TO_SUPER_MAX", (Object)String.valueOf(blObj.getLong("dl1_to_super_max")));
        map.put("DL1_TO_SUPER_MIN_X", (Object)String.valueOf(blObj.getLong("dl1_to_super_min_x")));
        map.put("IAP_MAX", (Object)String.valueOf(blObj.getInt("iap_max")));
        map.put("SYSTEM_IAP_MAX", (Object)String.valueOf(blObj.getInt("system_iap_max")));
        map.put("RATIO_TRANSFER_01", (Object)String.valueOf(blObj.getDouble("r_tf_01")));
        map.put("RATIO_TRANSFER_02", (Object)String.valueOf(blObj.getDouble("r_tf_02")));
        map.put("RATIO_TRANSFER_20", (Object)String.valueOf(blObj.getDouble("r_tf_20")));
        map.put("RATIO_TRANSFER_21", (Object)String.valueOf(blObj.getDouble("r_tf_21")));
        map.put("RATIO_TRANSFER_22", (Object)String.valueOf(blObj.getDouble("r_tf_22")));
        map.put("RATIO_TRANSFER_11", (Object)String.valueOf(blObj.getDouble("r_tf_11")));
        map.put("RATIO_TRANSFER_12", (Object)String.valueOf(blObj.getDouble("r_tf_12")));
        map.put("SMS_PLUS_OPEN", (Object)String.valueOf(blObj.getInt("is_sms_plus")));
        map.put("SMS_OPEN", (Object)String.valueOf(blObj.getInt("is_sms")));
        map.put("API_OTP_OPEN", (Object)String.valueOf(blObj.getInt("is_api_otp")));
        JSONArray jArrayVP = blObj.getJSONArray("iap_package");
        if (jArrayVP != null) {
            for (int i = 0; i < jArrayVP.length(); ++i) {
                JSONObject jObj = jArrayVP.getJSONObject(i);
                Iterator keys = jObj.keys();
                while (keys.hasNext()) {
                    String key = (String)keys.next();
                    IAPModel model = new IAPModel(i + 1, key, jObj.getInt(key));
                    iapPackages.put(i + 1, model);
                }
            }
        }
        JSONObject otpObj = new JSONObject(dao.getGameCommon("otp"));
        map.put("OTP_DEFAULT", (Object)otpObj.getString("otp_default"));

        map.put("tlmn_nguoi",dao.getGameCommon("game_tlmn") );
        map.put("game_notify",dao.getGameCommon("game_notify") );
        OTP_URL_SEND_MT = otpObj.getString("otp_url_send_mt");
        OTP_IP_FILTER = otpObj.getString("otp_ip_filter");
        OTP_URL_RECEIVE_MO = otpObj.getString("otp_url_receive_mo");
        OTP_DELAY_SEND_MT = otpObj.getInt("otp_delay_send_mt");
        MESSAGE_OTP_SUCCESS = otpObj.getString("message_otp_success");
        MESSAGE_ODP_SUCCESS = otpObj.getString("message_odp_success");
        MESSAGE_APP_SUCCESS = otpObj.getString("message_app_success");
        MESSAGE_ERROR_MOBILE = otpObj.getString("message_error_mobile");
        MESSAGE_ERROR_SYNTAX = otpObj.getString("message_error_syntax");
        JSONObject bnObj = new JSONObject(dao.getGameCommon("brandname"));
        map.put("BRANDNAME_OPEN", (Object)String.valueOf(bnObj.getInt("is_open")));
        BRANDNAME_SENDER = bnObj.getString("brandname_sender");
        BRANDNAME_USER = bnObj.getString("brandname_user");
        BRANDNAME_PASS = bnObj.getString("brandname_pass");
        BRANDNAME_URL = bnObj.getString("brandname_url");
        BRANDNAME_CLIENT_ID = bnObj.getInt("brandname_client_id");
        BRANDNAME_CLIENT_USER = bnObj.getString("brandname_client_user");
        BRANDNAME_CLIENT_PASS = bnObj.getString("brandname_client_pass");
        BRANDNAME_URL_REPORT_FROM_ST = bnObj.getString("brandname_url_report_from_st");
        JSONObject dvtObj = new JSONObject(dao.getGameCommon("dvt"));
        map.put("DVT_URL", (Object)dvtObj.getString("dvt_url"));
        map.put("DVT_PRIVATE_KEY", (Object)dvtObj.getString("dvt_private_key"));
        map.put("DVT_SECRET_KEY", (Object)dvtObj.getString("dvt_secret_key"));
        map.put("DVT_DATE_RE_CHECK", (Object)String.valueOf(dvtObj.getInt("dvt_date_re_check")));
        map.put("DVT_SMS_OPEN", (Object)String.valueOf(dvtObj.getInt("sms_open")));
        JSONObject otherObj = new JSONObject(dao.getGameCommon("other"));
        map.put("URL_ACTIVE_EMAIL", (Object)otherObj.getString("url_active_email"));
        String sign = otherObj.getString("sign");
        String signEmail = " H\u00c3\u00a3y \u00c4\u2018\u00e1\u00bb\u00abng ng\u00e1\u00ba\u00a7n ng\u00e1\u00ba\u00a1i li\u00c3\u00aan h\u00e1\u00bb\u2021 ngay v\u00e1\u00bb\u203ai ch\u00c3\u00bang t\u00c3\u00b4i khi b\u00e1\u00ba\u00a1n g\u00e1\u00ba\u00b7p s\u00e1\u00bb\u00b1 c\u00e1\u00bb\u2018.<br> Website: " + web + ".<br> Hotline: " + hotline + ".<br> Email: " + email + ".<br> Facebook: " + facebook + ".<br><br> Tr\u00c3\u00a2n tr\u00e1\u00bb\ufffdng! <br> " + sign + ".<br>";
        map.put("SIGN_EMAIL", (Object)signEmail);
        map.put("LIST_GAME_BAI", (Object)otherObj.getString("list_game_bai"));
        map.put("LIST_PHONE_ALERT", (Object)otherObj.getString("list_phone_alert"));
        map.put("HU_GAME_BAI_MAX", (Object)String.valueOf(otherObj.getLong("hu_game_bai_max")));
        map.put("SMS_FEE", (Object)String.valueOf(otherObj.getInt("sms_fee")));
        JSONObject gbObj = new JSONObject(dao.getGameCommon("game_bai"));
        map.put("HU_GAME_BAI", (Object)gbObj.toString());
        JSONObject npObj = new JSONObject(dao.getGameCommon("i2b"));
        map.put("NAPAS_VERSION", (Object)npObj.getString("version"));
        map.put("NAPAS_URL", (Object)npObj.getString("napas_url"));
        map.put("NAPAS_MERCHANT", (Object)npObj.getString("merchant_id"));
        map.put("NAPAS_ACCESS_CODE", (Object)npObj.getString("access_code"));
        map.put("NAPAS_SECRET_KEY", (Object)npObj.getString("secret_key"));
        map.put("NAPAS_USER", (Object)npObj.getString("user"));
        map.put("NAPAS_PASS", (Object)npObj.getString("password"));
        map.put("NAPAS_URL_RESULT", (Object)npObj.getString("url_result"));
        map.put("NAPAS_URL_CANCEL", (Object)npObj.getString("url_cancel"));
        map.put("NAPAS_AMOUNT_MIN", (Object)String.valueOf(npObj.getInt("amount_min")));
        JSONObject nlObj = new JSONObject(dao.getGameCommon("nganluong"));
        map.put("NL_OPEN", (Object)String.valueOf(nlObj.getInt("is_open")));
        map.put("NL_MERCHANT_ID", (Object)nlObj.getString("merchant_id"));
        map.put("NL_MERCHANT_PASSWORD", (Object)nlObj.getString("merchant_password"));
        map.put("NL_VERSION", (Object)nlObj.getString("version"));
        map.put("NL_RECEIVER_EMAIL", (Object)nlObj.getString("receiver_email"));
        map.put("NL_RETURN_URL", (Object)nlObj.getString("return_url"));
        map.put("NL_CANCEL_URL", (Object)nlObj.getString("cancel_url"));
        map.put("NL_TIME_LIMIT", (Object)String.valueOf(nlObj.getInt("time_limit")));
        map.put("NL_URL", (Object)nlObj.getString("nl_url"));
        map.put("NL_PAYMENT_METHOD", (Object)nlObj.getString("payment_method"));
        JSONObject vpeObj = new JSONObject(dao.getGameCommon("vippoint_event"));
        map.put("EVENT_TIME_START", (Object)vpeObj.getString("start"));
        map.put("EVENT_TIME_END", (Object)vpeObj.getString("end"));
        map.put("VIPPOINT_EVENT_URL", (Object)vpeObj.getString("url_help"));
        map.put("VIPPOINT_EVENT_RATE_SUB", (Object)String.valueOf(vpeObj.getInt("rate_sub")));
        map.put("VIPPOINT_EVENT_RATE_ADD", (Object)String.valueOf(vpeObj.getInt("rate_add")));
        map.put("VIPPOINT_EVENT_RATE_SUB_BOT", (Object)String.valueOf(vpeObj.getInt("rate_sub_bot")));
        map.put("VIPPOINT_EVENT_RATE_ADD_BOT", (Object)String.valueOf(vpeObj.getInt("rate_add_bot")));
        map.put("VIPPOINT_INDEX", (Object)String.valueOf(vpeObj.getInt("vippoint_index")));
        String luckyVip = dao.getGameCommon("lucky_vip");
        String lucky = dao.getGameCommon("lucky");
        JSONObject luckyObj = new JSONObject(lucky);
        LuckyUtils.init(luckyVip, lucky, luckyObj.getInt("num_type"));
        map.put("LUCKY_RECHARGE_INDEX", (Object)String.valueOf(luckyObj.getLong("recharge_index")));
        map.put("LUCKY_SLOT_MAX_WIN", (Object)String.valueOf(luckyObj.getInt("slot_max_win")));
        map.put("LUCKY_SLOT_ROOM", (Object)String.valueOf(luckyObj.getInt("slot_room")));
        map.put("LUCKY_MAX_IN_DAY", (Object)String.valueOf(luckyObj.getInt("max_in_day")));
        map.put("LUCKY_MAX_BY_IP", (Object)String.valueOf(luckyObj.getInt("max_by_ip")));
        JSONObject smsPlusObj = new JSONObject(dao.getGameCommon("sms_plus"));
        map.put("SMS_PLUS_AMOUNT_MIN", (Object)String.valueOf(smsPlusObj.getInt("amount_min")));
        map.put("SMS_PLUS_URL", (Object)smsPlusObj.getString("url"));
        map.put("SMS_PLUS_ACCESS_KEY", (Object)smsPlusObj.getString("access_key"));
        map.put("SMS_PLUS_SECRET_KEY", (Object)smsPlusObj.getString("secret_key"));
        map.put("SMS_PLUS_COMMAND_CODE", (Object)smsPlusObj.getString("command_code"));
        map.put("SMS_PLUS_GAME_CODE", (Object)smsPlusObj.getString("game_code"));
        map.put("SMS_COMMAND", (Object)smsPlusObj.getString("command"));
        map.put("API_OTP_URL_REQUEST", (Object)smsPlusObj.getString("url_otp_request"));
        map.put("API_OTP_URL_CONFIRM", (Object)smsPlusObj.getString("url_otp_confirm"));
        map.put("API_OTP_FORMAT", (Object)smsPlusObj.getString("otp_format"));
        map.put("API_OTP_TIMEOUT", (Object)String.valueOf(smsPlusObj.getInt("otp_timeout")));
        map.put("API_OTP_FAIL_DELAY", (Object)String.valueOf(smsPlusObj.getInt("otp_fail_delay")));
        map.put("API_OTP_FAIL_NUM_LOCK", (Object)String.valueOf(smsPlusObj.getInt("otp_fail_num_lock")));
        SMSPLUS_SUCCESS = smsPlusObj.getString("message_success");
        SMSPLUS_ERROR_NICKNAME = smsPlusObj.getString("message_error_nickname");
        SMSPLUS_ERROR_SYNTAX = smsPlusObj.getString("message_error_syntax");
        SMSPLUS_ERROR_SYSTEM = smsPlusObj.getString("message_error_system");
        SMSPLUS_ERROR_LOGIN = smsPlusObj.getString("message_error_login");
        SMSPLUS_ERROR_AMOUNT = smsPlusObj.getString("message_error_amount");
        JSONObject vcObj = new JSONObject(dao.getGameCommon("vin_card"));
        map.put("VIN_CARD_URL", (Object)vcObj.getString("vc_url"));
        map.put("VIN_CARD_PARTNER", (Object)vcObj.getString("vc_partner"));
        map.put("VIN_CARD_USER_LIMIT", (Object)String.valueOf(vcObj.getLong("vc_user_limit")));
        map.put("VIN_CARD_SYSTEM_LIMIT", (Object)String.valueOf(vcObj.getLong("vc_system_limit")));
        JSONObject maxpayObj = new JSONObject(dao.getGameCommon("maxpay"));
        map.put("MAXPAY_URL", (Object)maxpayObj.getString("maxpay_url"));
        map.put("MAXPAY_MERCHANT_ID", (Object)maxpayObj.getString("merchant_id"));
        map.put("MAXPAY_SECRET_KEY", (Object)maxpayObj.getString("secret_key"));
        JSONObject lucky79Obj = new JSONObject(dao.getGameCommon("lucky79"));
        map.put("LUCKY79_URL", (Object)lucky79Obj.getString("lucky79_url"));
        map.put("LUCKY79_MERCHANT_ID", (Object)lucky79Obj.getString("merchant_id"));
        map.put("LUCKY79_SECRET_KEY", (Object)lucky79Obj.getString("secret_key"));
        JSONObject priorityObj1 = new JSONObject(dao.getGameCommon("priority_partner"));
        map.put("RECHARGE_PARTNER", (Object)priorityObj1.getString("recharge"));
        map.put("TOPUP_PARTNER", (Object)priorityObj1.getString("topup"));
        JSONObject cashout = new JSONObject(priorityObj1.getString("cashout"));
        JSONObject vtt = new JSONObject(cashout.getString("vtt"));
        map.put("CASHOUT_VTT_PRIMARY", (Object)vtt.getString("primary"));
        map.put("CASHOUT_VTT_BACKUP", (Object)vtt.getString("backup"));
        JSONObject vms = new JSONObject(cashout.getString("vms"));
        map.put("CASHOUT_VMS_PRIMARY", (Object)vms.getString("primary"));
        map.put("CASHOUT_VMS_BACKUP", (Object)vms.getString("backup"));
        JSONObject vnp = new JSONObject(cashout.getString("vnp"));
        map.put("CASHOUT_VNP_PRIMARY", (Object)vnp.getString("primary"));
        map.put("CASHOUT_VNP_BACKUP", (Object)vnp.getString("backup"));
        JSONObject vnm = new JSONObject(cashout.getString("vnm"));
        map.put("CASHOUT_VNM_PRIMARY", (Object)vnm.getString("primary"));
        map.put("CASHOUT_VNM_BACKUP", (Object)vnm.getString("backup"));
        JSONObject gate = new JSONObject(cashout.getString("gate"));
        map.put("CASHOUT_GATE_PRIMARY", (Object)gate.getString("primary"));
        map.put("CASHOUT_GATE_BACKUP", (Object)gate.getString("backup"));
        JSONObject zing = new JSONObject(cashout.getString("zing"));
        map.put("CASHOUT_ZING_PRIMARY", (Object)zing.getString("primary"));
        map.put("CASHOUT_ZING_BACKUP", (Object)zing.getString("backup"));
        JSONObject vcoin = new JSONObject(cashout.getString("vcoin"));
        map.put("CASHOUT_VCOIN_PRIMARY", (Object)vcoin.getString("primary"));
        map.put("CASHOUT_VCOIN_BACKUP", (Object)vcoin.getString("backup"));
        JSONObject alertObj = new JSONObject(dao.getGameCommon("alert"));
        map.put("ALERT_URL", (Object)alertObj.getString("alert_url"));
        map.put("COUNT_FAIL", (Object)alertObj.getString("count_fail"));
        map.put("DISCONNECT_GROUP_NUMBER", (Object)alertObj.getString("disconnect_group_number"));
        map.put("PENDING_GROUP_NUMBER", (Object)alertObj.getString("pending_group_number"));
        map.put("FREEZE_MONEY_GROUP_NUMBER", (Object)alertObj.getString("freeze_money_group_number"));
        map.put("MEGA_CARD_GROUP_NUMBER", (Object)alertObj.getString("mega_card_group_number"));

        String telegramToken = alertObj.getString("Telegram_boot_token");
        String telegramChatId = alertObj.getString("Telegram_chat_id");
        String telegramGiftCodeChatId = alertObj.getString("Telegram_giftcode_id");
        String urlLeaderBoard = alertObj.getString("url_leaderboard");
        map.put("Telegram_boot_token", (Object)telegramToken);
        map.put("Telegram_chat_id", (Object)telegramChatId);
        map.put("Telegram_giftcode_id", telegramGiftCodeChatId);
        map.put("url_leaderboard",urlLeaderBoard);


        JSONObject _1PayObj = new JSONObject(dao.getGameCommon("1pay"));
        map.put("_1PAY_URL", (Object)_1PayObj.getString("1pay_url"));
        map.put("_1PAY_USER", (Object)_1PayObj.getString("1pay_user"));
        map.put("_1PAY_USER_API", (Object)_1PayObj.getString("1pay_user_api"));
        map.put("_1PAY_PASS", (Object)_1PayObj.getString("1pay_pass"));
        map.put("_1PAY_CODE_API", (Object)_1PayObj.getString("1pay_code_api"));
        map.put("_1PAY_PRIVATE_KEY", (Object)_1PayObj.getString("1pay_private_key"));
        JSONObject agentObj = new JSONObject(dao.getGameCommon("agent"));
        map.put("TIME_SEARCH", (Object)agentObj.getString("time_search"));
        JSONObject vtcObj = new JSONObject(dao.getGameCommon("vtc"));
        map.put("VTC_SERVICE_URL", (Object)vtcObj.getString("vtc_url"));
        map.put("VTC_PARTNER_CODE", (Object)vtcObj.getString("vtc_code"));
        map.put("VTC_PRIVATE_KEY", (Object)vtcObj.getString("vtc_private_key"));
        map.put("VTC_PARTNER_SECRET_KEY", (Object)vtcObj.getString("vtc_secret_key"));
        map.put("VTCPAY_PUBLIC_KEY", (Object)vtcObj.getString("vtc_pay_public_key"));
        map.put("VTCPAY_PRIVATE_KEY", (Object)vtcObj.getString("vtc_pay_private_key"));
        map.put("VTCPAY_PRICE", (Object)vtcObj.getString("vtc_pay_price"));
        JSONObject ePayObj = new JSONObject(dao.getGameCommon("epay"));
        map.put("CDV_WEBSERVICE_URL", (Object)ePayObj.getString("CDV_WEBSERVICE_URL"));
        map.put("CDV_PARTNER_NAME", (Object)ePayObj.getString("CDV_PARTNER_NAME"));
        map.put("CDV_PRIVATE_KEY", (Object)ePayObj.getString("CDV_PRIVATE_KEY"));
        map.put("CDV_PUBLIC_KEY", (Object)ePayObj.getString("CDV_PUBLIC_KEY"));
        map.put("CDV_KEY_SOFTPIN", (Object)ePayObj.getString("CDV_KEY_SOFTPIN"));
        JSONObject timeRecheckObj = new JSONObject(dao.getGameCommon("time_recheck"));
        map.put("TIME_RECHECK_RECHARGE", (Object)timeRecheckObj.getString("recharge"));
        map.put("TIME_RECHECK_CASHOUT_BY_CARD", (Object)timeRecheckObj.getString("cash_out_by_card"));
        JSONObject missionRuleObj = new JSONObject(dao.getGameCommon("mission_rule"));
        map.put("MAX_LEVEL_MISSION", (Object)missionRuleObj.getString("max_level"));
        map.put("MATCH_MAX_VIN", (Object)missionRuleObj.getString("match_max_vin"));
        map.put("MATCH_MAX_XU", (Object)missionRuleObj.getString("match_max_xu"));
        map.put("MIN_TAI_XIU_VIN", (Object)missionRuleObj.getString("min_tai_xiu_vin"));
        map.put("MIN_TAI_XIU_XU", (Object)missionRuleObj.getString("min_tai_xiu_xu"));
        JSONObject bonusVin = new JSONObject(missionRuleObj.getString("bonus_vin"));
        map.put("BONUS_VIN_0", (Object)bonusVin.getString("0"));
        map.put("BONUS_VIN_1", (Object)bonusVin.getString("1"));
        map.put("BONUS_VIN_2", (Object)bonusVin.getString("2"));
        map.put("BONUS_VIN_3", (Object)bonusVin.getString("3"));
        map.put("BONUS_VIN_4", (Object)bonusVin.getString("4"));
        map.put("BONUS_VIN_5", (Object)bonusVin.getString("5"));
        map.put("BONUS_VIN_6", (Object)bonusVin.getString("6"));
        map.put("BONUS_VIN_7", (Object)bonusVin.getString("7"));
        map.put("BONUS_VIN_8", (Object)bonusVin.getString("8"));
        map.put("BONUS_VIN_9", (Object)bonusVin.getString("9"));
        map.put("BONUS_VIN_10", (Object)bonusVin.getString("10"));
        map.put("BONUS_VIN_11", (Object)bonusVin.getString("11"));
        map.put("BONUS_VIN_12", (Object)bonusVin.getString("12"));
        map.put("BONUS_VIN_13", (Object)bonusVin.getString("13"));
        map.put("BONUS_VIN_14", (Object)bonusVin.getString("14"));
        map.put("BONUS_VIN_15", (Object)bonusVin.getString("15"));
        map.put("BONUS_VIN_16", (Object)bonusVin.getString("16"));
        map.put("BONUS_VIN_17", (Object)bonusVin.getString("17"));
        map.put("BONUS_VIN_18", (Object)bonusVin.getString("18"));
        map.put("BONUS_VIN_19", (Object)bonusVin.getString("19"));
        map.put("BONUS_VIN_20", (Object)bonusVin.getString("20"));
        JSONObject bonusXu = new JSONObject(missionRuleObj.getString("bonus_xu"));
        map.put("BONUS_XU_0", (Object)bonusXu.getString("0"));
        map.put("BONUS_XU_1", (Object)bonusXu.getString("1"));
        map.put("BONUS_XU_2", (Object)bonusXu.getString("2"));
        map.put("BONUS_XU_3", (Object)bonusXu.getString("3"));
        map.put("BONUS_XU_4", (Object)bonusXu.getString("4"));
        map.put("BONUS_XU_5", (Object)bonusXu.getString("5"));
        map.put("BONUS_XU_6", (Object)bonusXu.getString("6"));
        map.put("BONUS_XU_7", (Object)bonusXu.getString("7"));
        map.put("BONUS_XU_8", (Object)bonusXu.getString("8"));
        map.put("BONUS_XU_9", (Object)bonusXu.getString("9"));
        map.put("BONUS_XU_10", (Object)bonusXu.getString("10"));
        map.put("BONUS_XU_11", (Object)bonusXu.getString("11"));
        map.put("BONUS_XU_12", (Object)bonusXu.getString("12"));
        map.put("BONUS_XU_13", (Object)bonusXu.getString("13"));
        map.put("BONUS_XU_14", (Object)bonusXu.getString("14"));
        map.put("BONUS_XU_15", (Object)bonusXu.getString("15"));
        map.put("BONUS_XU_16", (Object)bonusXu.getString("16"));
        map.put("BONUS_XU_17", (Object)bonusXu.getString("17"));
        map.put("BONUS_XU_18", (Object)bonusXu.getString("18"));
        map.put("BONUS_XU_19", (Object)bonusXu.getString("19"));
        map.put("BONUS_XU_20", (Object)bonusXu.getString("20"));
        JSONObject megaObj = new JSONObject(dao.getGameCommon("epay_megacard"));
        map.put("MEGA_IS_VAT", (Object)megaObj.getString("is_vat"));
        map.put("MEGA_URL", (Object)megaObj.getString("mega_url"));
        map.put("MEGA_PARTNER_CODE", (Object)megaObj.getString("partner_code"));
        map.put("MEGA_PARTNER_ID", (Object)megaObj.getString("partner_id"));
        map.put("MEGA_MPIN", (Object)megaObj.getString("mpin"));
        map.put("MEGA_USER", (Object)megaObj.getString("user"));
        map.put("MEGA_PASS", (Object)megaObj.getString("pass"));
        map.put("MEGA_PUBLIC_KEY", (Object)megaObj.getString("public_key"));
        map.put("MEGA_PRIVATE_KEY", (Object)megaObj.getString("private_key"));
        map.put("MEGA_URL_VAT", (Object)megaObj.getString("mega_url_vat"));
        map.put("MEGA_PARTNER_CODE_VAT", (Object)megaObj.getString("partner_code_vat"));
        map.put("MEGA_PARTNER_ID_VAT", (Object)megaObj.getString("partner_id_vat"));
        map.put("MEGA_MPIN_VAT", (Object)megaObj.getString("mpin_vat"));
        map.put("MEGA_USER_VAT", (Object)megaObj.getString("user_vat"));
        map.put("MEGA_PASS_VAT", (Object)megaObj.getString("pass_vat"));
        map.put("MEGA_PUBLIC_KEY_VAT", (Object)megaObj.getString("public_key_vat"));
        map.put("MEGA_PRIVATE_KEY_VAT", (Object)megaObj.getString("private_key_vat"));
        JSONObject partnerIdObj = new JSONObject(dao.getGameCommon("partner_id"));
        map.put("VTCPAY_PARTNER_ID", (Object)partnerIdObj.getString("vtc_pay"));
        JSONObject priorityObj2 = new JSONObject(dao.getGameCommon("priority_partner"));
        JSONObject recharge = new JSONObject(priorityObj2.getString("recharge"));
        JSONObject vttRecharge = new JSONObject(recharge.getString("vtt"));
        map.put("RECHARGE_VTT_PRIMARY", (Object)vttRecharge.getString("primary"));
        map.put("RECHARGE_VTT_BACKUP", (Object)vttRecharge.getString("backup"));
        JSONObject vmsRecharge = new JSONObject(recharge.getString("vms"));
        map.put("RECHARGE_VMS_PRIMARY", (Object)vmsRecharge.getString("primary"));
        map.put("RECHARGE_VMS_BACKUP", (Object)vmsRecharge.getString("backup"));
        JSONObject vnpRecharge = new JSONObject(recharge.getString("vnp"));
        map.put("RECHARGE_VNP_PRIMARY", (Object)vnpRecharge.getString("primary"));
        map.put("RECHARGE_VNP_BACKUP", (Object)vnpRecharge.getString("backup"));
        JSONObject gateRecharge = new JSONObject(recharge.getString("gate"));
        map.put("RECHARGE_GATE_PRIMARY", (Object)gateRecharge.getString("primary"));
        map.put("RECHARGE_GATE_BACKUP", (Object)gateRecharge.getString("backup"));
        JSONObject mcObj = new JSONObject(dao.getGameCommon("merchant"));
        JSONArray jArray = mcObj.getJSONArray("mc_info");
        if (jArray != null) {
            for (int j = 0; j < jArray.length(); ++j) {
                JSONObject jObj2 = jArray.getJSONObject(j);
                Iterator keys2 = jObj2.keys();
                while (keys2.hasNext()) {
                    String key2 = (String)keys2.next();
                    JSONArray a = jObj2.getJSONArray(key2);
                    map.put((Object)(key2 + "CASHOUT_LIMIT_SYSTEM"), (Object)String.valueOf(a.getLong(6)));
                    map.put((Object)(key2 + "CASHOUT_LIMIT_USER"), (Object)String.valueOf(a.getLong(7)));
                }
            }
        }
        JSONObject VTCVcoinObj = new JSONObject(dao.getGameCommon("vtc_vcoin"));
        map.put("vcoin_url", (Object)VTCVcoinObj.getString("vcoin_url"));
        map.put("vcoin_partner_id", (Object)VTCVcoinObj.getString("vcoin_partner_id"));
        map.put("vcoin_partner_key", (Object)VTCVcoinObj.getString("vcoin_partner_key"));

        //thecao247

        JSONObject thecao247 = new JSONObject(dao.getGameCommon("lucky79"));
        map.put("THE_CAO_URL", (Object)thecao247.getString("lucky79_url"));
        map.put("LUCKY79_MERCHANT_ID", (Object)thecao247.getString("merchant_id"));
        map.put("LUCKY79_SECRET_KEY", (Object)thecao247.getString("secret_key"));
    }

    public static String getValueStr(String key) throws KeyNotFoundException {
        HazelcastInstance instance = HazelcastClientFactory.getInstance();
        IMap map = instance.getMap("cacheConfig");
        if (map.containsKey((Object)key)) {
            return (String)map.get((Object)key);
        }
        throw new KeyNotFoundException();
    }

    public static int getValueInt(String key) throws KeyNotFoundException, NumberFormatException {
        HazelcastInstance instance = HazelcastClientFactory.getInstance();
        IMap map = instance.getMap("cacheConfig");
        if (map.containsKey((Object)key)) {
            return Integer.parseInt((String)map.get((Object)key));
        }
        throw new KeyNotFoundException();
    }

    public static double getValueDouble(String key) throws KeyNotFoundException, NumberFormatException {
        HazelcastInstance instance = HazelcastClientFactory.getInstance();
        IMap map = instance.getMap("cacheConfig");
        if (map.containsKey((Object)key)) {
            return Double.parseDouble((String)map.get((Object)key));
        }
        throw new KeyNotFoundException();
    }

    public static long getValueLong(String key) throws KeyNotFoundException, NumberFormatException {
        HazelcastInstance instance = HazelcastClientFactory.getInstance();
        IMap map = instance.getMap("cacheConfig");
        if (map.containsKey((Object)key)) {
            return Long.parseLong((String)map.get((Object)key));
        }
        throw new KeyNotFoundException();
    }

    public static String getHuVangGameBai() throws KeyNotFoundException {
        HazelcastInstance instance = HazelcastClientFactory.getInstance();
        IMap map = instance.getMap("cacheConfig");
        if (map.containsKey((Object)"HU_GAME_BAI")) {
            return (String)map.get((Object)"HU_GAME_BAI");
        }
        throw new KeyNotFoundException();
    }

    public static List<String> getPhoneAlert() throws KeyNotFoundException {
        HazelcastInstance instance = HazelcastClientFactory.getInstance();
        IMap map = instance.getMap("cacheConfig");
        if (map.containsKey((Object)"LIST_PHONE_ALERT")) {
            String[] split;
            ArrayList<String> res = new ArrayList<String>();
            String[] arr = split = ((String)map.get((Object)"LIST_PHONE_ALERT")).split(",");
            for (String m : split) {
                if (m.isEmpty()) continue;
                res.add(m);
            }
            return res;
        }
        throw new KeyNotFoundException();
    }

    public static IAPModel getIAPPackageById(int id) {
        return iapPackages.get(id);
    }

    public static IAPModel getIAPPackageByName(String name) {
        IAPModel model = null;
        for (Map.Entry<Integer, IAPModel> entry : iapPackages.entrySet()) {
            if (!entry.getValue().getName().equals(name)) continue;
            model = entry.getValue();
            break;
        }
        return model;
    }
}

