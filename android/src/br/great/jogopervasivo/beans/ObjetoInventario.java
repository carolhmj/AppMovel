package br.great.jogopervasivo.beans;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import br.great.jogopervasivo.util.Armazenamento;
import br.great.jogopervasivo.util.Constantes;
import br.great.jogopervasivo.util.InformacoesTemporarias;
import br.great.jogopervasivo.util.MetodosUteis;
import br.great.jogopervasivo.webServices.RecuperarObjetosInventario;
import br.great.jogopervasivo.webServices.Servidor;
import br.ufc.great.arviewer.android.R;

/**
 * Created by messiaslima on 08/05/2015.
 *
 * @author messiaslima
 * @version 1.0
 */
public class ObjetoInventario {

    private int mecsimples_id;
    private String nome;
    private String tipoObjeto;
    private String arquivo;

    public String getArquivo() {
        return arquivo;
    }

    public void setArquivo(String arquivo) {
        this.arquivo = arquivo;
    }

    public String getTipoObjeto() {
        return tipoObjeto;
    }

    public void setTipoObjeto(String tipoObjeto) {
        this.tipoObjeto = tipoObjeto;
    }

    public int getMecsimples_id() {
        return mecsimples_id;
    }

    public void setMecsimples_id(int mecsimples_id) {
        this.mecsimples_id = mecsimples_id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    @Override
    public String toString() {
        return nome;
    }

    /**
     * Verifica se um jogador está próximo a outro
     *
     * @param jogador Objeto que representa o jogador ao qual se deseja compartilhar um objeto
     * @return boolean se está ou não dentro do limiar de proximidade
     */
    private boolean isProximo(Jogador jogador) {
        Location locationJogador = MetodosUteis.latLngToLocation(LocationManager.GPS_PROVIDER, jogador.getPosicao());
        double distancia = locationJogador.distanceTo(Armazenamento.resgatarUltimaLocalizacao(InformacoesTemporarias.contextoTelaPrincipal));
        return distancia < Constantes.LIMIAR_DE_PROXIMIDADE;
    }

    /**
     * Metodo para compartilhar um objeto no inventário
     */
    public void compartilhar(final Context context) {
        List<Jogador> jogadoresProximos = new ArrayList<>();

        /*Recupera a lista de jogadores dos grupos, verifica quais estão dentro do limiar de proximidade
         * e os coloca em um Array diferente TODO: continuar a comentar */
        for (Grupo grupo : InformacoesTemporarias.grupos) {
            for (Jogador jogador : grupo.getJogadores()) {
                if (isProximo(jogador) && (jogador.getId() != InformacoesTemporarias.idJogador)) {
                    jogadoresProximos.add(jogador);
                }
            }
        }
        final ArrayAdapter<Jogador> adapter = new ArrayAdapter<Jogador>(context, android.R.layout.simple_list_item_1, jogadoresProximos);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.escolha_jogador_title);
        //builder.setMessage(R.string.escolha_jogador_message);
        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, final int i) {

                new AsyncTask<Void, Void, String>() {
                    @Override
                    protected String doInBackground(Void... voids) {
                        JSONObject acao = new JSONObject();
                        JSONObject mecanica = new JSONObject();
                        JSONArray requisiscao = new JSONArray();
                        try {
                            acao.put("acao", 113);
                            mecanica.put("jogo_id", InformacoesTemporarias.jogoAtual.getId());
                            mecanica.put("objeto_id", getMecsimples_id());
                            mecanica.put("jogador_id", InformacoesTemporarias.idJogador);
                            mecanica.put("jogador_id2", adapter.getItem(i).getId());
                            requisiscao.put(0, acao);
                            requisiscao.put(1, mecanica);
                        } catch (JSONException je) {
                            Log.e(Constantes.TAG, "erro no json " + je.getMessage());
                            je.printStackTrace();
                        }

                        return Servidor.fazerGet(requisiscao.toString());

                    }

                    @Override
                    protected void onPostExecute(String feedback) {
                        Log.i(Constantes.TAG, feedback);
                        if (feedback.contains("true")) {
                            Toast.makeText(context, context.getString(R.string.objeto_compaartilhado) + adapter.getItem(i).getNome(), Toast.LENGTH_SHORT).show();
                            RecuperarObjetosInventario.recuperar(context);
                        } else {
                            Toast.makeText(context, R.string.falha_de_conexao, Toast.LENGTH_LONG).show();
                        }
                    }
                }.execute();

            }
        });
        builder.create().show();
    }
}
