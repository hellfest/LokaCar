package fr.eni.lokacar.ui.login;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.v4.media.RatingCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import fr.eni.lokacar.AppActivity;
import fr.eni.lokacar.R;
import fr.eni.lokacar.modele.Gerant;
import fr.eni.lokacar.ui.home.HomeActivity;
import fr.eni.lokacar.ui.main.MainActivity;
import fr.eni.lokacar.utils.Constant;
import fr.eni.lokacar.utils.Network;
import fr.eni.lokacar.utils.Preference;

public class LoginActivity extends AppActivity {

    private EditText editTextLogin;
    private EditText editTextPassword;
    private Button buttonLogin;

    private String identifiant;
    private String mdp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        editTextLogin = (EditText) findViewById(R.id.editTextLogin);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        buttonLogin = (Button) findViewById(R.id.buttonLogin);

    }

    public void onClickButtonSeConnecter(View view) {
        identifiant = editTextLogin.getText().toString();
        mdp = editTextPassword.getText().toString();


        if (!identifiant.isEmpty() && !mdp.isEmpty()) {

            //check network available or not
            if (Network.isNetworkAvailable(LoginActivity.this)) {

                // Instantiate the RequestQueue.
                RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
                String url = String.format(Constant.URL_IDENTIFICATION, identifiant, mdp);

//                // Afficher la boite de dialogue (loader)
//                final Dialog dialog = FastDialog.showProgressDialog(MainActivity.this, "Chargement...");
//                dialog.show();

                // Request a string response from the provided URL.
                StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                        new Response.Listener<String>() {
                            @Override
                            //json = parametre de reponse
                            public void onResponse(String json) {
//                                dialog.dismiss(); //cacher le loader

//                                // Now we call setRefreshing(false) to signal refresh has finished
//                                swipeContainer.setRefreshing(false);

                                Gson gson = new Gson();

                                Gerant gerant = gson.fromJson(json, Gerant.class);


                                // Ou enregistrement de tout l'objet comme préférence
//                                // Pour qu'elle persiste lors de la réouverture de l'appli
                                Preference.setGerant(LoginActivity.this, gerant);
                                // enregistrement de la ville saisie comme préférence
                                // Pour qu'elle persiste lors de la réouverture de l'appli
//                                Preference.setCity(MainActivity.this, owm.name);
//
//

//                                textViewCity.setText(owm.name);
//                                textViewTemperature.setText(owm.main.temp + " °C");
//                                textViewHumidity.setText(owm.main.humidity + " % of humidity");
//                                textViewSunrise.setText(getString(R.string.leve_soleil) + localToGMT(owm.sys.sunrise) + getString(R.string.heure_soleil));
//                                textViewSunset.setText("Couché du soleil: " + localToGMT(owm.sys.sunset) + getString(R.string.heure_soleil));
//
//                                // afficher l'image avec picasso
//                                if (owm.weather.size() > 0) {
//                                    Picasso.with(MainActivity.this).load(String.format(Constant.URL_IMAGE, owm.weather.get(0).icon)).into(imageViewWeather);
//                                }
//
//                                //Afficher la googlemap
//                                // Add a marker in Town and move the camera
//                                LatLng ville = new LatLng(owm.coord.lat, owm.coord.lon);
//                                mMap.clear();
//                                mMap.addMarker(new MarkerOptions().position(ville).title("Marker in " + owm.name));
//                                mMap.moveCamera(CameraUpdateFactory.newLatLng(ville));
                                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                startActivity(intent);
                                Toast.makeText(LoginActivity.this, "Bonjour " + gerant.prenom, Toast.LENGTH_SHORT).show();

                            }

                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

//                        dialog.dismiss(); //cacher le loader
//                        // Now we call setRefreshing(false) to signal refresh has finished
//                        swipeContainer.setRefreshing(false);
//
//                        String json = new String(error.networkResponse.data);
//                        Gson gson = new Gson();
//                        OpenWeatherMap owm = gson.fromJson(json, OpenWeatherMap.class);

                        Toast.makeText(LoginActivity.this, "Erreur d'identification, veuillez vérifier votre identifiant et votre mot de passe", Toast.LENGTH_SHORT).show();

                    }
                });
                // Add the request to the RequestQueue.
                queue.add(stringRequest);
//            } else {
//                FastDialog.showDialog(MainActivity.this, FastDialog.SIMPLE_DIALOG, getString(R.string.dialog_error_network));
//                // Now we call setRefreshing(false) to signal refresh has finished
//                swipeContainer.setRefreshing(false);
//            }
//        } else {
//            FastDialog.showDialog(MainActivity.this, FastDialog.SIMPLE_DIALOG, getString(R.string.dialog_error_city_empty));
//            // Now we call setRefreshing(false) to signal refresh has finished
//            swipeContainer.setRefreshing(false);
//        }

            }
        } else
        {
            Toast.makeText(LoginActivity.this, "Le login et le mot de passe doivent être renseignés", Toast.LENGTH_SHORT).show();
        }
    }
}
