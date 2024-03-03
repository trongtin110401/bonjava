package game.service.impl;

import game.dto.data.UserScore;
import game.dto.request.LeaderboardCriteria;
import game.dto.request.LeaderboardParam;
import game.dto.request.UserRequestDto;
import game.repository.LeaderboardRepository;
import game.service.LeaderboardService;
import org.redisson.client.protocol.ScoredEntry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LeaderboardServiceImpl implements LeaderboardService {

    @Autowired
    private LeaderboardRepository leaderboardRepository;

    @Override
    public void addScore(LeaderboardParam param) {
        leaderboardRepository.addScore(param.getBoardName(), param.getUsername(), param.getScore());
    }

    @Override
    public List<UserScore> receiveData(LeaderboardCriteria criteria) {
        int fromIndex = (criteria.getPageIndex() - 1) * criteria.getPageSize();
        int endIndex = fromIndex + criteria.getPageSize() -1;
        Collection<ScoredEntry<String>> scoredEntries =
                leaderboardRepository.receiveData(criteria.getBoardName(), fromIndex, endIndex);
        return scoredEntries.stream()
                .map(entry -> new UserScore(entry.getValue(), entry.getScore().longValue()))
                .collect(Collectors.toList());
    }

    @Override
    public List<UserScore> getByNicknameAndGameName(UserRequestDto userRequestDto) {
        return leaderboardRepository.getByNickNameAndGameName(userRequestDto.getBoardName(), userRequestDto.getUsers());
    }
}
