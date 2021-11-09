package solid.icon.myweather;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
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

import solid.icon.myweather.room.CitiesListHelper;
import solid.icon.myweather.room.SavedCitiesHelper;
import solid.icon.myweather.weather_adapter.WeatherAdapter;
import solid.icon.myweather.weather_adapter.WeatherModal;

public class MainActivity extends AppCompatActivity {

    public final static String TEMP_VALUE = "℃";
    public final static String SPEED_VALUE = "Km/h";

    private TextView city_name_text, temperature_text, condition_text, forecast_text;
    private EditText city_name_edit;
    private ImageView condition_image, background_image, search_icon, add_city_icon;
    private RecyclerView weather_recycleView;
    private ArrayList<WeatherModal> weatherModalArrayList;
    private WeatherAdapter weatherAdapter;
    private String startCity = getResources().getString(R.string.first_main_city);
    private Spinner city_spinner;
    private int item_select = 0;
    private String spinner_item = "";
    private SavedCitiesHelper savedCitiesHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        savedCitiesHelper = new SavedCitiesHelper(this);
        savedCitiesHelper.addKiev_and_Dnipropetrovsk();
        init();
        spinner_adapters();

        search_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(hasConnection(MainActivity.this)) {
                    String cityName = city_name_edit.getText().toString().trim();
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

        add_city_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String city = city_name_edit.getText().toString().trim();
                if(!city.isEmpty()) {
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("Add city")
                            .setMessage("Are you sure you want to add " + city + "?")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    make_toast("City: " + city + " is added");
                                    savedCitiesHelper.add_cityToRoomDB(city);
                                    spinner_adapters();
                                }
                            })
                            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    make_toast("Cancel");
                                }
                            })
                            .show();
                } else {
                    make_toast("Field city name is empty");
                }
            }
        });
        setInfo(startCity);
    }

    private void init(){
        city_name_text = findViewById(R.id.city_name_text);
        temperature_text = findViewById(R.id.temperature_text);
        condition_text = findViewById(R.id.condition_text);
        city_name_edit = findViewById(R.id.city_name_edit);
        condition_image = findViewById(R.id.condition_image);
        background_image = findViewById(R.id.background_image);
        forecast_text = findViewById(R.id.forecast_text);
        search_icon = findViewById(R.id.search_icon);
        add_city_icon = findViewById(R.id.add_city_icon);
        weather_recycleView = findViewById(R.id.weather_recyclerView);
        city_spinner = findViewById(R.id.city_spinner);
        weatherModalArrayList = new ArrayList<>();
        weatherAdapter = new WeatherAdapter(this, weatherModalArrayList);
        weather_recycleView.setAdapter(weatherAdapter);
    }

    private void setInfo(String cityName){
        if(hasConnection(this)) {
            Log.e("connection", "= yes");
            getWeatherName(cityName);
            setCityName(cityName);
            condition_image.setVisibility(View.VISIBLE);
            forecast_text.setVisibility(View.VISIBLE);
        } else{
            Log.e("connection", "= no");
            setWeatherInfo(cityName);
            make_toast("no internet connection");
        }
    }

    private void spinner_adapters() {
        String[] city = savedCitiesHelper.getArrayCities();
        ArrayAdapter<String> adapter = new ArrayAdapter(this, R.layout.spinner_item, city);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        city_spinner.setAdapter(adapter);
        AdapterView.OnItemSelectedListener itemSelectedListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Get the selected object
                spinner_item = (String)parent.getItemAtPosition(position);
                item_select = position;
                Log.e("item_select = ", String.valueOf(item_select));
                Log.e("spinner_item = ", spinner_item);
                setInfo(spinner_item);
                //weather_recycleView.scrollToPosition(10);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        };
        city_spinner.setOnItemSelectedListener(itemSelectedListener);

        city_spinner.setSelection(item_select);
    }

    private void setWeatherInfo(String cityName){
        weatherModalArrayList.clear();
        background_image.setImageResource(R.drawable.day_night);
        weatherModalArrayList = new CitiesListHelper().getAL_weatherModals(cityName);
        weatherAdapter = new WeatherAdapter(this, weatherModalArrayList);
        weather_recycleView.setAdapter(weatherAdapter);
        weatherAdapter.notifyDataSetChanged();
        city_name_text.setText(cityName);
        condition_text.setText(get_preferences_condition(cityName));
        temperature_text.setText(new CitiesListHelper().getCurrentTemperature(cityName).concat(TEMP_VALUE));
    }

    private void set_preferences_condition(String cityName, String condition){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(cityName,condition);
        editor.apply();
    }

    private String get_preferences_condition(String cityName){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        return preferences.getString(cityName, "");
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

                    set_preferences_condition(cityName, condition);

                    temperature_text.setText(temperature.concat(TEMP_VALUE));
                    condition_text.setText(condition);
                    Picasso.get().load("http:".concat(conditionIcon)).into(condition_image);

                    if(is_day == 1){
                        Picasso.get().load(urlDayIcon).into(background_image);
                    }else {
                        Picasso.get().load(urlNightIcon).into(background_image);
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
                new CitiesListHelper().toRoomDB(cityName, weatherModalArrayList);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("ERROR", error.toString());
                make_toast("Pls enter valid city name!");
                city_name_text.setText("wrong city name");
                condition_text.setText("");
                weatherModalArrayList.clear();
                wrongCityName();
            }
        });

        requestQueue.add(jsonObjectRequest);
    }

    private void wrongCityName(){
        temperature_text.setText("");
        condition_text.setText("");
        weatherModalArrayList.clear();
        weatherAdapter.notifyDataSetChanged();
        condition_image.setVisibility(View.GONE);
        forecast_text.setVisibility(View.GONE);
    }

    private void setCityName(String cityName){
        String upCity = cityName.substring(0, 1).toUpperCase() + cityName.substring(1);
        city_name_text.setText(upCity);
    }

    private void make_toast(String text){
        Toast.makeText(MainActivity.this, text, Toast.LENGTH_SHORT).show();
    }
}