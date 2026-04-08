package com.example.sportsgo.entities;

public class Admin {

    private String uid;
    private String nombre;
    private String email;

    // Constructor vacío obligatorio para Firestore
    public Admin() {}

    public Admin(String uid, String nombre, String email) {
        this.uid = uid;
        this.nombre = nombre;
        this.email = email;
    }

    public String getUid() { return uid; }
    public void setUid(String uid) { this.uid = uid; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}