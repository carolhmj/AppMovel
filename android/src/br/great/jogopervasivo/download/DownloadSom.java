package br.great.jogopervasivo.download;

import android.os.AsyncTask;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import br.great.jogopervasivo.LoginActivity;
import br.great.jogopervasivo.util.Armazenamento;
import br.great.jogopervasivo.util.Constantes;
import br.great.jogopervasivo.util.InformacoesTemporarias;

/**
 * Created by messiaslima on 04/05/2015.
 *
 * @author messiaslima
 * @version 1.0
 */
public class DownloadSom extends AsyncTask<Void, Void, File> {

    private String arquivo;

    public DownloadSom(String nomeDoArquivo) {
        this.arquivo = nomeDoArquivo;
    }

    public File downloadSomSincrono() throws IOException {
        File som = InformacoesTemporarias.criarAudioTemporario();
        Log.i(Constantes.TAG,"Baixando audio:" + arquivo+ " em "+ Armazenamento.resgatarIPArquivos(LoginActivity.getInstance()) + "csons/" + arquivo);
        URL url = new URL(Armazenamento.resgatarIPArquivos(LoginActivity.getInstance())+ "csons/" + arquivo);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        InputStream input = connection.getInputStream();
        FileOutputStream fos = new FileOutputStream(som);
        int bytes;
        while ((bytes = input.read())!=-1){
            fos.write(bytes);
        }
        input.close();
        fos.close();
        connection.disconnect();
        Log.i("Debug", "Som baixado: " + arquivo);
        return som;
    }

    @Override
    protected File doInBackground(Void... params) {
        try {
            return downloadSomSincrono();
        } catch (IOException ioe) {
            return null;
        }
    }


}
