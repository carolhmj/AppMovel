package br.great.jogopervasivo.beans.mecanicas;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import br.ufc.great.arviewer.android.R;
import br.great.jogopervasivo.actvititesDoJogo.TelaPrincipalActivity;
import br.great.jogopervasivo.beans.Mecanica;
import br.great.jogopervasivo.util.Constantes;
import br.great.jogopervasivo.util.InformacoesTemporarias;
import br.great.jogopervasivo.webServices.Servidor;

/**
 * Created by messiaslima on 04/05/2015.
 *
 * @author messiaslima
 * @version 1.0
 */
public class VSons extends Mecanica implements Imecanica {
    private String arqSom;

    public String getArqSom() {
        return arqSom;
    }

    public void setArqSom(String arqSom) {
        this.arqSom = arqSom;
    }

    @Override
    public void realizarMecanica(final TelaPrincipalActivity context) {


        if (getEstado()==2){
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
          /*      JSONObject acao = new JSONObject();
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

                    try {

                        InformacoesTemporarias.jogoOcupado = true;
                        final File file = new File(Constantes.PASTA_DE_ARQUIVOS, arqSom);
                        final MediaPlayer musica = MediaPlayer.create(context, Uri.fromFile(file));
                        View view = context.getLayoutInflater().inflate(R.layout.player_audio, null);

                        final Button button = (Button) view.findViewById(R.id.reproduzir_button);
                        button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (musica.isPlaying()) {
                                    musica.stop();
                                    button.setText(R.string.reproduzir);
                                } else {
                                    musica.start();
                                    button.setText(R.string.reproduzindo);
                                }

                            }
                        });

                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setNegativeButton(R.string.OK, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                confirmarRealizacao(context, null, null, null);
                            }
                        });

                        builder.setTitle(getNome());
                        builder.setView(view);
                        builder.setTitle(R.string.app_name);
                        builder.create().show();
                    } catch (Exception e) {
                        Toast.makeText(context.getApplicationContext(),"Erro ao executar o arquivo",Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }

                } else {
                    mostarToastFeedback(context);
                }
            }
        }.execute();
    }
}
