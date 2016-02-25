package eu.ensg.forester;

import android.content.Intent;
import android.database.DatabaseUtils;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;

import eu.ensg.forester.ForesterSpatialiteOpenHelper;
import eu.ensg.spatialite.SpatialiteDatabase;
import eu.ensg.spatialite.SpatialiteOpenHelper;

public class CreateActivity extends AppCompatActivity {

    final String SERIAL = "string";
    private Button mPasserelle2 = null;
    private SpatialiteDatabase database;

    private EditText last_name;
    private EditText first_name;
    private EditText serial;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        mPasserelle2 = (Button) findViewById(R.id.button2);
        serial = (EditText) findViewById(R.id.serial3);
        first_name = (EditText) findViewById(R.id.serial);
        last_name = (EditText) findViewById(R.id.serial2);


        mPasserelle2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                create_onClick(v);

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

    private void create_onClick(View v) {

        String id = serial.getText().toString();

        try {
            database.exec("INSERT INTO Forester (FirstName, LastName, Serial) " +
                    "VALUES ('" + first_name.getText().toString() + "', '" +
                    last_name.getText().toString() + "', '" +
                    serial.getText().toString() + "')");

//                    database.exec("INSERT INTO Forester (FirstName, LastName, Serial) " +
//                            "VALUES (" + DatabaseUtils.sqlEscapeString(editFirstName.getText().toString()) + ", " +
//                            DatabaseUtils.sqlEscapeString(editLastName.getText().toString()) + ", " +
//                            DatabaseUtils.sqlEscapeString(editSerial.getText().toString()) + ")");

            //redirection vers la page de login
            Intent secondeActivite = new Intent(CreateActivity.this, LoginActivity.class);
            secondeActivite.putExtra(SERIAL, serial.getText().toString());
            // Puis on lance l'intent !
            startActivity(secondeActivite);

        } catch (jsqlite.Exception e) {
            e.printStackTrace();
        }

    }

}

