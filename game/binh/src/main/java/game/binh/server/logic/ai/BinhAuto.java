// 
// Decompiled by Procyon v0.5.36
// 

package game.binh.server.logic.ai;

import com.vinplay.vbee.common.config.VBeePath;
import game.utils.GameUtils;
import game.utils.LoggerUtils;
import java.util.Calendar;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.io.File;
import java.util.LinkedList;
import java.util.Random;
import java.util.List;

public class BinhAuto
{
    private static BinhAuto ins;
    private List<BinhSuit> binhAt;
    private List<BinhSuit> binhThuong;
    private List<BinhSuit> jackpotAt;
    private List<BinhSuit> jackpotThuong;
    private Random rd;
    private volatile int lastDay;
    private static String basePath;
    
    public static void main(final String[] args) {
        final long t = System.currentTimeMillis();
        final BinhSuit suit = instance().getSuit(0);
        final long dt = System.currentTimeMillis() - t;
        System.out.println(dt);
        System.out.println(suit);
    }
    
    public static BinhAuto instance() {
        if (BinhAuto.ins == null) {
            BinhAuto.ins = new BinhAuto();
        }
        return BinhAuto.ins;
    }
    
    private BinhAuto() {
        this.binhAt = new LinkedList<BinhSuit>();
        this.binhThuong = new LinkedList<BinhSuit>();
        this.jackpotAt = new LinkedList<BinhSuit>();
        this.jackpotThuong = new LinkedList<BinhSuit>();
        this.rd = new Random();
        this.lastDay = 0;
        this.initJackpot();
        this.init();
    }
    
    public void loadData(final int rule, final int fileIndex, final List<BinhSuit> list, final List<BinhSuit> jackpotList) throws Exception {
        final StringBuilder sb = new StringBuilder();
        sb.append(BinhAuto.basePath);
        if (rule == 0) {
          sb.append("/data/binhthuong/binh_newbie").append(fileIndex).append(".properties");
        }
        else {
            sb.append("/data/binhat/binh_advance").append(fileIndex).append(".properties");
        }
        final File file = new File(sb.toString());
        final FileInputStream fileStream = new FileInputStream(file);
        final InputStreamReader decoder = new InputStreamReader(fileStream, "UTF-8");
        final BufferedReader reader = new BufferedReader(decoder);
        String text = null;
        int count = 0;
        final Object suilt = null;
        final String[] lines = new String[4];
        do {
            text = reader.readLine();
            final int d = count % 5;
            if (d != 4) {
                lines[d] = text;
            }
            else {
                final BinhSuit suit = new BinhSuit(lines);
                if (!suit.canJackpot()) {
                    list.add(suit);
                }
                else {
                    jackpotList.add(suit);
                }
            }
            ++count;
        } while (text != null);
        fileStream.close();
        decoder.close();
        reader.close();
    }
    
    public void initJackpot() {
        try {
            this.loadData(0, 200, this.binhThuong, this.jackpotThuong);
            this.loadData(1, 200, this.binhAt, this.jackpotAt);
        }
        catch (Exception ex) {}
    }
    
    public synchronized void init() {
        try {
            final long t1 = System.currentTimeMillis();
            this.lastDay = Calendar.getInstance().get(6);
            final int random = new Random().nextInt(200);
            this.binhThuong.clear();
            this.binhAt.clear();
            this.loadData(0, random, this.binhThuong, this.jackpotThuong);
            this.loadData(1, random, this.binhAt, this.jackpotAt);
            final long t2 = System.currentTimeMillis();
            LoggerUtils.error("binh", (Object[])new Object[] { "Binh Data loading time(ms):", t2 - t1, this.binhThuong.size(), this.jackpotThuong.size(), this.jackpotAt.size(), this.binhAt.size() });
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public synchronized BinhSuit getSuit(final int rule, final boolean canJackpot) {
        final int today = Calendar.getInstance().get(6);
        if (today != this.lastDay) {
            this.init();
        }
        if (!canJackpot) {
            return this.getSuit(rule);
        }
//        final int rd = GameUtils.rd.nextInt(2000);
//        if (rd == 0) {
//            return this.getSuitJackpot(rule);
//        }
        return this.getSuit(rule);
    }
    
    private BinhSuit getSuit(final int rule) {
        int max = 0;
        int randomIndex = 0;
        try {
            max = this.binhThuong.size();
            if (rule == 1) {
                max = this.binhAt.size();
            }
            randomIndex = this.rd.nextInt(max);
            if (rule == 0) {
                return this.binhThuong.get(randomIndex);
            }
            return this.binhAt.get(randomIndex);
        }
        catch (Exception e) {
            LoggerUtils.error("binh", (Object[])new Object[] { "ERROR BinhAuTo getSuit", "binhThuong =", this.binhThuong.size(), "jackpotThuong =", this.jackpotThuong.size(), "binhAt =", this.binhAt.size(), "jackpotAt =", this.jackpotAt.size(), "max =", max, "randomIndex =", randomIndex });
            return null;
        }
    }
    
    public synchronized BinhSuit getSuitJackpot(final int rule) {
        int max = this.jackpotThuong.size();
        if (rule == 1) {
            max = this.jackpotAt.size();
        }
        final int randomIndex = this.rd.nextInt(max);
        if (rule == 0) {
            return this.jackpotThuong.get(randomIndex);
        }
        return this.jackpotAt.get(randomIndex);
    }
    
    static {
        BinhAuto.ins = null;
        BinhAuto.basePath = VBeePath.basePath;
    }
    
    public class BinhDataLoad implements Runnable
    {
        @Override
        public void run() {
            try {
                for (int i = 1; i < 200; ++i) {
                    BinhAuto.this.loadData(0, i, BinhAuto.this.binhThuong, BinhAuto.this.jackpotThuong);
                    BinhAuto.this.loadData(1, i, BinhAuto.this.binhAt, BinhAuto.this.jackpotAt);
                }
                LoggerUtils.debug("binh", (Object[])new Object[] { "COMPLETED LOADING BINH DATA" });
            }
            catch (Exception e) {
                LoggerUtils.error("binh", (Object[])new Object[] { "ERROR BinhDataLoad run:", e.toString() });
            }
        }
    }
}
