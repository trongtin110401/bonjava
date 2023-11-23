package com.vinplay.zoan;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

public class ZoanMomoResponse {
    private int code;
    private String message;
    private ZoanMomoData data;

    public ZoanMomoResponse(int _code, String _message)
    {
        this.code = _code;
        this.message = _message;
    }

    public String toJson() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString((Object)this);
        }
        catch (JsonProcessingException mapper) {
            return "{\"code\":500,\"message\":\"error\"}";
        }
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ZoanMomoData getData() {
        return data;
    }

    public void setData(ZoanMomoData data) {
        this.data = data;
    }

    public class ZoanMomoData
    {
        private String partnerName;
        private List<ZoanMomoAccount> accounts;

        public String getPartnerName() {
            return partnerName;
        }

        public void setPartnerName(String partnerName) {
            this.partnerName = partnerName;
        }

        public List<ZoanMomoAccount> getAccounts() {
            return accounts;
        }

        public void setAccounts(List<ZoanMomoAccount> accounts) {
            this.accounts = accounts;
        }
    }

    public class ZoanMomoAccount
    {
        private String phone;
        private int isMaxquota;
        private String name;

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public int getIsMaxquota() {
            return isMaxquota;
        }

        public void setIsMaxquota(int isMaxquota) {
            this.isMaxquota = isMaxquota;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
