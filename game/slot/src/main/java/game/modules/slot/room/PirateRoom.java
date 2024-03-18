package game.modules.slot.room;

import game.modules.slot.SlotModule;
import game.modules.slot.cmd.Slot25CommandCollection;
import game.modules.slot.cmd.send.slot25linebasic.Slot25ResultMsg;
import game.modules.slot.listener.SlotLogListener;

public class PirateRoom extends Slot25BasicRoom {

    public PirateRoom(SlotModule module, Slot25CommandCollection commandCollection, SlotLogListener logListener, String gameName, byte id, String room, short moneyType, long pot, long fund, int betValue, long initJackpotValue) {
        super(module, commandCollection, logListener, gameName, id, room, moneyType, pot, fund, betValue, initJackpotValue);
    }


    @Override
    protected void afterPlay(Slot25ResultMsg msg) {

    }

    @Override
    protected int setFreeSpin(String nickName, String lines, int countFreeSpin, int remainAmountOfFreeSpin) {
        int soLuot = 0;
        switch (countFreeSpin) {
            case 3: {
                soLuot = 4 + remainAmountOfFreeSpin;
                slotService.setLuotQuayFreeSlot(this.cacheFreeSpinName, nickName, lines, soLuot, 1, betValue);
                break;
            }
            case 6: {
                soLuot = 8 + remainAmountOfFreeSpin;
                slotService.setLuotQuayFreeSlot(this.cacheFreeSpinName, nickName, lines, soLuot, 2, betValue);
                break;
            }
            case 18: {
                soLuot = 22 + remainAmountOfFreeSpin;
                slotService.setLuotQuayFreeSlot(this.cacheFreeSpinName, nickName, lines, soLuot, 3, betValue);
            }
        }
        return Math.max(soLuot, remainAmountOfFreeSpin);
    }
}
