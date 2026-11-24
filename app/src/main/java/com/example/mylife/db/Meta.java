
package com.example.mylife.db;

public class Meta {

    private int id;
    private String titulo;
    private String descripcion;
    private boolean cumplida;
    private String fechaObjetivo;

    public Meta() {
    }

    // Constructor anterior para mantener compatibilidad.
    public Meta(int id, String titulo, String descripcion,
                boolean cumplida) {

        this(id, titulo, descripcion, cumplida, null);
    }

    // Nuevo constructor con fecha.
    public Meta(int id, String titulo, String descripcion,
                boolean cumplida, String fechaObjetivo) {

        this.id = id;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.cumplida = cumplida;
        this.fechaObjetivo = fechaObjetivo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public boolean isCumplida() {
        return cumplida;
    }

    public void setCumplida(boolean cumplida) {
        this.cumplida = cumplida;
    }

    public String getFechaObjetivo() {
        return fechaObjetivo;
    }

    public void setFechaObjetivo(String fechaObjetivo) {
        this.fechaObjetivo = fechaObjetivo;
    }
}
