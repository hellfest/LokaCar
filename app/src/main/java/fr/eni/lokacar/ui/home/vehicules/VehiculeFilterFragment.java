package fr.eni.lokacar.ui.home.vehicules;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.List;

import fr.eni.lokacar.R;
import fr.eni.lokacar.adpater.VehiculeAdapter;
import fr.eni.lokacar.modele.Gerant;
import fr.eni.lokacar.modele.Marque;
import fr.eni.lokacar.modele.Vehicule;
import fr.eni.lokacar.ui.login.LoginActivity;
import fr.eni.lokacar.utils.Constant;
import fr.eni.lokacar.utils.Network;
import fr.eni.lokacar.utils.Preference;


public class VehiculeFilterFragment extends Fragment {
    private static final String TAG = "Vehicules";
    ListView listViewVehicules;
    //Pour saisir marque et modèle afin de filtrer la liste
    private AutoCompleteTextView autoTextViewVehiculeMarque;
    private AutoCompleteTextView autoTextViewVehiculeModele;

    private Gerant gerant;

    private List<Marque> listMarque= new ArrayList<>();
    private ArrayAdapter<Marque> adapterMarque;
    private String libelleMarque;
    private List<Vehicule> listVehicule = new ArrayList<>();
    private VehiculeAdapter adapterVehicule;


    public VehiculeFilterFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_vehicule, container, false);

        listViewVehicules = (ListView) view.findViewById(R.id.listViewVehicule);
        autoTextViewVehiculeMarque = (AutoCompleteTextView) view.findViewById(R.id.autoTextViewVehiculeMarque);
        autoTextViewVehiculeModele = (AutoCompleteTextView) view.findViewById(R.id.autoTextViewVehiculeModele);

        return view;

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        gerant = Preference.getGerant(getContext());

        afficherVehicules();
        afficherMarque();
        //TODO GERER LES PARAMETRES
       // afficherVehicules(null,null);

        
        listViewVehicules.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                Intent intent = new Intent(getContext(), DetailsVehiculeActivity.class);

                // envoyer l'objet Véhicule (plusieurs objets)
                Bundle bundle = new Bundle();
                bundle.putSerializable("vehicule", listVehicule.get(position));
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    //Méthode permettant de récupérer la liste des véhicules
    public void afficherVehicules() {

        if (gerant != null) {

            //check network available or not
            if (Network.isNetworkAvailable(getContext())) {

                // Instantiate the RequestQueue.
                RequestQueue queue = Volley.newRequestQueue(getContext());
                String url = String.format(Constant.URL_LIST_VEHICULE, gerant.session);

                // Request a string response from the provided URL.
                StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                        new Response.Listener<String>() {
                            @Override
                            //json = parametre de reponse
                            public void onResponse(String json) {

                                Gson gson = new Gson();

                                Type listType = new TypeToken<ArrayList<Vehicule>>() {
                                }.getType();
                                listVehicule.clear();
                                listVehicule.addAll((Collection<? extends Vehicule>) gson.fromJson(json, listType));
                                adapterVehicule = new VehiculeAdapter(getContext(), R.layout.item_vehicule, listVehicule);
                                listViewVehicules.setAdapter(adapterVehicule);

                                //adapterMarque.notifyDataSetChanged();
                            }

                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Toast.makeText(getContext(), "Erreur d'affichage de la liste", Toast.LENGTH_SHORT).show();

                    }
                });
                // Add the request to the RequestQueue.
                queue.add(stringRequest);

            }
        } else {
            Toast.makeText(getContext(), "Impossible de faire la connexion, veuillez vous connecter", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getContext(), LoginActivity.class);
            startActivity(intent);
        }

    }

    //Méthode permettant de récupérer la liste des véhicules
    public void afficherMarque() {
        
        if (gerant != null) {

            //check network available or not
            if (Network.isNetworkAvailable(getContext())) {

                // Instantiate the RequestQueue.
                RequestQueue queue = Volley.newRequestQueue(getContext());
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
                                listMarque.clear();
                                listMarque.addAll((Collection<? extends Marque>) gson.fromJson(json, listType));
                                // Pour afficher la liste des marques
                                adapterMarque = new ArrayAdapter<Marque>(getContext(),
                                        android.R.layout.simple_dropdown_item_1line, listMarque);
                                autoTextViewVehiculeMarque.setAdapter(adapterMarque);

                                autoTextViewVehiculeMarque.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                                        Log.e(TAG, "view: "+((TextView) view).getText().toString());

                                        adapterVehicule.getFilter().filter(((TextView) view).getText().toString());
                                        // TODO CREER REQUËTE LISTE VEHICULE WHERE MARQUE = et WHERE MODELE =
                                        //TO DO repasser la methode afficherVehicule
                                        //afficherVehicules(libelleMarque);
                                    }
                                });
                            }

                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Toast.makeText(getContext(), "Erreur d'affichage de la liste des marques", Toast.LENGTH_SHORT).show();

                    }
                });
                // Add the request to the RequestQueue.
                queue.add(stringRequest);

            }
        } else {
            Toast.makeText(getContext(), "Impossible de faire la connexion, veuillez vous connecter", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getContext(), LoginActivity.class);
            startActivity(intent);
        }

    }

}
