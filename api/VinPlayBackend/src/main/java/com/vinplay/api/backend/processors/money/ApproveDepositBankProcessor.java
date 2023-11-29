package com.vinplay.api.backend.processors.money;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.vinplay.api.backend.processors.cashout.NapRutGame;
import com.vinplay.api.backend.processors.cashout.NapRutModel;
import com.vinplay.common.notification.SendToWS;
import com.vinplay.common.report.EventactionAdminObj;
import com.vinplay.dal.common.BroadCastUserMoney;
import com.vinplay.dichvuthe.dao.RechargeDao;
import com.vinplay.dichvuthe.dao.impl.RechargeDaoImpl;
import com.vinplay.dichvuthe.entities.DepositBankModel;
import com.vinplay.dichvuthe.service.impl.RechargeServiceImpl;
import com.vinplay.dichvuthe.utils.DvtConst;
import com.vinplay.lognaprut.HistoryTransConst;
import com.vinplay.lognaprut.HistoryTransDao;
import com.vinplay.lognaprut.entities.HistoryTransModel;
import com.vinplay.lognaprut.impl.HistoryTransDaoImpl;
import com.vinplay.lognaprut.impl.InsertELK;
import com.vinplay.lognaprut.service.HistoryTransService;
import com.vinplay.lognaprut.service.impl.HistoryTransServiceImpl;
import com.vinplay.usercore.service.impl.UserServiceImpl;
import com.vinplay.usercore.utils.GameCommon;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.mongodb.MongoDBConnectionFactory;
import com.vinplay.vbee.common.response.BaseResponseModel;
import com.vinplay.vbee.common.statics.Consts;
import com.vinplay.vbee.common.utils.VinPlayUtils;
import org.apache.log4j.Logger;
import org.bson.Document;
import org.bson.conversions.Bson;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

// todo : approve tiền nạp qua ngân hàng cho user
public class ApproveDepositBankProcessor implements BaseProcessor<HttpServletRequest, String> {
    private static final Logger logger = Logger.getLogger("backend");
// todo fix connect db oxyhome6699
    @Override
    public String execute(Param<HttpServletRequest> param) {
        // type = 0 is approve
        // type = 1 is reject
        logger.info("=============ApproveDepositBankProcessor start=============");
        synchronized (this) {
            BaseResponseModel response = new BaseResponseModel(false, "1001");
            HistoryTransService historyTransService = new HistoryTransServiceImpl();
            try {
                HttpServletRequest request = (HttpServletRequest) param.get();
                //check transId
                String transId = request.getParameter("transId");
                String typeStr = request.getParameter("type");
                String userApprove = request.getParameter("uad");
                String tienx = request.getParameter("tien");
                logger.info("Param info: " + "transId: " + transId + " typeStr" + typeStr + " tien" + tienx);
                long tien = Long.parseLong(tienx);
                long tien_final = 0;
                if (transId.isEmpty() || typeStr.isEmpty()) {
                    logger.error("transId or typeStr is empty");
                    return response.toJson();
                }
                int type = Integer.parseInt(typeStr);
                RechargeDao dao = new RechargeDaoImpl();

                RechargeServiceImpl rechargeService = new RechargeServiceImpl();
                DepositBankModel trans = rechargeService.finMoMoDepositByTransactionId(transId);



                // update trans in db
                int status = type == 100 ? DvtConst.STATUS_APPROVE : DvtConst.STATUS_REJECT;
//                boolean resultUpdateTrans = dao.UpdateDepositBankManualStatus(transId, status, trans.Description, userApprove);
                boolean resultUpdateTrans = dao.UpdateDepositBankManualStatus(transId, status, trans.getDescription(), userApprove);
                logger.debug(this.getClass().getName() + "resultUpdateTrans: " + resultUpdateTrans);
                if (!resultUpdateTrans) {
                    return response.toJson();
                }
                logger.error(this.getClass().getName() + " Type :" + type);
                if (type == 1) {
                    BroadCastUserMoney.pushBroadTime2(trans.getNickname());
                    response.setSuccess(true);
                    historyTransService.update(transId, trans.getNickname(), HistoryTransConst.BANK, "Từ chối", "Giao dịch bị từ chối");
                    EventactionAdminObj model = new EventactionAdminObj();
                    model.setId(transId);
                    model.setStatus(2);
                    model.setType("DEPOSIT_BANK");
                    updateCodepay(trans.getNickname(), true,trans.getDescription(), trans.getUserSender());
                    try {
                        SendToWS.sendBEExcEventaction(model);
                    } catch (IOException e) {
                        logger.error(e.getMessage());
                        e.printStackTrace();
                    }
                    return response.toJson();
                }
                //update user money
                if (type == 100) {
                    UserServiceImpl service = new UserServiceImpl();
                    try {
                        double flus = GameCommon.getValueDouble("RATIO_RECHARGE_BANK_TL");
                        double fee = GameCommon.getValueDouble("RATIO_RECHARGE_BANK");
                        double amount = fee * tien;
                        long totalFee = Math.round(tien - amount);
                        double tien_tmp = tien * flus;
                        tien_final = (long) tien_tmp;
                        totalFee = totalFee > 0 ? totalFee : 0;
                        response = service.updateMoneyFromAdmin(trans.getNickname(), tien_final, "vin", Consts.RECHARGE_BY_BANK, "Deposit bank", "Deposit bank", totalFee);

                    } catch (Exception e) {
                        logger.error(e.getMessage());
                        e.printStackTrace();
                    }
                    HistoryTransDao historyTransDao = new HistoryTransDaoImpl();
                    historyTransDao.insertTransaction(new HistoryTransModel(transId + "|" + "test bank", "CodePay", "Nạp tiền", "", "Thành công", "Nạp tiền Thành công ", "trans.Nickname", HistoryTransConst.BANK, transId));
                    updateMoneyCodePayMomoSun(transId, tien_final);
                    updateMoneyCodePayMomoSun2(transId, tien_final + "");
                    updateSttCodePayMomoSun(transId, userApprove);
                    updateSTTCodePayMomoSun2(transId);
                    EventactionAdminObj model = new EventactionAdminObj();
                    model.setId(transId);
                    model.setStatus(100);
                    model.setType("DEPOSIT_BANK");
                    try {
                        SendToWS.sendBEExcEventaction(model);
                    } catch (IOException e) {
                        logger.error(e.getMessage());
                        e.printStackTrace();
                    }
                }
                BroadCastUserMoney.pushBroadCast(trans.getNickname());
                BroadCastUserMoney.pushBroadTime(trans.getNickname());
                updateCodepay(trans.getNickname(), true, trans.getDescription(),trans.getBankBrandName());
                updateMoneyCodePayMomoSun(transId, tien);
                updateMoneyCodePayMomoSun2(transId, tien + "");
                NapRutGame nrg = new NapRutGame();
                String codedl = nrg.getMaDaily(trans.Nickname);
                long SoTien = tien;
                if (codedl == null) {
                    int xx = 2;
                } else if (codedl != null && codedl.trim().length() == 0) {
                    int xx = 2;
                } else if (codedl != null && codedl.trim().equalsIgnoreCase("null") == false) {
                    String usend = trans.getUserSender();

                    if (usend.equalsIgnoreCase("CodePay")) {
                        NapRutModel napgame = new NapRutModel(transId, trans.getNickname(), codedl, SoTien, "CodePay", trans.CreatedAt);
                        nrg.NapRut(napgame);
                    } else if (usend.equalsIgnoreCase("Momo")) {
                        NapRutModel napgame = new NapRutModel(trans.getId(),  trans.getNickname(), codedl, SoTien, "MoMo", trans.CreatedAt);
                        nrg.NapRut(napgame);
                    } else {
                        NapRutModel napgame = new NapRutModel(trans.getId(),  trans.getNickname(), codedl, SoTien, "Bank", trans.CreatedAt);
                        nrg.NapRut(napgame);
                    }
                    NapRutModel napgame = new NapRutModel(transId,trans.getNickname() , codedl, SoTien, "Bank", trans.CreatedAt);
                    nrg.NapRut(napgame);
                } else {
                    int xx = 2;
                }

                return response.toJson();

            } catch (Exception e) {
                logger.error(e.getMessage());
                return response.toJson();
            }

        }
    }

    private void updateMoneyCodePayMomoSun(String TrainID, long tien) {
        try {
            MongoDatabase db = MongoDBConnectionFactory.getDB();
            MongoCollection col = db.getCollection("deposit_bank_manual");
            Document doc = new Document();
            doc.append("Amount", tien);
            col.updateOne((Bson) new Document("Id", TrainID), (Bson) new Document("$set", (Object) doc));

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void updateMoneyCodePayMomoSun2(String TrainID, String tien) {
        try {
            InsertELK elk = new InsertELK();
            MongoDatabase db = MongoDBConnectionFactory.getDB();
            MongoCollection col = db.getCollection("History_User_transaction");
            Document doc = new Document();
            doc.append("sotien", tien);
            col.updateOne((Bson) new Document("transId", TrainID), (Bson) new Document("$set", (Object) doc));
            HistoryTransModel his = elk.GetHistorybyTransID(TrainID);
            his.setSotien(tien);
            elk.InsertHistoryUserTransOK(his, Long.parseLong(his.getId()), his.getCreateAt());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void updateSttCodePayMomoSun(String TrainID, String ua) {
        try {
            MongoDatabase db = MongoDBConnectionFactory.getDB();
            MongoCollection col = db.getCollection("deposit_bank_manual");
            Document doc = new Document();
            doc.append("Status", 100);
            doc.append("UserApprove", ua);
            col.updateOne((Bson) new Document("Id", TrainID), (Bson) new Document("$set", (Object) doc));

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void updateSTTCodePayMomoSun2(String TrainID) {
        try {
            InsertELK elk = new InsertELK();
            MongoDatabase db = MongoDBConnectionFactory.getDB();
            MongoCollection col = db.getCollection("History_User_transaction");
            Document doc = new Document();
            doc.append("trangthai", "Thành công");
            col.updateOne((Bson) new Document("transId", TrainID), (Bson) new Document("$set", (Object) doc));
            HistoryTransModel his = elk.GetHistorybyTransID(TrainID);
            his.setTrangthai("Thành công");
            elk.InsertHistoryUserTransOK(his, Long.parseLong(his.getId()), his.getCreateAt());

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void updateCodepay(String nickname, boolean use, String codepay, String bankname) {
        try {
            int check = 0;
            if (use == true) {
                check = 1;
            } else {
                check = 0;
            }
            MongoDatabase db = MongoDBConnectionFactory.getDB();
            MongoCollection col = db.getCollection("CodePayOK");
            Document doc = new Document();
            String timeAt = VinPlayUtils.getCurrentDateTime();
            doc.append("codepay", codepay);
            doc.append("use", check);
            doc.append("createAt", timeAt);
            doc.append("bankname", bankname);
            col.updateOne((Bson) new Document("nickname", nickname), new Document("$set", doc));

        } catch (Exception e) {
            System.out.println("loi ne a oi: " + e);
        }
    }

}
