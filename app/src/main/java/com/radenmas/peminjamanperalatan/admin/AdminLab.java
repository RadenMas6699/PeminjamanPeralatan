package com.radenmas.peminjamanperalatan.admin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.radenmas.peminjamanperalatan.R;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class AdminLab extends AppCompatActivity implements View.OnClickListener {

    Fragment fragment = null;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;

    int dataTab;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.adm_act_lab);

        TabLayout tab_lab = findViewById(R.id.tab_lab);
        FrameLayout content_lab = findViewById(R.id.content_lab);

        MaterialButton btn_add_tools = findViewById(R.id.btn_add_tools);

        fragment = new AdminFragLabMesin();
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.content_lab, fragment);
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        fragmentTransaction.commit();

        btn_add_tools.setOnClickListener(this);

        tab_lab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 1:
                        dataTab = 1;
                        fragment = new AdminFragLabInstalasi();
                        break;
                    case 2:
                        dataTab = 2;
                        fragment = new AdminFragLabInstrumentasi();
                        break;
                    default:
                        dataTab = 0;
                        fragment = new AdminFragLabMesin();
                        break;

                }
                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ft.replace(R.id.content_lab, fragment);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                ft.commit();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

    @Override
    public void onClick(View view) {
        String lab = null;
        switch (dataTab) {
            case 0:
                lab = "Lab_Mesin";
                break;
            case 1:
                lab = "Lab_Instalasi";
                break;
            case 2:
                lab = "Lab_Instrumentasi";
                break;
        }

        String lower = lab.toLowerCase().replace("_", "");

        BottomSheetDialog dialog = new BottomSheetDialog(AdminLab.this, R.style.BottomSheetDialog);
        dialog.setContentView(R.layout.bottom_add_lab);
        dialog.setCanceledOnTouchOutside(false);

        EditText etTool = dialog.findViewById(R.id.et_add_tool);
        EditText etJmlTool = dialog.findViewById(R.id.et_jml_tool);
        MaterialButton btnAddTool = dialog.findViewById(R.id.btn_add_tool);
        ImageView cancelAddTool = dialog.findViewById(R.id.cancel_add_tool);

        cancelAddTool.setOnClickListener(view1 -> dialog.dismiss());

        String finalLab = lab;
        String finalLab1 = lab;
        String finalLab2 = lab;
        btnAddTool.setOnClickListener(view1 -> {

            InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            manager.hideSoftInputFromWindow(etTool.getApplicationWindowToken(), 0);
            manager.hideSoftInputFromWindow(etJmlTool.getApplicationWindowToken(), 0);

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference(finalLab);
            reference.keepSynced(true);

            String id = reference.push().getKey();

            String name_tools = etTool.getText().toString().trim();
            String jml_tools = etJmlTool.getText().toString().trim();

            String strReplace = name_tools.replace(" ", "_").toLowerCase();
            String encode = "appsajerah." + lower + "." + strReplace;

            if (name_tools.isEmpty()) {
                etTool.setError("Masukkan Nama Alat");
            } else if (jml_tools.isEmpty()) {
                etJmlTool.setError("Masukkan Jumlah Alat");
            } else {
                int jumlah_tool = Integer.parseInt(jml_tools);

                ProgressDialog progressDialog = new ProgressDialog(AdminLab.this);
                progressDialog.show();
                progressDialog.setContentView(R.layout.progress_layout);
                progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                progressDialog.show();

                MultiFormatWriter writer = new MultiFormatWriter();
                try {
                    BitMatrix matrix = writer.encode(encode, BarcodeFormat.QR_CODE, 250, 250);
                    BarcodeEncoder encoder = new BarcodeEncoder();
                    Bitmap bitmap = encoder.createBitmap(matrix);

                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    StorageReference storageRef = storage.getReferenceFromUrl("gs://sajrahapppeminjamanalatlab.appspot.com").child(finalLab1).child(name_tools).child(id + ".jpg");

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] data = baos.toByteArray();

                    UploadTask uploadTask = storageRef.putBytes(data);
                    uploadTask.addOnSuccessListener(taskSnapshot -> {
                        storageRef.getDownloadUrl().addOnSuccessListener(uri -> {

                            Map<String, Object> nama_tool = new HashMap<>();
                            nama_tool.put("id_tool", id);
                            nama_tool.put("nama_tool", name_tools);
                            nama_tool.put("jml_tool", jumlah_tool);
                            nama_tool.put("text_qr_tool", encode);
                            nama_tool.put("type_tool", "Buah");
                            nama_tool.put("nama_lab", finalLab2.replace("_", " "));
                            nama_tool.put("link_tool", String.valueOf(uri));

                            reference.child(id).setValue(nama_tool);
                        });

                        new Handler().postDelayed(() -> {
                            progressDialog.dismiss();
                            etTool.setText("");
                            etJmlTool.setText("");
                            Toast.makeText(AdminLab.this, name_tools + " berhasil ditambahkan", Toast.LENGTH_SHORT).show();
                        }, 1000);
                    }).addOnFailureListener(e -> Toast.makeText(AdminLab.this, "" + e, Toast.LENGTH_SHORT).show());

                } catch (WriterException e) {
                    e.printStackTrace();
                }
            }
        });

        dialog.show();

    }

    public void Back(View view) {
        onBackPressed();
    }
}