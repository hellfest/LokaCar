package fr.eni.lokacar.modele;

import java.io.Serializable;

/**
 * Created by goruchon2016 on 26/06/2017.
 */

public class Marque implements Serializable {
    public int id;
    public String libelle;

    @Override
    public String toString() {
        return libelle;
    }
}
