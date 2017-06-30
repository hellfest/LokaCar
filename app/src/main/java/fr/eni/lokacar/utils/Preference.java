package fr.eni.lokacar.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.gson.Gson;


import fr.eni.lokacar.modele.Client;
import fr.eni.lokacar.modele.Gerant;
import fr.eni.lokacar.modele.Vehicule;

/**
 * Created by goruchon2016 on 23/06/2017.
 */

// pour gérer des préférences
public class Preference {


    private static final String PREF_GERANT = "gerant";
    private static final String PREF_VEHICULE = "vehicule";
    private static final String PREF_CLIENT = "client";

    //Methode pour retourner les préférences
    private static SharedPreferences get(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

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


    //Récupéréation de tout l'objet enregistré en JSON
    public static Vehicule getVehicule(Context context) {


        String json = get(context).getString(PREF_VEHICULE, null);

        if (json != null) {
            Gson gson = new Gson();
            return gson.fromJson(json, Vehicule.class);

        } else { // aucune donnée
            return null;
        }
    }

    // Methode pour enregistrer le Gerant
    public static void setVehicule(Context context, Vehicule vehicule) {

        Gson gson = new Gson();

        //Récupération de l'objet en Json
        String json = gson.toJson(vehicule);

        // Permet de l'enregistrer dans les préférences
        get(context)
                .edit()
                .putString(PREF_VEHICULE, json)
                .commit();
    }

    // Methode pour enregistrer le Gerant
    public static void removeVehicule(Context context) {

        get(context)
                .edit().remove(PREF_VEHICULE).commit();
    }

    //Récupéréation de tout l'objet enregistré en JSON
    public static Client getClient(Context context) {


        String json = get(context).getString(PREF_CLIENT, null);

        if (json != null) {
            Gson gson = new Gson();
            return gson.fromJson(json, Client.class);

        } else { // aucune donnée
            return null;
        }
    }

    // Methode pour enregistrer le Gerant
    public static void setClient(Context context, Client client) {

        Gson gson = new Gson();

        //Récupération de l'objet en Json
        String json = gson.toJson(client);

        // Permet de l'enregistrer dans les préférences
        get(context)
                .edit()
                .putString(PREF_CLIENT, json)
                .commit();
    }

    // Methode pour enregistrer le Gerant
    public static void removeClient(Context context) {

        get(context)
                .edit().remove(PREF_CLIENT).commit();
    }

    // Methode pour enregistrer le Gerant
    public static void removeGerant(Context context) {

        get(context)
                .edit().remove(PREF_GERANT).commit();
    }
}
