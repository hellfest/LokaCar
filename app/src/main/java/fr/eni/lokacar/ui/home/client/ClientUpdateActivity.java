package fr.eni.lokacar.ui.home.client;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
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

import java.util.HashMap;
import java.util.Map;

import fr.eni.lokacar.AppActivity;
import fr.eni.lokacar.R;
import fr.eni.lokacar.modele.Client;
import fr.eni.lokacar.modele.Gerant;
import fr.eni.lokacar.modele.StatusRest;
import fr.eni.lokacar.ui.login.LoginActivity;
import fr.eni.lokacar.utils.Constant;
import fr.eni.lokacar.utils.Network;
import fr.eni.lokacar.utils.Preference;
import fr.eni.lokacar.utils.Utils;

public class ClientUpdateActivity extends AppActivity {

    private TextView textViewTitre;
    private EditText editTextNom;
    private EditText editTextPrenom;
    private EditText editTextNumPermis;
    private EditText editTextEmail;
    private EditText editTextTelephone;
    private EditText editTextAdresse;
    private EditText editTextCp;
    private EditText editTextVille;
    private Client client;
    private int id;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_form);

        textViewTitre = (TextView) findViewById(R.id.textViewTitre);
        editTextNom = (EditText) findViewById(R.id.editTextNom);
        editTextPrenom = (EditText) findViewById(R.id.editTextPrenom);
        editTextNumPermis = (EditText) findViewById(R.id.editTextNumPermis);
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextTelephone = (EditText) findViewById(R.id.editTextTelephone);
        editTextAdresse = (EditText) findViewById(R.id.editTextAdresse);
        editTextCp = (EditText) findViewById(R.id.editTextCp);
        editTextVille = (EditText) findViewById(R.id.editTextVille);

        textViewTitre.setText("Modification d'un client");

        id = getIntent().getIntExtra("client", -1);

        client = null;

        getClient();

    }


    /**
     *
     */
    public void getClient() {
        Gerant gerant = Preference.getGerant(ClientUpdateActivity.this);

        if (gerant != null) {

            //check network available or not
            if (Network.isNetworkAvailable(ClientUpdateActivity.this)) {

                // Instantiate the RequestQueue.
                RequestQueue queue = Volley.newRequestQueue(ClientUpdateActivity.this);

                String url = String.format(Constant.URL_GET_CLIENT, id, gerant.session);

                // Request a string response from the provided URL.
                StringRequest stringRequest = new StringRequest(Request.Method.PUT, url,
                        new Response.Listener<String>() {
                            @Override
                            //json = parametre de reponse
                            public void onResponse(String json) {
                                Gson gson = new Gson();
                                client = gson.fromJson(json, Client.class);
                                afficheClient();
                            }

                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Toast.makeText(ClientUpdateActivity.this, "Erreur de récupération du client", Toast.LENGTH_SHORT).show();

                    }
                });
                /*
                {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("id_marque", "500");
                        params.put("immat", "500");
                        params.put("id_marque", "500");
                        params.put("id_marque", "500");
                        return params;
                    }
                };*/

                // Add the request to the RequestQueue.
                queue.add(stringRequest);

            }
        } else {
            Toast.makeText(ClientUpdateActivity.this, "Impossible de faire la connexion, veuillez vous connecter", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(ClientUpdateActivity.this, LoginActivity.class);
            startActivity(intent);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_client_update, menu);
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

        if (id == R.id.action_clientUpdate) {
            updateClient();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void afficheClient(){
        if (client != null){
            editTextNom.setText(client.nom);
            editTextPrenom.setText(client.prenom);
            editTextNumPermis.setText(client.numero_permis);
            editTextEmail.setText(client.email);
            editTextTelephone.setText(client.telephone);
            editTextAdresse.setText(client.adresse);
            editTextCp.setText(client.cp);
            editTextVille.setText(client.ville);
        }
    }

    /**
     *
     */
    private void updateClient(){

        final String nom =  editTextNom.getText().toString();
        final String prenom =  editTextPrenom.getText().toString();
        final String permis =  editTextNumPermis.getText().toString();
        final String email =  editTextEmail.getText().toString();
        final String telephone =  editTextTelephone.getText().toString();
        final String adresse =  editTextAdresse.getText().toString();
        final String cp =  editTextCp.getText().toString();
        final String ville =  editTextVille.getText().toString();

        boolean error = false;

        if (nom== null || nom.isEmpty()){
            editTextNom.setError("Le nom est obligatoire");
            error = true;
        }

        if (prenom == null || prenom.isEmpty()){
            editTextPrenom.setError("Le prénom est obligatoire");
            error = true;
        }

        if (permis == null || permis.isEmpty()){
            editTextNumPermis.setError("Le numéro de permis est obligatoire");
            error = true;
        }

        if (email == null || email.isEmpty() ){
            editTextEmail.setError("L'email est obligatoire");
            error = true;
        }
        else if (!Utils.isEmailValid(email)){
            editTextNom.setError("L'email est mal formé");
            error = true;
        }

        if (telephone == null || telephone.isEmpty()){
            editTextTelephone.setError("Le téléphone de permis est obligatoire");
            error = true;
        }

        if (!error){
            Gerant gerant = Preference.getGerant(ClientUpdateActivity.this);

            if (gerant != null) {

                //check network available or not
                if (Network.isNetworkAvailable(ClientUpdateActivity.this)) {

                    // Instantiate the RequestQueue.
                    RequestQueue queue = Volley.newRequestQueue(ClientUpdateActivity.this);

                    String url = String.format(Constant.URL_UPDATE_CLIENT, gerant.session);

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
                                    }

                                    Toast.makeText(ClientUpdateActivity.this, status.message, Toast.LENGTH_SHORT).show();

                                }

                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                            Toast.makeText(ClientUpdateActivity.this, "Erreur de la mise à jour du client", Toast.LENGTH_SHORT).show();

                        }
                    }){
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<String, String>();
                            params.put("id", String.valueOf(client.id));
                            params.put("nom", nom);
                            params.put("prenom", prenom);
                            params.put("permis", permis);
                            params.put("email", email);
                            params.put("telephone", telephone);
                            params.put("adresse", adresse);
                            params.put("cp", cp);
                            params.put("ville", ville);
                            return params;
                        }
                    };

                    // Add the request to the RequestQueue.
                    queue.add(stringRequest);

                }
            } else {
                Toast.makeText(ClientUpdateActivity.this, "Impossible de faire la connexion, veuillez vous connecter", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ClientUpdateActivity.this, LoginActivity.class);
                startActivity(intent);
            }

        }

    }

}
