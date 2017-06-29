package fr.eni.lokacar.adpater;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
 * Created by lbouvet2016 on 29/06/2017.
 */

public class VehiculeLocationAdapter extends ArrayAdapter<Vehicule> {

    private LayoutInflater inflater;
    private int resId;

    public VehiculeLocationAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<Vehicule> objects) {
        super(context, resource, objects);

        inflater = LayoutInflater.from(context);
        resId = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = inflater.inflate(resId, null);

            viewHolder = new ViewHolder(); //instanciation

            viewHolder.textViewVehiculeMarque = (TextView) convertView.findViewById(R.id.textViewVehiculeMarque);
            viewHolder.textViewVehiculeModele = (TextView) convertView.findViewById(R.id.textViewVehiculeModele);
            viewHolder.textViewVehiculeImmatriculation = (TextView) convertView.findViewById(R.id.textViewVehiculeImmatriculation);
            viewHolder.imageViewVehicule = (ImageView) convertView.findViewById(R.id.imageViewVehicule);
            viewHolder.textViewVehiculePrix = (TextView) convertView.findViewById(R.id.textViewVehiculePrix);
            viewHolder.textViewVehiculePuissance = (TextView) convertView.findViewById(R.id.textViewVehiculePuissance);


            // Permet de Tagger l'objet viewHolder afin de passer les éléments textview et image view
            // title et category
            convertView.setTag(viewHolder);
        } else {
            // Pour
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Vehicule vehicule = getItem(position);

        viewHolder.textViewVehiculeMarque.setText(vehicule.modele.marque.libelle);
        viewHolder.textViewVehiculeModele.setText(vehicule.modele.libelle);
        viewHolder.textViewVehiculeImmatriculation.setText(vehicule.immatriculation);
        viewHolder.textViewVehiculePrix.setText(String.format(getContext().getString(R.string.location_pattern_prix), vehicule.modele.prix_jour));
        viewHolder.textViewVehiculePuissance.setText(String.format(getContext().getString(R.string.location_pattern_puissance), vehicule.modele.puissance));
        //TODO : gérer l'affichage de la première photo du vehicule
        viewHolder.imageViewVehicule.setImageResource(R.drawable.logo_small);

        return convertView;

    }

    private class ViewHolder{
        TextView textViewVehiculeMarque;
        TextView textViewVehiculeModele;
        TextView textViewVehiculeImmatriculation;
        TextView textViewVehiculePrix;
        TextView textViewVehiculePuissance;
        ImageView imageViewVehicule;
    }
}
