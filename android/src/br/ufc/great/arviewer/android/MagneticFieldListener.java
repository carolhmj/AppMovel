package br.ufc.great.arviewer.android;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.widget.Toast;

import br.ufc.great.arviewer.ARViewer;

/**
 * Created by great on 27/04/16.
 * @author Messias Lima
 */
public class MagneticFieldListener implements SensorEventListener {

    SensorManager sensorManager;
    Sensor sensor;
    ARViewer arViewer;

    public MagneticFieldListener(Context context, ARViewer arViewer){
        this.arViewer = arViewer;
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        if (sensor == null) {
            Toast.makeText(context.getApplicationContext(), "Sensor campo magnetico nao disponivel", Toast.LENGTH_LONG).show();
        }
    }

    public void startMonitoring() {
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
    //    arViewer.setMagneticFieldValues(sensorEvent.values);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
