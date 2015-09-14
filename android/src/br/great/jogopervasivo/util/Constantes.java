package br.great.jogopervasivo.util;

import android.os.Environment;

import br.great.jogopervasivo.actvititesDoJogo.ConfiguracoesActivity;

/**
 * Created by messiaslima on 02/02/2015.
 *
 * @author messiaslima
 */
public class Constantes {

    public static final String SERVIDOR_DE_APLICACAO = "http://200.129.43.207:8083";
    public static final String SERVIDOR_DE_ARQUIVOS = "http://200.129.43.207/pervasivedb/";

    //public static final String SERVIDOR_DE_APLICACAO = "http://192.168.0.72:8080/WebServidor/webresources";
    //public static final String SERVIDOR_DE_ARQUIVOS = "http://192.168.0.48/pervasivebd/img/";

    //static String ip = Armazenamento.resgatarString(ConfiguracoesActivity.TAG_CONFIGURACAO_IP, InformacoesTemporarias.contexto);
    //static String porta = Armazenamento.resgatarString(ConfiguracoesActivity.TAG_CONFIGURACAO_PORTA, InformacoesTemporarias.contexto);


    //public static final String SERVIDOR_DE_APLICACAO = "http://" + ip + ":" + porta + "/WebServidor/webresources";
    //public static final String SERVIDOR_DE_ARQUIVOS = "http://" + ip + "/pervasivedb/";

    public static final String PASTA_DE_ARQUIVOS = Environment.getExternalStorageDirectory().getAbsolutePath() + "/GreatPervasiveGame";
    public static final String TAG = "Debug";
    public static final String JOGADOR_NOME = "jogador_nome";
    public static final String JOGADOR_ID = "jogador_id";
    public static final String REGISTRO_DISPOSITIVO = "registro_dispositivo";
    public static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    public static final String VERSAO_APP = "versao_app";
    public static final String JOGADOR_LATITUDE = "jogador_latitude";
    public static final String JOGADOR_LONGITUDE = "jogador_longitude";
    public static final String JOGO_EXECUTANDO = "jogo_executando";
    public static final String JOGO_PAI_ID = "jogo_pai_id";
    public static final String JOGO_PAI_NOME = "jogo_pai_nome";
    public static final String TIPO_MECANICA_VFOTOS = "vfotos";
    public static final String TIPO_MECANICA_VSONS = "vsons";
    public static final String TIPO_MECANICA_VVIDEOS = "vvideos";
    public static final String TIPO_MECANICA_V_OBJ_3D = "vobjeto3d";
    public static final String TIPO_MECANICA_IRLOCAIS = "irlocais";
    public static final String TIPO_MECANICA_VTEXTOS = "vtextos";
    public static final String TIPO_MECANICA_CSONS = "csons";
    public static final String TIPO_MECANICA_CFOTOS = "cfotos";
    public static final String TIPO_MECANICA_CTEXTOS = "ctextos";
    public static final String TIPO_MECANICA_CVIDEOS = "cvideos";
    public static final String TIPO_MECANICA_DFOTOS = "dfotos";
    public static final String TIPO_MECANICA_DOBJETOS3D = "dobjeto3d";
    public static final String TIPO_MECANICA_DSONS = "dsons";
    public static final String TIPO_MECANICA_DVIDEOS = "dvideos";
    public static final String TIPO_MECANICA_DTEXTOS = "dtextos";
    public static final int LIMIAR_DE_PROXIMIDADE = 20;


}
