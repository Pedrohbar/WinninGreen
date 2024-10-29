package com.example.winningreen;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.util.Log;

import com.example.winningreen.api.model.response.TokenResponse;
import com.example.winningreen.repository.ArduinoRepository;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TokenManager {
    private static final String TAG = "TokenManager";
    private static TokenManager instance;
    private Context context;
    private ArduinoRepository arduinoRepository;
    private Handler handler;

    // Interface de Callback para notificar o resultado da geração do token
    public interface TokenCallback {
        void onTokenGenerated();
        void onTokenGenerationFailed();
    }

    private TokenManager(Context context) {
        this.context = context.getApplicationContext(); // Usar Application Context para evitar vazamentos
        this.arduinoRepository = new ArduinoRepository(this.context);
        this.handler = new Handler();
    }

    // Método para obter a instância
    public static synchronized TokenManager getInstance(Context context) {
        if (instance == null) {
            instance = new TokenManager(context);
        }
        return instance;
    }

    // Método para gerar o token inicial
    public void generateToken(final TokenCallback callback) {
        Log.d(TAG, "generateToken: Iniciando geração de token");
        arduinoRepository.generateToken(new Callback<TokenResponse>() {
            @Override
            public void onResponse(Call<TokenResponse> call, Response<TokenResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    TokenResponse tokenResponse = response.body();
                    saveToken(tokenResponse.getAccessToken());
                    callback.onTokenGenerated();
                } else {
                    callback.onTokenGenerationFailed();
                }
            }

            @Override
            public void onFailure(Call<TokenResponse> call, Throwable t) {
                callback.onTokenGenerationFailed();
            }
        });
    }

    // Método para salvar o token em SharedPreferences
    private void saveToken(String token) {
        SharedPreferences prefs = context.getSharedPreferences("arduino_prefs", Context.MODE_PRIVATE);
        prefs.edit()
                .putString("token", token)
                .apply();
    }

}
