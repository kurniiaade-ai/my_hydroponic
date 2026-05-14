package com.delan.myhydroponic;

public class Tanaman {
    private String nama, status;
    private double ph, ec;
    private int hari, temp, progres;

    public Tanaman() {} // Wajib untuk Firebase

    public String getNama() { return nama; }
    public String getStatus() { return status; }
    public int getHari() { return hari; }
    public double getPh() { return ph; }
    public double getEc() { return ec; }
    public int getTemp() { return temp; }
    public int getProgres() { return progres; }
}