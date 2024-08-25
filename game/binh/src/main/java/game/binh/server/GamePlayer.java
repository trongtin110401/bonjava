// 
// Decompiled by Procyon v0.5.36
// 

package game.binh.server;

import game.binh.server.logic.KetQuaSoBai;
import game.binh.server.logic.GroupCard;
import net.sf.json.JSONObject;
import game.modules.gameRoom.entities.GameMoneyInfo;
import game.entities.PlayerInfo;
import bitzero.server.entities.User;

public class GamePlayer
{
    public static final int psNO_LOGIN = 0;
    public static final int psVIEW = 1;
    public static final int psSIT = 2;
    public static final int psPLAY = 3;
    public static final int THANG_SAM = 1;
    public static final int THANG_TRANG = 2;
    public static final int THANG_THUONG = 3;
    public int chair;
    public boolean reqQuitRoom;
    public boolean choiTiepVanSau;
    public boolean sochi;
    public int tuDongChoi;
    public int boSoChi;
    public User user;
    public PlayerInfo pInfo;
    public GameMoneyInfo gameMoneyInfo;
    public sPlayerInfo spInfo;
    public sResultInfo spRes;
    private volatile int playerStatus;
    public boolean isSoChi;
    public boolean sapLang;
    public int yeuCauBotRoiPhong;
    
    public GamePlayer() {
        this.reqQuitRoom = false;
        this.choiTiepVanSau = true;
        this.sochi = false;
        this.tuDongChoi = 0;
        this.boSoChi = 0;
        this.user = null;
        this.pInfo = null;
        this.gameMoneyInfo = null;
        this.spInfo = new sPlayerInfo();
        this.spRes = new sResultInfo();
        this.playerStatus = 0;
        this.isSoChi = false;
        this.sapLang = false;
        this.yeuCauBotRoiPhong = -1;
    }
    
    @Override
    public String toString() {
        try {
            final JSONObject json = this.toJSONObject();
            if (json != null) {
                return json.toString();
            }
            return "{}";
        }
        catch (Exception e) {
            return "{}";
        }
    }
    
    public JSONObject toJSONObject() {
        try {
            final JSONObject json = new JSONObject();
            if (this.user != null) {
                json.put((Object)"user", (Object)this.user.getName());
            }
            json.put((Object)"reqQuitRoom", (Object)this.reqQuitRoom);
            json.put((Object)"playerStatus", (Object)this.playerStatus);
            if (this.gameMoneyInfo != null) {
                json.put((Object)"gameMoneyInfo", (Object)this.gameMoneyInfo.toJSONObject());
            }
            if (this.spInfo.handCards != null) {
                json.put((Object)"handCards", (Object)this.spInfo.handCards.toString());
            }
            else {
                json.put((Object)"handCards", (Object)"");
            }
            return json;
        }
        catch (Exception e) {
            return null;
        }
    }
    
    public boolean dangChoSoChi() {
        return this.isPlaying() && !this.sochi;
    }
    
    public void prepareNewGame() {
        this.sochi = false;
        this.sapLang = false;
        this.spRes.resetResult();
        this.spInfo.clearInfo();
        this.tuDongChoi = 0;
    }
    
    public int getHandCardsSize() {
        return this.spInfo.handCards.cards.size();
    }
    
    public byte[] getHandCards() {
        return this.spInfo.handCards.toByteArray();
    }
    
    public void setPlayerStatus(final int playerStatus) {
        this.playerStatus = playerStatus;
    }
    
    public int getPlayerStatus() {
        return this.playerStatus;
    }
    
    public User getUser() {
        return this.user;
    }
    
    public PlayerInfo getPlayerInfo() {
        return this.pInfo;
    }
    
    public void addCards(final GroupCard groupCard, final boolean cheat, final int rule) {
        if (!cheat && this.user != null && !this.user.isBot()) {
            groupCard.kiemtraBo(rule);
        }
        this.spInfo.handCards = groupCard;
        this.spInfo.sorttedCard.ApplyNewGroupCards(this.spInfo.handCards, rule);
    }
    
    public int kiemTraMauBinh(final int rule) {
        return this.spInfo.getKind(rule);
    }
    
    public void takeChair(final User user, final PlayerInfo pInfo, final GameMoneyInfo moneyInfo) {
        this.user = user;
        this.pInfo = pInfo;
        this.gameMoneyInfo = moneyInfo;
        this.reqQuitRoom = false;
        user.setProperty("user_chair", this.chair);
    }
    
    public boolean isPlaying() {
        return this.playerStatus == 3;
    }
    
    public boolean canPlayNextGame() {
        return !this.reqQuitRoom && this.checkMoneyCanPlay();
    }
    
    public boolean hasUser() {
        return this.playerStatus != 0;
    }
    
    public boolean checkMoneyCanPlay() {
        return this.gameMoneyInfo.moneyCheck();
    }
    
    public void autoSort(final int rule) {
        this.spInfo.autoSort(rule);
    }
    
    public void thangThuaSapLang(final GamePlayer gp2) {
        final KetQuaSoBai kq11 = this.spRes.getResultWithPlayer(this.chair);
        final KetQuaSoBai kq12 = gp2.spRes.getResultWithPlayer(this.chair);
        final KetQuaSoBai kq13 = gp2.spRes.getResultWithPlayer(gp2.chair);
        final KetQuaSoBai kq14 = this.spRes.getResultWithPlayer(gp2.chair);
        kq12.thangThuaSapLang(gp2.chair);
        kq14.thangThuaSapLang(this.chair);
        kq11.thangThuaSapLang(kq14);
        kq13.thangThuaSapLang(kq12);
    }
}
