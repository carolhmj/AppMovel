package br.great.jogopervasivo.webServices;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import br.ufc.great.arviewer.android.R;
import br.great.jogopervasivo.arrayAdapters.JogosDisponiveisAdapter;
import br.great.jogopervasivo.beans.Jogo;
import br.great.jogopervasivo.util.Constantes;

/**
 * Created by messiaslima on 03/02/2015.
 *
 * @author messiaslima
 * @version 1.0
 * @since 1.0
 */
public class RecuperarJogosDisponiveis extends AsyncTask<String, Void, String> {

    private Context context;
    private ProgressDialog progressDialog;
    private ListView listView;

    public RecuperarJogosDisponiveis(Context context, ListView listView) {
        this.context = context;
        this.listView = listView;
    }

    @Override
    protected void onPreExecute() {
        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(context.getString(R.string.buscando_jogos));
        progressDialog.show();
    }

    @Override
    protected String doInBackground(String... params) {

        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject = new JSONObject();
        JSONObject jsonObject1 = new JSONObject();

        try {
            jsonObject.put("acao",101);
            jsonObject1.put("latitude",params[0]);
            jsonObject1.put("longitude",params[1]);
            jsonObject1.put("distancia",params[2]);
            jsonArray.put(0,jsonObject);
            jsonArray.put(1,jsonObject1);
        } catch (JSONException je) {
            je.printStackTrace();
        }
        return Servidor.fazerGet(jsonArray.toString());

    }

    @Override
    protected void onPostExecute(String s) {
        progressDialog.dismiss();
        List<Jogo> jogosDisponiveis = new ArrayList<Jogo>();
        try {
            JSONArray jsonArray = new JSONArray(s);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                Jogo jogo = new Jogo();
                jogo.setId(jsonObject.getInt("id"));
                jogo.setNome(jsonObject.getString("nome"));
                jogo.setIcone(jsonObject.getString("icone"));
                jogosDisponiveis.add(jogo);
            }


        } catch (JSONException je) {
            Log.e(Constantes.TAG, "Erro no json \n" + je.getMessage());
            je.printStackTrace();
            jogosDisponiveis = null;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setPositiveButton(R.string.OK, null);
        if (jogosDisponiveis == null) {
            builder.setMessage(R.string.falha_de_conexao);
            builder.create().show();
        } else {
            if (jogosDisponiveis.size() == 0) {
                builder.setMessage(R.string.sem_jogos_disponiveis);
                builder.create().show();
            } else {
                JogosDisponiveisAdapter adapter = new JogosDisponiveisAdapter(context, R.layout.jogos_disponiveis_item_lista, jogosDisponiveis);
                listView.setAdapter(adapter); //Preenche o listView com os jogos disponíveis
            }
        }
    }
}
