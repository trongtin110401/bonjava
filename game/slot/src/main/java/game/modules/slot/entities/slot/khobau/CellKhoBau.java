/*
 * Decompiled with CFR 0.144.
 */
package game.modules.slot.entities.slot.khobau;

import game.modules.slot.entities.slot.khobau.KhoBauItem;

public class CellKhoBau {
    private int row;
    private int col;
    private KhoBauItem item = KhoBauItem.NONE;

    public CellKhoBau(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public int getRow() {
        return this.row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getCol() {
        return this.col;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public KhoBauItem getItem() {
        return this.item;
    }

    public void setItem(KhoBauItem item) {
        this.item = item;
    }
}

