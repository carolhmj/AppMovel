package br.ufc.great.arviewer.android;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.widget.Toast;

import br.ufc.great.arviewer.ARViewer;

/**
 * Created by messiaslima on 26/06/2015.
 * @author messiaslima
 */
public class AcelerometerListener implements SensorEventListener {

    SensorManager sensorManager;
    Sensor sensor;
    ARViewer arViewer;

    public AcelerometerListener(Context context, ARViewer arViewer) {
        this.arViewer = arViewer;
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (sensor == null) {
            Toast.makeText(context.getApplicationContext(), "Sensor acelerômetro nao disponivel", Toast.LENGTH_LONG).show();
        }
    }

    public void startMonitoring() {
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        arViewer.setAccelerometerValues(sensorEvent.values);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
