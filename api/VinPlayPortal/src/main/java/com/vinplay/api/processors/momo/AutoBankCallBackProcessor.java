package com.vinplay.api.processors.momo;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.vinplay.api.entities.BankCallBack;
import com.vinplay.api.entities.BankCallBackResponse;
import com.vinplay.api.processors.AutoXuLyBank.AutoBankEntity;
import com.vinplay.common.HttpCommon;
import com.vinplay.dal.common.BroadCastUserMoney;
import com.vinplay.dichvuthe.dao.RechargeDao;
import com.vinplay.dichvuthe.dao.impl.RechargeDaoImpl;
import com.vinplay.dichvuthe.entities.DepositBankModel;
import com.vinplay.dichvuthe.utils.DvtConst;
import com.vinplay.lognaprut.HistoryTransConst;
import com.vinplay.lognaprut.entities.HistoryTransModel;
import com.vinplay.lognaprut.service.HistoryTransService;
import com.vinplay.lognaprut.service.impl.HistoryTransServiceImpl;
import com.vinplay.usercore.service.impl.UserServiceImpl;
import com.vinplay.usercore.utils.GameCommon;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.statics.Consts;
import com.vinplay.vbee.common.utils.VinPlayUtils;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.codec.digest.DigestUtils;
import org.json.JSONObject;
import org.bson.Document;
import com.vinplay.vbee.common.mongodb.MongoDBConnectionFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

public class AutoBankCallBackProcessor implements BaseProcessor<HttpServletRequest, String> {
    @Override
    public String execute(Param<HttpServletRequest> param) {
        HttpServletRequest request = (HttpServletRequest) param.get();
        String tranID = null;
        String keyID = null;
        String phoneAccount = null;
        String phoneCustomer = null;
        String nameCustomer = null;
        String comment = null;
        int mamount = 0;
        String type = null;
        String money = null;
        String signature = null;
        int status = 0;
        String body = null;
        try {
            StringBuilder sb = new StringBuilder();
            String line;
            java.io.BufferedReader reader = request.getReader();
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            body = sb.toString();

            System.out.println("body nap bank sunvin: " + body);
            if (body.contains("\"ResponseCode\":1")) {
                JSONObject obj = new JSONObject(body);
                String contentStr = obj.getString("ResponseContent");
                JSONObject jsonObject = new JSONObject(contentStr);
                tranID = jsonObject.getString("OrderInfo");
                keyID = jsonObject.getString("RefCode");
                phoneAccount = jsonObject.getString("Mobile");
                phoneCustomer = jsonObject.getString("Mobile");
                nameCustomer = jsonObject.getString("MomoName");
                comment = jsonObject.getString("OrderNo");
                mamount = jsonObject.getInt("Amount");
                type = jsonObject.getString("Type");
                money = jsonObject.getString("Amount");
                signature = obj.getString("Signature");
                status = obj.getInt("ResponseCode");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        BankCallBack bankcallback;
        BankCallBackResponse response = new BankCallBackResponse(200, "Thành công");
        bankcallback = new BankCallBack(tranID,keyID,phoneAccount,phoneCustomer,nameCustomer,comment,String.valueOf(mamount),type,money,signature);
        RechargeDao dao = new RechargeDaoImpl();
        // find transaction in db
        synchronized (this){
            DepositBankModel trans = dao.FindDepositBankById(bankcallback.getKeyID());
            HistoryTransService historyTransService = new HistoryTransServiceImpl();
            if (trans == null) {
                response.setErrorCode(500);
                response.setErrorDescription("Không tồn tại transaction Id");
                return response.toJson();
            }

            if(trans.getStatus() == 2 || trans.getStatus() == 100){
                response.setErrorCode(500);
                response.setErrorDescription("Từ chối callback. Giao dịch đã xử lý trước đó!!!");
                return response.toJson();
            }
            if(status != 1){
                boolean resultUpdateTrans = dao.UpdateDepositBankManualStatus(bankcallback.getKeyID(), DvtConst.STATUS_REJECT, "", "Nap Bank Auto");
                historyTransService.update(trans.Id, trans.Nickname, HistoryTransConst.BANK, " Từ Chối", " giao dịch bị hủy");
                if (!resultUpdateTrans) {
                    response.setErrorCode(500);
                    response.setErrorDescription("Cập nhật thất bại");
                    return response.toJson();
                }else{
                    response.setErrorCode(500);
                    response.setErrorDescription("Cập nhật đơn nạp bị hủy");
                    return response.toJson();
                }
            }

            if (verifySignature(body)){
                // update trạng thái thành công
                if (bankcallback.getAmount().equals(String.valueOf(trans.Amount)) && trans.getStatus() !=100) { // đúng mới cộng tiền
                    System.out.println("update status : ");

                    boolean resultUpdateTrans = dao.UpdateDepositBankManualStatus(bankcallback.getKeyID(), DvtConst.STATUS_APPROVE, "", "Nap Bank Auto");
//                    historyTransService.update(trans.Id, trans.Nickname, HistoryTransConst.BANK, "Thành công", " giao dịch thành công");
//                    if (!resultUpdateTrans) {
//                        System.out.println("Cập nhật thất bại : ");
//                        response.setErrorCode(500);
//                        response.setErrorDescription("Cập nhật thất bại");
//                        return response.toJson();
//                    }
                    // cộng tiền
                    UserServiceImpl service = new UserServiceImpl();
                    try {
                        System.out.println("Tinh tien : ");

                        double fee = GameCommon.getValueDouble("RATIO_RECHARGE_BANK");
                        double amount = fee * trans.Amount;
                        long totalFee = Math.round(trans.Amount - amount);
                        totalFee = totalFee > 0 ? totalFee : 0;
                        service.updateMoneyFromAdmin(trans.Nickname, (long) amount, "vin",
                                Consts.RECHARGE_BY_BANK, "Nạp Bank",
                                "nạp Bank tự động", totalFee);
                        this.insertTransaction(new HistoryTransModel(HistoryTransConst.BANK, HistoryTransConst.BANK, "recharge", String.valueOf(amount), "Thành Công", "giao dịch thành công", trans.getNickname(), HistoryTransConst.BANK, trans.Id));
                    }catch (Exception e) {
                        System.out.println("Lỗi cộng tiền bank: " + e.getMessage());
                        response.setErrorCode(500);
                        response.setErrorDescription(""+e);
                        return response.toJson();
                    }
                }else{
                    historyTransService.update(trans.Id, trans.Nickname, HistoryTransConst.BANK, "Từ chối", "Chuyển tiền không đủ");
                }


            }else{
                response.setErrorCode(500);
                response.setErrorDescription("Signature sai!");
                return response.toJson();
            }
            this.Notify(trans.getNickname(), String.valueOf(trans.getAmount()), comment);
            BroadCastUserMoney.pushBroadCast(trans.Nickname);
        }
        return response.toJson();
    }

    public void insertTransaction(HistoryTransModel historyTransModel) {
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        MongoCollection col = db.getCollection("History_User_transaction");
        Document doc = new Document();
        long idelk = VinPlayUtils.generateTransId();
        String timeAt = VinPlayUtils.getCurrentDateTime();
        doc.append("Id", idelk);
        doc.append("giaodich", historyTransModel.getGiaodich());
        doc.append("congGiaoDich", historyTransModel.congGiaoDich);
        doc.append("hinhthuc", historyTransModel.hinhthuc);
        doc.append("sotien", historyTransModel.sotien);
        doc.append("trangthai", historyTransModel.trangthai);
        doc.append("ghiChu", historyTransModel.ghiChu);
        doc.append("nickName", historyTransModel.nickName);
        doc.append("hinhthucTrans", historyTransModel.hinhthucTrans);
        doc.append("transId", historyTransModel.transId);
        doc.append("createAt", timeAt);
        col.insertOne(doc);
    }

    public void Notify(String nickname, String sotien, String maGiaoDich){
        try {

            String noidung = "[Hệ thống] Nạp tiền thành công%0A- Nick name: "+nickname+"%0A- Số tiền nạp: "+ sotien +"%0A- Nạp bằng ngân hàng" + "%0A- Mã giao dịch: " + maGiaoDich;

            OkHttpClient client = HttpCommon.getInstance().getHttpClient().newBuilder()
                    .build();
            Request request = new Request.Builder()
                    .url("https://api.telegram.org/bot8577075433:AAFiaTwiLAHforWcKFbV4qeMZ_Fp6C8Bg-Q/sendMessage?chat_id=-1003182093888&text="+noidung)
                    .method("GET", null)
                    .build();
            Response response = client.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean verifySignature(String body) {
        try {
            JSONObject obj = new JSONObject(body);
            String responseCode = obj.get("ResponseCode").toString();
            String description = obj.getString("Description");
            String responseContent = obj.getString("ResponseContent");
            String signatureFromServer = obj.getString("Signature");
            AutoBankEntity autoBank = new AutoBankEntity();
            String partnerKey = autoBank.partnerKey;
            String raw = responseCode + description + responseContent + partnerKey;
            String signatureLocal = DigestUtils.md5Hex(raw).toLowerCase();
            System.out.println("Signature from server: " + signatureFromServer);
            System.out.println("Signature local: " + signatureLocal);
            return signatureLocal.equals(signatureFromServer);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}
