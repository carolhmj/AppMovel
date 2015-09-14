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
 * Created by messiaslima on 13/08/2015.
 *
 * @author messiaslima
 */
public class DownloadMTL extends AsyncTask<Void, Void, File> {
    private String arquivo;
    File textura;

    /**
     * @param arquivo nome do arquivo a ser baixado
     */
    public DownloadMTL(String arquivo) {
        this.arquivo = arquivo;
    }

    /**
     * método que baixa o arquivo
     *
     * @return file do arquivo baixado referenciado ja no sistema de arquivos local
     */
    public File downloadMTLSincrono() throws IOException {

        textura = InformacoesTemporarias.criarMTLTemporario();
        Log.i(Constantes.TAG, "Baixando mtl:" + arquivo + " em " + Armazenamento.resgatarIPArquivos(LoginActivity.getInstance()) + "objeto3d/" + arquivo);
        URL url = new URL(Armazenamento.resgatarIPArquivos(LoginActivity.getInstance()) + "objeto3d/" + arquivo);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        InputStream input = connection.getInputStream();
        FileOutputStream fos = new FileOutputStream(textura);
        int bytes;
        while ((bytes = input.read()) != -1) {
            fos.write(bytes);
        }
        input.close();
        fos.close();
        connection.disconnect();
        Log.i("Debug", "textura baixada: " + arquivo);
        return textura;
    }

    @Override
    protected File doInBackground(Void... params) {
        try {
            return downloadMTLSincrono();
        } catch (IOException ioe) {
            if (textura != null) {
                textura.delete();
            }
            return null;
        }
    }
}
