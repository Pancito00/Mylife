package com.example.mylife.activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.mylife.R;
import com.example.mylife.db.Base;
import com.example.mylife.db.Sesion;
import com.example.mylife.db.Tarea;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class PerfilFragment extends Fragment {

    private Base base;
    private Sesion sesion;

    private TextView txtNombre, txtCorreo;
    private TextView txtTareas, txtHabitos, txtPorcentaje;
    private ProgressBar progreso;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_perfil, container, false);

        base = new Base(requireContext());
        sesion = new Sesion(requireContext());

        txtNombre = v.findViewById(R.id.txtNombreUsuario);
        txtCorreo = v.findViewById(R.id.txtCorreoUsuario);
        txtTareas = v.findViewById(R.id.txtTareasCompletadas);
        txtHabitos = v.findViewById(R.id.txtHabitosCompletados);
        txtPorcentaje = v.findViewById(R.id.txtPorcentajeProgreso);
        progreso = v.findViewById(R.id.progresoSemanal);

        v.findViewById(R.id.btnEditarPerfil)
                .setOnClickListener(x -> mostrarDialogoNombre());

        v.findViewById(R.id.btnAcercaDe).setOnClickListener(x -> {
            if (getActivity() instanceof MenuPrincipalActivity) {
                ((MenuPrincipalActivity) getActivity()).abrirAcerca();
            }
        });

        v.findViewById(R.id.btnCerrarSesion).setOnClickListener(x -> {
            if (getActivity() instanceof MenuPrincipalActivity) {
                ((MenuPrincipalActivity) getActivity()).cerrarSesion();
            }
        });

        cargarDatos();
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (txtNombre != null) {
            cargarDatos();
        }
    }

    // ====================== DATOS ======================

    private void cargarDatos() {

        String nombre = sesion.getNombre();
        txtNombre.setText(nombre.isEmpty() ? "Tu nombre" : nombre);
        txtCorreo.setText(sesion.getCorreo());

        txtTareas.setText(String.valueOf(base.listarTareas(Base.HECHAS).size()));
        txtHabitos.setText(String.valueOf(base.contarRegistrosHabitos()));

        // Progreso de la semana (domingo a sábado): tareas hechas / tareas totales
        SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

        Calendar inicio = Calendar.getInstance();
        inicio.add(Calendar.DAY_OF_YEAR,
                -(inicio.get(Calendar.DAY_OF_WEEK) - Calendar.SUNDAY));

        Calendar fin = (Calendar) inicio.clone();
        fin.add(Calendar.DAY_OF_YEAR, 6);

        String desde = formato.format(inicio.getTime());
        String hasta = formato.format(fin.getTime());

        int total = 0;
        int hechas = 0;

        for (Tarea t : base.listarTareas(Base.TODAS)) {
            String f = t.getFecha();
            if (f != null && f.compareTo(desde) >= 0 && f.compareTo(hasta) <= 0) {
                total++;
                if (t.isHecha()) {
                    hechas++;
                }
            }
        }

        int porcentaje = total == 0 ? 0 : (hechas * 100) / total;
        progreso.setProgress(porcentaje);

        if (total == 0) {
            txtPorcentaje.setText("Aún no tienes tareas esta semana ♡");
        } else {
            txtPorcentaje.setText(hechas + " de " + total
                    + " tareas de esta semana (" + porcentaje + "%)");
        }
    }

    // ====================== EDITAR NOMBRE ======================

    private void mostrarDialogoNombre() {

        EditText et = new EditText(requireContext());
        et.setHint("Tu nombre");
        et.setSingleLine(true);
        et.setText(sesion.getNombre());
        et.setSelection(et.getText().length());

        FrameLayout contenedor = new FrameLayout(requireContext());
        int margen = (int) (20 * getResources().getDisplayMetrics().density);
        contenedor.setPadding(margen, margen / 2, margen, 0);
        contenedor.addView(et);

        new AlertDialog.Builder(requireContext())
                .setTitle("Editar perfil")
                .setView(contenedor)
                .setPositiveButton("Guardar", (dialog, which) -> {

                    String nuevo = et.getText().toString().trim();

                    if (nuevo.isEmpty()) {
                        Toast.makeText(getContext(), "Escribe tu nombre",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }

                    base.actualizarNombreUsuario(sesion.getId(), nuevo);
                    sesion.setNombre(nuevo);
                    txtNombre.setText(nuevo);

                    if (getActivity() instanceof MenuPrincipalActivity) {
                        ((MenuPrincipalActivity) getActivity())
                                .actualizarEncabezado();
                    }

                    Toast.makeText(getContext(), "Perfil actualizado ♡",
                            Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
}
