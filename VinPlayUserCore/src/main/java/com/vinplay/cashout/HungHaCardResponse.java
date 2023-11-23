package com.vinplay.cashout;

import com.vinplay.vbee.common.utils.VinPlayUtils;
import org.python.parser.ast.Str;

import java.util.List;

public class HungHaCardResponse {
    public List<HungHaCard> ListCard;
    public int Total;
    public String Username;
    public String Id;

    public HungHaCardResponse(){}

    public HungHaCardResponse(List<HungHaCard> listCard, int total) {
        ListCard = listCard;
        Total = total;
        Id = String.valueOf(VinPlayUtils.generateTransId());
    }
}
