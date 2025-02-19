package game.kubet;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.LoadState;
import com.microsoft.playwright.options.WaitForSelectorState;
import org.apache.commons.lang3.RandomStringUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.zip.GZIPInputStream;

public class Test {

    public static void main(String[] args) {

        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true).setSlowMo(50));
            BrowserContext context = browser.newContext();
            Page page = context.newPage();
            page.navigate("https://www.ku2552.net/Home/Index");
            int step = 0;
            // Chờ trang tải xong
            page.waitForLoadState(LoadState.NETWORKIDLE);
            page.click("#RegisterImmediate");

            // Đợi popup hiển thị (tuỳ vào trang web, có thể cần điều chỉnh)
            // Chờ trang tải xong
//            page.waitForTimeout(3000);
            page.fill("#AccountID", RandomStringUtils.randomAlphanumeric(12));
//            page.waitForTimeout(1000);
            page.fill("#NickName", "FRV22025");
//            page.waitForTimeout(1000);
            page.fill("#PWD", "1111aaaa");
//            page.waitForTimeout(1000);

            page.click("#RegisterMemberButton");
//            page.waitForTimeout(1000);

            page.waitForURL("https://www.ku2552.net/Home/RegisterFinished"); // Thay đổi URL tùy theo trang thực tế

            System.out.println("step 1");
            page.locator("//*[@id=\"MainMenu\"]/div/div[1]/div[2]/ul/li[5]/a").click();
            System.out.println("step 2");
            page.waitForLoadState(LoadState.NETWORKIDLE);
            System.out.println("step 3");

//            page.locator("//*[@id=\"FastTransfer\"]/div[2]/div[2]/input").click();
//            System.out.println("step 4");

            // Chờ tab mới được mở
            Page newPage = context.waitForPage(() -> {
                // Click sẽ mở tab mới
                page.locator("//*[@id=\"FastTransfer\"]/div[2]/div[2]/input").click();
            });

            // Chờ tab mới tải hoàn toàn
            newPage.waitForURL("https://ga88.ku2552.net/Home/Game");

            // In ra URL của tab mới
            System.out.println("Tab mới được mở: " + newPage.url());


            System.out.println(newPage.title());


//            getTimeSessionKey(newPage);

            newPage.onWebSocket(ws -> {
                if (ws.url().equals("wss://kuapi1.win/")) {
                    ws.onFrameReceived(data -> {
                        // Giải nén dữ liệu
                        byte[] decompressedData = new byte[0];
                        try {
                            decompressedData = ungzip(data.binary());
//                            String content = new String(decompressedData, "UTF-8");
//                            if (content.contains("\"gType\":71")) {
                                System.out.println(LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + " : " + new String(decompressedData, "UTF-8"));
                                System.out.println("\n");
//                            }
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
                    ws.onSocketError(s -> System.out.println("error: " + s));
                }
            });

            newPage.waitForTimeout(12000000);
        }
//        catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }

    }

    private static void getTimeSessionKey(Page newPage) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    // Gửi POST request từ trình duyệt
                    Object response = newPage.evaluate("async () => {" +
                            "   const response = await fetch('https://ga88.ku2552.net/Api/Common/GetSKAll', {" + // Thay bằng URL API thực tế
                            "       method: 'POST'," +
                            "       headers: { 'Content-Type': 'application/json' }," +
                            "       body: JSON.stringify({" +
                            "           SKey: '3'," +
                            "           CKey: '611011'," +
                            "           DType: 1," +
                            "           CType: 1," +
                            "           IsS: 1," +
                            "           PlayTool: 4" +
                            "       })" +
                            "   });" +
                            "   return response.json();" + // Trả về JSON phản hồi
                            "}");

                    // In kết quả phản hồi từ API
                    System.out.println("API Response: " + response);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }).start();
    }

    public static byte[] ungzip(byte[] compressedData) throws IOException {
        try (ByteArrayInputStream byteStream = new ByteArrayInputStream(compressedData);
             GZIPInputStream gzipStream = new GZIPInputStream(byteStream);
             ByteArrayOutputStream outStream = new ByteArrayOutputStream()) {

            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = gzipStream.read(buffer)) != -1) {
                outStream.write(buffer, 0, bytesRead);
            }
            return outStream.toByteArray();
        }
    }
}
