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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
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
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.eni.lokacar.R;
import fr.eni.lokacar.adpater.VehiculeAdapter;
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

import static android.app.Activity.RESULT_OK;


public class VehiculeFilterFragment extends Fragment {

    private static final int REQUEST_CODE = 200;
    private static final String TAG = "Vehicules";
    private static final int REQUEST_CODE2 = 300;
    private static final  int REQUEST_CODE3 = 400;
    ListView listViewVehicules;
    //Pour saisir marque et modèle afin de filtrer la liste
    private AutoCompleteTextView autoTextViewVehiculeMarque;
    private AutoCompleteTextView autoTextViewVehiculeModele;
    private FloatingActionButton refresh;


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

        // Pour avoir le menu du fragment depuis le fragment
        setHasOptionsMenu(true);

        listViewVehicules = (ListView) view.findViewById(R.id.listViewVehicule);

        autoTextViewVehiculeModele = (AutoCompleteTextView) view.findViewById(R.id.autoTextViewVehiculeModele);

        boutonAddVehicule = (FloatingActionButton) view.findViewById(R.id.addVehicule);
        refresh = (FloatingActionButton) view.findViewById(R.id.refresh);

        return view;

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        gerant = Preference.getGerant(getContext());

        getActivity().setTitle("Gestion des véhicules");

        //Pour créer une seule fois l'adapter
        adapterVehicule = new VehiculeAdapter(getContext(), R.layout.item_vehicule, listVehicule);
        listViewVehicules.setAdapter(adapterVehicule);


        afficherVehicules();
        afficherModele();

        // Methode d'envoi du véhicule quand click sur la ligne pour détails
        listViewVehicules.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                Intent intent = new Intent(getContext(), DetailsVehiculeActivity.class);

                // envoyer l'objet Véhicule (plusieurs objets)
                Bundle bundle = new Bundle();
                bundle.putSerializable("vehicule", listVehicule.get(position));
                intent.putExtras(bundle);
                startActivityForResult(intent,REQUEST_CODE2);
            }
        });


        // Méthode d'envoi vers le formulaire d'ajout d'un véhicule
        boutonAddVehicule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), AddVehiculeActivity.class);
                startActivityForResult(intent,REQUEST_CODE);
            }
        });

        // Méthode de rafraîchissement de la liste après une recherche
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                afficherVehicules();
                autoTextViewVehiculeModele.setText("");
            }
        });

        // Méthode pour supprimer un véhicule sur un click long
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_CODE){
            if (resultCode == RESULT_OK){

                afficherVehicules();

            }
        }
        if(requestCode == REQUEST_CODE2){
            if (resultCode == RESULT_OK){

                afficherVehicules();
                autoTextViewVehiculeModele.setText("");
            }
        }

        if(requestCode == REQUEST_CODE3){
            if (resultCode == RESULT_OK){

                searchVehicules(data.getIntExtra("modele", 0), data.getBooleanExtra("disponible", true));
                autoTextViewVehiculeModele.setText("");
            }
        }
    }

    //Méthode permettant de récupérer la liste des véhicules
    public void afficherVehicules() {

        if(listModele != null) {
            Log.e(TAG, "listModele: " + listModele.size());
        }

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

                                adapterVehicule.setOriginalData(listVehicule);
                                adapterVehicule.notifyDataSetChanged();
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

    //Méthode permettant de récupérer la liste des marques
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

                                Type listType = new TypeToken<ArrayList<Modele>>() {
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_fragment_vehicule, menu);
        super.onCreateOptionsMenu(menu, inflater);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();



        if (id == R.id.action_vehicule_search) {
            Intent intent = new Intent(getContext(), SearchVehiculeActivity.class);
            startActivityForResult(intent,REQUEST_CODE3);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //Méthode permettant de récupérer la liste des véhicules par recherche
    public void searchVehicules(final int modele_id, final boolean disponible) {


            if (gerant != null) {

                //check network available or not
                if (Network.isNetworkAvailable(getContext())) {

                    // Instantiate the RequestQueue.
                    RequestQueue queue = Volley.newRequestQueue(getContext());
                    String url = String.format(Constant.URL_SEARCH_VEHICULE, gerant.session);

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
                                    adapterVehicule.setOriginalData(listVehicule);

                                    adapterVehicule.notifyDataSetChanged();
                                    if (listVehicule.size() < 1)
                                    {
                                        Toast.makeText(getContext(), "Pas de véhicule correspondant aux critères", Toast.LENGTH_SHORT).show();
                                    }
                                }

                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                            Toast.makeText(getContext(), "Erreur d'affichage de la liste", Toast.LENGTH_SHORT).show();

                        }
                    }) {
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String, String> params = new HashMap<String, String>();
                            params.put("modele", String.valueOf(modele_id));
                            params.put("disponible", (disponible) ? "1" : "0");
                            params.put("agence", String.valueOf(gerant.agence.id));
                            return params;
                        }
                    };
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

