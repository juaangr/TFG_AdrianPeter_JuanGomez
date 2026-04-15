package com.example.sportsgo.entities;

public class Trainer {

    private String email;
    private String password;
    private String nombre;

    // por ejemplo fuerza/hipertrofia - cardio/atletas... idea....
    private String especialidad;

    public Trainer() {
    }

    public Trainer(String email, String password, String nombre) {
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

    public String getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(String especialidad) {
        this.especialidad = especialidad;
    }
}
