package fr.eni.lokacar.modele;

import java.io.Serializable;
import java.util.List;

/**
 * Created by goruchon2016 on 26/06/2017.
 */

public class Vehicule implements Serializable {

    public int id;
    public String immatriculation;
    public boolean disponible;
    public String archive;
    public Modele modele;
    public Agence agence;
    public List<Photo> listPhotos;

    @Override
    public String toString() {
        return immatriculation;
    }
}
