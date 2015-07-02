package br.great.jogopervasivo.bancoDeDados;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.util.ArrayList;
import java.util.List;

import br.great.jogopervasivo.beans.InstanciaDeJogo;
import br.great.jogopervasivo.beans.Jogo;

/**
 * Created by messiaslima on 25/02/2015.
 *
 * @author messiaslima
 * @version 1.0
 * @since 1.0
 */
public class JogoDAO {
    private SQLiteDatabase database;
    private String[] colunas = {
            CriadorDoBancoDeDados.JOGO_ID,
            CriadorDoBancoDeDados.JOGO_NOME,
            CriadorDoBancoDeDados.JOGO_NOME_FICTICIO,
            CriadorDoBancoDeDados.JOGO_ICONE,
            CriadorDoBancoDeDados.JOGO_GRUPO_ID};
    private CriadorDoBancoDeDados dbCreator;

    /**
     * Construtor da classe DAO
     *
     * @param context activity que irá usar o banco de dados
     */
    public JogoDAO(Context context) {
        dbCreator = new CriadorDoBancoDeDados(context);
    }

    /**
     * Deve ser chamado antes de usar o banco de dados
     */
    public void abrir() {
        database = dbCreator.getWritableDatabase();
    }

    /**
     * Deve ser chamado após a utilização do banco
     */
    public void fechar() {
        database.close();
        dbCreator.close();
    }

    /**
     * Salva um novo registro no banco
     *
     * @param id   identificador do jogo
     * @param nome nome do jogo
     */
    public void salvar(int id, String nome) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(CriadorDoBancoDeDados.JOGO_ID, id);
        contentValues.put(CriadorDoBancoDeDados.JOGO_NOME, nome);
        database.insert(CriadorDoBancoDeDados.TABELA_JOGO, null, contentValues);
    }

    /**
     * Salva um novo registro no banco
     *
     * @param jogo objeto da classe Jogo a ser salva
     *
     */
    public void salvar(InstanciaDeJogo jogo) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(CriadorDoBancoDeDados.JOGO_ID, jogo.getId());
        contentValues.put(CriadorDoBancoDeDados.JOGO_NOME, jogo.getNome());
        contentValues.put(CriadorDoBancoDeDados.JOGO_NOME_FICTICIO,jogo.getNomeFicticio());
        contentValues.put(CriadorDoBancoDeDados.JOGO_ICONE,jogo.getIcone());
        contentValues.put(CriadorDoBancoDeDados.JOGO_GRUPO_ID,jogo.getGrupoId());
        database.insert(CriadorDoBancoDeDados.TABELA_JOGO, null, contentValues);
    }

    /**
     * @return todos os jogos que o usuário está participando
     * */
    public List<InstanciaDeJogo> recuperarTodos(){
        List<InstanciaDeJogo> jogos = new ArrayList<>();
        Cursor cursor = database.query(CriadorDoBancoDeDados.TABELA_JOGO,colunas,null,null,null,null,CriadorDoBancoDeDados.JOGO_NOME);
        while(cursor.moveToNext()){
            InstanciaDeJogo jogo = new InstanciaDeJogo();
            jogo.setId(cursor.getInt(0));
            jogo.setNome(cursor.getString(1));
            jogo.setNomeFicticio(cursor.getString(2));
            jogo.setIcone(cursor.getString(3));
            jogo.setGrupoId(cursor.getInt(4));
            jogos.add(jogo);
        }
        cursor.close();
        return jogos;
    }
}
