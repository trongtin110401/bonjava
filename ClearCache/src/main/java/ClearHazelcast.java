import com.vinplay.vbee.common.utils.CommonUtils;

import java.util.ArrayList;
import java.util.List;

public class ClearHazelcast {
    public static void main(String[] args) {
        List<String> users = new ArrayList<String>();
        users.add("testdaily1");

        CommonUtils.clearHazelcastMap("users", users);

        System.out.println("test");
    }
}
