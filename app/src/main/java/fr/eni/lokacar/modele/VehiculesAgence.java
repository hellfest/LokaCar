package fr.eni.lokacar.modele;

import java.io.Serializable;
import java.util.List;

/**
 * Created by goruchon2016 on 28/06/2017.
 */

public class VehiculesAgence implements Serializable {

    public List<Vehicule> vehicules;
    public List<Modele> modeles;
    public List<Marque> marques;
}
