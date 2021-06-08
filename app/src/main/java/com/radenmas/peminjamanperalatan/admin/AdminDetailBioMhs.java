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
import android.widget.TextView;

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

public class AdminDetailBioMhs extends AppCompatActivity {
    private FirebaseRecyclerOptions<DataRecycler> options;
    private FirebaseRecyclerAdapter<DataRecycler, FirebaseViewHolder> adapterbio;
    String nameKelas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.adm_act_detail_bio_mhs);

        nameKelas = getIntent().getExtras().getString("nameKelas");

        TextView tvKelas = findViewById(R.id.tvKelas);
        RecyclerView rvUserMhs = findViewById(R.id.rvUserBioMhs);
        LottieAnimationView lottieEmpty = findViewById(R.id.lottieEmpty);
        ShimmerFrameLayout shimmerDataMhs = findViewById(R.id.shimmer_data_mhs);
        shimmerDataMhs.startShimmer();

        DatabaseReference reffUser = FirebaseDatabase.getInstance().getReference().child(nameKelas);
        reffUser.keepSynced(true);

        rvUserMhs.setHasFixedSize(true);
        rvUserMhs.setLayoutManager(new LinearLayoutManager(AdminDetailBioMhs.this));

        tvKelas.setText(nameKelas);

        options = new FirebaseRecyclerOptions.Builder<DataRecycler>().setQuery(reffUser, DataRecycler.class).build();

        adapterbio = new FirebaseRecyclerAdapter<DataRecycler, FirebaseViewHolder>(options) {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            protected void onBindViewHolder(@NonNull FirebaseViewHolder firebaseViewHolder, int i, @NonNull DataRecycler dataRecycler) {
                firebaseViewHolder.name.setText(dataRecycler.getName());
                firebaseViewHolder.jumlah.setText(dataRecycler.getNim());
                Picasso.get().load(dataRecycler.getImg_profil()).error(R.drawable.ic_user_circle_black).transform(new CircleTransform()).into(firebaseViewHolder.img_user);
                firebaseViewHolder.listItem.setOnClickListener(view -> {
                    Intent intent = new Intent(AdminDetailBioMhs.this, AdminPreviewDataMhs.class);
                    intent.putExtra("uidUser", dataRecycler.getId());
                    startActivity(intent);
                });
            }

            @NonNull
            @Override
            public FirebaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new FirebaseViewHolder(LayoutInflater.from(AdminDetailBioMhs.this).inflate(R.layout.list_user, parent, false));
            }
        };

        SetEmptyState setEmptyState = new SetEmptyState();
        setEmptyState.emptyState(reffUser, shimmerDataMhs, lottieEmpty);

        rvUserMhs.setAdapter(adapterbio);
    }

    @Override
    public void onStart() {
        super.onStart();
        adapterbio.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapterbio.stopListening();
    }

    public void Back(View view) {
        onBackPressed();
    }
}