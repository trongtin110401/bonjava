package game;

public interface SlotExtendService {
    void logSlotExtend(long l, String s, long l1, String s1, String s2, String s3, short i, long l2, short i1, String s4) throws java.io.IOException, java.util.concurrent.TimeoutException, InterruptedException;

    int countLSDG(String s, int i);

    java.util.List<com.vinplay.vbee.common.models.minigame.pokego.LSGDPokeGo> getLSGD(String s, int i, int i1);

    void addTop(String s, int i, long l, int i1, String s1, int i2) throws java.io.IOException, java.util.concurrent.TimeoutException, InterruptedException;

    java.util.List<com.vinplay.vbee.common.models.minigame.pokego.TopPokeGo> getTopSlotExtend(int i, int i1);

    long getLastReferenceId();
}
