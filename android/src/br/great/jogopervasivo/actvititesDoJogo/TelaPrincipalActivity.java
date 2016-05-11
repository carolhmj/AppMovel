package br.great.jogopervasivo.actvititesDoJogo;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.great.jogopervasivo.arrayAdapters.ListarMecanicasAdapter;
import br.great.jogopervasivo.beans.Grupo;
import br.great.jogopervasivo.beans.Jogador;
import br.great.jogopervasivo.beans.Mecanica;
import br.great.jogopervasivo.beans.mecanicas.CFotos;
import br.great.jogopervasivo.beans.mecanicas.CSons;
import br.great.jogopervasivo.beans.mecanicas.CTextos;
import br.great.jogopervasivo.beans.mecanicas.CVideos;
import br.great.jogopervasivo.beans.mecanicas.Deixar;
import br.great.jogopervasivo.beans.mecanicas.IrLocais;
import br.great.jogopervasivo.beans.mecanicas.VObj3d;
import br.great.jogopervasivo.beans.mecanicas.VSons;
import br.great.jogopervasivo.beans.mecanicas.VVideos;
import br.great.jogopervasivo.beans.mecanicas.Vfotos;
import br.great.jogopervasivo.beans.mecanicas.Vtextos;
import br.great.jogopervasivo.util.Armazenamento;
import br.great.jogopervasivo.util.Constantes;
import br.great.jogopervasivo.util.InformacoesTemporarias;
import br.great.jogopervasivo.util.MetodosUteis;
import br.great.jogopervasivo.webServices.AtualizarLocalizacaoJogadores;
import br.great.jogopervasivo.webServices.SolicitarMissaoAtual;
import br.great.jogopervasivo.webServices.UploadDeArquivo;
import br.ufc.great.arviewer.android.AndroidLauncher;
import br.ufc.great.arviewer.android.R;

public class TelaPrincipalActivity extends Activity implements LocationListener {
    private GoogleMap mapa;
    public LocationManager locationManager = null;
    ProgressDialog progressDialog;
    Marker marcadorJogador = null, marcadorIrLocal = null;
    MarkerOptions opcoesDeMarcador;
    Map<String, Marker> hashMarcadores = new HashMap<>();
    CFotos mecanicaCFotoAtual = null;
    public static CVideos mecanicaCVideosAtual = null;
    public static Deixar mecanicaDeixarAtual = null;
    public static VVideos mecanicaVVideosAtual = null;
    public static VObj3d mecanicaVObj3dAtual = null;
    static ActionBar actionBar;
    boolean pediuMecanicas = false;
    AndroidLauncher visualizadorDeRA;

    public void setVisualizadorDeRA(AndroidLauncher visualizadorDeRA) {
        this.visualizadorDeRA = visualizadorDeRA;
    }

    //Constantes locais
    public static final int MARCADOR_LOCAL = 0;
    public static final int MARCADOR_MECANICA_REALIZADA = 4;
    public static final int MARCADOR_MECANICA_BLOQUEADA = 5;
    public static final int MARCADOR_ALIADO = 1;
    public static final int MARCADOR_ADVERSARIO = 2;
    public static final int REQUEST_CODE_FOTO = 3;
    public static final int REQUEST_CODE_VIDEO = 200;
    public static final int REQUEST_CODE_VER_VIDEO = 201;
    public static final int REQUEST_CODE_VER_OBJ_3D = 202;


    /**
     * Adiciona um marcador de qualquer tipo no mapa
     *
     * @param nome    palavra que identifica o marcador
     * @param posicao localização geografica do marcador
     * @param tipo    constante que define o tipo do marcador
     */
    private void adicionarMarcador(String nome, LatLng posicao, int tipo, String mensagemBloqueio) {
        if (hashMarcadores.containsKey(nome)) {
            hashMarcadores.get(nome).setPosition(posicao);
        } else {
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.title(nome);
            markerOptions.position(posicao);
            switch (tipo) {
                case MARCADOR_LOCAL:
                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_marcador_local));
                    break;
                case MARCADOR_ADVERSARIO:
                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_jogador_rosa));
                    if (mensagemBloqueio.equals("0")) {
                        markerOptions.alpha(markerOptions.getAlpha() / 2);
                    }
                    break;
                case MARCADOR_ALIADO:
                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_jogador));
                    break;
                case MARCADOR_MECANICA_REALIZADA:
                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_bandeira_azul));
                    break;
                case MARCADOR_MECANICA_BLOQUEADA:
                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_marcador_irlocais_rosa));
                    markerOptions.snippet(mensagemBloqueio);
                    break;
            }
            Marker marker = mapa.addMarker(markerOptions);
            hashMarcadores.put(nome, marker);
        }
    }


    /**
     * Adiciona os marcadores dos outros jogadores no mapa, adversarios ou copanheiros de equipe
     */
    public void adicionarMarcadoresOutrosPlayers() {
        Handler h = new Handler(getMainLooper());
        h.post(new Runnable() {
            @Override
            public void run() {
                if (Armazenamento.resgatarBoolean(Constantes.JOGO_EXECUTANDO, TelaPrincipalActivity.this) && InformacoesTemporarias.marcadoresDeJogadores != null) {
                    for (Grupo g : InformacoesTemporarias.grupos) {
                        if (g.getId() == InformacoesTemporarias.grupoAtual.getId()) {
                            InformacoesTemporarias.grupoAtual = g;
                        }
                        for (Jogador m : g.getJogadores()) {
                            if (m.getId() != InformacoesTemporarias.idJogador) {
                                adicionarMarcador(m.getNome(), m.getPosicao(), m.getTipo(), "" + m.getVida());
                            }
                        }
                    }
                }
            }
        });

    }

    private void configurarMapa() {
        //Criando objetos de manipulação dos mapas
        final MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapa = mapFragment.getMap();

        //Tipo de visualização dos mapas
        mapa.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mapa.setBuildingsEnabled(true);

        //Atualiza a posição da camera do mapa
        final CameraUpdate camera = CameraUpdateFactory.zoomTo(25);
        mapa.moveCamera(camera);

        mapa.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                moverCamera(Armazenamento.resgatarUltimaLocalizacao(TelaPrincipalActivity.this));
            }
        });

        mapa.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                String nome = marker.getTitle();
                Mecanica mecanica;
                mecanica = Mecanica.getMecanica(nome);

                try {
                    Log.e(Constantes.TAG, new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create().toJson(InformacoesTemporarias.grupoAtual));
                } catch (Exception e) {
                    Log.e(Constantes.TAG, "Ainda nao tem equipe selecionada");
                }
                if (mecanica == null && InformacoesTemporarias.grupoAtual != null && ((InformacoesTemporarias.grupoAtual.getTipoJogador() == Jogador.TIPO_CAPTURADOR) || (InformacoesTemporarias.grupoAtual.getTipoJogador() == Jogador.TIPO_HIBRIDO))) {
                    //Log.e(Constantes.TAG, "Sou capturador");

                    Jogador jogador = InformacoesTemporarias.getJogador(nome);
                    if (jogador != null) {
                        Location localizacaoJogador = MetodosUteis.latLngToLocation(LocationManager.GPS_PROVIDER, jogador.getPosicao());
                        if (Armazenamento.resgatarUltimaLocalizacao(TelaPrincipalActivity.this).distanceTo(localizacaoJogador) < Constantes.LIMIAR_DE_PROXIMIDADE) {
                            // Log.e(Constantes.TAG, "cliquei no jogador :" + jogador.getNome());
                            if (jogador.getGrupo().getTipoJogador() == Jogador.TIPO_CAPTURAVEL || jogador.getGrupo().getTipoJogador() == Jogador.TIPO_HIBRIDO) {
                                //Log.e(Constantes.TAG, "Jogador clicado é capturavel");
                                jogador.capturar(TelaPrincipalActivity.this);
                            } else {
                                //Log.e(Constantes.TAG, "Jogador clicado não é capturavel");
                                Toast.makeText(getApplicationContext(), R.string.nao_pode_capturar_jogador, Toast.LENGTH_LONG).show();
                            }
                        }
                    }

                }
                realizarMecanica(mecanica);
                return false;
            }
        });
    }

    private void realizarMecanica(Mecanica mecanica) {
        if (mecanica != null && !(mecanica.isRealizada())) {
            Location localizacaoJogador = Armazenamento.resgatarUltimaLocalizacao(TelaPrincipalActivity.this);
            Location localizacaoMecanica = new Location(LocationManager.GPS_PROVIDER);
            localizacaoMecanica.setLatitude(mecanica.getLocalizacao().latitude);
            localizacaoMecanica.setLongitude(mecanica.getLocalizacao().longitude);
            if (localizacaoJogador.distanceTo(localizacaoMecanica) < Constantes.LIMIAR_DE_PROXIMIDADE) {
                String tipoMecanica = mecanica.getTipoSimples();

                switch (tipoMecanica) {
                    case Constantes.TIPO_MECANICA_CVIDEOS:
                        mecanicaCVideosAtual = (CVideos) mecanica;
                        ((CVideos) mecanica).realizarMecanica(TelaPrincipalActivity.this);
                        break;
                    case Constantes.TIPO_MECANICA_VFOTOS:
                        ((Vfotos) mecanica).realizarMecanica(TelaPrincipalActivity.this);
                        break;
                    case Constantes.TIPO_MECANICA_VTEXTOS:
                        ((Vtextos) mecanica).realizarMecanica(TelaPrincipalActivity.this);
                        break;
                    case Constantes.TIPO_MECANICA_CFOTOS:
                        mecanicaCFotoAtual = (CFotos) mecanica;
                        ((CFotos) mecanica).realizarMecanica(TelaPrincipalActivity.this);
                        break;
                    case Constantes.TIPO_MECANICA_IRLOCAIS:
                        ((IrLocais) mecanica).realizarMecanica(TelaPrincipalActivity.this);
                        break;
                    case Constantes.TIPO_MECANICA_CSONS:
                        ((CSons) mecanica).realizarMecanica(TelaPrincipalActivity.this);
                        break;
                    case Constantes.TIPO_MECANICA_VSONS:
                        ((VSons) mecanica).realizarMecanica(TelaPrincipalActivity.this);
                        break;
                    case Constantes.TIPO_MECANICA_CTEXTOS:
                        ((CTextos) mecanica).realizarMecanica(TelaPrincipalActivity.this);
                        break;
                    case Constantes.TIPO_MECANICA_VVIDEOS:
                        ((VVideos) mecanica).realizarMecanica(TelaPrincipalActivity.this);
                        break;
                    case Constantes.TIPO_MECANICA_DFOTOS:
                        ((Deixar) mecanica).realizarMecanica(TelaPrincipalActivity.this);
                        break;
                    case Constantes.TIPO_MECANICA_DOBJETOS3D:
                        ((Deixar) mecanica).realizarMecanica(TelaPrincipalActivity.this);
                        break;
                    case Constantes.TIPO_MECANICA_DSONS:
                        ((Deixar) mecanica).realizarMecanica(TelaPrincipalActivity.this);
                        break;
                    case Constantes.TIPO_MECANICA_DTEXTOS:
                        ((Deixar) mecanica).realizarMecanica(TelaPrincipalActivity.this);
                        break;
                    case Constantes.TIPO_MECANICA_DVIDEOS:
                        ((Deixar) mecanica).realizarMecanica(TelaPrincipalActivity.this);
                        break;
                    case Constantes.TIPO_MECANICA_V_OBJ_3D:
                        ((VObj3d) mecanica).realizarMecanica(TelaPrincipalActivity.this);
                        break;
                    default:
                        Toast.makeText(getApplicationContext(), "Tipo da mecanica ainda náo implementada: " + mecanica.getTipoSimples(), Toast.LENGTH_LONG).show();
                }


            } else {
                Log.i(Constantes.TAG, "Ainda longe: Distancia: " + localizacaoJogador.distanceTo(localizacaoMecanica));
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tela_principal);

        configurarMapa();

        actionBar = getActionBar();

        mostrarProgressDialog(getString(R.string.ativando_gps));

        //Para que se possa mostrar coisas na tela principal de qualquer parte do sistema
        InformacoesTemporarias.contextoTelaPrincipal = this;

        // Se existe alguma mensagem enviada pela Notification, recebe aqui
        String msg = getIntent().getStringExtra("message");
        if (msg != null) {
            verificarMensagem();
        }

        //Ativa GPS
        ativarGps();

        Thread threadLocalizacao = new Thread(new AtualizarLocalizacaoJogadores(this));
        threadLocalizacao.start();

        instance = this;
    }

    /**
     * atualiza o life do jogador na action bar
     */
    public static void atualizarVida() {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if (InformacoesTemporarias.life != null) {
                    TabHostActivity.actionBar.setSubtitle("life: " + InformacoesTemporarias.life);
                } else {
                    TabHostActivity.actionBar.setSubtitle(null);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        invalidateOptionsMenu();


        //Elimina erros quando re-compilo o APK antes de fechar o antigo
        if (Armazenamento.resgatarBoolean(Constantes.JOGO_EXECUTANDO, this)) {
            if (InformacoesTemporarias.jogoAtual == null) {
                Armazenamento.salvar(Constantes.JOGO_EXECUTANDO, false, this);
                invalidateOptionsMenu();
            } else {
                if (!pediuMecanicas) {
                    Toast.makeText(getApplicationContext(), R.string.jogo_iniciado, Toast.LENGTH_SHORT).show();
                    SolicitarMissaoAtual solicitarMissaoAtual = new SolicitarMissaoAtual(this);
                    solicitarMissaoAtual.execute();
                    pediuMecanicas = true;
                }
            }
        }
    }

    private void mostrarProgressDialog(String mensagem) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setCancelable(false);
        }
        progressDialog.setMessage(mensagem);
        progressDialog.show();
    }

    private void esconderProgressDialog() {
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    private void ativarGps() {
        //Ativando o GPS
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 1, this);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, this);

        //Faz a inicialização do Sistema pela segunda vez  mais rápido
        if (Armazenamento.resgatarUltimaLocalizacao(this) != null) {
            onLocationChanged(Armazenamento.resgatarUltimaLocalizacao(this));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        boolean emJogo = Armazenamento.resgatarBoolean(Constantes.JOGO_EXECUTANDO, this);
        if (emJogo) {
            getMenuInflater().inflate(R.menu.menu_tela_principal_durante_jogo, menu);
        } else {
            getMenuInflater().inflate(R.menu.menu_tela_principal, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.menuNovoJogo) {
            Intent intent = new Intent(TelaPrincipalActivity.this, TiposDeJogosDisponiveisActivity.class);
            startActivity(intent);
            return true;
        }

        if (id == R.id.menuChat) {
            Intent intent = new Intent(TelaPrincipalActivity.this, ChatActivity.class);
            startActivity(intent);
            return true;
        }

        if (id == R.id.sairDoJogo) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.deseja_sair);
            builder.setNegativeButton(R.string.cancelar, null);
            builder.setPositiveButton(R.string.sair_do_jogo, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    terminarJogo();
                }
            });
            builder.create().show();
        }

        if (id == R.id.menuMissoes) {
            try {
                if (InformacoesTemporarias.mecanicasAtuais != null || InformacoesTemporarias.mecanicasAtuais.size() <= 0) {
                    final ListarMecanicasAdapter adapter = new ListarMecanicasAdapter(this, R.layout.mecanicas_item_lista, InformacoesTemporarias.mecanicasAtuais);
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle(R.string.missoes)
                            .setNegativeButton(R.string.OK, null)
                            .setAdapter(adapter, null)
                            .create().show();
                } else {
                    Toast.makeText(this, R.string.sem_missoes, Toast.LENGTH_SHORT).show();
                }
            } catch (Exception ex) {
                Toast.makeText(this, R.string.sem_missoes, Toast.LENGTH_SHORT).show();
            }
        }


        return super.onOptionsItemSelected(item);
    }

    public void terminarJogo() {
        mapa.clear();
        marcadorIrLocal = null;
        marcadorJogador = null;
        hashMarcadores.clear();
        adicionarMarcadorJogador(Armazenamento.resgatarUltimaLocalizacao(this));
        Armazenamento.salvar(Constantes.JOGO_EXECUTANDO, false, TelaPrincipalActivity.this);
        InformacoesTemporarias.life = null;
        invalidateOptionsMenu();
        InformacoesTemporarias.mecanicasAtuais = null;
        pediuMecanicas = false;
        InformacoesTemporarias.inventario.clear();
    }

    //Mover camera
    private void moverCamera(Location location) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(latLng)
                .zoom(25)
                .bearing(mapa.getCameraPosition().bearing)
                .tilt(70)
                .build();
        mapa.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

    }

    //Adicinar marcador do jogador
    private void adicionarMarcadorJogador(Location location) {
        //Se o marcador do jogador ainda não foi colocado
        if (marcadorJogador == null) {
            if (opcoesDeMarcador == null) {
                opcoesDeMarcador = new MarkerOptions();
            }
            opcoesDeMarcador.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_jogador))
                    .title(getString(R.string.eu))
                    .snippet(InformacoesTemporarias.nomeJogador)
                    .position(new LatLng(location.getLatitude(), location.getLongitude()));

            marcadorJogador = mapa.addMarker(opcoesDeMarcador);
            moverCamera(location);
        } else {
            //Se o marcador já existe, ele apenas atualiza a posição
            marcadorJogador.setPosition(new LatLng(location.getLatitude(), location.getLongitude()));
        }
    }

    /**
     * Verifica de alguma mensagem foi enviada ao jogador via GCM
     */
    public void verificarMensagem() {
        if (InformacoesTemporarias.mensagem != null) {
            Handler h = new Handler(getMainLooper());
            h.post(new Runnable() {
                @Override
                public void run() {
                    AlertDialog.Builder builder = new AlertDialog.Builder(TelaPrincipalActivity.this);
                    builder.setTitle(R.string.app_name)
                            .setMessage(InformacoesTemporarias.mensagem)
                            .setNeutralButton(R.string.OK, null)
                            .create().show();
                    InformacoesTemporarias.mensagem = null;
                }
            });

        }
    }

    /**
     * Testa de a condição atual do jogador satisfaz alguma mecanica
     */
    public void mostrarMecanicas() {
        if (InformacoesTemporarias.mecanicasAtuais != null) {
            List<Mecanica> mecanicasAtuais = InformacoesTemporarias.mecanicasAtuais;
            for (Mecanica m : mecanicasAtuais) {
                Marker marker = hashMarcadores.get(m.getNome());
                if (marker != null) {
                    marker.remove();
                }
                if (m.isVisivel() && m.isMostrar()) {
                    if (m.isRealizada()) {
                        try {
                            hashMarcadores.get(m.getNome()).remove();
                            hashMarcadores.remove(m.getNome());
                        } catch (Exception e) {

                        }
                        adicionarMarcador(m.getNome(), m.getLocalizacao(), MARCADOR_MECANICA_REALIZADA, null);
                    } else if (m.getEstado() == 2) {
                        try {
                            hashMarcadores.get(m.getNome()).remove();
                            hashMarcadores.remove(m.getNome());
                        } catch (Exception e) {

                        }
                        adicionarMarcador(m.getNome(), m.getLocalizacao(), MARCADOR_MECANICA_BLOQUEADA, m.getMsgbloqueio());
                    } else if ((m.getEstado() == 0) || (m.getEstado() == 1)) {
                        try {
                            hashMarcadores.get(m.getNome()).remove();
                            hashMarcadores.remove(m.getNome());
                        } catch (Exception e) {

                        }
                        adicionarMarcador(m.getNome(), m.getLocalizacao(), MARCADOR_LOCAL, null);
                    }
                }
            }
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        esconderProgressDialog();
        mostrarMecanicas();
        Armazenamento.salvarLocalizacao(location, this);
        adicionarMarcadorJogador(location);
        verificarMensagem();
        adicionarMarcadoresOutrosPlayers();
        verificarMecanicasEscondidas();
        if(visualizadorDeRA!=null){
            visualizadorDeRA.onLocationChanged(location);
        }
    }

    private void verificarMecanicasEscondidas() {
        if (InformacoesTemporarias.mecanicasAtuais != null) {
            List<Mecanica> mecanicas = InformacoesTemporarias.mecanicasAtuais;
            for (Mecanica mecanica : mecanicas) {
                if (mecanica.getVisivel() == Mecanica.VISIVEL_NUNCA || mecanica.getVisivel() == Mecanica.VISIVEL_NUNCA2) {
                    realizarMecanica(mecanica);
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        locationManager.removeUpdates(this);
        terminarJogo();
        super.onDestroy();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

        if (provider.equals(LocationManager.GPS_PROVIDER)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.gps_desativado).setMessage(getString(R.string.app_name) + " " + getString(R.string.nao_funciona_sem_gps));
            builder.setNegativeButton(R.string.sair_do_jogo
                    , new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            builder.setPositiveButton("Ativar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                }
            });
            builder.setCancelable(true).create().show();

        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onBackPressed() {
        voltar();
    }

    public void voltar() {
        if (Armazenamento.resgatarBoolean(Constantes.JOGO_EXECUTANDO, this)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.app_name);
            builder.setMessage(R.string.deseja_sair);
            builder.setPositiveButton(R.string.sair_do_jogo, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    terminarJogo();
                    finish();
                }
            });
            builder.setNegativeButton(R.string.cancelar, null);
            builder.create().show();
        } else {
            finish();
        }
    }

    private static TelaPrincipalActivity instance = null;

    public static TelaPrincipalActivity getInstance() {
        return instance;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e("TAG", "Executou onActivityResult");
        if (resultCode == Activity.RESULT_OK) {

            if (requestCode == REQUEST_CODE_FOTO) {
                Log.i(Constantes.TAG, "Caminho da imagem: " + CFotos.pathDeImagem);
                File foto = new File(CFotos.pathDeImagem);
                UploadDeArquivo.enviarFoto(this, foto, mecanicaCFotoAtual);
                mecanicaCFotoAtual = null;
            } else if (requestCode == REQUEST_CODE_VIDEO) {
                Log.i(Constantes.TAG, "Caminho do Video: ");
                Uri fileUri = data.getData();
                File file = new File(getRealPathFromURI(fileUri));
                UploadDeArquivo.enviarVideo(this, file, mecanicaCVideosAtual);
                mecanicaCVideosAtual = null;
            } else if (requestCode == Deixar.REQUEST_CODE) {
                Bundle extras = data.getExtras();
                int mecSimplesId = extras.getInt(InventarioActivity.ITEM_ID);
                String tipo = extras.getString(InventarioActivity.ITEM_TIPO);
                String arquivo = extras.getString(InventarioActivity.ITEM_ARQUIVO);
                mecanicaDeixarAtual.confirmarRealizacao(this, arquivo, tipo, mecSimplesId);
                mecanicaDeixarAtual = null;
            } else if (requestCode == REQUEST_CODE_VER_OBJ_3D) {
                mecanicaVObj3dAtual.confirmarRealizacao(TelaPrincipalActivity.this, null, null, null);
                mecanicaVObj3dAtual = null;
            }
        } else if (requestCode == Vfotos.REQUEST_CODE_VER_IMAGEM) {
            Toast.makeText(this, "Viu a imagem", Toast.LENGTH_SHORT).show();
        } else if (requestCode == REQUEST_CODE_VER_VIDEO) {
            mecanicaVVideosAtual.confirmarRealizacao(TelaPrincipalActivity.this, null, null, null);
            mecanicaVVideosAtual = null;
        }
    }

    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Video.VideoColumns.DATA);
        return cursor.getString(idx);
    }

    public void transicaoMarcador(final String nome) {
        final float[] markerAlpha = new float[1];

        for (int i = 0; i < 5; i++) {
            new Handler(getMainLooper()).
                    post(new Runnable() {
                        @Override
                        public void run() {

                            final Marker marcador = hashMarcadores.get(nome);
                            if (marcador != null) {
                                Location location = new Location(LocationManager.GPS_PROVIDER);
                                location.setLatitude(marcador.getPosition().latitude);
                                location.setLongitude(marcador.getPosition().longitude);
                                moverCamera(location);
                                markerAlpha[0] = marcador.getAlpha();
                                marcador.setAlpha(marcador.getAlpha() / 1.5f);
                            }

                        }
                    });

            try {
                Thread.sleep(250);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        SolicitarMissaoAtual solicitarMissaoAtual = new SolicitarMissaoAtual(InformacoesTemporarias.contextoTelaPrincipal);
        solicitarMissaoAtual.execute();
    }

    public void diminuirOpacidadeMarcador(final String nome) {
        new Handler(getMainLooper()).post(new Runnable() {
            @Override
            public void run() {

                final Marker marcador = hashMarcadores.get(nome);
                Location location = new Location(LocationManager.GPS_PROVIDER);
                location.setLatitude(marcador.getPosition().latitude);
                location.setLongitude(marcador.getPosition().longitude);
                moverCamera(location);
                marcador.setAlpha(marcador.getAlpha() / 2f);

            }
        });
    }
}
