package com.radenmas.peminjamanperalatan.user;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.radenmas.peminjamanperalatan.R;

public class UserMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_act_main);

        SharedPreferences myaccount = getSharedPreferences("myAccount", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = myaccount.edit();
        editor.putString("userType", "user");
        editor.apply();

        navBottom();
    }

    private void navBottom() {
        BottomNavigationView navigationView = findViewById(R.id.navBottom);

        navigationView.getOrCreateBadge(R.id.navHistory);
        final BadgeDrawable badgeHistory = navigationView.getBadge(R.id.navHistory);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        DatabaseReference reff = FirebaseDatabase.getInstance().getReference("Pinjam").child(auth.getCurrentUser().getUid());
        reff.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int getCount = (int) snapshot.getChildrenCount();
                if (getCount != 0) {
                    badgeHistory.setVisible(true);
                    badgeHistory.setNumber(getCount);
                } else {
                    badgeHistory.setVisible(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        navigationView.setItemIconTintList(null);
        navigationView.setOnNavigationItemSelectedListener(item -> {
            Fragment selectedFragment = null;

            switch (item.getItemId()) {
                case R.id.navHome:
                    selectedFragment = new FragHomeUser();
                    break;
                case R.id.navHistory:
                    selectedFragment = new FragHistoryUser();
                    break;
                case R.id.navAccount:
                    selectedFragment = new FragAccountUser();
                    break;
            }

            getSupportFragmentManager().beginTransaction().replace(R.id.content, selectedFragment).commit();
            return true;
        });

        getSupportFragmentManager().beginTransaction().replace(R.id.content, new FragHomeUser()).commit();
    }

}