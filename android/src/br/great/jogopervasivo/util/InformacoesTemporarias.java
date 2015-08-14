package br.great.jogopervasivo.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.provider.Settings;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.ufc.great.arviewer.android.R;
import br.great.jogopervasivo.actvititesDoJogo.TelaPrincipalActivity;
import br.great.jogopervasivo.beans.Grupo;
import br.great.jogopervasivo.beans.InstanciaDeJogo;
import br.great.jogopervasivo.beans.Jogo;
import br.great.jogopervasivo.beans.Jogador;
import br.great.jogopervasivo.beans.Mecanica;
import br.great.jogopervasivo.beans.Missao;
import br.great.jogopervasivo.beans.ObjetoInventario;

/**
 * Created by messiaslima on 11/02/2015.
 *
 * @author messiaslima
 * @version 1.0
 * @since 1.0
 */
public class InformacoesTemporarias {
    public static Jogo jogoPai;
    public static InstanciaDeJogo instanciaDeJogo;
    public static Grupo grupo;
    public static int idJogador = 0;
    public static String nomeJogador;
    public static InstanciaDeJogo jogoAtual;
    public static Grupo grupoAtual;
    public static List<Mecanica> mecanicasAtuais = null;
    public static boolean jogoOcupado = false;
    public static TelaPrincipalActivity contextoTelaPrincipal;
    public static String mensagem = null;
    public static String life = null;
    public static List<Jogador> marcadoresDeJogadores = new ArrayList<>();
    public static List<ObjetoInventario> inventario = new ArrayList<>();
    public static List<Grupo> grupos = new ArrayList<>();
    public static Map<String, Float> distanciaJogadores = new HashMap<>();
    public static Context contexto;
    public static List<Missao> missoesAtuais ;


    public static void verificarDistanciaJogadores() {
       /* if (grupoAtual.getTipoJogador() == Jogador.TIPO_CAPTURADOR) {
            Location localizacaoJogador = Armazenamento.resgatarUltimaLocalizacao(contextoTelaPrincipal);
            for (Grupo grupo : grupos) {
                if (grupo.getId() != grupoAtual.getId()) {
                    for (Jogador adversario : grupo.getJogadores()) {
                        Location localizacaoAdversario = MetodosUteis.latLngToLocation(LocationManager.GPS_PROVIDER, adversario.getPosicao());
                        Float distanciaAtual = localizacaoJogador.distanceTo(localizacaoAdversario);
                        Float distanciaSalva = distanciaJogadores.get(adversario.getNome());
                        if (distanciaSalva == null) {
                            distanciaJogadores.put(adversario.getNome(), distanciaAtual);
                        } else {
                            if (distanciaSalva < Constantes.LIMIAR_DE_PROXIMIDADE && distanciaAtual < Constantes.LIMIAR_DE_PROXIMIDADE) {
                               // diyasg
                            }
                        }
                    }
                }
            }
        }*/
    }


    public static Jogador getJogador(String nome) {
        for (Grupo g : grupos) {
            for (Jogador jogador : g.getJogadores()) {
                if (jogador.getNome().equals(nome)){
                    return jogador;
                }
            }
        }
        return null;
    }

    static File file;

    static {
        file = new File(Constantes.PASTA_DE_ARQUIVOS);
        if (!file.exists()) {
            file.mkdir();
        }
    }

    /**
     * Cria arquivo temporário de imagem
     *
     * @return File do arquivo temporário
     */
    public static File criarImagemTemporaria() {
        String fileName = "Great";
        String extension = ".jpg";
        File image = null;
        try {
            image = File.createTempFile(fileName, extension, file);
        } catch (IOException ioe) {
            Log.e("Debug", ioe.getMessage());
        }
        return image;
    }


    /**
     * Cria arquivo temporário de objeto 3d
     *
     * @return File do arquivo temporário
     */
    public static File criarObjTemporario() {
        String fileName = "GamePervasivoVideo";
        String extension = ".obj";
        File video = null;
        try {
            video = File.createTempFile(fileName, extension, file);
        } catch (Exception ioe) {
            Log.e("Debug", ioe.getMessage());
        }
        return video;
    }

    /**
     * Cria arquivo temporário de textura png
     *
     * @return File do arquivo temporário
     */
    public static File criarPNGTemporario() {
        String fileName = "GamePervasivo";
        String extension = ".png";
        File video = null;
        try {
            video = File.createTempFile(fileName, extension, file);
        } catch (Exception ioe) {
            Log.e("Debug", ioe.getMessage());
        }
        return video;
    }


    /**
     * Cria arquivo temporário de textura png
     *
     * @return File do arquivo temporário
     */
    public static File criarMTLTemporario() {
        String fileName = "GamePervasivo";
        String extension = ".mtl";
        File video = null;
        try {
            video = File.createTempFile(fileName, extension, file);
        } catch (Exception ioe) {
            Log.e("Debug", ioe.getMessage());
        }
        return video;
    }

    /**
     * Cria arquivo temporário de imagem
     *
     * @return File do arquivo temporário
     */
    public static File criarVideoTemporario() {
        String fileName = "GamePervasivoVideo";
        String extension = ".3gp";
        File video = null;
        try {
            video = File.createTempFile(fileName, extension, file);
        } catch (Exception ioe) {
            Log.e("Debug", ioe.getMessage());
        }
        return video;
    }

    /**
     * Cria arquivo temporário de som
     *
     * @return File do arquivo temporário
     */
    public static File criarAudioTemporario() {
        String fileName = "Great";
        String extension = ".mp3";
        File som = null;
        try {
            som = File.createTempFile(fileName, extension, file);
        } catch (IOException ioe) {
            Log.e("Debug", ioe.getMessage());
        }
        return som;
    }
    /*  Testa se existe conexão com a internet
        Coloquei em "InformacoesTemporarias" porque
        o estado da rede é bem variável
    */

    /**
     * Testa o estado da conexao
     *
     * @param context contexto da activity que chamou esse método
     * @return booleano se a conexão esta ativa ou nao
     */
    public static boolean conexaoAtiva(final Context context) {
        boolean conectado;
        ConnectivityManager conectivtyManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (conectivtyManager.getActiveNetworkInfo() != null
                && conectivtyManager.getActiveNetworkInfo().isAvailable()
                && conectivtyManager.getActiveNetworkInfo().isConnected()) {
            conectado = true;
        } else {
            conectado = false;
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(R.string.falha_de_conexao);
            builder.setMessage(R.string.ativar_conexao)
                    .setNegativeButton(R.string.cancelar, null)
                    .setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            context.startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
                        }
                    })
                    .create().show();


        }
        return conectado;
    }
}
