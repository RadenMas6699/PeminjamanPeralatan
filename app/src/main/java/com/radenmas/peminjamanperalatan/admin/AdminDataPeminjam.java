package com.radenmas.peminjamanperalatan.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.airbnb.lottie.LottieAnimationView;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.radenmas.peminjamanperalatan.CircleTransform;
import com.radenmas.peminjamanperalatan.FirebaseViewHolder;
import com.radenmas.peminjamanperalatan.R;
import com.radenmas.peminjamanperalatan.SetEmptyState;
import com.radenmas.peminjamanperalatan.adapter.DataRecycler;
import com.squareup.picasso.Picasso;

public class AdminDataPeminjam extends AppCompatActivity {
    private ShimmerFrameLayout shimmerDataPeminjam;
    private FirebaseRecyclerOptions<DataRecycler> options;
    private FirebaseRecyclerAdapter<DataRecycler, FirebaseViewHolder> adapter;

    String key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.adm_act_data_peminjam);

        DatabaseReference dbPinjam = FirebaseDatabase.getInstance().getReference("ListPinjam");
        dbPinjam.keepSynced(true);

        RecyclerView rvDataPeminjam = findViewById(R.id.rvDataPeminjam);
        LottieAnimationView lottieEmpty = findViewById(R.id.lottieEmpty);
        shimmerDataPeminjam = findViewById(R.id.shimmer_data_peminjam);
        shimmerDataPeminjam.startShimmer();

        rvDataPeminjam.setHasFixedSize(true);
        rvDataPeminjam.setLayoutManager(new LinearLayoutManager(AdminDataPeminjam.this));

        options = new FirebaseRecyclerOptions.Builder<DataRecycler>().setQuery(dbPinjam, DataRecycler.class).build();

        adapter = new FirebaseRecyclerAdapter<DataRecycler, FirebaseViewHolder>(options) {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            protected void onBindViewHolder(@NonNull FirebaseViewHolder firebaseViewHolder, int i, @NonNull DataRecycler dataRecycler) {
                shimmerDataPeminjam.setVisibility(View.INVISIBLE);
                firebaseViewHolder.name.setText(dataRecycler.getName());
                firebaseViewHolder.jumlah.setText(dataRecycler.getKelompok());
                Picasso.get().load(dataRecycler.getImg_profil()).error(R.drawable.ic_user_circle_black).transform(new CircleTransform()).into(firebaseViewHolder.img_user);
                firebaseViewHolder.listItem.setOnClickListener(view -> {
                    Intent intent = new Intent(AdminDataPeminjam.this, AdminDetailPinjaman.class);
                    intent.putExtra("uidUser", dataRecycler.getId());
                    startActivity(intent);
                });
            }

            @NonNull
            @Override
            public FirebaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new FirebaseViewHolder(LayoutInflater.from(AdminDataPeminjam.this).inflate(R.layout.list_user, parent, false));
            }
        };

        SetEmptyState setEmptyState = new SetEmptyState();
        setEmptyState.emptyState(dbPinjam, shimmerDataPeminjam, lottieEmpty);


        rvDataPeminjam.setAdapter(adapter);

    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    public void Back(View view) {
        onBackPressed();
    }
}