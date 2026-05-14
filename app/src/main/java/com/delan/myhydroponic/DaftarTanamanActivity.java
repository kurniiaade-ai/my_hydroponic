package com.delan.myhydroponic;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DaftarTanamanActivity extends AppCompatActivity {

    private RecyclerView rvTanaman;
    private DatabaseReference mDatabase;
    private FirebaseRecyclerAdapter<Tanaman, TanamanViewHolder> adapter;
    private LinearLayout navDashboard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daftar_tanaman);

        // --- TEMA STATUS BAR (SAMA DENGAN MAINACTIVITY ANDA) ---
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.bg_light));
        window.setNavigationBarColor(Color.WHITE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
        }

        // Binding Navigasi untuk kembali ke Dashboard
        navDashboard = findViewById(R.id.navDashboard);
        navDashboard.setOnClickListener(v -> finish()); // Menutup activity ini untuk kembali ke Main

        // Inisialisasi Firebase (Node: Tanaman)
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Tanaman");

        rvTanaman = findViewById(R.id.rvTanaman);
        rvTanaman.setLayoutManager(new LinearLayoutManager(this));

        setupAdapter();
    }

    private void setupAdapter() {
        FirebaseRecyclerOptions<Tanaman> options =
                new FirebaseRecyclerOptions.Builder<Tanaman>()
                        .setQuery(mDatabase, Tanaman.class)
                        .build();

        adapter = new FirebaseRecyclerAdapter<Tanaman, TanamanViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull TanamanViewHolder holder, int position, @NonNull Tanaman model) {
                holder.tvNama.setText(model.getNama());
                holder.tvHari.setText("Hari ke- " + model.getHari());
                holder.tvStatus.setText("Tahap: " + model.getStatus());
                holder.pbProgres.setProgress(model.getProgres());
                holder.tvParam.setText("pH: " + model.getPh() + " | EC: " + model.getEc() + " | Temp: " + model.getTemp() + "°C");
            }

            @NonNull
            @Override
            public TanamanViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tanaman, parent, false);
                return new TanamanViewHolder(view);
            }
        };
        rvTanaman.setAdapter(adapter);
    }

    // ViewHolder sebagai Inner Class
    public static class TanamanViewHolder extends RecyclerView.ViewHolder {
        TextView tvNama, tvHari, tvStatus, tvParam;
        ProgressBar pbProgres;

        public TanamanViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNama = itemView.findViewById(R.id.tvNamaTanaman);
            tvHari = itemView.findViewById(R.id.tvHari);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvParam = itemView.findViewById(R.id.tvParameter);
            pbProgres = itemView.findViewById(R.id.pbKesehatan);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (adapter != null) adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (adapter != null) adapter.stopListening();
    }
}