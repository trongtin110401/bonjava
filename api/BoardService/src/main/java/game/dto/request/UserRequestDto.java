package game.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class UserRequestDto {

    private String boardName;
    private List<String> users;
}
