/*
 * Decompiled with CFR 0.144.
 */
package game.modules.slot.entities.slot.line20basic;

import java.util.ArrayList;
import java.util.List;

public class Line20Lines {
    private List<Line20> lines = new ArrayList<Line20>();

    public Line20Lines() {
        this.initLines();
    }

    private void initLines() {
        this.lines.clear();
        Line20 line1 = new Line20("line1", 1, 0, 1, 1, 1, 2, 1, 3, 1, 4);
        this.lines.add(line1);
        Line20 line2 = new Line20("line2", 0, 0, 0, 1, 0, 2, 0, 3, 0, 4);
        this.lines.add(line2);
        Line20 line3 = new Line20("line3", 2, 0, 2, 1, 2, 2, 2, 3, 2, 4);
        this.lines.add(line3);
        Line20 line4 = new Line20("line4", 1, 0, 1, 1, 0, 2, 1, 3, 1, 4);
        this.lines.add(line4);
        Line20 line5 = new Line20("line5", 1, 0, 1, 1, 2, 2, 1, 3, 1, 4);
        this.lines.add(line5);
        Line20 line6 = new Line20("line6", 0, 0, 0, 1, 1, 2, 0, 3, 0, 4);
        this.lines.add(line6);
        Line20 line7 = new Line20("line7", 2, 0, 2, 1, 1, 2, 2, 3, 2, 4);
        this.lines.add(line7);
        Line20 line8 = new Line20("line8", 0, 0, 2, 1, 0, 2, 2, 3, 0, 4);
        this.lines.add(line8);
        Line20 line9 = new Line20("line9", 2, 0, 0, 1, 2, 2, 0, 3, 2, 4);
        this.lines.add(line9);
        Line20 line10 = new Line20("line10", 1, 0, 0, 1, 2, 2, 0, 3, 1, 4);
        this.lines.add(line10);
        Line20 line11 = new Line20("line11", 2, 0, 1, 1, 0, 2, 1, 3, 2, 4);
        this.lines.add(line11);
        Line20 line12 = new Line20("line12", 0, 0, 1, 1, 2, 2, 1, 3, 0, 4);
        this.lines.add(line12);
        Line20 line13 = new Line20("line13", 1, 0, 2, 1, 1, 2, 0, 3, 1, 4);
        this.lines.add(line13);
        Line20 line14 = new Line20("line14", 1, 0, 0, 1, 1, 2, 2, 3, 1, 4);
        this.lines.add(line14);
        Line20 line15 = new Line20("line15", 2, 0, 1, 1, 1, 2, 1, 3, 2, 4);
        this.lines.add(line15);
        Line20 line16 = new Line20("line16", 0, 0, 1, 1, 1, 2, 1, 3, 0, 4);
        this.lines.add(line16);
        Line20 line17 = new Line20("line17", 1, 0, 0, 1, 0, 2, 0, 3, 1, 4);
        this.lines.add(line17);
        Line20 line18 = new Line20("line18", 1, 0, 2, 1, 2, 2, 2, 3, 1, 4);
        this.lines.add(line18);
        Line20 line19 = new Line20("line19", 2, 0, 2, 1, 1, 2, 0, 3, 0, 4);
        this.lines.add(line19);
        Line20 line20 = new Line20("line20", 0, 0, 0, 1, 1, 2, 2, 3, 2, 4);
        this.lines.add(line20);
    }

    public List<Line20> list() {
        return this.lines;
    }

    public Line20 get(int index) {
        return this.lines.get(index);
    }

    public void renew() {
    }
}

