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

import java.util.Calendar;
import java.util.List;

import fr.eni.lokacar.R;
import fr.eni.lokacar.modele.Location;
import fr.eni.lokacar.modele.Vehicule;
import fr.eni.lokacar.utils.Constant;

/**
 * Created by lbouvet2016 on 30/06/2017.
 */

public class LocationAdapter extends ArrayAdapter<Location> {

    private LayoutInflater inflater;
    private int resId;

    public LocationAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<Location> objects) {
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
            viewHolder.textViewNomClient = (TextView) convertView.findViewById(R.id.textViewNomClient);
            viewHolder.textViewDateDebut = (TextView) convertView.findViewById(R.id.textViewDateDebut);
            viewHolder.textViewDateFin = (TextView) convertView.findViewById(R.id.textViewDateFin);


            // Permet de Tagger l'objet viewHolder afin de passer les éléments textview et image view
            // title et category
            convertView.setTag(viewHolder);
        } else {
            // Pour
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Location location = getItem(position);

        viewHolder.textViewVehiculeMarque.setText(location.vehicule.modele.marque.libelle);
        viewHolder.textViewVehiculeModele.setText(location.vehicule.modele.libelle);
        viewHolder.textViewVehiculeImmatriculation.setText(location.vehicule.immatriculation);
        viewHolder.textViewVehiculePrix.setText(String.format(getContext().getString(R.string.location_pattern_prix), location.vehicule.modele.prix_jour));
        viewHolder.textViewVehiculePuissance.setText(String.format(getContext().getString(R.string.location_pattern_puissance), location.vehicule.modele.puissance));
        viewHolder.textViewNomClient.setText("Client : " + location.client.nom+" "+ location.client.prenom);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(location.date_debut);

        viewHolder.textViewDateDebut.setText("Du " + String.format("%s/%s/%s",
                calendar.get(Calendar.DAY_OF_MONTH)
                ,calendar.get(Calendar.MONTH)
                ,calendar.get(Calendar.YEAR)));

        calendar.setTime(location.date_fin);

        if(calendar.get(Calendar.YEAR) > Constant.DATE_MINIMUM) {

            viewHolder.textViewDateFin.setText("au "+String.format("%s/%s/%s",
                    calendar.get(Calendar.DAY_OF_MONTH)
                    , calendar.get(Calendar.MONTH)
                    , calendar.get(Calendar.YEAR)));
        }
        else{
            viewHolder.textViewDateFin.setText("au --");
        }

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
        TextView textViewNomClient;
        TextView textViewDateDebut;
        TextView textViewDateFin;
    }
}
