package com.example.winningreen;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


import com.example.winningreen.api.model.response.ThingResponse;
import com.example.winningreen.repository.ArduinoRepository;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ThingIdActivity extends AppCompatActivity {

    private EditText edtThingId;
    private Button btnValidarThingId;
    private ArduinoRepository arduinoRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thing_id);

        edtThingId = findViewById(R.id.edtThingId);
        btnValidarThingId = findViewById(R.id.btnValidarThingId);

        arduinoRepository = new ArduinoRepository(this);

        btnValidarThingId.setOnClickListener(v -> {
            String thingId = edtThingId.getText().toString().trim();

            if (!thingId.isEmpty()) {
                validarThingId(thingId);
            } else {
                Toast.makeText(this, "Por favor, insira o Thing ID.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void validarThingId(String thingId) {
        arduinoRepository.getThing(thingId, new Callback<ThingResponse>() {
            @Override
            public void onResponse(Call<ThingResponse> call, Response<ThingResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Thing ID is valid, save and proceed
                    salvarThingId(thingId);
                    Intent intent = new Intent(ThingIdActivity.this, DashboardActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(ThingIdActivity.this, "Thing ID inválido ou sem acesso.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ThingResponse> call, Throwable t) {
                Toast.makeText(ThingIdActivity.this, "Erro na comunicação: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void salvarThingId(String thingId) {
        SharedPreferences prefs = getSharedPreferences("arduino_prefs", MODE_PRIVATE);
        prefs.edit()
                .putString("thingId", thingId)
                .apply();
    }
}
