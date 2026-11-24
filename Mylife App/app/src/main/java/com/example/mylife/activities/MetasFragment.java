package com.example.mylife.activities;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.mylife.R;
import com.example.mylife.adaptadores.MetaAdapter;
import com.example.mylife.db.Base;
import com.example.mylife.db.Meta;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MetasFragment extends Fragment {

    private ListView listView;
    private Base base;
    private ArrayList<Meta> lista;
    private int filtroActual = Base.TODAS;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_metas, container, false);

        base = new Base(requireContext());
        listView = v.findViewById(R.id.listaMetas);
        listView.setEmptyView(v.findViewById(R.id.vacio));
        FloatingActionButton fab = v.findViewById(R.id.fabAgregarMeta);

        fab.setOnClickListener(x -> mostrarDialogoMeta(null));
        listView.setOnItemClickListener((parent, view, position, id) ->
                mostrarOpciones(lista.get(position)));

        cargarLista();
        return v;
    }

    private void cargarLista() {
        lista = base.listarMetas(filtroActual);
        listView.setAdapter(new MetaAdapter(requireContext(), lista));
    }

    // ---------------------- MENÚ DE OPCIONES ----------------------

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_metas, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.metas_todas) {
            filtroActual = Base.TODAS;
            cargarLista();
        } else if (id == R.id.metas_progreso) {
            filtroActual = Base.PENDIENTES;
            cargarLista();
        } else if (id == R.id.metas_cumplidas) {
            filtroActual = Base.HECHAS;
            cargarLista();
        } else if (id == R.id.metas_eliminar_cumplidas) {
            int borradas = base.eliminarMetasCumplidas();
            Toast.makeText(getContext(), borradas + " meta(s) eliminada(s)",
                    Toast.LENGTH_SHORT).show();
            cargarLista();
        } else {
            return super.onOptionsItemSelected(item);
        }
        return true;
    }

    // ---------------------- DIÁLOGOS ----------------------

    private void mostrarOpciones(Meta m) {
        String cambiar = m.isCumplida() ? "Marcar como en progreso" : "Marcar como cumplida";
        String[] opciones = {cambiar, "Editar", "Eliminar"};

        new AlertDialog.Builder(requireContext())
                .setTitle(m.getTitulo())
                .setItems(opciones, (dialog, which) -> {
                    if (which == 0) {
                        base.cambiarEstadoMeta(m.getId(), !m.isCumplida());
                        if (!m.isCumplida()) {
                            Toast.makeText(getContext(), "¡Meta cumplida! 🎉",
                                    Toast.LENGTH_SHORT).show();
                        }
                        cargarLista();
                    } else if (which == 1) {
                        mostrarDialogoMeta(m);
                    } else {
                        new AlertDialog.Builder(requireContext())
                                .setMessage("¿Eliminar la meta \"" + m.getTitulo() + "\"?")
                                .setPositiveButton("Sí", (d, w) -> {
                                    base.eliminarMeta(m.getId());
                                    Toast.makeText(getContext(), "Meta eliminada",
                                            Toast.LENGTH_SHORT).show();
                                    cargarLista();
                                })
                                .setNegativeButton("Cancelar", null)
                                .show();
                    }
                })
                .show();
    }

    // Si existente == null agrega, si no, edita
    private void mostrarDialogoMeta(@Nullable Meta existente) {
        View dv = LayoutInflater.from(getContext()).inflate(R.layout.dialog_meta, null);

        EditText etTitulo = dv.findViewById(R.id.etTituloMeta);
        EditText etDescripcion = dv.findViewById(R.id.etDescripcionMeta);
        TextView tvFecha = dv.findViewById(R.id.tvFechaMeta);
        Button btnFecha = dv.findViewById(R.id.btnFechaMeta);
        TextView tvQuitar = dv.findViewById(R.id.tvQuitarFecha);

        // La fecha objetivo es opcional (null = sin fecha)
        final String[] fecha = {null};

        if (existente != null) {
            etTitulo.setText(existente.getTitulo());
            etDescripcion.setText(existente.getDescripcion());
            fecha[0] = existente.getFechaObjetivo();
        }
        mostrarFecha(tvFecha, fecha[0]);

        btnFecha.setOnClickListener(x -> {
            Calendar c = calendarioDe(fecha[0]);
            new DatePickerDialog(requireContext(), (picker, y, m, d) -> {
                // yyyy-MM-dd para que coincida con el calendario de Inicio
                fecha[0] = String.format(Locale.US, "%04d-%02d-%02d", y, m + 1, d);
                mostrarFecha(tvFecha, fecha[0]);
            }, c.get(Calendar.YEAR), c.get(Calendar.MONTH),
                    c.get(Calendar.DAY_OF_MONTH)).show();
        });

        tvQuitar.setOnClickListener(x -> {
            fecha[0] = null;
            mostrarFecha(tvFecha, null);
        });

        new AlertDialog.Builder(requireContext())
                .setTitle(existente == null ? "Nueva meta" : "Editar meta")
                .setView(dv)
                .setPositiveButton("Guardar", (dialog, which) -> {
                    String titulo = etTitulo.getText().toString().trim();
                    String descripcion = etDescripcion.getText().toString().trim();

                    if (titulo.isEmpty()) {
                        Toast.makeText(getContext(), "Escribe un título",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (existente == null) {
                        base.insertarMeta(new Meta(0, titulo, descripcion, false, fecha[0]));
                    } else {
                        existente.setTitulo(titulo);
                        existente.setDescripcion(descripcion);
                        existente.setFechaObjetivo(fecha[0]);
                        base.actualizarMeta(existente);
                    }
                    cargarLista();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void mostrarFecha(TextView tv, @Nullable String fecha) {
        tv.setText(fecha == null ? "Sin fecha objetivo" : "🎯 " + fecha);
    }

    private Calendar calendarioDe(@Nullable String fecha) {
        Calendar c = Calendar.getInstance();
        if (fecha != null) {
            try {
                Date d = new SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(fecha);
                if (d != null) {
                    c.setTime(d);
                }
            } catch (ParseException ignorada) {
                // Si la fecha no se puede leer, se usa la de hoy
            }
        }
        return c;
    }
}
