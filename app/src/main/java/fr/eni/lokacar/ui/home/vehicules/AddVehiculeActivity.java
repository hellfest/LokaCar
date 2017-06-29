package fr.eni.lokacar.ui.home.vehicules;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
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
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.eni.lokacar.AppActivity;
import fr.eni.lokacar.R;
import fr.eni.lokacar.modele.Gerant;
import fr.eni.lokacar.modele.Marque;
import fr.eni.lokacar.modele.Modele;
import fr.eni.lokacar.modele.StatusRest;
import fr.eni.lokacar.ui.home.HomeActivity;
import fr.eni.lokacar.ui.home.client.ClientAddActivity;
import fr.eni.lokacar.ui.login.LoginActivity;
import fr.eni.lokacar.utils.Constant;
import fr.eni.lokacar.utils.Network;
import fr.eni.lokacar.utils.Preference;
import fr.eni.lokacar.utils.Utils;

import static fr.eni.lokacar.R.id.autoTextViewVehiculeModele;
import static java.security.AccessController.getContext;

public class AddVehiculeActivity extends AppActivity {

    private Spinner spinnerMarqueAddVehicule;
    private Spinner spinnerModeleAddVehicule;
    private EditText editTextImmatriculation;
    private CheckBox chekBoxDisponible;
    private Button boutonValiderVehicule;
    private TextView textViewTitreAjoutVehicule;
    private Gerant gerant;
    private List<Marque> listMarques = new ArrayList<>();
    private List<Modele> listModelesMarque = new ArrayList<>();
    private ArrayAdapter<Marque> adapterMarque;
    private ArrayAdapter<Modele> adapterModele;
    Marque marque;
    Modele modele;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_vehicule);


        spinnerMarqueAddVehicule = (Spinner) findViewById(R.id.spinnerMarqueAddVehicule);
        spinnerModeleAddVehicule = (Spinner) findViewById(R.id.spinnerModeleAddVehicule);
        editTextImmatriculation = (EditText) findViewById(R.id.editTextImmatriculation);
        chekBoxDisponible = (CheckBox) findViewById(R.id.ChekBoxDisponible);
        boutonValiderVehicule = (Button) findViewById(R.id.boutonValiderVehicule);
        textViewTitreAjoutVehicule = (TextView) findViewById(R.id.textViewTitreAjoutVehicule);

        textViewTitreAjoutVehicule.setText("Formulaire création d'un véhicule");


        //Récupération du gérant connecté
        gerant = Preference.getGerant(AddVehiculeActivity.this);

        // Récupération de la marque quand sélectionnée dans la liste
        spinnerMarqueAddVehicule.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                marque = (Marque) adapterView.getItemAtPosition(position);
                afficherModele(marque.id);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        spinnerModeleAddVehicule.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                modele = (Modele) adapterView.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        adapterModele = new ArrayAdapter<Modele>(AddVehiculeActivity.this,
                android.R.layout.simple_dropdown_item_1line, listModelesMarque);
        spinnerModeleAddVehicule.setAdapter(adapterModele);


        afficherMarque();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_vehicule_add, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        if (id == R.id.action_vehicule_add) {

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onClickBoutonValiderVehicule(View view) {
        addVehicule();
    }

    /**
     *
     */
    private void addVehicule() {

        if (modele != null && modele.id > 0) {

            final String immatriculation = editTextImmatriculation.getText().toString();
            final int modele_id = modele.id;
            final boolean disponible;

            if (chekBoxDisponible.isChecked()) {
                disponible = true;
            } else {
                disponible = false;
            }

            boolean error = false;

            if (immatriculation == null || immatriculation.isEmpty()) {
                editTextImmatriculation.setError("L'immatriculation est obligatoire");
                error = true;
            }
            if (!error) {
                Gerant gerant = Preference.getGerant(AddVehiculeActivity.this);

                if (gerant != null) {

                    //check network available or not
                    if (Network.isNetworkAvailable(AddVehiculeActivity.this)) {

                        // Instantiate the RequestQueue.
                        RequestQueue queue = Volley.newRequestQueue(AddVehiculeActivity.this);

                        String url = String.format(Constant.URL_ADD_VEHICULE, gerant.session);

                        // Request a string response from the provided URL.
                        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                                new Response.Listener<String>() {
                                    @Override
                                    //json = parametre de reponse
                                    public void onResponse(String json) {
                                        Gson gson = new Gson();
                                        StatusRest status = gson.fromJson(json, StatusRest.class);
                                        if (status.status) {
                                            setResult(RESULT_OK, null);
                                            onBackPressed();
                                        }

                                        Toast.makeText(AddVehiculeActivity.this, status.message, Toast.LENGTH_SHORT).show();

                                    }

                                }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {

                                Toast.makeText(AddVehiculeActivity.this, "Erreur de l'ajout du véhicule", Toast.LENGTH_SHORT).show();

                            }
                        }) {
                            @Override
                            protected Map<String, String> getParams() throws AuthFailureError {
                                Map<String, String> params = new HashMap<String, String>();

                                params.put("modele", String.valueOf(modele_id));
                                params.put("immatriculation", immatriculation);
                                params.put("disponible", (disponible) ? "1" : "0");
                                return params;
                            }
                        };

                        // Add the request to the RequestQueue.
                        queue.add(stringRequest);

                    }
                } else {
                    Toast.makeText(AddVehiculeActivity.this, "Impossible de faire la connexion, veuillez vous connecter", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(AddVehiculeActivity.this, HomeActivity.class);
                    startActivity(intent);
                }

            }
        } else {
            Toast.makeText(AddVehiculeActivity.this, "Impossible de créer le nouveau véhicule. Le modèle et la  marque doivent être renseignés", Toast.LENGTH_SHORT).show();
        }
    }

    //Méthode permettant de récupérer la liste des marques
    public void afficherMarque() {

        if (gerant != null) {

            //check network available or not
            if (Network.isNetworkAvailable(AddVehiculeActivity.this)) {

                // Instantiate the RequestQueue.
                RequestQueue queue = Volley.newRequestQueue(AddVehiculeActivity.this);
                String url = String.format(Constant.URL_LIST_MARQUE, gerant.session);

                // Request a string response from the provided URL.
                StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                        new Response.Listener<String>() {
                            @Override
                            //json = parametre de reponse
                            public void onResponse(String json) {

                                Gson gson = new Gson();

                                Type listType = new TypeToken<ArrayList<Marque>>() {
                                }.getType();
                                listMarques.clear();
                                Marque m1 = new Marque();
                                m1.libelle = "Choisir une marque";
                                listMarques.add(m1);

                                listMarques.addAll((Collection<? extends Marque>) gson.fromJson(json, listType));
                                // Pour afficher la liste des marques
                                adapterMarque = new ArrayAdapter<Marque>(AddVehiculeActivity.this,
                                        android.R.layout.simple_dropdown_item_1line, listMarques);
                                spinnerMarqueAddVehicule.setAdapter(adapterMarque);

                            }

                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Toast.makeText(AddVehiculeActivity.this, "Erreur d'affichage de la liste des marques", Toast.LENGTH_SHORT).show();

                    }
                });
                // Add the request to the RequestQueue.
                queue.add(stringRequest);

            }
        } else {
            Toast.makeText(AddVehiculeActivity.this, "Impossible de faire la connexion, veuillez vous connecter", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(AddVehiculeActivity.this, HomeActivity.class);
            startActivity(intent);
        }

    }

    //Méthode permettant de récupérer la liste des marques
    public void afficherModele(final int idMarque) {

        if (gerant != null) {

            //check network available or not
            if (Network.isNetworkAvailable(AddVehiculeActivity.this)) {

                // Instantiate the RequestQueue.
                RequestQueue queue = Volley.newRequestQueue(AddVehiculeActivity.this);
                String url = String.format(Constant.URL_LIST_MARQUE_MODELES, idMarque, gerant.session);

                // Request a string response from the provided URL.
                StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                        new Response.Listener<String>() {
                            @Override
                            //json = parametre de reponse
                            public void onResponse(String json) {

                                Gson gson = new Gson();

                                Type listType = new TypeToken<ArrayList<Modele>>() {
                                }.getType();
                                listModelesMarque.clear();
                                if (idMarque > 0) {
                                    Modele m1 = new Modele();
                                    m1.libelle = "Choisir un modele";
                                    listModelesMarque.add(m1);
                                    listModelesMarque.addAll((Collection<? extends Modele>) gson.fromJson(json, listType));

                                }
                                adapterModele.notifyDataSetChanged();
                            }

                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Toast.makeText(AddVehiculeActivity.this, "Erreur d'affichage de la liste des marques", Toast.LENGTH_SHORT).show();

                    }
                });
                // Add the request to the RequestQueue.
                queue.add(stringRequest);

            }
        } else {
            Toast.makeText(AddVehiculeActivity.this, "Impossible de faire la connexion, veuillez vous connecter", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(AddVehiculeActivity.this, HomeActivity.class);
            startActivity(intent);
        }

    }
}
