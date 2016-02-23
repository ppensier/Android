package eu.ensg.forester;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import eu.ensg.forester.CreateActivity;
import eu.ensg.forester.MapsActivity;
import eu.ensg.forester.R;

public class LoginActivity extends AppCompatActivity {

    public static final String PREFERENCE_NAME = "eu.ensg.forester";
    private Button mPasserelle = null;
    private Button clickOnly = null;
    final String SERIAL = "string";
    private EditText serial;
    public static final String KEY_SERIAL = "Serial";
    private SharedPreferences preferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        preferences = getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        clickOnly = (Button)findViewById(R.id.button);
        mPasserelle = (Button) findViewById(R.id.button2);
        Intent intent = getIntent();
        serial = (EditText) findViewById(R.id.serial);

        //        if(intent != null) {
//            serial.setText(intent.getStringExtra(SERIAL));
//        }

        if (intent != null) {
            serial.setText(intent.getStringExtra(SERIAL));
        } else {
            Log.d("TOTO", preferences.getString(KEY_SERIAL, ""));
            serial.setText(preferences.getString(KEY_SERIAL, ""));
        }


        mPasserelle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Le premier paramètre est le nom de l'activité actuelle
                // Le second est le nom de l'activité de destination
                Intent secondeActivite = new Intent(LoginActivity.this, CreateActivity.class);

                // Puis on lance l'intent !
                startActivity(secondeActivite);
            }
        });

        clickOnly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Toast.makeText(LoginActivity.this, "Hého, tu es un Minipouce ou quoi ?", Toast.LENGTH_SHORT).show();
                // Réagir au clic

                //on stocke le login renseigné
                String login = serial.getText().toString();

                // stoque le serial dans la mémoire de l'appareil
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString(KEY_SERIAL, login);
                editor.commit();
                editor.apply();

                // appel la map
                Intent intent = new Intent(LoginActivity.this, MapsActivity.class);
                startActivity(intent);
            }

        });
    }
}


