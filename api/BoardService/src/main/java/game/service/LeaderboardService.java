package game.service;

import game.dto.data.UserScore;
import game.dto.request.LeaderboardCriteria;
import game.dto.request.LeaderboardParam;

import java.util.List;

public interface LeaderboardService {

    /**
     * @param param
     */
    void addScore(LeaderboardParam param);

    /**
     * @param criteria
     * @return
     */
    List<UserScore> receiveData(LeaderboardCriteria criteria);
}
