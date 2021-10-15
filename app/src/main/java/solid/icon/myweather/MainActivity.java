package solid.icon.myweather;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
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
import java.util.List;

import solid.icon.myweather.room.AppDatabase;
import solid.icon.myweather.room.CitiesList;
import solid.icon.myweather.room.CitiesListDao;
import solid.icon.myweather.room.RoomHelper;

public class MainActivity extends AppCompatActivity {

    private TextView TV_city_name, TV_temperature, TV_condition;
    private EditText ED_city_name;
    private ImageView IV_condition, IV_background, IV_search;
    private RecyclerView RV_weather;
    private ArrayList<WeatherModal> weatherModalArrayList;
    private WeatherAdapter weatherAdapter;
    private String startCity = "kiev";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        init();

        if(hasConnection(this)) {
            getWeatherName(startCity);
            setCityName(startCity);
        } else{

        }

        IV_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cityName = ED_city_name.getText().toString().trim();
                if(cityName.isEmpty()){
                    weatherModalArrayList.clear();
                    make_toast("Field city name is empty");
                }else {
                    getWeatherName(cityName);
                    setCityName(cityName);
                }
            }
        });
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
        weatherModalArrayList = new ArrayList<>();
        weatherAdapter = new WeatherAdapter(this, weatherModalArrayList);
        RV_weather.setAdapter(weatherAdapter);

    }

    public static boolean hasConnection(final Context context) {
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
                RoomHelper.toRoomDB(cityName, weatherModalArrayList);
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