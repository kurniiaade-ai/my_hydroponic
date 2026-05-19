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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class TanamanFragment extends Fragment {

    private RecyclerView rvTanaman;
    private DatabaseReference mDatabase;
    private FirebaseRecyclerAdapter<Tanaman, FullTanamanViewHolder> adapter;
    private FloatingActionButton fabAdd;
    private EditText etSearchTanaman;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_daftar_tanaman, container, false);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Tanaman");
        rvTanaman = view.findViewById(R.id.rvTanaman);
        etSearchTanaman = view.findViewById(R.id.etSearchTanaman);

        // Mencegah Inconsistency detected pada RecyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext()) {
            @Override
            public boolean supportsPredictiveItemAnimations() {
                return false;
            }
        };
        rvTanaman.setLayoutManager(layoutManager);

        fabAdd = view.findViewById(R.id.fabAdd);
        fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), TambahTanamanActivity.class);
            startActivity(intent);
        });

        // Menjalankan setup list bawaan
        setupFullListAdapter();

        // LOGIKA PENCARIAN REAL-TIME
        etSearchTanaman.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String searchText = s.toString().trim();
                cariDataTanaman(searchText);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // LOGIKA MENUTUP KEYBOARD SAAT KLIK 'DONE'
        etSearchTanaman.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                // Sembunyikan keyboard
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
                // Hapus fokus dari form pencarian agar kursor hilang
                etSearchTanaman.clearFocus();
                return true;
            }
            return false;
        });

        return view;
    }

    private void cariDataTanaman(String searchText) {
        Query firebaseQuery;

        if (searchText.isEmpty()) {
            firebaseQuery = mDatabase;
        } else {
            String formatSearch = searchText.substring(0, 1).toUpperCase() + searchText.substring(1);
            firebaseQuery = mDatabase.orderByChild("nama").startAt(formatSearch).endAt(formatSearch + "\uf8ff");
        }

        FirebaseRecyclerOptions<Tanaman> options = new FirebaseRecyclerOptions.Builder<Tanaman>()
                .setQuery(firebaseQuery, Tanaman.class).build();

        if (adapter != null) {
            adapter.updateOptions(options);
        }
    }

    private void setupFullListAdapter() {
        FirebaseRecyclerOptions<Tanaman> options = new FirebaseRecyclerOptions.Builder<Tanaman>()
                .setQuery(mDatabase, Tanaman.class).build();

        adapter = new FirebaseRecyclerAdapter<Tanaman, FullTanamanViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull FullTanamanViewHolder holder, int position, @NonNull Tanaman model) {
                holder.tvNama.setText(model.getNama());

                long waktuSekarang = System.currentTimeMillis();
                long waktuMulai = model.getTimestampMulai();

                if (waktuMulai > 0) {
                    long umurHari = ((waktuSekarang - waktuMulai) / (1000 * 60 * 60 * 24)) + 1;
                    if (umurHari < 1) umurHari = 1;
                    holder.tvHari.setText("Hari ke- " + umurHari + " / " + model.getTargetHari());
                } else {
                    holder.tvHari.setText("Hari ke- " + model.getHari() + " / " + model.getTargetHari());
                }

                holder.tvStatus.setText("Tahap: " + (model.getStatus() != null ? model.getStatus() : "Menunggu"));
                holder.pbKesehatan.setProgress(model.getProgres());
                holder.tvParam.setText("Target: " + model.getPpm() + " PPM | Temp: " + model.getTemp() + "°C");

                // FITUR HAPUS DATA
                final String postKey = getRef(position).getKey();
                holder.itemView.setOnLongClickListener(v -> {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("Hapus Tanaman");
                    builder.setMessage("Apakah Anda yakin ingin menghapus '" + model.getNama() + "' dari daftar?");

                    builder.setPositiveButton("Hapus", (dialog, which) -> {
                        if (postKey != null) {
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

            @NonNull
            @Override
            public FullTanamanViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tanaman, parent, false);
                return new FullTanamanViewHolder(view);
            }
        };
        rvTanaman.setAdapter(adapter);
    }

    public static class FullTanamanViewHolder extends RecyclerView.ViewHolder {
        TextView tvNama, tvHari, tvStatus, tvParam;
        ProgressBar pbKesehatan;

        public FullTanamanViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNama = itemView.findViewById(R.id.tvNamaTanaman);
            tvHari = itemView.findViewById(R.id.tvHari);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvParam = itemView.findViewById(R.id.tvParameter);
            pbKesehatan = itemView.findViewById(R.id.pbKesehatan);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (adapter != null) {
            if (etSearchTanaman != null && etSearchTanaman.getText().toString().isEmpty()) {
                adapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (adapter != null) adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (adapter != null) adapter.stopListening();
    }
}