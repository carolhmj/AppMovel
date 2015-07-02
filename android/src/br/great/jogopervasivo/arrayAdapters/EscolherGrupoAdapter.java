package br.great.jogopervasivo.arrayAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;


import br.great.jogopervasivo.beans.Grupo;
import br.ufc.great.arviewer.android.R;

/**
 * Created by messiaslima on 10/02/2015.
 */
public class EscolherGrupoAdapter extends ArrayAdapter<Grupo> {
    public EscolherGrupoAdapter(Context context, int resource, List<Grupo> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater li = LayoutInflater.from(getContext());
            convertView = li.inflate(R.layout.grupos_item_lista, null);
        }

        Grupo grupo = getItem(position);
        if (grupo != null) {
            TextView nome = (TextView) convertView.findViewById(R.id.grupos_item_lista_nome);
            nome.setText(grupo.getNome());
        }
        return convertView;
    }
}
