package br.great.jogopervasivo.actvititesDoJogo;

import android.app.Activity;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import br.great.jogopervasivo.arrayAdapters.InventarioArrayAdapter;
import br.great.jogopervasivo.beans.ObjetoInventario;
import br.great.jogopervasivo.util.Constantes;
import br.great.jogopervasivo.util.InformacoesTemporarias;
import br.great.jogopervasivo.webServices.RecuperarObjetosInventario;
import br.ufc.great.arviewer.android.R;

public class InventarioActivity extends Activity {
    ListView listView;
    public static final String ITEM_ID = "item_id";
    public static final String ITEM_TIPO = "item_tipo";
    public static final String ITEM_ARQUIVO = "item_arquivo";
    private static InventarioActivity instance = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inventario);


        listView = (ListView) findViewById(R.id.objetoInventarioListView);


        final Intent intent = getIntent();
        final Bundle bundle = intent.getExtras();
        boolean selecao = false;

        if (bundle != null) {
            selecao = bundle.getBoolean("selecao", false);
        }

        if (selecao) {
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    ObjetoInventario objeto = (ObjetoInventario) parent.getItemAtPosition(position);
                    if (verificarTipo(bundle.getString("tipo"), objeto.getTipoObjeto())) {
                        intent.putExtra(ITEM_ID, objeto.getMecsimples_id());
                        intent.putExtra(ITEM_TIPO, objeto.getTipoObjeto());
                        intent.putExtra(ITEM_ARQUIVO, objeto.getArquivo());
                        setResult(Activity.RESULT_OK, intent);
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), R.string.objeto_invalido, Toast.LENGTH_LONG).show();
                    }
                }
            });
        } else {
            recuperarObjetos();
        }

        instance = this;
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (inventarioArrayAdapter != null) {
            inventarioArrayAdapter.notifyDataSetChanged();
        }
    }

    public void recuperarObjetos() {
        InformacoesTemporarias.inventario.clear();
        RecuperarObjetosInventario.recuperar(this);
    }

    private boolean verificarTipo(String tipoRequisitado, String tipoSelecionado) {
        if (tipoRequisitado.equals(Constantes.TIPO_MECANICA_DFOTOS)) {
            if (tipoSelecionado.equals(Constantes.TIPO_MECANICA_CFOTOS)) {
                return true;
            } else {
                return false;
            }
        }
        if (tipoRequisitado.equals(Constantes.TIPO_MECANICA_DOBJETOS3D)) {
            throw new UnsupportedOperationException();
        }
        if (tipoRequisitado.equals(Constantes.TIPO_MECANICA_DSONS)) {
            if (tipoSelecionado.equals(Constantes.TIPO_MECANICA_CSONS)) {
                return true;
            } else {
                return false;
            }
        }
        if (tipoRequisitado.equals(Constantes.TIPO_MECANICA_DTEXTOS)) {
            if (tipoSelecionado.equals(Constantes.TIPO_MECANICA_CTEXTOS)) {
                return true;
            } else {
                return false;
            }
        }
        if (tipoRequisitado.equals(Constantes.TIPO_MECANICA_DVIDEOS)) {
            if (tipoSelecionado.equals(Constantes.TIPO_MECANICA_CVIDEOS)) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    @Override
    protected void onResume() {
        atualizarLista();
        super.onResume();
    }

    InventarioArrayAdapter inventarioArrayAdapter = null;

    public void atualizarLista() {
        if (inventarioArrayAdapter != null) {
            inventarioArrayAdapter.notifyDataSetChanged();
        } else {
            inventarioArrayAdapter = new InventarioArrayAdapter(this, R.layout.invertario_item_lista, InformacoesTemporarias.inventario);
            inventarioArrayAdapter.registerDataSetObserver(new DataSetObserver() {
                @Override
                public void onChanged() {
                    inventarioArrayAdapter = new InventarioArrayAdapter(InventarioActivity.this, R.layout.invertario_item_lista, InformacoesTemporarias.inventario);
                    inventarioArrayAdapter.registerDataSetObserver(this);
                    listView.setAdapter(inventarioArrayAdapter);
                }
            });
            listView.setAdapter(inventarioArrayAdapter);
        }
    }

    public static InventarioActivity getInstace() {
        return instance;
    }

    @Override
    public void onBackPressed() {
        TelaPrincipalActivity instance = TelaPrincipalActivity.getInstance();
        if (instance != null) {
            instance.voltar();
        }
    }
}
