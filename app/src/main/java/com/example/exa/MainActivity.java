package com.example.exa;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import WebServices.Asynchtask;
import WebServices.WebService;

// 1. La clase ahora implementa la interfaz Asynchtask para recibir la respuesta.
public class MainActivity extends AppCompatActivity implements Asynchtask {

    private RecyclerView recyclerView;
    private UsuarioAd usuarioAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicializar el RecyclerView (esto no cambia)
        recyclerView = findViewById(R.id.recyclerViewUsers);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 2. Iniciar la petición de red usando la clase WebService.
        loadUsersData();
    }

    /**
     * Prepara y ejecuta la petición a la API usando la clase WebService.
     */
    private void loadUsersData() {
        // La URL de la API para obtener los usuarios.
        String url = "https://randomuser.me/api/?results=20";

        // 3. Se crea una instancia de WebService.
        // Se le pasa la URL, datos vacíos, y el contexto (this) como actividad y como listener (callback).
        WebService webService = new WebService(url, new HashMap<>(), this, this);

        // 4. Se ejecuta la petición en segundo plano. El resultado llegará al método processFinish.
        webService.execute("GET");
    }

    /**
     * Este método es OBLIGATORIO por implementar Asynchtask.
     * Se ejecuta automáticamente cuando la clase WebService termina su trabajo y tiene una respuesta.
     * @param result La respuesta del servidor en formato de texto (String).
     */
    @Override
    public void processFinish(String result) {
        // 5. El manejo de la respuesta ahora ocurre aquí.
        try {
            // Se convierte el texto de respuesta en un objeto JSON.
            JSONObject jsonResponse = new JSONObject(result);

            // Se extrae el array de usuarios llamado "results".
            JSONArray usersArray = jsonResponse.getJSONArray("results");

            // Se crea el adaptador con los datos obtenidos.
            usuarioAd = new UsuarioAd(usersArray, this);

            // Se asigna el adaptador al RecyclerView para mostrar la lista.
            recyclerView.setAdapter(usuarioAd);

        } catch (JSONException e) {
            // Se maneja cualquier error que ocurra al leer el formato del JSON.
            Log.e("MainActivity", "Error de parseo JSON", e);
            Toast.makeText(this, "El formato de los datos es incorrecto", Toast.LENGTH_SHORT).show();
        }
    }
}