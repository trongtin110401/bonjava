/*
 * Decompiled with CFR 0.144.
 */
package game.models.cache;



import game.models.minigame.taixiu.XepHangRLTLModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TopRLTLModel
implements Serializable {
    private static final long serialVersionUID = 1L;
    private List<XepHangRLTLModel> results = new ArrayList<XepHangRLTLModel>();

    public List<XepHangRLTLModel> getResults() {
        return this.results;
    }

    public void setResults(List<XepHangRLTLModel> results) {
        this.results = results;
    }
}

