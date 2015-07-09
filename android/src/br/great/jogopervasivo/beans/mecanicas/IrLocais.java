package br.great.jogopervasivo.beans.mecanicas;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import br.ufc.great.arviewer.android.R;
import br.great.jogopervasivo.actvititesDoJogo.TelaPrincipalActivity;
import br.great.jogopervasivo.beans.Mecanica;
import br.great.jogopervasivo.util.Constantes;
import br.great.jogopervasivo.util.InformacoesTemporarias;
import br.great.jogopervasivo.webServices.Servidor;

/**
 * Created by messiaslima on 05/03/2015.
 *
 * @author messiaslima
 */
public class IrLocais extends Mecanica implements Imecanica {

    private int id;
    private LatLng posicao;

    public LatLng getPosicao() {
        return posicao;
    }

    public void setPosicao(LatLng posicao) {
        this.posicao = posicao;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public void realizarMecanica(final TelaPrincipalActivity context) {


        if (getEstado()==2){
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
       /*         JSONObject acao = new JSONObject();
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
                    confirmarRealizacao(context, null,null,null);
                    Toast.makeText(context, "Querida cheguei!", Toast.LENGTH_SHORT).show();
                }else{
                    mostarToastFeedback(context);
                }
            }
        }.execute();

    }
}
