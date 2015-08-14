package br.great.jogopervasivo.beans.mecanicas;

import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;

import br.great.jogopervasivo.actvititesDoJogo.TelaPrincipalActivity;
import br.great.jogopervasivo.beans.Mecanica;
import br.great.jogopervasivo.util.Armazenamento;
import br.ufc.great.arviewer.android.AndroidLauncher;
import br.ufc.great.arviewer.android.R;

/**
 * Created by messiaslima on 07/07/2015.
 *
 * @author messiaslima
 * @version 1.0
 */
public class VObj3d extends Mecanica implements Imecanica {
    private String arqObj3d;
    private String arqTextura;

    public String getArqObj3d() {
        return arqObj3d;
    }

    public void setArqObj3d(String arqObj3d) {
        this.arqObj3d = arqObj3d;
    }

    public String getArqTextura() {
        return arqTextura;
    }

    public void setArqTextura(String arqTextura) {
        this.arqTextura = arqTextura;
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

                super.onPreExecute();
            }

            @Override
            protected Boolean doInBackground(Void... params) {
              /*  JSONObject acao = new JSONObject();
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
                    Intent intent = new Intent(context, AndroidLauncher.class);
                    intent.putExtra("NOME_OBJETO", getArqObj3d());
                    intent.putExtra("NOME_TEXTURA", getArqTextura());
                    intent.putExtra("LAT_OBJETO", getLocalizacao().latitude);
                    intent.putExtra("LON_OBJETO", getLocalizacao().longitude);
                    Location location = Armazenamento.resgatarUltimaLocalizacao(context);
                    intent.putExtra("LAT_JOGADOR", location.getLatitude());
                    intent.putExtra("LON_JOGADOR", location.getLongitude());
                    TelaPrincipalActivity.mecanicaVObj3dAtual = VObj3d.this;
                    context.startActivityForResult(intent, TelaPrincipalActivity.REQUEST_CODE_VER_OBJ_3D);
                } else {
                    mostarToastFeedback(context);
                }
            }
        }.execute();
    }
}
