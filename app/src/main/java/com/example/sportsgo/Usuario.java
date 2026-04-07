package com.example.sportsgo;

import org.bson.types.ObjectId;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Usuario extends RealmObject {
    @PrimaryKey
    private ObjectId id = new ObjectId();

    private String nombre;
    private String email;
    private String password;
    private int edad;
    private String rol; //Aqui se guarda si es trainer o pupilo
    private double peso;
    private double altura;
    private int calorias;
    private boolean permisoCompleto; // Aqui almacenamos el permiso que haya aceptado el Pupilo

    //Constructor vacio para el uso de Realm
    public Usuario() {
    }


    //Constructor para crear los usuarios
    public Usuario(String nombre, String rol) {
        this.nombre = nombre;
        this.rol = rol;
    }
    public double getPeso() {
        return peso;
    }

    public void setPeso(double peso) {
        this.peso = peso;
    }

    public double getAltura() {
        return altura;
    }

    public void setAltura(double altura) {
        this.altura = altura;
    }

    public int getCalorias() {
        return calorias;
    }

    public void setCalorias(int calorias) {
        this.calorias = calorias;
    }

    public boolean isPermisoCompleto() {
        return permisoCompleto;
    }

    public void setPermisoCompleto(boolean permisoCompleto) {
        this.permisoCompleto = permisoCompleto;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getEdad() {
        return edad;
    }

    public void setEdad(int edad) {
        this.edad = edad;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }
}
