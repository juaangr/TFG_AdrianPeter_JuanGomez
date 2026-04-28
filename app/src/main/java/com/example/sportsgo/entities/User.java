package com.example.sportsgo.entities;

public class User {

    private String uid;
    private String nombre;
    private String email;
    private int calorias;
    private boolean permisoCompleto;

    // Constructor vacío obligatorio para Firestore
    public User() {}

    public User(String uid, String nombre, String email) {
        this.uid = uid;
        this.nombre = nombre;
        this.email = email;
        this.calorias = 0;
        this.permisoCompleto = false;
    }

    public String getUid() { return uid; }
    public void setUid(String uid) { this.uid = uid; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public int getCalorias() { return calorias; }
    public void setCalorias(int calorias) { this.calorias = calorias; }

    public boolean isPermisoCompleto() { return permisoCompleto; }
    public void setPermisoCompleto(boolean permisoCompleto) { this.permisoCompleto = permisoCompleto; }
}