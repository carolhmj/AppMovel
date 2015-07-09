package br.great.jogopervasivo.beans.mecanicas;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;

import br.great.jogopervasivo.actvititesDoJogo.TelaPrincipalActivity;
import br.great.jogopervasivo.beans.Mecanica;
import br.great.jogopervasivo.util.InformacoesTemporarias;
import br.ufc.great.arviewer.android.R;

/**
 * Created by messiaslima on 06/03/2015.
 *
 * @author messiaslima
 */
public class Vtextos extends Mecanica implements Imecanica {
    private String texto;
    private int id;

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public void realizarMecanica(final TelaPrincipalActivity context) {

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
            }

            @Override
            protected Boolean doInBackground(Void... params) {
           /*     JSONObject acao = new JSONObject();
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
                progressDialog.dismiss();
                if (aBoolean) {
                    InformacoesTemporarias.jogoOcupado = true;
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setCancelable(false)
                            .setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    confirmarRealizacao(context, null, null, null);
                                }
                            })
                            .setTitle(getNome())
                            .setMessage(getTexto())
                            .create()
                            .show();
                } else {
                    mostarToastFeedback(context);
                }
            }
        }.execute();


    }
}
