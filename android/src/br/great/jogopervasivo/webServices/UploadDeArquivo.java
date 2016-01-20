package br.great.jogopervasivo.webServices;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import br.great.jogopervasivo.actvititesDoJogo.TelaPrincipalActivity;
import br.great.jogopervasivo.util.Armazenamento;
import br.ufc.great.arviewer.android.R;
import br.great.jogopervasivo.beans.mecanicas.CFotos;
import br.great.jogopervasivo.beans.mecanicas.CSons;
import br.great.jogopervasivo.beans.mecanicas.CVideos;
import br.great.jogopervasivo.util.Constantes;

/**
 * Created by messiaslima on 11/03/2015.
 *
 * @author messiaslima
 */
public class UploadDeArquivo {

    public static void enviarFoto(final Context context, final File arquivo, final CFotos cFotos) {
        new AsyncTask<Void, Void, Boolean>() {
            ProgressDialog progressDialog = new ProgressDialog(context);
            String nomeDoArquivo;

            @Override
            protected void onPreExecute() {
                progressDialog.setMessage(context.getString(R.string.enviando_arquivo));
                progressDialog.setCancelable(false);
                progressDialog.show();
            }

            @Override
            protected void onProgressUpdate(Void... values) {
                progressDialog.setMessage(context.getString(R.string.enviando_metadados));
            }

            @Override
            protected Boolean doInBackground(Void... params) {
                nomeDoArquivo = enviarArquivo(arquivo, "/WebServidor/webresources/Servidor/setFoto");
                if (nomeDoArquivo.equals("")) {
                    return false;
                }
                Log.i(Constantes.TAG, "Nome do arquivo recebido: " + nomeDoArquivo);
                publishProgress();

                return true;// enviarMetadadosFoto(nomeDoArquivo,context,cFotos);
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                progressDialog.dismiss();

                if (aBoolean) {
                    Toast.makeText(context, R.string.arquivo_enviado, Toast.LENGTH_SHORT).show();
                    cFotos.confirmarRealizacao(context, nomeDoArquivo,null,null);
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle(cFotos.getNome());
                    builder.setMessage(R.string.falha_de_conexao);
                    builder.setNeutralButton(R.string.OK, null);
                    builder.create().show();
                }
            }
        }.execute();
    }

    public static void enviarVideo(final Context context, final File arquivo, final CVideos cVideos) {
        new AsyncTask<Void, Void, Boolean>() {
            String nomeDoArquivo;
            ProgressDialog progressDialog = new ProgressDialog(context);

            @Override
            protected void onPreExecute() {
                progressDialog.setMessage(context.getString(R.string.enviando_arquivo));
                progressDialog.setCancelable(false);
                progressDialog.show();
            }

            @Override
            protected void onProgressUpdate(Void... values) {
                progressDialog.setMessage(context.getString(R.string.enviando_metadados));
            }

            @Override
            protected Boolean doInBackground(Void... params) {
                nomeDoArquivo = enviarArquivo(arquivo, "/WebServidor/webresources/Servidor/setVideo");
                if (nomeDoArquivo.equals("")) {
                    return false;
                }
                Log.i(Constantes.TAG, "Nome do arquivo recebido: " + nomeDoArquivo);
                publishProgress();

                return true;// enviarMetadadosVideo(nomeDoArquivo,context,cVideos);

            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                progressDialog.dismiss();

                if (aBoolean) {
                    Toast.makeText(context, R.string.arquivo_enviado, Toast.LENGTH_SHORT).show();
                    cVideos.confirmarRealizacao(context, nomeDoArquivo,null,null);
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle(cVideos.getNome());
                    builder.setMessage(R.string.falha_de_conexao);
                    builder.setNeutralButton(R.string.OK, null);
                    builder.create().show();
                }
            }
        }.execute();
    }

    public static void enviarAudio(final Context context, final File arquivo, final CSons cSons) {
        new AsyncTask<Void, Void, Boolean>() {
            ProgressDialog progressDialog = new ProgressDialog(context);
            String nomeDoArquivo;

            @Override
            protected void onPreExecute() {
                progressDialog.setMessage(context.getString(R.string.enviando_arquivo));
                progressDialog.setCancelable(false);
                progressDialog.show();
            }

            @Override
            protected void onProgressUpdate(Void... values) {
                progressDialog.setMessage(context.getString(R.string.enviando_metadados));
            }

            @Override
            protected Boolean doInBackground(Void... params) {
                nomeDoArquivo = enviarArquivo(arquivo, "/WebServidor/webresources/Servidor/setSom");
                if (nomeDoArquivo.equals("")) {
                    return false;
                }
                arquivo.delete();

                return true;//enviarMetadadosSom(nomeDoArquivo,context,cSons);
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                progressDialog.dismiss();
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle(R.string.enviar);
                if (aBoolean) {
                    builder.setMessage(R.string.arquivo_enviado);
                    cSons.confirmarRealizacao(context, nomeDoArquivo,null,null);
                } else {
                    builder.setMessage(R.string.falha_de_conexao);
                }
                builder.setNeutralButton(R.string.OK, null);
            }
        }.execute();
    }


    private static String enviarArquivo(File arquivo, String uriServico) {
        try {

            URL url = new URL("http://"+Armazenamento.resgatarIP(TelaPrincipalActivity.getInstance())+":"+Armazenamento.resgatarPorta(TelaPrincipalActivity.getInstance())+ uriServico);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(1000 * 10);
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            OutputStream os = new BufferedOutputStream(connection.getOutputStream());
            FileInputStream fis = new FileInputStream(arquivo);
            byte[] b = new byte[(int) arquivo.length()];
            fis.read(b);
            os.write(b);
            //clean up
            os.flush();
            DataInputStream input = new DataInputStream(connection.getInputStream());
            String linha;
            String resultado = "";
            while ((linha = input.readLine()) != null) {
                resultado = resultado + linha;
            }
            input.close();
            Log.i(Constantes.TAG, resultado);
            return resultado;
        } catch (MalformedURLException mue) {
            Log.e(Constantes.TAG, "URL mal formada");
            mue.printStackTrace();
        } catch (IOException ioe) {
            Log.e(Constantes.TAG, "Erro de IO");
            ioe.printStackTrace();
        }
        return "";
    }
}
