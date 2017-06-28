package fr.eni.lokacar.modele;

import java.io.Serializable;

/**
 * Created by goruchon2016 on 26/06/2017.
 */

public class Agence implements Serializable {

    public int id;
    public String nom;
    public String c_a;
    public String adresse;
    public String cp;
    public String ville;
    public String email;
    public String telephone;
    public String id_gerant;
}
