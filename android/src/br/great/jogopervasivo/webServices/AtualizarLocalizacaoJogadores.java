package br.great.jogopervasivo.webServices;

import android.content.Context;
import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import br.great.jogopervasivo.actvititesDoJogo.TelaPrincipalActivity;
import br.great.jogopervasivo.beans.Grupo;
import br.great.jogopervasivo.beans.Jogador;
import br.great.jogopervasivo.util.Armazenamento;
import br.great.jogopervasivo.util.Constantes;
import br.great.jogopervasivo.util.InformacoesTemporarias;

/**
 * Created by messiaslima on 07/05/2015.
 *
 * @author messiaslima
 * @version 1.0
 */
public class AtualizarLocalizacaoJogadores implements Runnable {
    Context context;


    public AtualizarLocalizacaoJogadores(Context context) {
        this.context = context;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(1000 * 10);
                //Log.i("Debug", "executou a thread");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (Armazenamento.resgatarBoolean(Constantes.JOGO_EXECUTANDO, context)) {
                try {

                    JSONArray jsonArrayReq = new JSONArray();
                    JSONObject jsonObjectReq = new JSONObject();
                    JSONObject jsonObject1Req = new JSONObject();

                    Location location = Armazenamento.resgatarUltimaLocalizacao(context);

                    try {
                        jsonObjectReq.put("acao", 103);
                        jsonObject1Req.put("latitude", location.getLatitude());
                        jsonObject1Req.put("jogo_id", InformacoesTemporarias.jogoAtual.getId());
                        jsonObject1Req.put("longitude", location.getLongitude());
                        jsonObject1Req.put("jogador_id", InformacoesTemporarias.idJogador);
                        jsonArrayReq.put(0, jsonObjectReq);
                        jsonArrayReq.put(1, jsonObject1Req);
                    } catch (JSONException je) {
                        je.printStackTrace();
                    }

                    String repostaServidor = Servidor.fazerGet(jsonArrayReq.toString());

                    try {
                        JSONArray gruposJsonArray = new JSONArray(repostaServidor).getJSONArray(0);
                        JSONArray informacoesAdicionaisJsonArray = new JSONArray(repostaServidor).getJSONArray(1);

                        List<Grupo> grupos = new ArrayList<>();
                        for (int i = 0; i < gruposJsonArray.length(); i++) {
                            JSONObject grupoJson = gruposJsonArray.getJSONObject(i);

                            Grupo grupo = new Grupo();
                            grupo.setId(grupoJson.getInt("id"));
                            grupo.setNome(grupoJson.getString("nome"));
                            grupo.setTipoJogador(grupoJson.getInt("tipoJogador"));

                            JSONArray jogadoresJson = grupoJson.getJSONArray("jogadores");

                            List<Jogador> jogadores = new ArrayList<>();

                            for (int j = 0; j < jogadoresJson.length(); j++) {

                                Jogador jogador = new Jogador();
                                JSONObject jogadorJson = jogadoresJson.getJSONObject(j);

                                jogador.setNome(jogadorJson.getString("login"));
                                jogador.setId(jogadorJson.getInt("id"));

                                if (grupo.getId() == InformacoesTemporarias.grupoAtual.getId()) {
                                    jogador.setTipo(TelaPrincipalActivity.MARCADOR_ALIADO);
                                } else {
                                    jogador.setTipo(TelaPrincipalActivity.MARCADOR_ADVERSARIO);
                                }

                                jogador.setPosicao(new LatLng(jogadorJson.getJSONObject("posicao").getDouble("latitude"), jogadorJson.getJSONObject("posicao").getDouble("longitude")));

                                if (jogadorJson.getInt("id") != InformacoesTemporarias.idJogador) {
                                    InformacoesTemporarias.marcadoresDeJogadores.add(jogador);
                                    InformacoesTemporarias.grupoAtual.setTipoJogador(grupo.getTipoJogador());
                                }

                                InformacoesTemporarias.contextoTelaPrincipal.adicionarMarcadoresOutrosPlayers();


                                for (int index = 0; index < informacoesAdicionaisJsonArray.length(); index++) {
                                    if (informacoesAdicionaisJsonArray.getJSONObject(index).getInt("jogador_id") == jogador.getId()) {
                                        jogador.setVida(informacoesAdicionaisJsonArray.getJSONObject(index).getInt("vida"));
                                    }
                                    if (informacoesAdicionaisJsonArray.getJSONObject(index).getInt("jogador_id") == InformacoesTemporarias.idJogador){
                                        InformacoesTemporarias.life = Integer.toString(informacoesAdicionaisJsonArray.getJSONObject(index).getInt("vida"));
                                    }
                                }
                                TelaPrincipalActivity.atualizarVida();
                                jogador.setGrupo(grupo);
                                jogadores.add(jogador);
                            }

                            grupo.setJogadores(jogadores);
                            grupos.add(grupo);

                        }

                        InformacoesTemporarias.grupos = grupos;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                } catch (Exception e) {
                }
            }

            InformacoesTemporarias.verificarDistanciaJogadores();
            InformacoesTemporarias.contextoTelaPrincipal.adicionarMarcadoresOutrosPlayers();
        }

    }
}
