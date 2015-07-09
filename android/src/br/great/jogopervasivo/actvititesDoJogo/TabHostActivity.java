package br.great.jogopervasivo.actvititesDoJogo;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.TabActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TabHost;
import android.widget.Toast;

import br.great.jogopervasivo.arrayAdapters.ListarMissoesAdapter;
import br.great.jogopervasivo.beans.Mecanica;
import br.great.jogopervasivo.beans.Missao;
import br.great.jogopervasivo.util.Armazenamento;
import br.great.jogopervasivo.util.Constantes;
import br.great.jogopervasivo.util.InformacoesTemporarias;
import br.ufc.great.arviewer.android.R;

public class TabHostActivity extends TabActivity {
    TabHost tabHost;
    TabHost.TabSpec descritor;
    static ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab_host);

        tabHost = getTabHost();

        descritor = tabHost.newTabSpec(getString(R.string.mapa));
        descritor.setContent(new Intent().setClass(this, TelaPrincipalActivity.class));
        descritor.setIndicator(getString(R.string.mapa));
        tabHost.addTab(descritor);
        actionBar = getActionBar();
    }

    @Override
    protected void onResume() {
        invalidateOptionsMenu();
        boolean emJogo = Armazenamento.resgatarBoolean(Constantes.JOGO_EXECUTANDO, this);
        if (emJogo) {

            tabHost.clearAllTabs();
            descritor = tabHost.newTabSpec(getString(R.string.mapa));
            descritor.setContent(new Intent().setClass(this, TelaPrincipalActivity.class));
            descritor.setIndicator(getString(R.string.mapa));
            tabHost.addTab(descritor);
            descritor = tabHost.newTabSpec(getString(R.string.inventario));
            descritor.setContent(new Intent().setClass(this, InventarioActivity.class));
            descritor.setIndicator(getString(R.string.inventario));
            tabHost.addTab(descritor);
        }


        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        boolean emJogo = Armazenamento.resgatarBoolean(Constantes.JOGO_EXECUTANDO, this);
        if (emJogo) {
            getMenuInflater().inflate(R.menu.menu_tela_principal_durante_jogo, menu);
        } else {
            getMenuInflater().inflate(R.menu.menu_tela_principal, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.menuNovoJogo) {
            Intent intent = new Intent(TabHostActivity.this, TiposDeJogosDisponiveisActivity.class);
            startActivity(intent);
            return true;
        }

        if (id == R.id.menuChat) {
            Intent intent = new Intent(TabHostActivity.this, ChatActivity.class);
            startActivity(intent);
            return true;
        }

        if (id == R.id.sairDoJogo) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.deseja_sair);
            builder.setNegativeButton(R.string.cancelar, null);
            builder.setPositiveButton(R.string.sair_do_jogo, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    InformacoesTemporarias.contextoTelaPrincipal.terminarJogo();
                    invalidateOptionsMenu();
                }
            });
            builder.create().show();
        }

        if (id == R.id.menuMissoes) {
            try {
                if (InformacoesTemporarias.missoesAtuais != null || InformacoesTemporarias.missoesAtuais.size() <= 0) {
                    final ListarMissoesAdapter adapter = new ListarMissoesAdapter(this, R.layout.mecanicas_item_lista, InformacoesTemporarias.missoesAtuais);
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle(R.string.missoes)
                            .setNegativeButton(R.string.OK, null)
                            .setPositiveButton(R.string.mostrar_tudo, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    for (Mecanica mecanica : InformacoesTemporarias.mecanicasAtuais) {
                                        mecanica.setMostrar(true);
                                    }
                                    InformacoesTemporarias.contextoTelaPrincipal.mostrarMecanicas();
                                }
                            })
                            .setAdapter(adapter, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Missao missao = adapter.getItem(which);
                                    for (Mecanica mecanica : InformacoesTemporarias.mecanicasAtuais) {
                                        mecanica.setMostrar(true);
                                        if (mecanica.getMissao_id() != missao.getId()) {
                                            mecanica.setMostrar(false);
                                        }
                                    }
                                    InformacoesTemporarias.contextoTelaPrincipal.mostrarMecanicas();
                                }
                            })
                            .create().show();
                } else {
                    Toast.makeText(this, R.string.sem_missoes, Toast.LENGTH_SHORT).show();
                }
            } catch (Exception ex) {
                Toast.makeText(this, R.string.sem_missoes, Toast.LENGTH_SHORT).show();
            }
        }


        return super.onOptionsItemSelected(item);
    }


}
