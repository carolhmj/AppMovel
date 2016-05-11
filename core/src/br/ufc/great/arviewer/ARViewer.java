package br.ufc.great.arviewer;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.loader.G3dModelLoader;
import com.badlogic.gdx.graphics.g3d.loader.ObjLoader;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.TextureProvider;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.UBJsonReader;

import java.io.File;


public class ARViewer extends InputAdapter implements ApplicationListener {
    public Environment environment;
    public PerspectiveCamera cam;
    public CameraInputController camController;
    public ModelBatch modelBatch;
    public Model model;
    public ModelInstance instance;
    public AssetManager assets;
    public Array<GameObject> instances = new Array<GameObject>();
    float azimuth;
    float pitch;
    float roll;
    boolean init = true;
    float mina, maxa, minp, maxp, minr, maxr;
    boolean loading;
    float alpha = 0.5f;
    float xcam, zcam;
    double altcam;
    float coordAlpha = 2f;

    boolean readyToShowObjects = false;

    float acelerometerX, acelerometerY, acelerometerZ;
    float giroscopeX = 0, giroscopeY = 0, giroscopeZ = 0;

    private int selected = -1, selecting = -1;

    //String PASTA_DE_ARQUIVOS = Gdx.files.getExternalStoragePath() + "/GreatPervasiveGame/";

    public static final int TYPE_ANIMATION = 1;
    public static final int TYPE_STATIC_OBJECT = 0;


    File objeto;

    private int tipo;
    ObjLoader objLoader;
    G3dModelLoader g3dModelLoader;
    ModelInstance modelInstance;
    FileHandle fileHandle;

    String nomeDoObjeto;
    String nomeDaTextura;

    Thread setResultOK;

    public ARViewer(File file, String nomeDoObjeto, String nomeDaTextura, Thread thread, int tipo) {
        this.objeto = file;
        this.nomeDoObjeto = nomeDoObjeto;
        this.nomeDaTextura = nomeDaTextura;
        this.setResultOK = thread;
        this.tipo = tipo;
    }

    @Override
    public void create() {

        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));

        modelBatch = new ModelBatch();

        cam = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        cam.position.set(0, 0, 0); //Position : posicao da camera no plano
        cam.lookAt(0, 0, 0);
        cam.near = 0.001f;
        cam.far = 300f;
        cam.update();

        camController = new CameraInputController(cam);
        Gdx.input.setInputProcessor(new InputMultiplexer(this, camController));

        assets = new AssetManager();
        //assets.load("ship.obj", Model.class);

        fileHandle = new FileHandle(objeto);
        boolean x = fileHandle.exists();

        Gdx.app.error("Arquivo existe", "existe: " + x);

        loading = true;
        mina = minp = minr = 999;
        maxa = maxp = maxr = -999;

        if (tipo == TYPE_ANIMATION) {
            UBJsonReader ubJsonReader = new UBJsonReader();
            g3dModelLoader = new G3dModelLoader(ubJsonReader);
        } else {
            objLoader = new ObjLoader();
        }

    }

    GameObject gameObject;

    private void doneLoading() {

        FileTextureProviderExterna textureProvider = new FileTextureProviderExterna();
        textureProvider.load("GreatPervasiveGame/" + nomeDaTextura);


        Model model;

        Gdx.app.error("Nome do Arquivo", Gdx.files.external("GreatPervasiveGame/" + nomeDoObjeto).path());

        if (tipo == TYPE_ANIMATION) {
            //nomeDoObjeto = "teste3.g3db";
            model = g3dModelLoader.loadModel(Gdx.files.external("GreatPervasiveGame/" + nomeDoObjeto));
            modelInstance = new ModelInstance(model);
        } else {
            model = objLoader.loadModel(Gdx.files.external("GreatPervasiveGame/" + nomeDoObjeto), textureProvider);
        }


        Gdx.app.error("debug", Gdx.files.external("GreatPervasiveGame/" + nomeDoObjeto).path());

        String id = model.nodes.get(0).id;

        gameObject = new GameObject(model, id, true);

        instances.add(gameObject);

        loading = false;
    }

    float i = 0;
    float distanciaEntreOJogadorEOObjeto;

    @Override
    public void render() {

        i += 0.01;

        if (loading && assets.update()) {
            doneLoading();
        }

        camController.update();

        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        modelBatch.begin(cam);
        modelBatch.render(instances, environment);
        modelBatch.end();


        float devAzimuth = Gdx.input.getAzimuth();
        float devPitch = Gdx.input.getPitch();
        float devRoll = Gdx.input.getRoll();

//        cam.up.x = 0;//azimuth
//        cam.up.y = 1;
//        cam.up.z = 0;

        azimuth = azimuth * alpha + (1 - alpha) * devAzimuth;
        pitch = pitch * alpha + (1 - alpha) * devPitch;
        roll = roll * alpha + (1 - alpha) * devRoll;

//      Vector3 look = new Vector3(0, 0, i);
//      cam.lookAt(look);

        cam.position.set(0, 0, distanciaEntreOJogadorEOObjeto);// O terceiro parametro é a distancia entre um ponto e outro

        cam.update();


    }


    public void setDistanciaEntreOJogadorEOObjeto(float distanciaEntreOJogadorEOObjeto){

        Gdx.app.error("Distancia de objeto", "Distancia recebida " + distanciaEntreOJogadorEOObjeto );

        this.distanciaEntreOJogadorEOObjeto = distanciaEntreOJogadorEOObjeto;
    }

    public void drawLineOnLogCat() {
        Gdx.app.error("ARViewer", "________________________________________________________");
    }

    public void setCamCoord(float x, float y, double alt) {

        drawLineOnLogCat();
        Gdx.app.error("Localizacao Recebida", "latitude: " + x + " Longitude: " + y + " Altura: " + alt);
        drawLineOnLogCat();

        xcam = x;
        zcam = y;
        altcam = alt;

        if (!readyToShowObjects) {
            readyToShowObjects = true;
        }
    }


    public void setAccelerometerValues(float[] values) {
        acelerometerX = values[0];
        acelerometerY = values[1];
        acelerometerZ = values[2];

    }


    private static final float NS2S = 1.0f / 1000000000.0f;
    private final float[] deltaRotationVector = new float[4];
    private float timestamp;
    float EPSILON = 0;
    float[] historicValues = new float[3];

    private float[] lowPassFilter(float[] newValues) {

        historicValues[0] = historicValues[0] * alpha + (1 - alpha) * newValues[0];
        historicValues[1] = historicValues[1] * alpha + (1 - alpha) * newValues[1];
        historicValues[2] = historicValues[2] * alpha + (1 - alpha) * newValues[2];

        return historicValues;
    }


    public void setGiroscopeValues(float[] values, float eventTimestamp) {

        values = lowPassFilter(values);

        if (timestamp != 0) {
            final float dT = (eventTimestamp - timestamp) * NS2S;
            // Axis of the rotation sample, not normalized yet.
            float axisX = values[0];
            float axisY = values[1];
            float axisZ = values[2];

            // Calculate the angular speed of the sample
            float omegaMagnitude = (float) Math.sqrt(axisX * axisX + axisY * axisY + axisZ * axisZ);

            // Normalize the rotation vector if it's big enough to get the axis
            // (that is, EPSILON should represent your maximum allowable margin of error)
            if (omegaMagnitude > EPSILON) {
                axisX /= omegaMagnitude;
                axisY /= omegaMagnitude;
                axisZ /= omegaMagnitude;
            }

            // Integrate around this axis with the angular speed by the timestep
            // in order to get a delta rotation from this sample over the timestep
            // We will convert this axis-angle representation of the delta rotation
            // into a quaternion before turning it into the rotation matrix.
            float thetaOverTwo = omegaMagnitude * dT / 2.0f;
            float sinThetaOverTwo = (float) Math.sin(thetaOverTwo);
            float cosThetaOverTwo = (float) Math.cos(thetaOverTwo);
            deltaRotationVector[0] = (sinThetaOverTwo * axisY) * (-1);
            deltaRotationVector[1] = (sinThetaOverTwo * axisX);
            deltaRotationVector[2] = sinThetaOverTwo * axisZ;
            deltaRotationVector[3] = cosThetaOverTwo;

            // Quaternion quaternionObject = new Quaternion(0, 0, deltaRotationVector[2],0);

            Quaternion quaternionCam = new Quaternion(deltaRotationVector[0], deltaRotationVector[1], 0, deltaRotationVector[3]);


            if (gameObject != null) {
                gameObject.transform.rotate(0, 0, 1, axisZ * (-10));
            }

            cam.rotate(quaternionCam);

        }

        timestamp = eventTimestamp;

//        giroscopeX = giroscopeX + truncateValue(values[0]);
//        giroscopeY = giroscopeY + truncateValue(values[1]);
//        giroscopeZ = giroscopeZ + truncateValue(values[2]);
//        if (truncateValue(values[0]) > 0.0) {
//            drawLineOnLogCat();
//            Gdx.app.error("giroscopio valores", "X: " + truncateValue(values[0]) + " Y: " + truncateValue(values[1]) + " Z: " + truncateValue(values[2]));
//            Gdx.app.error("testObjeto valores", "X: " + giroscopeX + " Y: " + giroscopeY + " Z: " + giroscopeZ);
//            drawLineOnLogCat();
//        }
    }


    private float truncateValue(float value) {
        int aux = (int) (value * 10);
        return aux / 10f;
    }

    @Override
    public void dispose() {
        modelBatch.dispose();
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    public int getObject(int screenX, int screenY) {

        Ray ray = cam.getPickRay(screenX, screenY);
        int result = -1;
        float distance = -1;
        for (int i = 0; i < instances.size; ++i) {
            final GameObject instance = instances.get(i);
            Vector3 position = new Vector3();
            instance.transform.getTranslation(position);
            position.add(instance.center);
            float dist2 = ray.origin.dst2(position);
            if (distance >= 0f && dist2 > distance) continue;
            if (Intersector.intersectRaySphere(ray, position, instance.radius, null)) {
                result = i;
                distance = dist2;
            }
        }
        return result;
    }

    //evento chamado quando toca a tela
    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        Gdx.app.error("colisao1", "objeto " + selecting);
        selecting = getObject(screenX, screenY);
        Gdx.app.error("colisao1", "objeto " + selecting);
        return selecting >= 0;
    }


    //evento chamado quando toca a tela
    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (selecting >= 0) {
            if (selecting == getObject(screenX, screenY)) {
                setResultOK.start();
                Gdx.app.error("colisao3", "objeto " + selecting);
            }
            selecting = -1;
            return true;
        }

        Gdx.app.error("colisao4", "objeto " + selecting);
        return false;
    }
}

