package br.great.jogopervasivo.beans;

import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import br.great.jogopervasivo.actvititesDoJogo.TelaPrincipalActivity;
import br.great.jogopervasivo.beans.mecanicas.Imecanica;
import br.great.jogopervasivo.util.Armazenamento;
import br.great.jogopervasivo.util.Constantes;
import br.great.jogopervasivo.util.InformacoesTemporarias;
import br.great.jogopervasivo.webServices.RecuperarObjetosInventario;
import br.great.jogopervasivo.webServices.Servidor;
import br.ufc.great.arviewer.android.R;

/**
 * Created by messiaslima on 03/03/2015.
 *
 * @author messiaslima
 * @version 1.0
 * @since 1.0
 */
public class Mecanica {

    private static int tentativas = 0;
    private int ordem;
    private int missao_id;
    private int mecanicaSimples_id;
    private String tipoSimples;
    private String tempo;
    private String nome;
    private boolean realizada;
    private LatLng localizacao;
    private boolean liberada;
    private int estado;
    private int visivel;
    private boolean mostrar = true;
    private int id;
    private int tipo;
    private Imecanica objeto;
    private boolean escondido;
    private String msgbloqueio;

    public static final int VISIVEL_NAO = 0;
    public static final int VISIVEL_SIM = 1;
    public static final int VISIVEL_NUNCA = 2;
    public static final int VISIVEL_NUNCA2 = 2;

    public boolean isMostrar() {
        return mostrar;
    }

    public void setMostrar(boolean mostrar) {
        this.mostrar = mostrar;
    }

    public String getMsgbloqueio() {
        return msgbloqueio;
    }

    public void setMsgbloqueio(String msgbloqueio) {
        this.msgbloqueio = msgbloqueio;
    }

    public boolean isEscondido() {
        return escondido;
    }

    public void setEscondido(boolean escondido) {
        this.escondido = escondido;
    }

    public Imecanica getObjeto() {
        return objeto;
    }

    public void setObjeto(Imecanica objeto) {
        this.objeto = objeto;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    private boolean vibrar;

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }

    public boolean isVisivel() {
        return visivel == VISIVEL_SIM;
    }

    public void setVisivel(int visivel) {
        this.visivel = visivel;
    }

    public int getVisivel() {
        return this.visivel;
    }

    public int getTipo() {
        return tipo;
    }

    public void setTipo(int tipo) {
        this.tipo = tipo;
    }

    public boolean isVibrar() {
        return vibrar;
    }

    public void setVibrar(boolean vibrar) {
        this.vibrar = vibrar;
    }

    public boolean isLiberada() {
        return liberada;
    }

    public void setLiberada(boolean liberada) {
        this.liberada = liberada;
    }

    public LatLng getLocalizacao() {
        return localizacao;
    }

    public void setLocalizacao(LatLng localizacao) {
        this.localizacao = localizacao;
    }

    public boolean isRealizada() {
        return realizada;
    }

    public void setRealizada(boolean realizada) {
        this.realizada = realizada;
    }

    public int getOrdem() {
        return ordem;
    }

    public void setOrdem(int ordem) {
        this.ordem = ordem;
    }

    public int getMissao_id() {
        return missao_id;
    }

    public void setMissao_id(int missao_id) {
        this.missao_id = missao_id;
    }

    public int getMecanicaSimples_id() {
        return mecanicaSimples_id;
    }

    public void setMecanicaSimples_id(int mecanicaSimples_id) {
        this.mecanicaSimples_id = mecanicaSimples_id;
    }

    public String getTipoSimples() {
        return tipoSimples;
    }

    public void setTipoSimples(String tipoSimples) {
        this.tipoSimples = tipoSimples;
    }

    public String getTempo() {
        return tempo;
    }

    public void setTempo(String tempo) {
        this.tempo = tempo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    @Override
    public String toString() {
        return getNome();
    }

    /**
     * manda a confirmação para o servidor que a mecânica foi realizada
     */
    public void confirmarRealizacao(final Context context, final String nomeDoArquivo, final String tipo, final Integer idMecanicaSimples) {
        tentativas++;
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {

                JSONObject acao = new JSONObject();
                JSONObject jogo = new JSONObject();
                JSONObject jogador = new JSONObject();
                JSONArray array = new JSONArray();
                try {
                    acao.put("acao", 108);
                    jogo.put("jogo_id", InformacoesTemporarias.jogoAtual.getId());
                    jogo.put("grupo_id", InformacoesTemporarias.grupoAtual.getId());
                    jogo.put("mecanica_id", getId());
                    jogador.put("jogador_id", InformacoesTemporarias.idJogador);
                    Location localizacaoJogador = Armazenamento.resgatarUltimaLocalizacao(context);
                    jogador.put("latitude", localizacaoJogador.getLatitude());
                    jogador.put("longitude", localizacaoJogador.getLongitude());
                    if (nomeDoArquivo != null) {
                        jogador.put("arquivo", nomeDoArquivo.replace(" ", "%20"));
                    }

                    if (tipo != null) {
                        jogador.put("tipo", tipo.replace(" ", "%20"));
                    }

                    if (idMecanicaSimples != null) {
                        jogador.put("mecsimples_id", idMecanicaSimples);
                    }

                    array.put(0, acao);
                    array.put(1, jogo);
                    array.put(2, jogador);
                } catch (JSONException je) {
                }

                String resposta = Servidor.fazerGet(array.toString());

                /*na resposta vem:
                * O life do jogador  e lista de objetos no inventario
                * O codigo abaixo o recupera, e define o tipo de acordo com o tipo da mecanica
                * Por fim, manda executar o método que faz um efeito de "fade out" no marcador
                */
                try {
                    JSONObject jsonObject;
                    try {
                        jsonObject = new JSONArray(resposta).getJSONArray(2).getJSONObject(0);
                    } catch (Exception e) {
                        jsonObject = new JSONArray(resposta).getJSONArray(0).getJSONObject(0);
                    }
                    InformacoesTemporarias.jogoOcupado = false;
                    boolean resultado = jsonObject.getBoolean("result");
                    if (resultado) {
                        InformacoesTemporarias.life = new JSONArray(resposta).getJSONArray(1).getJSONObject(0).getString("vida");
                        JSONArray objetosJsonArray = new JSONArray(resposta).getJSONArray(1).getJSONObject(0).getJSONArray("objJogador");
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

                        setRealizada(true);
                        RecuperarObjetosInventario.recuperar(context);
                        TelaPrincipalActivity.atualizarVida();
                        InformacoesTemporarias.contextoTelaPrincipal.transicaoMarcador(getNome());
                    }
                } catch (JSONException e) {
                    //Limita o número de tentativas de conexão, para não entrar em "looping"
                    if (InformacoesTemporarias.conexaoAtiva(context)) {
                        if (tentativas < 10) {
                            confirmarRealizacao(context, nomeDoArquivo, tipo, idMecanicaSimples);
                        } else {
                            tentativas = 0;
                        }
                        e.printStackTrace();
                    }
                }

                return null;
            }
        }.execute();
    }

    /**
     * Recupera mecanica pelo nome
     *
     * @param nome nome da mecanica a ser recuperada
     * @return referencia ao pobjeto de mecanica
     */
    public static Mecanica getMecanica(String nome) {
        try {
            for (Mecanica m : InformacoesTemporarias.mecanicasAtuais) {
                if (m.getNome().equals(nome)) {
                    return m;
                }
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    /**
     * Verifica no servidor, se a mecânica poderá ser realizada pelo jogador nesse momento
     */
    public boolean verificarAutorizacaoDaMecanica() {
        JSONObject acao = new JSONObject();
        JSONObject mecanica = new JSONObject();
        JSONArray requisiscao = new JSONArray();
        try {
            acao.put("acao", 107);
            mecanica.put("jogo_id", InformacoesTemporarias.jogoAtual.getId());
            mecanica.put("grupo_id", InformacoesTemporarias.grupoAtual.getId());
            mecanica.put("mecanica_id", getId());
            mecanica.put("jogador_id", InformacoesTemporarias.idJogador);
            requisiscao.put(0, acao);
            requisiscao.put(1, mecanica);
            JSONObject resposta = new JSONArray(Servidor.fazerGet(requisiscao.toString())).getJSONObject(0);
            nivelDeAutorizacaoDeRealizacao = resposta.getInt("result");
            if (resposta.getInt("result") == 0 || resposta.getInt("result") == 3) {
                return false;
            } else {
                return true;
            }
        } catch (JSONException je) {
            Log.e(Constantes.TAG, "erro no json " + je.getMessage());
            je.printStackTrace();
            return false;
        }
    }

    private int nivelDeAutorizacaoDeRealizacao;

    public void mostarToastFeedback(Context context) {
        if (nivelDeAutorizacaoDeRealizacao == 0) {
            Toast.makeText(context.getApplicationContext(), R.string.nao_pode_realizar_mec, Toast.LENGTH_LONG).show();
        }
        if (nivelDeAutorizacaoDeRealizacao == 3) {
            Toast.makeText(context.getApplicationContext(), R.string.sem_permissao_para_realizar_mecanica, Toast.LENGTH_LONG).show();
        }
    }
}
