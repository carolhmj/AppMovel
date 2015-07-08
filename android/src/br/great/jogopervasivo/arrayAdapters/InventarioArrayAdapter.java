package br.great.jogopervasivo.arrayAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import br.great.jogopervasivo.beans.ObjetoInventario;
import br.ufc.great.arviewer.android.R;

/**
 * Created by messiaslima on 08/07/2015.
 *
 * @author messiaslima
 */

public class InventarioArrayAdapter extends ArrayAdapter<ObjetoInventario> {
    public InventarioArrayAdapter(Context context, int resource, List<ObjetoInventario> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater li = LayoutInflater.from(getContext());
            convertView = li.inflate(R.layout.invertario_item_lista, null);
        }
        final ObjetoInventario objetoInventario = getItem(position);

        if (objetoInventario != null) {

            TextView nome = (TextView) convertView.findViewById(R.id.nomeObjetoInventarioListItem);
            nome.setText(objetoInventario.getNome());

            ImageButton botao = (ImageButton) convertView.findViewById(R.id.botaoObjetoInventarioListItem);
            botao.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    objetoInventario.compartilhar(getContext());
                }
            });
        }
        return convertView;
    }

}
