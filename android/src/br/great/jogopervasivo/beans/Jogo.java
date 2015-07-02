package br.great.jogopervasivo.beans;

import java.util.List;

/**
 * Created by messiaslima on 28/11/2014.
 */
public class Jogo {
    private int id;
    private String nome;
    private String icone;
    private List<Grupo> grupos;

    public List<Grupo> getGrupos() {
        return grupos;
    }

    public void setGrupos(List<Grupo> grupos) {
        this.grupos = grupos;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getIcone() {
        return icone;
    }

    public void setIcone(String icone) {
        this.icone = icone;
    }

    @Override
    public String toString() {
        return nome;
    }
}
