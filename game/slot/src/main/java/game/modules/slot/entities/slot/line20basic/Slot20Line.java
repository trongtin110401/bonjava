/*
 * Decompiled with CFR 0.144.
 */
package game.modules.slot.entities.slot.line20basic;

import java.util.ArrayList;
import java.util.List;

public class Slot20Line {
    private String name;
    private List<Slot20Cell> cells = new ArrayList<Slot20Cell>();

    public Slot20Line(String name) {
        this.name = name;
    }

    public Slot20Line(String name, List<Slot20Cell> cells) {
        this.name = name;
        this.cells = cells;
    }

    public Slot20Line(String name, int r1, int c1, int r2, int c2, int r3, int c3, int r4, int c4, int r5, int c5) {
        this.name = name;
        Slot20Cell cell1 = new Slot20Cell(r1, c1);
        this.cells.add(cell1);
        Slot20Cell cell2 = new Slot20Cell(r2, c2);
        this.cells.add(cell2);
        Slot20Cell cell3 = new Slot20Cell(r3, c3);
        this.cells.add(cell3);
        Slot20Cell cell4 = new Slot20Cell(r4, c4);
        this.cells.add(cell4);
        Slot20Cell cell5 = new Slot20Cell(r5, c5);
        this.cells.add(cell5);
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Slot20Cell> getCells() {
        return this.cells;
    }

    public void setCells(List<Slot20Cell> cells) {
        this.cells = cells;
    }

    public Slot20Cell getCell(int index) {
        return this.cells.get(index);
    }

    public Slot20Item getItem(int index) {
        return this.cells.get(index).getItem();
    }
}

