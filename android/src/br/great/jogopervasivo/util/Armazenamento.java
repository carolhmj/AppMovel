package br.great.jogopervasivo.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

import br.great.jogopervasivo.LoginActivity;
import br.great.jogopervasivo.actvititesDoJogo.ConfiguracoesActivity;

/**
 * Created by messiaslima on 02/02/2015.
 *
 * @author messiaslima
 * @version 1.0
 * @since 1.0
 */
public class Armazenamento {

    public static String resgatarIP(Context context){
     String ip =  resgatarString(ConfiguracoesActivity.TAG_CONFIGURACAO_IP, context);
        if (ip.trim().length()==0){
            return "200.129.43.207";
        }
        return ip;
    }
    public static int resgatarPorta(Context context){
        int porta =  resgatarInt(ConfiguracoesActivity.TAG_CONFIGURACAO_PORTA, context);
        if (porta==-1){
            return 8083;
        }
        return porta;
    }


    public static String resgatarIPArquivos(Context context){
        String ip =  resgatarString(ConfiguracoesActivity.TAG_CONFIGURACAO_IP_ARQUIVOS, context);
        if (ip.trim().length()==0){
            salvar(ConfiguracoesActivity.TAG_CONFIGURACAO_IP_ARQUIVOS,"200.129.43.207",context);
            return "http://"+resgatarIP(context)+"/pervasivedb/";
        }
        return "http://"+ip+"/pervasivedb/";
    }


    /**
     * Método que salva alocalização atual do jogador
     *
     * @param location localização do logador
     * @param context  contexto da activity principal
     */
    public static void salvarLocalizacao(final Location location, Context context) {
        SharedPreferences preferences = novoPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(Constantes.JOGADOR_LATITUDE, Double.toString(location.getLatitude()));
        editor.putString(Constantes.JOGADOR_LONGITUDE, Double.toString(location.getLongitude()));
        editor.commit();
    }


    //resgatar ultima posição salva

    /**
     * Recupera a ultima localizacao salva no sharedPreferences
     *
     * @param context contexto da activity principal
     */
    public static Location resgatarUltimaLocalizacao(Context context) {
        SharedPreferences prefs = novoPreferences(context);
        Double latitude = Double.parseDouble(prefs.getString(Constantes.JOGADOR_LATITUDE, "0"));
        Double longitude = Double.parseDouble(prefs.getString(Constantes.JOGADOR_LONGITUDE, "0"));
        Location localizacao = new Location(LocationManager.GPS_PROVIDER);
        localizacao.setLatitude(latitude);
        localizacao.setLongitude(longitude);
        if (latitude == 0 && longitude == 0) {
            return null;
        }
        return localizacao;
    }

    /**
     * Salva valores booleanos no sharedPreferences
     *
     * @param context contexto da activity principal
     * @param valor   valor da variavel a ser salva
     * @param tag     constante que identifica o valor
     */
    public static void salvar(String tag, boolean valor, Context context) {
        SharedPreferences preferences = novoPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(tag, valor);
        editor.commit();
    }

    /**
     * Salva valores String no sharedPreferences
     *
     * @param context contexto da activity principal
     * @param valor   valor da variavel a ser salva
     * @param tag     constante que identifica o valor
     */
    public static void salvar(String tag, String valor, Context context) {
        SharedPreferences preferences = novoPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(tag, valor);
        editor.commit();
    }

    /**
     * Salva valores inteiros no sharedPreferences
     *
     * @param context contexto da activity principal
     * @param valor   valor da variavel a ser salva
     * @param tag     constante que identifica o valor
     */
    public static void salvar(String tag, int valor, Context context) {
        SharedPreferences preferences = novoPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(tag, valor);
        editor.commit();
    }

    /**
     * Recupera valores String salvas no sharedPreferences
     *
     * @param context contexto da activity principal
     * @param tag     constante que identifica o valor
     * @return valor solicitado
     */
    public static String resgatarString(String tag, Context context) {
        SharedPreferences prefs = novoPreferences(context);
        return prefs.getString(tag, "");
    }

    /**
     * Recupera valores booleanos salvas no sharedPreferences
     *
     * @param context contexto da activity principal
     * @param tag     constante que identifica o valor
     * @return valor solicitado
     */
    public static boolean resgatarBoolean(String tag, Context context) {
        SharedPreferences prefs = novoPreferences(context);
        return prefs.getBoolean(tag, false);
    }

    /**
     * Recupera valores inteiros salvas no sharedPreferences
     *
     * @param context contexto da activity principal
     * @param tag     constante que identifica o valor
     * @return valor solicitado
     */
    public static int resgatarInt(String tag, Context context) {
        SharedPreferences prefs = novoPreferences(context);
        return prefs.getInt(tag, -1);
    }

    /**
     * Recupera o registro GCM do dispositivo salvo pela última vez
     *
     * @param context contexto da activity principal
     * @return ultimo registro GCM salvo
     */
    public static String resgatarRegistroDoDispositivo(Context context) {
        SharedPreferences prefs = novoPreferences(context);
        String registrationId = prefs.getString(Constantes.REGISTRO_DISPOSITIVO, "");
        if (registrationId.trim().length() == 0) {
            Log.i(Constantes.TAG, "Registratro não encontrado");
            return "";
        }
        int registeredVersion = prefs.getInt(Constantes.VERSAO_APP, Integer.MIN_VALUE);
        int currentVersion = Armazenamento.pegarVersaoDoAplicativo(context);

        if (registeredVersion != currentVersion) {
            Log.i(Constantes.TAG, "A versão do aplicativo mudou");
            return "";
        }
        return registrationId;
    }

    /**
     * Salva registro GCM no sharedPreferences
     *
     * @param context contexto da activity principal
     * @param regId   numero de registro recebido do servidor do Google
     */
    public static void salvarRegistroDoDispositivo(Context context, String regId) {
        SharedPreferences prefs = novoPreferences(context);
        int appVersion = Armazenamento.pegarVersaoDoAplicativo(context);

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Constantes.REGISTRO_DISPOSITIVO, regId);
        editor.putInt(Constantes.VERSAO_APP, appVersion);
        editor.commit();
    }

    /**
     * Factory de instancias de SharedPreferences
     *
     * @param context
     * @return instancia de SharedPreferences
     */
    private static SharedPreferences novoPreferences(Context context) {
        return context.getSharedPreferences(LoginActivity.class.getSimpleName(), Context.MODE_PRIVATE);
    }

    /**
     * @return versão do aplicativo
     */
    public static int pegarVersaoDoAplicativo(Context context) {
        try {
            PackageInfo pi = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return pi.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(Constantes.TAG, "Pacote não encontrado");
        }
        return 0;
    }
}
