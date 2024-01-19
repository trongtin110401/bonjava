/*
 * Decompiled with CFR 0.144.
 */
package game.modules.slot.entities.slot.line20basic;

public class Slot20Cell {
    private int row;
    private int col;
    private Slot20Item item = Slot20Item.NONE;

    public Slot20Cell(int row, int col) {
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

    public Slot20Item getItem() {
        return this.item;
    }

    public void setItem(Slot20Item item) {
        this.item = item;
    }
}

