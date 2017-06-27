package fr.eni.lokacar.ui.home.vehicules;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
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
import java.util.List;

import fr.eni.lokacar.R;
import fr.eni.lokacar.adpater.VehiculeAdapter;
import fr.eni.lokacar.modele.Gerant;
import fr.eni.lokacar.modele.Marque;
import fr.eni.lokacar.modele.Modele;
import fr.eni.lokacar.modele.Vehicule;
import fr.eni.lokacar.ui.home.HomeActivity;
import fr.eni.lokacar.ui.login.LoginActivity;
import fr.eni.lokacar.utils.Constant;
import fr.eni.lokacar.utils.Network;
import fr.eni.lokacar.utils.Preference;


public class VehiculeFragment extends Fragment {
    ListView listViewVehicules;
    private List<Vehicule> listVehicule = new ArrayList<>();

    public VehiculeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_vehicule, container, false);

        listViewVehicules = (ListView) view.findViewById(R.id.listViewVehicule);

        return view;


    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        afficherVehicules();
    }

    public void afficherVehicules() {
        Gerant gerant = Preference.getGerant(getContext());

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
                                listVehicule = gson.fromJson(json, listType);
                                listViewVehicules.setAdapter(new VehiculeAdapter(getContext(), R.layout.item_vehicule, listVehicule));
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

//        Vehicule v1 = new Vehicule();
//        Modele m1 = new Modele();
//        v1.modele = m1;
//        Marque marque = new Marque();
//        v1.modele.marque = marque;
//        v1.immatriculation="rrr-123-cdd";
//        v1.modele.libelle="1008";
//        v1.modele.marque.libelle="Peugeot";
//        listVehicule.add(v1);
        // adapter de la liste


    }


}
