package solid.icon.myweather.room;

import android.content.Context;

import java.util.List;

import solid.icon.myweather.App;
import solid.icon.myweather.R;

public class SavedCitiesHelper {

     private AppDatabase db = App.getInstance().getDatabase();
     private final SavedCitiesDao savedCitiesDao = db.savedCitiesDao();
     private Context context;

    public SavedCitiesHelper(Context context) {
        this.context = context;
    }

    public void addKiev_and_Dnipropetrovsk(){
        String first_main_city = context.getResources().getString(R.string.first_main_city);
        String second_main_city = context.getResources().getString(R.string.second_main_city);
        SavedCities savedFirstCity = new SavedCities();
        SavedCities savedSecondCity = new SavedCities();
        savedFirstCity.nameCity = first_main_city;
        savedSecondCity.nameCity = second_main_city;
        if(!isCity(first_main_city)) {
            savedCitiesDao.insert(savedFirstCity);
        }
        if(!isCity(second_main_city)) {
            savedCitiesDao.insert(savedSecondCity);
        }
    }

    public void add_cityToRoomDB(String cityName){
        if(!isCity(cityName)){
            SavedCities savedCities = new SavedCities();
            savedCities.nameCity = cityName;
            savedCitiesDao.insert(savedCities);
        }
    }

    public String[] getArrayCities(){
        List<SavedCities> savedCitiesList = savedCitiesDao.getAll();
        String[] arrayCities = new String[savedCitiesList.size()];
        for(int iterator = 0; iterator < savedCitiesList.size(); iterator++){
            arrayCities[iterator] = savedCitiesList.get(iterator).nameCity;
        }
        return arrayCities;
    }

    private boolean isCity(String cityName){
        List<SavedCities> savedCitiesList = savedCitiesDao.getAll();
        boolean isCity = false;
        for(SavedCities c : savedCitiesList){
            if(c.nameCity.equals(cityName)){
                isCity = true;
                break;
            }
        }
        return isCity;
    }
}
