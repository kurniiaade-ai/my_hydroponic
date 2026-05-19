package com.delan.myhydroponic;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DashboardFragment extends Fragment {

    private TextView txtWaterTemp, txtTds, txtAki;
    private MaterialCardView cardTemp, cardTds, cardAki;
    private ImageView iconTemp, iconTds, iconAki, imgProfileTop;
    private RecyclerView rvTanamanHorizontal;
    private DatabaseReference mDatabaseTanaman;
    private FirebaseRecyclerAdapter<Tanaman, MainActivity.DashTanamanViewHolder> firebaseAdapter;
    private FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        mAuth = FirebaseAuth.getInstance();

        txtWaterTemp = view.findViewById(R.id.txtWaterTemp);
        txtTds = view.findViewById(R.id.txtTds);
        txtAki = view.findViewById(R.id.txtAki);
        cardTemp = view.findViewById(R.id.cardTemp);
        cardTds = view.findViewById(R.id.cardTds);
        cardAki = view.findViewById(R.id.cardAki);
        iconTemp = view.findViewById(R.id.iconTemp);
        iconTds = view.findViewById(R.id.iconTds);
        iconAki = view.findViewById(R.id.iconAki);
        imgProfileTop = view.findViewById(R.id.imgProfileTop);

        cardTemp.setOnClickListener(v -> highlightCard("TEMP"));
        cardTds.setOnClickListener(v -> highlightCard("TDS"));
        cardAki.setOnClickListener(v -> highlightCard("AKI"));
        highlightCard("TDS");

        imgProfileTop.setOnClickListener(v -> performLogout());

        mDatabaseTanaman = FirebaseDatabase.getInstance().getReference().child("Tanaman");
        rvTanamanHorizontal = view.findViewById(R.id.rvTanamanHorizontal);
        rvTanamanHorizontal.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        setupDashboardAdapter();
        return view;
    }

    private void setupDashboardAdapter() {
        FirebaseRecyclerOptions<Tanaman> options = new FirebaseRecyclerOptions.Builder<Tanaman>()
                .setQuery(mDatabaseTanaman, Tanaman.class).build();

        firebaseAdapter = new FirebaseRecyclerAdapter<Tanaman, MainActivity.DashTanamanViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MainActivity.DashTanamanViewHolder holder, int position, @NonNull Tanaman model) {
                holder.tvNama.setText(model.getNama());
                holder.tvDetail.setText(model.getStatus() + " • Hari " + model.getHari());
            }
            @NonNull
            @Override
            public MainActivity.DashTanamanViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tanaman_horizontal, parent, false);
                return new MainActivity.DashTanamanViewHolder(view);
            }
        };
        rvTanamanHorizontal.setAdapter(firebaseAdapter);
    }

    private void highlightCard(String selectedCard) {
        resetAllCards();
        int glowColor = 0;
        MaterialCardView activeCard = null;
        ImageView activeIcon = null;

        if (selectedCard.equals("TEMP") && isAdded()) {
            activeCard = cardTemp; activeIcon = iconTemp;
            glowColor = ContextCompat.getColor(requireContext(), R.color.glow_temp);
        } else if (selectedCard.equals("TDS") && isAdded()) {
            activeCard = cardTds; activeIcon = iconTds;
            glowColor = ContextCompat.getColor(requireContext(), R.color.glow_tds);
        } else if (selectedCard.equals("AKI") && isAdded()) {
            activeCard = cardAki; activeIcon = iconAki;
            glowColor = ContextCompat.getColor(requireContext(), R.color.glow_aki);
        }

        if (activeCard != null) {
            activeCard.setStrokeWidth(5);
            activeCard.setStrokeColor(glowColor);
            activeCard.setCardElevation(20f);
            activeIcon.setImageTintList(ColorStateList.valueOf(glowColor));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                activeCard.setOutlineSpotShadowColor(glowColor);
                activeCard.setOutlineAmbientShadowColor(glowColor);
            }
        }
    }

    private void resetAllCards() {
        if (!isAdded()) return;
        int defaultIconColor = ContextCompat.getColor(requireContext(), R.color.text_gray);

        cardTemp.setStrokeWidth(0); cardTemp.setCardElevation(8f);
        iconTemp.setImageTintList(ColorStateList.valueOf(defaultIconColor));
        cardTds.setStrokeWidth(0); cardTds.setCardElevation(8f);
        iconTds.setImageTintList(ColorStateList.valueOf(defaultIconColor));
        cardAki.setStrokeWidth(0); cardAki.setCardElevation(8f);
        iconAki.setImageTintList(ColorStateList.valueOf(defaultIconColor));
    }

    private void performLogout() {
        if (mAuth != null && getActivity() != null) {
            mAuth.signOut();
            Toast.makeText(getContext(), "Logout berhasil", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            getActivity().finish();
        }
    }

    @Override
    public void onStart() { super.onStart(); if (firebaseAdapter != null) firebaseAdapter.startListening(); }

    @Override
    public void onStop() { super.onStop(); if (firebaseAdapter != null) firebaseAdapter.stopListening(); }
}