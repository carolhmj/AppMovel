package br.great.jogopervasivo.beans.mecanicas;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;

import java.io.File;

import br.great.jogopervasivo.actvititesDoJogo.TelaPrincipalActivity;
import br.great.jogopervasivo.beans.Mecanica;
import br.great.jogopervasivo.util.InformacoesTemporarias;
import br.ufc.great.arviewer.android.R;

/**
 * Created by messiaslima on 12/03/2015.
 *
 * @author messiaslima
 */
public class CFotos extends Mecanica implements Imecanica {
    private int idFotos;

    public static String pathDeImagem;

    public int getIdFotos() {
        return idFotos;
    }

    public void setIdFotos(int id) {
        this.idFotos = id;
    }

    @Override
    public void realizarMecanica(final TelaPrincipalActivity context) {

        //Caso a mecanica não possa ser realizada
        if (getEstado() == 2) {
            return;
        }

        new AsyncTask<Void, Void, Boolean>() {
            ProgressDialog progressDialog;

            @Override
            protected void onPreExecute() {
                progressDialog = new ProgressDialog(context);
                progressDialog.setCancelable(false);
                progressDialog.setMessage(context.getString(R.string.obtendo_informacoes));
                super.onPreExecute();
            }

            @Override
            protected Boolean doInBackground(Void... params) {
               /* JSONObject acao = new JSONObject();
                JSONObject mecanica = new JSONObject();
                JSONArray requisiscao = new JSONArray();
                try {
                    acao.put("acao", 107);
                    mecanica.put("jogo_id", InformacoesTemporarias.jogoAtual.getId());
                    mecanica.put("grupo_id", InformacoesTemporarias.grupoAtual.getId());
                    mecanica.put("mecanica_id", getId());
                    mecanica.put("jogador_id", InformacoesTemporarias.idJogador);
                    requisiscao.put(0, acao);
                    requisiscao.put(1, mecanica);
                    JSONObject resposta = new JSONArray(Servidor.fazerGet(requisiscao.toString())).getJSONObject(0);
                    return resposta.getInt("result") != 0;
                } catch (JSONException je) {
                    Log.e(Constantes.TAG, "erro no json " + je.getMessage());
                    je.printStackTrace();
                    return false;
                }*/

                return verificarAutorizacaoDaMecanica();
            }


            @Override
            protected void onPostExecute(Boolean aBoolean) {
                if (aBoolean) {

                    InformacoesTemporarias.jogoOcupado = true;
                    File imagem = InformacoesTemporarias.criarImagemTemporaria(); //Cria um arquivo temporário de imagem

                    //Coloca o path do arquivo temporário numa variável estática para ser recuperada da Activity principal
                    CFotos.pathDeImagem = imagem.getAbsolutePath();

                    //Chama a intent da camera
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(imagem));
                    context.startActivityForResult(intent, TelaPrincipalActivity.REQUEST_CODE_FOTO);

                } else {
                    mostarToastFeedback(context);
                }
            }
        }.execute();


    }
}
