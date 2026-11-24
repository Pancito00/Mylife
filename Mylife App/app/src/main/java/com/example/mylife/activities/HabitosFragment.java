package com.example.mylife.activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.mylife.R;
import com.example.mylife.adaptadores.HabitoAdapter;
import com.example.mylife.db.Base;
import com.example.mylife.db.Habito;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class HabitosFragment extends Fragment {

    private static final String[] ICONOS = {"💧", "🏃", "📚", "😴", "🧘", "🥗"};
    private static final String[] ETIQUETAS = {"💧 Agua", "🏃 Ejercicio", "📚 Leer",
            "😴 Dormir", "🧘 Meditar", "🥗 Comer sano"};

    private ListView listView;
    private Base base;
    private ArrayList<Habito> lista;
    private Set<Integer> hechosHoy = new HashSet<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_habitos, container, false);

        base = new Base(requireContext());
        listView = v.findViewById(R.id.listaHabitos);
        listView.setEmptyView(v.findViewById(R.id.vacio));
        FloatingActionButton fab = v.findViewById(R.id.fabAgregarHabito);

        fab.setOnClickListener(x -> mostrarDialogoHabito(null));
        listView.setOnItemClickListener((parent, view, position, id) ->
                mostrarOpciones(lista.get(position)));

        cargarLista();
        return v;
    }

    private void cargarLista() {
        lista = base.listarHabitos();
        hechosHoy = base.listarHabitosCumplidosHoy();
        listView.setAdapter(new HabitoAdapter(requireContext(), lista, hechosHoy));
    }

    private void mostrarOpciones(Habito h) {

        boolean hecho = hechosHoy.contains(h.getId());

        String[] opciones = {
                hecho ? "Quitar el cumplido de hoy" : "Cumplí hoy ✔",
                "Reiniciar contador",
                "Editar",
                "Eliminar"
        };

        new AlertDialog.Builder(requireContext())
                .setTitle(h.getNombre())
                .setItems(opciones, (dialog, which) -> {
                    if (which == 0) {
                        if (hecho) {
                            base.quitarDiaHabito(h.getId());
                        } else {
                            base.sumarDiaHabito(h.getId());
                            Toast.makeText(getContext(), "¡Muy bien! 🐷",
                                    Toast.LENGTH_SHORT).show();
                        }
                        cargarLista();
                    } else if (which == 1) {
                        confirmar("¿Reiniciar el contador de \"" + h.getNombre()
                                + "\"? Se borrará su historial.", () -> {
                            base.reiniciarHabito(h.getId());
                            cargarLista();
                        });
                    } else if (which == 2) {
                        mostrarDialogoHabito(h);
                    } else {
                        confirmar("¿Eliminar el hábito \"" + h.getNombre() + "\"?", () -> {
                            base.eliminarHabito(h.getId());
                            Toast.makeText(getContext(), "Hábito eliminado",
                                    Toast.LENGTH_SHORT).show();
                            cargarLista();
                        });
                    }
                })
                .show();
    }

    private void confirmar(String mensaje, Runnable accion) {
        new AlertDialog.Builder(requireContext())
                .setMessage(mensaje)
                .setPositiveButton("Sí", (dialog, which) -> accion.run())
                .setNegativeButton("Cancelar", null)
                .show();
    }

    // Si existente == null agrega, si no, edita
    private void mostrarDialogoHabito(@Nullable Habito existente) {
        View dv = LayoutInflater.from(getContext()).inflate(R.layout.dialog_habito, null);

        EditText etNombre = dv.findViewById(R.id.etNombre);
        Spinner spIcono = dv.findViewById(R.id.spIcono);

        spIcono.setAdapter(new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_dropdown_item, ETIQUETAS));

        if (existente != null) {
            etNombre.setText(existente.getNombre());
            for (int i = 0; i < ICONOS.length; i++) {
                if (ICONOS[i].equals(existente.getIcono())) {
                    spIcono.setSelection(i);
                }
            }
        }

        new AlertDialog.Builder(requireContext())
                .setTitle(existente == null ? "Nuevo hábito" : "Editar hábito")
                .setView(dv)
                .setPositiveButton("Guardar", (dialog, which) -> {
                    String nombre = etNombre.getText().toString().trim();
                    String icono = ICONOS[spIcono.getSelectedItemPosition()];

                    if (nombre.isEmpty()) {
                        Toast.makeText(getContext(), "Escribe un nombre",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (existente == null) {
                        base.insertarHabito(new Habito(0, nombre, icono, 0));
                    } else {
                        existente.setNombre(nombre);
                        existente.setIcono(icono);
                        base.actualizarHabito(existente);
                    }
                    cargarLista();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
}
