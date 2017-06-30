package fr.eni.lokacar;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

import fr.eni.lokacar.modele.Gerant;
import fr.eni.lokacar.ui.home.HomeActivity;
import fr.eni.lokacar.ui.login.LoginActivity;
import fr.eni.lokacar.utils.Preference;
import fr.eni.lokacar.utils.Utils;


/**
 * Created by goruchon2016 on 21/06/2017.
 * Classe générique pour avoir la flèche retour
 * dans le menu et permettre un retour à l'arrière
 */

public class AppActivity extends AppCompatActivity {

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("DEMARRAGE", "APPACT");
        Utils.testSession(AppActivity.this);

        //Check actionBar
        if (getSupportActionBar() != null)
            // Pour n'afficher la flèche que sur les autres activités
            if(!(this instanceof HomeActivity) && !(this instanceof LoginActivity))
            //Afficher la flèche retour
            {getSupportActionBar().setDisplayHomeAsUpEnabled(true);}

    }
}
