package br.great.jogopervasivo.actvititesDoJogo;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;


import br.great.jogopervasivo.webServices.Servidor;
import br.ufc.great.arviewer.android.R;

public class NovoUsuarioActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.novo_usuario);

        final EditText login = (EditText) findViewById(R.id.novo_usuario_login);
        final EditText senha = (EditText) findViewById(R.id.novo_usuario_senha);
        final EditText senha2 = (EditText) findViewById(R.id.novo_usuario_senha2);
        Button botao = (Button) findViewById(R.id.novo_usuario_botao);


        botao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Verifica se os campos estão digitados corretamente e faz a requisição
                if ((login.getEditableText().toString().trim().length() > 0) && (senha.getEditableText().toString().trim().length() > 0) && (senha.getEditableText().toString().equals(senha2.getEditableText().toString()))) {
                    new AsyncTask<Void, Void, JSONObject>() {
                        ProgressDialog progressDialog = new ProgressDialog(NovoUsuarioActivity.this);

                        @Override
                        protected void onPreExecute() {
                            progressDialog.setCancelable(false);
                            progressDialog.setMessage(getString(R.string.enviando_informacoes));
                            progressDialog.show();
                        }

                        @Override
                        protected JSONObject doInBackground(Void... params) {
                            return Servidor.cadastrarNovoUsuário(NovoUsuarioActivity.this, login.getEditableText().toString().trim(), senha2.getEditableText().toString().trim());
                        }

                        @Override
                        protected void onPostExecute(JSONObject jsonObject) {
                            progressDialog.dismiss();
                            if (jsonObject.optBoolean("resultado")) {

                            }
                        }
                    }.execute();
                } else {
                    Toast.makeText(getApplicationContext(), R.string.verifique_os_campos, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}
