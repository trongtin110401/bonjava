package game.controller;

import game.service.SendingBeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController("/")
public class Login {

    @Autowired
    SendingBeService sendingBeService;

    @GetMapping("sendBe")
    // api nay chi de nhan event do ben gs ban sang ma thoi , ah ben admin php cung co the call internal cai api nay
    public ResponseEntity sendBE(@RequestParam(name = "game-name", defaultValue = "XocDia") String gameName) {
        sendingBeService.sendAdmin(gameName);
        return ResponseEntity.ok().body("ok");
    }
}
