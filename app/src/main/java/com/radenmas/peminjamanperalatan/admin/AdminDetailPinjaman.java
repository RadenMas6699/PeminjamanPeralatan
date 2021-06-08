package com.radenmas.peminjamanperalatan.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.radenmas.peminjamanperalatan.FirebaseViewHolder;
import com.radenmas.peminjamanperalatan.R;
import com.radenmas.peminjamanperalatan.adapter.DataRecycler;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class AdminDetailPinjaman extends AppCompatActivity {
    private TextView tvName, tvNim, tvKelas, tvKelompok;

    private DatabaseReference reference;
    private DatabaseReference listPinjam;
    private FirebaseRecyclerOptions<DataRecycler> options;
    private FirebaseRecyclerAdapter<DataRecycler, FirebaseViewHolder> adapter;
    private int jmlPinjam;
    private int intAvailable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.adm_act_detail_pinjaman);

        String uidUser = getIntent().getExtras().getString("uidUser");

        tvName = findViewById(R.id.tv_name);
        tvNim = findViewById(R.id.tv_nim);
        tvKelas = findViewById(R.id.tv_kelas);
        tvKelompok = findViewById(R.id.tv_kelompok);
        RecyclerView rvAlatPeminjam = findViewById(R.id.rv_alat_peminjam);
        rvAlatPeminjam.setHasFixedSize(true);
        rvAlatPeminjam.setLayoutManager(new LinearLayoutManager(AdminDetailPinjaman.this));

        getDataUser(uidUser);

        reference = FirebaseDatabase.getInstance().getReference("Pinjam").child(uidUser);
        listPinjam = FirebaseDatabase.getInstance().getReference("ListPinjam").child(uidUser);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                int count = (int) snapshot.getChildrenCount();
                if (count == 0) {
                    listPinjam.removeValue();
                    onBackPressed();
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

        options = new FirebaseRecyclerOptions.Builder<DataRecycler>().setQuery(reference, DataRecycler.class).build();
        adapter = new FirebaseRecyclerAdapter<DataRecycler, FirebaseViewHolder>(options) {
            @SuppressLint({"UseCompatLoadingForDrawables", "SetTextI18n"})
            @Override
            protected void onBindViewHolder(@NonNull FirebaseViewHolder firebaseViewHolder, int i, @NonNull DataRecycler dataRecycler) {
                int number = i + 1;
                int check = dataRecycler.getCheck();
                if (check == 1) {
                    firebaseViewHolder.check.setVisibility(View.VISIBLE);

                    firebaseViewHolder.listItem.setOnClickListener(view -> {

                        PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
                        popupMenu.inflate(R.menu.menu_data_peminjam);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            popupMenu.setGravity(Gravity.END);
                        }
                        popupMenu.setOnMenuItemClickListener(menuItem -> {
                            if (menuItem.getItemId() == R.id.popup_acc) {
                                //get Jumlah Available Lab

                                String namaLab = dataRecycler.getNama_lab().replace(" ", "_");
                                String idTool = dataRecycler.getId_tool();

                                //get Jumlah Pinjam
                                jmlPinjam = dataRecycler.getJml_tool();

                                DatabaseReference refAvailable = FirebaseDatabase.getInstance().getReference(namaLab).child(idTool);
                                refAvailable.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                        String jmlAvailable = (String) snapshot.child("jml_tool").getValue().toString();
                                        intAvailable = Integer.parseInt(jmlAvailable);

                                        //add available dengan pinjam
                                        int updateStock = intAvailable + jmlPinjam;

                                        //update Data
                                        Map<String, Object> updateData = new HashMap<>();
                                        updateData.put("jml_tool", updateStock);
                                        refAvailable.updateChildren(updateData);

                                        //delete Data Pinjam
                                        reference.child(dataRecycler.getId_tool()).removeValue();

                                    }

                                    @Override
                                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                                    }
                                });


                            }
                            return true;
                        });
                        popupMenu.show();
                    });
                } else {
                    firebaseViewHolder.check.setVisibility(View.INVISIBLE);
                }

                firebaseViewHolder.number.setText("" + number);
                firebaseViewHolder.name.setText(dataRecycler.getNama_tool());
                firebaseViewHolder.jumlah.setText(dataRecycler.getJml_tool() + "  " + dataRecycler.getType_tool());
                firebaseViewHolder.lab.setText(dataRecycler.getNama_lab());
                firebaseViewHolder.scan.setVisibility(View.INVISIBLE);
            }

            @NonNull
            @Override
            public FirebaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new FirebaseViewHolder(LayoutInflater.from(AdminDetailPinjaman.this).inflate(R.layout.list_history, parent, false));
            }

        };

        adapter.notifyDataSetChanged();

        rvAlatPeminjam.setAdapter(adapter);

    }

    private void getDataUser(String uidUser) {
        DatabaseReference user = FirebaseDatabase.getInstance().getReference("User").child(uidUser);
        user.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                String name = (String) snapshot.child("name").getValue();
                String nim = (String) snapshot.child("nim").getValue();
                String kelas = (String) snapshot.child("nama_kelas").getValue();
                String kelompok = (String) snapshot.child("kelompok").getValue();

                tvName.setText(name);
                tvNim.setText(nim);
                tvKelas.setText(kelas);
                tvKelompok.setText(kelompok);
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
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