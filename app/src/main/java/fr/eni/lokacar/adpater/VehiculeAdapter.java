package fr.eni.lokacar.adpater;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import fr.eni.lokacar.R;
import fr.eni.lokacar.modele.Vehicule;

/**
 * Created by goruchon2016 on 27/06/2017.
 */

public class VehiculeAdapter extends ArrayAdapter<Vehicule> {
    private List<Vehicule> liste;
    private LayoutInflater inflater;
    private int resId;

    public VehiculeAdapter(Context context, int resource, List<Vehicule> objects) {
        super(context, resource, objects);
        inflater = LayoutInflater.from(context);
        resId = resource;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewholder; // définition

        if (convertView == null) {
            convertView = inflater.inflate(resId, null);

            viewholder = new ViewHolder(); //instanciation

            viewholder.textViewVehiculeMarque = (TextView) convertView.findViewById(R.id.textViewVehiculeMarque);
            viewholder.textViewVehiculeModele = (TextView) convertView.findViewById(R.id.textViewVehiculeModele);
            viewholder.textViewVehiculeImmatriculation = (TextView) convertView.findViewById(R.id.textViewVehiculeImmatriculation);
            viewholder.imageViewVehicule = (ImageView) convertView.findViewById(R.id.imageViewVehicule);


            // Permet de Tagger l'objet viewHolder afin de passer les éléments textview et image view
            // title et category
            convertView.setTag(viewholder);
        } else {
            // Pour
            viewholder = (ViewHolder) convertView.getTag();
        }

        Vehicule vehicule = getItem(position);

        viewholder.textViewVehiculeMarque.setText(vehicule.modele.marque.libelle);
        viewholder.textViewVehiculeModele.setText(vehicule.modele.libelle);
        viewholder.textViewVehiculeImmatriculation.setText(vehicule.immatriculation);

        //TODO : gérer l'affichage de la première photo du vehicule
        viewholder.imageViewVehicule.setImageResource(R.drawable.logo_small);

        return convertView;
    }

    // Création d'une classe intermédiaire
    private class ViewHolder {
        TextView textViewVehiculeMarque;
        TextView textViewVehiculeModele;
        TextView textViewVehiculeImmatriculation;
        ImageView imageViewVehicule;
    }

}