package com.example.winningreen;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class IrrigationManager {
    private static final String TAG = "IrrigationManager";
    private static final String PREFS_NAME = "irrigation_prefs";
    private static final String KEY_IRRIGATION_ENABLED_PREFIX = "irrigation_enabled_";
    private static final String KEY_MIN_HUMIDITY_PREFIX = "min_humidity_";
    private static final String KEY_MAX_HUMIDITY_PREFIX = "max_humidity_";

    private SharedPreferences sharedPreferences;

    public IrrigationManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    // Salvar configuração de irrigação automática para um sensor específico
    public void saveIrrigationConfig(String sensorId, int minHumidity, int maxHumidity) {
        Log.d(TAG, "saveIrrigationConfig: Salvando configuração de irrigação para sensor " + sensorId);
        sharedPreferences.edit()
                .putBoolean(KEY_IRRIGATION_ENABLED_PREFIX + sensorId, true)
                .putInt(KEY_MIN_HUMIDITY_PREFIX + sensorId, minHumidity)
                .putInt(KEY_MAX_HUMIDITY_PREFIX + sensorId, maxHumidity)
                .apply();
    }

    // Verificar se a irrigação automática está habilitada para um sensor específico
    public boolean isIrrigationEnabled(String sensorId) {
        return sharedPreferences.getBoolean(KEY_IRRIGATION_ENABLED_PREFIX + sensorId, false);
    }

    // Obter a umidade mínima configurada para um sensor específico
    public int getMinHumidity(String sensorId) {
        return sharedPreferences.getInt(KEY_MIN_HUMIDITY_PREFIX + sensorId, 0);
    }

    // Obter a umidade máxima configurada para um sensor específico
    public int getMaxHumidity(String sensorId) {
        return sharedPreferences.getInt(KEY_MAX_HUMIDITY_PREFIX + sensorId, 100);
    }

}
