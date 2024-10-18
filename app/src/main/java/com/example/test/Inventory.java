package com.example.test;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.Date;

@IgnoreExtraProperties
public class Inventory {
    private int invId;
    private int invMedId;
    private Date invReception;
    private Date invExpiry;
    private String invDescription;
    private int invQuantity;
    private String invQrcode;
    private float invConsumptionRate;

    public Inventory(){

    }

    // Getters and Setters
    public int getInvId() { return invId; }
    public void setInvId(int invId) { this.invId = invId; }

    public int getInvMedId() { return invMedId; }
    public void setInvMedId(int invMedId) { this.invMedId = invMedId; }

    public Date getInvReception() { return invReception; }
    public void setInvReception(Date invReception) { this.invReception = invReception; }

    public Date getInvExpiry() { return invExpiry; }
    public void setInvExpiry(Date invExpiry) { this.invExpiry = invExpiry; }

    public String getInvDescription() { return invDescription; }
    public void setInvDescription(String invDescription) { this.invDescription = invDescription; }

    public int getInvQuantity() { return invQuantity; }
    public void setInvQuantity(int invQuantity) { this.invQuantity = invQuantity; }

    public String getInvQrcode() { return invQrcode; }
    public void setInvQrcode(String invQrcode) { this.invQrcode = invQrcode; }

    public float getInvConsumptionRate() { return invConsumptionRate; }
    public void setInvConsumptionRate(float invConsumptionRate) { this.invConsumptionRate = invConsumptionRate; }
}