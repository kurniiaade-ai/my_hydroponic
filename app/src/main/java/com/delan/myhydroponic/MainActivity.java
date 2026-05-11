package com.delan.myhydroponic;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class MainActivity extends AppCompatActivity {

    TextView txtTemp, txtLocation, txtTime;
    ImageButton btnLogout;
    ImageView imgWeather;

    FirebaseAuth mAuth;

    // Handler auto refresh
    Handler handler = new Handler();

    // Koordinat lokasi hidroponik
    double lat = -6.2880807;
    double lon = 106.8649933;

    // Runnable update cuaca
    Runnable weatherRunnable = new Runnable() {
        @Override
        public void run() {

            getWeatherData(lat, lon);

            // Update setiap 5 menit
            handler.postDelayed(this, 300000);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inisialisasi komponen
        txtTemp = findViewById(R.id.txtTemp);
        txtLocation = findViewById(R.id.txtLocation);
        txtTime = findViewById(R.id.txtTime);
        btnLogout = findViewById(R.id.btnLogout);
        imgWeather = findViewById(R.id.imgWeather);

        mAuth = FirebaseAuth.getInstance();

        // Waktu sekarang
        String currentTime = new SimpleDateFormat(
                "hh:mm a",
                Locale.getDefault()
        ).format(new Date());

        txtTime.setText(currentTime);

        // Tombol logout
        btnLogout.setOnClickListener(v -> {

            mAuth.signOut();

            Toast.makeText(
                    MainActivity.this,
                    "Logout berhasil",
                    Toast.LENGTH_SHORT
            ).show();

            Intent intent =
                    new Intent(
                            MainActivity.this,
                            LoginActivity.class);

            startActivity(intent);

            finish();
        });

        // Jalankan auto update cuaca
        weatherRunnable.run();
    }

    private void getWeatherData(double lat, double lon) {

        String apiKey = "51652f369798f9c9697089c599dee0fb";

        String url =
                "https://api.openweathermap.org/data/2.5/weather?lat="
                        + lat
                        + "&lon="
                        + lon
                        + "&units=metric&appid="
                        + apiKey;

        RequestQueue queue =
                Volley.newRequestQueue(this);

        JsonObjectRequest request =
                new JsonObjectRequest(
                        Request.Method.GET,
                        url,
                        null,

                        response -> {

                            try {

                                // Ambil data main
                                JSONObject main =
                                        response.getJSONObject("main");

                                // Ambil suhu
                                double temp =
                                        main.getDouble("temp");

                                // Ambil nama lokasi
                                String location =
                                        response.getString("name");

                                // Ambil icon cuaca
                                JSONObject weather =
                                        response.getJSONArray("weather")
                                                .getJSONObject(0);

                                String iconCode =
                                        weather.getString("icon");

                                // URL icon OpenWeather
                                String iconUrl =
                                        "https://openweathermap.org/img/wn/"
                                                + iconCode
                                                + "@2x.png";

                                // Load icon ke ImageView
                                Glide.with(MainActivity.this)
                                        .load(iconUrl)
                                        .into(imgWeather);

                                // Set suhu
                                txtTemp.setText(
                                        ((int) temp) + "°C");

                                // Set lokasi
                                txtLocation.setText(location);

                                // Update jam terakhir refresh
                                String currentTime =
                                        new SimpleDateFormat(
                                                "hh:mm a",
                                                Locale.getDefault()
                                        ).format(new Date());

                                txtTime.setText(currentTime);

                            } catch (Exception e) {

                                e.printStackTrace();

                                Toast.makeText(
                                        MainActivity.this,
                                        "Error parsing data",
                                        Toast.LENGTH_SHORT
                                ).show();
                            }
                        },

                        error -> {

                            txtTemp.setText("--°C");

                            Toast.makeText(
                                    MainActivity.this,
                                    "Gagal mengambil data cuaca",
                                    Toast.LENGTH_LONG
                            ).show();
                        });

        queue.add(request);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Hentikan auto refresh
        handler.removeCallbacks(weatherRunnable);
    }
}