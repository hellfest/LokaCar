package fr.eni.lokacar.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.gson.Gson;


import fr.eni.lokacar.modele.Gerant;

/**
 * Created by goruchon2016 on 23/06/2017.
 */

// pour gérer des préférences
public class Preference {


    private static final String PREF_GERANT = "gerant";

    //Methode pour retourner les préférences
    private static SharedPreferences get(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

//    // Methode pour afficher la ville
//    public static String getCity(Context context) {
//        return get(context).getString(PREF_CITY, "");
//    }
//
//    // Methode pour enregistrer la ville
//    public static void setCity(Context context, String city) {
//        get(context)
//                .edit()
//                .putString(PREF_CITY, city)
//                .commit();
//    }

    //Récupéréation de tout l'objet enregistré en JSON
    public static Gerant getGerant(Context context) {


        String json = get(context).getString(PREF_GERANT, null);

        if (json != null) {
            Gson gson = new Gson();
            return gson.fromJson(json, Gerant.class);

        } else { // aucune donnée
            return null;
        }
    }

    // Methode pour enregistrer le Gerant
    public static void setGerant(Context context, Gerant gerant) {

        Gson gson = new Gson();

        //Récupération de l'objet en Json
        String json = gson.toJson(gerant);

        // Permet de l'enregistrer dans les préférences
        get(context)
                .edit()
                .putString(PREF_GERANT, json)
                .commit();
    }
}
