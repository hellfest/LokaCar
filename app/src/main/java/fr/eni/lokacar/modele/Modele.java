package fr.eni.lokacar.modele;

import java.io.Serializable;

/**
 * Created by goruchon2016 on 26/06/2017.
 */

public class Modele implements Serializable{
    public int id;
    public String libelle;
    public String prix_jour;
    public String puissance;
    public Categorie categorie;
    public Marque marque;

    @Override
    public String toString() {
        return libelle;
    }
}
