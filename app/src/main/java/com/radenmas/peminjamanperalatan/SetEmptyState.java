package com.radenmas.peminjamanperalatan;

import android.view.View;

import androidx.annotation.NonNull;

import com.airbnb.lottie.LottieAnimationView;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

public class SetEmptyState {

    public SetEmptyState() {
    }

    public void emptyState(DatabaseReference reference, ShimmerFrameLayout shimmer, LottieAnimationView lottie) {
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                int count = (int) snapshot.getChildrenCount();
                if (count == 0) {
                    shimmer.setVisibility(View.INVISIBLE);
                    lottie.setVisibility(View.VISIBLE);
                } else {
                    shimmer.setVisibility(View.INVISIBLE);
                    lottie.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }
}
