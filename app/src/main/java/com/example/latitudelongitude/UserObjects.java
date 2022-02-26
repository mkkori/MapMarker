package com.example.latitudelongitude;

import android.location.Location;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.Polyline;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class UserObjects {
    Boolean bool_drawingLine;
    Boolean bool_drawingPoly;
    Boolean bool_drawingPoint;
    ArrayList<LatLng> currShapeLatLng;
    Polygon currPolygon;
    Polyline currLine;
    Location currLocation;

    ArrayList<Marker> currMarkers;

    //AllFinishedLinesAndPolygons
    ArrayList<Polygon> FinishedPoly;
    ArrayList<Polyline> FinishedLine;
    ArrayList<Marker> FinishedPoint;

    public UserObjects(){
        bool_drawingLine = false;
        bool_drawingPoly = false;
        bool_drawingPoint = false;
        currShapeLatLng = new ArrayList<>();
        currMarkers = new ArrayList<>();

        FinishedPoly = new ArrayList<>();
        FinishedLine = new ArrayList<>();
        FinishedPoint = new ArrayList<>();
    }

    public void setDrawing(String shapeType){
        switch(shapeType){
            case "line":
                this.bool_drawingLine = true;
                this.bool_drawingPoly = false;
                this.bool_drawingPoint = false;
                break;
            case "poly":
                this.bool_drawingLine = false;
                this.bool_drawingPoly = true;
                this.bool_drawingPoint = false;
                break;
            case "point":
                this.bool_drawingLine = false;
                this.bool_drawingPoly = false;
                this.bool_drawingPoint = true;
                break;
            case "none":
                this.bool_drawingLine = false;
                this.bool_drawingPoly = false;
                this.bool_drawingPoint = false;
        }
    }

    public String getNextName(){
        String next = Integer.toString(FinishedPoly.size()+FinishedPoint.size()+FinishedLine.size());
        return "Shape"+next;
    }

    public void setAndShowProps(MapsActivity mapsActivity){
        Polyproperties polyproperties = new Polyproperties();
        polyproperties.name = getNextName();

        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        polyproperties.date = dateFormat.format(date);


        polyproperties.description = "";
        SupportiveObject so = new SupportiveObject();
        polyproperties.perimeter = 0.0;
        polyproperties.phone = "";

        if(bool_drawingPoly){
            Polygon p =  mapsActivity.userObjects.FinishedPoly.get(mapsActivity.userObjects.FinishedPoly.size()-1);
            polyproperties.area =  so.getGeometryFromPoly(p).getArea();
            polyproperties.color = String.format("#%06X", (0xFFFFFF & p.getFillColor())) ;
            p.setTag(polyproperties);
        }
        if(bool_drawingLine){
            Polyline l = mapsActivity.userObjects.FinishedLine.get(mapsActivity.userObjects.FinishedLine.size()-1);
            polyproperties.area =  0.0;
            polyproperties.color = String.format("#%06X", (0xFFFFFF & l.getColor())) ;
            l.setTag(polyproperties);
        }
        if(bool_drawingPoint){
            polyproperties.area =  0.0;
            polyproperties.color = "#000000";
        }

        EditText edit_shapeName =  (EditText) mapsActivity.bottomCard.findViewById(R.id.edit_shapeName);
        EditText edit_shapeDesc =  (EditText) mapsActivity.bottomCard.findViewById(R.id.edit_shapeDesc);
        TextView text_shapeCreatedOnValue = (TextView) mapsActivity.bottomCard.findViewById(R.id.text_shapeCreatedOnValue);
        EditText edit_shapePhone =  (EditText) mapsActivity.bottomCard.findViewById(R.id.edit_shapePhone);
        TextView text_shapeDimensionsValue =  (TextView) mapsActivity.bottomCard.findViewById(R.id.text_shapeDimensionValue);

        edit_shapeName.setText(polyproperties.name);
        edit_shapeDesc.setText(polyproperties.description);
        text_shapeCreatedOnValue.setText(polyproperties.date);
        edit_shapePhone.setText(polyproperties.phone);
        text_shapeDimensionsValue.setText("Area: "  + polyproperties.area + " Perimeter: " + polyproperties.perimeter);
    }
}
