package com.example.sportsgo;

import org.bson.types.ObjectId;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Ejercicios extends RealmObject {
    @PrimaryKey
    private ObjectId id = new ObjectId();
    private String nombre;
    private int image;
    private int series;
    private int repeticiones;
    private String peso;
    private String descripcion;
    private String urlVideo;
    private String nombrePupilo;
    private boolean completado;
    private String categoria;
    private String grupoMuscular;
    private boolean plantilla;

    public Ejercicios() {
    }

    public Ejercicios(ObjectId id, String nombre, int image, int series, int repeticiones, String peso, String descripcion, String urlVideo, String nombrePupilo, boolean completado, String categoria, String grupoMuscular, boolean plantilla) {
        this.id = id;
        this.nombre = nombre;
        this.image = image;
        this.series = series;
        this.repeticiones = repeticiones;
        this.peso = peso;
        this.descripcion = descripcion;
        this.urlVideo = urlVideo;
        this.nombrePupilo = nombrePupilo;
        this.completado = completado;
        this.categoria = categoria;
        this.grupoMuscular = grupoMuscular;
        this.plantilla = plantilla;
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

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public int getSeries() {
        return series;
    }

    public void setSeries(int series) {
        this.series = series;
    }

    public int getRepeticiones() {
        return repeticiones;
    }

    public void setRepeticiones(int repeticiones) {
        this.repeticiones = repeticiones;
    }

    public String getPeso() {
        return peso;
    }

    public void setPeso(String peso) {
        this.peso = peso;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getUrlVideo() {
        return urlVideo;
    }

    public void setUrlVideo(String urlVideo) {
        this.urlVideo = urlVideo;
    }

    public String getNombrePupilo() {
        return nombrePupilo;
    }

    public void setNombrePupilo(String nombrePupilo) {
        this.nombrePupilo = nombrePupilo;
    }

    public boolean isCompletado() {
        return completado;
    }

    public void setCompletado(boolean completado) {
        this.completado = completado;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getGrupoMuscular() {
        return grupoMuscular;
    }

    public void setGrupoMuscular(String grupoMuscular) {
        this.grupoMuscular = grupoMuscular;
    }

    public boolean isPlantilla() {
        return plantilla;
    }

    public void setPlantilla(boolean plantilla) {
        this.plantilla = plantilla;
    }
}






