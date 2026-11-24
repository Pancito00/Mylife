package com.example.mylife.adaptadores;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mylife.R;
import com.example.mylife.db.Tarea;

import java.util.ArrayList;

public class TareaAdapter extends BaseAdapter {

    private final Context context;
    private final ArrayList<Tarea> tareas;

    public TareaAdapter(Context context, ArrayList<Tarea> tareas) {
        this.context = context;
        this.tareas = tareas;
    }

    @Override
    public int getCount() { return tareas.size(); }

    @Override
    public Object getItem(int position) { return tareas.get(position); }

    @Override
    public long getItemId(int position) { return tareas.get(position).getId(); }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context)
                    .inflate(R.layout.item_tarea, parent, false);
        }

        Tarea t = tareas.get(position);

        ImageView img = convertView.findViewById(R.id.imgEstado);
        TextView tvTitulo = convertView.findViewById(R.id.tvTitulo);
        TextView tvMateria = convertView.findViewById(R.id.tvMateria);
        TextView tvFecha = convertView.findViewById(R.id.tvFecha);

        tvTitulo.setText(t.getTitulo());
        tvMateria.setText(t.getMateria());
        tvFecha.setText(t.getFecha());

        // Imagen según el estado. Luego puedes cambiarlas por tus propios drawables
        if (t.isHecha()) {
            img.setImageResource(android.R.drawable.checkbox_on_background);
        } else {
            img.setImageResource(android.R.drawable.checkbox_off_background);
        }

        return convertView;
    }
}