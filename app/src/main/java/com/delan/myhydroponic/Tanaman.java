package com.delan.myhydroponic;

public class Tanaman {
    private String nama;
    private String status;
    private int hari;
    private int progres;
    private int temp;

    // Variabel baru yang kita tambahkan
    private int ppm;
    private int targetHari;
    private long timestampMulai;
    private long timestampPanen;

    // Constructor kosong (Wajib ada agar Firebase tidak error saat menarik data)
    public Tanaman() {
    }

    // --- GETTER METHODS ---

    public String getNama() {
        return nama;
    }

    public String getStatus() {
        return status;
    }

    public int getHari() {
        return hari;
    }

    public int getProgres() {
        return progres;
    }

    public int getTemp() {
        return temp;
    }

    // Getter untuk data PPM dan Timestamp yang sebelumnya error
    public int getPpm() {
        return ppm;
    }

    public int getTargetHari() {
        return targetHari;
    }

    public long getTimestampMulai() {
        return timestampMulai;
    }

    public long getTimestampPanen() {
        return timestampPanen;
    }
}