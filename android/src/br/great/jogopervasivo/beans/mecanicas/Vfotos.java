package br.great.jogopervasivo.beans.mecanicas;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import br.ufc.great.arviewer.android.R;
import br.great.jogopervasivo.actvititesDoJogo.TelaPrincipalActivity;
import br.great.jogopervasivo.beans.Mecanica;
import br.great.jogopervasivo.util.Constantes;
import br.great.jogopervasivo.util.InformacoesTemporarias;
import br.great.jogopervasivo.webServices.Servidor;

/**
 * Created by messiaslima on 03/03/2015.
 *
 * @author messiaslima
 */
public class Vfotos extends Mecanica implements Imecanica {
    private int idFotos;
    private String arqImage;
    public static final int REQUEST_CODE_VER_IMAGEM = 6;
    private static final String PASTA_IMAGENS = Constantes.PASTA_DE_ARQUIVOS;

    public int getIdFotos() {
        return idFotos;
    }

    public void setIdFotos(int id) {
        this.idFotos = id;
    }

    public String getArqImage() {
        return arqImage;
    }

    public void setArqImage(String arqImage) {
        this.arqImage = arqImage;
    }

    @Override
    public void realizarMecanica(final TelaPrincipalActivity context) {

        final String[] result = new String[1];
        final Bitmap[] bm = new Bitmap[1];

        if (getEstado()==2){
            return;
        }

        new AsyncTask<Void, Void, Boolean>() {
            ProgressDialog progressDialog;

            @Override
            protected void onPreExecute() {
                progressDialog = new ProgressDialog(context);
                progressDialog.setCancelable(false);
                progressDialog.setMessage(context.getString(R.string.obtendo_informacoes));

                super.onPreExecute();
            }

            @Override
            protected Boolean doInBackground(Void... params) {
       /*         JSONObject acao = new JSONObject();
                JSONObject mecanica = new JSONObject();
                JSONArray requisiscao = new JSONArray();
                try {
                    acao.put("acao", 107);
                    mecanica.put("jogo_id", InformacoesTemporarias.jogoAtual.getId());
                    mecanica.put("grupo_id", InformacoesTemporarias.grupoAtual.getId());
                    mecanica.put("mecanica_id", getId());
                    mecanica.put("jogador_id", InformacoesTemporarias.idJogador);
                    requisiscao.put(0, acao);
                    requisiscao.put(1, mecanica);
                    JSONObject resposta = new JSONArray(Servidor.fazerGet(requisiscao.toString())).getJSONObject(0);
                    return resposta.getInt("result") != 0;
                } catch (JSONException je) {
                    Log.e(Constantes.TAG, "erro no json " + je.getMessage());
                    je.printStackTrace();
                    return false;
                }*/
                return verificarAutorizacaoDaMecanica();
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                progressDialog.dismiss();
                if (aBoolean) {

                    InformacoesTemporarias.jogoOcupado = true;
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    View view = context.getLayoutInflater().inflate(R.layout.vfotos, null);
                    builder.setView(view);
                    builder.setTitle(getNome());
                    if (arqImage.trim().length() > 0) {
                        ImageView imageView = (ImageView) view.findViewById(R.id.vfotos_imageView);
                        bm[0] = BitmapFactory.decodeFile(PASTA_IMAGENS+"/"+ arqImage);
                        imageView.setImageBitmap(bm[0]);
                    } else {
                        TextView textView = (TextView) view.findViewById(R.id.vfotos_textView);
                        textView.setText(R.string.falha_de_conexao);
                    }
                    builder.setNegativeButton(R.string.OK, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
//                            confirmarRealizacao(context, null,null,null);
                        }
                    });
                    builder.create().show();

                } else {
                    mostarToastFeedback(context);
                }
            }
        }.execute();

        File f = new File(
                Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_PICTURES
                ),
                arqImage
        );

        bm[0] = BitmapFactory.decodeFile(PASTA_IMAGENS+"/"+ arqImage);
        if (bm[0] != null) {
            Log.d("VFotos", "non null bitmap");
        } else {
            Log.d("VFotos", "null bitmap");
        }
        result[0] = f.getAbsolutePath();
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(f);
            bm[0].compress(Bitmap.CompressFormat.PNG, 100, out);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //Tentando abrir a imagem usando o puzzle
        Log.d("VFotos", "Call Puzzle");
        Intent callPuzzle = new Intent("android.puzzletest.PUZZLEACTIVITY");
        callPuzzle.putExtra("DIFFICULTY", 0);
//        Log.d("VFotos", "image value:" + PASTA_IMAGENS+"/"+ arqImage);
        Log.d("VFotos", "image value:" + result[0]);
//        Log.d("VFotos", "URI value: " + Uri.fromFile(new File(PASTA_IMAGENS+"/"+ arqImage)).toString());
        callPuzzle.putExtra("IMAGE", result[0]);
//        callPuzzle.putExtra("IMAGE", Uri.fromFile(new File(PASTA_IMAGENS+"/"+ arqImage)).toString());
        context.startActivityForResult(callPuzzle, REQUEST_CODE_VER_IMAGEM);

        //Codigo pra abrir imagem direto na galeria
        /*Intent it = new Intent();
        it.setAction(android.content.Intent.ACTION_VIEW);
        it.setDataAndType(Uri.fromFile(new File(PASTA_IMAGENS + arqImage)), "image/jpg");
        context.startActivityForResult(it, REQUEST_CODE_VER_IMAGEM);
        */
    }

}
