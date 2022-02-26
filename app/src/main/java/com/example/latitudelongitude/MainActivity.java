package com.example.latitudelongitude;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Button button= (Button)findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), MapsActivity.class);
                startActivity(i);

                //For shape buffer
                //Coordinate c = new Coordinate(10,10);
                //Geometry g = new GeometryFactory().createPolygon()

                // Create a new user with a first and last name
                //FirebaseFirestore db = FirebaseFirestore.getInstance();
                //Map<String, Object> user = new HashMap<>();
                //user.put("first", "Ada");
                //user.put("last", "Lovelace");
                //user.put("born", 1815);
            }
        });
    }


}
