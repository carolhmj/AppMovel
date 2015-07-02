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
import br.great.jogopervasivo.util.Constantes;

/**
 * Created by messiaslima on 12/03/2015.
 *
 * @author messiaslima
 */
public class ListarMecanicasAdapter extends ArrayAdapter<Mecanica> {
    public ListarMecanicasAdapter(Context context, int resource, List<Mecanica> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        if (convertView == null) {
            LayoutInflater li = LayoutInflater.from(getContext());
            convertView = li.inflate(R.layout.mecanicas_item_lista, null);
        }
        Mecanica mecanica = getItem(position);

        if (mecanica != null) {

            TextView nomeDoJogo = (TextView) convertView.findViewById(R.id.mecanica_nome);
            String relizada = "";
            if (mecanica.isRealizada()) {
                relizada = "(Realizada)";
            }
            if (mecanica.getEstado() == 2) {
                relizada = "(Bloqueada)";
            }
            nomeDoJogo.setText(mecanica.getNome() + relizada);

            ImageView imagem = (ImageView) convertView.findViewById(R.id.mecanica_imagem);
            if (mecanica.getTipoSimples().equals(Constantes.TIPO_MECANICA_CSONS)) {
                imagem.setImageResource(R.drawable.ic_action_mic);
            }
            if (mecanica.getTipoSimples().equals(Constantes.TIPO_MECANICA_CFOTOS)) {
                imagem.setImageResource(R.drawable.ic_action_camera);
            }
            if (mecanica.getTipoSimples().equals(Constantes.TIPO_MECANICA_CVIDEOS)) {
                imagem.setImageResource(R.drawable.ic_action_video);
            }
            if (mecanica.getTipoSimples().equals(Constantes.TIPO_MECANICA_CTEXTOS)) {
                imagem.setImageResource(android.R.drawable.stat_notify_chat);
            }

        }
        return convertView;


    }
}
