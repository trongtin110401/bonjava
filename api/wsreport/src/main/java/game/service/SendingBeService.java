package game.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import game.bean.MapperUtils;
import game.entity.entitiesxocdia.GamePotReportModel;
import game.entity.response.XocDiaReportResponse;
import game.exceptions.KeyNotFoundException;
import game.ws.ServerGame;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.yeauty.pojo.Session;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

@Service
public class SendingBeService {
    private static final String USER_TAI_XIU = "user_tai_xiu";  // serive  send den be , service nay dc goi khi call api be kia , no se lay du lieu tu  cache tra ve ben fe ket noi ws


    @Autowired
    CacheService cacheService;

    @Async
    public CompletableFuture<Long> sendAdmin() {

        final long start = System.currentTimeMillis();
        try {
            String flag = (String) cacheService.getValueStr("Xocdia_Change");

            ArrayList<GamePotReportModel> gamePotReportModels = new ArrayList<GamePotReportModel>();
            for (int i = 0; i < 6; i++) {
                GamePotReportModel gamePotReportModel = MapperUtils.mapper.readValue(cacheService.getValueStr("XocDia_Pot" + i), GamePotReportModel.class);
                gamePotReportModels.add(gamePotReportModel);
            }
            XocDiaReportResponse xocDiaReportResponse = new XocDiaReportResponse("2", gamePotReportModels);

            String json = MapperUtils.mapper.writeValueAsString(xocDiaReportResponse);
            this.sendMessToAdmin(json);


        } catch (KeyNotFoundException | JsonProcessingException e) {
            e.printStackTrace();
            System.out.println("lloi duoi");

        }
        return CompletableFuture.completedFuture(start);
    }

    private void sendMessToAdmin(String mess) {
        for (Session session : ServerGame.sessions) {
            session.sendText(mess);
        }
    }


}
