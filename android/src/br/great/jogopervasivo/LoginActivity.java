package br.great.jogopervasivo;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONObject;

import br.great.jogopervasivo.actvititesDoJogo.ConfiguracoesActivity;
import br.great.jogopervasivo.actvititesDoJogo.NovoUsuarioActivity;
import br.great.jogopervasivo.actvititesDoJogo.TabHostActivity;
import br.great.jogopervasivo.actvititesDoJogo.TelaPrincipalActivity;
import br.great.jogopervasivo.actvititesDoJogo.TiposDeJogosDisponiveisActivity;
import br.great.jogopervasivo.util.Armazenamento;
import br.great.jogopervasivo.util.Constantes;
import br.great.jogopervasivo.util.InformacoesTemporarias;
import br.great.jogopervasivo.webServices.Servidor;
import br.ufc.great.arviewer.android.R;

public class LoginActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        Armazenamento.salvar(Constantes.JOGO_EXECUTANDO, false, this);
        final EditText login = (EditText) findViewById(R.id.login_login);
        final EditText senha = (EditText) findViewById(R.id.login_senha);
        Button botao = (Button) findViewById(R.id.login_botao);
        TextView novoUsuario = (TextView) findViewById(R.id.login_nova_conta);
        TextView configuracoes = (TextView) findViewById(R.id.login_configuracoes);
        ActionBar actionBar = getActionBar();
        actionBar.hide();

        novoUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, NovoUsuarioActivity.class);
                startActivity(intent);
            }
        });

        botao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AsyncTask<Void, Void, JSONObject>() {
                    ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);

                    @Override
                    protected void onPreExecute() {
                        progressDialog.setCancelable(false);
                        progressDialog.setMessage(getString(R.string.autenticando));
                        progressDialog.show();
                    }

                    @Override
                    protected JSONObject doInBackground(Void... params) {
                        JSONObject resposta = Servidor.fazerLogin(LoginActivity.this, login.getEditableText().toString(), senha.getEditableText().toString());
                        return resposta;
                    }

                    @Override
                    protected void onPostExecute(JSONObject jsonObject) {
                        progressDialog.dismiss();
                        if (jsonObject.optBoolean("resultado")) {
                            Intent intent = new Intent(LoginActivity.this, TabHostActivity.class);
                            startActivity(intent);
                        } else {
                            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                            builder.setMessage(jsonObject.optString("mensagem"));
                            builder.setNegativeButton(R.string.OK,null);
                            builder.create().show();
                        }
                    }
                }.execute();
            }
        });

        configuracoes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, ConfiguracoesActivity.class));
            }
        });
        //InformacoesTemporarias.contexto=this;
    }
}
