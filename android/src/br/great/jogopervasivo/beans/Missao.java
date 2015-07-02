package br.great.jogopervasivo.beans;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by messiaslima on 24/06/2015.
 * @author messiaslima
 */
public class Missao {
    private int id;
    private boolean reqterminar;
    private String nome;
    private int finMecanicas;
    private LatLng posicaoInicial;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isReqterminar() {
        return reqterminar;
    }

    public void setReqterminar(boolean reqterminar) {
        this.reqterminar = reqterminar;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getFinMecanicas() {
        return finMecanicas;
    }

    public void setFinMecanicas(int finMecanicas) {
        this.finMecanicas = finMecanicas;
    }

    public LatLng getPosicaoInicial() {
        return posicaoInicial;
    }

    public void setPosicaoInicial(LatLng posicaoInicial) {
        this.posicaoInicial = posicaoInicial;
    }
}
