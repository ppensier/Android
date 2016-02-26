package eu.ensg.forester;

import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;

import java.io.IOException;

import eu.ensg.spatialite.GPSUtils;
import eu.ensg.spatialite.SpatialiteDatabase;
import eu.ensg.spatialite.SpatialiteOpenHelper;
import eu.ensg.spatialite.geom.BadGeometryException;
import eu.ensg.spatialite.geom.Point;
import eu.ensg.spatialite.geom.XY;
import eu.ensg.spatialite.geom.Polygon;
import jsqlite.Stmt;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener {

    public static final float ZOOM_INIT = 10f;
    private GoogleMap mMap;
    private Point currentPosition = new Point(2, 48);
    private TextView positionLabel;
    private Polygon currentSector;
    private boolean isRecording = false;
    private LinearLayout rl;
    private SpatialiteDatabase database;
    private String IDforester;
    final String SERIAL = "string";
    public static final int GPS_SRID = 4326;
    private com.google.android.gms.maps.model.Polygon currentPolygon;
    private Button save;
    private Button abort;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        positionLabel = (TextView) findViewById(R.id.position);
        rl = (LinearLayout) findViewById(R.id.layout);
//        if(isRecording) {
//            LinearLayout rl = (LinearLayout) findViewById(R.id.layout);
//            rl.setBackgroundColor(Color.WHITE);
//            rl.setAlpha((float) 0.6);
//            rl.setVisibility(View.VISIBLE);
//        }

        save = (Button) findViewById(R.id.id_save);
        abort = (Button) findViewById(R.id.id_abort);

        save.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        save_onClick(v);

                                    }

                                }

        );

        abort.setOnClickListener(new View.OnClickListener() {


                                     @Override
                                     public void onClick(View v) {
                                         abort_onClick(v);
                                     }
                                 }



        );

        //recupération de l'ID du forrestier de l'activité Login
        Intent intent = getIntent();
        IDforester = intent.getStringExtra(SERIAL);
        initDatabase();

    }

    private void abort_onClick(View v) {
        isRecording = false;
        //on masque la barre
        rl.setVisibility(View.GONE);

    }

    private void save_onClick(View v) {

        //on stoppe l'enregistrement
        isRecording = false;
        //on masque la barre
        rl.setVisibility(View.GONE);

//        currentSector.getCoordinates().size();
        //Toast.makeText(this, currentSector.getCoordinates().size(), Toast.LENGTH_LONG).show();
        Log.d("SIZE", "" + currentSector.getCoordinates());
        add_sector_db("District", "Current District", currentSector);

    }

    private void add_sector_db(String name, String description, Polygon polygon) {

        try {
            database.exec("INSERT INTO District (ForesterID, Name, Description, Area) VALUES ('" +
                    IDforester + "', '" +
                    name + "', '" +
                    description + "', " +
                    polygon.toSpatialiteQuery(GPS_SRID) + ") ");
        }
        catch (jsqlite.Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Sql Error !!!!", Toast.LENGTH_LONG).show();
            //System.exit(0);
        }
        catch (BadGeometryException e) {
            e.printStackTrace();
            Toast.makeText(this, "Polygon marshalling Error !!!!", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //modification des coordonnées GPS
        GPSUtils.requestLocationUpdates(this, this);

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        moveTo(currentPosition);
        zoomTo(ZOOM_INIT);

        loadPointOfInterests();
        loadDistricts();
    }

    private void loadPointOfInterests() {
        try {
            Stmt stmt = database.prepare("SELECT name, description, ST_asText(position) FROM PointOfInterest WHERE foresterID = " + IDforester);
            while (stmt.step()) {
                String name = stmt.column_string(0);
                String description = stmt.column_string(1);
                Point position = Point.unMarshall(stmt.column_string(2));

                //on ajoute les points à la carte
                add_poi_point(position);

            }
        } catch (jsqlite.Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Sql Error !!!!", Toast.LENGTH_LONG).show();
        }
    }

    private void loadDistricts() {
        try {
            Stmt stmt = database.prepare("SELECT name, ST_asText(area) as area FROM District WHERE foresterID = " + IDforester);
            while (stmt.step()) {

                Polygon polygon = Polygon.unMarshall(stmt.column_string(1));


                addPolygon(polygon);
            }
        } catch (jsqlite.Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Sql Error !!!!", Toast.LENGTH_LONG).show();
        }
    }

    private void moveTo(Point position) {

        // positionnement initial
        mMap.moveCamera(CameraUpdateFactory.newLatLng(position.toLatLng()));

    }

    private void zoomTo(float zoom) {
        // animation
        mMap.animateCamera(CameraUpdateFactory.zoomTo(zoom), 2000, null);
    }

    @Override
    public void onLocationChanged(Location location) {

        String longitude = "Longitude: " + location.getLongitude();
        Log.v("LOCATION", longitude);
        String latitude = "Latitude: " + location.getLatitude();
        Log.v("location", latitude);

        currentPosition = new Point(new XY(location));
        positionLabel.setText(currentPosition.toString());
        moveTo(currentPosition);

        if (isRecording){
            currentSector.addCoordinate(currentPosition.getCoordinate());
            drawPolygon(currentSector);
            //currentSector
        }
    }

    public void drawPolygon(Polygon geom) {
        // on  réecrit le secteur à chaque fois
        if (currentPolygon != null) currentPolygon.remove();
        currentPolygon = addPolygon(geom);
    }

    public com.google.android.gms.maps.model.Polygon addPolygon(Polygon geom) {

        PolygonOptions options = new PolygonOptions();

        //on boucle sur tous les sommet du polygone
        for (XY xy : geom.getCoordinates().getCoords()) {
            options.add(new LatLng(xy.getY(), xy.getX()));
        }

        options.strokeWidth(5).strokeColor(Color.BLUE).geodesic(true);

        return mMap.addPolygon(options);

    }


    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_map, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item)
    {
        switch(item.getItemId())
        {
            case R.id.action_switch_type:
                //Dans le Menu "m", on active tous les items dans le groupe d'identifiant "R.id.group2"
                switch_menu(item);
                return true;
            case R.id.action_add_poi:
                add_poi(item);
                return true;
            case R.id.action_add_sector:
                add_sector(item);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void add_sector(MenuItem item) {
        isRecording = true;
        currentSector = new Polygon();
        rl.setVisibility(View.VISIBLE);
    }

    private void add_poi(MenuItem item) {
        add_poi_point(currentPosition);
        moveTo(currentPosition);
        zoomTo(10);

        add_poi_db("Point of Interest", currentPosition.toString(), currentPosition);

    }

    private void add_poi_db(String name, String description, Point position) {

        try {
            database.exec("INSERT INTO PointOfInterest (ForesterID, Name, Description, Position) VALUES (" +
                    IDforester + ", '" +
                    name + "', '" +
                    position.toString() + "', " +
                    position.toSpatialiteQuery(GPS_SRID) + ") ");
        }
        catch (jsqlite.Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Sql Error !!!!", Toast.LENGTH_LONG).show();
            //System.exit(0);
        }
        catch (BadGeometryException e) {
            e.printStackTrace();
            Toast.makeText(this, "Polygon marshalling Error !!!!", Toast.LENGTH_LONG).show();
        }
    }

    private void add_poi_point(Point point) {
        mMap.addMarker(new MarkerOptions()
                        .position(point.toLatLng())
                        .title("Point of interest")
                        .snippet(currentPosition.toString())
                        //.draggable(true)
        );
    }

    private void switch_menu(MenuItem item) {
        if (mMap.getMapType() == GoogleMap.MAP_TYPE_NORMAL){
            mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        }
        else if (mMap.getMapType() == GoogleMap.MAP_TYPE_SATELLITE){
            mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        }
        else if(mMap.getMapType() == GoogleMap.MAP_TYPE_HYBRID){
            mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        }
        else if(mMap.getMapType() == GoogleMap.MAP_TYPE_TERRAIN){
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        }
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

}
