// 
// Decompiled by Procyon v0.5.36
// 

package game.binh.server.logic;

import org.slf4j.LoggerFactory;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import org.slf4j.Logger;

public class BinhRule
{
    private static Logger logger;
    
    public static synchronized int SoSanhChi2(final GroupCard gc1, final GroupCard gc2) {
        if (gc1.kiemtraBo(1) < gc2.kiemtraBo(1)) {
            return 1;
        }
        if (gc1.kiemtraBo(1) > gc2.kiemtraBo(1)) {
            return -1;
        }
        if (gc1.kiemtraBo(1) == gc2.kiemtraBo(1)) {
            switch (gc1.kiemtraBo(1)) {
                case 8:
                case 12: {
                    int max1 = gc1.GetMaxNumber();
                    int max2 = gc2.GetMaxNumber();
                    if (max1 > max2) {
                        return 1;
                    }
                    if (max1 < max2) {
                        return -1;
                    }
                    if (max1 != max2) {
                        break;
                    }
                    if (max1 != 14) {
                        break;
                    }
                    max1 = gc1.GetSecondMaxNumber();
                    max2 = gc2.GetSecondMaxNumber();
                    if (max1 > max1) {
                        return 1;
                    }
                    if (max1 >= max2) {
                        break;
                    }
                    return -1;
                }
                case 9:
                case 10:
                case 11:
                case 13:
                case 14:
                case 15:
                case 16: {
                    int smaller = gc1.cards.size();
                    if (gc1.cards.size() > gc2.cards.size()) {
                        smaller = gc2.cards.size();
                    }
                    for (int i = 0; i < smaller; ++i) {
                        final Card c1 = gc1.cards.get(i);
                        final Card c2 = gc2.cards.get(i);
                        if (c1.SO > c2.SO) {
                            return 1;
                        }
                        if (c1.SO < c2.SO) {
                            return -1;
                        }
                    }
                    break;
                }
            }
        }
        return 0;
    }
    
    public static synchronized int SoSanhChi1(final GroupCard gc1, final GroupCard gc2) {
        if (gc1.kiemtraBo(0) < gc2.kiemtraBo(0)) {
            return 1;
        }
        if (gc1.kiemtraBo(0) > gc2.kiemtraBo(0)) {
            return -1;
        }
        if (gc1.kiemtraBo(0) == gc2.kiemtraBo(0)) {
            switch (gc1.kiemtraBo(0)) {
                case 8:
                case 12: {
                    for (int i = gc1.cards.size() - 1; i >= 0; --i) {
                        final Card c1 = gc1.cards.get(i);
                        final Card c2 = gc2.cards.get(i);
                        if (c1.SO > c2.SO) {
                            return 1;
                        }
                        if (c1.SO < c2.SO) {
                            return -1;
                        }
                    }
                }
                case 9:
                case 10:
                case 11:
                case 13:
                case 14:
                case 15:
                case 16: {
                    int smaller = gc1.cards.size();
                    if (gc1.cards.size() > gc2.cards.size()) {
                        smaller = gc2.cards.size();
                    }
                    for (int j = 0; j < smaller; ++j) {
                        final Card c3 = gc1.cards.get(j);
                        final Card c4 = gc2.cards.get(j);
                        if (c3.SO > c4.SO) {
                            return 1;
                        }
                        if (c3.SO < c4.SO) {
                            return -1;
                        }
                    }
                    break;
                }
            }
        }
        return 0;
    }
    
    public static synchronized SoSanhChi BinhChiMode1(final GroupCard gc1, final GroupCard gc2, final int chi) {
        final int kind1 = gc1.kiemtraBo(0);
        final int kind2 = gc2.kiemtraBo(0);
        final SoSanhChi sc = new SoSanhChi();
        final int kq = SoSanhChi1(gc1, gc2);
        int rate = 1;
        if (kind1 == 8 || kind2 == 8) {
            if (chi == 1) {
                rate = 10;
            }
            else if (chi == 2) {
                rate = 20;
            }
        }
        else if (kind1 == 9 || kind2 == 9) {
            rate = 8;
            if (chi == 2) {
                rate = 16;
            }
        }
        else if (kind1 == 10 || kind2 == 10) {
            if (chi == 2) {
                rate = 4;
            }
        }
        else if ((kind1 == 13 || kind2 == 13) && chi == 3) {
            rate = 6;
        }
        sc.chiCount1 = kq * rate;
        sc.chiCount2 = -1 * kq * rate;
        return sc;
    }
    
    public static synchronized SoSanhChi BinhChiMode2(final GroupCard gc1, final GroupCard gc2, final int chi) {
        final int kq = SoSanhChi2(gc1, gc2);
        final int kind1 = gc1.kiemtraBo(1);
        final int kind2 = gc2.kiemtraBo(1);
        final SoSanhChi sc = new SoSanhChi();
        int rate = 1;
        if (kind1 == 8 || kind2 == 8) {
            if (chi == 1) {
                rate = 10;
            }
            else if (chi == 2) {
                rate = 20;
            }
        }
        else if (kind1 == 9 || kind2 == 9) {
            rate = 8;
            if (chi == 2) {
                rate = 16;
            }
            if (gc1.coTuQuyAt() || gc2.coTuQuyAt()) {
                rate = 20;
            }
        }
        else if (kind1 == 10 || kind2 == 10) {
            if (chi == 2) {
                rate = 4;
            }
        }
        else if (kind1 == 13 || kind2 == 13) {
            rate = 1;
            if (chi == 3) {
                rate = 6;
                if ((gc1.hasA() && kind1 == 13) || (kind2 == 13 && gc2.hasA())) {
                    rate = 20;
                }
            }
        }
        sc.chiCount1 = kq * rate;
        sc.chiCount2 = -1 * kq * rate;
        return sc;
    }
    
    public static int demChiPhatBinhLung2(final PlayerCard pc, final int chi) {
        int sc = 0;
        GroupCard gc1 = null;
        switch (chi) {
            case 1: {
                gc1 = pc.ChiMot();
                break;
            }
            case 2: {
                gc1 = pc.ChiHai();
                break;
            }
            case 3: {
                gc1 = pc.ChiBa();
                break;
            }
        }
        final int kind1 = gc1.kiemtraBo(1);
        switch (kind1) {
            case 8: {
                if (chi == 1) {
                    sc += 10;
                }
                if (chi != 2) {
                    break;
                }
                sc += 20;
                break;
            }
            case 9: {
                if (chi == 1) {
                    sc += 8;
                }
                if (chi != 2) {
                    break;
                }
                sc += 16;
                break;
            }
            case 10: {
                if (chi == 2) {
                    sc += 4;
                    break;
                }
                ++sc;
                break;
            }
            case 13: {
                if (chi == 3) {
                    sc += 6;
                    break;
                }
                ++sc;
                break;
            }
            default: {
                ++sc;
                break;
            }
        }
        return sc;
    }
    
    public static int demChiPhatBinhLung1(final PlayerCard pc, final int chi) {
        int sc = 0;
        GroupCard gc1 = null;
        switch (chi) {
            case 1: {
                gc1 = pc.ChiMot();
                break;
            }
            case 2: {
                gc1 = pc.ChiHai();
                break;
            }
            case 3: {
                gc1 = pc.ChiBa();
                break;
            }
        }
        final int kind1 = gc1.kiemtraBo(0);
        switch (kind1) {
            case 8: {
                if (chi == 1) {
                    sc += 10;
                }
                if (chi != 2) {
                    break;
                }
                sc += 20;
                break;
            }
            case 9: {
                if (gc1.coTuQuyAt()) {
                    sc += 20;
                    break;
                }
                if (chi == 1) {
                    sc += 8;
                    break;
                }
                if (chi != 2) {
                    break;
                }
                sc += 16;
                break;
            }
            case 10: {
                if (chi == 2) {
                    sc += 4;
                    break;
                }
                ++sc;
                break;
            }
            case 13: {
                if (chi != 3) {
                    ++sc;
                    break;
                }
                if (gc1.hasA()) {
                    sc += 20;
                    break;
                }
                sc += 6;
                break;
            }
            default: {
                ++sc;
                break;
            }
        }
        return sc;
    }
    
    public static SoSanhChi BinhChiLungMode1(final PlayerCard pc1, final PlayerCard pc2, final int chi) {
        final SoSanhChi sc = new SoSanhChi();
        final int playerCardKind1 = pc1.GetPlayerCardsKind(0);
        final int playerCardKind2 = pc2.GetPlayerCardsKind(0);
        if (playerCardKind1 == 6 && playerCardKind2 == 7) {
            sc.chiCount1 = demChiPhatBinhLung1(pc1, chi);
            sc.chiCount2 = -sc.chiCount1;
        }
        if (playerCardKind1 == 7 && playerCardKind2 == 6) {
            sc.chiCount1 = -demChiPhatBinhLung1(pc2, chi);
            sc.chiCount2 = -sc.chiCount1;
        }
        return sc;
    }
    
    public static SoSanhChi BinhChiLungMode2(final PlayerCard pc1, final PlayerCard pc2, final int chi) {
        final SoSanhChi sc = new SoSanhChi();
        final int playerCardKind1 = pc1.GetPlayerCardsKind(1);
        final int playerCardKind2 = pc2.GetPlayerCardsKind(1);
        if (playerCardKind1 == 6 && playerCardKind2 == 7) {
            sc.chiCount1 = demChiPhatBinhLung2(pc1, chi);
            sc.chiCount2 = -sc.chiCount1;
        }
        if (playerCardKind1 == 7 && playerCardKind2 == 6) {
            sc.chiCount1 = -demChiPhatBinhLung2(pc2, chi);
            sc.chiCount2 = -sc.chiCount1;
        }
        return sc;
    }
    
    public static synchronized SoSanhChi BinhLungMode1(final PlayerCard pc1, final PlayerCard pc2) {
        final SoSanhChi sc = new SoSanhChi();
        final SoSanhChi sc2 = BinhChiLungMode1(pc1, pc2, 1);
        final SoSanhChi sc3 = BinhChiLungMode1(pc1, pc2, 2);
        final SoSanhChi sc4 = BinhChiLungMode1(pc1, pc2, 3);
        sc.chiCount1 = 2 * (sc2.chiCount1 + sc3.chiCount1 + sc4.chiCount1);
        sc.chiCount2 = 2 * (sc2.chiCount2 + sc3.chiCount2 + sc4.chiCount2);
        return sc;
    }
    
    public static synchronized SoSanhChi BinhLungMode2(final PlayerCard pc1, final PlayerCard pc2) {
        final SoSanhChi sc = new SoSanhChi();
        final SoSanhChi sc2 = BinhChiLungMode2(pc1, pc2, 1);
        final SoSanhChi sc3 = BinhChiLungMode2(pc1, pc2, 2);
        final SoSanhChi sc4 = BinhChiLungMode2(pc1, pc2, 3);
        sc.chiCount1 = 2 * (sc2.chiCount1 + sc3.chiCount1 + sc4.chiCount1);
        sc.chiCount2 = 2 * (sc2.chiCount2 + sc3.chiCount2 + sc4.chiCount2);
        return sc;
    }
    
    public static GroupCard timBaCaiSanh(final GroupCard gc) {
        final GroupCard baSanh = new GroupCard();
        final ArrayList<Integer> listSo = new ArrayList<Integer>();
        for (int i = 0; i < gc.cards.size(); ++i) {
            final Card c = gc.cards.get(i);
            listSo.add(c.SO);
        }
        final List<Integer> listSanh = timTatCaSanh(listSo);
        if (listSanh == null) {
            return null;
        }
        final boolean[] used = new boolean[gc.cards.size()];
        for (int j = 0; j < listSanh.size(); ++j) {
            final int x = listSanh.get(j);
            for (int k = 0; k < gc.cards.size(); ++k) {
                if (!used[k]) {
                    final Card c2 = gc.cards.get(k);
                    if (c2.SO == x) {
                        baSanh.cards.add(c2);
                        used[k] = true;
                        break;
                    }
                }
            }
        }
        return baSanh;
    }
    
    private static List<Integer> subtract(final List<Integer> parent, final List<Integer> sub) {
        final ArrayList<Integer> copy = new ArrayList<Integer>(parent.size());
        for (int i = 0; i < parent.size(); ++i) {
            final Integer cur = parent.get(i);
            copy.add(cur);
        }
        for (int i = 0; i < sub.size(); ++i) {
            final Integer x = sub.get(i);
            copy.remove(x);
        }
        return copy;
    }
    
    private static boolean checkSanh(final List<Integer> listSo) {
        int prev = -10;
        int begin = -10;
        for (int i = 0; i < listSo.size(); ++i) {
            final int cur = listSo.get(i);
            if (begin == -10) {
                begin = cur;
                prev = cur;
            }
            else {
                if (!isNextTo(begin, prev, cur)) {
                    return false;
                }
                prev = cur;
            }
        }
        return true;
    }
    
    private static List<Integer> timTatCaSanh(final List<Integer> listSo) {
        Collections.sort(listSo);
        List<Integer> result = new ArrayList<Integer>();
        for (int i = 0; i < listSo.size(); ++i) {
            final List<Integer> sanh51 = timSanh5(i, listSo);
            if (sanh51 != null) {
                result.clear();
                result.addAll(sanh51);
                final List<Integer> newList = subtract(listSo, sanh51);
                for (int j = 0; j < newList.size(); ++j) {
                    final List<Integer> sanh52 = timSanh5(j, newList);
                    if (sanh52 != null) {
                        result.addAll(sanh52);
                        final List<Integer> lastList = subtract(newList, sanh52);
                        if (checkSanh(lastList)) {
                            result.addAll(lastList);
                            return result;
                        }
                        result = subtract(result, sanh52);
                    }
                }
            }
        }
        return null;
    }
    
    private static List<Integer> timSanh5(final int from, final List<Integer> listSo) {
        final ArrayList<Integer> longest = new ArrayList<Integer>();
        final int size = listSo.size();
        int prev = -10;
        int begin = -10;
        for (int i = from; i < from + size; ++i) {
            final int index = i % size;
            final int cur = listSo.get(index);
            if (longest.size() == 0) {
                begin = cur;
                longest.add(cur);
                prev = cur;
            }
            else if (isNextTo(begin, prev, cur)) {
                longest.add(cur);
                prev = cur;
            }
            if (longest.size() == 5) {
                break;
            }
        }
        if (longest.size() == 5) {
            return longest;
        }
        return null;
    }
    
    private static boolean isNextTo(final int begin, final int prev, final int next) {
        return next == prev + 1 || (begin == 2 && next == 14) || (begin == 14 && next == 2 && prev != 2);
    }
    
    public static boolean isMauBinh(final int kind) {
        return kind >= 0 && kind <= 5;
    }
    
    public static long GetPlayerCardMauBinhRate(final int kind) {
        int rate = 1;
        switch (kind) {
            case 0: {
                rate = 72;
                break;
            }
            case 1: {
                rate = 30;
                break;
            }
            case 2: {
                rate = 24;
                break;
            }
            case 3: {
                rate = 18;
                break;
            }
            case 4: {
                rate = 18;
                break;
            }
            case 5: {
                rate = 18;
                break;
            }
        }
        return rate;
    }
    
    public static long getSoLaThangAt(final int soLaAt) {
        if (soLaAt == 0) {
            return -4L;
        }
        if (soLaAt == 2) {
            return 4L;
        }
        if (soLaAt == 3) {
            return 8L;
        }
        if (soLaAt == 4) {
            return 12L;
        }
        return 0L;
    }
    
    static {
        BinhRule.logger = LoggerFactory.getLogger("BinhLogic");
    }
}
