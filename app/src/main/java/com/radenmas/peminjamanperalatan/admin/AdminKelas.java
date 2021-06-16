package com.radenmas.peminjamanperalatan.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.radenmas.peminjamanperalatan.FirebaseViewHolder;
import com.radenmas.peminjamanperalatan.R;
import com.radenmas.peminjamanperalatan.SetEmptyState;
import com.radenmas.peminjamanperalatan.adapter.DataRecycler;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class AdminKelas extends AppCompatActivity {
    private DatabaseReference reffKelas, countKelas;
    private FirebaseRecyclerOptions<DataRecycler> options;
    private FirebaseRecyclerAdapter<DataRecycler, FirebaseViewHolder> adapter;
    private ShimmerFrameLayout shimmerKelas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.adm_act_kelas);

        RecyclerView rv_kelas = findViewById(R.id.rv_kelas);
        LottieAnimationView lottieEmpty = findViewById(R.id.lottieEmpty);
        shimmerKelas = findViewById(R.id.shimmer_kelas);
        shimmerKelas.startShimmer();

        TextView tvEmptyState = findViewById(R.id.tvEmptyState);

        reffKelas = FirebaseDatabase.getInstance().getReference("Kelas");
        countKelas = FirebaseDatabase.getInstance().getReference();
        reffKelas.keepSynced(true);
        countKelas.keepSynced(true);

        rv_kelas.setHasFixedSize(true);
        rv_kelas.setLayoutManager(new LinearLayoutManager(AdminKelas.this));

        options = new FirebaseRecyclerOptions.Builder<DataRecycler>().setQuery(reffKelas, DataRecycler.class).build();

        adapter = new FirebaseRecyclerAdapter<DataRecycler, FirebaseViewHolder>(options) {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            protected void onBindViewHolder(@NonNull FirebaseViewHolder firebaseViewHolder, int i, @NonNull DataRecycler dataRecycler) {
                shimmerKelas.setVisibility(View.INVISIBLE);

                firebaseViewHolder.name.setText(dataRecycler.getNama_kelas());

                //getcount kelas
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
                firebaseViewHolder.popup.setVisibility(View.VISIBLE);
                firebaseViewHolder.popup.setImageDrawable(getResources().getDrawable(R.drawable.ic_delete));
                firebaseViewHolder.popup.setOnClickListener(view1 -> new AlertDialog.Builder(AdminKelas.this)
                        .setMessage("Hapus kelas " + dataRecycler.getNama_kelas())
                        .setPositiveButton("Ya", (dialogInterface, i1) -> {
                            //show loading
                            ProgressDialog progressDialog = new ProgressDialog(AdminKelas.this);
                            progressDialog.show();
                            progressDialog.setContentView(R.layout.progress_layout);
                            progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                            progressDialog.show();

                            reffKelas.child(dataRecycler.getId_kelas()).removeValue().addOnCompleteListener(task -> {

                                //hapus realtime database
                                Query query = reffKelas.orderByChild("id_kelas").equalTo(dataRecycler.getId_kelas());
                                query.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                            dataSnapshot.getRef().removeValue();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

                                new Handler().postDelayed(() -> {
                                    progressDialog.dismiss();
                                    Toast.makeText(AdminKelas.this, dataRecycler.getNama_kelas() + " berhasil dihapus", Toast.LENGTH_SHORT).show();
                                }, 500);

                            });
                        })
                        .setNegativeButton("Tidak", (dialogInterface, i1) -> {

                        }).show());
            }

            @NonNull
            @Override
            public FirebaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new FirebaseViewHolder(LayoutInflater.from(AdminKelas.this).inflate(R.layout.list_item_kelas, parent, false));
            }
        };

        SetEmptyState setEmptyState = new SetEmptyState();
        setEmptyState.emptyState(reffKelas, shimmerKelas, lottieEmpty, tvEmptyState);

        adapter.notifyDataSetChanged();

        rv_kelas.setAdapter(adapter);

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

    public void AddKelas(View view) {
        BottomSheetDialog dialog = new BottomSheetDialog(AdminKelas.this, R.style.BottomSheetDialog);
        dialog.setContentView(R.layout.bottom_add_kelas);
        dialog.setCanceledOnTouchOutside(false);

        TextInputEditText etKelas = dialog.findViewById(R.id.et_add_kelas);
        MaterialButton btnAddKelas = dialog.findViewById(R.id.btn_add_kelas);
        ImageView cancelAddKelas = dialog.findViewById(R.id.cancel_add_kelas);

        cancelAddKelas.setOnClickListener(view1 -> dialog.dismiss());

        btnAddKelas.setOnClickListener(view1 -> {

            InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            manager.hideSoftInputFromWindow(etKelas.getApplicationWindowToken(), 0);

            String nama_kelas = etKelas.getText().toString();

            if (nama_kelas.isEmpty()) {
                etKelas.setError("Masukkan Nama Kelas");
            } else {

                String id = reffKelas.push().getKey();

                Map<String, Object> nama_kelas_obj = new HashMap<>();
                nama_kelas_obj.put("id_kelas", id);
                nama_kelas_obj.put("nama_kelas", nama_kelas);

                reffKelas.child(id).setValue(nama_kelas_obj);

                ProgressDialog progressDialog = new ProgressDialog(AdminKelas.this);
                progressDialog.show();
                progressDialog.setContentView(R.layout.progress_layout);
                progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                progressDialog.show();

                new Handler().postDelayed(() -> {
                    progressDialog.dismiss();
                    etKelas.setText("");
                    Toast.makeText(AdminKelas.this, nama_kelas + " berhasil ditambahkan", Toast.LENGTH_SHORT).show();
                }, 1000);

            }

        });

        dialog.show();
    }

    public void Back(View view) {
        onBackPressed();
    }
}