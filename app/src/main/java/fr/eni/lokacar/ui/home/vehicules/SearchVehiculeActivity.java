package fr.eni.lokacar.ui.home.vehicules;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
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

import fr.eni.lokacar.R;
import fr.eni.lokacar.modele.Gerant;
import fr.eni.lokacar.modele.Marque;
import fr.eni.lokacar.modele.Modele;
import fr.eni.lokacar.modele.StatusRest;
import fr.eni.lokacar.modele.Vehicule;
import fr.eni.lokacar.ui.home.HomeActivity;
import fr.eni.lokacar.ui.login.LoginActivity;
import fr.eni.lokacar.utils.Constant;
import fr.eni.lokacar.utils.Network;
import fr.eni.lokacar.utils.Preference;

public class SearchVehiculeActivity extends AppCompatActivity {

    private Spinner spinnerMarqueAddVehicule;
    private Spinner spinnerModeleAddVehicule;
    private EditText editTextImmatriculation;
    private CheckBox chekBoxDisponible;
    private TextView textViewTitreAjoutVehicule;
    private LinearLayout linearLayoutListViewPhoto;
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
        chekBoxDisponible = (CheckBox) findViewById(R.id.chekBoxDisponible);
        textViewTitreAjoutVehicule = (TextView) findViewById(R.id.textViewTitreAjoutVehicule);
        linearLayoutListViewPhoto = (LinearLayout) findViewById(R.id.linearLayoutListViewPhoto);

        textViewTitreAjoutVehicule.setText("Formulaire de Recherche d'un véhicule");
        linearLayoutListViewPhoto.setVisibility(View.INVISIBLE);
        editTextImmatriculation.setVisibility(View.GONE);


        //Récupération du gérant connecté
        gerant = Preference.getGerant(SearchVehiculeActivity.this);

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

        // Pour afficher la liste des marques
        adapterMarque = new ArrayAdapter<Marque>(SearchVehiculeActivity.this,
                android.R.layout.simple_dropdown_item_1line, listMarques);
        spinnerMarqueAddVehicule.setAdapter(adapterMarque);
        // Pour afficher la liste des modèles en fonction de la marque
        adapterModele = new ArrayAdapter<Modele>(SearchVehiculeActivity.this,
                android.R.layout.simple_dropdown_item_1line, listModelesMarque);
        spinnerModeleAddVehicule.setAdapter(adapterModele);

        afficherMarque();

    }

    //Méthode de validation des valeurs sélectionnées
    public void onClickBoutonValiderVehicule(View view) {

        searchVehicules();
    }


    //Méthode pour renvoyer les critères de recherche au fragment
    public void searchVehicules() {
        final boolean disponible;
        if (chekBoxDisponible.isChecked()) {
            disponible = true;
        } else {
            disponible = false;
        }

        if (modele != null && modele.id > 0) {

            final int modele_id = modele.id;

            Intent intent = new Intent();
            intent.putExtra("modele", modele_id);
            intent.putExtra("disponible", disponible);
            setResult(RESULT_OK, intent);

        } else {
            Intent intent = new Intent();
            intent.putExtra("modele", -1);
            intent.putExtra("disponible", disponible);
            setResult(RESULT_OK, intent);

            //Toast.makeText(this, "La saisie du modèle est obligatoire", Toast.LENGTH_SHORT).show();
        }
        onBackPressed();
    }


    //Méthode permettant de récupérer la liste des marques
    public void afficherMarque() {

        if (gerant != null) {

            //check network available or not
            if (Network.isNetworkAvailable(SearchVehiculeActivity.this)) {

                // Instantiate the RequestQueue.
                RequestQueue queue = Volley.newRequestQueue(SearchVehiculeActivity.this);
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

                                adapterMarque.notifyDataSetChanged();
                            }

                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Toast.makeText(SearchVehiculeActivity.this, "Erreur d'affichage de la liste des marques", Toast.LENGTH_SHORT).show();

                    }
                });
                // Add the request to the RequestQueue.
                queue.add(stringRequest);

            }
        } else {
            Toast.makeText(SearchVehiculeActivity.this, "Impossible de faire la connexion, veuillez vous connecter", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(SearchVehiculeActivity.this, HomeActivity.class);
            startActivity(intent);
        }

    }

    //Méthode permettant de récupérer la liste des marques
    public void afficherModele(final int idMarque) {

        if (gerant != null) {

            //check network available or not
            if (Network.isNetworkAvailable(SearchVehiculeActivity.this)) {

                // Instantiate the RequestQueue.
                RequestQueue queue = Volley.newRequestQueue(SearchVehiculeActivity.this);
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

                        Toast.makeText(SearchVehiculeActivity.this, "Erreur d'affichage de la liste des marques", Toast.LENGTH_SHORT).show();

                    }
                });
                // Add the request to the RequestQueue.
                queue.add(stringRequest);

            }
        } else {
            Toast.makeText(SearchVehiculeActivity.this, "Impossible de faire la connexion, veuillez vous connecter", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(SearchVehiculeActivity.this, HomeActivity.class);
            startActivity(intent);
        }

    }

}

