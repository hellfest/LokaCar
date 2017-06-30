package fr.eni.lokacar.ui.home.location;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
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
import com.google.gson.GsonBuilder;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import fr.eni.lokacar.AppActivity;
import fr.eni.lokacar.R;
import fr.eni.lokacar.modele.Client;
import fr.eni.lokacar.modele.Gerant;
import fr.eni.lokacar.modele.Location;
import fr.eni.lokacar.modele.StatusRest;
import fr.eni.lokacar.ui.home.client.ClientUpdateActivity;
import fr.eni.lokacar.ui.login.LoginActivity;
import fr.eni.lokacar.utils.Constant;
import fr.eni.lokacar.utils.Network;
import fr.eni.lokacar.utils.Preference;

public class LocationFormActivity extends AppActivity {

    private Location location;
    private int id;

    private TextView editTextMarque;
    private TextView editTextModele;
    private TextView editTextImmat;
    private TextView editTextPuissance;
    private TextView editTextPrix;
    private TextView editTextCout;
    private TextView editTextNom;
    private TextView editTextPermis;
    private TextView editTextEmail;
    private TextView editTextDateDebut;
    private TextView editTextDateFin;
    private Calendar calDebut = Calendar.getInstance();;
    private Calendar calFin = Calendar.getInstance();;


    private DatePickerDialog.OnDateSetListener dateDebut;
    private DatePickerDialog.OnDateSetListener dateFin;

    private DatePickerDialog dateDebutDatepicker;
    private DatePickerDialog dateFinDatepicker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_form);

        editTextMarque = (TextView) findViewById(R.id.editTextMarque);
        editTextModele = (TextView) findViewById(R.id.editTextModele);
        editTextImmat = (TextView) findViewById(R.id.editTextImmat);
        editTextPuissance = (TextView) findViewById(R.id.editTextPuissance);
        editTextPrix = (TextView) findViewById(R.id.editTextPrix);
        editTextCout = (TextView) findViewById(R.id.editTextCout);
        editTextNom = (TextView) findViewById(R.id.editTextNom);
        editTextPermis = (TextView) findViewById(R.id.editTextPermis);
        editTextEmail = (TextView) findViewById(R.id.editTextEmail);
        editTextDateDebut = (TextView) findViewById(R.id.editTextDateDebut);
        editTextDateFin = (TextView) findViewById(R.id.editTextDateFin);

        id = getIntent().getIntExtra("location", -1);

        location = null;

        getLocation();

        dateDebut = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                editTextDateDebut.setText(String.format("%02d/%02d/%s", dayOfMonth, (monthOfYear + 1), year));

                Calendar calendar = Calendar.getInstance();
                calendar.set(year,monthOfYear,dayOfMonth);

                location.setDateDebut(calendar.getTime());

                dateFinDatepicker.getDatePicker().setMinDate(calendar.getTime().getTime());
                calculPrix();
            }

        };

        dateFin = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                editTextDateFin.setText(String.format("%02d/%02d/%s", dayOfMonth, (monthOfYear + 1), year));

                Calendar calendar = Calendar.getInstance();
                calendar.set(year,monthOfYear,dayOfMonth);

                location.setDateFin(calendar.getTime());
                calculPrix();
            }

        };

        editTextDateDebut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dateDebutDatepicker = new DatePickerDialog(LocationFormActivity.this, dateDebut, calDebut
                        .get(Calendar.YEAR), calDebut.get(Calendar.MONTH),
                        calDebut.get(Calendar.DAY_OF_MONTH));
                dateDebutDatepicker.getDatePicker().setMinDate(location.getDateDebut().getTime());
                dateDebutDatepicker.show();
            }
        });

        editTextDateFin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dateFinDatepicker = new DatePickerDialog(LocationFormActivity.this, dateFin, calFin
                        .get(Calendar.YEAR), calFin.get(Calendar.MONTH),
                        calFin.get(Calendar.DAY_OF_MONTH));

                dateFinDatepicker.getDatePicker().setMinDate(location.getDateDebut().getTime());
                dateFinDatepicker.show();
            }
        });

    }

    /**
     *
     */
    public void getLocation() {
        Gerant gerant = Preference.getGerant(LocationFormActivity.this);

        if (gerant != null) {

            //check network available or not
            if (Network.isNetworkAvailable(LocationFormActivity.this)) {

                // Instantiate the RequestQueue.
                RequestQueue queue = Volley.newRequestQueue(LocationFormActivity.this);

                String url = String.format(Constant.URL_GET_LOCATION, id, gerant.session);

                // Request a string response from the provided URL.
                StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                        new Response.Listener<String>() {
                            @Override
                            //json = parametre de reponse
                            public void onResponse(String json) {
                                Gson gson = new Gson();
                                //Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
                                location = gson.fromJson(json, Location.class);

                                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                formatter.setTimeZone(TimeZone.getTimeZone("Europe/Paris"));

                                calDebut.setTime(location.getDateDebut());

                                Calendar calendar = Calendar.getInstance();
                                calendar.setTime(location.getDateFin());

                                if (calendar.get(Calendar.YEAR) > Constant.DATE_MINIMUM) {
                                    calFin.setTime(location.getDateFin());
                                }
                                else{
                                    calendar = Calendar.getInstance();
                                    calFin.setTime(calendar.getTime());
                                }

                                afficheLocation();
                                calculPrix();

                            }

                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Toast.makeText(LocationFormActivity.this, "Erreur récupération de la location", Toast.LENGTH_SHORT).show();

                    }
                });

                // Add the request to the RequestQueue.
                queue.add(stringRequest);

            }
        } else {
            Toast.makeText(LocationFormActivity.this, R.string.client_error_insert, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(LocationFormActivity.this, LoginActivity.class);
            startActivity(intent);
        }

    }

    public void afficheLocation() {


        editTextMarque.setText(location.vehicule.modele.marque.libelle);
        editTextModele.setText(location.vehicule.modele.libelle);
        editTextImmat.setText(location.vehicule.immatriculation);
        editTextPuissance.setText(location.vehicule.modele.puissance + " CV");
        editTextPrix.setText(location.vehicule.modele.prix_jour + " €/jour");
        editTextNom.setText(location.client.nom + " " + location.client.prenom);
        editTextPermis.setText(location.client.numero_permis);
        editTextEmail.setText(location.client.email);

        String sDate = String.format("%02d/%02d/%s",
                calDebut.get(Calendar.DAY_OF_MONTH)
                , (calDebut.get(Calendar.MONTH) + 1)
                , calDebut.get(Calendar.YEAR));
        editTextDateDebut.setText(sDate);

        //Calendar calendar = Calendar.getInstance();
       //calendar.setTime(location.getDateFin());
        if (calFin.get(Calendar.YEAR) > Constant.DATE_MINIMUM){

            sDate = String.format("%02d/%02d/%s",
                    calFin.get(Calendar.DAY_OF_MONTH)
                    , (calFin.get(Calendar.MONTH) + 1 )
                    , calFin.get(Calendar.YEAR));

            editTextDateFin.setText(sDate);
        }

    }

    private void calculPrix(){
        long nbJours = 0;

        Calendar calF = Calendar.getInstance();
        calF.setTime(location.getDateFin());

        String prix = "--";

        if (calF.get(Calendar.YEAR) > Constant.DATE_MINIMUM) {

            long diff = location.getDateFin().getTime() - location.getDateDebut().getTime();

            nbJours = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS) ;

            if (nbJours <= 0){
                nbJours = 0;
            }
            //Ajoute une journée minimum
            nbJours++;

            long calcul = nbJours * (long)Double.parseDouble(location.vehicule.modele.prix_jour);

            prix = String.valueOf(calcul);
        }

        editTextCout.setText(prix +" €");

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_location_update, menu);
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

        if (id == R.id.action_locationUpdate) {
            updateLocation();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void updateLocation() {

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(location.getDateFin());

        if (location.getDateDebut().compareTo(location.getDateFin()) <= 0
                && calendar.get(Calendar.YEAR) > Constant.DATE_MINIMUM) {

            Gerant gerant = Preference.getGerant(LocationFormActivity.this);

            if (gerant != null) {

                //check network available or not
                if (Network.isNetworkAvailable(LocationFormActivity.this)) {

                    // Instantiate the RequestQueue.
                    RequestQueue queue = Volley.newRequestQueue(LocationFormActivity.this);

                    String url = String.format(Constant.URL_UPDATE_LOCATION, gerant.session);

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

                                    Toast.makeText(LocationFormActivity.this, status.message, Toast.LENGTH_SHORT).show();

                                }

                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                            Toast.makeText(LocationFormActivity.this, R.string.location_erreur_update, Toast.LENGTH_SHORT).show();

                        }
                    }) {
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String, String> params = new HashMap<String, String>();
                            params.put("id", String.valueOf(location.id));

                            params.put("date_debut", location.date_debut);
                            params.put("date_fin", location.date_fin);
                            return params;
                        }
                    };

                    // Add the request to the RequestQueue.
                    queue.add(stringRequest);

                }

            } else {
                Toast.makeText(LocationFormActivity.this,  R.string.client_error_insert, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(LocationFormActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        }
        else{
            Toast.makeText(LocationFormActivity.this, "La date de fin doit être postérieure ou égale à la date de début", Toast.LENGTH_SHORT).show();
        }
    }

}
