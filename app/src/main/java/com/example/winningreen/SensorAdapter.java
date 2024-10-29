package com.example.winningreen;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.winningreen.model.HumiditySensor;

import java.util.List;

public class SensorAdapter extends RecyclerView.Adapter<SensorAdapter.SensorViewHolder> {

    private List<HumiditySensor> sensorList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onSensorClick(int position);
    }

    public SensorAdapter(List<HumiditySensor> sensorList, OnItemClickListener listener) {
        this.sensorList = sensorList;
        this.listener = listener;
    }

    @Override
    public SensorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sensor, parent, false);
        return new SensorViewHolder(v);
    }

    @Override
    public void onBindViewHolder(SensorViewHolder holder, int position) {
        HumiditySensor sensor = sensorList.get(position);
        holder.bind(sensor, listener);
    }

    @Override
    public int getItemCount() {
        return sensorList.size();
    }

    public static class SensorViewHolder extends RecyclerView.ViewHolder {
        public ImageView iconSensor;
        public TextView textSensorName;
        public TextView textSensorValue;

        public SensorViewHolder(View itemView) {
            super(itemView);
            iconSensor = itemView.findViewById(R.id.icon_sensor);
            textSensorName = itemView.findViewById(R.id.text_sensor_name);
            textSensorValue = itemView.findViewById(R.id.text_sensor_value);
        }

        public void bind(HumiditySensor sensor, OnItemClickListener listener) {
            iconSensor.setImageResource(R.drawable.ic_humidity);
            textSensorName.setText(sensor.getDisplayName());
            textSensorValue.setText("Umidade: " + sensor.getValue() + "%");

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onSensorClick(getAdapterPosition());
                }
            });
        }
    }
}