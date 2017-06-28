package fr.eni.lokacar.utils;

/**
 * Created by goruchon2016 on 27/06/2017.
 */

public class Constant {
    public static final String URL_IDENTIFICATION = "http://10.1.139.21:8383/slim/gerant/ident/%1$s/%2$s";
    // Syntaxe Avec le framework sliM
    // --------------Requêtes VEHICULES------------------
    public static final String URL_LIST_VEHICULE = "http://10.1.139.21:8383/slim/vehicule/all/%s";

    //--------------Requêtes Marques------------------
    public static final String URL_LIST_MARQUE = "http://10.1.139.21:8383/slim/vehicule/marque/all/%s";

    //--------------Requêtes Marques------------------
    public static final String URL_LIST_MODELE = "http://10.1.139.21:8383/slim/vehicule/modele/all/%s";

    //--------------Requête liste des clients recherche ----------------
    public static final String URL_LIST_VEHICULE_AGENCE= "http://10.1.139.21:8383/slim/vehicule/liste/all/%s";

    //--------------Requête liste des clients recherche ----------------
    public static final String URL_LIST_SEARCH_CLIENTS= "http://10.1.139.21:8383/slim/client/liste/%s/%s";

}
