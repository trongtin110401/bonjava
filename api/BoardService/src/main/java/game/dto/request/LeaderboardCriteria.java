package game.dto.request;

import lombok.Data;

@Data
public class LeaderboardCriteria {

    private String boardName;
    private int pageIndex;
    private int pageSize;
}
