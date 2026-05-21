package com.delan.myhydroponic;

import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

public class MainActivity extends AppCompatActivity {

    // Deklarasi variabel menu navigasi baru
    private LinearLayout navDashboard, navTanaman, navMonitoring, navLog;
    private ImageView imgDashboard, imgTanaman, imgMonitoring, imgLog;
    private TextView txtDashboard, txtTanaman, txtMonitoring, txtLog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Binding ID Layout Komponen
        navDashboard = findViewById(R.id.navDashboard);
        navTanaman = findViewById(R.id.navTanaman);
        navMonitoring = findViewById(R.id.navMonitoring); // Mengganti Profil
        navLog = findViewById(R.id.navLog);

        imgDashboard = findViewById(R.id.imgDashboard);
        imgTanaman = findViewById(R.id.imgTanaman);
        imgMonitoring = findViewById(R.id.imgMonitoring); // Mengganti Profil
        imgLog = findViewById(R.id.imgLog);

        txtDashboard = findViewById(R.id.txtDashboard);
        txtTanaman = findViewById(R.id.txtTanaman);
        txtMonitoring = findViewById(R.id.txtMonitoring); // Mengganti Profil
        txtLog = findViewById(R.id.txtLog);

        // Halaman Utama Default saat aplikasi dibuka
        if (savedInstanceState == null) {
            pilihMenu("DASHBOARD");
        }

        // Action Click Listener
        navDashboard.setOnClickListener(v -> pilihMenu("DASHBOARD"));
        navTanaman.setOnClickListener(v -> pilihMenu("TANAMAN"));
        navMonitoring.setOnClickListener(v -> pilihMenu("MONITORING"));
        navLog.setOnClickListener(v -> pilihMenu("LOG"));
    }

    /**
     * Fungsi Router Navigasi & Highlight Warna Komponen Menu Aktif
     */
    private void pilihMenu(String menu) {
        // Reset seluruh warna menu menjadi default (Abu-abu)
        resetWarnaMenu();

        Fragment selectedFragment = null;
        int warnaAktif = ContextCompat.getColor(this, R.color.glow_temp);

        switch (menu) {
            case "DASHBOARD":
                imgDashboard.setColorFilter(warnaAktif);
                txtDashboard.setTextColor(warnaAktif);
                txtDashboard.setTypeface(null, Typeface.BOLD);
                selectedFragment = new DashboardFragment();
                break;
            case "TANAMAN":
                imgTanaman.setColorFilter(warnaAktif);
                txtTanaman.setTextColor(warnaAktif);
                txtTanaman.setTypeface(null, Typeface.BOLD);
                selectedFragment = new TanamanFragment();
                break;
            case "MONITORING":
                imgMonitoring.setColorFilter(warnaAktif);
                txtMonitoring.setTextColor(warnaAktif);
                txtMonitoring.setTypeface(null, Typeface.BOLD);
                // selectedFragment = new MonitoringFragment(); // Lepas komentar jika fragment sudah dibuat
                Toast.makeText(this, "Halaman Monitoring sedang disiapkan", Toast.LENGTH_SHORT).show();
                break;
            case "LOG":
                imgLog.setColorFilter(warnaAktif);
                txtLog.setTextColor(warnaAktif);
                txtLog.setTypeface(null, Typeface.BOLD);
                // selectedFragment = new LogFragment(); // Lepas komentar jika fragment sudah dibuat
                Toast.makeText(this, "Halaman Log belum tersedia", Toast.LENGTH_SHORT).show();
                break;
        }

        // Eksekusi pemuatan fragmen halaman
        if (selectedFragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, selectedFragment)
                    .commit();
        }
    }

    /**
     * Setel ulang status visual warna menu ke kondisi pasif (Abu-abu)
     */
    private void resetWarnaMenu() {
        int warnaDefault = ContextCompat.getColor(this, R.color.text_gray);

        imgDashboard.setColorFilter(warnaDefault);
        imgTanaman.setColorFilter(warnaDefault);
        imgMonitoring.setColorFilter(warnaDefault);
        imgLog.setColorFilter(warnaDefault);

        txtDashboard.setTextColor(warnaDefault);
        txtTanaman.setTextColor(warnaDefault);
        txtMonitoring.setTextColor(warnaDefault);
        txtLog.setTextColor(warnaDefault);

        txtDashboard.setTypeface(null, Typeface.NORMAL);
        txtTanaman.setTypeface(null, Typeface.NORMAL);
        txtMonitoring.setTypeface(null, Typeface.NORMAL);
        txtLog.setTypeface(null, Typeface.NORMAL);
    }

    /**
     * Fungsi Akses Luar (Triggered dari DashboardFragment tombol 'Lihat Semua')
     */
    public void pindahKeFragmentTanaman() {
        pilihMenu("TANAMAN");
    }
}