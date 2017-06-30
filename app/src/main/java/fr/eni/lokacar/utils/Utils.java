package fr.eni.lokacar.utils;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

import fr.eni.lokacar.AppActivity;
import fr.eni.lokacar.R;
import fr.eni.lokacar.modele.Gerant;
import fr.eni.lokacar.modele.Location;
import fr.eni.lokacar.modele.StatusRest;
import fr.eni.lokacar.ui.home.location.LocationFormActivity;
import fr.eni.lokacar.ui.login.LoginActivity;

/**
 * Created by lbouvet2016 on 28/06/2017.
 */

public class Utils {
    public static boolean isEmailValid(String email) {
        return !(email == null || TextUtils.isEmpty(email)) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static void testSession(final Context context){

        Gerant gerant = Preference.getGerant(context);

        if (gerant != null) {

            //check network available or not
            if (Network.isNetworkAvailable(context)) {

                // Instantiate the RequestQueue.
                RequestQueue queue = Volley.newRequestQueue(context);

                String url = String.format(Constant.URL_PING, gerant.session);

                // Request a string response from the provided URL.
                StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                        new Response.Listener<String>() {
                            @Override
                            //json = parametre de reponse
                            public void onResponse(String json) {
                                Gson gson = new Gson();
                                StatusRest status = gson.fromJson(json, StatusRest.class);
                                if (!status.status) {
                                    Preference.removeGerant(context);
                                }


                            }

                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {


                    }
                });

                // Add the request to the RequestQueue.
                queue.add(stringRequest);

            }
        }

    }


}
