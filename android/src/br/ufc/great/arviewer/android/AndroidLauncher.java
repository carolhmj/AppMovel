package br.ufc.great.arviewer.android;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

import java.io.File;

import br.ufc.great.arviewer.ARViewer;
import br.ufc.great.arviewer.Resultado;

public class AndroidLauncher extends AndroidApplication implements LocationListener {

    private String TAG = "ARViewer";

    private Camera mCamera;
    private CameraPreview mCameraPreview;
    private View libgdxView;
    double lat_obj = -3.745896;
    double lon_obj = -38.574405;
    private ARViewer arViewer;
    ProgressDialog progressDialog;
    LocationManager locationManager;

    public Resultado clicado = new Resultado(false);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Intent intent = getIntent();
        String nome_objeto = intent.getExtras().getString("NOME_OBJETO");
        String nome_textura = intent.getExtras().getString("NOME_TEXTURA");
        lat_obj = intent.getExtras().getDouble("LAT_OBJETO");
        lon_obj = intent.getExtras().getDouble("LON_OBJETO");


        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Aguarde enquando o GPS atualiza a posicao...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        AndroidApplicationConfiguration cfg = new AndroidApplicationConfiguration();
        cfg.r = 8;
        cfg.g = 8;
        cfg.b = 8;
        cfg.a = 8;


        File arquivo = new File(Environment.getExternalStorageDirectory() + "/GreatPervasiveGame/" + nome_objeto);

        if (arquivo.exists()) {
            Log.e("File", "Existe");
        }

        arViewer = new ARViewer(arquivo, nome_objeto, nome_textura, new Thread(new Runnable() {
            @Override
            public void run() {
                setResult(RESULT_OK);
                Log.e("TAG","Executou a Thread");
                finish();
            }
        }));

        libgdxView = initializeForView(arViewer, cfg);

        if (libgdxView instanceof SurfaceView) {
            SurfaceView glView = (SurfaceView) libgdxView;
            // force alpha channel - I'm not sure we need this as the GL surface is already using alpha channel
            glView.getHolder().setFormat(PixelFormat.TRANSLUCENT);
        }

        FrameLayout frame = (FrameLayout) findViewById(R.id.camera);
        frame.addView(libgdxView);

        if (checkCameraHardware(getApplicationContext())) {
            mCamera = getCameraInstance();
            mCameraPreview = new CameraPreview(this, mCamera);
            frame.addView(mCameraPreview);
        } else {
            finish();
        }

        AcelerometerListener acelerometerListener = new AcelerometerListener(this, arViewer);
        acelerometerListener.startMonitoring();

        GiroscopeListener giroscopeListener = new GiroscopeListener(this, arViewer);
        giroscopeListener.startMonitoring();

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, this);

    }


    public Location newLocation(double lat, double lon) {
        Location l = new Location(LocationManager.GPS_PROVIDER);
        l.setLatitude(lat);
        l.setLongitude(lon);
        return l;
    }

    /**
     * Check if this device has a camera
     */
    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

    private static Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
        } catch (Exception e) {
            // Camera is not available (in use or does not exist)
            e.printStackTrace();
        }
        return c; // returns null if camera is unavailable
    }

    public void onLocationChanged(Location location) {

        progressDialog.dismiss();

        Log.e(TAG, "latitude: " + location.getLatitude()
                + " , longitude: " + location.getLongitude() + " , altitude: "
                + location.getAltitude());

        float[] dist = new float[1];

        double lat = location.getLatitude();
        double lon = location.getLongitude();

        Location.distanceBetween(lat, lon, lat_obj, lon_obj, dist);
        float distance = dist[0];
        lat = lat_obj - lat;
        lon = lon_obj - lon;
        float xcoord = (float) (lat / 0.000009);
        float zcoord = (float) (lon / 0.000009);


        arViewer.setCamCoord(xcoord, zcoord, location.getAltitude());
        Log.e(TAG, "distance: " + dist[0] + " coordenadas " + xcoord + ", " + zcoord);
        //Log.e(TAG, "x,y: " + xcoord + " , " + ycoord);

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    @Override
    protected void onPause() {
        Log.e("TAG", "Executou onpause");
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.e("TAG", "Executou onStop");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("TAG", "Executou ondestroy");
        locationManager.removeUpdates(this);
        mCamera.stopPreview();
        mCamera.release();
    }
}
