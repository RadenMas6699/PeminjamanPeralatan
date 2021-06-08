package com.radenmas.peminjamanperalatan.user;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.radenmas.peminjamanperalatan.FirebaseViewHolder;
import com.radenmas.peminjamanperalatan.R;
import com.radenmas.peminjamanperalatan.adapter.DataRecycler;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class FragHistoryUser extends Fragment {
    private LottieAnimationView lottieEmpty;
    IntentIntegrator qrScanIntegrator;
    private ShimmerFrameLayout shimmerHistory;
    private DatabaseReference reference, referenceLab;
    private FirebaseRecyclerOptions<DataRecycler> options;
    private FirebaseRecyclerAdapter<DataRecycler, FirebaseViewHolder> adapter;
    String text_qr = "";
    String id_tool = "";
    String nameLabRep = "";
    int available, jml_available, jml_pinjam;

    public FragHistoryUser() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.user_frag_history, container, false);

        shimmerHistory = view.findViewById(R.id.shimmer_history);
        shimmerHistory.startShimmer();
        lottieEmpty = view.findViewById(R.id.lottieEmpty);

        RecyclerView rv_list_history = view.findViewById(R.id.rv_list_history);
        rv_list_history.setHasFixedSize(true);
        rv_list_history.setLayoutManager(new LinearLayoutManager(getContext()));

        FirebaseAuth auth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference("Pinjam").child(auth.getCurrentUser().getUid());
        reference.keepSynced(true);

        options = new FirebaseRecyclerOptions.Builder<DataRecycler>().setQuery(reference, DataRecycler.class).build();
        adapter = new FirebaseRecyclerAdapter<DataRecycler, FirebaseViewHolder>(options) {
            @SuppressLint({"UseCompatLoadingForDrawables", "SetTextI18n"})
            @Override
            protected void onBindViewHolder(@NonNull FirebaseViewHolder firebaseViewHolder, int i, @NonNull DataRecycler dataRecycler) {
                int number = i + 1;
                int check = dataRecycler.getCheck();
                if (check == 1) {
                    firebaseViewHolder.check.setVisibility(View.VISIBLE);
                    firebaseViewHolder.linearBtnAdd.setVisibility(View.INVISIBLE);
                    firebaseViewHolder.scan.setVisibility(View.INVISIBLE);
                } else {
                    firebaseViewHolder.check.setVisibility(View.INVISIBLE);
                    firebaseViewHolder.linearBtnAdd.setVisibility(View.VISIBLE);
                    firebaseViewHolder.scan.setVisibility(View.VISIBLE);
                }

                firebaseViewHolder.number.setText("" + number);
                firebaseViewHolder.name.setText(dataRecycler.getNama_tool());
                firebaseViewHolder.lab.setText(dataRecycler.getNama_lab());

                //tombol min
                firebaseViewHolder.imgMin.setOnClickListener(view12 -> {
                    int jmlMin = dataRecycler.getJml_tool() - 1;
                    if (jmlMin == 0) {
                        //hapus recyler
                        reference.child(dataRecycler.getId_tool()).removeValue();
                    } else {
                        Map<String, Object> jml_tool = new HashMap<>();
                        jml_tool.put("jml_tool", jmlMin);
                        reference.child(dataRecycler.getId_tool()).updateChildren(jml_tool);
                    }
                });

                //tombol tambah
                firebaseViewHolder.imgAdd.setOnClickListener(view13 -> {
                    int jmlAdd = dataRecycler.getJml_tool() + 1;
                    Map<String, Object> jml_tool = new HashMap<>();
                    jml_tool.put("jml_tool", jmlAdd);
                    reference.child(dataRecycler.getId_tool()).updateChildren(jml_tool);
                });

                //get Jumlah alat dilab
                nameLabRep = dataRecycler.getNama_lab().replace(" ", "_");
                referenceLab = FirebaseDatabase.getInstance().getReference(nameLabRep).child(dataRecycler.getId_tool());
                referenceLab.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        String jmlToolLab = (String) snapshot.child("jml_tool").getValue().toString();

                        firebaseViewHolder.jumlah.setText("Stok: " + jmlToolLab + "  " + dataRecycler.getType_tool());
                        available = Integer.parseInt(jmlToolLab);
                        if (dataRecycler.getJml_tool() >= available) {
                            firebaseViewHolder.imgAdd.setClickable(false);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });

                firebaseViewHolder.etJml.setClickable(false);
                firebaseViewHolder.etJml.setText(String.valueOf(dataRecycler.getJml_tool()));

                firebaseViewHolder.scan.setOnClickListener(view1 -> {
                    performAction();
                    text_qr = dataRecycler.getText_qr_tool();
                    id_tool = dataRecycler.getId_tool();
                    jml_available = available;
                    jml_pinjam = dataRecycler.getJml_tool();
                });

                shimmerHistory.setVisibility(View.INVISIBLE);
            }

            @NonNull
            @Override
            public FirebaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new FirebaseViewHolder(LayoutInflater.from(getContext()).inflate(R.layout.list_history, parent, false));
            }
        };


        adapter.notifyDataSetChanged();

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                int count = (int) snapshot.getChildrenCount();
                if (count == 0) {
                    lottieEmpty.setVisibility(View.VISIBLE);
                    shimmerHistory.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

        rv_list_history.setAdapter(adapter);


        qrScanIntegrator = IntentIntegrator.forSupportFragment(this);
        qrScanIntegrator.setPrompt("Arahkan kamera ke QR Code");
        qrScanIntegrator.setBeepEnabled(true);
        qrScanIntegrator.setCameraId(0);
        qrScanIntegrator.setBeepEnabled(false);
        qrScanIntegrator.setBarcodeImageEnabled(true);
        qrScanIntegrator.setOrientationLocked(true);
        qrScanIntegrator.setCaptureActivity(Capture.class);


        return view;
    }

    private void performAction() {
        qrScanIntegrator.initiateScan();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        String dataQr = String.valueOf(result.getContents());
        if (dataQr.equals(text_qr)) {
            //update status
            FirebaseAuth auth = FirebaseAuth.getInstance();
            reference = FirebaseDatabase.getInstance().getReference("Pinjam").child(auth.getCurrentUser().getUid());
            Map<String, Object> check_tool = new HashMap<>();
            check_tool.put("check", 1);
            reference.child(id_tool).updateChildren(check_tool);

            //update stok dilab
            int updateStock = jml_available - jml_pinjam;
            referenceLab = FirebaseDatabase.getInstance().getReference(nameLabRep).child(id_tool);
            Map<String, Object> updateData = new HashMap<>();
            updateData.put("jml_tool", updateStock);
            referenceLab.updateChildren(updateData);

        }else {
            if (!dataQr.equals("null")){
                Snackbar.make(getView(), "QR Code tidak sesuai",Snackbar.LENGTH_SHORT).show();
            }
        }
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
}