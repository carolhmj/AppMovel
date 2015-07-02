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

import java.util.ArrayList;
import java.util.List;


import br.great.jogopervasivo.beans.Mensagem;
import br.great.jogopervasivo.util.Constantes;
import br.great.jogopervasivo.util.InformacoesTemporarias;
import br.great.jogopervasivo.webServices.Servidor;
import br.ufc.great.arviewer.android.R;

public class ChatActivity extends Activity {

    public static List<Mensagem> todasAsMensagens= new ArrayList<>();
    private static TextView mensagensTextView;
    private static boolean telaAberta;

    public static void limparMensagens(){
        if(mensagensTextView != null){
            mensagensTextView.setText("");
        }
    }

    public static void receberMensagem(String autor, String mensagem){
        Log.i(Constantes.TAG, "GCM recebido!: Mensagem");
        Mensagem m = new Mensagem(autor,mensagem);
        todasAsMensagens.add(m);
        if(telaAberta){
            plotarMensagens();
        }
    }

    private static void plotarMensagens(){
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                limparMensagens();
                for(Mensagem m : todasAsMensagens){
                    mensagensTextView.append("\n "+m.getAuthor()+": "+m.getMessage());
                }
            }
        });
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat);

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

                        String feedback = Servidor.fazerGet("/jogo/setEnviarMensagem?jogo_id=" + InformacoesTemporarias.jogoAtual.getId() + "" +
                                "&mensagem=" + params[0].replace(" ", "%20") + "" +
                                "&jogador_id=" + InformacoesTemporarias.idJogador);
                        Log.i(Constantes.TAG, feedback);
                        if (feedback.equals("true")) {
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
                            receberMensagem(InformacoesTemporarias.nomeJogador, editTextMensagem.getEditableText().toString());
                            editTextMensagem.setText("");
                        } else {
                            Toast.makeText(ChatActivity.this, "Erro ao enviar a mensagem", Toast.LENGTH_SHORT).show();
                        }
                    }
                };

                sendMessage.execute(editTextMensagem.getEditableText().toString());
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


}
