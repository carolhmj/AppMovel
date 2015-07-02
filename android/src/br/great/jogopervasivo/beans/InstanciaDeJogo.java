package br.great.jogopervasivo.beans;

/**
 * Created by messiaslima on 06/02/2015.
 */
public class InstanciaDeJogo {
    private String nomeFicticio;
    private int grupoId;
    private String nome;
    private String icone;
    private int id;
    private String grupoNome;
    private boolean jogadorParticipando;

    public boolean isJogadorParticipando() {
        return jogadorParticipando;
    }

    public void setJogadorParticipando(boolean jogadorParticipando) {
        this.jogadorParticipando = jogadorParticipando;
    }

    public String getGrupoNome() {
        return grupoNome;
    }

    public void setGrupoNome(String grupoNome) {
        this.grupoNome = grupoNome;
    }

    public String getNomeFicticio() {
        return nomeFicticio;
    }

    public void setNomeFicticio(String nomeFicticio) {
        this.nomeFicticio = nomeFicticio;
    }

    public int getGrupoId() {
        return grupoId;
    }

    public void setGrupoId(int grupoId) {
        this.grupoId = grupoId;
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
