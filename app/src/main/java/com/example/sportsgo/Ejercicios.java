package com.example.sportsgo;

public class Ejercicios {

    private String nombre;
    private int imagen;
    private int series;
    private int repeticiones;
    private String peso;

    public Ejercicios(String nombre, int imagen, int series, int repeticiones, String peso) {
        this.nombre = nombre;
        this.imagen = imagen;
        this.series = series;
        this.repeticiones = repeticiones;
        this.peso = peso;
    }

    public String getNombre() { return nombre; }
    public int getImagen() { return imagen; }
    public int getSeries() { return series; }
    public int getRepeticiones() { return repeticiones; }
    public String getPeso() { return peso; }
}


