package game.repository;

import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RedissonClient;
import org.redisson.client.protocol.ScoredEntry;

import java.util.Collection;
import java.util.List;

public interface LeaderboardRepository {

    /**
     * @param boardName
     * @param value
     */
    void addScore(String boardName, String key, long value);

    /**
     * @param boardName
     * @param from
     * @param end
     * @return
     */
    Collection<ScoredEntry<String>> receiveData(String boardName, int fromIndex, int endIndex);

    /**
     * @return
     */
    RedissonClient getRedissonClient();

    /**
     * @param boardName
     * @param <T>
     * @return
     */
    default <T> RScoredSortedSet<T> getBoard(String boardName) {
        return getRedissonClient().getScoredSortedSet(boardName);
    }

}
