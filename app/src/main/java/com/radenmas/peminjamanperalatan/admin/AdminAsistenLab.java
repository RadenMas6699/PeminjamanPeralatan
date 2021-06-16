package com.radenmas.peminjamanperalatan.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.radenmas.peminjamanperalatan.CircleTransform;
import com.radenmas.peminjamanperalatan.FirebaseViewHolder;
import com.radenmas.peminjamanperalatan.Login;
import com.radenmas.peminjamanperalatan.R;
import com.radenmas.peminjamanperalatan.SetEmptyState;
import com.radenmas.peminjamanperalatan.adapter.DataRecycler;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class AdminAsistenLab extends AppCompatActivity implements View.OnClickListener, OnSuccessListener<AuthResult>, OnFailureListener {
    //    private TextInputEditText etName, etEmail, etPhone, etPassword;
    private DatabaseReference reffAsisten;
    private FirebaseRecyclerOptions<DataRecycler> options;
    private FirebaseRecyclerAdapter<DataRecycler, FirebaseViewHolder> adapter;
    private ShimmerFrameLayout shimmerAsisten;
    private String name, email, phone, pass, prefix;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    private BottomSheetDialog dialog;
//    private MaterialButton btnAsistenLab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.adm_act_asisten_lab);

        firebaseAuth = FirebaseAuth.getInstance();

        RecyclerView rv_asisten_lab = findViewById(R.id.rv_asisten_lab);
        LottieAnimationView lottieEmpty = findViewById(R.id.lottieEmpty);
        shimmerAsisten = findViewById(R.id.shimmer_asisten_lab);
        shimmerAsisten.startShimmer();

        TextView tvEmptyState = findViewById(R.id.tvEmptyState);

        reffAsisten = FirebaseDatabase.getInstance().getReference("Admin");
        reffAsisten.keepSynced(true);

        rv_asisten_lab.setHasFixedSize(true);
        rv_asisten_lab.setLayoutManager(new LinearLayoutManager(AdminAsistenLab.this));

        options = new FirebaseRecyclerOptions.Builder<DataRecycler>().setQuery(reffAsisten, DataRecycler.class).build();

        adapter = new FirebaseRecyclerAdapter<DataRecycler, FirebaseViewHolder>(options) {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            protected void onBindViewHolder(@NonNull FirebaseViewHolder firebaseViewHolder, int i, @NonNull DataRecycler dataRecycler) {
                shimmerAsisten.setVisibility(View.INVISIBLE);
                firebaseViewHolder.name.setText(dataRecycler.getName());
                firebaseViewHolder.jumlah.setText(dataRecycler.getEmail());
                Picasso.get().load(dataRecycler.getImg_profil()).error(R.drawable.ic_user_circle_black).transform(new CircleTransform()).into(firebaseViewHolder.img_user);
            }

            @NonNull
            @Override
            public FirebaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new FirebaseViewHolder(LayoutInflater.from(AdminAsistenLab.this).inflate(R.layout.list_user, parent, false));
            }
        };

        SetEmptyState setEmptyState = new SetEmptyState();
        setEmptyState.emptyState(reffAsisten, shimmerAsisten, lottieEmpty, tvEmptyState);

        adapter.notifyDataSetChanged();

        rv_asisten_lab.setAdapter(adapter);
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

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, AdminMainActivity.class));
        finish();
    }

    public void Back(View view) {
        startActivity(new Intent(this, AdminMainActivity.class));
        finish();
    }

    public void AddAsistenLab(View view) {
        dialog = new BottomSheetDialog(AdminAsistenLab.this, R.style.BottomSheetDialog);
        dialog.setContentView(R.layout.bottom_add_asisten_lab);
        dialog.setCanceledOnTouchOutside(false);

        TextInputEditText etName = dialog.findViewById(R.id.et_name_asisten_lab);
        TextInputEditText etEmail = dialog.findViewById(R.id.et_email_asisten_lab);
        TextInputEditText etPhone = dialog.findViewById(R.id.et_phone_asisten_lab);
        TextInputEditText etPassword = dialog.findViewById(R.id.et_password_asisten_lab);

        MaterialButton btnAsistenLabbb = dialog.findViewById(R.id.btnTambahAsistenLab);

        btnAsistenLabbb.setOnClickListener(view1 -> {
            InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            manager.hideSoftInputFromWindow(etName.getApplicationWindowToken(), 0);
            manager.hideSoftInputFromWindow(etEmail.getApplicationWindowToken(), 0);
            manager.hideSoftInputFromWindow(etPhone.getApplicationWindowToken(), 0);
            manager.hideSoftInputFromWindow(etPassword.getApplicationWindowToken(), 0);

            name = etName.getText().toString().trim();
            email = etEmail.getText().toString().trim();
            phone = etPhone.getText().toString().trim();
            pass = etPassword.getText().toString().trim();

            if (name.isEmpty()) {
                etName.setError("Nama Asisten Lab kosong");
            } else if (email.isEmpty()) {
                etEmail.setError("Email kosong");
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                etEmail.setError("Username tidak valid");
            } else if (phone.isEmpty()) {
                etPhone.setError("No Hp kosong");
            } else if (pass.isEmpty()) {
                etPassword.setError("Password kosong");
            } else {
                register();
            }
        });

        dialog.show();
    }

    private void register() {
        progressDialog = new ProgressDialog(AdminAsistenLab.this);
        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_layout);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        progressDialog.show();

        firebaseAuth.createUserWithEmailAndPassword(email, pass).addOnSuccessListener(this).addOnFailureListener(this);
    }

    @Override
    public void onSuccess(AuthResult authResult) {

        String id = firebaseAuth.getUid();

        char preff = phone.charAt(0);
        if (preff == '0') {
            prefix = phone.replaceFirst("0", "+62");
        } else {
            prefix = "+62".concat(phone);
        }

        Map<String, Object> adminInfo = new HashMap<>();
        adminInfo.put("name", name);
        adminInfo.put("email", email);
        adminInfo.put("img_profil", "-");
        adminInfo.put("phone", prefix);
        adminInfo.put("id", id);
        adminInfo.put("userId", "Admin");

        reffAsisten.child(id).setValue(adminInfo);

        new Handler().postDelayed(() -> {
            progressDialog.dismiss();
            dialog.dismiss();

            Toast.makeText(AdminAsistenLab.this, name + " berhasil ditambahkan", Toast.LENGTH_SHORT).show();

            FirebaseAuth.getInstance().signOut();

            FirebaseAuth.getInstance().signInWithEmailAndPassword("admin@gmail.com", "admin123");

        }, 1000);
    }

    //    @Override
    public void onFailure(@NonNull @NotNull Exception e) {
        progressDialog.setContentView(R.layout.progress_failed);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        new Handler().postDelayed(() -> progressDialog.dismiss(), 2000);
    }

    @Override
    public void onClick(View view) {

    }
}