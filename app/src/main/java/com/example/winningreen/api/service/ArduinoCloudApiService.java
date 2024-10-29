package com.example.winningreen.api.service;

import com.example.winningreen.api.model.PropertyValue;
import com.example.winningreen.api.model.request.TokenRequest;
import com.example.winningreen.api.model.response.ThingResponse;
import com.example.winningreen.api.model.response.TokenResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ArduinoCloudApiService {

    @POST("/iot/v1/clients/token")
    Call<TokenResponse> generateToken(@Body TokenRequest tokenRequest);

    @GET("/iot/v2/things/{id}")
    Call<ThingResponse> getThing(
            @Path("id") String thingId,
            @Header("Authorization") String token,
            @Header("Accept") String acceptHeader,
            @Query("show_properties") boolean showProperties
    );

    @PUT("/iot/v2/things/{thingId}/properties/{propertyId}/publish")
    Call<Void> updatePropertyValueThing(
            @Path("thingId") String thingId,
            @Path("propertyId") String propertyId,
            @Header("Authorization") String authorization,
            @Header("Content-Type") String contentType,
            @Header("Accept") String accept,
            @Body PropertyValue<Integer> value
    );
}
