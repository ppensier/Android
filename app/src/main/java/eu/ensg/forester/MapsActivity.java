package eu.ensg.forester;

import android.location.Location;
import android.location.LocationListener;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import eu.ensg.spatialite.geom.Point;
import eu.ensg.spatialite.geom.XY;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener {

    public static final float ZOOM_INIT = 10f;
    private GoogleMap mMap;
    private Point currentPosition = new Point(2, 48);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        moveTo(currentPosition);
        zoomTo(ZOOM_INIT);
    }

    private void moveTo(Point position) {

        // positionnement initial
        mMap.moveCamera(CameraUpdateFactory.newLatLng(position.toLatLng()));

    }

    private void zoomTo(float zoom) {
        // animation
        mMap.animateCamera(CameraUpdateFactory.zoomTo(zoom), 10000, null);
    }

    @Override
    public void onLocationChanged(Location location) {
        currentPosition = new Point(new XY(location));
        //positionLabel.setText(currentPosition.toString());

        //if (isRecording == true) {
            //currentSector.addCoordinate(new XY(location));
            //drawPolygon(currentSector);
            moveTo(currentPosition);
        //}
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

}
