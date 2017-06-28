package fr.eni.lokacar.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import fr.eni.lokacar.AppActivity;
import fr.eni.lokacar.R;
import fr.eni.lokacar.adpater.VehiculeAdapter;
import fr.eni.lokacar.modele.Gerant;
import fr.eni.lokacar.modele.Vehicule;
import fr.eni.lokacar.ui.home.client.ClientFragment;
import fr.eni.lokacar.ui.home.vehicules.VehiculeFilterFragment;
import fr.eni.lokacar.ui.home.vehicules.VehiculeFragment;
import fr.eni.lokacar.ui.main.MainActivity;
import fr.eni.lokacar.utils.Preference;

public class HomeActivity extends AppActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Gerant gerant;
    private List<Vehicule> vehiculeList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        TextView textViewGerant = (TextView) navigationView.getHeaderView(0).findViewById(R.id.textViewGerant);
        TextView textViewEmail = (TextView) navigationView.getHeaderView(0).findViewById(R.id.textViewEmail);

        //Pour récupérer le gérant en session
        gerant = Preference.getGerant(HomeActivity.this);
        // Pour afficher des infos du gérant dans l'entête de la navigation view
        textViewGerant.setText(gerant.nom.toUpperCase() + " " + gerant.prenom.substring(0, 1).toUpperCase() + gerant.prenom.substring(1));
        textViewEmail.setText(gerant.email);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/

    private void switchFragment(Fragment fragment) {

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.linearLayoutFragment, fragment)
                .commit();

    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.location) {

        } else if (id == R.id.vehicule) {
            VehiculeFragment vehiculeFragment = new VehiculeFragment();
            switchFragment(vehiculeFragment);

        } else if (id == R.id.client) {
            switchFragment(new ClientFragment());
        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.chiffre_affaires) {

        } else if (id == R.id.deconnexion) {
            Preference.setGerant(HomeActivity.this, null);
            Intent intent = new Intent(HomeActivity.this, MainActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
