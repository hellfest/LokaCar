package fr.eni.lokacar.utils;

/**
 * Created by goruchon2016 on 27/06/2017.
 */

public class Constant {
    public static final String URL_IDENTIFICATION = "http://10.1.139.21:8383/slim/gerant/ident/%1$s/%2$s";
    // Syntaxe Avec le framework sliM
    // --------------Requêtes VEHICULES------------------
    public static final String URL_LIST_VEHICULE = "http://10.1.139.21:8383/slim/vehicule/all/%s";

    //--------------Requête de suppression d'un vehicule ----------------
    public static final String URL_DELETE_VEHICULE = "http://10.1.139.21:8383/slim/vehicule/delete/%s/%s";

    //-------------Requeste de récupération d'un vehicule ------------
    public static final String URL_GET_VEHICULE = "http://10.1.139.21:8383/slim/vehicule/%s/%s";

    //-------------Requeste de mise à jour du client
    public static final String URL_UPDATE_VEHICULE = "http://10.1.139.21:8383/slim/vehicule/update/%s";

    //--------------Requêtes Marques------------------
    public static final String URL_LIST_MARQUE = "http://10.1.139.21:8383/slim/vehicule/marque/all/%s";

    //--------------Requêtes Modeles------------------
    public static final String URL_LIST_MODELE = "http://10.1.139.21:8383/slim/vehicule/modele/all/%s";

    //--------------Requête liste des clients recherche ----------------
    public static final String URL_LIST_VEHICULE_AGENCE= "http://10.1.139.21:8383/slim/vehicule/liste/all/%s";

    //--------------Requête liste des clients recherche ----------------
    public static final String URL_LIST_SEARCH_CLIENTS= "http://10.1.139.21:8383/slim/client/liste/%s/%s";

    //--------------Requête de suppression du client ----------------
    public static final String URL_DELETE_CLIENT = "http://10.1.139.21:8383/slim/client/delete/%s/%s";

    //-------------Requeste de récupération du client ------------
    public static final String URL_GET_CLIENT = "http://10.1.139.21:8383/slim/client/%s/%s";

    //-------------Requeste de mise à jour du client
    public static final String URL_UPDATE_CLIENT = "http://10.1.139.21:8383/slim/client/update/%s";


}
