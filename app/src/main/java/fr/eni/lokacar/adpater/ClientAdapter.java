package fr.eni.lokacar.adpater;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import fr.eni.lokacar.R;
import fr.eni.lokacar.modele.Client;


/**
 * Created by lbouvet2016 on 28/06/2017.
 */

public class ClientAdapter extends ArrayAdapter<Client> {

    private LayoutInflater inflater;
    private int resId;

    public ClientAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<Client> objects) {
        super(context, resource, objects);

        inflater = LayoutInflater.from(context);
        resId = resource;

}

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewholder; // définition

        if (convertView == null) {
            convertView = inflater.inflate(resId, null);

            viewholder = new ViewHolder(); //instanciation

            viewholder.textViewClientNom = (TextView) convertView.findViewById(R.id.textViewClientNom);
            viewholder.textViewClientPrenom = (TextView) convertView.findViewById(R.id.textViewClientPrenom);
            viewholder.textViewClientAdresse = (TextView) convertView.findViewById(R.id.textViewClientAdresse);
            viewholder.textViewClientCp = (TextView) convertView.findViewById(R.id.textViewClientCp);
            viewholder.textViewClientVille = (TextView) convertView.findViewById(R.id.textViewClientVille);

            // Permet de Tagger l'objet viewHolder afin de passer les éléments textview
            convertView.setTag(viewholder);
        } else {
            // Pour
            viewholder = (ViewHolder) convertView.getTag();
        }

        Client client = getItem(position);

        viewholder.textViewClientNom.setText(client.nom);
        viewholder.textViewClientPrenom.setText(client.prenom);
        viewholder.textViewClientAdresse.setText(client.adresse);
        viewholder.textViewClientCp.setText(client.cp);
        viewholder.textViewClientVille.setText(client.ville);

        return convertView;
    }

    // Création d'une classe intermédiaire
    private class ViewHolder {
        TextView textViewClientNom;
        TextView textViewClientPrenom;
        TextView textViewClientAdresse;
        TextView textViewClientCp;
        TextView textViewClientVille;
    }
}
