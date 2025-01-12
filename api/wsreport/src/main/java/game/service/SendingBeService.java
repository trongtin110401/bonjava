package game.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import game.bean.MapperUtils;
import game.entity.entitiesxocdia.GamePotReportModel;
import game.entity.entitiesxocdia.UserBetModel;
import game.entity.response.XocDiaReportResponse;
import game.exceptions.KeyNotFoundException;
import game.scheduler.ScheduledTasks;
import game.ws.ServerGame;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.yeauty.pojo.Session;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
public class SendingBeService {

    @Autowired
    CacheService cacheService;

    @Async
    public CompletableFuture<Long> sendAdmin(String gameName) {
        if (gameName.equals("XocDia")) {
            return processForXocDia();
        } else {
            return processForXocDiaKubet();
        }
    }

    /**
     * @return
     */
    private CompletableFuture<Long> processForXocDia() {
        final long start = System.currentTimeMillis();
        try {
            ArrayList<GamePotReportModel> gamePotReportModels = new ArrayList<>();
            for (int i = 0; i < 6; i++) {
                GamePotReportModel gamePotReportModel = MapperUtils.mapper.readValue(cacheService.getValueStr("XocDia_Pot" + i), GamePotReportModel.class);
                gamePotReportModels.add(gamePotReportModel);
            }
            XocDiaReportResponse xocDiaReportResponse = convertModel(gamePotReportModels);
            String json = MapperUtils.mapper.writeValueAsString(xocDiaReportResponse);
            this.sendMessToXocDiaAdmin(json);
        } catch (KeyNotFoundException | JsonProcessingException e) {
            e.printStackTrace();
        }
        return CompletableFuture.completedFuture(start);
    }

    /**
     * @return
     */
    private CompletableFuture<Long> processForXocDiaKubet() {
        final long start = System.currentTimeMillis();
        try {
            ArrayList<GamePotReportModel> gamePotReportModels = new ArrayList<>();
            for (int i = 0; i < 6; i++) {
                GamePotReportModel gamePotReportModel = MapperUtils.mapper.readValue(cacheService.getValueStr("XocDiaKubet_Pot" + i), GamePotReportModel.class);
                gamePotReportModels.add(gamePotReportModel);
            }
            XocDiaReportResponse xocDiaReportResponse = convertModel(gamePotReportModels);
            String json = MapperUtils.mapper.writeValueAsString(xocDiaReportResponse);
            this.sendMessToXocDiaKubetAdmin(json);
        } catch (KeyNotFoundException | JsonProcessingException e) {
            e.printStackTrace();
        }
        return CompletableFuture.completedFuture(start);
    }

    /**
     * @param mess
     */
    private void sendMessToXocDiaAdmin(String mess) {
        for (Session session : ServerGame.sessions) {
            session.sendText(mess);
        }
    }

    /**
     * @param mess
     */
    private void sendMessToXocDiaKubetAdmin(String mess) {
        for (Session session : ServerGame.sessions) {
            session.sendText(mess);
        }
    }

    /**
     * @param gamePotReportModels
     * @return
     */
    static XocDiaReportResponse convertModel(ArrayList<GamePotReportModel> gamePotReportModels) {
        XocDiaReportResponse xocDiaResponse = new XocDiaReportResponse();
        xocDiaResponse.setCode("2");
        List<UserBetModel> userBetModels = new ArrayList<>();
        List<String> users = new ArrayList<>();
        for (GamePotReportModel model : gamePotReportModels) {
            UserBetModel userBetModel = new UserBetModel();
            for (Map.Entry<String, Long> entry : model.getUserBetMap().entrySet()) {
                if (!users.contains(entry.getKey())) {
                    users.add(entry.getKey());
                    userBetModel.setNickname(entry.getKey());
                    userBetModels.add(userBetModel);
                }
            }
        }
        ScheduledTasks scheduledTasks = new ScheduledTasks();
        userBetModels = scheduledTasks.getUserXD(userBetModels, "XocDia");
        xocDiaResponse.setUsers(userBetModels);
        xocDiaResponse.setPotList(gamePotReportModels);
        return xocDiaResponse;
    }
}
