package com.example.test;

import com.google.firebase.database.IgnoreExtraProperties;

import java.security.Timestamp;

@IgnoreExtraProperties
public class Transaction {
    private int trnsId;
    private int trnsTransactorId;
    private Timestamp trnsDate;
    private int trnsInventory;
    private int trnsQtyIssued;

    public Transaction(){

    }


    public int getTrnsId() { return trnsId; }
    public void setTrnsId(int trnsId) { this.trnsId = trnsId; }

    public int getTrnsTransactorId() { return trnsTransactorId; }
    public void setTrnsTransactorId(int trnsTransactorId) { this.trnsTransactorId = trnsTransactorId; }

    public Timestamp getTrnsDate() { return trnsDate; }
    public void setTrnsDate(Timestamp trnsDate) { this.trnsDate = trnsDate; }

    public int getTrnsInventory() { return trnsInventory; }
    public void setTrnsInventory(int trnsInventory) { this.trnsInventory = trnsInventory; }

    public int getTrnsQtyIssued() { return trnsQtyIssued; }
    public void setTrnsQtyIssued(int trnsQtyIssued) { this.trnsQtyIssued = trnsQtyIssued; }
}
