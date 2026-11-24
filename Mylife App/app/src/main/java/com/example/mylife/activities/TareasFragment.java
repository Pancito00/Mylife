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
import com.example.mylife.adaptadores.TareaAdapter;
import com.example.mylife.db.Base;
import com.example.mylife.db.Tarea;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TareasFragment extends Fragment {

    private ListView listView;
    private Base base;
    private ArrayList<Tarea> lista;
    private int filtroActual = Base.TODAS;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true); // para que el fragment tenga su menú de opciones
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_tareas, container, false);

        base = new Base(getContext(), "mylife", null, 2);
        listView = v.findViewById(R.id.listaTareas);
        listView.setEmptyView(v.findViewById(R.id.vacio));
        FloatingActionButton fab = v.findViewById(R.id.fabAgregar);

        fab.setOnClickListener(x -> mostrarDialogoTarea(null));
        listView.setOnItemClickListener((parent, view, position, id) ->
                mostrarOpciones(lista.get(position)));

        cargarLista();
        return v;
    }

    // ---------------------- LISTA ----------------------

    private void cargarLista() {
        lista = base.listarTareas(filtroActual);
        listView.setAdapter(new TareaAdapter(getContext(), lista));
    }

    // ---------------------- MENÚ DE OPCIONES ----------------------

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_tareas, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.ver_todas) {
            filtroActual = Base.TODAS;
            cargarLista();
        } else if (id == R.id.ver_pendientes) {
            filtroActual = Base.PENDIENTES;
            cargarLista();
        } else if (id == R.id.ver_hechas) {
            filtroActual = Base.HECHAS;
            cargarLista();
        } else if (id == R.id.eliminar_hechas) {
            int borradas = base.eliminarTareasHechas();
            Toast.makeText(getContext(), borradas + " tarea(s) eliminada(s)",
                    Toast.LENGTH_SHORT).show();
            cargarLista();
        } else {
            return super.onOptionsItemSelected(item);
        }
        return true;
    }

    // ---------------------- DIÁLOGO AL TOCAR UNA TAREA ----------------------

    private void mostrarOpciones(Tarea t) {
        String cambiar = t.isHecha() ? "Marcar como pendiente" : "Marcar como hecha";
        String[] opciones = {cambiar, "Editar", "Eliminar"};

        new AlertDialog.Builder(requireContext())
                .setTitle(t.getTitulo())
                .setItems(opciones, (dialog, which) -> {
                    if (which == 0) {
                        base.cambiarEstadoTarea(t.getId(), !t.isHecha());
                        cargarLista();
                    } else if (which == 1) {
                        mostrarDialogoTarea(t);
                    } else {
                        base.eliminarTarea(t.getId());
                        Toast.makeText(getContext(), "Tarea eliminada",
                                Toast.LENGTH_SHORT).show();
                        cargarLista();
                    }
                })
                .show();
    }

    // ---------------------- DIÁLOGO AGREGAR / EDITAR ----------------------

    // Si existente == null agrega, si no, edita
    private void mostrarDialogoTarea(@Nullable Tarea existente) {
        View dv = LayoutInflater.from(getContext()).inflate(R.layout.dialog_tarea, null);

        EditText etTitulo = dv.findViewById(R.id.etTitulo);
        EditText etMateria = dv.findViewById(R.id.etMateria);
        TextView tvFechaSel = dv.findViewById(R.id.tvFechaSel);
        Button btnFecha = dv.findViewById(R.id.btnFecha);

        final String[] fecha = {hoy()};

        if (existente != null) {
            etTitulo.setText(existente.getTitulo());
            etMateria.setText(existente.getMateria());
            fecha[0] = existente.getFecha();
        }
        tvFechaSel.setText(fecha[0]);

        btnFecha.setOnClickListener(x -> {
            Calendar c = Calendar.getInstance();
            new DatePickerDialog(requireContext(), (picker, y, m, d) -> {
                // yyyy-MM-dd para que SQLite ordene bien por fecha
                fecha[0] = String.format(Locale.US, "%04d-%02d-%02d", y, m + 1, d);
                tvFechaSel.setText(fecha[0]);
            }, c.get(Calendar.YEAR), c.get(Calendar.MONTH),
                    c.get(Calendar.DAY_OF_MONTH)).show();
        });

        new AlertDialog.Builder(requireContext())
                .setTitle(existente == null ? "Nueva tarea" : "Editar tarea")
                .setView(dv)
                .setPositiveButton("Guardar", (dialog, which) -> {
                    String titulo = etTitulo.getText().toString().trim();
                    String materia = etMateria.getText().toString().trim();

                    if (titulo.isEmpty()) {
                        Toast.makeText(getContext(), "Escribe un título",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (existente == null) {
                        base.insertarTarea(new Tarea(0, titulo, materia, fecha[0], false));
                    } else {
                        existente.setTitulo(titulo);
                        existente.setMateria(materia);
                        existente.setFecha(fecha[0]);
                        base.actualizarTarea(existente);
                    }
                    cargarLista();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private String hoy() {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(new Date());
    }
}