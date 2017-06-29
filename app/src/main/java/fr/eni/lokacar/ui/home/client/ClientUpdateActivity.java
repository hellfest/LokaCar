package fr.eni.lokacar.ui.home.client;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
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

                        Toast.makeText(ClientUpdateActivity.this, R.string.client_error_get, Toast.LENGTH_SHORT).show();

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
            Toast.makeText(ClientUpdateActivity.this, R.string.client_error_insert, Toast.LENGTH_SHORT).show();
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

        if (id == R.id.action_clientLocation) {
            locationClient();
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
            editTextNom.setError(getString(R.string.client_error_nom));
            error = true;
        }

        if (prenom == null || prenom.isEmpty()){
            editTextPrenom.setError(getString(R.string.client_error_prenom));
            error = true;
        }

        if (permis == null || permis.isEmpty()){
            editTextNumPermis.setError(getString(R.string.client_error_permis));
            error = true;
        }

        if (email == null || email.isEmpty() ){
            editTextEmail.setError(getString(R.string.client_error_email));
            error = true;
        }
        else if (!Utils.isEmailValid(email)){
            editTextNom.setError(getString(R.string.client_error_email_forme));
            error = true;
        }

        if (telephone == null || telephone.isEmpty()){
            editTextTelephone.setError(getString(R.string.client_error_telephone));
            error = true;
        }


        if (!error){
            Gerant gerant = Preference.getGerant(ClientUpdateActivity.this);
            client.nom = nom;
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
                                        Intent output = new Intent();
                                        output.putExtra("nomClient", nom);
                                        setResult(RESULT_OK, output);
                                    }

                                    Toast.makeText(ClientUpdateActivity.this, status.message, Toast.LENGTH_SHORT).show();

                                }

                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                            Toast.makeText(ClientUpdateActivity.this, R.string.client_erreur_update, Toast.LENGTH_SHORT).show();

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
                Toast.makeText(ClientUpdateActivity.this,  R.string.client_error_insert, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ClientUpdateActivity.this, LoginActivity.class);
                startActivity(intent);
            }

        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent output = new Intent();
        output.putExtra("nomClient", client.nom);
        setResult(RESULT_OK, output);
    }

    protected void locationClient(){

        Preference.removeClient(ClientUpdateActivity.this);

        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(ClientUpdateActivity.this, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(ClientUpdateActivity.this);
        }
        builder.setTitle("Faire une location")
                .setMessage("Voulez-vous enregistrer ce client pour une location ?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Preference.setClient(ClientUpdateActivity.this,client);
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

}
