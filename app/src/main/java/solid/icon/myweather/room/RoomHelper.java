package solid.icon.myweather.room;

import java.util.ArrayList;
import java.util.List;

import solid.icon.myweather.App;
import solid.icon.myweather.WeatherModal;

public class RoomHelper {

    private static AppDatabase db = App.getInstance().getDatabase();
    private static final CitiesListDao citiesListDao = db.dayLifeCycleDao();

    public RoomHelper() {
    }

    public static void toRoomDB(String cityName, ArrayList<WeatherModal> weatherModals){
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
            for(CitiesList c : citiesListList){
                for(WeatherModal w : weatherModals){
                    c.nameCity = cityName;
                    c.time = w.getTime();
                    c.temperature = w.getTemperature();
                    c.wind = w.getWindSpeed();
                    c.icon = w.getIcon();
                    citiesListDao.update(c);
                }
            }
        }
    }

    public static ArrayList<WeatherModal> getAL_weatherModals(String cityName){
        List<CitiesList> citiesListList = citiesListDao.getAllByCityName(cityName);
        boolean isCity = isCity(cityName, citiesListList);
        ArrayList<WeatherModal> weatherModals = new ArrayList<>();
        if(!isCity){
            for(CitiesList citiesList : citiesListList){
                String time = citiesList.time;
                String temperature = citiesList.temperature;
                String wind = citiesList.wind;
                String icon = citiesList.icon;
                WeatherModal weatherModal = new WeatherModal(time, temperature, icon, wind);
                weatherModals.add(weatherModal);
            }
        }
        return weatherModals;
    }

    private static boolean isCity(String cityName, List<CitiesList> citiesListList){
        boolean isCity = false;
        for(CitiesList c : citiesListList){
            if(c.nameCity.equals(cityName)){
                isCity = true;
                break;
            }
        }
        return isCity;
    }
}
