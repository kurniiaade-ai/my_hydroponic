package com.delan.myhydroponic;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    // View Components Sensor
    TextView txtWaterTemp, txtTds, txtAki;

    // Cards & Icons untuk Efek Glow
    MaterialCardView cardTemp, cardTds, cardAki;
    ImageView iconTemp, iconTds, iconAki;

    // Bottom Nav & Profile
    LinearLayout navDashboard, navTanaman, navLog, navProfil;
    ImageView imgProfileTop;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // --- SETTING STATUS BAR WHITE THEME ---
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.bg_light));
        window.setNavigationBarColor(Color.WHITE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.getDecorView().setSystemUiVisibility(android.view.View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | android.view.View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
        }

        mAuth = FirebaseAuth.getInstance();

        // Binding Sensor Texts
        txtWaterTemp = findViewById(R.id.txtWaterTemp);
        txtTds = findViewById(R.id.txtTds);
        txtAki = findViewById(R.id.txtAki);

        // Binding Cards & Icons
        cardTemp = findViewById(R.id.cardTemp);
        cardTds = findViewById(R.id.cardTds);
        cardAki = findViewById(R.id.cardAki);
        iconTemp = findViewById(R.id.iconTemp);
        iconTds = findViewById(R.id.iconTds);
        iconAki = findViewById(R.id.iconAki);

        // --- LOGIKA KLIK KARTU (GLOW EFFECT) ---
        cardTemp.setOnClickListener(v -> highlightCard("TEMP"));
        cardTds.setOnClickListener(v -> highlightCard("TDS"));
        cardAki.setOnClickListener(v -> highlightCard("AKI"));

        // Set Default Sorotan Pertama
        highlightCard("TDS"); // Misalnya TDS disorot secara default

        // Binding Navigation & Profile
        imgProfileTop = findViewById(R.id.imgProfileTop);
        navProfil = findViewById(R.id.navProfil);
        navTanaman = findViewById(R.id.navTanaman);

        // Listener untuk pindah ke DaftarTanamanActivity
        navTanaman.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, DaftarTanamanActivity.class);
            startActivity(intent);
        });

        imgProfileTop.setOnClickListener(v -> performLogout());
        navProfil.setOnClickListener(v -> performLogout());
    }

    // FUNGSI UNTUK MENGUBAH EFEK GLOW PADA CARD
    private void highlightCard(String selectedCard) {
        // 1. Reset semua kartu ke tampilan default (Abu-abu, tidak nyala)
        resetAllCards();

        // 2. Set efek menyala pada kartu yang dipilih
        int glowColor = 0;
        MaterialCardView activeCard = null;
        ImageView activeIcon = null;

        if (selectedCard.equals("TEMP")) {
            activeCard = cardTemp;
            activeIcon = iconTemp;
            glowColor = ContextCompat.getColor(this, R.color.glow_temp);
        } else if (selectedCard.equals("TDS")) {
            activeCard = cardTds;
            activeIcon = iconTds;
            glowColor = ContextCompat.getColor(this, R.color.glow_tds);
        } else if (selectedCard.equals("AKI")) {
            activeCard = cardAki;
            activeIcon = iconAki;
            glowColor = ContextCompat.getColor(this, R.color.glow_aki);
        }

        if (activeCard != null) {
            // Beri garis tepi berwarna
            activeCard.setStrokeWidth(5); // Ketebalan garis
            activeCard.setStrokeColor(glowColor);

            // Angkat kartu sedikit lebih tinggi
            activeCard.setCardElevation(20f);

            // Ubah warna ikon
            activeIcon.setImageTintList(ColorStateList.valueOf(glowColor));

            // Efek cahaya bayangan (Glow) untuk Android 9+ (Pie)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                activeCard.setOutlineSpotShadowColor(glowColor);
                activeCard.setOutlineAmbientShadowColor(glowColor);
            }
        }
    }

    private void resetAllCards() {
        int defaultIconColor = ContextCompat.getColor(this, R.color.text_gray);

        // Reset Suhu
        cardTemp.setStrokeWidth(0);
        cardTemp.setCardElevation(8f);
        iconTemp.setImageTintList(ColorStateList.valueOf(defaultIconColor));

        // Reset Nutrisi
        cardTds.setStrokeWidth(0);
        cardTds.setCardElevation(8f);
        iconTds.setImageTintList(ColorStateList.valueOf(defaultIconColor));

        // Reset Aki
        cardAki.setStrokeWidth(0);
        cardAki.setCardElevation(8f);
        iconAki.setImageTintList(ColorStateList.valueOf(defaultIconColor));

        // Kembalikan warna shadow ke default (Hitam/Abu) untuk Android 9+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            int defaultShadow = Color.BLACK;
            cardTemp.setOutlineSpotShadowColor(defaultShadow);
            cardTemp.setOutlineAmbientShadowColor(defaultShadow);
            cardTds.setOutlineSpotShadowColor(defaultShadow);
            cardTds.setOutlineAmbientShadowColor(defaultShadow);
            cardAki.setOutlineSpotShadowColor(defaultShadow);
            cardAki.setOutlineAmbientShadowColor(defaultShadow);
        }
    }

    private void performLogout() {
        if (mAuth != null) {
            mAuth.signOut();
            Toast.makeText(this, "Logout berhasil", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
    }
}