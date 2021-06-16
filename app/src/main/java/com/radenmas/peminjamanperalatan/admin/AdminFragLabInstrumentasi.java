package com.radenmas.peminjamanperalatan.admin;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.radenmas.peminjamanperalatan.FirebaseViewHolder;
import com.radenmas.peminjamanperalatan.R;
import com.radenmas.peminjamanperalatan.SetEmptyState;
import com.radenmas.peminjamanperalatan.adapter.DataRecycler;
import com.squareup.picasso.Picasso;

public class AdminFragLabInstrumentasi extends Fragment {

    private DatabaseReference reffLabInstrumentasi;
    private ShimmerFrameLayout shimmer_frag;
    private final String path = "Lab_Instrumentasi";

    private FirebaseRecyclerOptions<DataRecycler> options;
    private FirebaseRecyclerAdapter<DataRecycler, FirebaseViewHolder> adapter;

    public AdminFragLabInstrumentasi() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.adm_frag_lab, container, false);

        RecyclerView rv_lab_instrumentasi = view.findViewById(R.id.rv_lab);
        LottieAnimationView lottieEmpty = view.findViewById(R.id.lottieEmpty);
        shimmer_frag = view.findViewById(R.id.shimmer_frag);
        shimmer_frag.startShimmer();

        TextView tvEmptyState = view.findViewById(R.id.tvEmptyState);

        reffLabInstrumentasi = FirebaseDatabase.getInstance().getReference(path);
        reffLabInstrumentasi.keepSynced(true);

        rv_lab_instrumentasi.setHasFixedSize(true);
        rv_lab_instrumentasi.setLayoutManager(new LinearLayoutManager(getContext()));

        options = new FirebaseRecyclerOptions.Builder<DataRecycler>().setQuery(reffLabInstrumentasi, DataRecycler.class).build();

        adapter = new FirebaseRecyclerAdapter<DataRecycler, FirebaseViewHolder>(options) {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            protected void onBindViewHolder(@NonNull FirebaseViewHolder firebaseViewHolder, int i, @NonNull DataRecycler dataRecycler) {
                shimmer_frag.setVisibility(View.INVISIBLE);

                int number = i + 1;
                firebaseViewHolder.number.setText("" + number);
                firebaseViewHolder.name.setText(dataRecycler.getNama_tool());
                firebaseViewHolder.jumlah.setText(dataRecycler.getJml_tool() + "  " + dataRecycler.getType_tool());
                firebaseViewHolder.popup.setImageDrawable(getResources().getDrawable(R.drawable.ic_more_vertical));
                firebaseViewHolder.popup.setOnClickListener(view1 -> {
                    PopupMenu popupMenu = new PopupMenu(view1.getContext(), view1);
                    popupMenu.inflate(R.menu.menu_lab);
                    popupMenu.setOnMenuItemClickListener(menuItem -> {

                        switch (menuItem.getItemId()) {
                            case R.id.popup_detail:
                                showDetailTool(dataRecycler.getNama_lab(), dataRecycler.getNama_tool(), dataRecycler.getLink_tool(), dataRecycler.getJml_tool(), dataRecycler.getType_tool());
                                break;

                            case R.id.popup_delete:

                                new AlertDialog.Builder(getContext())
                                        .setMessage("Hapus alat " + dataRecycler.getNama_tool())
                                        .setPositiveButton("Ya", (dialogInterface, i1) -> reffLabInstrumentasi.child(dataRecycler.getId_tool()).removeValue().addOnCompleteListener(task -> {

                                            //hapus realtime database
                                            Query query = reffLabInstrumentasi.orderByChild("id_tool").equalTo(dataRecycler.getId_tool());
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

                                            //hapus storage
                                            StorageReference storageReference = FirebaseStorage.getInstance().getReference();
                                            StorageReference reference = storageReference.child(path).child(dataRecycler.getId_tool() + ".jpg");
                                            reference.delete().addOnSuccessListener(aVoid -> {
                                            }).addOnFailureListener(e -> {
                                            });

                                            Toast.makeText(getContext(), dataRecycler.getNama_tool() + " berhasil dihapus", Toast.LENGTH_SHORT).show();
                                        }))
                                        .setNegativeButton("Tidak", (dialogInterface, i1) -> {

                                        }).show();
                                break;
                        }
                        return true;
                    });
                    popupMenu.show();
                });
            }

            @NonNull
            @Override
            public FirebaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new FirebaseViewHolder(LayoutInflater.from(getContext()).inflate(R.layout.list_item_lab, parent, false));
            }
        };

        SetEmptyState setEmptyState = new SetEmptyState();
        setEmptyState.emptyState(reffLabInstrumentasi, shimmer_frag, lottieEmpty, tvEmptyState);

        rv_lab_instrumentasi.setAdapter(adapter);

        return view;
    }

    private void showDetailTool(String nama_lab, String nama_tool, String link_tool, int jml_tool, String type_tool) {
        BottomSheetDialog dialog = new BottomSheetDialog(getContext(), R.style.BottomSheetDialog);
        dialog.setContentView(R.layout.bottom_detail_tools);
        dialog.setCanceledOnTouchOutside(false);

        ImageView img_qr_code_detail = dialog.findViewById(R.id.img_qr_code_detail);
        TextView name_tool_detail = dialog.findViewById(R.id.name_tool_detail);
        TextView jml_tool_detail = dialog.findViewById(R.id.jml_tool_detail);

        Picasso.get().load(link_tool).into(img_qr_code_detail);
        name_tool_detail.setText(nama_tool);
        jml_tool_detail.setText(jml_tool + "  " + type_tool);

        MaterialButton btnSaveQR = dialog.findViewById(R.id.btnSaveQR);
        btnSaveQR.setOnClickListener(view -> {
            ProgressDialog pd = new ProgressDialog(getContext());
            pd.setTitle(nama_tool + ".jpg");
            pd.setMessage("Mengunduh..");
            pd.setIndeterminate(true);
            pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pd.show();

            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference httpsReference = storage.getReferenceFromUrl(link_tool);
            httpsReference.getDownloadUrl().addOnSuccessListener(uri -> {
                pd.dismiss();
                Toast.makeText(getContext(), "Berhasil mengunduh", Toast.LENGTH_SHORT).show();
                DownloadManager downloadmanager = (DownloadManager) getContext().getSystemService(Context.DOWNLOAD_SERVICE);
                DownloadManager.Request request = new DownloadManager.Request(uri);
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "/Peminjaman Peralatan/" + nama_lab + "/" + nama_tool + ".jpg");
                downloadmanager.enqueue(request);
            }).addOnFailureListener(e -> {
                pd.dismiss();
                Toast.makeText(getContext(), "Gagal mengunduh", Toast.LENGTH_SHORT).show();
            });
        });

        dialog.show();
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