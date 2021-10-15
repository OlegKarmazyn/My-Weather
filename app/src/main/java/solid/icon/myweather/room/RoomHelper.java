package solid.icon.myweather.room;

import java.util.ArrayList;
import java.util.List;

import solid.icon.myweather.App;
import solid.icon.myweather.WeatherModal;

public class RoomHelper {

    public RoomHelper() {
    }

    public static void toRoomDB(String cityName, ArrayList<WeatherModal> weatherModals){
        AppDatabase db = App.getInstance().getDatabase();
        final CitiesListDao citiesListDao = db.dayLifeCycleDao();
        boolean isCity = false;
        List<CitiesList> citiesListList = citiesListDao.getAll();

        for(CitiesList c : citiesListList){
            if(c.nameCity.equals(cityName)){
                isCity = true;
                break;
            }
        }

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
                    CitiesList citiesList = new CitiesList();
                    citiesList.nameCity = cityName;
                    citiesList.time = w.getTime();
                    citiesList.temperature = w.getTemperature();
                    citiesList.wind = w.getWindSpeed();
                    citiesList.icon = w.getIcon();
                    citiesListDao.update(citiesList);
                }
            }
        }
    }
}
