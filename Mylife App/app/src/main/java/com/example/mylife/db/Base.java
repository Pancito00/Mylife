package com.example.mylife.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

public class Base extends SQLiteOpenHelper {

    // ====================== CONFIGURACIÓN ======================

    public static final int VERSION_BD = 4;

    // Filtros (tareas y metas)
    public static final int TODAS = -1;
    public static final int PENDIENTES = 0;   // en metas: en progreso
    public static final int HECHAS = 1;       // en metas: cumplidas

    private static final String NOMBRE_BD = "mylife";

    private static final String TABLA_USUARIOS = "usuarios";
    private static final String TABLA_TAREAS = "tareas";
    private static final String TABLA_HABITOS = "habitos";
    private static final String TABLA_METAS = "metas";
    private static final String TABLA_REGISTROS = "habito_registros";

    private final Context contexto;

    // Uso: new Base(this)
    public Base(Context context) {
        super(context, NOMBRE_BD, null, VERSION_BD);
        this.contexto = context.getApplicationContext();
    }

    // Constructor anterior, por compatibilidad con llamadas existentes
    public Base(Context context, String name,
                SQLiteDatabase.CursorFactory factory, int version) {
        this(context);
    }

    // Id del usuario que tiene la sesión iniciada.
    // Todos los datos (tareas, hábitos, metas) pertenecen a un usuario.
    private int idUsuario() {
        return new Sesion(contexto).getId();
    }

    private String idUsuarioTexto() {
        return String.valueOf(idUsuario());
    }

    // ====================== CREACIÓN DE BD ======================

    @Override
    public void onCreate(SQLiteDatabase db) {

        crearTablaUsuarios(db);

        db.execSQL("CREATE TABLE tareas (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "id_usuario INTEGER NOT NULL DEFAULT 0, " +
                "titulo TEXT, " +
                "materia TEXT, " +
                "fecha TEXT, " +
                "hecha INTEGER DEFAULT 0)");

        db.execSQL("CREATE TABLE habitos (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "id_usuario INTEGER NOT NULL DEFAULT 0, " +
                "nombre TEXT, " +
                "icono TEXT, " +
                "dias_cumplidos INTEGER DEFAULT 0)");

        db.execSQL("CREATE TABLE metas (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "id_usuario INTEGER NOT NULL DEFAULT 0, " +
                "titulo TEXT, " +
                "descripcion TEXT, " +
                "cumplida INTEGER DEFAULT 0, " +
                "fecha_objetivo TEXT)");

        crearTablaRegistros(db);
    }

    // ====================== ACTUALIZACIÓN DE BD ======================

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        // Versión 1 -> 2
        if (oldVersion < 2) {

            if (!columnaExiste(db, TABLA_METAS, "fecha_objetivo")) {
                db.execSQL("ALTER TABLE metas ADD COLUMN fecha_objetivo TEXT");
            }

            crearTablaRegistros(db);
        }

        // Versión 2 -> 3
        if (oldVersion < 3) {
            crearTablaUsuarios(db);
        }

        // Versión 3 -> 4: los datos ahora pertenecen a cada usuario
        if (oldVersion < 4) {
            agregarColumnaUsuario(db);
        }
    }

    private void crearTablaUsuarios(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE IF NOT EXISTS usuarios (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "nombre TEXT NOT NULL, " +
                "correo TEXT NOT NULL UNIQUE, " +
                "contrasena TEXT NOT NULL)");
    }

    private void crearTablaRegistros(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE IF NOT EXISTS habito_registros (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "id_habito INTEGER NOT NULL, " +
                "id_usuario INTEGER NOT NULL DEFAULT 0, " +
                "fecha TEXT NOT NULL, " +
                "UNIQUE(id_habito, fecha))");

        db.execSQL("CREATE INDEX IF NOT EXISTS idx_registros_fecha " +
                "ON habito_registros(fecha)");
    }

    private void agregarColumnaUsuario(SQLiteDatabase db) {

        String[] tablas = {
                TABLA_TAREAS, TABLA_HABITOS, TABLA_METAS, TABLA_REGISTROS
        };

        for (String tabla : tablas) {

            if (!columnaExiste(db, tabla, "id_usuario")) {
                db.execSQL("ALTER TABLE " + tabla +
                        " ADD COLUMN id_usuario INTEGER NOT NULL DEFAULT 0");
            }

            // Lo que ya existía pasa al primer usuario registrado.
            db.execSQL("UPDATE " + tabla +
                    " SET id_usuario = COALESCE(" +
                    "(SELECT MIN(id) FROM usuarios), 0) " +
                    "WHERE id_usuario = 0");
        }
    }

    private boolean columnaExiste(SQLiteDatabase db,
                                  String tabla,
                                  String columna) {

        Cursor c = db.rawQuery("PRAGMA table_info(" + tabla + ")", null);

        try {
            while (c.moveToNext()) {
                if (columna.equals(c.getString(
                        c.getColumnIndexOrThrow("name")))) {
                    return true;
                }
            }
            return false;
        } finally {
            c.close();
        }
    }

    // ====================== UTILIDADES ======================

    private String obtenerFechaActual() {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.US)
                .format(new Date());
    }

    // Fechas (yyyy-MM-dd) de un mes que tienen algo registrado.
    // El mes se recibe de 1 a 12.
    private ArrayList<String> consultarFechasMes(String tabla,
                                                 String columna,
                                                 int anio, int mes) {

        ArrayList<String> fechas = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        Calendar calendario = Calendar.getInstance();
        calendario.set(anio, mes - 1, 1);
        int ultimoDia = calendario.getActualMaximum(Calendar.DAY_OF_MONTH);

        String inicio = String.format(Locale.US, "%04d-%02d-01", anio, mes);
        String fin = String.format(Locale.US, "%04d-%02d-%02d",
                anio, mes, ultimoDia);

        Cursor c = db.rawQuery(
                "SELECT DISTINCT " + columna + " FROM " + tabla +
                        " WHERE id_usuario = ? AND " + columna +
                        " BETWEEN ? AND ? ORDER BY " + columna + " ASC",
                new String[]{idUsuarioTexto(), inicio, fin}
        );

        try {
            while (c.moveToNext()) {
                fechas.add(c.getString(0));
            }
        } finally {
            c.close();
        }

        return fechas;
    }

    // ====================== TAREAS ======================

    public long insertarTarea(Tarea t) {

        SQLiteDatabase db = getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put("id_usuario", idUsuario());
        cv.put("titulo", t.getTitulo());
        cv.put("materia", t.getMateria());
        cv.put("fecha", t.getFecha());
        cv.put("hecha", t.isHecha() ? 1 : 0);

        return db.insert(TABLA_TAREAS, null, cv);
    }

    public ArrayList<Tarea> listarTareas(int filtro) {

        ArrayList<Tarea> lista = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        Cursor c;

        if (filtro == TODAS) {
            c = db.rawQuery(
                    "SELECT id, titulo, materia, fecha, hecha FROM tareas " +
                            "WHERE id_usuario = ? ORDER BY hecha ASC, fecha ASC",
                    new String[]{idUsuarioTexto()}
            );
        } else {
            c = db.rawQuery(
                    "SELECT id, titulo, materia, fecha, hecha FROM tareas " +
                            "WHERE id_usuario = ? AND hecha = ? " +
                            "ORDER BY fecha ASC",
                    new String[]{idUsuarioTexto(), String.valueOf(filtro)}
            );
        }

        try {
            while (c.moveToNext()) {
                lista.add(new Tarea(
                        c.getInt(0),
                        c.getString(1),
                        c.getString(2),
                        c.getString(3),
                        c.getInt(4) == 1
                ));
            }
        } finally {
            c.close();
        }

        return lista;
    }

    public int actualizarTarea(Tarea t) {

        SQLiteDatabase db = getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put("titulo", t.getTitulo());
        cv.put("materia", t.getMateria());
        cv.put("fecha", t.getFecha());
        cv.put("hecha", t.isHecha() ? 1 : 0);

        return db.update(TABLA_TAREAS, cv, "id = ?",
                new String[]{String.valueOf(t.getId())});
    }

    public int cambiarEstadoTarea(int id, boolean hecha) {

        SQLiteDatabase db = getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put("hecha", hecha ? 1 : 0);

        return db.update(TABLA_TAREAS, cv, "id = ?",
                new String[]{String.valueOf(id)});
    }

    public int eliminarTarea(int id) {

        SQLiteDatabase db = getWritableDatabase();

        return db.delete(TABLA_TAREAS, "id = ?",
                new String[]{String.valueOf(id)});
    }

    public int eliminarTareasHechas() {

        SQLiteDatabase db = getWritableDatabase();

        return db.delete(TABLA_TAREAS, "hecha = 1 AND id_usuario = ?",
                new String[]{idUsuarioTexto()});
    }

    public ArrayList<String> listarFechasTareasMes(int anio, int mes) {
        return consultarFechasMes(TABLA_TAREAS, "fecha", anio, mes);
    }

    // ====================== HÁBITOS ======================

    public long insertarHabito(Habito h) {

        SQLiteDatabase db = getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put("id_usuario", idUsuario());
        cv.put("nombre", h.getNombre());
        cv.put("icono", h.getIcono());
        cv.put("dias_cumplidos", h.getDiasCumplidos());

        return db.insert(TABLA_HABITOS, null, cv);
    }

    public ArrayList<Habito> listarHabitos() {

        ArrayList<Habito> lista = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        Cursor c = db.rawQuery(
                "SELECT id, nombre, icono, dias_cumplidos FROM habitos " +
                        "WHERE id_usuario = ? ORDER BY nombre ASC",
                new String[]{idUsuarioTexto()}
        );

        try {
            while (c.moveToNext()) {
                lista.add(new Habito(
                        c.getInt(0),
                        c.getString(1),
                        c.getString(2),
                        c.getInt(3)
                ));
            }
        } finally {
            c.close();
        }

        return lista;
    }

    public int actualizarHabito(Habito h) {

        SQLiteDatabase db = getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put("nombre", h.getNombre());
        cv.put("icono", h.getIcono());

        return db.update(TABLA_HABITOS, cv, "id = ?",
                new String[]{String.valueOf(h.getId())});
    }

    // Registra el cumplimiento del hábito en la fecha actual.
    // Devuelve true si se registró; false si ya estaba marcado hoy.
    public boolean sumarDiaHabito(int id) {

        SQLiteDatabase db = getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put("id_habito", id);
        cv.put("id_usuario", idUsuario());
        cv.put("fecha", obtenerFechaActual());

        long resultado = db.insertWithOnConflict(
                TABLA_REGISTROS, null, cv,
                SQLiteDatabase.CONFLICT_IGNORE
        );

        // Solo aumenta el contador si realmente se insertó el registro.
        if (resultado != -1) {
            db.execSQL("UPDATE habitos " +
                            "SET dias_cumplidos = dias_cumplidos + 1 " +
                            "WHERE id = ?",
                    new Object[]{id});
            return true;
        }

        return false;
    }

    // Desmarca el hábito de hoy (por si se marcó por error).
    public boolean quitarDiaHabito(int id) {

        SQLiteDatabase db = getWritableDatabase();

        int borrados = db.delete(
                TABLA_REGISTROS,
                "id_habito = ? AND fecha = ?",
                new String[]{String.valueOf(id), obtenerFechaActual()}
        );

        if (borrados > 0) {
            db.execSQL("UPDATE habitos " +
                            "SET dias_cumplidos = MAX(dias_cumplidos - 1, 0) " +
                            "WHERE id = ?",
                    new Object[]{id});
            return true;
        }

        return false;
    }

    // Ids de los hábitos que ya se cumplieron hoy.
    public Set<Integer> listarHabitosCumplidosHoy() {

        Set<Integer> ids = new HashSet<>();
        SQLiteDatabase db = getReadableDatabase();

        Cursor c = db.rawQuery(
                "SELECT id_habito FROM habito_registros " +
                        "WHERE id_usuario = ? AND fecha = ?",
                new String[]{idUsuarioTexto(), obtenerFechaActual()}
        );

        try {
            while (c.moveToNext()) {
                ids.add(c.getInt(0));
            }
        } finally {
            c.close();
        }

        return ids;
    }

    // Total de veces que se ha cumplido cualquier hábito.
    public int contarRegistrosHabitos() {

        SQLiteDatabase db = getReadableDatabase();

        Cursor c = db.rawQuery(
                "SELECT COUNT(*) FROM habito_registros WHERE id_usuario = ?",
                new String[]{idUsuarioTexto()}
        );

        try {
            return c.moveToFirst() ? c.getInt(0) : 0;
        } finally {
            c.close();
        }
    }

    // Reinicia el contador y el historial individual del hábito.
    public void reiniciarHabito(int id) {

        SQLiteDatabase db = getWritableDatabase();

        db.beginTransaction();

        try {
            db.delete(TABLA_REGISTROS, "id_habito = ?",
                    new String[]{String.valueOf(id)});

            ContentValues cv = new ContentValues();
            cv.put("dias_cumplidos", 0);

            db.update(TABLA_HABITOS, cv, "id = ?",
                    new String[]{String.valueOf(id)});

            db.setTransactionSuccessful();

        } finally {
            db.endTransaction();
        }
    }

    public int eliminarHabito(int id) {

        SQLiteDatabase db = getWritableDatabase();

        db.beginTransaction();

        try {
            db.delete(TABLA_REGISTROS, "id_habito = ?",
                    new String[]{String.valueOf(id)});

            int filas = db.delete(TABLA_HABITOS, "id = ?",
                    new String[]{String.valueOf(id)});

            db.setTransactionSuccessful();

            return filas;

        } finally {
            db.endTransaction();
        }
    }

    public ArrayList<String> listarFechasHabitosMes(int anio, int mes) {
        return consultarFechasMes(TABLA_REGISTROS, "fecha", anio, mes);
    }

    // ====================== RACHA GLOBAL ======================

    /*
     * La racha es global: basta con cumplir al menos un hábito en el día.
     * Si hoy todavía no hay registros, se conserva la racha hasta ayer.
     */
    public int obtenerRachaGlobal() {

        SQLiteDatabase db = getReadableDatabase();

        Set<String> fechas = new HashSet<>();

        Cursor c = db.rawQuery(
                "SELECT DISTINCT fecha FROM habito_registros " +
                        "WHERE id_usuario = ? ORDER BY fecha DESC",
                new String[]{idUsuarioTexto()}
        );

        try {
            while (c.moveToNext()) {
                fechas.add(c.getString(0));
            }
        } finally {
            c.close();
        }

        SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        Calendar calendario = Calendar.getInstance();

        // Si no hubo actividad hoy, comprobamos desde ayer.
        if (!fechas.contains(formato.format(calendario.getTime()))) {
            calendario.add(Calendar.DAY_OF_YEAR, -1);
        }

        int racha = 0;

        while (fechas.contains(formato.format(calendario.getTime()))) {
            racha++;
            calendario.add(Calendar.DAY_OF_YEAR, -1);
        }

        return racha;
    }

    // ====================== METAS ======================

    public long insertarMeta(Meta m) {

        SQLiteDatabase db = getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put("id_usuario", idUsuario());
        cv.put("titulo", m.getTitulo());
        cv.put("descripcion", m.getDescripcion());
        cv.put("cumplida", m.isCumplida() ? 1 : 0);
        cv.put("fecha_objetivo", m.getFechaObjetivo());

        return db.insert(TABLA_METAS, null, cv);
    }

    public ArrayList<Meta> listarMetas(int filtro) {

        ArrayList<Meta> lista = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        Cursor c;

        if (filtro == TODAS) {
            c = db.rawQuery(
                    "SELECT id, titulo, descripcion, cumplida, fecha_objetivo " +
                            "FROM metas WHERE id_usuario = ? " +
                            "ORDER BY cumplida ASC, id DESC",
                    new String[]{idUsuarioTexto()}
            );
        } else {
            c = db.rawQuery(
                    "SELECT id, titulo, descripcion, cumplida, fecha_objetivo " +
                            "FROM metas WHERE id_usuario = ? AND cumplida = ? " +
                            "ORDER BY id DESC",
                    new String[]{idUsuarioTexto(), String.valueOf(filtro)}
            );
        }

        try {
            while (c.moveToNext()) {
                lista.add(new Meta(
                        c.getInt(0),
                        c.getString(1),
                        c.getString(2),
                        c.getInt(3) == 1,
                        c.getString(4)
                ));
            }
        } finally {
            c.close();
        }

        return lista;
    }

    public int actualizarMeta(Meta m) {

        SQLiteDatabase db = getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put("titulo", m.getTitulo());
        cv.put("descripcion", m.getDescripcion());
        cv.put("cumplida", m.isCumplida() ? 1 : 0);
        cv.put("fecha_objetivo", m.getFechaObjetivo());

        return db.update(TABLA_METAS, cv, "id = ?",
                new String[]{String.valueOf(m.getId())});
    }

    public int cambiarEstadoMeta(int id, boolean cumplida) {

        SQLiteDatabase db = getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put("cumplida", cumplida ? 1 : 0);

        return db.update(TABLA_METAS, cv, "id = ?",
                new String[]{String.valueOf(id)});
    }

    public int eliminarMeta(int id) {

        SQLiteDatabase db = getWritableDatabase();

        return db.delete(TABLA_METAS, "id = ?",
                new String[]{String.valueOf(id)});
    }

    public int eliminarMetasCumplidas() {

        SQLiteDatabase db = getWritableDatabase();

        return db.delete(TABLA_METAS, "cumplida = 1 AND id_usuario = ?",
                new String[]{idUsuarioTexto()});
    }

    public ArrayList<String> listarFechasMetasMes(int anio, int mes) {
        return consultarFechasMes(TABLA_METAS, "fecha_objetivo", anio, mes);
    }

    // ====================== USUARIOS (LOGIN) ======================

    // Registra un usuario nuevo. Devuelve -1 si el correo ya existe.
    // La contraseña no se guarda tal cual: se guarda su hash con sal.
    public long registrarUsuario(String nombre, String correo,
                                 String contrasena) {

        SQLiteDatabase db = getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put("nombre", nombre.trim());
        cv.put("correo", correo.trim().toLowerCase(Locale.ROOT));
        cv.put("contrasena", crearHash(contrasena));

        return db.insert(TABLA_USUARIOS, null, cv);
    }

    // Comprueba correo y contraseña al iniciar sesión.
    public boolean validarUsuario(String correo, String contrasena) {

        SQLiteDatabase db = getReadableDatabase();

        Cursor c = db.rawQuery(
                "SELECT id, contrasena FROM usuarios WHERE correo = ?",
                new String[]{correo.trim().toLowerCase(Locale.ROOT)}
        );

        int id = -1;
        String guardada = null;

        try {
            if (c.moveToFirst()) {
                id = c.getInt(0);
                guardada = c.getString(1);
            }
        } finally {
            c.close();
        }

        if (guardada == null || !coincide(guardada, contrasena)) {
            return false;
        }

        // Cuentas creadas antes del cambio: pasan a guardarse con hash.
        if (!guardada.startsWith("sha256$")) {
            ContentValues cv = new ContentValues();
            cv.put("contrasena", crearHash(contrasena));
            getWritableDatabase().update("usuarios", cv, "id = ?",
                    new String[]{String.valueOf(id)});
        }

        return true;
    }

    public int obtenerIdUsuario(String correo) {

        SQLiteDatabase db = getReadableDatabase();

        Cursor c = db.rawQuery(
                "SELECT id FROM usuarios WHERE correo = ?",
                new String[]{correo.trim().toLowerCase(Locale.ROOT)}
        );

        try {
            return c.moveToFirst() ? c.getInt(0) : -1;
        } finally {
            c.close();
        }
    }

    public String obtenerNombreUsuario(String correo) {

        SQLiteDatabase db = getReadableDatabase();

        Cursor c = db.rawQuery(
                "SELECT nombre FROM usuarios WHERE correo = ?",
                new String[]{correo.trim().toLowerCase(Locale.ROOT)}
        );

        try {
            return c.moveToFirst() ? c.getString(0) : null;
        } finally {
            c.close();
        }
    }

    public int actualizarNombreUsuario(int id, String nombre) {

        SQLiteDatabase db = getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put("nombre", nombre.trim());

        return db.update(TABLA_USUARIOS, cv, "id = ?",
                new String[]{String.valueOf(id)});
    }

    // ---------- Contraseñas ----------

    // Formato guardado: sha256$sal$hash
    private static String crearHash(String contrasena) {
        String sal = UUID.randomUUID().toString();
        return "sha256$" + sal + "$" + sha256(sal + contrasena);
    }

    private static boolean coincide(String guardada, String contrasena) {

        if (guardada.startsWith("sha256$")) {
            String[] partes = guardada.split("\\$");
            return partes.length == 3
                    && partes[2].equals(sha256(partes[1] + contrasena));
        }

        // Cuenta antigua guardada en texto plano
        return guardada.equals(contrasena);
    }

    private static String sha256(String texto) {

        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] bytes = md.digest(texto.getBytes(StandardCharsets.UTF_8));

            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format(Locale.US, "%02x", b));
            }
            return sb.toString();

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
