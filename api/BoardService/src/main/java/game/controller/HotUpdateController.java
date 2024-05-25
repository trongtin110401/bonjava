package game.controller;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;

@RestController
@RequestMapping(path = "/hotupdate")
public class HotUpdateController {

    @GetMapping("/check")
    public ResponseEntity<String> check(HttpServletRequest request, HttpServletResponse response) throws IOException, GeoIp2Exception {
        String ip = request.getRemoteAddr();
        File database = new File(System.getProperty("user.dir") + File.separator + "db" + File.separator + "GeoLite2-Country.mmdb");
        DatabaseReader dbReader = new DatabaseReader.Builder(database).build();
        String country = dbReader.country(InetAddress.getByName(ip)).getCountry().getIsoCode();

        return ResponseEntity.ok(country);
    }

}
