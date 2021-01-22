package edu.ib.zpo_l14_zaj_color;

import android.app.Activity;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.util.Random;

public class MainActivity extends Activity implements SensorEventListener {
    private SensorManager sensorManager;
    private boolean isColor = false; // zmienna logiczna
    private View view;              // stworzenie widoku
    private long lastUpdate;        // zmienna czasu pomiaru

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        view = findViewById(R.id.textView); // wykorzystanie textView
        view.setBackgroundColor(Color.RED); // pierwszy kolor czerwony

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE); // chęć korzystania z czujników sprzętowych
        lastUpdate = System.currentTimeMillis(); // ustalenie czasu pomiaru ms
        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        // korzystanie z akcelerometru - przyspieszenie telefonu, wraz ze składową przyspieszenia ziemskiego
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    // reakcją na poruszenie telefonu jest wywołanie metody zmiany koloru tła
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            changeColorOfBackground(event);
        }
    }

    private void changeColorOfBackground(SensorEvent event) {
        Random rnd = new Random(); // zwraca randomowe wartości
        int color; // kolor tła

        // wartości zmiany położenia
        float[] values = event.values;
        float ax = values[0];
        float ay = values[1];
        float az = values[2];

        double accelerateSquareRoot = (Math.pow(ax, 2) + Math.pow(ay, 2) + Math.pow(az, 2))
                / (Math.pow(SensorManager.GRAVITY_EARTH, 2)); // zmniejszenie wartości w celu usprawnienia doboru wartości zmiany rekacji na wstrząśnięcie
        long actualTime = System.currentTimeMillis();; // zwracanie aktualnego czasu pomiaru ms

        if (accelerateSquareRoot >= 2) {// reakcja na lekkie wstrząśnięcie

            if (actualTime - lastUpdate < 200) { // zmiany mogą być rejestrowane co 200 ms
                return;
            }
            lastUpdate = actualTime; // nadpisanie czasu ostatniego wstrząśnięcia

            if (isColor) {
                // losowanie koloru tła
                color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
                view.setBackgroundColor(color);

            } else {
                color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
                view.setBackgroundColor(color);
            }
            isColor = !isColor;
        }
    }

    // metody decydujące o tym, że nie sprawdzamy czujników, kiedy aplikacja nie jest użytkowana
    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_GAME); // zczytujemy wskazania akcelerometru
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this); // stopujemy wskazania
    }
}