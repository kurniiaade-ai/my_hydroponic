package com.delan.myhydroponic;

import java.util.HashMap;
import java.util.Map;

public class TanamanPreset {
    public String nama;
    public int ppmIdeal;
    public int targetHariPanen;
    public int batasSemai;      // Hari terakhir fase Semai/Adaptasi
    public int batasVegetatif;  // Hari terakhir fase Vegetatif (sebelum Siap Panen)

    public TanamanPreset(String nama, int ppmIdeal, int targetHariPanen, int batasSemai, int batasVegetatif) {
        this.nama = nama;
        this.ppmIdeal = ppmIdeal;
        this.targetHariPanen = targetHariPanen;
        this.batasSemai = batasSemai;
        this.batasVegetatif = batasVegetatif;
    }

    public static Map<String, TanamanPreset> getPresets() {
        Map<String, TanamanPreset> presets = new HashMap<>();

        // Format: (Nama, PPM, Target Hari, Batas Hari Semai, Batas Hari Vegetatif)
        //presets.put("Selada", new TanamanPreset("Selada", 750, 30, 10, 25));
        //presets.put("Bayam Merah", new TanamanPreset("Bayam Merah", 1050, 25, 7, 20));
        //presets.put("Peppermint", new TanamanPreset("Peppermint", 1120, 45, 10, 35));
        //presets.put("Pakcoy", new TanamanPreset("Pakcoy", 1200, 28, 10, 24));
        //presets.put("Seledri", new TanamanPreset("Seledri", 1400, 50, 15, 40));
        //presets.put("Kailan", new TanamanPreset("Kailan", 1190, 35, 10, 28));
        presets.put("Kangkung", new TanamanPreset("Kangkung", 1150, 21, 5, 18));
        presets.put("Bayam Hijau", new TanamanPreset("Bayam Hijau", 1000, 25, 7, 20));

        return presets;
    }
}