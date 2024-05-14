/*
 * Decompiled with CFR 0.144.
 */
package game.modules.minigame.cmd;

public class MiniGameCMD {
    public static final short CHAT = 18000;
    public static final short CHAT_SUBSCRIBE = 18001;
    public static final short CHAT_UNSUBSCRIBE = 18002;
    public static final short CHAT_LOG = 18003;
    public static final short ADMIN_SERVER_INFO = 19000;
    public static final short ADMIN_KICK_USER = 19001;
    public static final short LOBBY_DOI_PASS = 20000;
    public static final short LOBBY_DOI_VIPPOINT = 20001;
    public static final short LOBBY_UPDATE_USER_INFO = 20002;
    public static final short LOBBY_UPDATE_EMAIL = 20003;
    public static final short LOBBY_UPDATE_MOBILE = 20004;
    public static final short LOBBY_ACTIVE_EMAIL = 20005;
    public static final short LOBBY_ACTIVE_MOBILE = 20006;
    public static final short LOBBY_UPDATE_NEW_MOBILE = 20007;
    public static final short LOBBY_LOGIN_OTP = 20008;
    public static final short LOBBY_KET_SAT = 20009;
    public static final short LOBBY_GAME_CONFIG = 20010;
    public static final short LOBBY_NAP_XU = 20011;
    public static final short LOBBY_NAP_THE_DIEN_THOAI = 20012;
    public static final short LOBBY_GACH_THE_DIEN_THOAI = 20200;
    public static final short LOBBY_NAP_QUA_NGAN_HANG = 20013;
    public static final short LOBBY_CHUYEN_KHOAN = 20014;
    public static final short LOBBY_MUA_MA_THE = 20015;
    public static final short LOBBY_NAP_TIEN_DIEN_THOAI = 20016;
    public static final short LOBBY_GIFT_CODE = 20017;
    public static final short LOBBY_CHECK_USER = 20018;
    public static final short LOBBY_OTP = 20019;
    public static final short LOBBY_RESULT_DOI_PASS = 20020;
    public static final short LOBBY_RESULT_DOI_VIPPOINT = 20021;
    public static final short LOBBY_RESULT_ACTIVE_MOBILE = 20026;
    public static final short LOBBY_RESULT_UPDATE_NEW_MOBILE = 20027;
    public static final short LOBBY_RESULT_ACTIVE_NEW_MOBILE = 20028;
    public static final short LOBBY_RESULT_KET_SAT = 20029;
    public static final short LOBBY_RESULT_NAP_XU = 20031;
    public static final short LOBBY_RESULT_CHUYEN_KHOAN = 20034;
    public static final short LOBBY_RESULT_MUA_MA_THE = 20035;
    public static final short LOBBY_RESULT_NAP_TIEN_DIEN_THOAI = 20036;
    public static final short LOBBY_CHECK_IAP = 20037;
    public static final short LOBBY_RESULT_IAP = 20038;
    public static final short LOBBY_GET_EVENT_VP_INFO = 20039;
    public static final short LOBBY_API_OTP_REQUEST = 20040;
    public static final short LOBBY_API_OTP_CONFIRM = 20041;
    public static final short MIS_LIST_MISSION = 21000;
    public static final short MIS_REWARD_MISSION = 21001;
    public static final short MIS_NOTIFY_MISSION_COMPLETE = 21002;
    public static final short PLAY_VQMM = 20042;
    public static final short GET_VQ_VIP = 20043;
    public static final short PLAY_VQ_VIP = 20044;
    public static final short LOBBY_NAP_VIN_CARD = 20045;
    public static final short LOBBY_NAP_MEGA_CARD = 20046;
    public static final short LOBBY_GET_INFO = 20050;
    public static final short LOBBY_GET_MONEY_USE = 20051;
    public static final short LOBBY_EVENT_VP_UNLUCKY = 20052;
    public static final short LOBBY_HAS_NEW_MAIL = 20053;
    public static final short LOBBY_BROADCAST_MESSAGE = 20100;
    public static final short LOBBY_UPDATE_JACKPOT = 20101;
    public static final short LOBBY_SUBSCRIBE_JACKPOT = 20102;
    public static final short LOBBY_UNSUBSCRIBE_JACKPOT = 20103;
    public static final short LOBBY_UPDATE_HU_VANG = 20104;
    public static final short SUBCRIBE_MINI_GAME = 2000;
    public static final short UNSUBCRIBE_MINI_GAME = 2001;
    public static final short CHANGE_ROOM = 2002;
    public static final short UPDATE_USER_INFO = 2003;
    public static final short BET_TAI_XIU = 2110;
    public static final short TAI_XIU_INFO = 2111;
    public static final short UPDATE_TAI_XIU_PER_SECOND = 2112;
    public static final short UPDATE_RESULT_DICES = 2113;
    public static final short UPDATE_PRIZE_TAI_XIU = 2114;
    public static final short START_NEW_GAME_TAI_XIU = 2115;
    public static final short LICH_SU_PHIEN_TX = 2116;
    public static final short LICH_SU_GIAO_DICH_TX = 2117;
    public static final short TAN_LOC = 2118;
    public static final short RUT_LOC = 2119;
    public static final short UPDATE_FUND_TAN_LOC = 2120;
    public static final short START_NEW_ROUND_RUT_LOC = 2121;
    public static final short UPDATE_SO_LUOT_RUT_LOC = 2122;
    public static final short ENABLE_RUT_LOC = 2123;
    public static final short BROADCAST_TX_TIME = 2124;
    public static final short PLAY_MINI_POKER = 4001;
    public static final short UPDATE_POT_MINI_POKER = 4002;
    public static final short SUBSCRIBE_MINI_POKER = 4003;
    public static final short UNSUBSCRIBE_MINI_POKER = 4004;
    public static final short CHANGE_ROOM_MINI_POKER = 4005;
    public static final short AUTO_PLAY_MINI_POKER = 4006;
    public static final short STOP_PLAY_MINI_POKER = 4007;
    public static final short FORCE_STOP_PLAY_MINI_POKER = 4008;
    public static final short MINI_POKER_X2_TASK = 4009;
    public static final short SUBSCRIBE_BAU_CUA = 5001;
    public static final short UNSUBSCRIBE_BAU_CUA = 5002;
    public static final short CHANGE_ROOM_BAU_CUA = 5003;
    public static final short BET_BAU_CUA = 5004;
    public static final short BAU_CUA_INFO = 5005;
    public static final short UPDATE_BAU_CUA_PER_SECOND = 5006;
    public static final short START_NEW_GAME_BAU_CUA = 5007;
    public static final short UPDATE_BAU_CUA_RESULT = 5008;
    public static final short UPDATE_BAU_CUA_PRIZE = 5009;
    public static final short BAU_CUA_GET_LICH_SU_PHIEN = 5010;
    public static final short START_PLAY_CAO_THAP = 6001;
    public static final short PLAY_CAO_THAP = 6002;
    public static final short UPDATE_POT_CAO_THAP = 6003;
    public static final short SUBSCRIBE_CAO_THAP = 6004;
    public static final short UNSUBSCRIBE_CAO_THAP = 6005;
    public static final short CHANGE_ROOM_CAO_THAP = 6006;
    public static final short STOP_PLAY_CAO_THAP = 6007;
    public static final short UPDATE_TIME_CAO_THAP = 6008;
    public static final short USER_INFO_CAO_THAP = 6009;
    public static final short PLAY_POKEGO = 7001;
    public static final short UPDATE_POT_POKEGO = 7002;
    public static final short SUBSCRIBE_POKEGO = 7003;
    public static final short UNSUBSCRIBE_POKEGO = 7004;
    public static final short CHANGE_ROOM_POKEGO = 7005;
    public static final short AUTO_PLAY_POKEGO = 7006;
    public static final short STOP_PLAY_POKEGO = 7007;
    public static final short FORCE_STOP_PLAY_POKEGO = 7008;
    public static final short POKEGO_X2 = 7009;
    public static final short LOGIN_OTHER_DEVICE = 20111;

    //Diamond New
    public static final short CMD_SLOT_EXTEND_SUB= 8001;
    public static final short CMD_SLOT_EXTEND_UNSUB= 8002;
    public static final short CMD_SLOT_EXTEND_SPIN = 8003;
    public static final short CMD_SLOT_EXTEND_CHANGE_ROOM = 8004;
    public static final short CMD_SLOT_EXTEND_AUTOSPIN = 8005;
    public static final short CMD_SLOT_EXTEND_UPDATE_POT = 8006;
    public static final short CMD_SLOT_EXTEND_FORCE_STOP = 8007;

    //deposit - withdraw
    public static final short CMD_DEPOSIT_BANK_MANUAL= 20201;
    public static final short CMD_DEPOSIT_ONE_PAY_BANK_MANUAL= 20299;
    public static final short CMD_DEPOSIT_MOMO_MANUAL= 20202;
    public static final short CMD_WITHDRAW_CARD_MANUAL= 20211;
    public static final short CMD_WITHDRAW_BANK_MANUAL= 20219;
    public static final short CMD_WITHDRAW_MOMO_MANUAL= 20215;
    public static final short CMD_DEPOSIT_CODEPAY_MANUAL= 20218;
    public static final short CMD_DEPOSIT_CODEPAY_TIME = 20555;


    public static final short CMD_GET_PHONE_NUMBER= 20221;
    public static final short CMD_TRANS_FER_MONEY_TO_USER= 20222;

}

