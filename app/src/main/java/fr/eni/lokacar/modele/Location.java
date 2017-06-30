package fr.eni.lokacar.modele;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by goruchon2016 on 26/06/2017.
 */

public class Location implements Serializable {
    public int id;
    public String date_debut;
    public String date_fin;
    public Vehicule vehicule;
    public Client client;

    public Date getDateDebut(){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        formatter.setTimeZone(TimeZone.getTimeZone("Europe/Paris"));

        Date d = null;
        try {
            d = formatter.parse(this.date_debut);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return d;

    }

    public Date getDateFin(){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        formatter.setTimeZone(TimeZone.getTimeZone("Europe/Paris"));
        Date d = null;
        try {
            d = formatter.parse(this.date_fin);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return d;

    }

    public void setDateDebut(Date d){
        if(d != null) {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            formatter.setTimeZone(TimeZone.getTimeZone("Europe/Paris"));
            this.date_debut = formatter.format(d);
        }
    }

    public void setDateFin(Date d){
        if(d != null) {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            formatter.setTimeZone(TimeZone.getTimeZone("Europe/Paris"));
            this.date_fin = formatter.format(d);
        }
    }
}
