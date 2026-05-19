package com.delan.myhydroponic;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

public class MainActivity extends AppCompatActivity {

    private LinearLayout navDashboard, navTanaman, navLog, navProfil;
    private ImageView imgDashboard, imgTanaman, imgLog, imgProfil;
    private TextView txtDashboard, txtTanaman, txtLog, txtProfil;

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
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
        }

        // Binding Komponen Navigasi
        navDashboard = findViewById(R.id.navDashboard);
        navTanaman = findViewById(R.id.navTanaman);
        navLog = findViewById(R.id.navLog);
        navProfil = findViewById(R.id.navProfil);

        imgDashboard = findViewById(R.id.imgDashboard);
        imgTanaman = findViewById(R.id.imgTanaman);
        imgLog = findViewById(R.id.imgLog);
        imgProfil = findViewById(R.id.imgProfil);

        txtDashboard = findViewById(R.id.txtDashboard);
        txtTanaman = findViewById(R.id.txtTanaman);
        txtLog = findViewById(R.id.txtLog);
        txtProfil = findViewById(R.id.txtProfil);

        // Klik Menu untuk ganti konten atas
        navDashboard.setOnClickListener(v -> loadFragment(new DashboardFragment(), "DASHBOARD"));
        navTanaman.setOnClickListener(v -> loadFragment(new TanamanFragment(), "TANAMAN"));
        //navLog.setOnClickListener(v -> loadFragment(new LogFragment(), "LOG"));
        //navProfil.setOnClickListener(v -> loadFragment(new ProfilFragment(), "PROFIL"));

        // Default awal saat pertama kali buka aplikasi
        if (savedInstanceState == null) {
            loadFragment(new DashboardFragment(), "DASHBOARD");
        }
    }

    private void loadFragment(Fragment fragment, String menuTag) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();

        resetNavColors();

        int activeColor = ContextCompat.getColor(this, R.color.glow_temp);
        if (menuTag.equals("DASHBOARD")) {
            imgDashboard.setImageTintList(ColorStateList.valueOf(activeColor));
            txtDashboard.setTextColor(activeColor);
        } else if (menuTag.equals("TANAMAN")) {
            imgTanaman.setImageTintList(ColorStateList.valueOf(activeColor));
            txtTanaman.setTextColor(activeColor);
        } else if (menuTag.equals("LOG")) {
            imgLog.setImageTintList(ColorStateList.valueOf(activeColor));
            txtLog.setTextColor(activeColor);
        } else if (menuTag.equals("PROFIL")) {
            imgProfil.setImageTintList(ColorStateList.valueOf(activeColor));
            txtProfil.setTextColor(activeColor);
        }
    }

    private void resetNavColors() {
        int inactiveColor = ContextCompat.getColor(this, R.color.text_gray);

        imgDashboard.setImageTintList(ColorStateList.valueOf(inactiveColor));
        txtDashboard.setTextColor(inactiveColor);
        imgTanaman.setImageTintList(ColorStateList.valueOf(inactiveColor));
        txtTanaman.setTextColor(inactiveColor);
        imgLog.setImageTintList(ColorStateList.valueOf(inactiveColor));
        txtLog.setTextColor(inactiveColor);
        imgProfil.setImageTintList(ColorStateList.valueOf(inactiveColor));
        txtProfil.setTextColor(inactiveColor);
    }

    // ViewHolder untuk list ringkas horizontal di Dashboard Fragment
    public static class DashTanamanViewHolder extends RecyclerView.ViewHolder {
        public TextView tvNama, tvDetail;
        public ImageView imgTanaman;

        public DashTanamanViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNama = itemView.findViewById(R.id.tvNamaDash);
            tvDetail = itemView.findViewById(R.id.tvDetailDash);
            imgTanaman = itemView.findViewById(R.id.imgTanamanDash);
        }
    }
}