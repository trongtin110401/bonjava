// 
// Decompiled by Procyon v0.5.36
// 

package game.binh.server.logic.ai;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class BinhSuit
{
    private List<BinhGroup> listGroup;
    
    public BinhSuit(final String[] lines) {
        this.listGroup = new LinkedList<BinhGroup>();
        for (int i = 0; i < lines.length; ++i) {
            final String line = lines[i];
            final BinhGroup group = new BinhGroup(line);
            this.listGroup.add(group);
        }
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < this.listGroup.size(); ++i) {
            final BinhGroup g = this.listGroup.get(i);
            sb.append(g).append("\n");
        }
        return sb.toString();
    }
    
    public boolean canJackpot() {
        for (final BinhGroup g : this.listGroup) {
            if (g.getScore() != 1005) {
                continue;
            }
            return true;
        }
        return false;
    }
    
    public int getMaxScore() {
        int max = 0;
        for (final BinhGroup g : this.listGroup) {
            if (g.getScore() <= max) {
                continue;
            }
            max = g.getScore();
        }
        return max;
    }
    
    public int getMinScore() {
        int min = Integer.MAX_VALUE;
        for (final BinhGroup g : this.listGroup) {
            if (g.getScore() >= min) {
                continue;
            }
            min = g.getScore();
        }
        return min;
    }
    
    public int getAverageScore() {
        int sum = 0;
        for (final BinhGroup g : this.listGroup) {
            sum += g.getScore();
        }
        return sum / 4;
    }
    
    public List<BinhGroup> getListGroup() {
        return this.listGroup;
    }
}
