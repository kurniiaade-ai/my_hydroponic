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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

        // MENGAMBIL UID USER YANG SEDANG LOGIN
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Sesi login berakhir, silakan login ulang.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        String uid = currentUser.getUid();

        // MENGARAHKAN DATABASE KE NODE UID USER TERSEBUT
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Tanaman").child(uid);

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

        selectedTimestampMulai = calendar.getTimeInMillis();
        updateLabelTanggal();

        ArrayList<String> listNamaPreset = new ArrayList<>(presetMap.keySet());
        ArrayAdapter<String> adapterNama = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, listNamaPreset);
        spNamaTanaman.setAdapter(adapterNama);

        spNamaTanaman.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedPlant = listNamaPreset.get(position);
                TanamanPreset preset = presetMap.get(selectedPlant);
                if (preset != null) {
                    tvTargetHari.setText(preset.targetHariPanen + " Hari");
                    tvPpmIdeal.setText(preset.ppmIdeal + " PPM");
                    updateTahapOtomatis();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        DatePickerDialog.OnDateSetListener dateSetListener = (view, year, month, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            selectedTimestampMulai = calendar.getTimeInMillis();
            updateLabelTanggal();
            updateTahapOtomatis();
        };

        cardTanggal.setOnClickListener(v -> new DatePickerDialog(TambahTanamanActivity.this, dateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)).show());

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
        if (spNamaTanaman.getSelectedItem() == null) {
            Toast.makeText(this, "Pilih jenis tanaman terlebih dahulu!", Toast.LENGTH_SHORT).show();
            return;
        }

        String nama = spNamaTanaman.getSelectedItem().toString();
        TanamanPreset preset = presetMap.get(nama);

        if (preset == null) {
            Toast.makeText(this, "Data preset tidak ditemukan!", Toast.LENGTH_SHORT).show();
            return;
        }

        long currentTimestamp = System.currentTimeMillis();
        long diffMillis = currentTimestamp - selectedTimestampMulai;
        int hariKe = (int) (diffMillis / (1000 * 60 * 60 * 24)) + 1;

        if (hariKe < 1) hariKe = 1;

        String tahap;
        if (hariKe <= preset.batasSemai) {
            tahap = "Semai / Adaptasi";
        } else if (hariKe <= preset.batasVegetatif) {
            tahap = "Vegetatif";
        } else {
            tahap = "Siap Panen";
        }

        Calendar panenCal = Calendar.getInstance();
        panenCal.setTimeInMillis(selectedTimestampMulai);
        panenCal.add(Calendar.DAY_OF_YEAR, preset.targetHariPanen);
        long timestampPanen = panenCal.getTimeInMillis();

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