package com.example.mylife.db;

public class Habito {
    private int id;
    private String nombre;
    private String icono;       // emoji
    private int diasCumplidos;

    public Habito() { }

    public Habito(int id, String nombre, String icono, int diasCumplidos) {
        this.id = id;
        this.nombre = nombre;
        this.icono = icono;
        this.diasCumplidos = diasCumplidos;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getIcono() { return icono; }
    public void setIcono(String icono) { this.icono = icono; }

    public int getDiasCumplidos() { return diasCumplidos; }
    public void setDiasCumplidos(int diasCumplidos) { this.diasCumplidos = diasCumplidos; }
}