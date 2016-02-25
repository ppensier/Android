package eu.ensg.forester;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.DatabaseUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import eu.ensg.forester.CreateActivity;
import eu.ensg.forester.MapsActivity;
import eu.ensg.forester.R;
import eu.ensg.spatialite.SpatialiteDatabase;
import eu.ensg.spatialite.SpatialiteOpenHelper;
import eu.ensg.spatialite.geom.Point;
import jsqlite.Stmt;

public class LoginActivity extends AppCompatActivity {

    public static final String PREFERENCE_NAME = "eu.ensg.forester";
    private Button mPasserelle = null;
    private Button clickOnly = null;
    final String SERIAL = "string";
    private EditText serial;
    public static final String KEY_SERIAL = "Serial";
    private SharedPreferences preferences;
    private SpatialiteDatabase database;

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
                login_onClick(v);

                //on stocke le login renseigné
                String login = serial.getText().toString();

                // stoque le serial dans la mémoire de l'appareil
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString(KEY_SERIAL, login);
                editor.commit();
                editor.apply();

            }

        });

        initDatabase();

    }

    private void initDatabase() {

        try {
            SpatialiteOpenHelper helper = new ForesterSpatialiteOpenHelper(this);
            database = helper.getDatabase();
        } catch (jsqlite.Exception | IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Cannot initialize database !", Toast.LENGTH_LONG).show();
            System.exit(0);
        }

    }

    private void login_onClick(View view){

        try {

            String id = serial.getText().toString();

            Stmt stmt = database.prepare("Select * from Forester WHERE Serial=" + id);

            if (stmt.step()) {
                int foresterID = stmt.column_int(0);
                // appel la map
                Intent intent = new Intent(LoginActivity.this, MapsActivity.class);
                intent.putExtra(SERIAL, id);
                startActivity(intent);
            }
            else
            {
                Toast.makeText(this, "Please create new User!", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(this, CreateActivity.class);
                startActivity(intent);
            }

        } catch (jsqlite.Exception e) {
            e.printStackTrace();
            System.exit(0);
        }

    }
}


