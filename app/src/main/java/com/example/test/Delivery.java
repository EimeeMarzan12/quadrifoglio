package com.example.test;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.Date;

@IgnoreExtraProperties
public class Delivery {
    private int dlvId;
    private int dlvReceptorId;
    private Date dlvReception;
    private Date dlvCourier;
    private int dlvSource;
    private String dlvDestination;

    public Delivery(){

    }

    // Getters and Setters
    public int getDlvId() { return dlvId; }
    public void setDlvId(int dlvId) { this.dlvId = dlvId; }

    public int getDlvReceptorId() { return dlvReceptorId; }
    public void setDlvReceptorId(int dlvReceptorId) { this.dlvReceptorId = dlvReceptorId; }

    public Date getDlvReception() { return dlvReception; }
    public void setDlvReception(Date dlvReception) { this.dlvReception = dlvReception; }

    public Date getDlvCourier() { return dlvCourier; }
    public void setDlvCourier(Date dlvCourier) { this.dlvCourier = dlvCourier; }

    public int getDlvSource() { return dlvSource; }
    public void setDlvSource(int dlvSource) { this.dlvSource = dlvSource; }

    public String getDlvDestination() { return dlvDestination; }
    public void setDlvDestination(String dlvDestination) { this.dlvDestination = dlvDestination; }
}