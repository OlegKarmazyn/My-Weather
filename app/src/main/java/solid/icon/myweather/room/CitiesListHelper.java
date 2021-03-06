package solid.icon.myweather.room;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import solid.icon.myweather.App;
import solid.icon.myweather.weather_adapter.WeatherModal;

public class CitiesListHelper {

     AppDatabase db = App.getInstance().getDatabase();
     final CitiesListDao citiesListDao = db.citiesListDao();

    public CitiesListHelper() {
    }

    public void toRoomDB(String cityName, ArrayList<WeatherModal> weatherModals){
        List<CitiesList> citiesListList = citiesListDao.getAll();
        boolean isCity = isCity(cityName, citiesListList);

        if(!isCity){
            for(WeatherModal w : weatherModals){
                CitiesList citiesList = new CitiesList();
                citiesList.nameCity = cityName;
                citiesList.time = w.getTime();
                citiesList.temperature = w.getTemperature();
                citiesList.wind = w.getWindSpeed();
                citiesList.icon = w.getIcon();
                citiesListDao.insert(citiesList);
            }
        } else {
            citiesListList = citiesListDao.getAllByCityName(cityName);
            int iterator = 0;
            for(CitiesList c : citiesListList){
                WeatherModal w = weatherModals.get(iterator);
                c.time = w.getTime();
                c.temperature = w.getTemperature();
                c.wind = w.getWindSpeed();
                c.icon = w.getIcon();
                citiesListDao.update(c);
                iterator++;
            }
        }
    }

    public ArrayList<WeatherModal> getAL_weatherModals(String cityName){
        List<CitiesList> citiesListList = citiesListDao.getAllByCityName(cityName);
        boolean isCity = isCity(cityName, citiesListList);
        ArrayList<WeatherModal> weatherModals = new ArrayList<>();
        if(isCity){
            for(CitiesList citiesList : citiesListList){
                String time = citiesList.time;
                String temperature = citiesList.temperature;
                String wind = citiesList.wind;
                String icon = citiesList.icon;
                Log.d("DATA", time + " --- " + temperature + " --- " + wind + " --- " + icon);
                WeatherModal weatherModal = new WeatherModal(time, temperature, icon, wind);
                weatherModals.add(weatherModal);
            }
        }
        return weatherModals;
    }

    private boolean isCity(String cityName, List<CitiesList> citiesListList){
        boolean isCity = false;
        for(CitiesList c : citiesListList){
            if(c.nameCity.equals(cityName)){
                isCity = true;
                break;
            }
        }
        return isCity;
    }

    public String getCurrentTemperature(String cityName){
        List<CitiesList> citiesListList = citiesListDao.getAllByCityName(cityName);
        for(int iterator = 0; iterator < citiesListList.size(); iterator++) {
            SimpleDateFormat in = new SimpleDateFormat("yyyy-MM-dd hh:mm");
            try {
                Date currentDate = new Date();
                Log.e("DATE = ", String.valueOf(currentDate));
                Date startDate = in.parse(citiesListList.get(iterator).time);
                Log.e("DATE 2 = ", String.valueOf(citiesListList.get(iterator).time));
                Date endDate = in.parse(citiesListList.get(iterator + 1).time);
                if (currentDate.after(startDate) && currentDate.before(endDate)) {
                    return citiesListList.get(iterator+3).temperature; //time Zone (+3)
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return "";
    }
}
