package com.radenmas.peminjamanperalatan;

import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

public class FirebaseViewHolder extends RecyclerView.ViewHolder {

    public final TextView number, name, jumlah, lab;
    public final ImageView check, scan, popup, img_user, imgMin, imgAdd;
    public final AppCompatButton btnAddAlat;
    public final RelativeLayout listItem;
    public final LinearLayout  linearBtnAdd;
    public final EditText etJml;

    public FirebaseViewHolder(@NonNull View itemView) {
        super(itemView);

        //list history
        listItem = itemView.findViewById(R.id.listItem);
        linearBtnAdd = itemView.findViewById(R.id.linearBtnAdd);
        imgMin = itemView.findViewById(R.id.img_min);
        imgAdd = itemView.findViewById(R.id.img_add);
        etJml = itemView.findViewById(R.id.et_jml);

        number = itemView.findViewById(R.id.tv_number);
        name = itemView.findViewById(R.id.tv_name);
        jumlah = itemView.findViewById(R.id.tv_jumlah);
        lab = itemView.findViewById(R.id.tv_lab);
        check = itemView.findViewById(R.id.img_check);
        scan = itemView.findViewById(R.id.img_scan);
        popup = itemView.findViewById(R.id.menu_popup);

        img_user = itemView.findViewById(R.id.img_user);

        btnAddAlat = itemView.findViewById(R.id.btn_add_alat);
    }
}
