package com.example.winningreen;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class CredentialsActivity extends AppCompatActivity {

    private static final String TAG = "CredentialsActivity";
    private EditText edtClientId, edtClientSecret;
    private Button btnValidarCredenciais;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credentials);
        Log.d(TAG, "onCreate: CredentialsActivity iniciado");

        edtClientId = findViewById(R.id.edtClientId);
        edtClientSecret = findViewById(R.id.edtClientSecret);
        btnValidarCredenciais = findViewById(R.id.btnValidarCredenciais);

        btnValidarCredenciais.setOnClickListener(v -> {
            String clientId = edtClientId.getText().toString().trim();
            String clientSecret = edtClientSecret.getText().toString().trim();

            if (!clientId.isEmpty() && !clientSecret.isEmpty()) {
                saveCretendials(clientId, clientSecret);

                //redireciona para TokenGenerationActivity
                Intent intent = new Intent(CredentialsActivity.this, TokenGenerationActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Por favor, insira as credenciais.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveCretendials(String clientId, String clientSecret) {

        SharedPreferences prefs = getSharedPreferences("arduino_prefs", MODE_PRIVATE);
        prefs.edit()
                .putString("clientId", clientId)
                .putString("clientSecret", clientSecret)
                .apply();
    }


}
