package br.great.jogopervasivo.arrayAdapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import br.ufc.great.arviewer.android.R;
import br.great.jogopervasivo.beans.Jogo;
import br.great.jogopervasivo.download.DownloadImagem;
import br.great.jogopervasivo.util.Constantes;

/**
 * Created by messiaslima on 03/02/2015.
 */
public class JogosDisponiveisAdapter extends ArrayAdapter<Jogo> {
    public JogosDisponiveisAdapter(Context context, int resource, List<Jogo> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater li = LayoutInflater.from(getContext());
            convertView = li.inflate(R.layout.jogos_disponiveis_item_lista, null);
        }
        Jogo jogo = getItem(position);

        if (jogo != null) {

            TextView nomeDoJogo = (TextView) convertView.findViewById(R.id.jogos_disponiveis_item_lista_nome);
            nomeDoJogo.setText(jogo.getNome());

            ImageView imagem = (ImageView) convertView.findViewById(R.id.jogos_disponiveis_item_lista_imagen);
            DownloadImagem downloadImagem = new DownloadImagem(imagem);

            try {
                downloadImagem.execute(jogo.getIcone());
            } catch (Exception e) {
                Log.e(Constantes.TAG, e.getMessage());
                e.printStackTrace();
            }
        }
        return convertView;
    }
}
