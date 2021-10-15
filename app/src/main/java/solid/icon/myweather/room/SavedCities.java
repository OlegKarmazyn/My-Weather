package solid.icon.myweather.room;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class SavedCities {

    @PrimaryKey(autoGenerate = true)
    public long id;

    @ColumnInfo
    public String nameCity;
}

