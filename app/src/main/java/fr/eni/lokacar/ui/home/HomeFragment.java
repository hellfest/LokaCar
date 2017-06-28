package fr.eni.lokacar.ui.home;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import fr.eni.lokacar.R;
import fr.eni.lokacar.modele.Gerant;
import fr.eni.lokacar.utils.Preference;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {
    private TextView textViewAccueilGerant;
    private TextView textViewNomAgence;
    private TextView textViewAdresseAgence;
    private TextView textViewCpAgence;
    private TextView textViewVilleAgence;
    private TextView textViewEmailAgence;
    private TextView textViewTelAgence;

    private View view;

    private Gerant gerant;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_home, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        gerant = Preference.getGerant(getContext());

        textViewAccueilGerant = (TextView) view.findViewById(R.id.textViewAccueilGerant);
        textViewNomAgence = (TextView) view.findViewById(R.id.textViewNomAgence);
        textViewAdresseAgence = (TextView) view.findViewById(R.id.textViewAdresseAgence);
        textViewCpAgence = (TextView) view.findViewById(R.id.textViewCpAgence);
        textViewVilleAgence = (TextView) view.findViewById(R.id.textViewVilleAgence);
        textViewEmailAgence = (TextView) view.findViewById(R.id.textViewEmailAgence);
        textViewTelAgence = (TextView) view.findViewById(R.id.textViewTelAgence);

        textViewAccueilGerant.setText(gerant.nom+" "+gerant.prenom);
        textViewNomAgence.setText(gerant.agence.nom);
        textViewAdresseAgence.setText(gerant.agence.adresse);
        textViewCpAgence.setText(gerant.agence.cp);
        textViewVilleAgence.setText(gerant.agence.ville);
        textViewEmailAgence.setText(gerant.agence.email);
        textViewTelAgence.setText(gerant.agence.telephone);
    }
}
