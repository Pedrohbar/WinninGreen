package com.example.winningreen;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.EditText;
import android.text.InputType;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.winningreen.api.model.ThingProperty;
import com.example.winningreen.api.model.response.ThingResponse;
import com.example.winningreen.model.HumiditySensor;
import com.example.winningreen.repository.ArduinoRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DashboardActivity extends AppCompatActivity implements SensorAdapter.OnItemClickListener {

    private static final String TAG = "DashboardActivity";
    private RecyclerView recyclerViewSensores;
    private TokenManager tokenManager;
    private SensorAdapter sensorAdapter;
    private List<HumiditySensor> sensorList;
    private ArduinoRepository arduinoRepository;
    private String thingId;
    private Map<String, String> customNames;
    private Button btnAtualizar;
    private Handler simuladorHandler;
    private Runnable simuladorRunnable;
    private IrrigationManager irrigationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        tokenManager = TokenManager.getInstance(this);

        //views
        recyclerViewSensores = findViewById(R.id.recyclerViewSensores);
        recyclerViewSensores.setLayoutManager(new LinearLayoutManager(this));

        btnAtualizar = findViewById(R.id.btnAtualizar);
        btnAtualizar.setOnClickListener(v -> getHumiditySensors());


        sensorList = new ArrayList<>();
        sensorAdapter = new SensorAdapter(sensorList, this);
        recyclerViewSensores.setAdapter(sensorAdapter);

        //ArduinoRepository
        arduinoRepository = new ArduinoRepository(this);

        //Nomes personalizados
        customNames = new HashMap<>();
        loadCustomSensorNames();

        // Inicialização do IrrigationManager
        irrigationManager = new IrrigationManager(this);

        // Obter o Thing ID salvo
        thingId = getSharedPreferences("arduino_prefs", MODE_PRIVATE)
                .getString("thingId", null);

        if (thingId != null) {
            getHumiditySensors();
            startMoistureLossSimulator();
        } else {
            Toast.makeText(this, "Thing ID não encontrado.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
    private void renewToken(final Runnable onSuccess) {
        tokenManager.generateToken(new TokenManager.TokenCallback() {
            @Override
            public void onTokenGenerated() {
                onSuccess.run();
            }

            @Override
            public void onTokenGenerationFailed() {
                Toast.makeText(DashboardActivity.this, "Erro ao renovar token.", Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void getHumiditySensors() {
        arduinoRepository.getThing(thingId, new Callback<ThingResponse>() {
            @Override
            public void onResponse(Call<ThingResponse> call, Response<ThingResponse> response) {
                if (response.code() == 401) {
                    renewToken(new Runnable() {
                        @Override
                        public void run() {
                            getHumiditySensors();
                        }
                    });
                }
                else if (response.isSuccessful() && response.body() != null) {
                    updateHumidityListSensors(response.body());
                } else {
                    Toast.makeText(DashboardActivity.this, "Erro ao obter dados dos sensores.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ThingResponse> call, Throwable t) {
                Toast.makeText(DashboardActivity.this, "Erro na comunicação: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Erro na comunicação: " + t.getMessage());
            }
        });
    }


    private void updateHumidityListSensors(ThingResponse thingResponse) {
        sensorList.clear();

        for (ThingProperty property : thingResponse.getProperties()) {
            // Filtrar variáveis onde type é PERCENTAGE_RELATIVE_HUMIDITY
            if ("PERCENTAGE_RELATIVE_HUMIDITY".equals(property.getType())) {
                String id = property.getId();
                String name = property.getName();
                String displayName = customNames.getOrDefault(id, id);
                int value;

                try {
                    value = Integer.parseInt(property.getLastValue());
                } catch (NumberFormatException e) {
                    value = 0; // Valor padrão se a conversão falhar
                }

                HumiditySensor sensor = new HumiditySensor(id, name, displayName, value);
                sensorList.add(sensor);
            }
        }

        sensorAdapter.notifyDataSetChanged();
    }

    @Override
    public void onSensorClick(int position) {
        HumiditySensor sensor = sensorList.get(position);
        showDialogOptions(sensor);
    }

    private void showDialogOptions(HumiditySensor sensor) {
        String[] opcoes = {"Trocar Nome", "Irrigar", "Irrigação Automática"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(sensor.getDisplayName());
        builder.setItems(opcoes, (dialog, which) -> {
            switch (which) {
                case 0:
                    showEditNameDialog(sensor);
                    break;
                case 1:
                    showManualIrrigateDialog(sensor);
                    break;
                case 2:
                    showAutomaticIrrigateDialog(sensor);
                    break;
            }
        });
        builder.show();
    }

    private void showEditNameDialog(HumiditySensor sensor) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Editar Nome do Sensor");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setText(sensor.getDisplayName());
        builder.setView(input);

        builder.setPositiveButton("Salvar", (dialog, which) -> {
            String newName = input.getText().toString().trim();
            if (!newName.isEmpty() && newName.length() <= 15) {
                sensor.setDisplayName(newName);
                saveCustomSensorName(sensor.getId(), newName);
                sensorAdapter.notifyDataSetChanged();
                Log.d(TAG, "Nome do sensor atualizado para: " + newName);
            } else {
                Toast.makeText(this, "Nome inválido. Máximo de 15 caracteres.", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Nome inválido inserido.");
            }
        });
        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void showManualIrrigateDialog(HumiditySensor sensor) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Irrigar");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        input.setHint("Umidade desejada (%)");
        builder.setView(input);

        builder.setPositiveButton("Confirmar", (dialog, which) -> {
            String valueStr = input.getText().toString().trim();
            if (!valueStr.isEmpty()) {
                try {
                    int value = Integer.parseInt(valueStr);
                    if (value > sensor.getValue() && value <= 100) {
                        // Simular irrigação atualizando o valor do sensor
                        updateSensorValue(sensor, value);
                        Log.d(TAG, "Irrigação iniciada para sensor " + sensor.getId() + " com valor " + value);
                    } else {
                        Toast.makeText(this, "Valor inválido. Deve ser maior que " + sensor.getValue() + " e até 100.", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Valor de irrigação inválido: " + value);
                    }
                } catch (NumberFormatException e) {
                    Toast.makeText(this, "Por favor, insira um valor válido.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Por favor, insira um valor.", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Valor de irrigação vazio.");
            }
        });
        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void showAutomaticIrrigateDialog(HumiditySensor sensor) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Irrigação Automática");

        // Criar layout com dois campos de entrada
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 40, 50, 10);

        final EditText inputMin = new EditText(this);
        inputMin.setInputType(InputType.TYPE_CLASS_NUMBER);
        inputMin.setHint("Umidade mínima (%)");
        layout.addView(inputMin);

        final EditText inputMax = new EditText(this);
        inputMax.setInputType(InputType.TYPE_CLASS_NUMBER);
        inputMax.setHint("Umidade máxima (%)");
        layout.addView(inputMax);

        builder.setView(layout);

        builder.setPositiveButton("Confirmar", (dialog, which) -> {
            String minStr = inputMin.getText().toString().trim();
            String maxStr = inputMax.getText().toString().trim();
            if (!minStr.isEmpty() && !maxStr.isEmpty()) {
                try {
                    int minValue = Integer.parseInt(minStr);
                    int maxValue = Integer.parseInt(maxStr);

                    if (minValue < maxValue && minValue >= 0 && maxValue <= 100) {
                        
                        irrigationManager.saveIrrigationConfig(sensor.getId(), minValue, maxValue);
                        Toast.makeText(this, "Irrigação automática configurada.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Valores inválidos.", Toast.LENGTH_SHORT).show();
                    }
                } catch (NumberFormatException e) {
                    Toast.makeText(this, "Por favor, insira valores válidos.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Por favor, insira os valores.", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Valores de irrigação automática vazios.");
            }
        });
        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void updateSensorValue(HumiditySensor sensor, int novoValor) {
        arduinoRepository.updatePropertyValue(thingId, sensor.getId(), novoValor, new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.code() == 401) {
                    renewToken(new Runnable() {
                        @Override
                        public void run() {
                            updateSensorValue(sensor, novoValor);
                        }
                    });
                }
                else if (response.isSuccessful()) {
                    sensor.setValue(novoValor);
                    sensorAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(DashboardActivity.this, "Falha ao atualizar o valor.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(DashboardActivity.this, "Erro na comunicação: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadCustomSensorNames() {
        SharedPreferences prefs = getSharedPreferences("sensor_names", Context.MODE_PRIVATE);
        Map<String, ?> allEntries = prefs.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            customNames.put(entry.getKey(), entry.getValue().toString());
        }
    }

    private void saveCustomSensorName(String variableId, String displayName) {
        SharedPreferences prefs = getSharedPreferences("sensor_names", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(variableId, displayName);
        editor.apply();
        customNames.put(variableId, displayName);
    }

    private void startMoistureLossSimulator() {
        simuladorHandler = new Handler();
        simuladorRunnable = new Runnable() {
            @Override
            public void run() {
                for (HumiditySensor sensor : sensorList) {
                    int newValue = sensor.getValue() - 1;
                    if (newValue < 0) newValue = 0;

                    // Atualizar valor do sensor
                    updateSensorValue(sensor, newValue);

                    // Verificar irrigação automática via IrrigationManager
                    if (irrigationManager.isIrrigationEnabled(sensor.getId())) {
                        int minHumidity = irrigationManager.getMinHumidity(sensor.getId());
                        int maxHumidity = irrigationManager.getMaxHumidity(sensor.getId());
                        if (sensor.getValue() <= minHumidity) {
                            // Irrigar para o valor máximo
                            updateSensorValue(sensor, maxHumidity);
                        }
                    }
                }
                // Agenda próxima simulação após 5 segundos
                simuladorHandler.postDelayed(this, 5000);
            }
        };
        // Iniciar simulação
        simuladorHandler.postDelayed(simuladorRunnable, 5000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (simuladorHandler != null && simuladorRunnable != null) {
            simuladorHandler.removeCallbacks(simuladorRunnable);
            Log.d(TAG, "Simulador de perda de umidade interrompido");
        }
    }
}
