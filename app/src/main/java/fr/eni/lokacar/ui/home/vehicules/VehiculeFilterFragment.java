package fr.eni.lokacar.ui.home.vehicules;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
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
import fr.eni.lokacar.modele.Modele;
import fr.eni.lokacar.modele.StatusRest;
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

    private FloatingActionButton boutonAddVehicule;

    private Gerant gerant;

    private List<Modele> listModele = new ArrayList<>();
    private ArrayAdapter<Modele> adapterModele;
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
       // autoTextViewVehiculeMarque = (AutoCompleteTextView) view.findViewById(R.id.autoTextViewVehiculeMarque);
        autoTextViewVehiculeModele = (AutoCompleteTextView) view.findViewById(R.id.autoTextViewVehiculeModele);

        boutonAddVehicule = (FloatingActionButton) view.findViewById(R.id.addVehicule);

        return view;

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        gerant = Preference.getGerant(getContext());

        afficherVehicules();
        afficherModele();

        
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

        boutonAddVehicule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), AddVehiculeActivity.class);
                startActivity(intent);
            }
        });

        listViewVehicules.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                final Vehicule vehicule = (Vehicule) parent.getItemAtPosition(position);

                AlertDialog.Builder builder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = new AlertDialog.Builder(getContext(), android.R.style.Theme_Material_Dialog_Alert);
                } else {
                    builder = new AlertDialog.Builder(getContext());
                }
                builder.setTitle("Supprimer un véhicule")
                        .setMessage("Etes vous sur de vouloir supprimer ce véhicule?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // continue with delete
                                deleteVehicule(vehicule.id);
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();


                return true;
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
    public void afficherModele() {
        
        if (gerant != null) {

            //check network available or not
            if (Network.isNetworkAvailable(getContext())) {

                // Instantiate the RequestQueue.
                RequestQueue queue = Volley.newRequestQueue(getContext());
                String url = String.format(Constant.URL_LIST_MODELE, gerant.session);

                // Request a string response from the provided URL.
                StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                        new Response.Listener<String>() {
                            @Override
                            //json = parametre de reponse
                            public void onResponse(String json) {

                                Gson gson = new Gson();

                                Type listType = new TypeToken<ArrayList<Marque>>() {
                                }.getType();
                                listModele.clear();
                                listModele.addAll((Collection<? extends Modele>) gson.fromJson(json, listType));
                                // Pour afficher la liste des marques
                                adapterModele = new ArrayAdapter<Modele>(getContext(),
                                        android.R.layout.simple_dropdown_item_1line, listModele);
                                autoTextViewVehiculeModele.setAdapter(adapterModele);

                                autoTextViewVehiculeModele.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                                        Log.e(TAG, "view: "+((TextView) view).getText().toString());

                                        //afficherVehicules(libelleMarque);
                                        adapterVehicule.getFilter().filter(((TextView) view).getText().toString());

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

    /**
     *Méthode de suppression d'un véhicule
     */
    public void deleteVehicule(int id) {
        Gerant gerant = Preference.getGerant(getContext());

        if (gerant != null) {

            //check network available or not
            if (Network.isNetworkAvailable(getContext())) {

                // Instantiate the RequestQueue.
                RequestQueue queue = Volley.newRequestQueue(getContext());

                String url = String.format(Constant.URL_DELETE_VEHICULE, id, gerant.session);

                // Request a string response from the provided URL.
                StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                        new Response.Listener<String>() {
                            @Override
                            //json = parametre de reponse
                            public void onResponse(String json) {

                                Gson gson = new Gson();

                                StatusRest status = gson.fromJson(json, StatusRest.class);

                                if (status.status){
                                    afficherVehicules();
                                }

                                Toast.makeText(getContext(), status.message, Toast.LENGTH_SHORT).show();


                            }

                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Toast.makeText(getContext(), "Erreur d'accès au serveur", Toast.LENGTH_SHORT).show();

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
