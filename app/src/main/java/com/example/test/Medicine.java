package com.example.test;

import com.google.firebase.database.IgnoreExtraProperties;

import java.sql.Blob;

@IgnoreExtraProperties
public class Medicine {
    private int medId;
    private String medName;
    private String medCategory;
    private String medDescription;
    private int medShelfLife;
    private Blob medPic;

    public Medicine(){

    }
    // Getters and Setters
    public int getMedId() { return medId; }
    public void setMedId(int medId) { this.medId = medId; }

    public String getMedName() { return medName; }
    public void setMedName(String medName) { this.medName = medName; }

    public String getMedCategory() { return medCategory; }
    public void setMedCategory(String medCategory) { this.medCategory = medCategory; }

    public String getMedDescription() { return medDescription; }
    public void setMedDescription(String medDescription) { this.medDescription = medDescription; }

    public int getMedShelfLife() { return medShelfLife; }
    public void setMedShelfLife(int medShelfLife) { this.medShelfLife = medShelfLife; }

    public Blob getMedPic() { return medPic; }
    public void setMedPic(Blob medPic) { this.medPic = medPic; }
}


