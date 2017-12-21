package model;

import model.AprioriTransactionPair;

import java.util.ArrayList;
import java.util.List;

public class AprioriPass {

    private int passNumber;
    private List<AprioriTransactionPair> aprioriTransactionPairList;

    public AprioriPass(int passNumber) {
        this.passNumber = passNumber;
        aprioriTransactionPairList = new ArrayList<>();
    }

    public int getPassNumber() {
        return passNumber;
    }

    public void setPassNumber(int passNumber) {
        this.passNumber = passNumber;
    }

    public List<AprioriTransactionPair> getAprioriTransactionPairList() {
        return aprioriTransactionPairList;
    }

    public void setAprioriTransactionPairList(List<AprioriTransactionPair> aprioriTransactionPairList) {
        this.aprioriTransactionPairList = aprioriTransactionPairList;
    }

    public void addAprioriTransactionPair(AprioriTransactionPair aprioriTransactionPair){
        getAprioriTransactionPairList().add(aprioriTransactionPair);
    }
    public void removeAprioriTransactionPair(AprioriTransactionPair aprioriTransactionPair){
        getAprioriTransactionPairList().remove(aprioriTransactionPair);
    }

}