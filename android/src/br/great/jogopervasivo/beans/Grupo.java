package br.great.jogopervasivo.beans;

import java.util.List;

/**
 * Created by messiaslima on 15/12/2014.
 * @author messiaslima
 */
public class Grupo {
    private int id;
    private String nome;
    private int tipoJogador;
    private List<Jogador> jogadores;

    public List<Jogador> getJogadores() {
        return jogadores;
    }

    public void setJogadores(List<Jogador> jogadores) {
        this.jogadores = jogadores;
    }

    public int getTipoJogador() {
        return tipoJogador;
    }

    public void setTipoJogador(int tipoJogador) {
        this.tipoJogador = tipoJogador;
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

    @Override
    public String toString() {
        return nome;
    }
}
