package com.example.sportsgo.entities;
// clase para firebase (firestore) creo que nos quedaremos con
// esta clase antes que la que está fuera de este package
public class User {
    private String email;
    private String password;
    private String nombre;
    private double peso;
    private double altura;


    public User() {
    }


    public User(String email, String password, String nombre) {
        this.email = email;
        this.password = password;
        this.nombre = nombre;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
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
}
