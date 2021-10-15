package solid.icon.myweather.room;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {CitiesList.class, SavedCities.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract CitiesListDao dayLifeCycleDao();
}
