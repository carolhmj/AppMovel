package br.great.jogopervasivo.bancoDeDados;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import br.great.jogopervasivo.util.Constantes;

/**
 * Created by messiaslima on 11/02/2015.
 *
 * @author messiaslima
 * @version 1.0
 * @since 1.0
 */
public class CriadorDoBancoDeDados extends SQLiteOpenHelper {

    //Informações da tabela jogo
    public static final String TABELA_JOGO = "jogo";
    public static final String JOGO_ID = "id_jogo";
    public static final String JOGO_NOME = "jogo_nome";
    public static final String JOGO_NOME_FICTICIO = "jogo_nome_ficticio";
    public static final String JOGO_ICONE = "jogo_icone";
    public static final String JOGO_GRUPO_ID="jogo_grupo_id";

    //Informações da tabela grupo
    public static final String TABELA_GRUPO = "grupo";
    public static final String GRUPO_ID = "id_grupo";
    public static final String GRUPO_NOME = "nome_grupo";

    public CriadorDoBancoDeDados(Context context) {
        super(context, "jogo_pervasivo.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        criarTabelaJogo(db);
        criarTabelaGrupo(db);
    }

    /**
     * criada para armazenar as várias instancias de jogos em que o usuário entrar, ja que o sharedPreferences só salva um registro por TAG
     */
    private void criarTabelaJogo(SQLiteDatabase db) {
        String query = "create table " + TABELA_JOGO + "" +
                "( " + JOGO_ID + " integer primary key not null" +
                "," + JOGO_NOME + " varchar" +
                ","+JOGO_NOME_FICTICIO+" varchar" +
                ","+JOGO_ICONE+" varchar" +
                ","+JOGO_GRUPO_ID+" integer);";
        db.execSQL(query);
    }

    /**
     * criada para armazenar os varios grupos em que o usuário entrar, ja que o sharedPreferences só salva um registro por TAG
     */
    private void criarTabelaGrupo(SQLiteDatabase db) {
        String query = "create table " + TABELA_GRUPO + "" +
                "( " + GRUPO_ID + " integer primary key not null, " +
                "" + GRUPO_NOME + " varchar );";
        db.execSQL(query);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS"+TABELA_JOGO+";");
        db.execSQL("DROP TABLE IF EXISTS"+TABELA_GRUPO+";");
        onCreate(db);
        Log.i(Constantes.TAG,"Destruiu o banco");
    }
}
