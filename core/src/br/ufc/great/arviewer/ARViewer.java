package br.ufc.great.arviewer;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

import java.text.DecimalFormat;

public class ARViewer implements ApplicationListener {
    public Environment environment;
    public PerspectiveCamera cam;
    //    public CameraInputController camController;
    public ModelBatch modelBatch;
    public Model model;
    public ModelInstance instance;
    public AssetManager assets;
    public Array<ModelInstance> instances = new Array<ModelInstance>();

    float azimuth;
    float pitch;
    float roll;
    boolean init = true;
    float mina, maxa, minp, maxp, minr, maxr;
    boolean loading;
    float alpha = 0.01f;
    float xcam, ycam;
    float coordAlpha = 2f;


    @Override
    public void create() {
        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));

        modelBatch = new ModelBatch();

        cam = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
//      cam.position.set(3f, 0, 0);
        cam.position.set(2f, 0, 0); //Position : posição da camera no plano
        cam.lookAt(0, 0, 0);
        cam.near = 0.001f;
        cam.far = 300f;
        cam.update();

        assets = new AssetManager();
        assets.load("ship.obj", Model.class);
        loading = true;
//        ModelBuilder modelBuilder = new ModelBuilder();
//        model = modelBuilder.createBox(5f, 5f, 5f,
//                new Material(ColorAttribute.createDiffuse(Color.GREEN)),
//                Usage.Position | Usage.Normal);
//        instance = new ModelInstance(model);

//        camController = new CameraInputController(cam);
//        Gdx.input.setInputProcessor(camController);
        mina = minp = minr = 999;
        maxa = maxp = maxr = -999;
    }


    private void doneLoading() {
        Model ship = assets.get("ship.obj", Model.class);
        ModelInstance shipInstance = new ModelInstance(ship);
        instances.add(shipInstance);
        loading = false;
    }

    @Override
    public void render() {

//
// camController.update();
        if (loading && assets.update())
            doneLoading();

        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        modelBatch.begin(cam);
        modelBatch.render(instances, environment);
        modelBatch.end();


        float devAzimuth = Gdx.input.getAzimuth();
        float devPitch = Gdx.input.getPitch();
        float devRoll = Gdx.input.getRoll();

//        if (devAzimuth > maxa) maxa = devAzimuth;
//        if (devPitch > maxp) maxp = devPitch;
//        if (devRoll > maxr) maxr = devRoll;
//
//        if (devAzimuth < mina) mina = devAzimuth;
//        if (devPitch < minp) minp = devPitch;
//        if (devRoll < minr) minr = devRoll;

//        if (init) {
//            Gdx.app.error("init", " apr ");
//            azimuth = devAzimuth;
//            pitch = devPitch;
//            roll = devRoll;
//            init = false;
//        }

//        float devAzimuth = 0;
//        float devPitch = 0;
//        float devRoll = 0;


//        devAzimuth = azimuth + 0.01f * (azimuth - devAzimuth);
//        devPitch = pitch + 0.01f * (pitch - devPitch);
//        devRoll = roll + 0.01f * (roll - devRoll);

//        if (Gdx.input.justTouched()) {
//            //init = true;
//            Gdx.app.error("azimuth - pitch - roll: ", String.valueOf(azimuth) + " | " + String.valueOf(pitch) + " | " + String.valueOf(roll));
//            Gdx.app.error("maxa", String.valueOf(maxa));
//            Gdx.app.error("mina", String.valueOf(mina));
//            Gdx.app.error("maxp", String.valueOf(maxp));
//            Gdx.app.error("minp", String.valueOf(minp));
//            Gdx.app.error("maxr", String.valueOf(maxr));
//            Gdx.app.error("minr", String.valueOf(minr));
//            cam.position.set(3, 0, -3);
//            init = false;
//        }

//        cam.direction.x = -1;
//       cam.direction.y = 0;
//        cam.direction.z = 0;
//
        cam.up.x = 0;//azimuth
        cam.up.y = 1;
        cam.up.z = 0;

//        azimuth = devAzimuth;
//        pitch = devPitch;
//        roll = devRoll;

        azimuth = azimuth + alpha * (devAzimuth - azimuth);
        pitch = pitch + alpha * (devPitch - pitch);
        roll = roll + alpha * (devRoll - roll);

        //1 proprio eixo(azimuth), 2 cima(pitch) , 3 esq->direira (roll)
//        Gdx.app.error("azimuth - roll - pitch", String.valueOf(azimuth) + " - " + String.valueOf(roll) + " - " + String.valueOf(pitch));
        //cam.rotate(-devAzimuth, 0, 0, 1);
        //cam.rotate(devPitch*2f, 0, 1, 0);
        //cam.rotate(devRoll, 0, 1, 0);//roll

        //Vector3 look = new Vector3(pitch, azimuth, (roll - 90));


        Vector3 look = new Vector3(-giroscopeY, giroscopeX, 0);

        cam.position.set(0, 0, 3);// localizacao

       // cam.position.rotate(60, -acelerometerX, -acelerometerY, 0);

        Gdx.app.error("Arviewer", "x,y: " + xcam / coordAlpha + " , " + ycam / coordAlpha);

        cam.lookAt(look);
        cam.update();

//        if (Gdx.input.justTouched())
//            cam.lookAt(0, 0, 0);

    }

    public void setCamCoord(float x, float y) {
        xcam = x;
        ycam = y;
    }

    float acelerometerX, acelerometerY, acelerometerZ;
    float giroscopeX = 0, giroscopeY = 0, giroscopeZ = 0;

    public void setAccelerometerValues(float[] values) {
        acelerometerX = values[0];
        acelerometerY = values[1];
        acelerometerZ = values[2];
    }

    public void setGiroscopeValues(float[] values) {


        giroscopeX = giroscopeX + truncateValue(values[0]);
        giroscopeY = giroscopeY + truncateValue(values[1]);
        giroscopeZ = giroscopeZ + truncateValue(values[2]);
        Gdx.app.error("giroscopio valores", "X: " + truncateValue(values[0]) + " Y: " + truncateValue(values[1]) + " Z: " + truncateValue(values[2]));
        Gdx.app.error("Objeto valores", "X: " + giroscopeX + " Y: " + giroscopeY + " Z: " + giroscopeZ);
    }


    private float truncateValue(float value){
        int aux = (int)(value*10);
        return aux/10f;
    }

    @Override
    public void dispose() {
        modelBatch.dispose();
//        model.dispose();
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
}
