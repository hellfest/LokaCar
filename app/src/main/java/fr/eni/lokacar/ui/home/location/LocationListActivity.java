package fr.eni.lokacar.ui.home.location;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.eni.lokacar.AppActivity;
import fr.eni.lokacar.R;
import fr.eni.lokacar.adpater.LocationAdapter;
import fr.eni.lokacar.modele.Gerant;
import fr.eni.lokacar.modele.Location;
import fr.eni.lokacar.modele.StatusRest;
import fr.eni.lokacar.modele.Vehicule;
import fr.eni.lokacar.ui.login.LoginActivity;
import fr.eni.lokacar.utils.Constant;
import fr.eni.lokacar.utils.Network;
import fr.eni.lokacar.utils.Preference;

public class LocationListActivity extends AppActivity {
    private static final int REQUEST_CODE = 200;
    private ListView listViewLocation;
    private LocationAdapter adapter;
    private List<Location> listLocation;
    private Gerant gerant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_list);
        gerant = Preference.getGerant(LocationListActivity.this);

        listLocation = new ArrayList<>();

        listViewLocation = (ListView) findViewById(R.id.listViewLocation);

        adapter = new LocationAdapter(LocationListActivity.this,R.layout.item_list_location, listLocation);
        listViewLocation.setAdapter(adapter);

        getLocation();

        listViewLocation.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Location location = (Location) parent.getItemAtPosition(position);

                Intent intent = new Intent(LocationListActivity.this,LocationFormActivity.class);
                intent.putExtra("location", location.id);

                startActivityForResult(intent, REQUEST_CODE);

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_CODE){
            if (resultCode == RESULT_OK){
                getLocation();
            }
        }
    }

    private void getLocation() {
        if (gerant != null) {

            //check network available or not
            if (Network.isNetworkAvailable(LocationListActivity.this)) {

                // Instantiate the RequestQueue.
                RequestQueue queue = Volley.newRequestQueue(LocationListActivity.this);

                String url = String.format(Constant.URL_LOCATION_LISTE, gerant.session);

                // Request a string response from the provided URL.
                StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                        new Response.Listener<String>() {
                            @Override
                            //json = parametre de reponse
                            public void onResponse(String json) {

                                Gson gson = new Gson();
                                //Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
                                Type listType = new TypeToken<ArrayList<Location>>(){}.getType();
                                listLocation.clear();
                                listLocation.addAll((Collection<? extends Location>) gson.fromJson(json, listType));
                                adapter.notifyDataSetChanged();

                            }

                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Toast.makeText(LocationListActivity.this, "Erreur récupération de la liste de véhicule", Toast.LENGTH_SHORT).show();

                    }
                });
                // Add the request to the RequestQueue.
                queue.add(stringRequest);

            }
        } else {
            Toast.makeText(LocationListActivity.this, R.string.error_connexion_gerant, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(LocationListActivity.this, LoginActivity.class);
            startActivity(intent);
        }


    }


}
