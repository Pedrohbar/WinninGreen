package com.example.winningreen.repository;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;


import com.example.winningreen.api.model.response.ThingResponse;
import com.example.winningreen.api.model.response.TokenResponse;
import com.example.winningreen.api.service.ApiService;

import retrofit2.Callback;

public class ArduinoRepository {

    private static final String TAG = "ArduinoRepository";
    private Context context;
    private ApiService apiService;

    private String clientId;
    private String clientSecret;

    public ArduinoRepository(Context context) {
        this.context = context.getApplicationContext();
        this.apiService = new ApiService();

        SharedPreferences prefs = context.getSharedPreferences("arduino_prefs", Context.MODE_PRIVATE);
        clientId = prefs.getString("clientId", null);
        clientSecret = prefs.getString("clientSecret", null);

        // Verificar se as credenciais estão disponíveis
        if (clientId == null || clientSecret == null) {
            Log.e(TAG, "ArduinoRepository: Credenciais não definidas.");
            throw new IllegalStateException("Credenciais não definidas. Certifique-se de que clientId e clientSecret foram salvos.");
        }
    }

    // Método para gerar o token
    public void generateToken(Callback<TokenResponse> callback) {
        Log.d(TAG, "gerarToken: Chamando API para gerar token");
        apiService.generateToken(clientId, clientSecret, callback);
    }

    // Método para obter o token atual
    private String getToken() {
        SharedPreferences prefs = context.getSharedPreferences("arduino_prefs", Context.MODE_PRIVATE);
        return prefs.getString("token", null);
    }

    // Método para obter a Thing
    public void getThing(String thingId, Callback<ThingResponse> callback) {

        String token = getToken();
        if (token == null) {
            throw new IllegalStateException("Token não disponível.");
        }
        apiService.getThing(thingId, token, callback);
    }

    // Método para atualizar o valor de uma propriedade
    public void updatePropertyValue(String thingId, String propertyId, int novoValor, Callback<Void> callback) {
        String token = getToken();
        if (token == null) {
            throw new IllegalStateException("Token não disponível.");
        }
        Log.d(TAG, "atualizarValorPropriedade: Chamando API para atualizar propriedade " + propertyId + " do Thing " + thingId + " para " + novoValor);
        apiService.updateThingPropertyValue(thingId, propertyId, token, novoValor, callback);
    }
}
