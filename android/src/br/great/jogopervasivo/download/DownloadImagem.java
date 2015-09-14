package br.great.jogopervasivo.download;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.io.File;
import java.io.FileNotFoundException;
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
 * Created by messiaslima on 03/02/2015.
 *
 * @author messiaslima
 * @version 1.0
 * @since 1.0
 */
public class DownloadImagem extends AsyncTask<String, Void, Bitmap> {

    private ImageView imageView = null;

    public DownloadImagem(ImageView imageView) {
        this.imageView = imageView;
    }


    @Override
    protected Bitmap doInBackground(String... params) {
        try {
            return downloadImagemSincrono(params[0]);
        } catch (IOException ioe) {
            return null;
        } catch (StackOverflowError sof) {
            return null;
        }
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (imageView != null) {
            imageView.setImageBitmap(bitmap);
        }
    }

    /**
     * Baixa uma imagem do servidor do jogo
     *
     * @param imagemNome nome da imagem a ser baixada
     * @return Bitmap da imagem
     */
    public Bitmap downloadImagemSincrono(String imagemNome) throws StackOverflowError, IOException {

        Log.i(Constantes.TAG,"Baixando foto:" + imagemNome+ " em "+ Armazenamento.resgatarIPArquivos(LoginActivity.getInstance()) + "cfotos/" + imagemNome);
        URL url = new URL(Armazenamento.resgatarIPArquivos(LoginActivity.getInstance()) + "cfotos/" + imagemNome);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        InputStream input = connection.getInputStream();
        Bitmap img = BitmapFactory.decodeStream(input);
        connection.disconnect();
        if (imageView == null) { //apenas pra fim de debug
            Log.i("Debug", "Imagem baixada: " + imagemNome);
        }
        return img;

    }

    //Salva a imagem no sistema de arquivos padrão

    /**
     * Salva imagem no sistema de arquivos padrão
     *
     * @param foto Bitmap da imagem a ser salva
     * @param nome nome da imagem
     */
    public static void salvarImagemNoSistemaDeArquivos(String nome, Bitmap foto) throws FileNotFoundException {
        File fotoFile = InformacoesTemporarias.criarImagemTemporaria();
        FileOutputStream fos = new FileOutputStream(fotoFile);
        foto.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        fotoFile.renameTo(new File(fotoFile.getParent() + "/" + nome));
    }
}
