package game.controller;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path = "/hotupdate")
public class HotUpdateController {

    @Value("${hotupdate.allowed.countrie}")
    private List<String> allowedCountries;

    @Value("${hotupdate.rejected.countries}")
    private List<String> rejectedCountries;

    private Map<String, String> allowedCountriesMap = new HashMap<>();
    private Map<String, String> rejectedCountriesMap = new HashMap<>();

    DatabaseReader dbReader;

    @PostConstruct
    public void init() throws IOException {
        allowedCountries.forEach(s -> allowedCountriesMap.put(s, s));
        rejectedCountries.forEach(s -> rejectedCountriesMap.put(s, s));

        File database = new File(System.getProperty("user.dir") + File.separator + "db" + File.separator + "GeoLite2-Country.mmdb");
        dbReader = new DatabaseReader.Builder(database).build();
    }


    @GetMapping("/check")
    public ResponseEntity<String> check(HttpServletRequest request, HttpServletResponse response) throws IOException, GeoIp2Exception {
        String ip = request.getRemoteAddr();
        String country = dbReader.country(InetAddress.getByName(ip)).getCountry().getIsoCode();
        return ResponseEntity.ok(country);
    }

}
