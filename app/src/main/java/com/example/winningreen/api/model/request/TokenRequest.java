package com.example.winningreen.api.model.request;

import com.google.gson.annotations.SerializedName;

public class TokenRequest {

    @SerializedName("grant_type")
    private String grantType = "client_credentials";

    @SerializedName("client_id")
    private String clientId;

    @SerializedName("client_secret")
    private String clientSecret;

    @SerializedName("audience")
    private String audiance = "https://api2.arduino.cc/iot";

    public TokenRequest(String clientId, String clientSecret) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

    public String getGrantType() {
        return grantType;
    }

    public String getClientId() {
        return clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }
    public String getAudiance() {
        return audiance;
    }
}
