package com.radenmas.peminjamanperalatan.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.radenmas.peminjamanperalatan.CircleTransform;
import com.radenmas.peminjamanperalatan.Login;
import com.radenmas.peminjamanperalatan.R;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class AdminAccount extends AppCompatActivity implements View.OnClickListener {
    private static final int RESULT_OK = -1;
    private ImageView img_admin;
    private TextView name_admin, email_admin, phone_admin;
    private DatabaseReference reference;
    private Uri filePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.adm_act_account);

        img_admin = findViewById(R.id.img_admin);
        ImageView change_profil_admin = findViewById(R.id.change_profil_admin);
        name_admin = findViewById(R.id.name_admin);
        email_admin = findViewById(R.id.email_admin);
        phone_admin = findViewById(R.id.phone_admin);

        MaterialButton btn_logout = findViewById(R.id.btn_logout);

        btn_logout.setOnClickListener(this);
        change_profil_admin.setOnClickListener(this);

        reference = FirebaseDatabase.getInstance().getReference();

        getDataAdmin();
    }

    private void getDataAdmin() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        reference.child("Admin").child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String admin = (String) snapshot.child("name").getValue();
                String email = (String) snapshot.child("email").getValue();
                String phone = (String) snapshot.child("phone").getValue();
                String img_profil = (String) snapshot.child("img_profil").getValue();

                Picasso.get().load(img_profil).error(R.drawable.ic_user_circle_black).transform(new CircleTransform()).into(img_admin);
                name_admin.setText(admin);
                email_admin.setText(email);
                phone_admin.setText(phone);

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
            case R.id.change_profil_admin:
                change_profil();
                break;
            case R.id.btn_logout:
                logout();
                break;
        }
    }

    private void logout() {
        new AlertDialog.Builder(this)
                .setMessage("Apakah anda yakin untuk keluar?")
                .setPositiveButton("Ya", (dialogInterface, i) -> {

                    //log out from firebase
                    FirebaseAuth.getInstance().signOut();

                    //hapus data Shared Preference
                    SharedPreferences myaccount = this.getSharedPreferences("myAccount", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = myaccount.edit();
                    editor.clear();
                    editor.apply();

                    //pindah activity
                    startActivity(new Intent(this, Login.class));
                    this.finish();
                })
                .setNegativeButton("Tidak", (dialogInterface, i) -> dialogInterface.dismiss())
                .show();
    }

    private void change_profil() {
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
            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            String uidUser = firebaseUser.getUid();
            UploadFoto(uidUser);

        }
    }

    private void UploadFoto(String uidUser) {

        StorageReference storageReference = FirebaseStorage.getInstance().getReference("Admin").child(uidUser);

        if (filePath != null) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            StorageReference ref = storageReference.child(uidUser);
            ref.putFile(filePath)
                    .addOnSuccessListener(taskSnapshot -> {
                        DatabaseReference dbUploadImg = FirebaseDatabase.getInstance().getReference("Admin").child(uidUser);
                        progressDialog.dismiss();
                        ref.getDownloadUrl().addOnSuccessListener(uri -> {
                            Map<String, Object> profilUser = new HashMap<>();
                            profilUser.put("img_profil", String.valueOf(uri));
                            dbUploadImg.updateChildren(profilUser);
                        });
                        Toast.makeText(this, "Foto profil berhasil diubah", Toast.LENGTH_LONG).show();
                    })
                    .addOnFailureListener(e -> {
                        progressDialog.dismiss();
                        Toast.makeText(this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    })
                    .addOnProgressListener(taskSnapshot -> {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                .getTotalByteCount());
                        progressDialog.setMessage("Uploaded " + (int) progress + "%");
                    });
        } else {
            Toast.makeText(this, "Gambar belum dipilih", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, AdminMainActivity.class));
        finish();
    }

    public void Back(View view) {
        onBackPressed();
    }
}