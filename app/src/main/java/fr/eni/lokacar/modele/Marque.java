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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Marque)) return false;

        Marque marque = (Marque) o;

        if (id != marque.id) return false;
        return this.libelle.equals(marque.libelle);

    }

}
