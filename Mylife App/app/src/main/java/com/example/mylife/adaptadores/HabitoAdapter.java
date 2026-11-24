package com.example.mylife.adaptadores;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.mylife.R;
import com.example.mylife.db.Habito;

import java.util.ArrayList;
import java.util.Set;

public class HabitoAdapter extends BaseAdapter {

    private final Context context;
    private final ArrayList<Habito> habitos;
    private final Set<Integer> hechosHoy;   // ids de los hábitos cumplidos hoy

    public HabitoAdapter(Context context, ArrayList<Habito> habitos,
                         Set<Integer> hechosHoy) {
        this.context = context;
        this.habitos = habitos;
        this.hechosHoy = hechosHoy;
    }

    @Override
    public int getCount() { return habitos.size(); }

    @Override
    public Object getItem(int position) { return habitos.get(position); }

    @Override
    public long getItemId(int position) { return habitos.get(position).getId(); }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context)
                    .inflate(R.layout.item_habito, parent, false);
        }

        Habito h = habitos.get(position);

        TextView tvIcono = convertView.findViewById(R.id.tvIcono);
        TextView tvNombre = convertView.findViewById(R.id.tvNombre);
        TextView tvDias = convertView.findViewById(R.id.tvDias);
        TextView tvHoy = convertView.findViewById(R.id.tvHoy);

        tvIcono.setText(h.getIcono());
        tvNombre.setText(h.getNombre());
        tvDias.setText("🔥 " + h.getDiasCumplidos() + " día(s) cumplidos");

        tvHoy.setVisibility(hechosHoy.contains(h.getId()) ? View.VISIBLE : View.GONE);

        return convertView;
    }
}
