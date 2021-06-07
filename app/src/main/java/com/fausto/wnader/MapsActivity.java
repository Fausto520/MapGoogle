package com.fausto.wnader;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.StreetViewPanoramaOptions;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.SupportStreetViewPanoramaFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PointOfInterest;
import com.google.android.gms.maps.model.TileOverlayOptions;

import java.util.Locale;
import java.util.zip.Inflater;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = SupportMapFragment.newInstance();
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, mapFragment). commit();
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
        int zoom = 15;


        // Add a marker in Sydney and move the camera
       /* LatLng panecillo = new LatLng(-0.23006914436805273, -78.51932290832588);
        mMap.addMarker(new MarkerOptions().position(panecillo).title("Ecuador, Panecillo"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(panecillo));*/

       LatLng home = new LatLng(43.34006775765631, -1.796057701756508);
       //mMap.addMarker(new MarkerOptions().position(home).title("Home"));
       mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(home,zoom));


      GroundOverlayOptions homeOverlay = new GroundOverlayOptions().image(BitmapDescriptorFactory.fromResource(R.drawable.android)).position(home,100);
        mMap.addGroundOverlay(homeOverlay);


       try {
           boolean success = googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this,R.raw.map_style));
           if(!success){
               Log.e("error","Error al cargar el estilo");
           }

       }catch (Resources.NotFoundException e){
           Log.e("error carga","Error al cargar el estilo");
       }


       setMapLongClick(mMap);
       enableMyLocation();
       setInfoWindowClickToPanorama(mMap);
        setPoiClick(mMap);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.map_opciones, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){

            case R.id.normal_map:
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            return  true;

            case  R.id.hybrid_map:
                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                return  true;

            case R.id.satellite_map:
                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                return true;

            case  R.id.terrain_map:
                mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                return  true;

            default:
                return  super.onOptionsItemSelected(item);


        }
    }

private void setMapLongClick(final GoogleMap map){
map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

    @Override
    public void onMapClick(LatLng latLng) {
        String snippet = String.format(Locale.getDefault(),"Lat:%1$.5f  Long: %2$.5f", latLng.latitude,latLng.longitude);
        map.addMarker(new MarkerOptions().position(latLng).title(getString(R.string.prueba)).snippet(snippet).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
    }
});

}
private void setPoiClick(final GoogleMap map){
        map.setOnPoiClickListener(new GoogleMap.OnPoiClickListener() {
            @Override
            public void onPoiClick(PointOfInterest poi) {

                Marker poiMarker = mMap.addMarker(new MarkerOptions().position(poi.latLng).title(poi.name).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)));
                poiMarker.showInfoWindow();

                poiMarker.setTag("poi");
            }
        });


}

private void enableMyLocation(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            mMap.setMyLocationEnabled(true);

    }else {

            ActivityCompat.requestPermissions(this, new String[]
                            {Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        }
}

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_LOCATION_PERMISSION:
                if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    enableMyLocation();
                    break;

                }
        }

    }

    private void setInfoWindowClickToPanorama(GoogleMap map){
        map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(@NonNull Marker marker) {
                if(marker.getTag() == "poi"){

                    StreetViewPanoramaOptions options = new StreetViewPanoramaOptions().position(marker.getPosition());

                    SupportStreetViewPanoramaFragment streetViewFragment = SupportStreetViewPanoramaFragment.newInstance(options);

                   getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, streetViewFragment).addToBackStack(null).commit();


                }
            }
        });
    }


}
