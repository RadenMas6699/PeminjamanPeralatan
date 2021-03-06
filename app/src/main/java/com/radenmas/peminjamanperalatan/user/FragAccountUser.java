package com.radenmas.peminjamanperalatan.user;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.radenmas.peminjamanperalatan.About;
import com.radenmas.peminjamanperalatan.CircleTransform;
import com.radenmas.peminjamanperalatan.Login;
import com.radenmas.peminjamanperalatan.R;
import com.radenmas.peminjamanperalatan.admin.AdminDetailBioMhs;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class FragAccountUser extends Fragment implements View.OnClickListener {
    private static final int RESULT_OK = -1;
    private ImageView img_user;
    private TextView name_user, nim_user, nim_user_desc, kelas_user_desc, kelompok_user_desc, email_user_desc, phone_user_desc, deleteAccount;
    private DatabaseReference reference;
    private Uri filePath;
    private String kelas;
    private String uid;
    private FirebaseUser user;

    public FragAccountUser() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.user_frag_account, container, false);

        img_user = view.findViewById(R.id.img_user);
        ImageView info_app = view.findViewById(R.id.info_app);
        ImageView change_profil = view.findViewById(R.id.change_profil);
        name_user = view.findViewById(R.id.name_user);
        nim_user = view.findViewById(R.id.status_user);
        deleteAccount = view.findViewById(R.id.deleteAccount);

        MaterialButton btn_logout = view.findViewById(R.id.btn_logout);

        nim_user_desc = view.findViewById(R.id.nim_user_desc);
        kelas_user_desc = view.findViewById(R.id.kelas_user_desc);
        kelompok_user_desc = view.findViewById(R.id.kelompok_user_desc);
        email_user_desc = view.findViewById(R.id.email_user_desc);
        phone_user_desc = view.findViewById(R.id.phone_user_desc);

        reference = FirebaseDatabase.getInstance().getReference();

        user = FirebaseAuth.getInstance().getCurrentUser();
        uid = user.getUid();

        getDataUser(uid);


        info_app.setOnClickListener(this);
        btn_logout.setOnClickListener(this);
        change_profil.setOnClickListener(this);
        deleteAccount.setOnClickListener(this);

        return view;
    }

    private void getDataUser(String uid) {
        reference.child("User").child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String user = (String) snapshot.child("name").getValue();
                String nim = (String) snapshot.child("nim").getValue();
                kelas = (String) snapshot.child("nama_kelas").getValue();
                String kelompok = (String) snapshot.child("kelompok").getValue();
                String email = (String) snapshot.child("email").getValue();
                String phone = (String) snapshot.child("phone").getValue();
                String img_profil = (String) snapshot.child("img_profil").getValue();

                Picasso.get().load(img_profil).error(R.drawable.ic_user_circle_black).transform(new CircleTransform()).into(img_user);
                name_user.setText(user);
                nim_user_desc.setText(nim);
                kelas_user_desc.setText(kelas);
                kelompok_user_desc.setText(kelompok);
                email_user_desc.setText(email);
                phone_user_desc.setText(phone);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.info_app:
                startActivity(new Intent(getContext(), About.class));
                break;
            case R.id.change_profil:
                ChooseFoto();
                break;
            case R.id.btn_logout:
                new AlertDialog.Builder(getActivity())
                        .setMessage("Apakah anda yakin untuk keluar?")
                        .setPositiveButton("Ya", (dialogInterface, i) -> {
                            logout();
                            deletePref();
                            moveLogin();
                        })
                        .setNegativeButton("Tidak", (dialogInterface, i) -> dialogInterface.dismiss())
                        .show();
                break;
            case R.id.deleteAccount:
                new AlertDialog.Builder(getActivity())
                        .setMessage("Apakah anda yakin akan menghapus akun ?")
                        .setPositiveButton("Ya", (dialogInterface, i) -> {
                            DeleteAccount();
                        })
                        .setNegativeButton("Tidak", (dialogInterface, i) -> dialogInterface.dismiss())
                        .show();
                break;

        }
    }

    private void DeleteAccount() {
        //delete account from firebase
        user.delete()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(getContext(), "Akun berhasil dihapus", Toast.LENGTH_SHORT).show();

                        logout();

                        deletePref();

                        //pindah activity
                        new Handler().postDelayed(() -> {
                            moveLogin();

                            //hapus storage profil
                            FirebaseStorage.getInstance().getReference("User").child(uid).child(uid).delete();

                            //hapus realtime database user dikelas
                            FirebaseDatabase.getInstance().getReference(kelas).child(uid).removeValue();

                            //hapus realtime database user
                            FirebaseDatabase.getInstance().getReference("User").child(uid).removeValue();
                        }, 500);
                    }
                });
    }

    private void moveLogin() {
        //pindah activity
        startActivity(new Intent(getActivity(), Login.class));
        getActivity().finish();
    }

    private void deletePref() {
        //hapus data Shared Preference
        SharedPreferences myaccount = getActivity().getSharedPreferences("myAccount", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = myaccount.edit();
        editor.clear();
        editor.apply();
    }

    private void logout() {
        //log out from firebase
        FirebaseAuth.getInstance().signOut();
    }

    private void ChooseFoto() {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Pilih Gambar"), 71);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 71 && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            filePath = data.getData();
            UploadFoto();

        }
    }

    private void UploadFoto() {

        StorageReference storageReference = FirebaseStorage.getInstance().getReference("User").child(uid);

        if (filePath != null) {
            final ProgressDialog progressDialog = new ProgressDialog(getContext());
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            StorageReference ref = storageReference.child(uid);
            ref.putFile(filePath)
                    .addOnSuccessListener(taskSnapshot -> {
                        DatabaseReference dbUploadImg = FirebaseDatabase.getInstance().getReference("User").child(uid);
                        DatabaseReference dbUploadKelas = FirebaseDatabase.getInstance().getReference(kelas).child(uid);
                        progressDialog.dismiss();
                        ref.getDownloadUrl().addOnSuccessListener(uri -> {
                            Map<String, Object> profilUser = new HashMap<>();
                            profilUser.put("img_profil", String.valueOf(uri));
                            dbUploadImg.updateChildren(profilUser);
                            dbUploadKelas.updateChildren(profilUser);
                        });
                        Toast.makeText(getActivity(), "Foto profil berhasil diubah", Toast.LENGTH_LONG).show();
                    })
                    .addOnFailureListener(e -> {
                        progressDialog.dismiss();
                        Toast.makeText(getActivity(), "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    })
                    .addOnProgressListener(taskSnapshot -> {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                .getTotalByteCount());
                        progressDialog.setMessage("Uploaded " + (int) progress + "%");
                    });
        } else {
            Toast.makeText(getActivity(), "Gambar belum dipilih", Toast.LENGTH_SHORT).show();
        }
    }

}