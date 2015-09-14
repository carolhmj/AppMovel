package br.great.jogopervasivo.beans;

import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.Expose;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import br.ufc.great.arviewer.android.R;
import br.great.jogopervasivo.actvititesDoJogo.TelaPrincipalActivity;
import br.great.jogopervasivo.util.Constantes;
import br.great.jogopervasivo.util.InformacoesTemporarias;
import br.great.jogopervasivo.webServices.Servidor;

/**
 * Created by messiaslima on 10/03/2015.
 *
 * @author messiaslima
 */
public class Jogador {

    public static final int TIPO_NEUTRO = 0;
    public static final int TIPO_CAPTURAVEL = 1;
    public static final int TIPO_CAPTURADOR = 2;
    public static final int TIPO_HIBRIDO = 3;

    @Expose
    private String nome;
    private int tipo;
    private LatLng posicao;
    private Grupo grupo;
    private int vida;
    @Expose
    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getVida() {
        return vida;
    }

    public void setVida(int vida) {
        this.vida = vida;
    }

    public Grupo getGrupo() {
        return grupo;
    }

    public void setGrupo(Grupo grupo) {
        this.grupo = grupo;
    }

    public String getNome() {
        return nome;
    }

    public LatLng getPosicao() {
        return posicao;
    }

    public void setPosicao(LatLng posicao) {
        this.posicao = posicao;
    }

    public int getTipo() {
        return tipo;
    }

    public void setTipo(int tipo) {
        this.tipo = tipo;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    //Quando um jogador toca no marcador de outro
    public void capturar(final Context context) {
        new AsyncTask<Void, Void, Boolean>() {


            @Override
            protected Boolean doInBackground(Void... params) {

                JSONArray jsonArrayReq = new JSONArray();
                JSONObject jsonObjectReq = new JSONObject();
                JSONObject jsonObject1Req = new JSONObject();
                JSONObject jsonObject2Req = new JSONObject();

                try {
                    jsonObjectReq.put("acao", 111);
                    jsonObject1Req.put("jogo_id", InformacoesTemporarias.jogoAtual.getId());
                    jsonObject1Req.put("grupo_id",InformacoesTemporarias.grupoAtual.getId());
                    jsonObject1Req.put("jogador_id", InformacoesTemporarias.idJogador);
                    jsonObject2Req.put("jogadorcapturado_id", getId());
                    jsonArrayReq.put(0, jsonObjectReq);
                    jsonArrayReq.put(1, jsonObject1Req);
                    jsonArrayReq.put(2, jsonObject2Req);
                } catch (JSONException je) {
                    je.printStackTrace();
                }

                String resposta = Servidor.fazerGet(jsonArrayReq.toString());
                return resposta.contains("true");
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                if (aBoolean) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle(R.string.app_name);
                    builder.setMessage(context.getString(R.string.capturou_jogador) + ": " + getNome());
                    builder.create().show();
                    ((TelaPrincipalActivity) context).diminuirOpacidadeMarcador(getNome());
                } else {
                    Toast.makeText(context.getApplicationContext(), R.string.nao_pode_capturar_jogador, Toast.LENGTH_LONG).show();
                }
            }
        }.execute();
    }

    @Override
    public String toString() {
        return nome;
    }
}
