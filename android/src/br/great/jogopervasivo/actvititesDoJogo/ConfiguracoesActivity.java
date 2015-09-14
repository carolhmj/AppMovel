package br.great.jogopervasivo.actvititesDoJogo;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import br.great.jogopervasivo.util.Armazenamento;
import br.ufc.great.arviewer.android.R;

public class ConfiguracoesActivity extends Activity {

    public static final String TAG_CONFIGURACAO_IP = "confIp";
    public static final String TAG_CONFIGURACAO_IP_ARQUIVOS = "confIpArquivos";
    public static final String TAG_CONFIGURACAO_PORTA = "confPorta";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.configuracoes);
        final EditText ipEditText = (EditText) findViewById(R.id.configuracoes_ip);
        final EditText portaEditText = (EditText) findViewById(R.id.configuracoes_porta);
        final EditText ipEditTextArquivos = (EditText) findViewById(R.id.configuracoes_ipArquivos);

        Button salvarButton = (Button) findViewById(R.id.configuracoes_salvar);

        ipEditText.setText(Armazenamento.resgatarIP(this));
        portaEditText.setText(Integer.toString(Armazenamento.resgatarPorta(this)));
        ipEditTextArquivos.setText(Armazenamento.resgatarIPArquivos(this).replace("http://","").replace("/pervasivedb/",""));

        salvarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Armazenamento.salvar(TAG_CONFIGURACAO_IP,ipEditText.getEditableText().toString(),ConfiguracoesActivity.this);
                Armazenamento.salvar(TAG_CONFIGURACAO_PORTA,Integer.parseInt(portaEditText.getEditableText().toString()),ConfiguracoesActivity.this);
                Armazenamento.salvar(TAG_CONFIGURACAO_IP_ARQUIVOS,ipEditTextArquivos.getEditableText().toString(),ConfiguracoesActivity.this);
                finish();
                Toast.makeText(getApplicationContext(),R.string.OK,Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_configuracoes, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
