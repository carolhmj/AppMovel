package br.ufc.great.arviewer;

/**
 * Created by messiaslima on 07/07/2015.
 * @author messiaslima
 */
public class Resultado {
    private boolean clicado;

    public Resultado(boolean clicado){
        setClicado(clicado);
    }

    public boolean isClicado() {
        return clicado;
    }

    public void setClicado(boolean clicado) {
        this.clicado = clicado;
    }
}
