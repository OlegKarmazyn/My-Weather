package solid.icon.myweather.room;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface CitiesListDao {

    @Query("SELECT * FROM CitiesList")
    List<CitiesList> getAll();

    @Query("SELECT * FROM CitiesList WHERE id = :id")
    CitiesList getById(long id);

    @Query("SELECT * FROM CitiesList WHERE nameCity = :nameCity")
    CitiesList getByNameCity(String nameCity);

    @Insert
    void insert(CitiesList citiesList);

    @Update
    void update(CitiesList citiesList);

    @Delete
    void delete(CitiesList citiesList);
}
