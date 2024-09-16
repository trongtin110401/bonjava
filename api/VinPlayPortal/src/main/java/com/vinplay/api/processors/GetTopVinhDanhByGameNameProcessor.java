package com.vinplay.api.processors;

import com.vinplay.api.constants.TypeVinhDanhConstants;
import com.vinplay.api.processors.minigame.response.TopVinhDanhResponse;
import com.vinplay.usercore.utils.GameCommon;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.models.minigame.TopWin;
import com.vinplay.vbee.common.rmq.HttpCommon;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class GetTopVinhDanhByGameNameProcessor
        implements BaseProcessor<HttpServletRequest, String> {
    private static final Logger logger = Logger.getLogger("api");

    TopVinhDanhResponse rp = new TopVinhDanhResponse(false, "1001");

    public String execute(Param<HttpServletRequest> param) {
        HttpServletRequest request = param.get();
        String boardName = request.getParameter("boardName");
        String pageIndex = request.getParameter("pageIndex");
        String pageSize = request.getParameter("pageSize");
        String type = request.getParameter("type");

        if (boardName == null || pageSize == null || pageIndex == null || type == null) {
            logger.error("request is not valid, some fields is null");
            return rp.toJson();
        }
        if (TypeVinhDanhConstants.getValueByKey(type) == null) {
            logger.error("type is not valid");
            return rp.toJson();
        }
        try {
            OkHttpClient client = HttpCommon.getInstance().getHttpClient().newBuilder()
                    .connectTimeout(3, TimeUnit.SECONDS)  // 3 seconds for connecting to the server
                    .readTimeout(3, TimeUnit.SECONDS)     // 3 seconds for reading the response
                    .build();
            String typeDate = getDate(type);
            String host = GameCommon.getValueStr("url_leaderboard");
            String url = "http://" + host + ":8087/leaderboard?boardName=" + boardName + "_" + typeDate + "&pageIndex=" + pageIndex + "&pageSize=" + pageSize;
            Request rq = new Request.Builder()
                    .url(url)
                    .method("GET", null)
                    .build();
            Response response = client.newCall(rq).execute();
            if (response.body() == null) {
                return rp.toJson();
            }
            List<TopWin> topWins = getTopWin(response.body().string());
            rp.setSuccess(true);
            rp.setErrorCode("0");
            rp.setListVinhDanh(topWins);
            response.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rp.toJson();
    }

    private String getDate(String type) {
        String dateType;
        LocalDate currentDate = LocalDate.now();
        if (type.equals(TypeVinhDanhConstants.DAY)) {
            dateType = "DAY_" + currentDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        } else if (type.equals(TypeVinhDanhConstants.WEEK)) {
            dateType = "WEEK_" + currentDate.getYear() + currentDate.get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear());
        } else if (type.equals(TypeVinhDanhConstants.MONTH)) {
            dateType = "MONTH_" + currentDate.getYear() + currentDate.getMonthValue();
        } else {
            dateType = "YEAR_" + currentDate.getYear();
        }

        return dateType;
    }

    public List<TopWin> getTopWin(String response) {
        List<TopWin> topWins = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray dataArray = jsonObject.getJSONArray("data");

            for (int i = 0; i < dataArray.length(); i++) {
                JSONObject dataObject = dataArray.getJSONObject(i);

                if (dataObject.getInt("score") <= 0) {
                    continue;
                }
                TopWin topWin = new TopWin();
                topWin.setUsername(dataObject.getString("username"));
                topWin.setTotalMoneyOnGame(dataObject.getInt("score"));
                topWin.setMoney(dataObject.getInt("score"));
                topWins.add(topWin);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return topWins;
    }
}
