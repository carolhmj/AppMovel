package br.great.jogopervasivo.arrayAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import br.ufc.great.arviewer.android.R;
import br.great.jogopervasivo.beans.Mecanica;
import br.great.jogopervasivo.beans.Missao;
import br.great.jogopervasivo.util.Constantes;

/**
 * Created by messiaslima on 24/06/2015.
 * @author messiaslima
 */
public class ListarMissoesAdapter extends ArrayAdapter<Missao> {
    public ListarMissoesAdapter(Context context, int resource, List<Missao> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        if (convertView == null) {
            LayoutInflater li = LayoutInflater.from(getContext());
            convertView = li.inflate(R.layout.mecanicas_item_lista, null);
        }
        Missao missao = getItem(position);

        if (missao != null) {

            TextView nomeDoJogo = (TextView) convertView.findViewById(R.id.mecanica_nome);
            nomeDoJogo.setText(missao.getNome());


        }
        return convertView;


    }
}
