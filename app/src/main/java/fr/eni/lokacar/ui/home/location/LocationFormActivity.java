package fr.eni.lokacar.ui.home.location;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import fr.eni.lokacar.R;
import fr.eni.lokacar.modele.Client;
import fr.eni.lokacar.modele.Gerant;
import fr.eni.lokacar.modele.Location;
import fr.eni.lokacar.ui.home.client.ClientUpdateActivity;
import fr.eni.lokacar.ui.login.LoginActivity;
import fr.eni.lokacar.utils.Constant;
import fr.eni.lokacar.utils.Network;
import fr.eni.lokacar.utils.Preference;

public class LocationFormActivity extends AppCompatActivity {

    private Location location;
    private int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_form);

        id = getIntent().getIntExtra("location", -1);

        location = null;

        getLocation();

    }

    /**
     *
     */
    public void getLocation() {
        Gerant gerant = Preference.getGerant(LocationFormActivity.this);

        if (gerant != null) {

            //check network available or not
            if (Network.isNetworkAvailable(LocationFormActivity.this)) {

                // Instantiate the RequestQueue.
                RequestQueue queue = Volley.newRequestQueue(LocationFormActivity.this);

                String url = String.format(Constant.URL_GET_LOCATION, id, gerant.session);

                // Request a string response from the provided URL.
                StringRequest stringRequest = new StringRequest(Request.Method.PUT, url,
                        new Response.Listener<String>() {
                            @Override
                            //json = parametre de reponse
                            public void onResponse(String json) {
                                Gson gson = new Gson();
                                location = gson.fromJson(json, Location.class);
                                //TODO : affichage
                            }

                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Toast.makeText(LocationFormActivity.this, "Erreur récupération de la location", Toast.LENGTH_SHORT).show();

                    }
                });

                // Add the request to the RequestQueue.
                queue.add(stringRequest);

            }
        } else {
            Toast.makeText(LocationFormActivity.this, R.string.client_error_insert, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(LocationFormActivity.this, LoginActivity.class);
            startActivity(intent);
        }

    }
}
