package br.great.jogopervasivo.beans;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import br.great.jogopervasivo.download.DownloadImagem;
import br.great.jogopervasivo.download.DownloadObj;
import br.great.jogopervasivo.download.DownloadSom;
import br.great.jogopervasivo.download.DownloadTexturaPNG;
import br.great.jogopervasivo.download.DownloadVideo;
import br.great.jogopervasivo.util.Constantes;
import br.great.jogopervasivo.util.InformacoesTemporarias;

/**
 * Created by messiaslima on 11/02/2015.
 * @author messiaslima

 */
public class Arquivo {
    private String tipo;
    private int prioridade;
    private int mecanica_id;
    private String arquivo;
    private String textura;
    private int arquivo_id;
    private int tentativasDownload = 0;

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public int getPrioridade() {
        return prioridade;
    }

    public void setPrioridade(int prioridade) {
        this.prioridade = prioridade;
    }

    public int getMecanica_id() {
        return mecanica_id;
    }

    public void setMecanica_id(int mecanica_id) {
        this.mecanica_id = mecanica_id;
    }

    public String getArquivo() {
        return arquivo;
    }

    public void setArquivo(String arquivo) {
        this.arquivo = arquivo;
    }

    public int getArquivo_id() {
        return arquivo_id;
    }

    public void setArquivo_id(int arquivo_id) {
        this.arquivo_id = arquivo_id;
    }

    public boolean baixar(Context context) {
        // Caso seja uma foto

        if (tentativasDownload == 5) {
            return false;
        }
        tentativasDownload++;
        switch (tipo) {
            case Constantes.TIPO_MECANICA_VFOTOS:
                try {
                    DownloadImagem dImage = new DownloadImagem(null);
                    Bitmap foto;
                    foto = dImage.downloadImagemSincrono(getArquivo());
                    if (foto == null) {
                    /*  Se a conexão estiver ativa, ele tenta baixar de novo até conseguir.
                        O jogo só pode começar se essa imagem estiver baixada.
                        Se não tiver conexão, cancela, retorna false e o jogo não inicia */
                        if (InformacoesTemporarias.conexaoAtiva(context)) {
                            baixar(context);
                        } else {
                            return false;
                        }
                    } else {
                        DownloadImagem.salvarImagemNoSistemaDeArquivos(arquivo, foto);
                    }
                } catch (FileNotFoundException fnfe) {
                    Log.e(Constantes.TAG, "Arquivo não encontrado");
                    return false;
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }
                break;
            case Constantes.TIPO_MECANICA_VSONS:
                DownloadSom downloadSom = new DownloadSom(getArquivo());
                try {
                    File som = downloadSom.downloadSomSincrono();
                    if (som == null) {
                    /*  Se a conexão estiver ativa, ele tenta baixar de novo até conseguir.
                        O jogo só pode começar se essa imagem estiver baixada.
                        Se não tiver conexão, cancela, retorna false e o jogo não inicia */
                        if (InformacoesTemporarias.conexaoAtiva(context)) {
                            baixar(context);
                        } else {
                            return false;
                        }
                    } else {
                        salvarNoSistemaDeArquivos(som);
                    }
                } catch (FileNotFoundException fnfe) {
                    Log.e(Constantes.TAG, "Arquivo não encontrado");
                    return false;
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                    baixar(context);
                }
                break;
            case Constantes.TIPO_MECANICA_VVIDEOS:
                DownloadVideo downloadVideo = new DownloadVideo(getArquivo());
                try {
                    File video = downloadVideo.downloadVideoSincrono();
                    if (video == null) {
                        if (InformacoesTemporarias.conexaoAtiva(context)) {
                            baixar(context);
                        } else {
                            return false;
                        }
                    } else {
                        salvarNoSistemaDeArquivos(video);
                    }
                } catch (FileNotFoundException fnfe) {
                    Log.e(Constantes.TAG, "Arquivo não encontrado");

                    return false;
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                    baixar(context);
                }
                break;
            case Constantes.TIPO_MECANICA_V_OBJ_3D:
                DownloadObj downloadObj = new DownloadObj(getArquivo());
                DownloadTexturaPNG downloadTexturaPNG = new DownloadTexturaPNG(getTextura());
                try {
                    File obj = downloadObj.downloadObjSincrono();
                    File textura = downloadTexturaPNG.downloadTexturaSincrono();
                    if (obj == null || textura==null) {
                        if (InformacoesTemporarias.conexaoAtiva(context)) {
                            baixar(context);
                        } else {
                            return false;
                        }
                    } else {
                        salvarNoSistemaDeArquivos(obj);
                        salvarTexturaNoSistemaDeArquivos(textura);
                    }
                } catch (FileNotFoundException fnfe) {
                    Log.e(Constantes.TAG, "Arquivo não encontrado");
                    return false;
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                    baixar(context);
                }
                break;
            default:
                throw new UnsupportedOperationException();
        }

        return true;
    }

    public void salvarNoSistemaDeArquivos(File arquivoASerSalvo) {
        arquivoASerSalvo.renameTo(new File(arquivoASerSalvo.getParent() + "/" + getArquivo()));
    }
    public void salvarTexturaNoSistemaDeArquivos(File arquivoASerSalvo) {
        arquivoASerSalvo.renameTo(new File(arquivoASerSalvo.getParent() + "/" + getTextura()));
    }
    public String getTextura() {
        return textura;
    }

    public void setTextura(String textura) {
        this.textura = textura;
    }
}
