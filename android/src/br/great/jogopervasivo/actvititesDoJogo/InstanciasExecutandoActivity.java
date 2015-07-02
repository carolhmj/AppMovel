package br.great.jogopervasivo.actvititesDoJogo;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;


import br.great.jogopervasivo.beans.Grupo;
import br.great.jogopervasivo.beans.InstanciaDeJogo;
import br.great.jogopervasivo.util.Armazenamento;
import br.great.jogopervasivo.util.Constantes;
import br.great.jogopervasivo.util.InformacoesTemporarias;
import br.great.jogopervasivo.webServices.CriarNovaInstanciaDeJogo;
import br.great.jogopervasivo.webServices.EscolherEquipe;
import br.great.jogopervasivo.webServices.RecuperarInstanciasDeJogos;
import br.ufc.great.arviewer.android.R;

public class InstanciasExecutandoActivity extends Activity {

    private Bundle extras;
    private ListView lista;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.instancias_executando);

        lista = (ListView) findViewById(R.id.instancias_executando_lista);
        extras = getIntent().getExtras();

        ActionBar actionBar = getActionBar();
        //Adiciona o nome do Jogo na action bar
        actionBar.setSubtitle(extras.getString(Constantes.JOGO_PAI_NOME));

        //Preenche a ListView assin que a activity inicia
        atuaizarLista();

        //Quanto clica na Instancia de jogo abre os grupos disponíveis
        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                InstanciaDeJogo instanciaDeJogo = (InstanciaDeJogo) parent.getItemAtPosition(position);
                InformacoesTemporarias.instanciaDeJogo = instanciaDeJogo;
                if (!instanciaDeJogo.isJogadorParticipando()) {
                    EscolherEquipe escolherEquipe = new EscolherEquipe(InstanciasExecutandoActivity.this);
                    escolherEquipe.execute(instanciaDeJogo.getId());
                } else {
                    InformacoesTemporarias.jogoAtual = instanciaDeJogo;
                    Grupo g = new Grupo();
                    g.setId(instanciaDeJogo.getGrupoId());
                    g.setNome(instanciaDeJogo.getGrupoNome());
                    InformacoesTemporarias.grupoAtual = g;
                    Armazenamento.salvar(Constantes.JOGO_EXECUTANDO,true,InstanciasExecutandoActivity.this);
                    finish();
                }
            }
        });
    }

    public void atuaizarLista() {
        RecuperarInstanciasDeJogos recuperarInstanciasDeJogos = new RecuperarInstanciasDeJogos(this, lista);
        recuperarInstanciasDeJogos.execute(extras.getInt(Constantes.JOGO_PAI_ID), InformacoesTemporarias.idJogador);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_instancias_executando, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //Criação de nova instancia
        if (id == R.id.instancias_executando_novo_jogo) {
            View layout = getLayoutInflater().inflate(R.layout.novo_jogo_layout, null);
            final EditText nomeFicticio = (EditText) layout.findViewById(R.id.novo_jogo_nome);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getString(R.string.novo_jogo))
                    .setView(layout)
                    .setNegativeButton(getString(R.string.cancelar), null)
                    .setPositiveButton(getString(R.string.OK), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            CriarNovaInstanciaDeJogo criarNovaInstanciaDeJogo = new CriarNovaInstanciaDeJogo(InstanciasExecutandoActivity.this);
                            criarNovaInstanciaDeJogo.execute(Integer.toString(extras.getInt(Constantes.JOGO_PAI_ID)), nomeFicticio.getEditableText().toString());
                        }
                    }).create().show();
        }

        return super.onOptionsItemSelected(item);
    }
}
