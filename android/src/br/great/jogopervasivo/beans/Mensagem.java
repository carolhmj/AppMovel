package br.great.jogopervasivo.beans;

/**
 * Created by messiaslima on 07/04/2015.
 * @author messiaslima
 */
public class Mensagem {
    private String autor, message;

    public Mensagem(String autor, String message) {
        this.autor = autor;
        this.message = message;
    }

    public String getAuthor() {
        return autor;
    }

    public String getMessage() {
        return message;
    }
}
