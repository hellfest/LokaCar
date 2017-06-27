package fr.eni.lokacar.modele;

import java.util.List;

/**
 * Created by goruchon2016 on 26/06/2017.
 */

public class Vehicule {

    public int id;
    public String immatriculation;
    public String disponible;
    public String archive;
    public Modele modele;
    public Agence agence;
    public List<Photo> listPhotos;
}
