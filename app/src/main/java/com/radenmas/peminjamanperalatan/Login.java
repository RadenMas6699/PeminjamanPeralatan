package com.radenmas.peminjamanperalatan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.chivorn.smartmaterialspinner.SmartMaterialSpinner;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.radenmas.peminjamanperalatan.admin.AdminMainActivity;
import com.radenmas.peminjamanperalatan.user.UserMainActivity;

import java.util.ArrayList;
import java.util.List;

public class Login extends AppCompatActivity implements View.OnClickListener, OnSuccessListener<AuthResult>, OnFailureListener {
    private TextInputEditText etUsernameLogin, etPasswordLogin;
    private FirebaseUser user;
    private ProgressDialog progressDialog;

    private DatabaseReference databaseReferenceUid;

    private String userId;
    private final Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_login);

        etUsernameLogin = findViewById(R.id.etUsernameLogin);
        etPasswordLogin = findViewById(R.id.etPasswordLogin);
        MaterialButton btnLogin = findViewById(R.id.btnLogin);
        LinearLayout llRegister = findViewById(R.id.llRegister);

        user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {

            SharedPreferences myaccount = this.getSharedPreferences("myAccount", Context.MODE_PRIVATE);
            String userType = myaccount.getString("userType", "");

            if (userType.equals("admin")) {
                startActivity(new Intent(Login.this, AdminMainActivity.class));
                finish();
            } else if (userType.equals("user")) {
                startActivity(new Intent(Login.this, UserMainActivity.class));
                finish();
            }

        }

        btnLogin.setOnClickListener(this);
        llRegister.setOnClickListener(this);


        SmartMaterialSpinner spLoginAs = findViewById(R.id.spLoginAs);
        List<String> userLevel = new ArrayList<>();
        userLevel.add("Asisten Lab");
        userLevel.add("Mahasiswa");

        spLoginAs.setItem(userLevel);

        spLoginAs.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                userId = "" + position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnLogin:
                login();
                break;
            case R.id.llRegister:
                startActivity(new Intent(Login.this, Register.class));
                break;
        }
    }

    private void login() {
        String user = etUsernameLogin.getText().toString();
        String pass = etPasswordLogin.getText().toString();

        InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(etUsernameLogin.getApplicationWindowToken(), 0);
        manager.hideSoftInputFromWindow(etPasswordLogin.getApplicationWindowToken(), 0);

        etUsernameLogin.clearFocus();
        etPasswordLogin.clearFocus();

        if (user.isEmpty()) {
            etUsernameLogin.setError("Username kosong");
        } else if (!Patterns.EMAIL_ADDRESS.matcher(user).matches()) {
            etUsernameLogin.setError("Username tidak valid");
        } else if (pass.isEmpty()) {
            etPasswordLogin.setError("Password kosong");
        } else if (userId == null) {
            Toast.makeText(this, "Pilih tipe user", Toast.LENGTH_SHORT).show();
        } else {
            //login
            progressDialog = new ProgressDialog(Login.this);
            progressDialog.show();
            progressDialog.setContentView(R.layout.progress_layout);
            progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

            FirebaseAuth.getInstance().signInWithEmailAndPassword(user, pass).addOnSuccessListener(this).addOnFailureListener(this);
        }
    }

    @Override
    public void onSuccess(AuthResult authResult) {
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        switch (userId) {
            case "0":
                databaseReferenceUid = FirebaseDatabase.getInstance().getReference("Admin").child(authResult.getUser().getUid());
                break;
            case "1":
                databaseReferenceUid = FirebaseDatabase.getInstance().getReference("User").child(authResult.getUser().getUid());
                break;
        }

        databaseReferenceUid.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String userUid = String.valueOf(snapshot.child("userId").getValue());

                if (userUid.equals("Admin")) {
                    progressDialog.setContentView(R.layout.progress_successful);
                    handler.postDelayed(() -> {
                        progressDialog.dismiss();
                        startActivity(new Intent(Login.this, AdminMainActivity.class));
                        finish();
                    }, 2000);
                } else if (userUid.equals("Mahasiswa")) {
                    progressDialog.setContentView(R.layout.progress_successful);
                    handler.postDelayed(() -> {
                        progressDialog.dismiss();
                        startActivity(new Intent(Login.this, UserMainActivity.class));
                        finish();
                    }, 2000);
                } else {
                    progressDialog.setContentView(R.layout.progress_failed);
                    handler.postDelayed(() -> progressDialog.dismiss(), 2000);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public void onFailure(@NonNull Exception e) {
        progressDialog.setContentView(R.layout.progress_failed);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        handler.postDelayed(() -> progressDialog.dismiss(), 2000);
    }

}