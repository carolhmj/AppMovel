package br.great.jogopervasivo.actvititesDoJogo;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;


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
        }else{
            recuperarObjetos();
        }


    }

    public void recuperarObjetos(){
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

    public void atualizarLista() {
        ArrayAdapter<ObjetoInventario> inventarioArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, InformacoesTemporarias.inventario);
        listView.setAdapter(inventarioArrayAdapter);
    }
}
