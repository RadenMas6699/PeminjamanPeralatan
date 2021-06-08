package com.radenmas.peminjamanperalatan.user;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.radenmas.peminjamanperalatan.CircleTransform;
import com.radenmas.peminjamanperalatan.R;
import com.squareup.picasso.Picasso;


public class FragHomeUser extends Fragment implements RadioGroup.OnCheckedChangeListener {

    private TextView nameAdmin, nameUser;
    private RadioButton rb_mesin, rb_instalasi, rb_instrumentasi;
    private DatabaseReference reference;
    private ImageView imgUser;
    ImageView imgAdmin;

    Fragment fragment = null;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;

    private ShimmerFrameLayout shimmerAdmin, shimmerUser;

    public FragHomeUser() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.user_frag_home, container, false);

        nameUser = view.findViewById(R.id.name_user);
        nameAdmin = view.findViewById(R.id.nameAdmin);
        RadioGroup rgLab = view.findViewById(R.id.rg_lab);
        FrameLayout content_user = view.findViewById(R.id.content_user);
        rb_mesin = view.findViewById(R.id.rb_mesin);
        rb_instalasi = view.findViewById(R.id.rb_instalasi);
        rb_instrumentasi = view.findViewById(R.id.rb_instrumentasi);
        imgUser = view.findViewById(R.id.img_user);
        imgAdmin = view.findViewById(R.id.img_admin);

        shimmerUser = view.findViewById(R.id.shimmer_user);
        shimmerAdmin = view.findViewById(R.id.shimmer_admin);
        shimmerUser.startShimmer();
        shimmerAdmin.startShimmer();

        reference = FirebaseDatabase.getInstance().getReference();
        reference.keepSynced(true);

        getNameUser();
        getNameAdminOnline();

        rgLab.setOnCheckedChangeListener(this);

        rb_mesin.setTextColor(getResources().getColor(R.color.black));
        rb_instalasi.setTextColor(getResources().getColor(android.R.color.darker_gray));
        rb_instrumentasi.setTextColor(getResources().getColor(android.R.color.darker_gray));

        Bundle bundle = new Bundle();
        bundle.putString("lab", "Lab_Mesin");

        fragment = new ListFragTool();
        fragmentManager = getFragmentManager();
        fragment.setArguments(bundle);

        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.content_user, fragment);
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        fragmentTransaction.commit();

        return view;
    }

    private void getNameUser() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        reference.child("User").child(auth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String user = (String) snapshot.child("name").getValue();
                String img_profil = (String) snapshot.child("img_profil").getValue();
                nameUser.setText("Hai, " + user);
                Picasso.get().load(img_profil).error(R.drawable.ic_user_circle_white).transform(new CircleTransform()).into(imgUser);
                shimmerUser.setVisibility(View.INVISIBLE);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getNameAdminOnline() {
        reference.child("Online").limitToLast(1).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot item : snapshot.getChildren()) {
                        String admin = item.child("name").getValue().toString();
                        String img_profil = item.child("profil").getValue().toString();
                        nameAdmin.setText(admin);
                        Picasso.get().load(img_profil).error(R.drawable.ic_user_circle_white).transform(new CircleTransform()).into(imgAdmin);
                        shimmerAdmin.setVisibility(View.INVISIBLE);
                    }
                } else {
                    shimmerAdmin.setVisibility(View.INVISIBLE);
                    nameAdmin.setText("-");
                    Picasso.get().load(R.drawable.ic_user_circle_white).into(imgAdmin);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @SuppressLint({"NonConstantResourceId"})
    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
        Bundle bundle = new Bundle();
        switch (i) {
            case R.id.rb_instalasi:
                rb_instalasi.setTextColor(getResources().getColor(R.color.black));
                rb_mesin.setTextColor(getResources().getColor(R.color.semi_black));
                rb_instrumentasi.setTextColor(getResources().getColor(R.color.semi_black));
                fragment = new ListFragTool();

                bundle.putString("lab", "Lab_Instalasi");

                break;
            case R.id.rb_instrumentasi:
                rb_instrumentasi.setTextColor(getResources().getColor(R.color.black));
                rb_instalasi.setTextColor(getResources().getColor(R.color.semi_black));
                rb_mesin.setTextColor(getResources().getColor(R.color.semi_black));
                fragment = new ListFragTool();

                bundle.putString("lab", "Lab_Instrumentasi");

                break;
            default:
                rb_mesin.setTextColor(getResources().getColor(R.color.black));
                rb_instalasi.setTextColor(getResources().getColor(R.color.semi_black));
                rb_instrumentasi.setTextColor(getResources().getColor(R.color.semi_black));
                fragment = new ListFragTool();

                bundle.putString("lab", "Lab_Mesin");

                break;
        }

        fragment.setArguments(bundle);

        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.content_user, fragment);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.commit();
    }

}
