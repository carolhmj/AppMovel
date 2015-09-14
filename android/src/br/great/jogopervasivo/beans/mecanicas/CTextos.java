package br.great.jogopervasivo.beans.mecanicas;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import br.great.jogopervasivo.actvititesDoJogo.TelaPrincipalActivity;
import br.great.jogopervasivo.beans.Mecanica;
import br.ufc.great.arviewer.android.R;

/**
 * Created by messiaslima on 18/03/2015.
 *
 * @author messiaslima
 */
public class CTextos extends Mecanica implements Imecanica {
    private int idTextos;
    private String texto;

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public int getIdTextos() {
        return idTextos;
    }

    public void setIdTextos(int id) {
        this.idTextos = id;
    }

    @Override
    public void realizarMecanica(final TelaPrincipalActivity context) {

        if (getEstado() == 2) {
            return;
        }


        new AsyncTask<Void, Void, Boolean>() {
            ProgressDialog progressDialog;

            @Override
            protected void onPreExecute() {
                progressDialog = new ProgressDialog(context);
                progressDialog.setCancelable(false);
                progressDialog.setMessage(context.getString(R.string.obtendo_informacoes));

                super.onPreExecute();
            }

            @Override
            protected Boolean doInBackground(Void... params) {
               /* JSONObject acao = new JSONObject();
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
                    return resposta.getInt("result") != 0;
                } catch (JSONException je) {
                    Log.e(Constantes.TAG, "erro no json " + je.getMessage());
                    je.printStackTrace();
                    return false;
                }*/

                return verificarAutorizacaoDaMecanica();
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                progressDialog.dismiss();
                if (aBoolean) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    View view = context.getLayoutInflater().inflate(R.layout.novo_jogo_layout, null);
                    final EditText campoDeTexto = (EditText) view.findViewById(R.id.novo_jogo_nome);
                    campoDeTexto.setHint(R.string.digite_texto);
                    builder.setView(view);
                    builder.setTitle(getNome())
                            .setNegativeButton(R.string.cancelar, null)
                            .setPositiveButton(R.string.enviar, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    new AsyncTask<Void, Void, Boolean>() {
                                        ProgressDialog progressDialog = new ProgressDialog(context);

                                        @Override
                                        protected void onPreExecute() {
                                            progressDialog.setCancelable(false);
                                            progressDialog.setMessage(context.getString(R.string.obtendo_informacoes));
                                            progressDialog.show();
                                        }


                                        @Override
                                        protected Boolean doInBackground(Void... params) {

                                            /*Esse método era utilizado na primeira versão do aplicativo
                                             * Em decorrencia da mudança de logica, O conteudo foi movido para o método
                                             * Mecanica.confirmarRealização()
                                             * */
                                            return true;
                                        }

                                        @Override
                                        protected void onPostExecute(Boolean aBoolean) {
                                            progressDialog.dismiss();
                                            int mensagem;
                                            if (aBoolean) {
                                                mensagem = R.string.arquivo_enviado;
                                                confirmarRealizacao(context, campoDeTexto.getEditableText().toString(), null, null);
                                            } else {
                                                mensagem = R.string.falha_de_conexao;
                                            }
                                            Toast.makeText(context.getApplicationContext(), mensagem, Toast.LENGTH_SHORT).show();
                                        }
                                    }.execute();
                                }
                            })
                            .create().show();

                } else {
                    mostarToastFeedback(context);
                }
            }
        }.execute();


    }
}
