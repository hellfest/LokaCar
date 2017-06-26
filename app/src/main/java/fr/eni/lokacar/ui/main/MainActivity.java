package fr.eni.lokacar.ui.main;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.Timer;
import java.util.TimerTask;

import fr.eni.lokacar.AppActivity;
import fr.eni.lokacar.R;
import fr.eni.lokacar.ui.login.LoginActivity;

public class MainActivity extends AppActivity {

    Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        }, 2000);
    }
}
