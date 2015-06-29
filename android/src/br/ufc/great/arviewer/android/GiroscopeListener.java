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
 */
public class GiroscopeListener implements SensorEventListener {


    SensorManager sensorManager;
    Sensor sensor;
    ARViewer arViewer;

    public GiroscopeListener(Context context, ARViewer arViewer) {
        this.arViewer = arViewer;
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        if (sensor != null) {
            Toast.makeText(context.getApplicationContext(), "Sensor " + sensor.getName() + " disponivel", Toast.LENGTH_LONG).show();

        } else {
            Toast.makeText(context.getApplicationContext(), "Sensor nao disponivel", Toast.LENGTH_LONG).show();
        }
    }
    public void startMonitoring(){
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
    }
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        arViewer.setGiroscopeValues(sensorEvent.values);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
