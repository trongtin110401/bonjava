/*
 * Decompiled with CFR 0.144.
 */
package game.modules.slot.entities.slot.line20basic;

import java.util.ArrayList;
import java.util.List;

public class Line20 {
    private String name;
    private List<Line20Cell> cells = new ArrayList<Line20Cell>();

    public Line20(String name) {
        this.name = name;
    }

    public Line20(String name, List<Line20Cell> cells) {
        this.name = name;
        this.cells = cells;
    }

    public Line20(String name, int r1, int c1, int r2, int c2, int r3, int c3, int r4, int c4, int r5, int c5) {
        this.name = name;
        Line20Cell cell1 = new Line20Cell(r1, c1);
        this.cells.add(cell1);
        Line20Cell cell2 = new Line20Cell(r2, c2);
        this.cells.add(cell2);
        Line20Cell cell3 = new Line20Cell(r3, c3);
        this.cells.add(cell3);
        Line20Cell cell4 = new Line20Cell(r4, c4);
        this.cells.add(cell4);
        Line20Cell cell5 = new Line20Cell(r5, c5);
        this.cells.add(cell5);
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Line20Cell> getCells() {
        return this.cells;
    }

    public void setCells(List<Line20Cell> cells) {
        this.cells = cells;
    }

    public Line20Cell getCell(int index) {
        return this.cells.get(index);
    }

    public Line20Item getItem(int index) {
        return this.cells.get(index).getItem();
    }
}

