// 
// Decompiled by Procyon v0.5.36
// 

package game.binh.server.logic;

import java.util.Vector;
import java.util.Iterator;
import java.util.Collection;
import java.util.Collections;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Comparator;

public class GroupCard {
    private static final String[] group_names;
    public static final int FULL_SIZE = 13;
    public static final int FIRST_SIZE = 5;
    public static final int SECOND_SIZE = 3;
    public static final int SANH_RONG = 0;
    public static final int DONG_MAU_MUOI_BA = 1;
    public static final int DONG_MAU_MUOI_HAI = 2;
    public static final int BA_THUNG = 3;
    public static final int BASANH = 4;
    public static final int LUC_PHE_BON = 5;
    public static final int EM_NORMAL = 6;
    public static final int EM_BINHLUNG = 7;
    public static final int EG_THUNGPHASANH = 8;
    public static final int EG_TUQUY = 9;
    public static final int EG_CULU = 10;
    public static final int EG_THUNG = 11;
    public static final int EG_SANH = 12;
    public static final int EG_SAMCHI = 13;
    public static final int EG_2DOIKHACNHAU = 14;
    public static final int EG_MOTDOI = 15;
    public static final int EG_RAC = 16;
    public static final int NO_GROUP = 17;
    public static final int NONE = 18;
    private GroupCard baSanh;
    public static final Comparator<Card> TANG_TU_HAI;
    public static final Comparator<Card> TANG_TU_AT;
    public static final Comparator<Card> GIAM;
    public int BO;
    public List<Card> cards;

    public GroupCard() {
        this.baSanh = null;
        this.BO = 18;
        this.cards = new LinkedList<Card>();
    }

    public GroupCard(final String data) {
        this.baSanh = null;
        this.BO = 18;
        this.cards = new LinkedList<Card>();
        final String[] s = data.split(":");
        if (s.length == 2) {
            this.cards = new ArrayList<Card>();
            final String d = s[1];
            final StringBuilder sb = new StringBuilder();
            final char[] c = new char[3];
            int k = 0;
            for (int i = 0; i < d.length() - 1; ++i) {
                c[k] = d.charAt(i);
                if (c[k] == 'r' || c[k] == 'c' || c[k] == 't' || c[k] == 'b') {
                    sb.setLength(0);
                    for (int j = 0; j < k; ++j) {
                        sb.append(c[j]);
                    }
                    int so = 0;
                    final String phanSo = sb.toString();
                    so = (phanSo.equalsIgnoreCase("A") ? 14 : (phanSo.equalsIgnoreCase("J") ? 11 : (phanSo.equalsIgnoreCase("Q") ? 12 : (phanSo.equalsIgnoreCase("K") ? 13 : Integer.parseInt(phanSo)))));
                    int phanChat = 0;
                    if (c[k] == 'r') {
                        phanChat = 2;
                    }
                    if (c[k] == 'c') {
                        phanChat = 3;
                    }
                    if (c[k] == 't') {
                        phanChat = 0;
                    }
                    if (c[k] == 'b') {
                        phanChat = 1;
                    }
                    final Card card = Card.createCard(so, phanChat);
                    this.cards.add(card);
                    k = 0;
                } else {
                    ++k;
                }
            }
        }
    }

    public GroupCard(final int[] ids) {
        this.baSanh = null;
        this.BO = 18;
        this.cards = new LinkedList<Card>();
        for (final int id : ids) {
            final Card card = Card.createCard(id);
            this.cards.add(card);
        }
    }

    public GroupCard(final byte[] ids) {
        this.baSanh = null;
        this.BO = 18;
        this.cards = new LinkedList<Card>();
        for (final byte id : ids) {
            final Card card = Card.createCard(id);
            this.cards.add(card);
        }
    }

    public void xepTangTuHai() {
        Collections.sort(this.cards, GroupCard.TANG_TU_HAI);
    }

    public void xepTangTuAt() {
        Collections.sort(this.cards, GroupCard.TANG_TU_AT);
    }

    public void xepGiam() {
        Collections.sort(this.cards, GroupCard.GIAM);
    }

    public void xepBo() {
        final int size = this.cards.size();
        if (size != 5 && size != 3 && size != 13) {
            return;
        }
        if (this.BO == 0) {
            this.xepTangTuHai();
        }
        if (this.BO == 12 || this.BO == 8) {
            this.xepSanh();
        }
        if (this.BO == 9) {
            this.xepTuQuy();
        } else if (this.BO == 10) {
            this.xepCuLu();
        } else if (this.BO == 13) {
            this.xepSam();
        } else if (this.BO == 14 || this.BO == 15) {
            this.xepThuDoi();
        }
    }

    private void xepSanh() {
        this.xepTangTuHai();
        final Card c1 = this.cards.get(this.cards.size() - 1);
        final Card c2 = this.cards.get(this.cards.size() - 2);
        if (!c2.nextTo(c1)) {
            this.xepTangTuAt();
        }
    }

    public void xepTuQuy() {
        Collections.sort(this.cards, GroupCard.TANG_TU_HAI);
        final Card c1 = this.cards.get(0);
        final Card c2 = this.cards.get(1);
        if (c1.SO != c2.SO) {
            this.cards.remove(0);
            this.cards.add(c1);
        }
    }

    public void xepCuLu() {
        final Card c1 = this.cards.get(1);
        final Card c2 = this.cards.get(2);
        if (c1.SO != c2.SO) {
            this.xepTangTuHai();
        }
    }

    public void swap(final int i, final int j) {
        Collections.swap(this.cards, i, j);
    }

    public void xepSam() {
        int from = 0;
        for (int i = 0; i < this.cards.size() - 1; ++i) {
            final Card c1 = this.cards.get(i);
            final Card c2 = this.cards.get(i + 1);
            if (c1.SO == c2.SO) {
                from = i;
                break;
            }
        }
        if (from == 1) {
            this.swap(0, 3);
            this.swap(0, 1);
            this.swap(1, 2);
        }
        if (from == 2) {
            this.swap(0, 3);
            this.swap(1, 4);
            this.swap(0, 2);
            this.swap(1, 2);
        }
    }

    public void xepThuDoi() {
        final ArrayList<Card> list = new ArrayList<Card>();
        for (int i = 0; i < this.cards.size() - 1; ++i) {
            final Card card1 = this.cards.get(i);
            final Card card2 = this.cards.get(i + 1);
            if (card1.SO == card2.SO) {
                list.add(card1);
                list.add(card2);
            }
        }
        for (int i = 0; i < this.cards.size(); ++i) {
            final Card c = this.cards.get(i);
            if (!list.contains(c)) {
                list.add(c);
            }
        }
        this.cards.clear();
        this.cards.addAll(list);
    }

    public void kiemTraBoNoSort(final int rule) {
        final GroupCard gc = this.copy();
        gc.kiemtraBo(rule);
        this.BO = gc.BO;
    }

    public int kiemTraBoMauBinhBaThungChiBa() {
        if (this.thung()) {
            return 11;
        }
        return 17;
    }

    public int kiemTraBoMauBinhBaSanhChiBa() {
        if (this.sanh()) {
            return 12;
        }
        return 17;
    }

    public int kiemtraBo(final int rule) {
        final int size = this.cards.size();
        if (size != 13 && size != 5 && size != 3) {
            this.xepGiam();
            return this.BO = 17;
        }
        if (this.BO != 18 && this.BO != 17) {
            return this.BO;
        }
        this.xepGiam();
        if (size == 13) {
            this.BO = (this.sanhRong() ? 0 : ((this.dongMauMuoiBa() && rule == 1) ? 1 : ((this.dongMauMuoiHai() && rule == 1) ? 2 : (this.baThung() ? 3 : (this.sauDoi() ? 5 : (this.baSanh() ? 4 : 6))))));
        } else if (size == 5) {
            final int bo = this.doiThuSamCuluTuQuy();
            this.BO = ((bo != 18) ? bo : (this.thung() ? (this.sanh() ? 8 : 11) : (this.sanh() ? 12 : 16)));
        } else if (size == 3) {
            final int bo = this.doiThuSamCuluTuQuy();
            this.BO = ((bo != 18) ? bo : 16);
        }
        this.xepBo();
        return this.BO;
    }

    public boolean sanhRong() {
        return this.cards.size() == 13 && this.sanh();
    }

    public int demLaMauDo() {
        int count = 0;
        for (int i = 0; i < this.cards.size(); ++i) {
            final Card c = this.cards.get(i);
            if (c.isRed()) {
                ++count;
            }
        }
        return count;
    }

    public boolean dongMauMuoiBa() {
        return this.demLaMauDo() == 13 || this.demLaMauDo() == 0;
    }

    public boolean dongMauMuoiHai() {
        return this.demLaMauDo() == 12 || this.demLaMauDo() == 1;
    }

    public boolean baThung() {
        if (this.cards.size() != 13) {
            return false;
        }
        final int[] chat = new int[4];
        for (int i = 0; i < this.cards.size(); ++i) {
            final Card c = this.cards.get(i);
            final int[] arrn = chat;
            final int n = c.CHAT;
            ++arrn[n];
        }
        boolean flag = false;
        for (int j = 0; j < chat.length; ++j) {
            if (chat[j] != 0 && (chat[j] != 3 || flag) && chat[j] != 5 && chat[j] != 8 && chat[j] != 10) {
                return false;
            }
            if (chat[j] == 3) {
                flag = true;
            }
        }
        return true;
    }

    public boolean sauDoi() {
        if (this.cards.size() != 13) {
            return false;
        }
        int count = 0;
        int doi = 0;
        Card prev = null;
        for (int i = 0; i < this.cards.size(); ++i) {
            final Card c = this.cards.get(i);
            if (prev != null && c.SO == prev.SO) {
                if (doi == 0 || doi == 2) {
                    ++count;
                }
                ++doi;
            } else {
                doi = 0;
            }
            prev = c;
        }
        return count == 6;
    }

    public boolean baSanh() {
        if (this.cards.size() != 13) {
            return false;
        }
        this.baSanh = BinhRule.timBaCaiSanh(this);
        return this.baSanh != null;
    }

    public int doiThuSamCuluTuQuy() {
        int count = 1;
        int max1 = 1;
        int max2 = 0;
        Card prev = null;
        for (int size = this.cards.size(), i = 0; i < size; ++i) {
            final Card c = this.cards.get(i);
            if (prev != null) {
                if (prev.SO == c.SO) {
                    ++count;
                }
                if (i == size - 1 || prev.SO != c.SO) {
                    if (count >= max1) {
                        max2 = max1;
                        max1 = count;
                    } else if (count >= max2) {
                        max2 = count;
                    }
                    if (prev.SO != c.SO && i != size - 1) {
                        count = 1;
                    }
                }
            }
            prev = c;
        }
        if (max1 == 4) {
            return 9;
        }
        if (max1 == 3 && max2 == 2) {
            return 10;
        }
        if (max1 == 3) {
            return 13;
        }
        if (max1 == 2 && max2 == 2) {
            return 14;
        }
        if (max1 == 2) {
            return 15;
        }
        return 18;
    }

    public boolean sam() {
        return false;
    }

    public boolean thu() {
        return false;
    }

    public boolean tuquy() {
        return false;
    }

    public boolean sanh() {
        Card prev = null;
        Card firstCard = null;
        for (int i = this.cards.size() - 1; i >= 0; --i) {
            final Card c = this.cards.get(i);
            if (prev == null) {
                prev = c;
                firstCard = c;
            } else {
                if (!prev.nextTo(c)) {
                    return i == 0 && c.nextTo(firstCard);
                }
                prev = c;
            }
        }
        return true;
    }

    public boolean thung() {
        Card prev = null;
        for (int i = 0; i < this.cards.size(); ++i) {
            final Card c = this.cards.get(i);
            if (prev != null && !prev.dongChat(c)) {
                return false;
            }
            prev = c;
        }
        return true;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("#");
        sb.append(GroupCard.group_names[this.BO]).append(":");
        for (final Card c : this.cards) {
            sb.append(c.name);
        }
        sb.append("$");
        return sb.toString();
    }

    public GroupCard getGroup(final int chi) {
        GroupCard gc = null;
        if (this.hasThungPhaSanh()) {
            gc = this.getThungPhaSanh();
            this.RemoveGroupCards(gc);
        } else if (this.hasTuQuy() != -1) {
            final int n = this.hasTuQuy();
            gc = this.getCardsSameNumber(n);
            this.RemoveGroupCards(gc);
            if (this.GetNumOfCards() > 1) {
                final GroupCard gCards = this.copy();
                if (gCards.hasCulu() && gCards.GetNumOfCards() > 5) {
                    gCards.RemoveGroupCards(gCards.getCulu());
                }
                if (gCards.hasThung() && gCards.GetNumOfCards() > 5) {
                    gCards.RemoveGroupCards(gCards.getThung());
                }
                if (gCards.hasSanh() && gCards.GetNumOfCards() > 5) {
                    gCards.RemoveGroupCards(gCards.getSanh());
                }
                if (gCards.hasXamChi() && gCards.GetNumOfCards() > 3) {
                    gCards.RemoveGroupCards(gCards.getXamChi());
                }
                if (gCards.hasThu() && gCards.GetNumOfCards() > 4) {
                    gCards.RemoveGroupCards(gCards.getThu());
                }
                if (gCards.hasPair() && gCards.GetNumOfCards() > 2) {
                    gCards.RemoveGroupCards(gCards.getPair());
                }
                gc.AddCard(gCards.Cards().get(0));
                this.RemoveCard(gCards.Cards().get(0));
            }
        } else if (this.hasCulu()) {
            gc = this.getCulu();
            this.RemoveGroupCards(gc);
        } else if (this.hasThung()) {
            gc = this.getThung();
            this.RemoveGroupCards(gc);
        } else if (this.hasSanh()) {
            gc = this.getSanh();
            this.RemoveGroupCards(gc);
        } else if (this.hasXamChi()) {
            gc = this.getXamChi();
            this.RemoveGroupCards(gc);
            if (this.GetNumOfCards() >= 2) {
                final GroupCard gc2 = this.copy();
                for (int j = 0; j < 2; ++j) {
                    if (gc2.hasThu()) {
                        gc2.RemoveGroupCards(gc2.getThu());
                    } else if (gc2.hasPair()) {
                        gc2.RemoveGroupCards(gc2.getPair());
                    }
                }
                for (int m = (gc2.GetNumOfCards() < 5 - gc.GetNumOfCards()) ? gc2.GetNumOfCards() : (5 - gc.GetNumOfCards()), j = 0; j < m; ++j) {
                    gc.AddCard(gc2.Cards().get(0));
                    this.RemoveCard(gc2.Cards().get(0));
                    gc2.RemoveCard(gc2.Cards().get(0));
                }
            }
        } else if (this.hasThu()) {
            gc = this.getThu();
            this.RemoveGroupCards(gc);
            if (this.GetNumOfCards() > 0) {
                final GroupCard gc3 = this.copy();
                if (gc3.hasPair()) {
                    gc3.RemoveGroupCards(gc3.getPair());
                }
                gc.AddCard(gc3.Cards().get(0));
                this.RemoveCard(gc3.Cards().get(0));
            }
        } else if (this.hasPair()) {
            gc = this.getPair();
            this.RemoveGroupCards(gc);
            for (int i = (this.GetNumOfCards() < 5 - gc.GetNumOfCards()) ? this.GetNumOfCards() : (5 - gc.GetNumOfCards()), k = 0; k < i; ++k) {
                gc.AddCard(this.cards.get(0));
                this.RemoveCard(this.cards.get(0));
            }
        } else {
            gc = new GroupCard();
            this.DecreaseSort();
            for (int l = 0; l < this.GetNumOfCards() && l < 5; ++l) {
                gc.AddCard(this.cards.get(l));
            }
            this.RemoveGroupCards(gc);
        }
        return gc;
    }

    private boolean hasPair() {
        final int[] num = new int[15];
        for (int i = 0; i < 15; ++i) {
            num[i] = 0;
        }
        for (int i = 0; i < this.GetNumOfCards(); ++i) {
            final int[] arrn = num;
            final int n = this.cards.get(i).GetNumber();
            ++arrn[n];
        }
        for (int i = 0; i < 15; ++i) {
            if (num[i] >= 2) {
                return true;
            }
        }
        return false;
    }

    private GroupCard getPair() {
        final int[] num = new int[15];
        for (int i = 0; i < 15; ++i) {
            num[i] = 0;
        }
        for (int i = 0; i < this.GetNumOfCards(); ++i) {
            final int[] arrn = num;
            final int n = this.cards.get(i).GetNumber();
            ++arrn[n];
        }
        for (int i = 14; i >= 0; --i) {
            if (num[i] >= 2) {
                final GroupCard gc = new GroupCard();
                for (int j = 0; j < this.GetNumOfCards(); ++j) {
                    if (this.cards.get(j).GetNumber() == i) {
                        gc.AddCard(this.cards.get(j));
                    }
                    if (gc.GetNumOfCards() == 2) {
                        return gc;
                    }
                }
            }
        }
        return null;
    }

    private boolean hasThu() {
        final int[] num = new int[15];
        for (int i = 0; i < 15; ++i) {
            num[i] = 0;
        }
        for (int i = 0; i < this.GetNumOfCards(); ++i) {
            final int[] arrn = num;
            final int n = this.cards.get(i).GetNumber();
            ++arrn[n];
        }
        int k = 0;
        for (int i = 0; i < 15; ++i) {
            if (num[i] >= 2) {
                ++k;
            }
        }
        return k >= 2;
    }

    private GroupCard getThu() {
        final int[] num = new int[15];
        for (int i = 0; i < 15; ++i) {
            num[i] = 0;
        }
        for (int i = 0; i < this.GetNumOfCards(); ++i) {
            final int[] arrn = num;
            final int n = this.cards.get(i).GetNumber();
            ++arrn[n];
        }
        int k = 0;
        final GroupCard gc = new GroupCard();
        for (int i = 14; i >= 0; --i) {
            if (num[i] >= 2) {
                for (int j = 0; j < this.GetNumOfCards(); ++j) {
                    if (this.cards.get(j).GetNumber() == i) {
                        gc.AddCard(this.cards.get(j));
                    }
                    if (gc.GetNumOfCards() == 4) {
                        break;
                    }
                }
                ++k;
            }
            if (k >= 2) {
                return gc;
            }
        }
        return null;
    }

    private boolean hasXamChi() {
        final int[] num = new int[15];
        for (int i = 0; i < 15; ++i) {
            num[i] = 0;
        }
        for (int i = 0; i < this.GetNumOfCards(); ++i) {
            final int[] arrn = num;
            final int n = this.cards.get(i).GetNumber();
            ++arrn[n];
        }
        for (int i = 0; i < 15; ++i) {
            if (num[i] >= 3) {
                return true;
            }
        }
        return false;
    }

    private GroupCard getXamChi() {
        final int[] num = new int[15];
        for (int i = 0; i < 15; ++i) {
            num[i] = 0;
        }
        for (int i = 0; i < this.GetNumOfCards(); ++i) {
            final int[] arrn = num;
            final int n = this.cards.get(i).GetNumber();
            ++arrn[n];
        }
        for (int i = 14; i >= 0; --i) {
            if (num[i] >= 3) {
                final GroupCard gc = new GroupCard();
                for (int j = 0; j < this.GetNumOfCards(); ++j) {
                    if (this.cards.get(j).GetNumber() == i) {
                        gc.AddCard(this.cards.get(j));
                    }
                    if (gc.GetNumOfCards() == 3) {
                        break;
                    }
                }
                return gc;
            }
        }
        return null;
    }

    private boolean hasSanh() {
        if (this.GetNumOfCards() < 5) {
            return false;
        }
        this.IncreaseSort();
        for (int i = 0; i < this.GetNumOfCards() - 4; ++i) {
            int index = 1;
            final int num = this.cards.get(i).GetNumber();
            if (this.cards.get(i).GetNumber() == 2 && this.cards.get(this.GetNumOfCards() - 1).GetNumber() == 14) {
                for (int j = i + 1; j < this.GetNumOfCards(); ++j) {
                    final int num2 = this.cards.get(j).GetNumber();
                    if (num + index == num2) {
                        ++index;
                    }
                }
                if (index >= 4) {
                    return true;
                }
            } else {
                for (int j = i + 1; j < this.GetNumOfCards(); ++j) {
                    if (num + index == this.cards.get(j).GetNumber()) {
                        ++index;
                    }
                }
                if (index >= 5) {
                    return true;
                }
            }
        }
        return false;
    }

    private GroupCard getSanh() {
        if (this.GetNumOfCards() < 5) {
            return null;
        }
        this.IncreaseSort();
        for (int i = 0; i < this.GetNumOfCards() - 4; ++i) {
            int index = 1;
            final int num = this.cards.get(i).GetNumber();
            final GroupCard gc = new GroupCard();
            gc.AddCard(this.cards.get(i));
            if (this.cards.get(i).GetNumber() == 2 && this.cards.get(this.GetNumOfCards() - 1).GetNumber() == 14) {
                gc.AddCard(this.cards.get(this.GetNumOfCards() - 1));
                for (int j = i + 1; j < this.GetNumOfCards(); ++j) {
                    final int num2 = this.cards.get(j).GetNumber();
                    if (num + index == num2) {
                        ++index;
                        gc.AddCard(this.cards.get(j));
                        if (gc.GetNumOfCards() == 5) {
                            break;
                        }
                    }
                }
                if (gc.GetNumOfCards() == 5) {
                    return gc;
                }
            } else {
                for (int j = i + 1; j < this.GetNumOfCards(); ++j) {
                    if (num + index == this.cards.get(j).GetNumber()) {
                        ++index;
                        gc.AddCard(this.cards.get(j));
                        if (gc.GetNumOfCards() == 5) {
                            break;
                        }
                    }
                }
                if (gc.GetNumOfCards() == 5) {
                    return gc;
                }
            }
        }
        return null;
    }

    private boolean hasThung() {
        if (this.GetNumOfCards() < 5) {
            return false;
        }
        for (int i = 3; i >= 0; --i) {
            if (this.getNumCardSuit(i) >= 5) {
                return true;
            }
        }
        return false;
    }

    private GroupCard getThung() {
        if (this.GetNumOfCards() < 5) {
            return null;
        }
        final GroupCard result = new GroupCard();
        for (int i = 3; i >= 0; --i) {
            if (this.getNumCardSuit(i) >= 5) {
                final GroupCard gc = this.getCardsSameSuit(i);
                gc.DecreaseSort();
                for (int j = 0; j < gc.GetNumOfCards() && j < 5; ++j) {
                    result.AddCard(gc.Cards().get(j));
                }
                return result;
            }
        }
        return null;
    }

    private GroupCard getCardsSameSuit(final int suit) {
        final GroupCard gc = new GroupCard();
        for (int i = 0; i < this.GetNumOfCards(); ++i) {
            if (this.cards.get(i).GetSuit() == suit) {
                gc.AddCard(this.cards.get(i));
            }
        }
        return gc;
    }

    private boolean hasCulu() {
        if (this.GetNumOfCards() < 5) {
            return false;
        }
        final int[] num = new int[15];
        for (int i = 0; i < 15; ++i) {
            num[i] = 0;
        }
        for (int i = 0; i < this.GetNumOfCards(); ++i) {
            final int n = this.cards.get(i).GetNumber();
            final int[] arrn = num;
            final int n2 = n;
            ++arrn[n2];
        }
        int k = 0;
        for (int i = 14; i >= 0; --i) {
            if (num[i] >= 3) {
                ++k;
                num[i] = 0;
                break;
            }
        }
        for (int i = 14; i >= 0; --i) {
            if (num[i] >= 2) {
                ++k;
                num[i] = 0;
                break;
            }
        }
        return k >= 2;
    }

    private GroupCard getCulu() {
        if (this.GetNumOfCards() < 5) {
            return null;
        }
        final GroupCard Result = new GroupCard();
        final int[] num = new int[15];
        for (int i = 0; i < 15; ++i) {
            num[i] = 0;
        }
        for (int i = 0; i < this.GetNumOfCards(); ++i) {
            final int n = this.cards.get(i).GetNumber();
            final int[] arrn = num;
            final int n2 = n;
            ++arrn[n2];
        }
        int k = 0;
        for (int i = 14; i >= 0; --i) {
            if (num[i] >= 3) {
                ++k;
                num[i] = 0;
                int count = 0;
                for (int j = 0; j < this.GetNumOfCards(); ++j) {
                    if (this.cards.get(j).GetNumber() == i) {
                        Result.AddCard(this.cards.get(j));
                        if (++count == 3) {
                            break;
                        }
                    }
                }
                break;
            }
        }
        for (int i = 0; i < 15; ++i) {
            if (num[i] >= 2) {
                ++k;
                num[i] = 0;
                int count = 0;
                for (int j = 0; j < this.GetNumOfCards(); ++j) {
                    if (this.cards.get(j).GetNumber() == i) {
                        Result.AddCard(this.cards.get(j));
                        if (++count == 2) {
                            break;
                        }
                    }
                }
                break;
            }
        }
        if (k == 2) {
            return Result;
        }
        return null;
    }

    private boolean hasThungPhaSanh() {
        if (this.GetNumOfCards() < 5) {
            return false;
        }
        for (int i = 3; i >= 0; --i) {
            final int nSuit = this.getNumCardSuit(i);
            if (nSuit >= 5) {
                final GroupCard gc = this.getGroupCardBySuit(i);
                gc.IncreaseSort();
                for (int z = 0; z < gc.GetNumOfCards(); ++z) {
                    int index = 1;
                    int k = 1;
                    final int num = gc.Cards().get(z).GetNumber();
                    if (gc.Cards().get(z).GetNumber() == 2 && gc.Cards().get(gc.GetNumOfCards() - 1).GetNumber() == 14) {
                        for (int j = 1 + z; j < gc.GetNumOfCards() - 1 && k < 4; ++j) {
                            final int num2 = gc.Cards().get(j).GetNumber();
                            if (num + index == num2) {
                                ++k;
                                ++index;
                            }
                        }
                        if (k >= 4) {
                            return true;
                        }
                    } else {
                        for (int j = 1 + z; j < gc.GetNumOfCards() && k < 5; ++j) {
                            final int num2 = gc.Cards().get(j).GetNumber();
                            if (num + index == num2) {
                                ++k;
                                ++index;
                            }
                        }
                        if (k >= 5) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public synchronized void IncreaseSort() {
        this.xepTangTuHai();
    }

    public synchronized void DecreaseSort() {
        this.xepGiam();
    }

    private GroupCard getThungPhaSanh() {
        if (this.GetNumOfCards() < 5) {
            return null;
        }
        for (int i = 3; i >= 0; --i) {
            final int nSuit = this.getNumCardSuit(i);
            if (nSuit >= 5) {
                final GroupCard gc = this.getGroupCardBySuit(i);
                gc.IncreaseSort();
                for (int z = 0; z < gc.GetNumOfCards(); ++z) {
                    final GroupCard Result = new GroupCard();
                    int index = 1;
                    int k = 1;
                    final int num = gc.Cards().get(z).GetNumber();
                    Result.AddCard(gc.Cards().get(z));
                    if (gc.Cards().get(z).GetNumber() == 2) {
                        for (int j = 1 + z; j < gc.GetNumOfCards() && k < 5; ++j) {
                            if (num + index == gc.Cards().get(j).GetNumber() || gc.Cards().get(j).GetNumber() == 14) {
                                Result.AddCard(gc.Cards().get(j));
                                ++k;
                                ++index;
                            }
                        }
                        if (k == 5) {
                            return Result;
                        }
                    } else {
                        for (int j = 1 + z; j < gc.GetNumOfCards() && k < 5; ++j) {
                            if (num + index == gc.Cards().get(j).GetNumber()) {
                                Result.AddCard(gc.Cards().get(j));
                                ++k;
                                ++index;
                            }
                        }
                        if (k == 5) {
                            return Result;
                        }
                    }
                }
            }
        }
        return null;
    }

    private GroupCard getGroupCardBySuit(final int suit) {
        final GroupCard gc = new GroupCard();
        for (int i = 0; i < this.GetNumOfCards(); ++i) {
            if (this.cards.get(i).GetSuit() == suit) {
                gc.AddCard(this.cards.get(i));
            }
        }
        return gc;
    }

    public synchronized int GetNumOfCards() {
        return this.cards.size();
    }

    public int getNumCardSuit(final int suit) {
        int nCount = 0;
        for (int i = 0; i < this.GetNumOfCards(); ++i) {
            if (this.cards.get(i).GetSuit() == suit) {
                ++nCount;
            }
        }
        return nCount;
    }

    private void RemoveGroupCards(final GroupCard gc) {
        for (int i = 0; i < gc.GetNumOfCards(); ++i) {
            if (this.searchCard(gc.Cards().get(i))) {
                this.RemoveCard(gc.Cards().get(i));
            }
        }
    }

    public synchronized void RemoveCard(final Card card) {
        if (this.cards.size() == 0) {
            return;
        }
        for (int i = 0; i < this.cards.size(); ++i) {
            if (this.cards.get(i).ID == card.ID) {
                this.cards.remove(i);
            }
        }
    }

    private boolean searchCard(final Card c) {
        for (int i = 0; i < this.GetNumOfCards(); ++i) {
            if (this.cards.get(i).ID == c.ID) {
                return true;
            }
        }
        return false;
    }

    private int hasTuQuy() {
        if (this.GetNumOfCards() < 4) {
            return -1;
        }
        final int[] num = new int[15];
        for (int i = 14; i >= 0; --i) {
            num[i] = 0;
        }
        for (int i = 0; i < this.GetNumOfCards(); ++i) {
            final int n = this.cards.get(i).GetNumber();
            final int[] arrn = num;
            final int n2 = n;
            ++arrn[n2];
            if (num[n] == 4) {
                return n;
            }
        }
        return -1;
    }

    private GroupCard getCardsSameNumber(final int num) {
        final GroupCard Result = new GroupCard();
        for (int i = 0; i < this.GetNumOfCards(); ++i) {
            if (this.cards.get(i).GetNumber() == num) {
                Result.AddCard(this.cards.get(i));
            }
        }
        return Result;
    }

    public GroupCard copy() {
        final GroupCard gc = new GroupCard();
        for (int i = 0; i < this.GetNumOfCards(); ++i) {
            final Card c = Card.createCard(this.cards.get(i));
            gc.AddCard(c);
        }
        return gc;
    }

    public synchronized void AddCard(final Card card) {
        this.cards.add(card);
    }

    public List<Card> Cards() {
        return this.cards;
    }

    public byte[] toByteArray() {
        final List<Card> list = this.cards;
        synchronized (list) {
            final byte[] c = new byte[this.cards.size()];
            for (int i = 0; i < this.cards.size(); ++i) {
                c[i] = (byte) this.cards.get(i).ID;
            }
            return c;
        }
    }

    public void sortLucPheBon() {
        if (this.cards.size() != 13) {
            return;
        }
        final Vector<ArrayList<Card>> bo = new Vector<ArrayList<Card>>(13);
        for (int i2 = 0; i2 < 13; ++i2) {
            bo.add(i2, null);
        }
        int count = 0;
        for (int i3 = 0; i3 < this.cards.size(); ++i3) {
            final Card c = this.cards.get(i3);
            ArrayList<Card> bai = bo.get(c.SO - 2);
            if (bai == null) {
                bai = new ArrayList<Card>();
                bo.set(c.SO - 2, bai);
                ++count;
            }
            bai.add(c);
        }
        List tuquy1 = null;
        List tuquy2 = null;
        List tuquy3 = null;
        List sam = null;
        final ArrayList doi = new ArrayList();
        int tq = 0;
        Card le = null;
        for (int j = 0; j < bo.size(); ++j) {
            final List bai2 = bo.get(j);
            if (bai2 != null) {
                if (bai2.size() == 1) {
                    le = (Card) bai2.get(0);
                } else if (bai2.size() == 2) {
                    doi.add(bai2.get(0));
                    doi.add(bai2.get(1));
                } else if (bai2.size() == 3) {
                    sam = bai2;
                } else if (tq == 0) {
                    tuquy1 = bai2;
                    ++tq;
                } else if (tq == 1) {
                    tuquy2 = bai2;
                    ++tq;
                } else if (tq == 2) {
                    tuquy3 = bai2;
                }
            }
        }
        for (int j = 0; j < 13; ++j) {
            this.cards.set(j, null);
        }
        if (sam != null) {
            this.cards.set(10, (Card) sam.get(0));
            this.cards.set(11, (Card) sam.get(1));
            this.cards.set(4, (Card) sam.get(2));
        }
        if (tuquy1 != null) {
            this.cards.set(0, (Card) tuquy1.get(0));
            this.cards.set(1, (Card) tuquy1.get(1));
            this.cards.set(5, (Card) tuquy1.get(2));
            this.cards.set(6, (Card) tuquy1.get(3));
        }
        if (tuquy2 != null) {
            this.cards.set(2, (Card) tuquy2.get(0));
            this.cards.set(3, (Card) tuquy2.get(1));
            this.cards.set(7, (Card) tuquy2.get(2));
            this.cards.set(8, (Card) tuquy2.get(3));
        }
        if (tuquy3 != null) {
            this.cards.set(10, (Card) tuquy3.get(0));
            this.cards.set(11, (Card) tuquy3.get(1));
            this.cards.set(9, (Card) tuquy3.get(2));
            this.cards.set(4, (Card) tuquy3.get(3));
        }
        if (le != null) {
            this.cards.set(12, le);
        }
        if (doi != null) {
            for (int j = 0; j < doi.size(); ++j) {
                final Card c2 = (Card) doi.get(j);
                for (int k = this.cards.size() - 1; k >= 0; --k) {
                    if (this.cards.get(k) == null) {
                        this.cards.set(k, c2);
                        break;
                    }
                }
            }
        }
    }

    public void sortBaSanh() {
        this.cards.clear();
        if (this.baSanh != null) {
            this.cards.addAll(this.baSanh.cards);
        }
    }

    public void sortBaThung() {
        final ArrayList<Card> bich = new ArrayList<Card>();
        final ArrayList<Card> tep = new ArrayList<Card>();
        final ArrayList<Card> ro = new ArrayList<Card>();
        final ArrayList<Card> co = new ArrayList<Card>();
        for (int i = 0; i < this.cards.size(); ++i) {
            final Card c = this.cards.get(i);
            if (c.CHAT == 0) {
                bich.add(c);
            } else if (c.CHAT == 1) {
                tep.add(c);
            } else if (c.CHAT == 2) {
                ro.add(c);
            } else if (c.CHAT == 3) {
                co.add(c);
            }
        }
        for (int i = 0; i < this.cards.size(); ++i) {
            this.cards.set(i, null);
        }
        this.chenBaCaiThung(bich);
        this.chenBaCaiThung(tep);
        this.chenBaCaiThung(ro);
        this.chenBaCaiThung(co);
    }

    private void chenBaCaiThung(final List<Card> listCard) {
        if (listCard.size() == 3) {
            final int from = 10;
            for (int i = 0; i < listCard.size(); ++i) {
                this.cards.set(from + i, listCard.get(i));
            }
        }
        if (listCard.size() == 5) {
            int from = 0;
            if (this.cards.get(0) != null) {
                from = 5;
            }
            for (int i = 0; i < listCard.size(); ++i) {
                this.cards.set(i + from, listCard.get(i));
            }
        }
        if (listCard.size() == 8) {
            int from2 = 0;
            if (this.cards.get(3) != null) {
                from2 = 5;
            }
            for (int i = 0; i < 5; ++i) {
                this.cards.set(i + from2, listCard.get(i));
            }
            final int from3 = 10;
            for (int i2 = 0; i2 < 3; ++i2) {
                this.cards.set(from3 + i2, listCard.get(5 + i2));
            }
        }
        if (listCard.size() == 10) {
            for (int i3 = 0; i3 < listCard.size(); ++i3) {
                this.cards.set(i3, listCard.get(i3));
            }
        }
    }

    public void reset() {
        this.cards.clear();
        this.BO = 18;
    }

    public int GetMaxNumber() {
        int max = -1;
        for (int i = 0; i < this.cards.size(); ++i) {
            final Card c = this.cards.get(i);
            if (c.SO > max) {
                max = c.SO;
            }
        }
        return max;
    }

    public int GetMaxNumberByBo(final int bo, final int rule) {
        if (bo == 8) {
            if (rule == 0) {
                return this.cards.get(this.cards.size() - 1).SO;
            }
            return this.GetMaxNumber();
        } else {
            if (bo == 12) {
                return this.cards.get(this.cards.size() - 1).SO;
            }
            return this.cards.get(0).SO;
        }
    }

    public Card GetMaxCard() {
        int max = -1;
        Card maxCard = null;
        for (int i = 0; i < this.cards.size(); ++i) {
            final Card c = this.cards.get(i);
            if (c.SO > max) {
                max = c.SO;
                maxCard = c;
            }
        }
        return maxCard;
    }

    public int GetSecondMaxNumber() {
        int max1 = -1;
        int max2 = -1;
        for (int i = 0; i < this.cards.size(); ++i) {
            final Card c = this.cards.get(i);
            if (c.SO > max1) {
                max2 = max1;
                max1 = c.SO;
            } else if (c.SO > max2) {
                max2 = c.SO;
            }
        }
        return max2;
    }

    public boolean hasA() {
        return this.demSoAt() > 0;
    }

    public int demSoAt() {
        int count = 0;
        for (int i = 0; i < this.cards.size(); ++i) {
            final Card c = this.cards.get(i);
            if (c.SO == 14) {
                ++count;
            }
        }
        return count;
    }

    public boolean coTuQuyAt() {
        return this.demSoAt() == 4;
    }

    public boolean isNoHu(final int rule) {
        return this.kiemtraBo(rule) == 0;
    }

    @Override
    public int hashCode() {
        int product = 1;
        for (final Card c : this.cards) {
            product *= c.ID;
        }
        return product;
    }

    @Override
    public boolean equals(final Object o) {
        if (o instanceof GroupCard) {
            final GroupCard gc = (GroupCard) o;
            for (int i = 0; i < this.cards.size(); ++i) {
                final Card c = this.cards.get(i);
                if (!gc.cards.contains(c)) {
                    return false;
                }
            }
            for (int i = 0; i < gc.cards.size(); ++i) {
                final Card c = gc.cards.get(i);
                if (!this.cards.contains(c)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    static {
        group_names = new String[]{"SR", "MB", "MH", "BT", "BS", "LPB", "NM", "BL", "TPS", "TQ", "CL", "TH", "S", "X", "HD", "D", "R", "NO", "NULL"};
        TANG_TU_HAI = new Comparator<Card>() {
            @Override
            public int compare(final Card c1, final Card c2) {
                return c1.compareTo(c2);
            }
        };
        TANG_TU_AT = new Comparator<Card>() {
            @Override
            public int compare(final Card c1, final Card c2) {
                return c1.soSanhAtNhoNhat(c2);
            }
        };
        GIAM = new Comparator<Card>() {
            @Override
            public int compare(final Card c1, final Card c2) {
                return c2.compareTo(c1);
            }
        };
    }
}
