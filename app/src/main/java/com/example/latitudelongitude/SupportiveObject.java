package com.example.latitudelongitude;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.util.Xml;

import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polygon;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.CoordinateList;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

public class SupportiveObject {
    public Geometry getGeometryFromPoly(Polygon g){
        CoordinateList c = new CoordinateList();
        GeometryFactory gf =  new GeometryFactory();
        List<LatLng> l =  g.getPoints();
        for(int i=0;i<l.size();i++){
            Coordinate cc = new Coordinate(l.get(i).latitude,l.get(i).longitude);
            c.add(cc);
        }
        return(gf.createPolygon(c.toCoordinateArray()));
    }

    public void moveCameraToCenterOfPoly(List<LatLng> latLngs, GoogleMap map ) {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for(LatLng i : latLngs){
            builder.include(i);
        }
        LatLngBounds bounds = builder.build();
        int padding = 100;
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds,padding);
        map.animateCamera(cu);
    }


    public String createKmlFromPoly(Polygon p ){
        Polygon tempPoly = p;
        XmlSerializer xml = Xml.newSerializer();
        StringWriter writer = new StringWriter();
        try {
            xml.setOutput(writer);

            //StartingDocument
            xml.startDocument("UTF-8", true);
            //Open Tag <kml>
            xml.startTag("","kml");
            xml.attribute("","xmlns","http://www.opengis.net/kml/2.2");
            xml.startTag("","Document");
            xml.startTag("","name");
            xml.text("Delhi");
            xml.endTag("","name");
            xml.startTag("","Placemark");
            xml.startTag("","name");
            xml.text("Delhi");
            xml.endTag("","name");

            xml.startTag("","Polygon");
            xml.startTag("","outerBoundaryIs");
            xml.startTag("","LinearRing");
            xml.startTag("","coordinates");

            xml.text(getCoordinateString(tempPoly.getPoints()));

            xml.endTag("","coordinates");
            xml.endTag("","LinearRing");
            xml.endTag("","outerBoundaryIs");
            xml.endTag("","Polygon");
            xml.endTag("","Placemark");
            xml.endTag("","Document");
            xml.endTag("","kml");
            xml.endDocument();
            Log.e("KML", writer.toString());
            return writer.toString();

        } catch (IOException e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }

    private String getCoordinateString(List<LatLng> latLng){
        String coordinateString = "";
        for(LatLng t:latLng){
            coordinateString = coordinateString+t.longitude+","+t.latitude+" ";
        }
        return coordinateString;
    }

    public void makeMarkerInvisible(ArrayList<Marker> markerList){
        for(Marker i : markerList){
            i.setVisible(false);
        }
    }

    public void removeMarkers(ArrayList<Marker> markerList){
        for(Marker i : markerList){
            i.remove();
        }
    }

    public BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }
}
