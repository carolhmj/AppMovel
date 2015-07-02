package br.great.jogopervasivo.actvititesDoJogo;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;


import br.great.jogopervasivo.beans.Jogo;
import br.great.jogopervasivo.util.Armazenamento;
import br.great.jogopervasivo.util.Constantes;
import br.great.jogopervasivo.util.InformacoesTemporarias;
import br.great.jogopervasivo.webServices.RecuperarJogosDisponiveis;
import br.ufc.great.arviewer.android.R;

public class TiposDeJogosDisponiveisActivity extends Activity {

    ListView lista;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.jogos_disponiveis);

        final SeekBar seekBar = (SeekBar) findViewById(R.id.jogos_disponiveis_bar);
        Button botao = (Button) findViewById(R.id.jogos_disponiveis_botao);
        lista = (ListView) findViewById(R.id.jogos_disponiveis_lista);
        final TextView label = (TextView) findViewById(R.id.jogos_disponiveis_label);

        //Quando clica no item da listView
        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Jogo j = (Jogo) parent.getItemAtPosition(position);
                Intent intent = new Intent(TiposDeJogosDisponiveisActivity.this, InstanciasExecutandoActivity.class);
                Bundle extras = new Bundle();
                extras.putInt(Constantes.JOGO_PAI_ID, j.getId());
                extras.putString(Constantes.JOGO_PAI_NOME,j.getNome());
                intent.putExtras(extras);
                startActivity(intent);
                InformacoesTemporarias.jogoPai = j;
                finish();
            }
        });

        //Quando alteramos o valor da seek bar
        //Muda a cor do TextView e altera  seu valor
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                label.setText(Integer.toString(progress) + "Km");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                label.setTextColor(Color.GRAY);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                label.setTextColor(Color.BLACK);
                if (seekBar.getProgress() == 0) {
                    seekBar.setProgress(1);
                }
            }
        });

        //Quando clica no botão de buscar jogos
        botao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                atualizarLista(seekBar.getProgress());
            }
        });
        atualizarLista(seekBar.getProgress());
    }

    private void atualizarLista(int distancia) {
        //Recupera a localização atual e usa como parâmetro de busca dos jogos
        Location location = Armazenamento.resgatarUltimaLocalizacao(this);
        RecuperarJogosDisponiveis recuperarJogosDisponiveis = new RecuperarJogosDisponiveis(this, lista);
        try {
            recuperarJogosDisponiveis.execute(Double.toString(location.getLatitude()), Double.toString(location.getLongitude()), Integer.toString(distancia));
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

}
