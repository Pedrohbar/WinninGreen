package com.example.winningreen.api.service;

import com.example.winningreen.api.RetrofitClient;
import com.example.winningreen.api.model.PropertyValue;
import com.example.winningreen.api.model.request.TokenRequest;
import com.example.winningreen.api.model.response.ThingResponse;
import com.example.winningreen.api.model.response.TokenResponse;

import retrofit2.Call;
import retrofit2.Callback;

public class ApiService {
    private final ArduinoCloudApiService apiInterface;

    public ApiService() {
        this.apiInterface = RetrofitClient.getApiService();
    }

    public void generateToken(String clientId, String clientSecret, Callback<TokenResponse> callback) {
        TokenRequest tokenRequest = new TokenRequest(clientId, clientSecret);
        Call<TokenResponse> call = apiInterface.generateToken(tokenRequest);

        call.enqueue(callback);
    }

    public void getThing(String thingId, String token, Callback<ThingResponse> callback) {
        String acceptHeader = "application/vnd.arduino.thing+json,application/vnd.goa.error+json";
        boolean showProperties = true;

        Call<ThingResponse> call = apiInterface.getThing(
                thingId,
                "Bearer " + token,
                acceptHeader,
                showProperties
        );

        call.enqueue(callback);
    }

    // Método para atualizar o valor de uma propriedade
    public void updateThingPropertyValue(String thingId, String propertyId, String token, int novoValor, Callback<Void> callback) {
        String authorization = "Bearer " + token;
        String contentType = "application/json";
        String accept = "application/json";

        PropertyValue<Integer> value = new PropertyValue<>(novoValor);

        Call<Void> call = apiInterface.updatePropertyValueThing(
                thingId,
                propertyId,
                authorization,
                contentType,
                accept,
                value
        );

        call.enqueue(callback);
    }
}
