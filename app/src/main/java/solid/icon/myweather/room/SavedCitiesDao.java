package solid.icon.myweather.room;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface SavedCitiesDao {

    @Query("SELECT * FROM SavedCities")
    List<SavedCities> getAll();

    @Query("SELECT * FROM SavedCities WHERE id = :id")
    CitiesList getById(long id);

    @Query("SELECT * FROM SavedCities WHERE nameCity = :nameCity")
    CitiesList getByNameCity(String nameCity);

    @Insert
    void insert(SavedCities savedCities);

    @Update
    void update(SavedCities savedCities);

    @Delete
    void delete(SavedCities savedCities);
}
