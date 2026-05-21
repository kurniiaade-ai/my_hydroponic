package com.delan.myhydroponic;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class TanamanFragment extends Fragment {

    private RecyclerView rvTanaman;
    private DatabaseReference mDatabase;
    private FloatingActionButton fabAdd;
    private EditText etSearchTanaman;

    private TanamanAdapter adapter;
    private List<TanamanItem> listTanamanAsli = new ArrayList<>();
    private List<TanamanItem> listTanamanFilter = new ArrayList<>();
    private ValueEventListener dbListener;

    class TanamanItem {
        String key;
        Tanaman tanaman;
        TanamanItem(String key, Tanaman tanaman) {
            this.key = key;
            this.tanaman = tanaman;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_daftar_tanaman, container, false);

        rvTanaman = view.findViewById(R.id.rvTanaman);
        etSearchTanaman = view.findViewById(R.id.etSearchTanaman);
        fabAdd = view.findViewById(R.id.fabAdd);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setItemPrefetchEnabled(false);
        rvTanaman.setLayoutManager(layoutManager);

        adapter = new TanamanAdapter();
        rvTanaman.setAdapter(adapter);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid();
            mDatabase = FirebaseDatabase.getInstance().getReference().child("Tanaman").child(uid);
            muatDataDariFirebase();
        } else {
            Toast.makeText(getContext(), "Sesi login tidak valid", Toast.LENGTH_SHORT).show();
        }

        fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), TambahTanamanActivity.class);
            startActivity(intent);
        });

        etSearchTanaman.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String searchText = s.toString().trim().toLowerCase();
                cariDataTanaman(searchText);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        etSearchTanaman.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
                etSearchTanaman.clearFocus();
                return true;
            }
            return false;
        });

        return view;
    }

    private void muatDataDariFirebase() {
        dbListener = mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listTanamanAsli.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Tanaman t = ds.getValue(Tanaman.class);
                    if (t != null) {
                        listTanamanAsli.add(new TanamanItem(ds.getKey(), t));
                    }
                }
                String currentSearch = etSearchTanaman.getText().toString().trim().toLowerCase();
                cariDataTanaman(currentSearch);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Gagal memuat data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cariDataTanaman(String searchText) {
        listTanamanFilter.clear();
        if (searchText.isEmpty()) {
            listTanamanFilter.addAll(listTanamanAsli);
        } else {
            for (TanamanItem item : listTanamanAsli) {
                if (item.tanaman.getNama() != null && item.tanaman.getNama().toLowerCase().contains(searchText)) {
                    listTanamanFilter.add(item);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    private int getGambarTanaman(String namaTanaman) {
        if (namaTanaman == null) return R.drawable.logo_app;
        switch (namaTanaman.toLowerCase()) {
            case "kangkung": return R.drawable.img_kangkung;
            case "bayam hijau": return R.drawable.img_bayam_hijau;
            default: return R.drawable.logo_app;
        }
    }

    private class TanamanAdapter extends RecyclerView.Adapter<FullTanamanViewHolder> {

        @NonNull
        @Override
        public FullTanamanViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tanaman, parent, false);
            return new FullTanamanViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull FullTanamanViewHolder holder, int position) {
            TanamanItem item = listTanamanFilter.get(position);
            Tanaman model = item.tanaman;
            String postKey = item.key;

            if (holder.tvNama != null) {
                holder.tvNama.setText(model.getNama());
            }

            int imageRes = getGambarTanaman(model.getNama());
            if (holder.imgTanaman != null) {
                holder.imgTanaman.setImageResource(imageRes);
            }

            long waktuSekarang = System.currentTimeMillis();
            long waktuMulai = model.getTimestampMulai();
            long umurHari = model.getHari();

            if (waktuMulai > 0) {
                umurHari = ((waktuSekarang - waktuMulai) / (1000 * 60 * 60 * 24)) + 1;
                if (umurHari < 1) umurHari = 1;
            }

            if (holder.tvHari != null) {
                holder.tvHari.setText("Hari ke- " + umurHari + " / " + model.getTargetHari());
            }

            if (holder.tvStatus != null) {
                holder.tvStatus.setText("Tahap: " + (model.getStatus() != null ? model.getStatus() : "Menunggu"));
            }

            // HITUNG DAN ATUR KEMAJUAN PROGRESS BAR SECARA DINAMIS
            if (holder.pbPertumbuhan != null) {
                int targetHari = model.getTargetHari();
                if (targetHari > 0) {
                    int progresPersen = (int) ((umurHari * 100) / targetHari);
                    if (progresPersen > 100) progresPersen = 100; // Dikunci max 100%
                    holder.pbPertumbuhan.setProgress(progresPersen);
                } else {
                    holder.pbPertumbuhan.setProgress(0);
                }
            }

            if (holder.tvParam != null) {
                holder.tvParam.setText("Target: " + model.getPpm() + " PPM | Temp: " + model.getTemp() + "°C");
            }

            holder.itemView.setOnLongClickListener(v -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Hapus Tanaman");
                builder.setMessage("Apakah Anda yakin ingin menghapus '" + model.getNama() + "' dari daftar?");

                builder.setPositiveButton("Hapus", (dialog, which) -> {
                    if (mDatabase != null && postKey != null) {
                        mDatabase.child(postKey).removeValue()
                                .addOnSuccessListener(aVoid -> Toast.makeText(getContext(), "Tanaman berhasil dihapus", Toast.LENGTH_SHORT).show())
                                .addOnFailureListener(e -> Toast.makeText(getContext(), "Gagal menghapus: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                    }
                });

                builder.setNegativeButton("Batal", (dialog, which) -> dialog.dismiss());
                builder.create().show();
                return true;
            });
        }

        @Override
        public int getItemCount() {
            return listTanamanFilter.size();
        }
    }

    public static class FullTanamanViewHolder extends RecyclerView.ViewHolder {
        TextView tvNama, tvHari, tvStatus, tvParam;
        ProgressBar pbPertumbuhan; // Variabel baru
        ImageView imgTanaman;

        public FullTanamanViewHolder(@NonNull View itemView) {
            super(itemView);
            imgTanaman = itemView.findViewById(R.id.imgTanaman);
            tvNama = itemView.findViewById(R.id.tvNamaTanaman);
            tvHari = itemView.findViewById(R.id.tvHari);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvParam = itemView.findViewById(R.id.tvParameter);
            pbPertumbuhan = itemView.findViewById(R.id.pbPertumbuhan); // Binding ID baru
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mDatabase != null && dbListener != null) {
            mDatabase.removeEventListener(dbListener);
        }
    }
}