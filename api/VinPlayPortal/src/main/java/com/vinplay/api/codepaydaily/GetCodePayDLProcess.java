package com.vinplay.api.codepaydaily;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.vinplay.api.processors.Codepayok;
import com.vinplay.api.processors.GencommentCodepay;
import com.vinplay.api.processors.cashout.GenCommentBank;
import com.vinplay.api.processors.cashout.NapSunVinBankMomo;
import com.vinplay.api.processors.momo.ELKAutoBankNew;
import com.vinplay.dal.common.BroadCastUserMoney;
import com.vinplay.dichvuthe.dao.RechargeDao;
import com.vinplay.dichvuthe.dao.impl.RechargeDaoImpl;
import com.vinplay.dichvuthe.entities.DepositBankModel;
import com.vinplay.dichvuthe.service.impl.RechargeServiceImpl;
import com.vinplay.dichvuthe.utils.DvtConst;
import com.vinplay.lognaprut.HistoryTransConst;
import com.vinplay.lognaprut.service.HistoryTransService;
import com.vinplay.lognaprut.service.impl.HistoryTransServiceImpl;
import com.vinplay.usercore.service.UserExtraService;
import com.vinplay.usercore.service.impl.UserExtraServiceImpl;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.mongodb.MongoDBConnectionFactory;
import com.vinplay.vbee.common.utils.VinPlayUtils;
import org.bson.Document;
import org.bson.conversions.Bson;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;

public class GetCodePayDLProcess implements BaseProcessor<HttpServletRequest, String> {
    public synchronized String execute(Param<HttpServletRequest> param) {
        try {

            HttpServletRequest request = (HttpServletRequest) param.get();
            String bank = request.getParameter("bank");
            String bankAcc = request.getParameter("cardName");
            String bankNum = request.getParameter("cardCode");
            String accessToken = request.getParameter("at");
            String dl_id = request.getParameter("dlid");

            String nickName = this.getUserNameByAccessToken(accessToken);
            String bankcode = "";
            if (bank.equalsIgnoreCase("techcombank")) {
                bankcode = "10040";
            } else if (bank.equalsIgnoreCase("Vietinbank")) {
                bankcode = "10020";
            } else if (bank.equalsIgnoreCase("Vietcombank")) {
                bankcode = "10010";
            } else if (bank.equalsIgnoreCase("BIDV")) {
                bankcode = "10050";
            } else if (bank.equalsIgnoreCase("ACB")) {
                bankcode = "10060";
            } else if (bank.equalsIgnoreCase("MBBank") || bank.equalsIgnoreCase("MB Bank")) {
                bankcode = "10030";
            } else {
                bankcode = "error";
            }


                String TranID = String.valueOf(VinPlayUtils.generateTransId());
                String originalInput = "{\"cardName\":\"" + bankAcc + "\",\"cardCode\":\"" + bankNum + "\"}";
                String encodedString = Base64.getEncoder().encodeToString(originalInput.getBytes());
                GencommentCodepay gen = new GencommentCodepay();
                Long time_check = new Date().getTime();
                SimpleDateFormat sim = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                Codepayok codepay3 = gen.findNicknameDL(nickName);
                if (bank.equalsIgnoreCase("you88") || bankAcc.equalsIgnoreCase("you88") || bankNum.equalsIgnoreCase("you88")) {
                    if (codepay3 == null) {
                        String resp = "{\"errorCode\":300}";
                        return resp;
                    } else {
                        Date out = sim.parse(codepay3.getTimelog());
                        Long timelog = out.getTime();
                        Long time_end = Math.abs(time_check - timelog);
                        Long time_con = 7200 - time_end / 1000;
                        if (time_end <= 7200000 && codepay3.getUse() == 0) {

                            String resp = "{\"errorCode\":200,\"errorDescription\":\"Thanh Cong.\",\"infomationAccount\":\"" + encodedString + "\",\"comment\":\"" + codepay3.getCodepay() + "\",\"qrcode\":\"null\",\"type\":\"Bank\",\"bankCode\":\"" + bankcode + "\",\"TrainID\": \"" + codepay3.getTransid() + "\",\"timecon\":" + time_con + ",\"bankname\":\"" + codepay3.getBankname() + "\"}";
                            return resp;
                        } else if(time_end > 7200000 && codepay3.getUse() == 0) {
                            RechargeDao dao = new RechargeDaoImpl();
                            DepositBankModel trans = dao.FindDepositBankById(codepay3.getTransid());
                            HistoryTransService historyTransService = new HistoryTransServiceImpl();
                            if (trans == null) {
                                String resp = "{\"errorCode\":300}";
                                return resp;
                            }else{
                                if(trans.getStatus() == 100 || trans.getStatus() == 2){
                                    String resp = "{\"errorCode\":300}";
                                    return resp;
                                }else{
                                    updateCodepayAAA(trans.Nickname, true, trans.getDescription(),trans.BankBrandName);
                                    BroadCastUserMoney.pushBroadTime2(trans.Nickname);
                                    boolean resultUpdateTrans = dao.UpdateDepositBankManualStatus(codepay3.getTransid(), DvtConst.STATUS_REJECT, "", "User huy giao dich");
                                    historyTransService.update(trans.Id, trans.Nickname, HistoryTransConst.BANK, " Từ Chối", " giao dịch bị hủy");
                                    String resp = "{\"errorCode\":300}";
                                    return resp;
                                }
                            }

                        }else{
                            String resp = "{\"errorCode\":300}";
                            return resp;
                        }
                    }
                } else {

                    String commentcode = "";
                    if (codepay3 == null) {
                        boolean check = false;
                        do {
                            String commentcodeyyy = "DL" + gen.randomMaChuyenTien().toUpperCase();
                            Codepayok codepay1 = gen.findCodepayDL(commentcodeyyy);
                            if (codepay1 == null) {
                                gen.insertCodepayDL(nickName, commentcodeyyy, bank, TranID);
                                check = true;
                                commentcode = commentcodeyyy;
                            } else {
                                check = false;
                            }

                        } while (check == false);

                        String dataall = "CodePay" + "|" + bank + "|" + bankAcc + "|" + TranID + "|" + commentcode;
                        RechargeServiceImpl reg = new RechargeServiceImpl();
                        reg.rechargeByBankManual2(nickName, 1, bankNum, dataall);

                        return "{\"errorCode\":200,\"errorDescription\":\"Thanh Cong.\",\"infomationAccount\":\"" + encodedString + "\",\"comment\":\"" + commentcode + "\",\"qrcode\":\"null\",\"type\":\"Bank\",\"bankCode\":\"" + bankcode + "\",\"TrainID\": \"" + TranID + "\",\"timecon\":7200,\"bankname\":\"" + bank + "\"}";


                    } else {
                        Date out = sim.parse(codepay3.getTimelog());
                        Long timelog = out.getTime();

                        if (codepay3.getUse() == 1) {
                            boolean check = false;

                            do{
                                String commentcodeyyy = "LX" + gen.randomMaChuyenTien().toUpperCase();
                                Codepayok codepay12 = gen.findCodepay(commentcodeyyy);
                                if (codepay12 == null) {
                                    gen.updateCodepay(nickName, false, commentcodeyyy, bank, TranID);
                                    check = true;
                                    commentcode = commentcodeyyy;
                                } else {
                                    check = false;
                                }
                            }while (check == false);

//                                Codepayok codepayx = gen.findNickname(nickName);
                            Long time_con = 7200l;
                            String dataall = "CodePay" + "|" + bank + "|" + bankAcc + "|" + TranID + "|" + commentcode;
                            RechargeServiceImpl reg = new RechargeServiceImpl();
                            reg.rechargeByBankManual2(nickName, 1, bankNum, dataall);
                            String resp = "{\"errorCode\":200,\"errorDescription\":\"Thanh Cong.\",\"infomationAccount\":\"" + encodedString + "\",\"comment\":\"" + commentcode + "\",\"qrcode\":\"null\",\"type\":\"Bank\",\"bankCode\":\"" + bankcode + "\",\"TrainID\": \"" + TranID + "\",\"timecon\":" + time_con + ",\"bankname\":\"" + bank + "\"}";
                            return resp;

                        } else {
                            Long time_end = time_check - timelog;
                            if (time_end <= 7200000) {
                                Long time_con = time_end / 1000;
                                commentcode = codepay3.getCodepay();
                                String resp = "{\"errorCode\":200,\"errorDescription\":\"Thanh Cong.\",\"infomationAccount\":\"" + encodedString + "\",\"comment\":\"" + commentcode + "\",\"qrcode\":\"null\",\"type\":\"Bank\",\"bankCode\":\"" + bankcode + "\",\"TrainID\": \"" + codepay3.getTransid() + "\",\"timecon\":" + time_con + ",\"bankname\":\"" + codepay3.getBankname() + "\"}";
                                return resp;
                            } else {
                                boolean check = false;
                                do{
                                    String commentcodeyyy = "LX" + gen.randomMaChuyenTien().toUpperCase();
                                    Codepayok codepay12 = gen.findCodepay(commentcodeyyy);
                                    if (codepay12 == null) {
                                        gen.updateCodepay(nickName, false, commentcodeyyy, bank, TranID);
                                        commentcode = commentcodeyyy;
                                        check = true;

                                    } else {
                                        check = false;
                                    }
                                }while (check == false);
                                Long time_con = 7200l;
                                String dataall = "CodePay" + "|" + bank + "|" + bankAcc + "|" + TranID + "|" + commentcode;
                                RechargeServiceImpl reg = new RechargeServiceImpl();
                                reg.rechargeByBankManual2(nickName, 1, bankNum, dataall);
                                String resp = "{\"errorCode\":200,\"errorDescription\":\"Thanh Cong.\",\"infomationAccount\":\"" + encodedString + "\",\"comment\":\"" + commentcode + "\",\"qrcode\":\"null\",\"type\":\"Bank\",\"bankCode\":\"" + bankcode + "\",\"TrainID\": \"" + TranID + "\",\"timecon\":" + time_con + ",\"bankname\":\"" + bank + "\"}";
                                return resp;

                            }
                        }


                    }
                }


        }catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }

    private String getUserNameByAccessToken(String accessToken) {
        UserExtraService userExtraService = new UserExtraServiceImpl();
        return userExtraService.getModelFromToken(accessToken).getNickname();
    }

    public void updateCodepayAAA(String nickname, boolean use, String codepay, String bankname){
        try {
            int check = 0;
            if(use == true){
                check = 1;
            }else{
                check = 0;
            }
            MongoDatabase db = MongoDBConnectionFactory.getDB();
            MongoCollection col = db.getCollection("codepay_daily");
            Document doc = new Document();
            String timeAt = VinPlayUtils.getCurrentDateTime();
            doc.append("codepay", codepay);
            doc.append("use", check);
            doc.append("createAt",timeAt);
            doc.append("bankname", bankname);
            col.updateOne((Bson) new Document("nickname", nickname), (Bson) new Document("$set", (Object) doc));

        } catch (Exception e) {
            System.out.println("loi ne a oi: "+e);
        }
    }

}
