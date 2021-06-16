package com.radenmas.peminjamanperalatan.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.radenmas.peminjamanperalatan.About;
import com.radenmas.peminjamanperalatan.CircleTransform;
import com.radenmas.peminjamanperalatan.R;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class AdminMainActivity extends AppCompatActivity {

    private TextView titleAdmin, tvJmlAsistenLab, tvJmlKelas, tvJmlBioMhs, tvJmlLab, tvJmlPinjam;
    private ImageView imgAdmin;
    private SwitchMaterial switch_online;
    private ShimmerFrameLayout shimmer_menu_admin, shimmer_img_admin;
    private DatabaseReference reference;
    private SharedPreferences myaccount;
    private SharedPreferences.Editor editor;
    private CardView card_asisten_lab;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.adm_act_main);

        reference = FirebaseDatabase.getInstance().getReference();
        reference.keepSynced(true);

        card_asisten_lab = findViewById(R.id.card_asisten_lab);

        myaccount = getSharedPreferences("myAccount", Context.MODE_PRIVATE);
        editor = myaccount.edit();
        editor.putString("userType", "admin");
        editor.apply();

        initView();

        getDataAdmin();

        setKondisiSwitch();

        ShowJumlahData("Admin", tvJmlAsistenLab, "Asisten Lab");
        ShowJumlahData("Kelas", tvJmlKelas, "Kelas");
        ShowJumlahData("User", tvJmlBioMhs, "Mahasiswa");
        ShowJumlahData("Pinjam", tvJmlPinjam, "Mahasiswa");
        //show Data Lab
        tvJmlLab.setText("3 Laboratorium");
    }

    private void setKondisiSwitch() {

        //get kondisi switch
        String switchOnline = myaccount.getString("switch", "");

        //set switch from last kondisi
        switch_online.setChecked(switchOnline.equals("on"));

        FirebaseAuth auth = FirebaseAuth.getInstance();
        String id_user = auth.getCurrentUser().getUid();

        //set switch on click
        switch_online.setOnCheckedChangeListener((compoundButton, b) -> {
            if (switch_online.isChecked()) {
                editor.putString("switch", "on");

                String nameAdmin = myaccount.getString("nameAdmin", "");
                String profilAdmin = myaccount.getString("profilAdmin", "");

                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Map<String, Object> nama_tool = new HashMap<>();
                        nama_tool.put("name", nameAdmin);
                        nama_tool.put("profil", profilAdmin);
                        reference.child("Online").child(id_user).setValue(nama_tool);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            } else {
                DatabaseReference online = FirebaseDatabase.getInstance().getReference("Online").child(id_user);
                online.removeValue();
                editor.putString("switch", "off");
            }
            editor.apply();
        });
    }

    private void getDataAdmin() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String id_user = user.getUid();
        if (id_user.equals("i0hVD6xewwdMj561c9H2OIk0rq13")) {
            card_asisten_lab.setVisibility(View.VISIBLE);
        } else {
            card_asisten_lab.setVisibility(View.GONE);
        }

        reference.child("Admin").child(id_user).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String name = snapshot.child("name").getValue().toString();
                    email = snapshot.child("email").getValue().toString();
                    String img_profil = snapshot.child("img_profil").getValue().toString();

                    titleAdmin.setText("Hai, " + name);
                    Picasso.get().load(img_profil).error(R.drawable.ic_user_circle_black).transform(new CircleTransform()).into(imgAdmin);
                    shimmer_img_admin.setVisibility(View.INVISIBLE);

                    editor.putString("nameAdmin", name);
                    editor.putString("emailAdmin", email);
                    editor.putString("profilAdmin", img_profil);
                    editor.apply();
                } else {
                    shimmer_img_admin.setVisibility(View.INVISIBLE);
                    titleAdmin.setText("Hai, ");
                    Picasso.get().load(R.drawable.ic_user_circle_black).transform(new CircleTransform()).into(imgAdmin);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void initView() {
        switch_online = findViewById(R.id.switch_online);
        shimmer_menu_admin = findViewById(R.id.shimmer_menu_admin);
        shimmer_img_admin = findViewById(R.id.shimmer_img_admin);
        shimmer_menu_admin.startShimmer();
        shimmer_img_admin.startShimmer();

        titleAdmin = findViewById(R.id.title_admin);
        imgAdmin = findViewById(R.id.img_admin);
        ImageView imgAbout = findViewById(R.id.img_about);
        tvJmlAsistenLab = findViewById(R.id.tv_asisten_lab);
        tvJmlKelas = findViewById(R.id.tv_jml_kelas);
        tvJmlBioMhs = findViewById(R.id.tv_jml_bio_mhs);
        tvJmlLab = findViewById(R.id.tv_jml_lab);
        tvJmlPinjam = findViewById(R.id.tv_jml_pinjam);

    }

    private void ShowJumlahData(String path, TextView textView, String desc) {
        reference.child(path).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String getData = String.valueOf(snapshot.getChildrenCount());
                textView.setText(getData + " " + desc);
                tvJmlLab.setVisibility(View.VISIBLE);
                shimmer_menu_admin.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void Account(View view) {
        startActivity(new Intent(AdminMainActivity.this, AdminAccount.class));
        finish();
    }

    public void AboutApp(View view) {
        startActivity(new Intent(AdminMainActivity.this, About.class));
    }

    public void KelasMhs(View view) {
        startActivity(new Intent(AdminMainActivity.this, AdminKelas.class));
    }

    public void BioMhs(View view) {
        startActivity(new Intent(AdminMainActivity.this, AdminBioMhs.class));
    }

    public void Lab(View view) {
        startActivity(new Intent(AdminMainActivity.this, AdminLab.class));
    }

    public void DataPinjam(View view) {
        startActivity(new Intent(AdminMainActivity.this, AdminDataPeminjam.class));
    }

    public void AsistenLab(View view) {
        startActivity(new Intent(AdminMainActivity.this, AdminAsistenLab.class));
        finish();
    }
}