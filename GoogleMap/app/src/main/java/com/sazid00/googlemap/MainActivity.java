package com.sazid00.googlemap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.net.ssl.SSLEngineResult;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    GoogleMap map;
    EditText text;
    private static int AUTOCOMPLETE_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        supportMapFragment.getMapAsync(this);


        permission();
        text = findViewById(R.id.textET);


        Places.initialize(getApplicationContext(), "AIzaSyAOEy3eToGSCR87zSIztCE4JyVFPoAUpjE");

        autocomplete();


    }

    private void autocomplete() {

        text.setFocusable(false);
        text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<Place.Field> fields = Arrays.asList(Place.Field.ADDRESS,Place.Field.LAT_LNG, Place.Field.NAME);
                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
                        .build(MainActivity.this);
                startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if(resultCode==AUTOCOMPLETE_REQUEST_CODE)
        {
            if(resultCode==RESULT_OK)
            {
                Place place = Autocomplete.getPlaceFromIntent(data);
                text.setText(place.getAddress());
            }
        }
        else if(resultCode == AutocompleteActivity.RESULT_ERROR)
        {
            Status status = Autocomplete.getStatusFromIntent(data);
            Toast.makeText(this, "Error", Toast.LENGTH_LONG).show();
        }
        else if(resultCode == RESULT_CANCELED)
        {
            //Result are not view ....................
        }


        super.onActivityResult(requestCode, resultCode, data);
    }

    private void permission() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},0);
            }
            return;
        }

    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

        map = googleMap;

        LatLng bd = new LatLng(23.71887602920471, 90.3881733367939);

        googleMap.addMarker(new MarkerOptions().position(bd).title("Dhaka").snippet("Historical Place"));

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(bd, 16));


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            permission();
            return;
        }
        googleMap.setMyLocationEnabled(true);

        googleMap.getUiSettings().setMyLocationButtonEnabled(false);


        searchLocation();


    }

    private void searchLocation() {

        text.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {


                if(i == EditorInfo.IME_ACTION_SEARCH|| i==EditorInfo.IME_ACTION_DONE||keyEvent.getAction() == KeyEvent.ACTION_DOWN||keyEvent.getAction()==KeyEvent.KEYCODE_ENTER)
                {
                    String search = text.getText().toString();
                    Geocoder geocoder = new Geocoder(MainActivity.this);
                    List<Address>location = new ArrayList<>();

                    try {
                        location =  geocoder.getFromLocationName(search,1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if(location.size()>0)
                    {
                        Address address = location.get(0);

                        LatLng latLng = new LatLng(address.getLatitude(),address.getLongitude());

                        map.addMarker(new MarkerOptions().position(latLng).title(address.getAddressLine(0)).snippet("Country name"+address.getCountryName()+"\nZip code"+address.getPostalCode()));
                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,15));
                    }


                }



                return false;
            }
        });


    }

    private void createLoaction() {

        FusedLocationProviderClient fusedLocationProviderClient = new FusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            permission();
            return;
        }
        Task location = fusedLocationProviderClient.getLastLocation();


        location.addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {

               Location current_location = (Location) task.getResult();

               map.addMarker(new MarkerOptions().position(new LatLng(current_location.getLatitude(),current_location.getLongitude())));
               map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(current_location.getLatitude(),current_location.getLongitude()),14));



            }
        });



    }

    public void createLocationMap(View view) {


        createLoaction();

    }
}