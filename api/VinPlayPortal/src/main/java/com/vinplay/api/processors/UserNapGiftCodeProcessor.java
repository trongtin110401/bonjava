package com.vinplay.api.processors;

import bitzero.util.common.business.Debug;
import com.vinplay.api.dao.ManageGiftCodeDAO;
import com.vinplay.api.entities.CodeTT;
import com.vinplay.api.entities.UseCode;
import com.vinplay.api.entities.UserOTP;
import com.vinplay.api.processors.minigame.response.TopVinhDanhResponse;
import com.vinplay.usercore.dao.GiftCodeDAO;
import com.vinplay.usercore.dao.impl.GiftCodeDAOImpl;
import com.vinplay.usercore.service.GiftCodeService;
import com.vinplay.usercore.service.UserService;
import com.vinplay.usercore.service.impl.GiftCodeServiceImpl;
import com.vinplay.usercore.service.impl.UserServiceImpl;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.response.GiftCodeUpdateResponse;
import com.vinplay.vbee.common.response.MoneyResponse;
import com.vinplay.vbee.common.statics.TransType;
import com.vinplay.vbee.common.utils.VinPlayUtils;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;

import static game.modules.gameRoom.entities.GameMoneyInfo.userService;

public class UserNapGiftCodeProcessor
        implements BaseProcessor<HttpServletRequest, String> {

    private GiftCodeService gfService = new GiftCodeServiceImpl();
    private static final Logger logger = Logger.getLogger("api");

    public String execute(Param<HttpServletRequest> param) {
        HttpServletRequest request = param.get();
        String code = request.getParameter("code");
        String nickName = request.getParameter("nickName");

        try {
            // get special gift_code
            ManageGiftCodeDAO dao = new ManageGiftCodeDAO();
//            boolean exists = dao.CheckSpecialGiftCodes(code);
            boolean check_code_tt = false;
            CodeTT codex = dao.getCodeTT(code);
            if (codex != null) {
                check_code_tt = true;
            }
            UserOTP usotp = dao.getUserActiveOTP(nickName);
            boolean check_use_code_tanthu = dao.checkUseCode(nickName);
            boolean check_user_active_otp;
            if (usotp.getActive() == 1) {
                check_user_active_otp = true;
            } else {
                check_user_active_otp = false;
            }


            if (check_user_active_otp) {
                if (!check_use_code_tanthu && check_code_tt) {
                    String timelog = VinPlayUtils.getCurrentDateTime();
                    UseCode usercode = new UseCode(nickName, codex.getCode(), 1, timelog, usotp.getUsername(), usotp.getPhone(), usotp.getActive());
                    dao.insertCodeTanThu(usercode);
                    MoneyResponse mnres = userService.updateMoney(nickName, codex.getMoney(), "vin", "GiftCodeTanThu", "GiftCodeTanThu", "M\u00e3: " + code, 0L, null, TransType.NO_VIPPOINT);
                } else {

                    GiftCodeUpdateResponse response = gfService.updateGiftCode(nickName, code);


//                    if (exists) {
//                        GiftCodeUpdateResponse response = this.gfService.updateSpecialGiftCodeNew(user.getName(), cmd.giftCode);
//                        Debug.trace("Giftcode:" + cmd.giftCode + ":" + response.getErrorCode());
//                        if (response.isSuccess()) {
//                            msg.currentMoneyVin = response.currentMoneyVin;
//                            msg.currentMoneyXu = response.currentMoneyXu;
//                            msg.moneyGiftCodeVin = response.moneyGiftCodeVin;
//                            msg.moneyGiftCodeXu = response.moneyGiftCodeXu;
//                        }
//                        msg.Error = this.parseErrorCodeGiftCode(response.getErrorCode());
//                    } else {
//                        GiftCodeUpdateResponse response = this.gfService.updateGiftCode(user.getName(), cmd.giftCode);
//                        if (response.isSuccess()) {
//                            msg.currentMoneyVin = response.currentMoneyVin;
//                            msg.currentMoneyXu = response.currentMoneyXu;
//                            msg.moneyGiftCodeVin = response.moneyGiftCodeVin;
//                            msg.moneyGiftCodeXu = response.moneyGiftCodeXu;
//                        }
//                        msg.Error = this.parseErrorCodeGiftCode(response.getErrorCode());
//                    }
                }
            } else {
//                msg.Error = this.parseErrorCodeGiftCode("10003");
            }

        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        return null;
    }
}

