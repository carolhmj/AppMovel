package br.great.jogopervasivo.webServices;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import br.ufc.great.arviewer.android.R;
import br.great.jogopervasivo.util.Constantes;

/**
 * Created by messiaslima on 02/02/2015
 * @author messiaslima
 * @version 1.0
 * @since 1.0
 */
public class CadastrarNovoUsuario extends AsyncTask<String, Void, String> {
    private Context contexto;
    private ProgressDialog progressDialog;

    public CadastrarNovoUsuario(Context context) {
        this.contexto = context;
    }

    @Override
    protected void onPreExecute() {
        progressDialog = new ProgressDialog(contexto);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(contexto.getString(R.string.salvando));
        progressDialog.show();
    }


    @Override
    protected String doInBackground(String... params) {
        return Servidor.fazerGet("/jogador/cadastrarJogador?email=" + params[0] + "&password=" + params[1]);
    }

    @Override
    protected void onPostExecute(String s) {
        progressDialog.dismiss();

        AlertDialog.Builder builder = new AlertDialog.Builder(contexto);
        builder.setPositiveButton(R.string.OK, null);
        builder.setCancelable(false);

        try {

            if (s.trim().length() == 0) {

                builder.setMessage(R.string.falha_de_conexao);
                builder.create().show();

            } else {

                JSONObject jsonObject = new JSONObject(s);

                boolean salvo = jsonObject.getBoolean("salvo");
                if (salvo) {
                    builder.setMessage(contexto.getString(R.string.usuario_cadastrado));
                    builder.create().show();
                } else {
                    builder.setMessage(contexto.getString(R.string.usuario_ja_cadastrado));
                    builder.create().show();
                }
            }

        } catch (JSONException je) {
            Log.e(Constantes.TAG, "erro no json \n" + je.getMessage());
            je.printStackTrace();
        }
    }
}
