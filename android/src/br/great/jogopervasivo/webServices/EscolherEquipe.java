package br.great.jogopervasivo.webServices;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import br.ufc.great.arviewer.android.R;
import br.great.jogopervasivo.arrayAdapters.EscolherGrupoAdapter;
import br.great.jogopervasivo.beans.Grupo;
import br.great.jogopervasivo.util.Armazenamento;
import br.great.jogopervasivo.util.Constantes;
import br.great.jogopervasivo.util.InformacoesTemporarias;

/**
 * Created by messiaslima on 10/02/2015.
 * @author messiaslima
 * @since 1.0
 * @version 1.0
 */
public class EscolherEquipe extends AsyncTask<Integer, Void, List<Grupo>> {
    private Context context;
    private ProgressDialog progressDialog;

    public EscolherEquipe(Context context) {
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(context.getString(R.string.obtendo_informacoes));
        progressDialog.show();
    }

    @Override
    protected List<Grupo> doInBackground(Integer... params) {
        JSONArray jsonArrayReq = new JSONArray();
        JSONObject jsonObjectReq = new JSONObject();
        JSONObject jsonObject1Req = new JSONObject();

        try {
            jsonObjectReq.put("acao", 104);
            jsonObject1Req.put("jogo_id", params[0]);
            jsonObject1Req.put("jogador_id",InformacoesTemporarias.idJogador);
            jsonArrayReq.put(0, jsonObjectReq);
            jsonArrayReq.put(1, jsonObject1Req);
        } catch (JSONException je) {
            je.printStackTrace();
        }

        String resposta = Servidor.fazerGet(jsonArrayReq.toString());
        try {
            JSONArray jsonArray = new JSONArray(resposta).getJSONArray(0);
            List<Grupo> grupos = new ArrayList<>();

            for (int i = 0; i < jsonArray.length(); i++) {
                Grupo grupo = new Grupo();
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                grupo.setId(jsonObject.getInt("id"));
                grupo.setNome(jsonObject.getString("nome"));

                JSONArray jogadoresJson = jsonObject.getJSONArray("jogadores");
                for(int j = 0 ; j< jogadoresJson.length(); j++){

                }
                grupos.add(grupo);
            }
            return grupos;
        } catch (JSONException je) {
            Log.e(Constantes.TAG, "Erro o json \n" + je.getMessage());
            je.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(final List<Grupo> grupos) {
        progressDialog.dismiss();

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        if (grupos == null) {
        builder.setMessage(R.string.falha_de_conexao)
                .setNegativeButton(R.string.OK,null)
                .create().show();

        } else {

            builder.setTitle(R.string.escolha_equipe);
            final EscolherGrupoAdapter adapter = new EscolherGrupoAdapter(context, R.layout.grupos_item_lista, grupos);
            builder.setNegativeButton(R.string.cancelar, null);
            builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    InformacoesTemporarias.grupo = adapter.getItem(which);
                    Location localizacao = Armazenamento.resgatarUltimaLocalizacao(context);
                    InicializarJogo inicializarJogo = new InicializarJogo(context,InformacoesTemporarias.instanciaDeJogo,InformacoesTemporarias.grupo,
                            localizacao.getLatitude(),localizacao.getLongitude());
                    inicializarJogo.execute();
                    Log.i(Constantes.TAG,"Inicializando o jogo");
                }
            });
            builder.create().show();
        }
    }
}
