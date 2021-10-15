package solid.icon.myweather.room;

import java.util.List;

import solid.icon.myweather.App;

public class SavedCitiesHelper {

     private AppDatabase db = App.getInstance().getDatabase();
     private final SavedCitiesDao savedCitiesDao = db.savedCitiesDao();

    public SavedCitiesHelper() {
    }

    public void addKiev_and_Dnipropetrovsk(){
        SavedCities savedCitiesKiev = new SavedCities();
        SavedCities savedCitiesDnipropetrovsk = new SavedCities();
        savedCitiesKiev.nameCity = "Kiev";
        savedCitiesDnipropetrovsk.nameCity = "Dnipropetrovsk";
        savedCitiesDao.insert(savedCitiesKiev);
        savedCitiesDao.insert(savedCitiesDnipropetrovsk);
    }

    public void add_cityToRoomDB(String cityName){
        List<SavedCities> savedCitiesList = savedCitiesDao.getAll();
        if(isCity(cityName, savedCitiesList)){
            SavedCities savedCities = new SavedCities();
            savedCities.nameCity = cityName;
            savedCitiesDao.insert(savedCities);
        }
    }

    public String[] getArrayCities(){
        String[] arrayCities = new String[]{};
        List<SavedCities> savedCitiesList = savedCitiesDao.getAll();
        for(int iterator = 0; iterator < savedCitiesList.size(); iterator++){
            arrayCities[iterator] = savedCitiesList.get(iterator).nameCity;
        }
        return arrayCities;
    }

    private boolean isCity(String cityName, List<SavedCities> savedCities){
        boolean isCity = false;
        for(SavedCities c : savedCities){
            if(c.nameCity.equals(cityName)){
                isCity = true;
                break;
            }
        }
        return isCity;
    }
}
