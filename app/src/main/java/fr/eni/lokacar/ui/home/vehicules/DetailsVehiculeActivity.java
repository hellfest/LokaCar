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
import android.widget.GridView;
import android.widget.LinearLayout;
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
import fr.eni.lokacar.modele.Vehicule;
import fr.eni.lokacar.ui.home.HomeActivity;
import fr.eni.lokacar.ui.home.client.ClientUpdateActivity;
import fr.eni.lokacar.ui.login.LoginActivity;
import fr.eni.lokacar.utils.Constant;
import fr.eni.lokacar.utils.Network;
import fr.eni.lokacar.utils.Preference;
import fr.eni.lokacar.utils.Utils;

public class DetailsVehiculeActivity extends AppActivity {

    private TextView textViewTitreAjoutVehicule;
    private Spinner spinnerMarqueAddVehicule;
    private Spinner spinnerModeleAddVehicule;
    private EditText editTextImmatriculation;
    private LinearLayout linearLayoutDisponible;
    private CheckBox chekBoxDisponible;
    private LinearLayout linearLayoutListViewPhoto;
    private GridView listViewPhotosVehicule;
    private Button boutonValiderVehicule;

    private Vehicule vehicule;
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

        textViewTitreAjoutVehicule = (TextView) findViewById(R.id.textViewTitreAjoutVehicule);
        spinnerMarqueAddVehicule = (Spinner) findViewById(R.id.spinnerMarqueAddVehicule);
        spinnerModeleAddVehicule = (Spinner) findViewById(R.id.spinnerModeleAddVehicule);
        editTextImmatriculation = (EditText) findViewById(R.id.editTextImmatriculation);
        linearLayoutDisponible = (LinearLayout) findViewById(R.id.linearLayoutDisponible);
        chekBoxDisponible = (CheckBox) findViewById(R.id.chekBoxDisponible);
        linearLayoutListViewPhoto = (LinearLayout) findViewById(R.id.linearLayoutListViewPhoto);
        listViewPhotosVehicule = (GridView) findViewById(R.id.listViewPhotosVehicule);
        boutonValiderVehicule = (Button) findViewById(R.id.boutonValiderVehicule);

        linearLayoutListViewPhoto.setVisibility(View.VISIBLE);

        textViewTitreAjoutVehicule.setText("Formulaire Modification d'un véhicule");

        //Récupération du gérant connecté
        gerant = Preference.getGerant(DetailsVehiculeActivity.this);

        //Récupération du véhicule
        if (getIntent().getExtras() != null) {
            Intent intent = getIntent();

            vehicule = (Vehicule) getIntent().getExtras().get("vehicule");

            Log.e("erreur dispo", "disponibilité: " + vehicule.disponible);

            // Récupération de la marque sélectionnée dans la liste
            spinnerMarqueAddVehicule.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                    marque = (Marque) adapterView.getItemAtPosition(position);

                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                }
            });

            // Récupération du modèle  sélectionné dans la liste
            spinnerModeleAddVehicule.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                    modele = (Modele) adapterView.getItemAtPosition(position);
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });


            adapterModele = new ArrayAdapter<Modele>(DetailsVehiculeActivity.this,
                    android.R.layout.simple_dropdown_item_1line, listModelesMarque);
            spinnerModeleAddVehicule.setAdapter(adapterModele);

            //Affichage de la marque et du modèle du véhicule sélectionné dans la liste
            afficherMarque();

            //Récupération des autres données du véhicule sélectionné dans la liste
            editTextImmatriculation.setText(vehicule.immatriculation);
            chekBoxDisponible.setChecked(vehicule.disponible);

            //TODO AJOUTER LA RECUPERATION DE PHOTOS

        }
    }


    public void onClickBoutonValiderVehicule(View view) {
        updateVehicule();
    }

    //Méthode permettant de récupérer la liste des marques
    public void afficherMarque() {

        if (gerant != null) {

            //check network available or not
            if (Network.isNetworkAvailable(DetailsVehiculeActivity.this)) {

                // Instantiate the RequestQueue.
                RequestQueue queue = Volley.newRequestQueue(DetailsVehiculeActivity.this);
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
                                listMarques.addAll((Collection<? extends Marque>) gson.fromJson(json, listType));


                                // Pour afficher la liste des marques
                                adapterMarque = new ArrayAdapter<Marque>(DetailsVehiculeActivity.this,
                                        android.R.layout.simple_dropdown_item_1line, listMarques);
                                spinnerMarqueAddVehicule.setAdapter(adapterMarque);

                                for (int j = 0; j < listMarques.size(); j++) {

                                    if (listMarques.get(j).libelle.equalsIgnoreCase(vehicule.modele.marque.libelle)) {
                                        spinnerMarqueAddVehicule.setSelection(j);
                                        afficherModele(vehicule.modele.marque.id);
                                        break;
                                    }
                                }
                            }

                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Toast.makeText(DetailsVehiculeActivity.this, "Erreur d'affichage de la liste des marques", Toast.LENGTH_SHORT).show();

                    }
                });
                // Add the request to the RequestQueue.
                queue.add(stringRequest);

            }
        } else {
            Toast.makeText(DetailsVehiculeActivity.this, "Impossible de faire la connexion, veuillez vous connecter", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(DetailsVehiculeActivity.this, HomeActivity.class);
            startActivity(intent);
        }

    }

    //Méthode permettant de récupérer la liste des modèles par marque
    public void afficherModele(final int idMarque) {

        if (gerant != null) {

            //check network available or not
            if (Network.isNetworkAvailable(DetailsVehiculeActivity.this)) {

                // Instantiate the RequestQueue.
                RequestQueue queue = Volley.newRequestQueue(DetailsVehiculeActivity.this);
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
                                listModelesMarque.addAll((Collection<? extends Modele>) gson.fromJson(json, listType));
                                for (int j = 0; j < listModelesMarque.size(); j++) {

                                    if (listModelesMarque.get(j).libelle.equalsIgnoreCase(vehicule.modele.libelle)) {
                                        spinnerModeleAddVehicule.setSelection(j);
                                        break;
                                    }
                                }
                                adapterModele.notifyDataSetChanged();
                            }

                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Toast.makeText(DetailsVehiculeActivity.this, "Erreur d'affichage de la liste des marques", Toast.LENGTH_SHORT).show();

                    }
                });
                // Add the request to the RequestQueue.
                queue.add(stringRequest);

            }
        } else {
            Toast.makeText(DetailsVehiculeActivity.this, "Impossible de faire la connexion, veuillez vous connecter", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(DetailsVehiculeActivity.this, HomeActivity.class);
            startActivity(intent);
        }

    }

    /**
     *
     */
    private void updateVehicule() {

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
            error = true;}


            if (!error) {
                //final Gerant gerant = Preference.getGerant(DetailsVehiculeActivity.this);
                if (gerant != null) {

                    //check network available or not
                    if (Network.isNetworkAvailable(DetailsVehiculeActivity.this)) {

                        // Instantiate the RequestQueue.
                        RequestQueue queue = Volley.newRequestQueue(DetailsVehiculeActivity.this);

                        String url = String.format(Constant.URL_UPDATE_VEHICULE, gerant.session);

                        // Request a string response from the provided URL.
                        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                                new Response.Listener<String>() {
                                    @Override
                                    //json = parametre de reponse
                                    public void onResponse(String json) {
                                        Gson gson = new Gson();
                                        StatusRest status = gson.fromJson(json, StatusRest.class);
                                        if (status.status) {

                                        }

                                        Toast.makeText(DetailsVehiculeActivity.this, status.message, Toast.LENGTH_SHORT).show();

                                    }

                                }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {

                                Toast.makeText(DetailsVehiculeActivity.this, R.string.client_erreur_update, Toast.LENGTH_SHORT).show();

                            }
                        }) {
                            @Override
                            protected Map<String, String> getParams() throws AuthFailureError {
                                Map<String, String> params = new HashMap<String, String>();
                                params.put("id", String.valueOf(vehicule.id));
                                params.put("modele", String.valueOf(modele_id));
                                params.put("immatriculation", immatriculation);
                                params.put("disponible", (disponible) ? "1" : "0");
                                params.put("agence", String.valueOf(gerant.agence.id));
                                //TODO AJOUTER L'ENVOIE DE PHOTOS
                                return params;
                            }
                        };

                        // Add the request to the RequestQueue.
                        queue.add(stringRequest);

                    }
                } else {
                    Toast.makeText(DetailsVehiculeActivity.this, R.string.client_error_insert, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(DetailsVehiculeActivity.this, LoginActivity.class);
                    startActivity(intent);
                }

            }

        }

    }

