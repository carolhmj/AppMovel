package br.great.jogopervasivo.beans.mecanicas;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import br.ufc.great.arviewer.android.R;
import br.great.jogopervasivo.actvititesDoJogo.TelaPrincipalActivity;
import br.great.jogopervasivo.beans.Mecanica;
import br.great.jogopervasivo.util.Constantes;
import br.great.jogopervasivo.util.InformacoesTemporarias;
import br.great.jogopervasivo.webServices.Servidor;
import br.great.jogopervasivo.webServices.UploadDeArquivo;

/**
 * Created by messiaslima on 11/03/2015.
 *
 * @author Messias Lima
 */
public class CSons extends Mecanica implements Imecanica {

    private int id;
    private boolean liberada;

    public boolean isLiberada() {
        return liberada;
    }

    public void setLiberada(boolean liberada) {
        this.liberada = liberada;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
           /*     JSONObject acao = new JSONObject();
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
                if (aBoolean) {

                    //Infla o layout do gravador
                    View layout = context.getLayoutInflater().inflate(R.layout.record_audio, null);

                    //Cria arquivo temporário
                    final File audioFile = InformacoesTemporarias.criarAudioTemporario();

                    //Configura o gravador de mídia
                    final MediaRecorder recorder = new MediaRecorder();
                    recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                    recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                    recorder.setOutputFile(audioFile.getAbsolutePath());
                    recorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
                    try {
                        recorder.prepare();
                    } catch (IOException ioe) {
                        Log.e(Constantes.TAG, "Erro ao preparar para gravar");
                        ioe.printStackTrace();
                    }

                    final Button botao = (Button) layout.findViewById(R.id.record_audio_botao);

                    //Adiciona a ação do botão de gravar
                    botao.setOnClickListener(new View.OnClickListener() {
                        boolean gravando = false;

                        @Override
                        public void onClick(View v) {
                            if (gravando) {
                                recorder.stop();
                                recorder.reset();
                                recorder.release();
                                gravando = false;
                                botao.setText(R.string.gravar_audio);
                            } else {
                                recorder.start();
                                botao.setText(R.string.gravando);
                                gravando = true;
                            }
                        }
                    });

                    //Mostra um alert dialog com o layout inflado
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle(getNome());
                    builder.setPositiveButton(R.string.enviar, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            UploadDeArquivo.enviarAudio(context, audioFile, CSons.this);
                        }
                    });
                    builder.setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            audioFile.delete();
                        }
                    });
                    builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            audioFile.delete();
                        }
                    });
                    builder.setView(layout);
                    builder.create().show();
                } else {
                    mostarToastFeedback(context);
                }
            }
        }.execute();
    }
}
