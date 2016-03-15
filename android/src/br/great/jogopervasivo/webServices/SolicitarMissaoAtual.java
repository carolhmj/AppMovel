package br.great.jogopervasivo.webServices;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import br.great.jogopervasivo.beans.mecanicas.VObj3d;
import br.ufc.great.arviewer.android.R;
import br.great.jogopervasivo.beans.Grupo;
import br.great.jogopervasivo.beans.InstanciaDeJogo;
import br.great.jogopervasivo.beans.Mecanica;
import br.great.jogopervasivo.beans.Missao;
import br.great.jogopervasivo.beans.mecanicas.CFotos;
import br.great.jogopervasivo.beans.mecanicas.CSons;
import br.great.jogopervasivo.beans.mecanicas.CTextos;
import br.great.jogopervasivo.beans.mecanicas.CVideos;
import br.great.jogopervasivo.beans.mecanicas.Deixar;
import br.great.jogopervasivo.beans.mecanicas.IrLocais;
import br.great.jogopervasivo.beans.mecanicas.VSons;
import br.great.jogopervasivo.beans.mecanicas.VVideos;
import br.great.jogopervasivo.beans.mecanicas.Vfotos;
import br.great.jogopervasivo.beans.mecanicas.Vtextos;
import br.great.jogopervasivo.util.Constantes;
import br.great.jogopervasivo.util.InformacoesTemporarias;

/**
 * Created by messiaslima on 27/02/2015.
 *
 * @author Messias Lima
 * @version 1.0
 * @since 1.0
 */
public class SolicitarMissaoAtual extends AsyncTask<Void, Void, Void> {

    private Context context;
    private int tentativas = 0;

    public SolicitarMissaoAtual(Context context) {
        this.context = context;
    }

    @Override
    protected Void doInBackground(Void... params) {
        solicitarSincrono();
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        InformacoesTemporarias.contextoTelaPrincipal.mostrarMecanicas();
    }

    public void solicitarSincrono() {
        tentativas++;
        if (tentativas > 10) {
            Toast.makeText(context.getApplicationContext(), R.string.falha_de_conexao, Toast.LENGTH_LONG).show();
            return;
        }

        if (InformacoesTemporarias.conexaoAtiva(context)) {

            Grupo grupo = InformacoesTemporarias.grupoAtual;
            InstanciaDeJogo jogo = InformacoesTemporarias.jogoAtual;

            JSONArray jsonArrayReq = new JSONArray();
            JSONObject jsonObjectReq = new JSONObject();
            JSONObject jsonObject1Req = new JSONObject();

            try {
                jsonObjectReq.put("acao", 102);
                jsonObject1Req.put("jogo_id", jogo.getId());
                jsonObject1Req.put("grupo_id", grupo.getId());
                jsonArrayReq.put(0, jsonObjectReq);
                jsonArrayReq.put(1, jsonObject1Req);
            } catch (JSONException je) {
                je.printStackTrace();
            }

            String resposta = Servidor.fazerGet(jsonArrayReq.toString());
            if (resposta.equals("")) {
                new SolicitarMissaoAtual(context).execute();
            } else {
                try {
                    JSONArray arrayMecanicas = new JSONArray(resposta).getJSONArray(0);
                    JSONArray arrayMissoes = new JSONArray(resposta).getJSONArray(1).getJSONArray(0);
                    List<Mecanica> mecanicasAtuais = new ArrayList<>();
                    for (int i = 0; i < arrayMecanicas.length(); i++) {

                        Mecanica mecanica;
                        JSONObject objetoMissao = arrayMecanicas.getJSONObject(i);
                        JSONObject objetoMecanica = objetoMissao.optJSONObject("objeto");

                        switch (objetoMissao.getString("tiposimples")) {
                            case Constantes.TIPO_MECANICA_CTEXTOS:
                                mecanica = new CTextos();
                                ((CTextos) mecanica).setTexto(objetoMecanica.optString("texto","<sem texto>"));
                                ((CTextos) mecanica).setIdTextos(objetoMecanica.getInt("texto_id"));
                                break;
                            case Constantes.TIPO_MECANICA_CFOTOS:
                                mecanica = new CFotos();
                                //((CFotos) mecanica).setIdFotos(objetoMecanica.getInt("foto_id"));
                                break;
                            case Constantes.TIPO_MECANICA_VFOTOS:
                                mecanica = new Vfotos();
                                ((Vfotos) mecanica).setArqImage(objetoMecanica.getString("arqimage"));
                                ((Vfotos) mecanica).setIdFotos(objetoMecanica.getInt("foto_id"));
                                break;
                            case Constantes.TIPO_MECANICA_VTEXTOS:
                                mecanica = new Vtextos();
                                ((Vtextos) mecanica).setTexto(objetoMecanica.getString("texto"));
                                break;
                            case Constantes.TIPO_MECANICA_IRLOCAIS:
                                mecanica = new IrLocais();
                                break;
                            case Constantes.TIPO_MECANICA_CSONS:
                                mecanica = new CSons();
                                break;
                            case Constantes.TIPO_MECANICA_CVIDEOS:
                                mecanica = new CVideos();
                                break;
                            case Constantes.TIPO_MECANICA_VSONS:
                                mecanica = new VSons();
                                ((VSons) mecanica).setArqSom(objetoMecanica.getString("arqSom"));
                                break;
                            case Constantes.TIPO_MECANICA_VVIDEOS:
                                mecanica = new VVideos();
                                ((VVideos) mecanica).setArqVideo(objetoMecanica.getString("arqVideo"));
                                break;
                            case Constantes.TIPO_MECANICA_DFOTOS:
                                mecanica = new Deixar();
                                ((Deixar) mecanica).setTipoObjeto(objetoMecanica.getString("tipoObjeto"));
                                break;
                            case Constantes.TIPO_MECANICA_DOBJETOS3D:
                                mecanica = new Deixar();
                                ((Deixar) mecanica).setTipoObjeto(objetoMecanica.getString("tipoObjeto"));
                                break;
                            case Constantes.TIPO_MECANICA_DSONS:
                                mecanica = new Deixar();
                                ((Deixar) mecanica).setTipoObjeto(objetoMecanica.getString("tipoObjeto"));

                                break;
                            case Constantes.TIPO_MECANICA_DTEXTOS:
                                mecanica = new Deixar();
                                ((Deixar) mecanica).setTipoObjeto(objetoMecanica.getString("tipoObjeto"));

                                break;
                            case Constantes.TIPO_MECANICA_DVIDEOS:
                                mecanica = new Deixar();
                                ((Deixar) mecanica).setTipoObjeto(objetoMecanica.getString("tipoObjeto"));
                                break;
                            case Constantes.TIPO_MECANICA_V_OBJ_3D:
                                mecanica = new VObj3d();
                                ((VObj3d)mecanica).setArqObj3d(objetoMecanica.getString("arqObjeto3d"));
                                ((VObj3d)mecanica).setArqTextura(objetoMecanica.getString("arqTextura"));
                                break;
                            default:
                                throw new UnsupportedOperationException();
                                //mecanica = new Mecanica();
                                //break;
                        }


                        mecanica.setId(objetoMissao.getInt("id"));
                        mecanica.setEstado(objetoMissao.getJSONObject("estado").getInt("estado"));
                        if (mecanica.getEstado() == 4) {
                            mecanica.setRealizada(true);
                        }
                        mecanica.setVisivel(objetoMissao.getInt("visivel"));
                        mecanica.setEscondido(objetoMissao.getInt("visivel") == 2);
                        mecanica.setMecanicaSimples_id(objetoMissao.getInt("mecsimples_id"));
                        mecanica.setTipo(objetoMissao.getInt("tipo"));
                        mecanica.setVibrar(getBoolean(objetoMissao.getInt("vibrar")));
                        mecanica.setTempo(objetoMissao.optString("tempo"));
                        mecanica.setTipoSimples(objetoMissao.getString("tiposimples"));
                        mecanica.setNome(objetoMissao.getString("nome"));
                        mecanica.setMsgbloqueio(objetoMissao.getString("msgbloqueio"));
                        mecanica.setMissao_id(objetoMissao.getInt("missao_id"));


                        if (objetoMecanica != null) {
                            mecanica.setLocalizacao(new LatLng(objetoMecanica.getJSONObject("posicao").getDouble("latitude"), objetoMecanica.getJSONObject("posicao").getDouble("longitude")));
                        } else {
                            mecanica.setLocalizacao(new LatLng(objetoMissao.getJSONObject("posicao").getDouble("latitude"), objetoMissao.getJSONObject("posicao").getDouble("longitude")));
                        }
                        mecanicasAtuais.add(mecanica);


                    }
                    InformacoesTemporarias.mecanicasAtuais = mecanicasAtuais;
                    List<Missao> missoes = new ArrayList<>();
                    for (int i = 0; i < arrayMissoes.length(); i++) {
                        JSONObject missaoJson = arrayMissoes.getJSONObject(i);
                        Missao missao = new Missao();
                        missao.setId(missaoJson.getInt("id"));
                        missao.setReqterminar(missaoJson.getBoolean("reqterminar"));
                        missao.setFinMecanicas(missaoJson.getInt("finMecanicas"));
                        missao.setNome(missaoJson.getString("nome"));
                        missao.setPosicaoInicial(new LatLng(missaoJson.getJSONObject("posicaoInicial").getDouble("latitude"), missaoJson.getJSONObject("posicaoInicial").getDouble("longitude")));
                        missoes.add(missao);
                    }
                    InformacoesTemporarias.missoesAtuais = missoes;
                } catch (JSONException je) {
                    Log.e(Constantes.TAG, "Erro no Json \n" + je.getMessage());
                    je.printStackTrace();
                }
            }
        }
    }

    public boolean getBoolean(int binario) {
        return binario == 1;
    }
}
