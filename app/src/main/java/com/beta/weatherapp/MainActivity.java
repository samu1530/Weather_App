package com.beta.weatherapp;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import android.os.*;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    TextView cityname;
    Button search;
    TextView show;
    String url;


    class getWeather extends AsyncTask<String, Void, String>{
        @Override
        protected String doInBackground(String... urls){
            StringBuilder result = new StringBuilder();
            try {
                URL url= new URL(urls[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

                String line="";
                while((line = reader.readLine()) != null){
                    result.append(line).append("\n");
                }
                return result.toString();

            }catch (Exception e){
                e.printStackTrace();
                return null;
            }
        }
        @Override
        protected void onPostExecute(String Result) {
            super.onPostExecute(Result);
            try {
                JSONObject jsonObject = new JSONObject(Result);
                JSONObject main = jsonObject.getJSONObject("main");

                // Extract values and add unit symbols
                String temperature = main.getString("temp") + " °C";
                String feelsLike = main.getString("feels_like") + " °C";
                String maxTemp = main.getString("temp_max") + " °C";
                String minTemp = main.getString("temp_min") + " °C";
                String pressure = main.getString("pressure") + " hPa";
                String humidity = main.getString("humidity") + " %";
                String seaLevel = main.has("sea_level") ? main.getString("sea_level") + " hPa" : "Not Available";
                String groundLevel = main.has("grnd_level") ? main.getString("grnd_level") + " hPa" : "Not Available";

                // Prepare the formatted weather information string
                String weatherInfo = "Temperature : " + temperature + "\n" +
                        "Feels Like : " + feelsLike + "\n" +
                        "Max Temperature : " + maxTemp + "\n" +
                        "Min Temperature : " + minTemp + "\n" +
                        "Pressure : " + pressure + "\n" +
                        "Humidity : " + humidity + "\n" +
                        "Sea Level : " + seaLevel + "\n" +
                        "Ground Level : " + groundLevel;

                // Display the formatted weather information
                show.setText(weatherInfo);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Press Back Again to Exit", Toast.LENGTH_SHORT).show();

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        cityname = findViewById(R.id.City_name);
        search = findViewById(R.id.search_button);
        show = findViewById(R.id.weather);

        final String[] temp={""};

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "Button Clicked", Toast.LENGTH_SHORT).show();
                String city = cityname.getText().toString();
                try {
                    if(city!=null){
                        url = "https://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=0b49534cd9b18eca12e06e393a7c0cd6&units=metric";
                    }else{
                        Toast.makeText(MainActivity.this, "Enter your city", Toast.LENGTH_SHORT).show();
                    }
                    getWeather task= new getWeather();
                    temp[0] = task.execute(url).get();
                } catch (ExecutionException e){
                    e.printStackTrace();
                }catch(InterruptedException e){
                    e.printStackTrace();
                }
                if(temp[0] == null){
                    show.setText("Unable to fetch your Weather");
                }

            }
        });
    }
}