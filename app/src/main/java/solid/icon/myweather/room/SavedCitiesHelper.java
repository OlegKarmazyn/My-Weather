package solid.icon.myweather.room;

import java.util.List;

import solid.icon.myweather.App;

public class SavedCitiesHelper {

     private AppDatabase db = App.getInstance().getDatabase();
     private final SavedCitiesDao savedCitiesDao = db.savedCitiesDao();

    public SavedCitiesHelper() {
    }

    public void addKiev_and_Dnipropetrovsk(){
        String kiev = "Kiev";
        String dnipropetrovsk = "Dnipropetrovsk";
        SavedCities savedCitiesKiev = new SavedCities();
        SavedCities savedCitiesDnipropetrovsk = new SavedCities();
        savedCitiesKiev.nameCity = kiev;
        savedCitiesDnipropetrovsk.nameCity = dnipropetrovsk;
        if(!isCity(kiev)) {
            savedCitiesDao.insert(savedCitiesKiev);
        }
        if(!isCity(dnipropetrovsk)) {
            savedCitiesDao.insert(savedCitiesDnipropetrovsk);
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
