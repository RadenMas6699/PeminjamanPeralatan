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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.radenmas.peminjamanperalatan.FirebaseViewHolder;
import com.radenmas.peminjamanperalatan.R;
import com.radenmas.peminjamanperalatan.SetEmptyState;
import com.radenmas.peminjamanperalatan.adapter.DataRecycler;

import org.jetbrains.annotations.NotNull;

public class AdminBioMhs extends AppCompatActivity {
    private DatabaseReference countKelas;
    private FirebaseRecyclerOptions<DataRecycler> options;
    private FirebaseRecyclerAdapter<DataRecycler, FirebaseViewHolder> adapter;
    private ShimmerFrameLayout shimmerDataMhs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.adm_act_bio_mhs);

        RecyclerView rvKlsMhs = findViewById(R.id.rvKlsMhs);
        shimmerDataMhs = findViewById(R.id.shimmer_data_mhs);
        shimmerDataMhs.startShimmer();
        LottieAnimationView lottieEmpty = findViewById(R.id.lottieEmpty);

        DatabaseReference reffKelas = FirebaseDatabase.getInstance().getReference("Kelas");
        countKelas = FirebaseDatabase.getInstance().getReference();
        reffKelas.keepSynced(true);
        countKelas.keepSynced(true);

        rvKlsMhs.setHasFixedSize(true);
        rvKlsMhs.setLayoutManager(new LinearLayoutManager(AdminBioMhs.this));

        options = new FirebaseRecyclerOptions.Builder<DataRecycler>().setQuery(reffKelas, DataRecycler.class).build();

        adapter = new FirebaseRecyclerAdapter<DataRecycler, FirebaseViewHolder>(options) {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            protected void onBindViewHolder(@NonNull FirebaseViewHolder firebaseViewHolder, int i, @NonNull DataRecycler dataRecycler) {
                shimmerDataMhs.setVisibility(View.INVISIBLE);

                firebaseViewHolder.name.setText(dataRecycler.getNama_kelas());

//                getcount kelas
                countKelas.child(dataRecycler.getNama_kelas()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        int jumlah = (int) snapshot.getChildrenCount();
                        firebaseViewHolder.jumlah.setText(jumlah + "  Mahasiswa");
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });
                firebaseViewHolder.listItem.setOnClickListener(view -> {
                    Intent intent = new Intent(AdminBioMhs.this, AdminDetailBioMhs.class);
                    intent.putExtra("nameKelas", ""+dataRecycler.getNama_kelas());
                    startActivity(intent);
                });
            }

            @NonNull
            @Override
            public FirebaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new FirebaseViewHolder(LayoutInflater.from(AdminBioMhs.this).inflate(R.layout.list_item_kelas, parent, false));
            }
        };

        SetEmptyState setEmptyState = new SetEmptyState();
        setEmptyState.emptyState(reffKelas,shimmerDataMhs, lottieEmpty);

        rvKlsMhs.setAdapter(adapter);
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