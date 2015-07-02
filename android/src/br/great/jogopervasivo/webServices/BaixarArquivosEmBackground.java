package br.great.jogopervasivo.webServices;


import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import br.great.jogopervasivo.beans.Arquivo;

import br.great.jogopervasivo.util.Armazenamento;
import br.great.jogopervasivo.util.Constantes;

/**
 * Created by messiaslima on 11/02/2015.
 *
 * @author messiaslima
 * ainda em desenvolvimento
 *
 */
public class BaixarArquivosEmBackground extends AsyncTask<Void, Void, Boolean> {

    private int jogo_id, grupo_id;
    private Double latitude, longitude;


    public BaixarArquivosEmBackground(int jogo_id, int grupo_id, Double latitude, Double longitude) {
        this.jogo_id = jogo_id;
        this.grupo_id = grupo_id;
        this.latitude = latitude;
        this.longitude = longitude;
    }


    @Override
    protected Boolean doInBackground(Void... params) {
      return null;
    }
}
