package game.controller;

import game.dto.data.UserScore;
import game.dto.request.LeaderboardCriteria;
import game.dto.request.LeaderboardParam;
import game.dto.request.UserRequestDto;
import game.dto.response.Response;
import game.service.LeaderboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping(path = "/leaderboard")
public class LeaderboardController {

    @Autowired
    private LeaderboardService leaderboardService;

    @GetMapping
    public ResponseEntity receive(@ModelAttribute LeaderboardCriteria criteria) {
        List<UserScore> results = leaderboardService.receiveData(criteria);
        return new ResponseEntity(Response.builder()
                .message("success")
                .status(0)
                .data(results)
                .build(), HttpStatus.OK);
    }


    @PostMapping
    public ResponseEntity add(@RequestBody LeaderboardParam param) {

        System.out.println(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));

        leaderboardService.addScore(param);
        return new ResponseEntity(Response.builder()
                .message("success")
                .status(0)
                .build(), HttpStatus.OK);
    }

    @GetMapping("/get_by_name")
    public ResponseEntity getByUsernameAndGameName(@ModelAttribute UserRequestDto criteria) {
        List<UserScore> results = leaderboardService.getByNicknameAndGameName(criteria);
        return new ResponseEntity(Response.builder()
                .message("success")
                .status(0)
                .data(results)
                .build(), HttpStatus.OK);
    }
}
