package com.example.latitudelongitude;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Polygon;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,View.OnClickListener {

    GoogleMap mMap;
    Geocoder geocoder;
    LocationManager locationManager;
    SupportiveObject supportiveObject;
    UserObjects userObjects;
    MapHandler mapHandler;
    ViewHandler viewHandler;

    //All Buttons and Texts
    Button buttCenter;
    Button buttStartLine;
    Button buttStartPoint;
    Button buttStartPoly;
    Button buttDone;
    Button buttUndone;
    Button buttSave;

    TextView textLatLong;
    TextView textGeohash;
    TextView textTopHeader;
    TextView textTopSubheader;

    Fragment topCard;
    CardView bottomCard;

    DatabaseReference firebaseDb;

    //All Button Clicks Handling
    @Override
    public void onClick(View view) {
        Log.e("abc",Integer.toString(view.getId()));
        viewHandler.clickHandler(view);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        supportiveObject = new SupportiveObject();
        userObjects = new UserObjects();

        // Firebase Handling
        firebaseDb = FirebaseDatabase.getInstance("https://mapproject-7af8b-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference();

        // Setting up all the buttons and the relevant click listeners
        buttStartPoint = findViewById(R.id.butt_point);
        buttStartLine = findViewById(R.id.butt_line);
        buttStartPoly = findViewById(R.id.butt_poly);
        buttCenter = findViewById(R.id.butt_center);
        buttDone = findViewById(R.id.butt_done);
        buttUndone = findViewById(R.id.butt_undone);
        buttSave = findViewById(R.id.butt_save);
        textLatLong = findViewById(R.id.text_latlong);
        textGeohash = findViewById(R.id.text_geohash);

        buttStartPoint.setOnClickListener(this);
        buttStartLine.setOnClickListener(this);
        buttStartPoly.setOnClickListener(this);
        buttCenter.setOnClickListener(this);
        buttUndone.setOnClickListener(this);
        buttDone.setOnClickListener(this);
        buttSave.setOnClickListener(this);

        topCard = getSupportFragmentManager().findFragmentById(R.id.frag_topCard);
        bottomCard = findViewById(R.id.frag_bottomCard);
        textTopHeader = topCard.getView().findViewById(R.id.text_fragHeader);
        textTopSubheader = topCard.getView().findViewById(R.id.text_fragSubHeader);

        geocoder = new Geocoder(this, Locale.getDefault());
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    }

    // Runs the method once the map is ready
    @Override
    public void onMapReady(final GoogleMap googleMap) {
        mMap = googleMap;
        mapHandler = new MapHandler(supportiveObject,mMap,userObjects,getApplicationContext(),this);
        viewHandler = new ViewHandler(this);

        //Reading Shared Prefs
        //viewHandler.readSharedPrefs();

        //SetupInitialScreen
        viewHandler.changeTopBarColor();
        viewHandler.setupInitialScreen();
        mapHandler.populateInitialMap();
    }

    // Responds to user permission
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        mapHandler.handleRequestPermission(requestCode, grantResults);
    }
}