package com.example.mylife.db;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Quién tiene la sesión iniciada. Usa las mismas preferencias y claves
 * que ya guardaba LoginActivity ("MyLifeSesion").
 */
public class Sesion {

    public static final String PREFS = "MyLifeSesion";
    public static final String K_ID = "idUsuario";
    public static final String K_NOMBRE = "nombreUsuario";
    public static final String K_CORREO = "correoUsuario";

    private final SharedPreferences prefs;

    public Sesion(Context context) {
        prefs = context.getApplicationContext()
                .getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    public void iniciar(int id, String nombre, String correo) {
        prefs.edit()
                .putInt(K_ID, id)
                .putString(K_NOMBRE, nombre)
                .putString(K_CORREO, correo)
                .apply();
    }

    public boolean activa() {
        return prefs.getInt(K_ID, -1) != -1;
    }

    public int getId() {
        return prefs.getInt(K_ID, -1);
    }

    public String getNombre() {
        String n = prefs.getString(K_NOMBRE, "");
        return n == null ? "" : n;
    }

    public String getCorreo() {
        String c = prefs.getString(K_CORREO, "");
        return c == null ? "" : c;
    }

    public void setNombre(String nombre) {
        prefs.edit().putString(K_NOMBRE, nombre).apply();
    }

    public void cerrar() {
        prefs.edit().clear().apply();
    }
}
