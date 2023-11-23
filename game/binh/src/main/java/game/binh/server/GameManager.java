// 
// Decompiled by Procyon v0.5.36
// 

package game.binh.server;

import java.util.LinkedList;
import java.util.Comparator;

import bitzero.util.common.business.Debug;
import game.binh.server.logic.GroupCard;
import game.binh.server.logic.ai.BinhGroup;

import java.util.List;
import java.util.Collections;

import game.modules.bot.BotManager;
import bitzero.server.extensions.data.BaseMsg;
import game.binh.server.cmd.send.SendUpdateAutoStart;
import game.utils.GameUtils;
import game.binh.server.logic.Gamble;

public class GameManager {
    public static final int DEM_LA = 1;
    public static final int GS_NO_START = 0;
    public static final int GS_GAME_PLAYING = 1;
    public static final int GS_GAME_END = 2;
    public static final int NO_ACTION = 0;
    public static final int CHIA_BAI = 1;
    public static final int BINH_SO_CHI = 2;
    public static final int HIEN_KET_QUA = 3;
    public int roomOwnerChair;
    public int roomCreatorUserId;
    public int gameState;
    public int gameAction;
    public int countDown;
    public boolean isAutoStart;
    public Gamble game;
    public BinhGameServer gameServer;
    public GameLogic logic;

    public GameManager() {
        this.roomOwnerChair = 4;
        this.gameState = 0;
        this.gameAction = 0;
        this.countDown = 0;
        this.isAutoStart = false;
        this.game = new Gamble();
        this.logic = new GameLogic();
    }

    public int getGameState() {
        return this.gameState;
    }

    public void prepareNewGame() {
        this.game.reset();
        this.isAutoStart = false;
        this.gameServer.kiemTraTuDongBatDau(20);
    }

    public void gameLoop() {
        if (this.gameState == 0 && this.isAutoStart) {
            --this.countDown;
            if (this.countDown <= 0) {
                this.gameState = 1;
                this.gameServer.start();
            }
        } else if (this.gameState == 1) {
            if (this.gameAction != 0) {
                --this.countDown;
                if (GameUtils.isBot && this.gameAction == 2) {
                    this.gameServer.botAutoPlay();
                }
                if (this.countDown <= 0) {
                    if (this.gameAction == 1) {
                        this.chiaBai();
                    } else if (this.gameAction == 2) {
                        this.gameAction = 3;
                        this.gameServer.endGame();
                    }
                }
            }
        } else if (this.gameState == 2) {
            --this.countDown;
            if (this.countDown == 5) {
                this.gameServer.notifyNoHu();
            }
            if (this.countDown <= 0) {
                this.gameServer.pPrepareNewGame();
            }
        } else {
            ++this.countDown;
            this.gameServer.kiemTraTuDongBatDau(20);
            if (this.countDown % 11 == 10) {
                this.gameServer.botJoinRoom();
            }
        }
    }

    public void notifyAutoStartToUsers(final int after) {
         SendUpdateAutoStart msg = new SendUpdateAutoStart();
        msg.isAutoStart = this.isAutoStart;
        msg.autoStartTime = (byte) after;
        this.gameServer.send(msg);
    }

    public void cancelAutoStart() {
        this.isAutoStart = false;
        this.notifyAutoStartToUsers(0);
    }

    public void makeAutoStart(int after) {
        if (this.gameState != 0) {
            return;
        }
        if (!this.isAutoStart) {
            this.countDown = after;
        } else if (after < this.countDown) {
            this.countDown = after;
        } else {
            after = this.countDown;
        }
        this.isAutoStart = true;
        this.notifyAutoStartToUsers(after);
    }

    public void chiaBai() {
        int botCount = 0;
        for (int i = 0; i < 4; ++i) {
            final GamePlayer gp = this.gameServer.playerList.get(i);
            if (gp.getUser() != null) {
                if (gp.getUser().isBot()) {
                    ++botCount;
                }
            }
        }
        if (botCount == 0) {
            this.chiaBaiNgauNhien();
        }
        else if (BotManager.instance().balanceMode == 0) {
            final int x = BotManager.instance().getRandomNumber(2);
            if (x == 0 && this.gameServer.getRoom().setting.moneyBet >= 5000L) {
                this.chiaBaiCanBang(true, botCount);
            }
            else {
                this.chiaBaiNgauNhien();
            }
        }
        else {
            final boolean isUp = BotManager.instance().balanceMode == 1;
            int x2 = BotManager.instance().getRandomNumber(3);
            if (isUp && this.gameServer.getRoom().setting.moneyBet >= 5000L) {
                x2 = 1;
            }
            if (x2 != 0) {
                this.chiaBaiCanBang(isUp, botCount);
            }
            else {
                this.chiaBaiNgauNhien();
            }
        }
    }

    public void chiaBaiNgauNhien() {
        final boolean canJackpot = this.gameServer.getRoom().setting.moneyBet < 1000L;
        final List<BinhGroup> cards = this.game.suit.dealCards(this.gameServer.getRoom().setting.rule, false);
        if (this.game.suit.cheat == 0) {
            Collections.shuffle(cards);
        }
        for (int i = 0; i < 4; ++i) {
            final GamePlayer gp = this.gameServer.playerList.get(i);
            if (gp.getUser() != null && gp.getUser().isBot()) {
                final GroupCard gc = cards.get(i).getOrderGroupCard();
                gp.addCards(gc, this.game.isCheat, this.gameServer.getRoom().setting.rule);
            }
            else {
                final GroupCard gc = cards.get(i).getRandomGroupCard();
                gp.addCards(gc, this.game.isCheat, this.gameServer.getRoom().setting.rule);
            }
        }
        this.gameServer.chiabai();
        this.gameAction = 2;
        this.countDown = 66;
    }
    
    public void chiaBaiCanBang(final boolean isUp, final int botCount) {
        final boolean canJackpot = this.gameServer.getRoom().setting.moneyBet < 1000L;
        final List<BinhGroup> cards = this.game.suit.dealCards(this.gameServer.getRoom().setting.rule, false);
        Collections.sort(cards, BinhGroup.SORT_COMPARATOR);
        final LinkedList<BinhGroup> high = new LinkedList<BinhGroup>();
        final LinkedList<BinhGroup> low = new LinkedList<BinhGroup>();
        int highSize = 4 - botCount;
        if (isUp) {
            highSize = botCount;
        }
        for (int i = 0; i < 4; ++i) {
            if (i < highSize) {
                high.add(cards.get(i));
            }
            else {
                low.add(cards.get(i));
            }
        }
        Collections.shuffle(high);
        Collections.shuffle(low);
        int lowCount = 0;
        int highCount = 0;
        for (int j = 0; j < 4; ++j) {
            final GamePlayer gp = this.gameServer.playerList.get(j);
            if (gp.getUser() != null && gp.getUser().isBot()) {
                if (isUp) {
                    final GroupCard gc = high.get(highCount++).getOrderGroupCard();
                    gp.addCards(gc, this.game.isCheat, this.gameServer.getRoom().setting.rule);
                } else {
                    final GroupCard gc = low.get(lowCount++).getOrderGroupCard();
                    gp.addCards(gc, this.game.isCheat, this.gameServer.getRoom().setting.rule);
                }
            } else if (isUp) {
                final GroupCard gc = low.get(lowCount++).getOrderGroupCard();
                gp.addCards(gc, this.game.isCheat, this.gameServer.getRoom().setting.rule);
            } else {
                final GroupCard gc = high.get(highCount++).getOrderGroupCard();
                gp.addCards(gc, this.game.isCheat, this.gameServer.getRoom().setting.rule);
            }
        }
        this.gameServer.chiabai();
        this.gameAction = 2;
        this.countDown = 66;
    }

    public boolean canOutRoom() {
        return this.getGameState() == 0;
    }
}
