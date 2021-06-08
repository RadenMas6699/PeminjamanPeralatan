package com.radenmas.peminjamanperalatan.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.radenmas.peminjamanperalatan.CircleTransform;
import com.radenmas.peminjamanperalatan.R;
import com.squareup.picasso.Picasso;

public class AdminPreviewDataMhs extends AppCompatActivity {
    private ImageView img_user;
    private TextView name_user;
    private TextView nim_user_desc;
    private TextView kelas_user_desc;
    private TextView kelompok_user_desc;
    private TextView email_user_desc;
    private TextView phone_user_desc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.adm_act_preview_data_mhs);

        String uidUser = getIntent().getExtras().getString("uidUser");

        img_user = findViewById(R.id.img_user);
        name_user = findViewById(R.id.name_user);

        nim_user_desc = findViewById(R.id.nim_user_desc);
        kelas_user_desc = findViewById(R.id.kelas_user_desc);
        kelompok_user_desc = findViewById(R.id.kelompok_user_desc);
        email_user_desc = findViewById(R.id.email_user_desc);
        phone_user_desc = findViewById(R.id.phone_user_desc);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("User").child(uidUser);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String user = (String) snapshot.child("name").getValue();
                String nim = (String) snapshot.child("nim").getValue();
                String kelas = (String) snapshot.child("nama_kelas").getValue();
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

    public void Back(View view) {
        onBackPressed();
    }
}