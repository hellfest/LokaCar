package fr.eni.lokacar.modele;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by goruchon2016 on 26/06/2017.
 */

public class Location implements Serializable {
    public int id;
    public Date date_debut;
    public Date date_fin;
    public Vehicule vehicule;
    public Client client;
}
