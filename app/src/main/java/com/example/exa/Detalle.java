package com.example.exa;


import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import WebServices.Asynchtask;
import WebServices.WebService;

public class Detalle extends FragmentActivity implements OnMapReadyCallback, Asynchtask {

    private GoogleMap googleMapInstance;
    private JSONObject userInfoJson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Asegúrate de que tu layout se llame 'activity_detalle.xml' o como lo hayas nombrado.
        setContentView(R.layout.activity_detalle);

        initializeUserDataFromIntent();

        if (userInfoJson != null) {
            populateUserDetails();
            requestCountryFlagData();
            initializeMapFragment();
        }
    }

    private void initializeUserDataFromIntent() {
        String userDataString = getIntent().getStringExtra("userData");
        if (userDataString == null || userDataString.isEmpty()) {
            Toast.makeText(this, "No se recibieron datos del usuario.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        try {
            this.userInfoJson = new JSONObject(userDataString);
        } catch (JSONException e) {
            Log.e("Detalle_Activity", "Error al parsear el string JSON del usuario.", e);
            Toast.makeText(this, "Error en el formato de los datos del usuario.", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    /**
     * Rellena las vistas con la información del usuario, usando los IDs del nuevo layout.
     */
    private void populateUserDetails() {
        // --- AJUSTE AQUÍ: Se usan los nuevos IDs del layout alternativo ---
        ImageView profileImageView = findViewById(R.id.userProfileAvatar);
        TextView nameTextView = findViewById(R.id.userNameText);
        TextView infoTextView = findViewById(R.id.userInfoText);

        try {
            JSONObject nameData = userInfoJson.getJSONObject("name");
            String fullName = String.format("%s. %s %s",
                    nameData.getString("title"),
                    nameData.getString("first"),
                    nameData.getString("last"));
            nameTextView.setText(fullName);

            String profileImageUrl = userInfoJson.getJSONObject("picture").getString("large");
            Glide.with(this)
                    .load(profileImageUrl)
                    .circleCrop()
                    .into(profileImageView);

            String email = userInfoJson.getString("email");
            String phone = userInfoJson.getString("phone");
            String address = buildFormattedAddress(userInfoJson.getJSONObject("location"));
            String contactInfo = String.format("Email: %s\n\nTeléfono: %s\n\nDirección: %s", email, phone, address);
            infoTextView.setText(contactInfo);

        } catch (JSONException e) {
            Log.e("Detalle_Activity", "Fallo al extraer datos para poblar la UI.", e);
        }
    }

    private String buildFormattedAddress(JSONObject locationData) throws JSONException {
        JSONObject streetData = locationData.getJSONObject("street");
        return String.format("%d %s, %s, %s",
                streetData.getInt("number"),
                streetData.getString("name"),
                locationData.getString("city"),
                locationData.getString("country"));
    }

    private void requestCountryFlagData() {
        try {
            String countryName = userInfoJson.getJSONObject("location").getString("country");
            String apiUrl = "https://restcountries.com/v3.1/name/" + countryName;
            WebService webServiceTask = new WebService(apiUrl, new HashMap<>(), this, this);
            webServiceTask.execute("GET");
        } catch (JSONException e) {
            Log.e("Detalle_Activity", "No se pudo obtener el nombre del país del JSON.", e);
        }
    }

    private void initializeMapFragment() {
        // --- AJUSTE AQUÍ: Se usa el ID del contenedor del mapa del nuevo layout ---
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapContainer);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMapInstance = googleMap;
        configureMapSettings();
        displayUserLocationOnMap();
    }

    private void configureMapSettings() {
        googleMapInstance.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        googleMapInstance.getUiSettings().setZoomControlsEnabled(true);
        googleMapInstance.getUiSettings().setCompassEnabled(true);
        googleMapInstance.getUiSettings().setAllGesturesEnabled(true);
        googleMapInstance.getUiSettings().setMapToolbarEnabled(true);
    }

    private void displayUserLocationOnMap() {
        try {
            JSONObject coordinates = userInfoJson.getJSONObject("location").getJSONObject("coordinates");
            double latitude = coordinates.getDouble("latitude");
            double longitude = coordinates.getDouble("longitude");
            String cityName = userInfoJson.getJSONObject("location").getString("city");

            LatLng userPosition = new LatLng(latitude, longitude);
            googleMapInstance.addMarker(new MarkerOptions().position(userPosition).title(cityName));
            googleMapInstance.moveCamera(CameraUpdateFactory.newLatLngZoom(userPosition, 15));

        } catch (JSONException e) {
            Log.e("Detalle_Activity", "No se pudieron obtener las coordenadas para el mapa.", e);
        }
    }

    @Override
    public void processFinish(String serverResponse) {
        // --- AJUSTE AQUÍ: Se usa el ID de la bandera del nuevo layout ---
        ImageView flagImageView = findViewById(R.id.countryFlagImage);
        try {
            JSONArray jsonResponse = new JSONArray(serverResponse);
            JSONObject countryData = jsonResponse.getJSONObject(0);
            String flagUrl = countryData.getJSONObject("flags").getString("png");
            Glide.with(this).load(flagUrl).into(flagImageView);

        } catch (JSONException e) {
            Log.e("Detalle_Activity", "Error al procesar la respuesta de la API de países.", e);
            Toast.makeText(this, "No se pudo cargar la imagen de la bandera.", Toast.LENGTH_SHORT).show();
        }
    }
}