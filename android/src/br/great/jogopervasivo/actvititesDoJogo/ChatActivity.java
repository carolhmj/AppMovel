package br.great.jogopervasivo.actvititesDoJogo;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import br.great.jogopervasivo.beans.Mensagem;
import br.great.jogopervasivo.util.Constantes;
import br.great.jogopervasivo.util.InformacoesTemporarias;
import br.great.jogopervasivo.webServices.Servidor;
import br.ufc.great.arviewer.android.R;

public class ChatActivity extends Activity {

    public static List<Mensagem> todasAsMensagens = new ArrayList<>();
    private static TextView mensagensTextView;
    public static boolean telaAberta = false;

    public static void limparMensagens() {
        if (mensagensTextView != null) {
            mensagensTextView.setText("");
        }
    }

    public static void receberMensagem(String autor, String mensagem) {
        Log.i(Constantes.TAG, "GCM recebido!: Mensagem");
        Mensagem m = new Mensagem(autor, mensagem);
        todasAsMensagens.add(m);
        if (telaAberta) {
            plotarMensagens();
        }
    }

    private static void plotarMensagens() {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                limparMensagens();
                for (Mensagem m : todasAsMensagens) {
                    mensagensTextView.append("\n " + m.getAuthor() + " : " + m.getMessage() + "\n -----");
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat);

        telaAberta = true;

        mensagensTextView = (TextView) findViewById(R.id.textViewMessages);
        final Button button = (Button) findViewById(R.id.buttonSendMessage);
        final EditText editTextMensagem = (EditText) findViewById(R.id.editTextMensagem);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AsyncTask<String, Void, Boolean> sendMessage = new AsyncTask<String, Void, Boolean>() {

                    @Override
                    protected void onPreExecute() {
                        button.setEnabled(false);
                        editTextMensagem.setEnabled(false);
                        super.onPreExecute();
                    }

                    @Override
                    protected Boolean doInBackground(String... params) {

                        JSONObject acao = new JSONObject();
                        JSONObject mecanica = new JSONObject();
                        JSONArray requisiscao = new JSONArray();
                        try {
                            acao.put("acao", 112);
                            mecanica.put("jogo_id", InformacoesTemporarias.jogoAtual.getId());
                            mecanica.put("grupo_id", InformacoesTemporarias.grupoAtual.getId());
                            mecanica.put("jogador_id", InformacoesTemporarias.idJogador);
                            mecanica.put("mensagem", params[0].replace(" ", "%20"));
                            requisiscao.put(0, acao);
                            requisiscao.put(1, mecanica);
                        } catch (JSONException je) {
                            Log.e(Constantes.TAG, "erro no json " + je.getMessage());
                            je.printStackTrace();
                        }

                        String feedback = Servidor.fazerGet(requisiscao.toString());

                        Log.i(Constantes.TAG, feedback);
                        if (feedback.contains("true")) {
                            return true;
                        } else {
                            return false;
                        }
                    }

                    @Override
                    protected void onPostExecute(Boolean aBoolean) {
                        button.setEnabled(true);
                        editTextMensagem.setEnabled(true);
                        if (aBoolean) {
                            receberMensagem(getString(R.string.eu), editTextMensagem.getEditableText().toString());
                            editTextMensagem.setText("");
                        } else {
                            Toast.makeText(ChatActivity.this, "Erro ao enviar a mensagem", Toast.LENGTH_SHORT).show();
                        }
                    }
                };

                if (!editTextMensagem.getEditableText().toString().isEmpty()) {
                    sendMessage.execute(editTextMensagem.getEditableText().toString());
                }
            }
        });
    }


    @Override
    protected void onResume() {
        telaAberta = true;
        plotarMensagens();
        super.onResume();
    }

    @Override
    protected void onPause() {
        telaAberta = false;
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        telaAberta = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        telaAberta = false;
    }
}
