package com.vinplay.vbee.rmq.report.processor;

import com.vinplay.common.HttpCommon;
import com.vinplay.vbee.dto.TopVinhDanhDto;
import okhttp3.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TopVinhDanhProcessor {
    private static final int timeoutInSeconds = 5; // 5 seconds

    public static void addTopVinhDanh(TopVinhDanhDto topVinhDanhDto) {

        List<String> boardNames = generateBoardName(topVinhDanhDto.getBoardName());
        for (String boardName : boardNames) {
            try {
                TopVinhDanhDto topVinhDanh = new TopVinhDanhDto();
                topVinhDanh.setUsername(topVinhDanhDto.getUsername());
                topVinhDanh.setScore(topVinhDanhDto.getScore());
                topVinhDanh.setBoardName(boardName);
                OkHttpClient client = HttpCommon.getInstance().getHttpClient().newBuilder()
                        .connectTimeout(timeoutInSeconds, java.util.concurrent.TimeUnit.SECONDS)
                        .readTimeout(timeoutInSeconds, java.util.concurrent.TimeUnit.SECONDS)
                        .build();
                MediaType mediaType = MediaType.parse("application/json");
                RequestBody body = RequestBody.create(mediaType, topVinhDanh.toJson());
                Request request = new Request.Builder()
                        .url("http://localhost:8087/leaderboard?boardName")
                        .method("POST", body)
                        .addHeader("Content-Type", "application/json")
                        .build();
                client.newCall(request).execute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static List<String> generateBoardName(String gameName) {
        List<String> boardNames = new ArrayList<>();
        LocalDate currentDate = LocalDate.now();
        boardNames.add(gameName + "_" + "DAY_" + currentDate.format(DateTimeFormatter.ofPattern("yyyyMMdd")));
        boardNames.add(gameName + "_" + "WEEK_" + currentDate.getYear() + currentDate.get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear()));
        boardNames.add(gameName + "_" + "MONTH_" + currentDate.getYear() + currentDate.getMonthValue());
        boardNames.add(gameName + "_" + "YEAR_" + currentDate.getYear());
        return boardNames;
    }

    public static void main(String[] args) {
        TopVinhDanhDto topVinhDanhDto = new TopVinhDanhDto();
        topVinhDanhDto.setBoardName("TAIXIU");
        topVinhDanhDto.setUsername("testacc123");
        topVinhDanhDto.setScore(50000);
        addTopVinhDanh(topVinhDanhDto);

    }
}
