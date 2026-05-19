package com.delan.myhydroponic;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.card.MaterialCardView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class TambahTanamanActivity extends AppCompatActivity {

    private Spinner spNamaTanaman;
    private MaterialCardView cardTanggal;
    private TextView tvTanggalTanam, tvTargetHari, tvPpmIdeal, tvTahapOtomatis;
    private Button btnSimpanTanaman;
    private ImageView btnBack;

    private DatabaseReference mDatabase;
    private Map<String, TanamanPreset> presetMap;

    private Calendar calendar;
    private long selectedTimestampMulai;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_tanaman);

        // Inisialisasi Firebase
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Tanaman");

        // Binding View
        spNamaTanaman = findViewById(R.id.spNamaTanaman);
        cardTanggal = findViewById(R.id.cardTanggal);
        tvTanggalTanam = findViewById(R.id.tvTanggalTanam);
        tvTargetHari = findViewById(R.id.tvTargetHari);
        tvPpmIdeal = findViewById(R.id.tvPpmIdeal);
        tvTahapOtomatis = findViewById(R.id.tvTahapOtomatis);
        btnSimpanTanaman = findViewById(R.id.btnSimpanTanaman);
        btnBack = findViewById(R.id.btnBack);

        presetMap = TanamanPreset.getPresets();
        calendar = Calendar.getInstance();

        // Set Default Tanggal ke Hari Ini
        selectedTimestampMulai = calendar.getTimeInMillis();
        updateLabelTanggal();

        // Setup Master Data ke Spinner
        ArrayList<String> listNamaPreset = new ArrayList<>(presetMap.keySet());
        ArrayAdapter<String> adapterNama = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, listNamaPreset);
        spNamaTanaman.setAdapter(adapterNama);

        // Listener saat Pilihan Tanaman Berubah
        spNamaTanaman.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedPlant = listNamaPreset.get(position);
                TanamanPreset preset = presetMap.get(selectedPlant);
                if (preset != null) {
                    tvTargetHari.setText(preset.targetHariPanen + " Hari");
                    tvPpmIdeal.setText(preset.ppmIdeal + " PPM");
                    updateTahapOtomatis(); // Update teks fase otomatis
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Setup DatePicker Kalender
        DatePickerDialog.OnDateSetListener dateSetListener = (view, year, month, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            selectedTimestampMulai = calendar.getTimeInMillis();
            updateLabelTanggal();
            updateTahapOtomatis(); // Update teks fase ketika tanggal berubah
        };

        cardTanggal.setOnClickListener(v -> new DatePickerDialog(TambahTanamanActivity.this, dateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)).show());

        // Listener Tombol
        btnBack.setOnClickListener(v -> finish());
        btnSimpanTanaman.setOnClickListener(v -> simpanDataKeFirebase());
    }

    private void updateLabelTanggal() {
        String format = "dd MMMM yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(format, new Locale("id", "ID"));
        tvTanggalTanam.setText(sdf.format(calendar.getTime()));
    }

    private void updateTahapOtomatis() {
        if (spNamaTanaman.getSelectedItem() == null) return;

        String nama = spNamaTanaman.getSelectedItem().toString();
        TanamanPreset preset = presetMap.get(nama);

        if (preset != null) {
            long currentTimestamp = System.currentTimeMillis();
            long diffMillis = currentTimestamp - selectedTimestampMulai;
            int hariKe = (int) (diffMillis / (1000 * 60 * 60 * 24)) + 1;

            if (hariKe < 1) hariKe = 1;

            String tahap;
            if (hariKe <= preset.batasSemai) {
                tahap = "Semai";
            } else if (hariKe <= preset.batasVegetatif) {
                tahap = "Vegetatif";
            } else {
                tahap = "Panen";
            }
            tvTahapOtomatis.setText(tahap);
        }
    }

    private void simpanDataKeFirebase() {
        // SAFETY CHECK 1: Pastikan ada tanaman yang dipilih di Spinner
        if (spNamaTanaman.getSelectedItem() == null) {
            Toast.makeText(this, "Pilih jenis tanaman terlebih dahulu!", Toast.LENGTH_SHORT).show();
            return;
        }

        String nama = spNamaTanaman.getSelectedItem().toString();
        TanamanPreset preset = presetMap.get(nama);

        // SAFETY CHECK 2: Pastikan data profil tanaman ada
        if (preset == null) {
            Toast.makeText(this, "Data profil tanaman tidak ditemukan!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Kalkulasi Umur Tanaman
        long currentTimestamp = System.currentTimeMillis();
        long diffMillis = currentTimestamp - selectedTimestampMulai;
        int hariKe = (int) (diffMillis / (1000 * 60 * 60 * 24)) + 1;

        if (hariKe < 1) hariKe = 1;

        // Logika Status Pertumbuhan
        String tahap;
        if (hariKe <= preset.batasSemai) {
            tahap = "Semai / Adaptasi";
        } else if (hariKe <= preset.batasVegetatif) {
            tahap = "Vegetatif";
        } else {
            tahap = "Siap Panen";
        }

        // Kalkulasi Timestamp Panen
        Calendar panenCal = Calendar.getInstance();
        panenCal.setTimeInMillis(selectedTimestampMulai);
        panenCal.add(Calendar.DAY_OF_YEAR, preset.targetHariPanen);
        long timestampPanen = panenCal.getTimeInMillis();

        // Push ke Firebase
        HashMap<String, Object> tanamanMap = new HashMap<>();
        tanamanMap.put("nama", nama);
        tanamanMap.put("status", tahap);
        tanamanMap.put("hari", hariKe);
        tanamanMap.put("progres", 100);
        tanamanMap.put("ppm", preset.ppmIdeal);
        tanamanMap.put("temp", 0);
        tanamanMap.put("timestampMulai", selectedTimestampMulai);
        tanamanMap.put("timestampPanen", timestampPanen);
        tanamanMap.put("targetHari", preset.targetHariPanen);

        // Mengganti tombol menjadi non-aktif sementara agar user tidak klik 2 kali
        btnSimpanTanaman.setEnabled(false);
        btnSimpanTanaman.setText("MENYIMPAN...");

        mDatabase.push().setValue(tanamanMap)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(TambahTanamanActivity.this, "Tanaman berhasil ditambahkan!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    btnSimpanTanaman.setEnabled(true);
                    btnSimpanTanaman.setText("SIMPAN TANAMAN");
                    Toast.makeText(TambahTanamanActivity.this, "Gagal menyimpan: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}