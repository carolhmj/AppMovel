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


import br.great.jogopervasivo.beans.InstanciaDeJogo;
import br.great.jogopervasivo.download.DownloadImagem;
import br.great.jogopervasivo.util.Constantes;
import br.ufc.great.arviewer.android.R;

/**
 * Created by messiaslima on 10/02/2015.
 */
public class InstanciasExecutandoAdapter extends ArrayAdapter<InstanciaDeJogo> {
    public InstanciasExecutandoAdapter(Context context, int resource, List<InstanciaDeJogo> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater li = LayoutInflater.from(getContext());
            convertView = li.inflate(R.layout.instancia_executando_item_lista, null);
        }

        InstanciaDeJogo instanciaDeJogo = getItem(position);

        if (instanciaDeJogo != null) {
            TextView titulo = (TextView) convertView.findViewById(R.id.instancia_executando_titulo);
            ImageView imagem = (ImageView) convertView.findViewById(R.id.instancia_executando_icone);
            titulo.setText(instanciaDeJogo.getNomeFicticio());
            DownloadImagem downloadImagem = new DownloadImagem(imagem);
            try {
                downloadImagem.execute(instanciaDeJogo.getIcone());
            } catch (Exception e) {
                Log.e(Constantes.TAG, e.getMessage());
                e.printStackTrace();
            }

            if(!instanciaDeJogo.isJogadorParticipando()){
                ImageView imagemGrupo = (ImageView) convertView.findViewById(R.id.instancia_executando_icone_grupo);
                imagemGrupo.setVisibility(View.INVISIBLE);
            }
        }
        return convertView;
    }
}
