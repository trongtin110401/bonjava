package game.repository.impl;

import game.dto.data.UserScore;
import game.repository.LeaderboardRepository;
import org.redisson.Redisson;
import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RedissonClient;
import org.redisson.client.protocol.ScoredEntry;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Repository
public class LeaderboardRepositoryImpl implements LeaderboardRepository {

    @Value("${redis.host:redis://localhost:6379}")
    private String redisHost;

    private RedissonClient redissonClient;

    @PostConstruct
    public void construct() {
        Config config = new Config();
        config.useSingleServer().setAddress(redisHost);
        redissonClient = Redisson.create(config);
    }

    @PreDestroy
    public void destroy() {
        redissonClient.shutdown();
    }

    @Override
    public void addScore(String boardName, String key, long value) {
        RScoredSortedSet<String> board = getBoard(boardName);
        board.addScore(key, value);
    }

    @Override
    public Collection<ScoredEntry<String>> receiveData(String boardName, int startIndex, int endIndex) {
        RScoredSortedSet<String> board = getBoard(boardName);
        return board.entryRangeReversed(startIndex, endIndex);
    }

    @Override
    public RedissonClient getRedissonClient() {
        return redissonClient;
    }

    @Override
    public List<UserScore> getByNickNameAndGameName(String boardName, List<String> nicknames) {
        List<UserScore> response = new ArrayList<>();
        RScoredSortedSet<String> board = getBoard(boardName);
        for (String nickname : nicknames) {
            UserScore userTopScoreResponse = new UserScore();
            userTopScoreResponse.setUsername(nickname);
            Double score = board.getScore(nickname);
            if (score == null) {
                score = 0d;
            }
            userTopScoreResponse.setScore(score.longValue());
            response.add(userTopScoreResponse);

        }
        return response;
    }
}
