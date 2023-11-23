package com.vinplay.api.processors.SEOGame;

import com.google.gson.Gson;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

public class ThongKeSEO implements BaseProcessor<HttpServletRequest, String> {
    public String execute(Param<HttpServletRequest> param) {
        ArrayList<Key2Entity> list_tk = new ArrayList<>();
        try {
            HttpServletRequest request = (HttpServletRequest)param.get();
            String time_start = request.getParameter("start");
            String time_end = request.getParameter("end");
            XuLyAnali xuly = new XuLyAnali();
            ArrayList<KeyEnity> list_key = xuly.getListKey(1,1000);
            ArrayList<AnaliEntity> list_seo = xuly.GetThongKE(time_start,time_end);


            for(KeyEnity k : list_key){
                int key1 = 0;
                int key2 = 0;
                int key3 = 0;
                int key4 = 0;
                int key5 = 0;
                int key6 = 0;
                int key7 = 0;
                int key8 = 0;
                int key9 = 0;
                int key10 = 0;
                Key3Entity k1 = new Key3Entity();
                Key3Entity k2 = new Key3Entity();
                Key3Entity k3 = new Key3Entity();
                Key3Entity k4 = new Key3Entity();
                Key3Entity k5 = new Key3Entity();
                Key3Entity k6 = new Key3Entity();
                Key3Entity k7 = new Key3Entity();
                Key3Entity k8 = new Key3Entity();
                Key3Entity k9 = new Key3Entity();
                Key3Entity k10 = new Key3Entity();
                k1.setValue(k.getKey1());
                k2.setValue(k.getKey2());
                k3.setValue(k.getKey3());
                k4.setValue(k.getKey4());
                k5.setValue(k.getKey5());
                k6.setValue(k.getKey6());
                k7.setValue(k.getKey7());
                k8.setValue(k.getKey8());
                k9.setValue(k.getKey9());
                k10.setValue(k.getKey10());
                String dai_ly = k.getCode_dl();
                for(AnaliEntity a : list_seo){
                    if(k.getCode_dl().equalsIgnoreCase(a.getCode_dl())){

                        if(k.getKey10().equalsIgnoreCase(a.getSource())){
                            key10++;
                        }
                        if(k.getKey1().equalsIgnoreCase(a.getAction())){
                            key1++;
                        }
                        if(k.getKey2().equalsIgnoreCase(a.getAction())){
                            key2++;
                        }
                        if(k.getKey3().equalsIgnoreCase(a.getAction())){
                            key3++;
                        }
                        if(k.getKey4().equalsIgnoreCase(a.getAction())){
                            key4++;
                        }
                        if(k.getKey5().equalsIgnoreCase(a.getAction())){
                            key5++;
                        }
                        if(k.getKey6().equalsIgnoreCase(a.getAction())){
                            key6++;
                        }
                        if(k.getKey7().equalsIgnoreCase(a.getAction())){
                            key7++;
                        }
                        if(k.getKey8().equalsIgnoreCase(a.getAction())){
                            key8++;
                        }
                        if(k.getKey9().equalsIgnoreCase(a.getAction())){
                            key9++;
                        }

                    }
                }
                k1.setCount(key1);
                k2.setCount(key2);
                k3.setCount(key3);
                k4.setCount(key4);
                k5.setCount(key5);
                k6.setCount(key6);
                k7.setCount(key7);
                k8.setCount(key8);
                k9.setCount(key9);
                k10.setCount(key10);
                ThongKeEntity tk = new ThongKeEntity(k.getCode_dl(),key1,key2,key3,key4,key5,key6,key7,key8,key9,key10);
                Key2Entity tk2 = new Key2Entity(k.getCode_dl(), k1, k2,k3,k4,k5,k6,k7,k8,k9,k10);
                list_tk.add(tk2);

            }
            Gson gson = new Gson();
            return gson.toJson(list_tk);

        }catch (Exception e) {
            e.printStackTrace();
        }
        Gson gson = new Gson();
        return gson.toJson(list_tk);
    }
}
