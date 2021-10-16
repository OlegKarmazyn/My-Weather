package solid.icon.myweather.weather_adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import solid.icon.myweather.R;

public class WeatherAdapter extends RecyclerView.Adapter<WeatherAdapter.ViewHolder> {

    private Context context;
    private ArrayList<WeatherModal> weatherModals;

    public WeatherAdapter(Context context, ArrayList<WeatherModal> weatherModals) {
        this.context = context;
        this.weatherModals = weatherModals;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.weather_card_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        WeatherModal weatherModal = weatherModals.get(position);

        String url = "http:".concat(weatherModal.getIcon());

        holder.TV_temperature.setText(weatherModal.getTemperature().concat("℃"));
        holder.TV_speed.setText(weatherModal.getTemperature().concat("Km/h"));

//        Picasso.get()
//                .load(url)
//                .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
//                .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
//                .error(R.mipmap.ic_launcher)
//                .into(holder.IV_condition);
        Picasso.get()
                .load(url)
                .networkPolicy(NetworkPolicy.OFFLINE)
                .into(holder.IV_condition, new Callback() {
                    @Override
                    public void onSuccess() {

                    }
                    @Override
                    public void onError(Exception e) {
                        Picasso.get().load(url).networkPolicy(NetworkPolicy.NO_CACHE)
                                .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE).error(R.mipmap.ic_launcher)
                                .into(holder.IV_condition, new Callback() {
                                    @Override
                                    public void onSuccess() {
                                        Log.v("Picasso","fetch image success in try again.");
                                    }
                                    @Override
                                    public void onError(Exception e) {
                                        Log.v("Picasso","Could not fetch image again...");
                                    }
                                });
                    }
                });

        SimpleDateFormat in = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        SimpleDateFormat out = new SimpleDateFormat("hh:mm aa");
        try {
            Date date = in.parse(weatherModal.getTime());
            holder.TV_time.setText(out.format(date));
        }catch (ParseException e){

        }
    }

    @Override
    public int getItemCount() {
        return weatherModals.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView TV_time, TV_temperature, TV_speed;
        private ImageView IV_condition;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            TV_time = itemView.findViewById(R.id.TV_time);
            TV_temperature = itemView.findViewById(R.id.TV_temperature);
            TV_speed = itemView.findViewById(R.id.TV_speed);
            IV_condition = itemView.findViewById(R.id.IV_condition);
        }
    }
}