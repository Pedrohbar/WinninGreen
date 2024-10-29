package com.example.winningreen;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class FirstScreenActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private Button btnModoTeste, btnInserirCredenciais;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Verificar se o setup já foi concluído
        SharedPreferences prefs = getSharedPreferences("arduino_prefs", MODE_PRIVATE);
        boolean isSetupComplete = prefs.getBoolean("isSetupComplete", false);

        if (isSetupComplete) {
            Log.d(TAG, "Setup já foi concluído. Redirecionando para DashboardActivity.");
            Intent intent = new Intent(FirstScreenActivity.this, DashboardActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        setContentView(R.layout.activity_first_screen);

        Log.d(TAG, "onCreate: MainActivity iniciado");

        btnModoTeste = findViewById(R.id.btnModoTeste);
        btnInserirCredenciais = findViewById(R.id.btnInserirCredenciais);

        btnModoTeste.setOnClickListener(v -> {
            Log.d(TAG, "Modo Teste selecionado");

            String testClientId = "5GSMpZoRAcVLcHHLW88fWsIYiula6D9c";
            String testClientSecret = "HznQ5iWwQryB4H69EBEAzvnESkV2anWs22X6J9krA5jVtSo7PKdqLfFxncpAstvF";
            String testThingId = "cde81b33-0aa8-419e-90c2-1c51db59dfc4";

            // Salvar as credenciais de teste e o modo de teste em SharedPreferences
            prefs.edit()
                    .putString("clientId", testClientId)
                    .putString("clientSecret", testClientSecret)
                    .putString("thingId", testThingId)
                    .putBoolean("isTestMode", true) // Flag para indicar Modo Teste
                    .putBoolean("isSetupComplete", true) // Marcar setup como completo
                    .apply();

            // Ir para TokenGenerationActivity
            Intent intent = new Intent(FirstScreenActivity.this, TokenGenerationActivity.class);
            startActivity(intent);
            finish();
        });

        btnInserirCredenciais.setOnClickListener(v -> {
            // Ir para CredentialsActivity
            Intent intent = new Intent(FirstScreenActivity.this, CredentialsActivity.class);
            startActivity(intent);
        });
    }
}
