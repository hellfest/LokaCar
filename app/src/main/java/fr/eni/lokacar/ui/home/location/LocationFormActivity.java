package fr.eni.lokacar.ui.home.location;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Calendar;

import fr.eni.lokacar.AppActivity;
import fr.eni.lokacar.R;
import fr.eni.lokacar.modele.Client;
import fr.eni.lokacar.modele.Gerant;
import fr.eni.lokacar.modele.Location;
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
    private TextView editTextNom;
    private TextView editTextPermis;
    private TextView editTextEmail;
    private TextView editTextDateDebut;
    private TextView editTextDateFin;
    private Calendar calDebut = Calendar.getInstance();;
    private Calendar calFin = Calendar.getInstance();;


    private DatePickerDialog.OnDateSetListener dateDebut;
    private DatePickerDialog.OnDateSetListener dateFin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_form);

        editTextMarque = (TextView) findViewById(R.id.editTextMarque);
        editTextModele = (TextView) findViewById(R.id.editTextModele);
        editTextImmat = (TextView) findViewById(R.id.editTextImmat);
        editTextPuissance = (TextView) findViewById(R.id.editTextPuissance);
        editTextPrix = (TextView) findViewById(R.id.editTextPrix);
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
                editTextDateDebut.setText(String.format("%s/%s/%s", dayOfMonth, (monthOfYear + 1), year));
            }

        };

        dateFin = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                editTextDateFin.setText(String.format("%s/%s/%s", dayOfMonth, (monthOfYear + 1), year));
            }

        };


        editTextDateDebut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(LocationFormActivity.this, dateDebut, calDebut
                        .get(Calendar.YEAR), calDebut.get(Calendar.MONTH),
                        calDebut.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        editTextDateFin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(LocationFormActivity.this, dateFin, calFin
                        .get(Calendar.YEAR), calFin.get(Calendar.MONTH),
                        calFin.get(Calendar.DAY_OF_MONTH)).show();
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
                                //Gson gson = new Gson();
                                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
                                location = gson.fromJson(json, Location.class);

                                calDebut.setTime(location.date_debut);

                                Calendar calendar = Calendar.getInstance();
                                calendar.setTime(location.date_fin);

                                if (calendar.get(Calendar.YEAR) > Constant.DATE_MINIMUM) {
                                    calFin.setTime(location.date_fin);
                                }
                                else{
                                    calendar = Calendar.getInstance();
                                    calFin.setTime(calendar.getTime());
                                }

                                afficheLocation();

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
        editTextPuissance.setText(location.vehicule.modele.puissance);
        editTextPrix.setText(location.vehicule.modele.prix_jour);
        editTextNom.setText(location.client.nom + " " + location.client.prenom);
        editTextPermis.setText(location.client.numero_permis);
        editTextEmail.setText(location.client.email);

        String sDate = String.format("%s/%s/%s",
                calDebut.get(Calendar.DAY_OF_MONTH)
                , calDebut.get(Calendar.MONTH)
                , calDebut.get(Calendar.YEAR));
        editTextDateDebut.setText(sDate);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(location.date_fin);
        if (calendar.get(Calendar.YEAR) > Constant.DATE_MINIMUM){

            sDate = String.format("%s/%s/%s",
                    calFin.get(Calendar.DAY_OF_MONTH)
                    , calFin.get(Calendar.MONTH)
                    , calFin.get(Calendar.YEAR));

            editTextDateFin.setText(sDate);
        }

    }

}
