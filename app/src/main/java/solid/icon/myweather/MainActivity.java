package solid.icon.myweather;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import solid.icon.myweather.room.RoomHelper;

public class MainActivity extends AppCompatActivity {

    private TextView TV_city_name, TV_temperature, TV_condition;
    private EditText ED_city_name;
    private ImageView IV_condition, IV_background, IV_search;
    private RecyclerView RV_weather;
    private ArrayList<WeatherModal> weatherModalArrayList;
    private WeatherAdapter weatherAdapter;
    private String startCity = "kiev";
    private Spinner spinner_city;
    private int item_select = 0;
    private String spinner_item = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        init();
        spinner_adapters();

        IV_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(hasConnection(MainActivity.this)) {
                    String cityName = ED_city_name.getText().toString().trim();
                    if (cityName.isEmpty()) {
                        weatherModalArrayList.clear();
                        make_toast("Field city name is empty");
                    } else {
                        getWeatherName(cityName);
                        setCityName(cityName);
                    }
                } else {
                    make_toast("no internet connection");
                }
            }
        });
        ifHasConnection();
    }

    private void init(){
        TV_city_name = findViewById(R.id.TV_city_name);
        TV_temperature = findViewById(R.id.TV_temperature);
        TV_condition = findViewById(R.id.TV_condition);
        ED_city_name = findViewById(R.id.ED_city_name);
        IV_condition = findViewById(R.id.IV_condition);
        IV_background = findViewById(R.id.IV_background);
        IV_search = findViewById(R.id.IV_search);
        RV_weather = findViewById(R.id.RV_weather);
        spinner_city = findViewById(R.id.spinner_city);
        weatherModalArrayList = new ArrayList<>();
        weatherAdapter = new WeatherAdapter(this, weatherModalArrayList);
        RV_weather.setAdapter(weatherAdapter);
    }

    private void ifHasConnection(){
        if(hasConnection(this)) {
            Log.e("connection", "= yes");
            getWeatherName(startCity);
            setCityName(startCity);
        } else{
            Log.e("connection", "= no");
            setWeatherInfo(startCity);
        }
    }

    private void spinner_adapters() {
        String[] city = new String[]{"Kiev", "Dnipropetrovsk"};
        ArrayAdapter<String> adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, city);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_city.setAdapter(adapter);
        AdapterView.OnItemSelectedListener itemSelectedListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                // Get the selected object
                spinner_item = (String)parent.getItemAtPosition(position);
                item_select = position;
                Log.e("item_select = ", String.valueOf(item_select));
                Log.e("spinner_item = ", spinner_item);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        };
        spinner_city.setOnItemSelectedListener(itemSelectedListener);

        spinner_city.setSelection(item_select);
    }

    private void setWeatherInfo(String cityName){
        weatherModalArrayList.clear();
        IV_background.setImageResource(R.drawable.day_night);
        weatherModalArrayList = new RoomHelper().getAL_weatherModals(cityName);
        weatherAdapter = new WeatherAdapter(this, weatherModalArrayList);
        RV_weather.setAdapter(weatherAdapter);
        weatherAdapter.notifyDataSetChanged();
    }

    private static boolean hasConnection(final Context context) {
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiInfo != null && wifiInfo.isConnected())
        {
            return true;
        }
        wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (wifiInfo != null && wifiInfo.isConnected())
        {
            return true;
        }
        wifiInfo = cm.getActiveNetworkInfo();
        if (wifiInfo != null && wifiInfo.isConnected())
        {
            return true;
        }
        return false;
    }

    private void getWeatherName(String cityName){
        final String url = "http://api.weatherapi.com/v1/forecast.json?key=b99185c6f87940b28d5112006211310&q=" + cityName + "&days=1&aqi=no&alerts=no";
        final String urlDayIcon = "https://images.wallpaperscraft.ru/image/single/oblaka_vysota_nebo_solnce_belyy_goluboy_svet_6448_320x480.jpg";
        final String urlNightIcon = "https://images.wallpaperscraft.ru/image/single/noch_luna_nebo_129012_320x480.jpg";

        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                weatherModalArrayList.clear();

                try {
                    JSONObject currentJSONObject = response.getJSONObject("current");
                    String temperature = currentJSONObject.getString("temp_c");
                    int is_day = currentJSONObject.getInt("is_day");
                    String condition = currentJSONObject.getJSONObject("condition").getString("text");
                    String conditionIcon = currentJSONObject.getJSONObject("condition").getString("icon");

                    TV_temperature.setText(temperature.concat("℃"));
                    TV_condition.setText(condition);
                    Picasso.get().load("http:".concat(conditionIcon)).into(IV_condition);

                    if(is_day == 1){
                        Picasso.get().load(urlDayIcon).into(IV_background);
                    }else {
                        Picasso.get().load(urlNightIcon).into(IV_background);
                    }

                    JSONObject forecastObject = response.getJSONObject("forecast").getJSONArray("forecastday").getJSONObject(0);
                    JSONArray hourArray = forecastObject.getJSONArray("hour");

                    for(int iterator = 0; iterator < hourArray.length(); iterator++ ){
                        JSONObject hourObj = hourArray.getJSONObject(iterator);
                        String time = hourObj.getString("time");
                        String temp = hourObj.getString("temp_c");
                        String icon = hourObj.getJSONObject("condition").getString("icon");
                        String wind = hourObj.getString("wind_kph");

                        weatherModalArrayList.add(new WeatherModal(time, temp, icon, wind));
                    }

                    weatherAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                new RoomHelper().toRoomDB(cityName, weatherModalArrayList);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("ERROR", error.toString());
                make_toast("Pls enter valid city name!");
                TV_city_name.setText("wrong city name");
                weatherModalArrayList.clear();
            }
        });

        requestQueue.add(jsonObjectRequest);
    }

    private void setCityName(String cityName){
        String upCity = "";
        upCity = cityName.substring(0, 1).toUpperCase() + cityName.substring(1);
        TV_city_name.setText(upCity);
    }

    private void make_toast(String text){
        Toast.makeText(MainActivity.this, text, Toast.LENGTH_SHORT).show();
    }
}