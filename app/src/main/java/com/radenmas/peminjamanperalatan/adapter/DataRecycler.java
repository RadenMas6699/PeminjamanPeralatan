package com.radenmas.peminjamanperalatan.adapter;

public class DataRecycler {
    //data User
    String id, email, name, nim, phone, img_profil, kelompok, userId;

    //data Kelas
    String id_kelas, nama_kelas;

    //data Tool
    String id_tool, nama_tool, link_tool, type_tool, text_qr_tool, nama_lab;
    int jml_tool;

    //data Pinjam
    int check;

    public DataRecycler() {
    }

    public DataRecycler(String id, String email, String name, String nim, String phone, String img_profil, String kelompok, String userId, String id_kelas, String nama_kelas, String id_tool, String nama_tool, String link_tool, String type_tool, String text_qr_tool, String nama_lab, int jml_tool, int check) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.nim = nim;
        this.phone = phone;
        this.img_profil = img_profil;
        this.kelompok = kelompok;
        this.userId = userId;
        this.id_kelas = id_kelas;
        this.nama_kelas = nama_kelas;
        this.id_tool = id_tool;
        this.nama_tool = nama_tool;
        this.link_tool = link_tool;
        this.type_tool = type_tool;
        this.text_qr_tool = text_qr_tool;
        this.nama_lab = nama_lab;
        this.jml_tool = jml_tool;
        this.check = check;
    }

    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getNim() {
        return nim;
    }

    public String getPhone() {
        return phone;
    }

    public String getImg_profil() {
        return img_profil;
    }

    public String getKelompok() {
        return kelompok;
    }

    public String getUserId() {
        return userId;
    }

    public String getId_kelas() {
        return id_kelas;
    }

    public String getNama_kelas() {
        return nama_kelas;
    }

    public String getId_tool() {
        return id_tool;
    }

    public String getNama_tool() {
        return nama_tool;
    }

    public String getLink_tool() {
        return link_tool;
    }

    public String getType_tool() {
        return type_tool;
    }

    public String getText_qr_tool() {
        return text_qr_tool;
    }

    public String getNama_lab() {
        return nama_lab;
    }

    public int getJml_tool() {
        return jml_tool;
    }

    public int getCheck() {
        return check;
    }
}
