package br.great.jogopervasivo.bancoDeDados;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import br.great.jogopervasivo.beans.Grupo;
import br.great.jogopervasivo.beans.Jogo;

/**
 * Created by messiaslima on 25/02/2015.
 *
 * @author messiaslima
 * @version 1.0
 * @since 1.0
 */
public class GrupoDAO {
    private SQLiteDatabase database;
    private String[] colunas = {CriadorDoBancoDeDados.GRUPO_ID, CriadorDoBancoDeDados.GRUPO_NOME};
    private CriadorDoBancoDeDados dbCreator;

    /**
     * Construtor da classe DAO
     *
     * @param context activity que irá usar o banco de dados
     */
    public GrupoDAO(Context context) {
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
     * @param id   identificador do grupo
     * @param nome nome do grupo
     */
    public void salvar(int id, String nome) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(CriadorDoBancoDeDados.GRUPO_ID, id);
        contentValues.put(CriadorDoBancoDeDados.GRUPO_NOME, nome);
        database.insert(CriadorDoBancoDeDados.TABELA_GRUPO, null, contentValues);
    }

    /**
     * Salva um novo registro no banco
     *
     * @param grupo Objeto da classe Grupo a ser salva
     */
    public void salvar(Grupo grupo) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(CriadorDoBancoDeDados.GRUPO_ID, grupo.getId());
        contentValues.put(CriadorDoBancoDeDados.GRUPO_NOME, grupo.getNome());
        database.insert(CriadorDoBancoDeDados.TABELA_GRUPO, null, contentValues);
    }

    /**
     * @return todos os grupos que o usuário está participando
     */
    public List<Grupo> recuperarTodos() {
        List<Grupo> grupos = new ArrayList<>();
        Cursor cursor = database.query(CriadorDoBancoDeDados.TABELA_GRUPO, colunas, null, null, null, null, CriadorDoBancoDeDados.GRUPO_NOME);
        while (cursor.moveToNext()) {
            Grupo grupo = new Grupo();
            grupo.setId(cursor.getInt(0));
            grupo.setNome(cursor.getString(1));
            grupos.add(grupo);
        }
        cursor.close();
        return grupos;
    }
}
