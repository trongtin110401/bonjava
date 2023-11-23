import com.vinplay.usercore.utils.GameCommon;
import com.vinplay.vbee.common.config.VBeePath;
import com.vinplay.vbee.common.hazelcast.HazelcastLoader;
import com.vinplay.vbee.common.mongodb.MongoDBConnectionFactory;
import com.vinplay.vbee.common.rmq.RMQApi;
import com.vinplay.vbee.common.utils.VinPlayUtils;

public class MainTest {
    private static String basePath;
    public static void main(String[] args) {
        try{
            basePath = VBeePath.initBasePath(MainTest.class);
            //RMQApi.start((String)"config/rmq.properties");
            //HazelcastLoader.start();
            //MongoDBConnectionFactory.init();
            //GameCommon.init();

            //System.out.println(result.toString());
            //CashOutService cashOutService = new CashOutServiceImpl();
            //SoftpinResponse res = cashOutService.cashOutByCardHungHa("raymond001", "VTT", 10000, 1);
            //System.out.println(res.toJson());
           // boolean ok = TelegramUtil.sendMessage("message without reply", "-497727920","1412367452:AAGd5h3tzJlVMUVN8SpxcaD-UWsZa8a7YWQ");
            //String pw = VinPlayUtils.getMD5Hash("$24S4rvwB*N85%");
            //System.out.println(pw);
            //BotMinigame.loadData();

        }catch (Exception e){
            System.out.println(e);
        }
        //HunghapayClient client = new HunghapayClient("bao99.club@gmail.com", "baoanh2017");
        //HungHaCardResponse result = client.BuyCard("VTT", 10000, 1);

    }
}
