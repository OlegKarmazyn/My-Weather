package solid.icon.myweather;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private TextView TV_city_name, TV_temperature, TV_condition;
    private EditText ED_city_name;
    private ImageView IV_launcher, IV_background, IV_search;
    private RecyclerView RV_weather;
    private ArrayList<WeatherModal> weatherModals;
    private WeatherAdapter weatherAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();

        IV_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cityName = TV_city_name.getText().toString().trim();
                if(cityName.isEmpty()){
                    //Toasty
                }
            }
        });
    }

    private void getCityName(String cityName){
        final String url = "http://api.weatherapi.com/v1/forecast.json?key=b99185c6f87940b28d5112006211310&q=" + cityName +"&days=1&aqi=no&alerts=no";

    }

    private void init(){
        TV_city_name = findViewById(R.id.TV_city_name);
        TV_temperature = findViewById(R.id.TV_temperature);
        TV_condition = findViewById(R.id.TV_condition);
        ED_city_name = findViewById(R.id.ED_city_name);
        IV_launcher = findViewById(R.id.IV_launcher);
        IV_background = findViewById(R.id.IV_background);
        IV_search = findViewById(R.id.IV_search);
        RV_weather = findViewById(R.id.RV_weather);
        weatherModals = new ArrayList<>();
        weatherAdapter = new WeatherAdapter(this, weatherModals);
        RV_weather.setAdapter(weatherAdapter);
    }
}