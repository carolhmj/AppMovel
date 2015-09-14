package br.great.jogopervasivo.webServices;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import br.great.jogopervasivo.beans.Arquivo;
import br.great.jogopervasivo.beans.Grupo;
import br.great.jogopervasivo.beans.InstanciaDeJogo;
import br.great.jogopervasivo.util.Armazenamento;
import br.great.jogopervasivo.util.Constantes;
import br.great.jogopervasivo.util.InformacoesTemporarias;
import br.ufc.great.arviewer.android.R;

/**
 * Created by messiaslima on 11/02/2015.
 *
 * @author messiaslima
 * @version 1.0
 * @since 1.0
 */
public class InicializarJogo extends AsyncTask<Void, String, Boolean> {

    private InstanciaDeJogo jogo;
    private Grupo grupo;
    private Context context;
    private Double latitude, longitude;
    private ProgressDialog progressDialog;
    private String mensagem = "";

    /**
     * @param context   contexto da activity que instcia a classe
     * @param grupo     grupo em que o jogador vai entrar
     * @param jogo      instancia do jogo a ser jogado
     * @param latitude  latitude atual do jogador
     * @param longitude longitude atual do jogador
     */
    public InicializarJogo(Context context, InstanciaDeJogo jogo, Grupo grupo, Double latitude, Double longitude) {
        this.jogo = jogo;
        this.grupo = grupo;
        this.latitude = latitude;
        this.longitude = longitude;
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(context.getString(R.string.baixando_arquivos));
        progressDialog.show();
    }

    @Override
    protected void onProgressUpdate(String... values) {
        progressDialog.setMessage(values[0]);
        if (values[1] != null) {
            progressDialog.setProgress(Integer.parseInt(values[1]));
        }
    }


    @Override
    protected Boolean doInBackground(Void... params) {
        boolean resposta;
        if (jogo.getGrupoId() == 0) {
            resposta = baixarArquivosIniciais();//Primeiro tenta baixar os arquivos necessarios pra inicialização do jogo
            if (!resposta) {
                mensagem = context.getString(R.string.baixando_arquivos_erro);
                return resposta;
            }
        }
        Log.i(Constantes.TAG, "Terminou de baixar os arquivos");
        //Caso não dê nenhum erro ele registra o jogador no jogo no servidor

        publishProgress(context.getString(R.string.registrando_jogador), null);
        resposta = registrarJogadorNoJogo();
        if (resposta) {
            return resposta;
        } else {
            mensagem = context.getString(R.string.erro_registrando_jogador);
            return resposta;
        }
    }

    @Override
    protected void onPostExecute(Boolean s) {
        progressDialog.dismiss();
        if (s) {
            //Salvando informaçoes do novo jogo e grupo no banco de dados
            InformacoesTemporarias.jogoAtual = jogo;
            InformacoesTemporarias.grupoAtual = grupo;
            Armazenamento.salvar(Constantes.JOGO_EXECUTANDO, true, context);//Diz que tem jogo executando;
            ((Activity) context).finish(); //Faz casting do Context para activity  e fecha a janela.
            RecuperarObjetosInventario.recuperar(context);
        } else {
            //Se der errado, ele mostra um AlertDialog
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage(mensagem)
                    .setNeutralButton(R.string.OK, null)
                    .create().show();
        }
    }

    /**
     * Registra o jogador no jogo escolhido
     *
     * @return booleano de sucesso ou não do registro
     */
    private boolean registrarJogadorNoJogo() {
        JSONArray jsonArrayReq = new JSONArray();
        JSONObject jsonObjectReq = new JSONObject();
        JSONObject jsonObject1Req = new JSONObject();

        try {
            jsonObjectReq.put("acao", 2);
            jsonObject1Req.put("jogo_id", jogo.getId());
            jsonObject1Req.put("grupo_id", grupo.getId());
            jsonObject1Req.put("jogador_id", InformacoesTemporarias.idJogador);
            jsonArrayReq.put(0, jsonObjectReq);
            jsonArrayReq.put(1, jsonObject1Req);
        } catch (JSONException je) {
            je.printStackTrace();
        }

        String resposta = Servidor.fazerGet(jsonArrayReq.toString());
        if (resposta.equals("[{\"result\":\"true\"}]")) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Baixa os arquivos necessários para a inicialização do jogo e inicia o download dos secundários em background
     */
    private boolean baixarArquivosIniciais() {
        JSONArray jsonArrayReq = new JSONArray();
        JSONObject jsonObjectReq = new JSONObject();
        JSONObject jsonObject1Req = new JSONObject();

        try {
            jsonObjectReq.put("acao", 109);
            jsonObject1Req.put("jogo_id", jogo.getId());
            jsonObject1Req.put("grupo_id", grupo.getId());
            jsonObject1Req.put("latitude", latitude);
            jsonObject1Req.put("longitude", longitude);
            jsonArrayReq.put(0, jsonObjectReq);
            jsonArrayReq.put(1, jsonObject1Req);
        } catch (JSONException je) {
            je.printStackTrace();
        }

        String resposta = Servidor.fazerGet(jsonArrayReq.toString());

        if (resposta.equals("[[]]")) {//Se não tiver nenhum arquivo a ser baixado
            return true;
        }

        if (resposta.trim().length() == 0) { // se tiver dado erro de conexão com o servidor e ele retornou uma string vazia
            return false;
        }

        try {
            JSONArray jsonArray = new JSONArray(resposta);

            List<Arquivo> arquivos = new ArrayList<>();

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject arquivoObject = jsonArray.getJSONObject(i);
                Arquivo arquivo = new Arquivo();
                arquivo.setPrioridade(arquivoObject.getInt("prioridade"));
                arquivo.setTipo(arquivoObject.getString("tipo"));
                arquivo.setArquivo(arquivoObject.getString("arquivo"));
                arquivo.setTextura(arquivoObject.optString("textura"));
                arquivo.setArqmtl(arquivoObject.optString("arqmtl"));
                arquivos.add(arquivo);
            }

            List<Arquivo> arquivosPrioritarios = new ArrayList<>();
            List<Arquivo> arquivosBackgroung = new ArrayList<>();

            for (Arquivo a : arquivos) { //Coloca os arquivos prioritários em um Array diferente
                if (a.getPrioridade() == 1) {
                    arquivosPrioritarios.add(a);
                }else{
                    arquivosBackgroung.add(a);
                }
            }

            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL); //Muda o ProgressDialog para barra de progresso
            progressDialog.setMax(arquivosPrioritarios.size()); //Muda o tamanho máximo da barra

            for (Arquivo a : arquivosPrioritarios) {
                //Toda vez que ele inicia um download de arquivo, aumenta o numero da barra
                progressDialog.setProgress(progressDialog.getProgress() + 1);
                boolean problemaDeRede = a.baixar(context); //Inicia o download
                if (!problemaDeRede) {
                    return false;
                }

            }

           /// for (Arquivo a : arquivos) { //Coloca os arquivos prioritários em um Array diferente
            //    if (a.getPrioridade() == 1) {
            //        arquivos.remove(a);
           ///     }
            //}

            BaixarArquivosEmBackground baixarArquivosEmBackground = new BaixarArquivosEmBackground(arquivosBackgroung, context);
            baixarArquivosEmBackground.execute();

            return true;

        } catch (JSONException je) {
            Log.e(Constantes.TAG, "Erro no Json \n" + je.getMessage());
            je.printStackTrace();
        }
        return false;
    }
}
