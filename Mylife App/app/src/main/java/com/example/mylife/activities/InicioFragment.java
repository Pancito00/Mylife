
package com.example.mylife.activities;

import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.mylife.R;
import com.example.mylife.db.Base;
import com.example.mylife.db.Tarea;
import com.example.mylife.db.Meta;
import com.example.mylife.db.Sesion;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class InicioFragment extends Fragment {

    private Base base;

    private TextView tvPendientes, tvHechas;
    private TextView tvMes, tvRacha, tvFechaSeleccionada;
    private TextView tvActividades;

    private GridLayout calendarioDias;

    private Calendar mesVisible;
    private String fechaSeleccionada;

    private final Set<String> fechasTareas = new HashSet<>();
    private final Set<String> fechasMetas = new HashSet<>();
    private final Set<String> fechasHabitos = new HashSet<>();

    private final int ROSA = Color.rgb(244, 143, 177);
    private final int LILA = Color.rgb(190, 170, 230);
    private final int VERDE = Color.rgb(129, 199, 132);
    private final int ROSA_FONDO = Color.rgb(255, 240, 245);
    private final int TEXTO = Color.rgb(90, 70, 80);

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(
                R.layout.fragment_inicio, container, false
        );

        base = new Base(getContext(), "mylife", null, Base.VERSION_BD);

        // Saludo con el nombre de quien inició sesión
        TextView tvBienvenida = v.findViewById(R.id.tvBienvenida);
        String nombre = new Sesion(requireContext()).getNombre();
        if (!nombre.isEmpty()) {
            tvBienvenida.setText("¡Hola, " + nombre.split(" ")[0] + "!");
        }

        tvPendientes = v.findViewById(R.id.tvPendientes);
        tvHechas = v.findViewById(R.id.tvHechas);
        tvMes = v.findViewById(R.id.tvMes);
        tvRacha = v.findViewById(R.id.tvRacha);
        tvFechaSeleccionada = v.findViewById(R.id.tvFechaSeleccionada);
        tvActividades = v.findViewById(R.id.tvActividades);

        calendarioDias = v.findViewById(R.id.calendarioDias);

        Button btnAnterior = v.findViewById(R.id.btnMesAnterior);
        Button btnSiguiente = v.findViewById(R.id.btnMesSiguiente);

        mesVisible = Calendar.getInstance();

        fechaSeleccionada = obtenerFecha(mesVisible);

        btnAnterior.setOnClickListener(view -> {
            mesVisible.add(Calendar.MONTH, -1);
            mostrarCalendario();
        });

        btnSiguiente.setOnClickListener(view -> {
            mesVisible.add(Calendar.MONTH, 1);
            mostrarCalendario();
        });

        actualizarResumen();
        mostrarCalendario();

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (base != null && tvPendientes != null) {
            actualizarResumen();
            mostrarCalendario();
        }
    }

    // ====================== RESUMEN ======================

    private void actualizarResumen() {

        int pendientes = base.listarTareas(Base.PENDIENTES).size();
        int hechas = base.listarTareas(Base.HECHAS).size();

        tvPendientes.setText("Tareas pendientes: " + pendientes);
        tvHechas.setText("Tareas realizadas: " + hechas);

        int racha = base.obtenerRachaGlobal();

        tvRacha.setText("🔥 Racha de hábitos: " + racha +
                (racha == 1 ? " día" : " días"));
    }

    // ====================== CALENDARIO ======================

    private void mostrarCalendario() {

        int anio = mesVisible.get(Calendar.YEAR);
        int mes = mesVisible.get(Calendar.MONTH) + 1;

        SimpleDateFormat formatoMes = new SimpleDateFormat(
                "MMMM yyyy", new Locale("es", "MX")
        );

        tvMes.setText(
                formatoMes.format(mesVisible.getTime())
                        .substring(0, 1).toUpperCase() +
                        formatoMes.format(mesVisible.getTime()).substring(1)
        );

        fechasTareas.clear();
        fechasMetas.clear();
        fechasHabitos.clear();

        fechasTareas.addAll(base.listarFechasTareasMes(anio, mes));
        fechasMetas.addAll(base.listarFechasMetasMes(anio, mes));
        fechasHabitos.addAll(base.listarFechasHabitosMes(anio, mes));

        calendarioDias.removeAllViews();

        Calendar primero = (Calendar) mesVisible.clone();
        primero.set(Calendar.DAY_OF_MONTH, 1);

        int espacios = primero.get(Calendar.DAY_OF_WEEK) - 1;

        int diasMes = mesVisible.getActualMaximum(
                Calendar.DAY_OF_MONTH
        );

        // Espacios vacíos antes del primer día.
        for (int i = 0; i < espacios; i++) {
            agregarEspacio();
        }

        // Días del mes.
        for (int dia = 1; dia <= diasMes; dia++) {

            Calendar fecha = (Calendar) mesVisible.clone();
            fecha.set(Calendar.DAY_OF_MONTH, dia);

            String fechaTexto = obtenerFecha(fecha);

            agregarDia(dia, fechaTexto);
        }

        mostrarActividadesSeleccionadas();
    }

    private void agregarEspacio() {

        View espacio = new View(requireContext());

        GridLayout.LayoutParams params = new GridLayout.LayoutParams();

        params.width = 0;
        params.height = dp(52);
        params.columnSpec = GridLayout.spec(
                GridLayout.UNDEFINED, 1f
        );

        calendarioDias.addView(espacio, params);
    }

    private void agregarDia(int dia, String fecha) {

        LinearLayout celda = new LinearLayout(requireContext());
        celda.setOrientation(LinearLayout.VERTICAL);
        celda.setGravity(Gravity.CENTER);
        celda.setPadding(2, 3, 2, 3);

        GridLayout.LayoutParams params = new GridLayout.LayoutParams();

        params.width = 0;
        params.height = dp(52);
        params.columnSpec = GridLayout.spec(
                GridLayout.UNDEFINED, 1f
        );

        params.setMargins(1, 1, 1, 1);

        TextView numero = new TextView(requireContext());

        numero.setText(String.valueOf(dia));
        numero.setTextSize(14);
        numero.setTextColor(TEXTO);
        numero.setGravity(Gravity.CENTER);
        numero.setTypeface(null, Typeface.BOLD);

        LinearLayout.LayoutParams numeroParams =
                new LinearLayout.LayoutParams(
                        dp(30), dp(30)
                );

        // Resaltar el día seleccionado.
        if (fecha.equals(fechaSeleccionada)) {

            GradientDrawable seleccionado = new GradientDrawable();
            seleccionado.setColor(ROSA);
            seleccionado.setCornerRadius(dp(30));

            numero.setBackground(seleccionado);
            numero.setTextColor(Color.WHITE);
        }

        celda.addView(numero, numeroParams);

        // Indicadores de actividades.
        LinearLayout indicadores = new LinearLayout(requireContext());
        indicadores.setOrientation(LinearLayout.HORIZONTAL);
        indicadores.setGravity(Gravity.CENTER);

        if (fechasTareas.contains(fecha)) {
            agregarPunto(indicadores, ROSA);
        }

        if (fechasMetas.contains(fecha)) {
            agregarPunto(indicadores, LILA);
        }

        if (fechasHabitos.contains(fecha)) {
            agregarPunto(indicadores, VERDE);
        }

        celda.addView(indicadores);

        celda.setOnClickListener(view -> {

            fechaSeleccionada = fecha;

            mostrarCalendario();
        });

        calendarioDias.addView(celda, params);
    }

    private void agregarPunto(LinearLayout contenedor, int color) {

        View punto = new View(requireContext());

        GradientDrawable forma = new GradientDrawable();
        forma.setShape(GradientDrawable.OVAL);
        forma.setColor(color);

        punto.setBackground(forma);

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        dp(6), dp(6)
                );

        params.setMargins(dp(2), 0, dp(2), 0);

        contenedor.addView(punto, params);
    }

    // ====================== ACTIVIDADES DEL DÍA ======================

    private void mostrarActividadesSeleccionadas() {

        tvFechaSeleccionada.setText(
                "Actividades del " + fechaSeleccionada
        );

        StringBuilder texto = new StringBuilder();

        ArrayList<Tarea> tareas = base.listarTareas(Base.TODAS);

        boolean hayActividad = false;

        // Buscar tareas de la fecha seleccionada.
        for (Tarea tarea : tareas) {

            if (fechaSeleccionada.equals(tarea.getFecha())) {

                texto.append("📌 ")
                        .append(tarea.getTitulo());

                if (tarea.getMateria() != null &&
                        !tarea.getMateria().isEmpty()) {

                    texto.append(" (")
                            .append(tarea.getMateria())
                            .append(")");
                }

                texto.append(tarea.isHecha()
                        ? " ✓ Realizada\n"
                        : " • Pendiente\n");

                hayActividad = true;
            }
        }

        // Buscar metas con esa fecha objetivo.
        ArrayList<Meta> metas = base.listarMetas(Base.TODAS);

        for (Meta meta : metas) {

            if (fechaSeleccionada.equals(meta.getFechaObjetivo())) {

                texto.append("🎯 ")
                        .append(meta.getTitulo())
                        .append(meta.isCumplida()
                                ? " ✓ Cumplida\n"
                                : " • Meta pendiente\n");

                hayActividad = true;
            }
        }

        // Comprobar si se cumplió algún hábito ese día.
        if (fechasHabitos.contains(fechaSeleccionada)) {

            texto.append("🌱 ¡Cumpliste al menos un hábito!\n");

            hayActividad = true;
        }

        if (!hayActividad) {
            texto.append("No hay actividades registradas para este día.");
        }

        tvActividades.setText(texto.toString());
    }

    // ====================== UTILIDADES ======================

    private String obtenerFecha(Calendar calendario) {

        return new SimpleDateFormat(
                "yyyy-MM-dd", Locale.US
        ).format(calendario.getTime());
    }

    private int dp(int valor) {

        return (int) (valor *
                getResources().getDisplayMetrics().density);
    }
}
