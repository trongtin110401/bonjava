/*
 * Decompiled with CFR 0.144.
 */
package game.modules.slot.entities.slot.line20basic;

import java.util.ArrayList;
import java.util.List;

public class Slot20Lines {
    private List<Slot20Line> lines = new ArrayList<Slot20Line>();

    public Slot20Lines() {
        this.initLines();
    }

    private void initLines() {
        this.lines.clear();
        Slot20Line line1 = new Slot20Line("line1", 1, 0, 1, 1, 1, 2, 1, 3, 1, 4);
        this.lines.add(line1);
        Slot20Line line2 = new Slot20Line("line2", 0, 0, 0, 1, 0, 2, 0, 3, 0, 4);
        this.lines.add(line2);
        Slot20Line line3 = new Slot20Line("line3", 2, 0, 2, 1, 2, 2, 2, 3, 2, 4);
        this.lines.add(line3);
        Slot20Line line4 = new Slot20Line("line4", 1, 0, 1, 1, 0, 2, 1, 3, 1, 4);
        this.lines.add(line4);
        Slot20Line line5 = new Slot20Line("line5", 1, 0, 1, 1, 2, 2, 1, 3, 1, 4);
        this.lines.add(line5);
        Slot20Line line6 = new Slot20Line("line6", 0, 0, 0, 1, 1, 2, 0, 3, 0, 4);
        this.lines.add(line6);
        Slot20Line line7 = new Slot20Line("line7", 2, 0, 2, 1, 1, 2, 2, 3, 2, 4);
        this.lines.add(line7);
        Slot20Line line8 = new Slot20Line("line8", 0, 0, 2, 1, 0, 2, 2, 3, 0, 4);
        this.lines.add(line8);
        Slot20Line line9 = new Slot20Line("line9", 2, 0, 0, 1, 2, 2, 0, 3, 2, 4);
        this.lines.add(line9);
        Slot20Line line10 = new Slot20Line("line10", 1, 0, 0, 1, 2, 2, 0, 3, 1, 4);
        this.lines.add(line10);
        Slot20Line line11 = new Slot20Line("line11", 2, 0, 1, 1, 0, 2, 1, 3, 2, 4);
        this.lines.add(line11);
        Slot20Line line12 = new Slot20Line("line12", 0, 0, 1, 1, 2, 2, 1, 3, 0, 4);
        this.lines.add(line12);
        Slot20Line line13 = new Slot20Line("line13", 1, 0, 2, 1, 1, 2, 0, 3, 1, 4);
        this.lines.add(line13);
        Slot20Line line14 = new Slot20Line("line14", 1, 0, 0, 1, 1, 2, 2, 3, 1, 4);
        this.lines.add(line14);
        Slot20Line line15 = new Slot20Line("line15", 2, 0, 1, 1, 1, 2, 1, 3, 2, 4);
        this.lines.add(line15);
        Slot20Line line16 = new Slot20Line("line16", 0, 0, 1, 1, 1, 2, 1, 3, 0, 4);
        this.lines.add(line16);
        Slot20Line line17 = new Slot20Line("line17", 1, 0, 0, 1, 0, 2, 0, 3, 1, 4);
        this.lines.add(line17);
        Slot20Line line18 = new Slot20Line("line18", 1, 0, 2, 1, 2, 2, 2, 3, 1, 4);
        this.lines.add(line18);
        Slot20Line line19 = new Slot20Line("line19", 2, 0, 2, 1, 1, 2, 0, 3, 0, 4);
        this.lines.add(line19);
        Slot20Line line20 = new Slot20Line("line20", 0, 0, 0, 1, 1, 2, 2, 3, 2, 4);
        this.lines.add(line20);
    }

    public List<Slot20Line> list() {
        return this.lines;
    }

    public Slot20Line get(int index) {
        return this.lines.get(index);
    }

    public void renew() {
    }
}

