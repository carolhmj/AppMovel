package br.great.jogopervasivo.webServices;

import android.content.Context;
import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import br.great.jogopervasivo.actvititesDoJogo.InventarioActivity;
import br.great.jogopervasivo.beans.ObjetoInventario;
import br.great.jogopervasivo.util.Constantes;
import br.great.jogopervasivo.util.InformacoesTemporarias;

/**
 * Created by Messias Junior on 19/05/2015.
 *
 * @author Messias Junior
 * @version 2.0
 */
public class RecuperarObjetosInventario {


    public static void recuperar(Context context) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                JSONArray jsonArray = new JSONArray();
                JSONObject acao = new JSONObject();
                JSONObject parametros = new JSONObject();

                try {
                    acao.put("acao", 110);
                    parametros.put("jogo_id", InformacoesTemporarias.jogoAtual.getId());
                    parametros.put("jogador_id", InformacoesTemporarias.idJogador);
                    jsonArray.put(0, acao);
                    jsonArray.put(1, parametros);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                String resposta = Servidor.fazerGet(jsonArray.toString());
                // Se a resposta veio vazia. Erro de conexao com a internet

                if (!(resposta.trim().length() == 0)) {
                    try {
                        JSONArray objetosJsonArray = new JSONArray(resposta).getJSONObject(0).getJSONArray("objJogador");
                        InformacoesTemporarias.inventario.clear();
                        for (int i = 0; i < objetosJsonArray.length(); i++) {
                            JSONObject objetoJsonObject = objetosJsonArray.getJSONObject(i);
                            ObjetoInventario objetoInventario = new ObjetoInventario();
                            objetoInventario.setNome(objetoJsonObject.getString("nome"));
                            objetoInventario.setMecsimples_id(objetoJsonObject.getInt("mecsimples_id"));
                            objetoInventario.setTipoObjeto(objetoJsonObject.getString("tipoObjeto"));

                            switch (objetoJsonObject.getString("tipoObjeto")) {
                                case Constantes.TIPO_MECANICA_CSONS:
                                    objetoInventario.setArquivo(objetoJsonObject.getString("arqSom"));
                                    break;
                                case Constantes.TIPO_MECANICA_CFOTOS:
                                    objetoInventario.setArquivo(objetoJsonObject.getString("arqimage"));
                                    break;
                                case Constantes.TIPO_MECANICA_CTEXTOS:
                                    objetoInventario.setArquivo(objetoJsonObject.getString("texto"));
                                    break;
                                case Constantes.TIPO_MECANICA_CVIDEOS:
                                    objetoInventario.setArquivo(objetoJsonObject.getString("arqVideo"));
                                    break;
                            }
                            InformacoesTemporarias.inventario.add(objetoInventario);

                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
               InventarioActivity instance = InventarioActivity.getInstace();
                if (instance!=null){
                    instance.atualizarLista();
                }
            }
        }.execute();
    }
}
