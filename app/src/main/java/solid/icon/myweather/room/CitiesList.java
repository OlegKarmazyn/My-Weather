package solid.icon.myweather.room;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class CitiesList {

    @PrimaryKey(autoGenerate = true)
    public long id;

    @ColumnInfo
    public String nameCity;

    @ColumnInfo
    public String time;

    @ColumnInfo
    public String temperature;

    @ColumnInfo
    public String wind;

    @ColumnInfo
    public String icon;
}

