package com.radenmas.peminjamanperalatan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.radenmas.peminjamanperalatan.adapter.DataRecycler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity implements View.OnClickListener, OnSuccessListener<AuthResult>, OnFailureListener {
    private TextInputEditText etFullnameRegister, etNimRegister, etKelompokRegister, etPhoneRegister, etUsernameRegister, etPasswordRegister;
    private MaterialAutoCompleteTextView etClassRegister;
    private ProgressDialog progressDialog;

    private FirebaseAuth auth;
    private FirebaseUser firebaseUser;

    private ArrayAdapter<String> adapter;
    private ArrayList<String> listKelas;

    private String name;
    private String nim;
    private String kelas;
    private String phone;
    private String kelompok;
    private String user;
    private String pass;
    private final Handler handler = new Handler();
    private final Handler handler1 = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.act_register);

        etFullnameRegister = findViewById(R.id.etFullnameRegister);
        etNimRegister = findViewById(R.id.etNimRegister);
        etClassRegister = findViewById(R.id.etClassRegister);
        etKelompokRegister = findViewById(R.id.etKelompokRegister);
        etPhoneRegister = findViewById(R.id.etPhoneRegister);
        etUsernameRegister = findViewById(R.id.etUsernameRegister);
        etPasswordRegister = findViewById(R.id.etPasswordRegister);
        MaterialButton btnRegister = findViewById(R.id.btnRegister);
        TextView tvLogin = findViewById(R.id.tvLogin);

        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();

        getDataKelas();

        btnRegister.setOnClickListener(this);
        tvLogin.setOnClickListener(this);

    }

    private void getDataKelas() {
        //Get Data Kelas to EditText Kelas
        DatabaseReference dbReffKelas = FirebaseDatabase.getInstance().getReference("Kelas");

        dbReffKelas.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot item : snapshot.getChildren()) {
                    DataRecycler data = item.getValue(DataRecycler.class);
                    listKelas.add(data.getNama_kelas());
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        listKelas = new ArrayList<>();
        adapter = new ArrayAdapter<>(Register.this, android.R.layout.simple_list_item_1, listKelas);
        etClassRegister.setAdapter(adapter);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnRegister:
                register();
                break;
            case R.id.tvLogin:
                onBackPressed();
                break;
        }
    }


    private void register() {
        name = etFullnameRegister.getText().toString().trim();
        nim = etNimRegister.getText().toString().trim();
        kelas = etClassRegister.getText().toString();
        kelompok = etKelompokRegister.getText().toString().trim();
        phone = etPhoneRegister.getText().toString().trim();
        user = etUsernameRegister.getText().toString().trim();
        pass = etPasswordRegister.getText().toString().trim();

        if (name.isEmpty()) {
            etFullnameRegister.setError("Nama kosong");
            etFullnameRegister.requestFocus();
        } else if (nim.isEmpty()) {
            etNimRegister.setError("NIM kosong");
            etNimRegister.requestFocus();
        } else if (kelas.isEmpty()) {
            etClassRegister.setError("Kelas kosong");
            etClassRegister.requestFocus();
        } else if (kelompok.isEmpty()) {
            etKelompokRegister.setError("Kelompok kosong");
            etKelompokRegister.requestFocus();
        } else if (phone.isEmpty()) {
            etPhoneRegister.setError("No Hp kosong");
            etPhoneRegister.requestFocus();
        } else if (user.isEmpty()) {
            etUsernameRegister.setError("Username kosong");
            etUsernameRegister.requestFocus();
        } else if (!Patterns.EMAIL_ADDRESS.matcher(user).matches()) {
            etUsernameRegister.setError("Username tidak valid");
            etUsernameRegister.requestFocus();
        } else if (pass.isEmpty()) {
            etPasswordRegister.setError("Password kosong");
            etPasswordRegister.requestFocus();
        } else {
            InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            manager.hideSoftInputFromWindow(etFullnameRegister.getApplicationWindowToken(), 0);
            manager.hideSoftInputFromWindow(etNimRegister.getApplicationWindowToken(), 0);
            manager.hideSoftInputFromWindow(etClassRegister.getApplicationWindowToken(), 0);
            manager.hideSoftInputFromWindow(etKelompokRegister.getApplicationWindowToken(), 0);
            manager.hideSoftInputFromWindow(etPhoneRegister.getApplicationWindowToken(), 0);
            manager.hideSoftInputFromWindow(etUsernameRegister.getApplicationWindowToken(), 0);
            manager.hideSoftInputFromWindow(etPasswordRegister.getApplicationWindowToken(), 0);

            //register
            progressDialog = new ProgressDialog(Register.this);
            progressDialog.show();
            progressDialog.setContentView(R.layout.progress_layout);
            progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

            auth.createUserWithEmailAndPassword(user, pass).addOnSuccessListener(this).addOnFailureListener(this);
        }
    }

    @Override
    public void onSuccess(AuthResult authResult) {
        progressDialog.setContentView(R.layout.progress_successful);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String uid = firebaseUser.getUid();

        DatabaseReference dbUser = FirebaseDatabase.getInstance().getReference("User").child(uid);
        DatabaseReference dbKelas = FirebaseDatabase.getInstance().getReference(kelas).child(uid);

        char preff = phone.charAt(0);
        String prefix;
        if (preff == '0') {
            prefix = phone.replaceFirst("0", "+62");
        } else {
            prefix = "+62".concat(phone);
        }

        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("name", name);
        userInfo.put("nama_kelas", kelas);
        userInfo.put("kelompok", "Kelompok " + kelompok);
        userInfo.put("email", user);
        userInfo.put("password", pass);
        userInfo.put("img_profil", "-");
        userInfo.put("nim", nim);
        userInfo.put("phone", prefix);
        userInfo.put("id", uid);
        userInfo.put("userId", "Mahasiswa");

        dbUser.setValue(userInfo);

        Map<String, Object> userKelas = new HashMap<>();
        userKelas.put("id", uid);
        userKelas.put("name", name);
        userKelas.put("img_profil", "-");
        userKelas.put("nim", nim);
        dbKelas.setValue(userKelas);

        handler.postDelayed(() -> {
            progressDialog.dismiss();
            handler1.postDelayed(this::onBackPressed, 500);
        }, 500);
    }

    @Override
    public void onFailure(@NonNull Exception e) {
        progressDialog.setContentView(R.layout.progress_failed);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        handler.postDelayed(() -> progressDialog.dismiss(), 2000);
    }

}