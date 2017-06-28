package fr.eni.lokacar.ui.home.client;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
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
import fr.eni.lokacar.adpater.ClientAdapter;
import fr.eni.lokacar.modele.Client;
import fr.eni.lokacar.modele.Gerant;
import fr.eni.lokacar.ui.login.LoginActivity;
import fr.eni.lokacar.utils.Constant;
import fr.eni.lokacar.utils.Network;
import fr.eni.lokacar.utils.Preference;


public class ClientFragment extends Fragment {

    private List<Client> listClients = new ArrayList<>();
    ListView listViewClients;
    EditText editTextClient;
    ClientAdapter adapter;

    public ClientFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_client, container, false);

        listViewClients = (ListView) view.findViewById(R.id.listViewClient);
        editTextClient = (EditText) view.findViewById(R.id.editTextClient);

        editTextClient.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.toString().length() >= 3){
                    afficherClients();
                }

                if (s.toString().length() < 3){
                    listClients.clear();
                    adapter.notifyDataSetChanged();
                }
            }
        });

        return view;

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        adapter = new ClientAdapter(getContext(), R.layout.item_client, listClients);
        listViewClients.setAdapter(adapter);
    }

    public void afficherClients() {
        Gerant gerant = Preference.getGerant(getContext());

        if (gerant != null) {

            //check network available or not
            if (Network.isNetworkAvailable(getContext())) {

                // Instantiate the RequestQueue.
                RequestQueue queue = Volley.newRequestQueue(getContext());

                String search = "--";
                if (!editTextClient.getText().toString().isEmpty()){
                    search = editTextClient.getText().toString();
                }

                String url = String.format(Constant.URL_LIST_SEARCH_CLIENTS, search, gerant.session);

                // Request a string response from the provided URL.
                StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                        new Response.Listener<String>() {
                            @Override
                            //json = parametre de reponse
                            public void onResponse(String json) {

                                Gson gson = new Gson();

                                Type listType = new TypeToken<ArrayList<Client>>() {
                                }.getType();
                                listClients = gson.fromJson(json, listType);

                                adapter.notifyDataSetChanged();

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
}
