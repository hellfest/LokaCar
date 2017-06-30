package fr.eni.lokacar.adpater;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import fr.eni.lokacar.R;
import fr.eni.lokacar.modele.Vehicule;

/**
 * Created by goruchon2016 on 27/06/2017.
 */

public class VehiculeAdapter extends ArrayAdapter<Vehicule> implements Filterable{
    private List<Vehicule> liste;
    private LayoutInflater inflater;
    private int resId;

    private List<Vehicule> originalData = null;
    private List<Vehicule> filteredData = null;
    private ItemFilter mFilter = new ItemFilter();

    public VehiculeAdapter(Context context, int resource, List<Vehicule> objects) {
        super(context, resource, objects);
        inflater = LayoutInflater.from(context);
        resId = resource;

        this.filteredData = objects ;
        this.originalData = new ArrayList<Vehicule>(objects);
    }

    public void setOriginalData(List<Vehicule> listVehicules){
        this.originalData.clear();
        this.originalData.addAll(listVehicules);
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

    public void setListe(List<Vehicule> liste) {
        this.liste = liste;
    }

    public int getCount() {
        return filteredData.size();
    }

    public Vehicule getItem(int position) {
        return filteredData.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public Filter getFilter() {
        return mFilter;
    }


    private class ItemFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {



            String filterString = constraint.toString().toLowerCase();

            FilterResults results = new FilterResults();

            final List<Vehicule> list = originalData;

            int count = list.size();
            final ArrayList<Vehicule> nlist = new ArrayList<Vehicule>(count);

            Vehicule filterableVehicule ;

            for (int i = 0; i < count; i++) {
                filterableVehicule = list.get(i);
                if (filterableVehicule.modele.toString().toLowerCase().contains(filterString)) {
                    nlist.add(filterableVehicule);
                }
            }

            results.values = nlist;
            results.count = nlist.size();

            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredData.clear();
            filteredData.addAll((ArrayList<Vehicule>) results.values);
            notifyDataSetChanged();
        }

    }

    // Création d'une classe intermédiaire
    private class ViewHolder {
        TextView textViewVehiculeMarque;
        TextView textViewVehiculeModele;
        TextView textViewVehiculeImmatriculation;
        ImageView imageViewVehicule;
    }

}