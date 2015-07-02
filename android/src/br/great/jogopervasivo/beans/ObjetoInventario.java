package br.great.jogopervasivo.beans;

/**
 * Created by messiaslima on 08/05/2015.
 * @author messiaslima
 * @version 1.0
 */
public class ObjetoInventario {
    private int mecsimples_id;
    private String nome;
    private String tipoObjeto;
    private String arquivo;

    public String getArquivo() {
        return arquivo;
    }

    public void setArquivo(String arquivo) {
        this.arquivo = arquivo;
    }

    public String getTipoObjeto() {
        return tipoObjeto;
    }

    public void setTipoObjeto(String tipoObjeto) {
        this.tipoObjeto = tipoObjeto;
    }

    public int getMecsimples_id() {
        return mecsimples_id;
    }

    public void setMecsimples_id(int mecsimples_id) {
        this.mecsimples_id = mecsimples_id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    @Override
    public String toString() {
        return nome;
    }
}
