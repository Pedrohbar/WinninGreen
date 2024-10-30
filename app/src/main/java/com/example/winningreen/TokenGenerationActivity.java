package com.example.winningreen;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class TokenGenerationActivity extends AppCompatActivity {

    private static final String TAG = "TokenGenerationActivity";
    private TokenManager tokenManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_token_generation);
        Log.d(TAG, "onCreate: TokenGenerationActivity iniciado");

        tokenManager = TokenManager.getInstance(this);
        generateToken();
    }

    private void generateToken() {
        tokenManager.generateToken(new TokenManager.TokenCallback() {
            @Override
            public void onTokenGenerated() {
                runOnUiThread(() -> {
                    Toast.makeText(TokenGenerationActivity.this, "Token gerado com sucesso!", Toast.LENGTH_SHORT).show();

                    // Verifica o modo
                    SharedPreferences prefs = getSharedPreferences("arduino_prefs", MODE_PRIVATE);
                    boolean isTestMode = prefs.getBoolean("isTestMode", false);

                    if (isTestMode) {
                        Intent intent = new Intent(TokenGenerationActivity.this, DashboardActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Intent intent = new Intent(TokenGenerationActivity.this, ThingIdActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
            }

            @Override
            public void onTokenGenerationFailed() {
                runOnUiThread(() -> {
                    Toast.makeText(TokenGenerationActivity.this, "Falha na autenticação. Verifique suas credenciais.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(TokenGenerationActivity.this, CredentialsActivity.class);
                    startActivity(intent);
                    finish();
                });
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}