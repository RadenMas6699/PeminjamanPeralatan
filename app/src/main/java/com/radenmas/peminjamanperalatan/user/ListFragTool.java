package com.radenmas.peminjamanperalatan.user;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
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

public class ListFragTool extends Fragment {

    private DatabaseReference getKondisiScan;
    private FirebaseRecyclerOptions<DataRecycler> options;
    private FirebaseRecyclerAdapter<DataRecycler, FirebaseViewHolder> adapter;
    private LottieAnimationView lottieEmpty;
    private ShimmerFrameLayout shimmer_frag;

    String user, kelompok, img_profil, userId, id_tool, lab, kunci, exist;

    public ListFragTool() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.list_frag_tool, container, false);

        RecyclerView rv_user_home_mesin = view.findViewById(R.id.rv_user_home_tool);
        rv_user_home_mesin.setHasFixedSize(true);
        rv_user_home_mesin.setLayoutManager(new LinearLayoutManager(getContext()));
        shimmer_frag = view.findViewById(R.id.shimmer_frag);
        shimmer_frag.startShimmer();

        TextView tvEmptyState = view.findViewById(R.id.tvEmptyState);

        lottieEmpty = view.findViewById(R.id.lottieEmpty);

        getDataUser();

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            lab = bundle.getString("lab");
        }

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(lab);
        reference.keepSynced(true);


        options = new FirebaseRecyclerOptions.Builder<DataRecycler>().setQuery(reference, DataRecycler.class).build();
        adapter = new FirebaseRecyclerAdapter<DataRecycler, FirebaseViewHolder>(options) {
            @SuppressLint({"UseCompatLoadingForDrawables", "SetTextI18n"})
            @Override
            protected void onBindViewHolder(@NonNull FirebaseViewHolder firebaseViewHolder, int i, @NonNull DataRecycler dataRecycler) {
                int number = i + 1;
                shimmer_frag.setVisibility(View.INVISIBLE);

                FirebaseAuth auth = FirebaseAuth.getInstance();
                String uidUser = auth.getCurrentUser().getUid();
                DatabaseReference order = FirebaseDatabase.getInstance().getReference("Pinjam").child(uidUser);
                DatabaseReference listPinjam = FirebaseDatabase.getInstance().getReference("ListPinjam").child(uidUser);

                firebaseViewHolder.number.setText("" + number);
                firebaseViewHolder.name.setText(dataRecycler.getNama_tool());
                firebaseViewHolder.jumlah.setText("Stok: " + dataRecycler.getJml_tool() + "  " + dataRecycler.getType_tool());

                firebaseViewHolder.btnAddAlat.setOnClickListener(view1 -> {
                    id_tool = dataRecycler.getId_tool();

                    Map<String, Object> nama_tool = new HashMap<>();
                    nama_tool.put("id_tool", id_tool);
                    nama_tool.put("nama_tool", dataRecycler.getNama_tool());
                    nama_tool.put("jml_tool", 1);
                    nama_tool.put("check", 0);
                    nama_tool.put("type_tool", dataRecycler.getType_tool());
                    nama_tool.put("nama_lab", dataRecycler.getNama_lab());
                    nama_tool.put("text_qr_tool", dataRecycler.getText_qr_tool());
                    order.child(id_tool).setValue(nama_tool);

                    Map<String, Object> dataUser = new HashMap<>();
                    dataUser.put("id", userId);
                    dataUser.put("name", user);
                    dataUser.put("kelompok", kelompok);
                    dataUser.put("img_profil", img_profil);
                    listPinjam.setValue(dataUser);
                });
            }

            @NonNull
            @Override
            public FirebaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new FirebaseViewHolder(LayoutInflater.from(getContext()).inflate(R.layout.list_item_home, parent, false));
            }
        };

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                int count = (int) snapshot.getChildrenCount();
                if (count == 0) {
                    shimmer_frag.setVisibility(View.INVISIBLE);
                    lottieEmpty.setVisibility(View.VISIBLE);
                    tvEmptyState.setVisibility(View.VISIBLE);
                } else {
                    shimmer_frag.setVisibility(View.INVISIBLE);
                    lottieEmpty.setVisibility(View.INVISIBLE);
                    tvEmptyState.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

        adapter.notifyDataSetChanged();

        rv_user_home_mesin.setAdapter(adapter);

        return view;
    }

    private void getDataUser() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        DatabaseReference dbuser = FirebaseDatabase.getInstance().getReference("User");
        dbuser.child(auth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                user = (String) snapshot.child("name").getValue();
                kelompok = (String) snapshot.child("kelompok").getValue();
                userId = (String) snapshot.child("id").getValue();
                img_profil = (String) snapshot.child("img_profil").getValue();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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
}