package model;

import java.util.ArrayList;
import java.util.List;

public class AprioriTransactionPair {

    private int pairOccuredCount;
    private List<Integer> pairList;

    public AprioriTransactionPair() {
        pairList = new ArrayList<>();
    }

    public int getPairOccuredCount() {
        return pairOccuredCount;
    }

    public void setPairOccuredCount(int pairOccuredCount) {
        this.pairOccuredCount = pairOccuredCount;
    }

    public List<Integer> getPairList() {
        return pairList;
    }

    public void setPairList(List<Integer> pairList) {
        this.pairList = pairList;
    }
    public void addAllPairList(List<Integer> pairList) {
        this.pairList.addAll(pairList);
    }

    public void addPair(int pairI, int pairJ) {
        if (pairI < pairJ) {
            getPairList().add(pairI);
            getPairList().add(pairJ);

        } else {
            getPairList().add(pairJ);
            getPairList().add(pairI);
        }
    }
}