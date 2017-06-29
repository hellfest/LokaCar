package fr.eni.lokacar.ui.home.location;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.eni.lokacar.R;
import fr.eni.lokacar.adpater.ClientAdapter;
import fr.eni.lokacar.adpater.VehiculeLocationAdapter;
import fr.eni.lokacar.modele.Categorie;
import fr.eni.lokacar.modele.Client;
import fr.eni.lokacar.modele.Gerant;
import fr.eni.lokacar.modele.Marque;
import fr.eni.lokacar.modele.Modele;
import fr.eni.lokacar.modele.StatusRest;
import fr.eni.lokacar.modele.Vehicule;
import fr.eni.lokacar.ui.home.client.ClientUpdateActivity;
import fr.eni.lokacar.ui.login.LoginActivity;
import fr.eni.lokacar.utils.Constant;
import fr.eni.lokacar.utils.Network;
import fr.eni.lokacar.utils.Preference;


public class LocationFragment extends Fragment {

    private Spinner spinnerCategorie;
    private Spinner spinnerModele;
    private ListView listViewLocationVehicule;
    private ArrayAdapter<Categorie> adapterCategorie;
    private ArrayAdapter<Modele> adapterModele;
    private List<Categorie> listeCategorie = new ArrayList<Categorie>();
    private List<Modele> listeModele = new ArrayList<Modele>();
    private List<Vehicule> listeVehicule = new ArrayList<Vehicule>();
    private VehiculeLocationAdapter adapterVehicule;
    private Gerant gerant;



    public LocationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_location, container, false);



        gerant = Preference.getGerant(getContext());

        spinnerCategorie = (Spinner) view.findViewById(R.id.spinnerCategorie);
        spinnerModele = (Spinner) view.findViewById(R.id.spinnerModele);
        listViewLocationVehicule = (ListView) view.findViewById(R.id.listViewLocationVehicule);

        getListCategories(null);
        getListModeles(null);

        adapterCategorie = new ArrayAdapter<>(getContext(),android.R.layout.simple_list_item_1,listeCategorie);
        adapterModele = new ArrayAdapter<>(getContext(),android.R.layout.simple_list_item_1,listeModele);
        adapterVehicule = new VehiculeLocationAdapter(getContext(),R.layout.item_vehicule_location,listeVehicule);

        listViewLocationVehicule.setAdapter(adapterVehicule);
        spinnerCategorie.setAdapter(adapterCategorie);

        spinnerCategorie.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Categorie categorie = (Categorie) parent.getItemAtPosition(position);

                if (categorie.id > 0){
                    spinnerModele.setEnabled(true);
                    chargeModele(categorie.id);
                }
                else{
                    spinnerModele.setEnabled(false);
                    getListModeles(null);
                    adapterModele.notifyDataSetChanged();
                    spinnerModele.setSelection(-1);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                spinnerModele.setEnabled(false);
                getListModeles(null);
                adapterModele.notifyDataSetChanged();
                spinnerModele.setSelection(-1);
            }
        });

        spinnerModele.setAdapter(adapterModele);
        spinnerModele.setEnabled(false);

        spinnerModele.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Modele modele = (Modele) parent.getItemAtPosition(position);

                if (modele.id > 0){
                    chargeVehicule(modele.id);
                }
                else{
                    listeVehicule.clear();
                    adapterVehicule.notifyDataSetChanged();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        listViewLocationVehicule.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Vehicule vehicule = (Vehicule) parent.getItemAtPosition(position);

                final Client client = Preference.getClient(getContext());

                if (client != null){
                    AlertDialog.Builder builder;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        builder = new AlertDialog.Builder(getContext(), android.R.style.Theme_Material_Dialog_Alert);
                    } else {
                        builder = new AlertDialog.Builder(getContext());
                    }
                    builder.setTitle("Client en mémoire")
                            .setMessage("Voulez-vous louer ce véhicule au client en mémoire ?\n"
                                    + client.nom +" "+client.prenom)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    locationVehicule(client,vehicule);
                                }
                            })
                            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    //TODO : Recherche du client
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }
                else{

                }

            }
        });

        chargeCategorie();

        // Inflate the layout for this fragment
        return view;
    }


    protected void getListCategories(List<Categorie> liste){
        listeCategorie.clear();
        List<Categorie> lst = null;
        if (liste == null) {
            lst = new ArrayList<>();
        }
        else{
            lst = new ArrayList<>(liste);
        }

        Categorie def = new Categorie();
        def.libelle = "Choisissez une catégorie";

        Collections.reverse(lst);
        lst.add(def);
        Collections.reverse(lst);

        listeCategorie.addAll(lst);

    }

    protected void getListModeles(List<Modele> liste){
        listeModele.clear();
        List<Modele> lst = null;
        if (liste == null) {
            lst = new ArrayList<>();
        }
        else{
            lst = new ArrayList<>(liste);
        }

        Modele def = new Modele();
        def.libelle = "Choisissez un modèle";

        Collections.reverse(lst);
        lst.add(def);
        Collections.reverse(lst);

        listeModele.addAll(lst);

    }

    protected void chargeCategorie(){
        if (gerant != null) {

            //check network available or not
            if (Network.isNetworkAvailable(getContext())) {

                // Instantiate the RequestQueue.
                RequestQueue queue = Volley.newRequestQueue(getContext());

                String url = String.format(Constant.URL_LIST_CATEGORIES, gerant.session);

                // Request a string response from the provided URL.
                StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                        new Response.Listener<String>() {
                            @Override
                            //json = parametre de reponse
                            public void onResponse(String json) {

                                Gson gson = new Gson();

                                Type listType = new TypeToken<ArrayList<Categorie>>(){}.getType();

                                List<Categorie> liste = gson.fromJson(json, listType);

                                getListCategories(liste);

                                adapterCategorie.notifyDataSetChanged();

                            }

                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Toast.makeText(getContext(), R.string.location_error_liste_marque, Toast.LENGTH_SHORT).show();

                    }
                });
                // Add the request to the RequestQueue.
                queue.add(stringRequest);

            }
        } else {
            Toast.makeText(getContext(), R.string.error_connexion_gerant, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getContext(), LoginActivity.class);
            startActivity(intent);
        }
    }


    protected void chargeModele(int categorieId){
        if (gerant != null) {

            //check network available or not
            if (Network.isNetworkAvailable(getContext())) {

                // Instantiate the RequestQueue.
                RequestQueue queue = Volley.newRequestQueue(getContext());

                String url = String.format(Constant.URL_LIST_MODELES_CATEGORIE, categorieId,gerant.session);

                // Request a string response from the provided URL.
                StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                        new Response.Listener<String>() {
                            @Override
                            //json = parametre de reponse
                            public void onResponse(String json) {

                                Gson gson = new Gson();

                                Type listType = new TypeToken<ArrayList<Modele>>(){}.getType();

                                List<Modele> liste = gson.fromJson(json, listType);

                                getListModeles(liste);

                                spinnerModele.setSelection(-1);
                                adapterModele.notifyDataSetChanged();

                            }

                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Toast.makeText(getContext(), R.string.location_error_liste_modele, Toast.LENGTH_SHORT).show();

                    }
                });
                // Add the request to the RequestQueue.
                queue.add(stringRequest);

            }
        } else {
            Toast.makeText(getContext(), R.string.error_connexion_gerant, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getContext(), LoginActivity.class);
            startActivity(intent);
        }
    }


    protected void chargeVehicule(int id){
        if (gerant != null) {

            //check network available or not
            if (Network.isNetworkAvailable(getContext())) {

                // Instantiate the RequestQueue.
                RequestQueue queue = Volley.newRequestQueue(getContext());

                String url = String.format(Constant.URL_LIST_LOCATION_VEHICULE, id,gerant.session);

                // Request a string response from the provided URL.
                StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                        new Response.Listener<String>() {
                            @Override
                            //json = parametre de reponse
                            public void onResponse(String json) {

                                Gson gson = new Gson();

                                Type listType = new TypeToken<ArrayList<Vehicule>>(){}.getType();
                                listeVehicule.clear();
                                listeVehicule.addAll((Collection<? extends Vehicule>) gson.fromJson(json, listType));

                                adapterVehicule.notifyDataSetChanged();

                            }

                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Toast.makeText(getContext(), R.string.location_error_liste_vehicule, Toast.LENGTH_SHORT).show();

                    }
                });
                // Add the request to the RequestQueue.
                queue.add(stringRequest);

            }
        } else {
            Toast.makeText(getContext(), R.string.error_connexion_gerant, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getContext(), LoginActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().setTitle("Location véhicule");
    }

    protected void locationVehicule(final Client client, final Vehicule vehicule){
        if (gerant != null) {

            //check network available or not
            if (Network.isNetworkAvailable(getContext())) {

                // Instantiate the RequestQueue.
                RequestQueue queue = Volley.newRequestQueue(getContext());

                String url = String.format(Constant.URL_LOCATION_INSERT, gerant.session);

                // Request a string response from the provided URL.
                StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                        new Response.Listener<String>() {
                            @Override
                            //json = parametre de reponse
                            public void onResponse(String json) {

                                Gson gson = new Gson();
                                StatusRest status = gson.fromJson(json, StatusRest.class);
                                if (status.status) {
                                    //Suppression du client en mémoire
                                    Preference.removeClient(getContext());

                                    Intent intent = new Intent(getContext(),LocationFormActivity.class);
                                    intent.putExtra("location", status.id);
                                    startActivity(intent);

                                }

                            }

                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Toast.makeText(getContext(), "Erreur enregistrement location", Toast.LENGTH_SHORT).show();

                    }
                }){
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("vehicule_id", String.valueOf(vehicule.id));
                        params.put("client_id", String.valueOf(client.id));

                        return params;
                    }
                };
                // Add the request to the RequestQueue.
                queue.add(stringRequest);

            }
        } else {
            Toast.makeText(getContext(), R.string.error_connexion_gerant, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getContext(), LoginActivity.class);
            startActivity(intent);
        }
    }
}
