package com.vinplay.khothe;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.vinplay._1pay.StatusObj;
import com.vinplay.dichvuthe.client.HttpClient;
import com.vinplay.dichvuthe.encode.RSA;
import com.vinplay.dichvuthe.entities.SoftpinObj;
import com.vinplay.usercore.utils.GameCommon;
import com.vinplay.usercore.utils.PartnerConfig;
import com.vinplay.vbee.common.models.SoftpinJson;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;

public class KhoTheClient {

    private static final Logger logger = Logger.getLogger((String)"cashout");

    public static SoftpinObj buyCard(String id, String provider, int price, int quantity) throws Exception {
        BuyCardRequestObj reqObj = new BuyCardRequestObj();
        reqObj.setPartner_id(PartnerConfig.KhoThePartnerId);
        reqObj.setDenomination(price);
        reqObj.setPartner_reference(id);
        reqObj.setQuantity(quantity);
        reqObj.setTelco(provider);
        String data = "partner_id=" + PartnerConfig.KhoThePartnerId + "&partner_reference=" + reqObj.getPartner_reference() + "&quantity=" + reqObj.getQuantity()
                + "&telco=" + reqObj.getTelco() + "&denomination=" + reqObj.getDenomination();
        logger.debug(data);
        String sign = RSA.sign(data, PartnerConfig.KhoThePrivateKey);
        reqObj.setSignature(sign);
        logger.debug(sign);
        logger.debug(reqObj.toJson());
        ObjectMapper mapper = new ObjectMapper();
        String res = HttpClient.post(PartnerConfig.KhoTheEndpoint, mapper.writeValueAsString((Object)reqObj));
        logger.debug(res);
        Gson gson = new Gson();
        try {
            BuyCardResponseObj result = gson.fromJson(res, BuyCardResponseObj.class);
            SoftpinObj response = new SoftpinObj();
            response.setId(id);
            response.setProvider(provider);
            response.setAmount(price);
            response.setQuantity(quantity);
            response.setStatus(result.getCode());
            response.setMessage(result.getMessage());
            response.setSign("");
            response.setPartnerTransId(id);
            if (result.getCode() == 0) {
                ArrayList<SoftpinJson> softpinList = new ArrayList<SoftpinJson>();
                for (int i = 0; i < result.getData().getCards().size(); ++i) {
                    BuyCardResponseObj.BuyCardResponseCard card = result.getData().getCards().get(i);
                    SoftpinJson softpinJson = new SoftpinJson();
                    softpinJson.setProvider(provider);
                    softpinJson.setAmount(card.getAmount());
                    softpinJson.setPin(decrypt(card.getPin()));
                    softpinJson.setSerial(decrypt(card.getSerial()));
                    softpinJson.setExpire("");
                    softpinList.add(softpinJson);
                }
                response.setSoftpinList(softpinList);
            }
            return response;
        }
        catch (Exception ex)
        {
            SoftpinObj response = new SoftpinObj();
            response.setMessage(res);
            return response;
        }
    }

    private static String decrypt(String input)
    {
        try {
            return RSA.deCrypt(input, PartnerConfig.KhoThePublicKey);
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
        return "";
    }
}
