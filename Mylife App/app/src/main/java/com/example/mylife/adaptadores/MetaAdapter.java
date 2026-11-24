package com.example.mylife.adaptadores;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.mylife.R;
import com.example.mylife.db.Meta;

import java.util.ArrayList;

public class MetaAdapter extends BaseAdapter {

    private final Context context;
    private final ArrayList<Meta> metas;

    public MetaAdapter(Context context, ArrayList<Meta> metas) {
        this.context = context;
        this.metas = metas;
    }

    @Override
    public int getCount() { return metas.size(); }

    @Override
    public Object getItem(int position) { return metas.get(position); }

    @Override
    public long getItemId(int position) { return metas.get(position).getId(); }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context)
                    .inflate(R.layout.item_meta, parent, false);
        }

        Meta m = metas.get(position);

        TextView tvIcono = convertView.findViewById(R.id.tvIcono);
        TextView tvTitulo = convertView.findViewById(R.id.tvTitulo);
        TextView tvDescripcion = convertView.findViewById(R.id.tvDescripcion);
        TextView tvFecha = convertView.findViewById(R.id.tvFecha);

        tvTitulo.setText(m.getTitulo());
        tvDescripcion.setText(m.getDescripcion());

        String fecha = m.getFechaObjetivo();
        if (fecha == null || fecha.isEmpty()) {
            tvFecha.setVisibility(View.GONE);
        } else {
            tvFecha.setText("📅 " + fecha);
            tvFecha.setVisibility(View.VISIBLE);
        }

        if (m.isCumplida()) {
            tvIcono.setText("🏆");
            tvTitulo.setPaintFlags(tvTitulo.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            tvIcono.setText("🎯");
            tvTitulo.setPaintFlags(tvTitulo.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
        }

        return convertView;
    }
}
