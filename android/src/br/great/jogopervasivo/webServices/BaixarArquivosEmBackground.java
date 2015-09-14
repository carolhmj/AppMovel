package br.great.jogopervasivo.webServices;


import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

import br.great.jogopervasivo.beans.Arquivo;
import br.great.jogopervasivo.util.Constantes;
import br.great.jogopervasivo.util.InformacoesTemporarias;
import br.ufc.great.arviewer.android.R;

/**
 * Created by messiaslima on 11/02/2015.
 *
 * @author messiaslima
 */
public class BaixarArquivosEmBackground extends AsyncTask<Void, Void, Boolean> {

    List<Arquivo> arquivos;
    Context context;

    public BaixarArquivosEmBackground(List<Arquivo> arquivos, Context context) {
        this.arquivos = arquivos;
        this.context = context;
    }


    @Override
    protected Boolean doInBackground(Void... params) {

        while (true) {

            if (arquivos.size() == 0) {
                Log.e(Constantes.TAG,"Sem arquivos a serem baixados em background");
                break;
            }

            for (Arquivo a : arquivos) {
                boolean success = false;
                while (!success) {
                    success = a.baixar(context);
                    if (success) {
                        Log.i(Constantes.TAG,"Tentou baixar arquivo"+ a.getArquivo());
                        a.setBaixado(true);
                    }
                    if (!InformacoesTemporarias.conexaoAtiva(context)) {
                        Log.e(Constantes.TAG,"Sem conexão de rede");
                        return false;
                    }
                }
            }

            for (Arquivo a : arquivos) {
                if (a.isBaixado()) {
                    arquivos.remove(a);
                }
            }
        }
        return true;
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        if (!aBoolean) {
            Toast.makeText(context, R.string.falha_de_conexao, Toast.LENGTH_LONG).show();
        }
        super.onPostExecute(aBoolean);

    }
}
