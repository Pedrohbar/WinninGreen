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

                    // Verificar o modo atual
                    SharedPreferences prefs = getSharedPreferences("arduino_prefs", MODE_PRIVATE);
                    boolean isTestMode = prefs.getBoolean("isTestMode", false);

                    if (isTestMode) {
                        // No Modo Teste, navegar diretamente para DashboardActivity
                        Log.d(TAG, "Modo Teste ativo. Navegando para DashboardActivity");
                        Intent intent = new Intent(TokenGenerationActivity.this, DashboardActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        // No Modo Normal, navegar para ThingIdActivity
                        Log.d(TAG, "Modo Normal ativo. Navegando para ThingIdActivity");
                        Intent intent = new Intent(TokenGenerationActivity.this, ThingIdActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
            }

            @Override
            public void onTokenGenerationFailed() {
                Log.e(TAG, "onTokenGenerationFailed: Falha na geração do token");
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
        // Opcional: Interromper a renovação se necessário
        // tokenManager.stopTokenRenewal();
    }
}