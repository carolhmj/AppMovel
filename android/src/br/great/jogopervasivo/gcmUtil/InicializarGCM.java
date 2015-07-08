package br.great.jogopervasivo.gcmUtil;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import br.great.jogopervasivo.util.Armazenamento;
import br.great.jogopervasivo.util.Constantes;
import br.great.jogopervasivo.util.InformacoesTemporarias;
import br.great.jogopervasivo.webServices.Servidor;

/**
 * Created by messiaslima on 02/02/2015.
 *
 * @author messiaslima
 *         version 2.0
 */

public class InicializarGCM {
    private Activity activity;
    private String registroRecebido;
    private GoogleCloudMessaging gcm;
    private String SENDER_ID = "613942689940"; // Project Number do Google project

    public InicializarGCM(Context context, String registroRecebido) {
        this.activity = (Activity) context;
        this.registroRecebido = registroRecebido;
    }

    /**
     * Inicializa o processo de registro do dispositivo nos servidores
     */
    public void inicializar() {

        if (verificarPlayServices()) { //Verifica o Play services
            //Log.i(Constantes.TAG, "Google play services instalado");
            gcm = GoogleCloudMessaging.getInstance(activity);
            String registroSalvo = Armazenamento.resgatarRegistroDoDispositivo(activity);
            Log.e(Constantes.TAG, "Registro recebido é: " + registroRecebido);
            if (registroSalvo.trim().length() == 0) {
                Log.e(Constantes.TAG, "Sem registro salvo");
                registrarDispositivo();
            } else {
                if (!registroSalvo.equals(registroRecebido)) {
                    Log.e(Constantes.TAG, "Registro recebido do servidor é diferente \n o registro salvo é: " + registroSalvo);
                    registrarDispositivo();
                }
            }
        }else{

        }
    }

    /**
     * Faz a requisição aos servidores pra registarar o dispositivo
     */
    private void registrarDispositivo() {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                try {

                    String registroAtual = gcm.register(SENDER_ID); //Registra o device no servidor do Google
                    Armazenamento.salvarRegistroDoDispositivo(activity, registroAtual);//Salva no SharedPreferences
                    Log.i(Constantes.TAG, "Registro atual: " + registroAtual);
                    //Registra no servidor do jogo
                    JSONArray jsonArray = new JSONArray();
                    try {

                        JSONObject jsonObject2 = new JSONObject();
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("acao", 5);
                        jsonObject2.put("jogador_id", InformacoesTemporarias.idJogador);
                        jsonObject2.put("idDispositivo", registroAtual);
                        jsonArray.put(0, jsonObject);
                        jsonArray.put(1, jsonObject2);
                    } catch (JSONException je) {
                        je.printStackTrace();
                    }
                    String resposta = Servidor.fazerGet(jsonArray.toString());
                    Log.i(Constantes.TAG, "Feedback de inicializar GCM: " + resposta);
                } catch (IOException e) {
                    Log.e(Constantes.TAG, e.getMessage());
                    e.printStackTrace();
                }
                return null;
            }
        }.execute();
    }

    /**
     * Verifica se o playservices esta instalado
     */
    private boolean verificarPlayServices() {
        final int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(activity);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
               // GooglePlayServicesUtil.getErrorDialog(resultCode, activity, Constantes.PLAY_SERVICES_RESOLUTION_REQUEST);
            } else {
                Toast.makeText(activity.getApplicationContext(), "Play services sem suporte", Toast.LENGTH_SHORT).show();
            }
            return false;
        }
        return true;
    }
}
