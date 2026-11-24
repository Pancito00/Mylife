package com.example.mylife.db;

public class Tarea {
    private int id;
    private String titulo;
    private String materia;
    private String fecha;   // formato yyyy-MM-dd (así se ordena bien en SQLite)
    private boolean hecha;

    public Tarea() { }

    public Tarea(int id, String titulo, String materia, String fecha, boolean hecha) {
        this.id = id;
        this.titulo = titulo;
        this.materia = materia;
        this.fecha = fecha;
        this.hecha = hecha;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getMateria() { return materia; }
    public void setMateria(String materia) { this.materia = materia; }

    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }

    public boolean isHecha() { return hecha; }
    public void setHecha(boolean hecha) { this.hecha = hecha; }
}