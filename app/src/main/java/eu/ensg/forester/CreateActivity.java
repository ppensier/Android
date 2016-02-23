package eu.ensg.forester;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class CreateActivity extends AppCompatActivity {

    final String SERIAL = "string";
    private Button mPasserelle2 = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        mPasserelle2 = (Button) findViewById(R.id.button2);
        final EditText serial = (EditText) findViewById(R.id.serial3);


        mPasserelle2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Le premier paramètre est le nom de l'activité actuelle
                // Le second est le nom de l'activité de destination
                Intent secondeActivite = new Intent(CreateActivity.this, LoginActivity.class);
                secondeActivite.putExtra(SERIAL, serial.getText().toString());
                // Puis on lance l'intent !
                startActivity(secondeActivite);
            }
        });

    }

}
