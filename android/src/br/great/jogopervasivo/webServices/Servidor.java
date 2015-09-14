package br.great.jogopervasivo.webServices;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;

import br.great.jogopervasivo.LoginActivity;
import br.great.jogopervasivo.gcmUtil.InicializarGCM;
import br.great.jogopervasivo.util.Armazenamento;
import br.great.jogopervasivo.util.Constantes;
import br.great.jogopervasivo.util.InformacoesTemporarias;
import br.ufc.great.arviewer.android.R;

/**
 * Created by messiaslima on 02/02/2015.
 *
 * @author messiaslima
 * @version 1.0
 * @since 1.0
 */
public class Servidor {
    /**
     * Executa uma requisição HTTP de método GET ao servidor de aplicação do Jogo pervasivo
     *
     * @param json parametro da ação
     * @return reposta do servidor
     */
    public static String fazerGet(String json) {
        String uri = "/WebServidor/webresources/Servidor/getJogo?json=";
        String resultado = "";
        try {
            //URL url = new URL(Constantes.SERVIDOR_DE_APLICACAO + uri + json);
            URL url = new URL("http", Armazenamento.resgatarIP(LoginActivity.getInstance()), Armazenamento.resgatarPorta(LoginActivity.getInstance()), uri + json);
            Log.i(Constantes.TAG, Armazenamento.resgatarIP(LoginActivity.getInstance())+":"+Armazenamento.resgatarPorta(LoginActivity.getInstance())+ uri + json);
            URLConnection connection = url.openConnection();
            connection.setConnectTimeout(1000 * 60);
            DataInputStream input = new DataInputStream(connection.getInputStream());
            String linha;
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(input));
            StringBuffer newData = new StringBuffer();
            String s = "";
            while (null != ((s = bufferedReader.readLine()))) {
                newData.append(s);
            }
            resultado = newData.toString();
            input.close();

        } catch (MalformedURLException mue) {
            System.err.println("__URL mal formada");
            mue.printStackTrace();
        } catch (SocketTimeoutException stoe) {
            System.err.println("__Timeout de conexão");
            stoe.printStackTrace();
        } catch (IOException ioe) {
            System.err.println("__Erro de IO");
            ioe.printStackTrace();
        }

        Log.i(Constantes.TAG, "Resposta do servidor: " + resultado);
        return resultado;
    }

    /**
     * Faz a autenticação do usuário
     *
     * @param email email do usuário
     * @param senha senha do usuário
     * @return json object com o resultado e a mensagem especificando o erro
     */
    public static JSONObject fazerLogin(Context contexto, String email, String senha) {

        JSONArray jsonArray = new JSONArray();
        JSONObject acao = new JSONObject();
        JSONObject parametros = new JSONObject();

        try {
            acao.put("acao", 4);
            parametros.put("email", email);
            parametros.put("senha", senha);
            jsonArray.put(0, acao);
            jsonArray.put(1, parametros);
        } catch (JSONException e) {
            e.printStackTrace();
            return gerarResultado(false, contexto.getString(R.string.erro_formatacao_dados));
        }

        String resposta = fazerGet(jsonArray.toString());
        // Se a resposta veio vazia. Erro de conxão com a internet
        if (resposta.trim().length() == 0) {
            return gerarResultado(false, contexto.getString(R.string.falha_de_conexao));
        } else {
            JSONObject jsonObject;
            try {
                jsonObject = new JSONArray(resposta).getJSONObject(0);
            } catch (Exception ex) {
                ex.printStackTrace();
                return gerarResultado(false, "A resposta do servidor veio estranha");
            }

            if (jsonObject.optInt("id", 0) == 0) {
                return gerarResultado(false, contexto.getString(R.string.falha_de_autenticacao));
            } else {
                InformacoesTemporarias.idJogador = jsonObject.optInt("id");
                InformacoesTemporarias.nomeJogador = jsonObject.optString("email", contexto.getString(R.string.desconhecido));
                String idDispositivo = "";
                idDispositivo = jsonObject.optString("idDispositivo", "");
                InicializarGCM initGcm = new InicializarGCM(contexto, idDispositivo);
                initGcm.inicializar(); //inicializa o serviço de GCM , testa se está ativo ou não
                return gerarResultado(true, "");
            }
        }
    }

    /**
     * gera um jason para ser usado só internamente para fim de retorno de metodos
     *
     * @param mensagem  mensagem de especificação da ação
     * @param resultado boolean de sucesso da ação
     * @return JSON de resposta
     */
    private static JSONObject gerarResultado(boolean resultado, String mensagem) {
        JSONObject resposta = new JSONObject();
        try {
            resposta.put("resultado", resultado);
            resposta.put("mensagem", mensagem);
        } catch (JSONException je) {
            Log.e(Constantes.TAG, "Erro na formatação do JSON resposta em 'Servidor.gerarResultado()'");
        }
        return resposta;
    }

    /**
     * Cadastra o novo usuário
     *
     * @param context activity que chama esse método
     * @param email   nome do novo usuário
     * @param senha   senha do usuário
     * @return json de resultado
     */
    public static JSONObject cadastrarNovoUsuário(Context context, String email, String senha) {
        JSONArray jsonArray = new JSONArray();
        JSONObject acao = new JSONObject();
        JSONObject parametros = new JSONObject();

        try {
            acao.put("acao", 1);
            parametros.put("email", email);
            parametros.put("senha", senha);
            jsonArray.put(0, acao);
            jsonArray.put(1, parametros);

            String resposta = fazerGet(jsonArray.toString());
            JSONObject jsonObject = new JSONArray(resposta).getJSONObject(0);

            if (jsonObject.getString("result").equals("Salvo com sucesso!")) {
                return gerarResultado(true, context.getString(R.string.usuario_cadastrado));
            } else {
                return gerarResultado(false, context.getString(R.string.usuario_ja_cadastrado));
            }

        } catch (JSONException je) {
            je.printStackTrace();
            return gerarResultado(false, context.getString(R.string.erro_formatacao_dados));
        }
    }
}
