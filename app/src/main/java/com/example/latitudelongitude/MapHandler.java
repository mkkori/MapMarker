package com.example.latitudelongitude;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.LocationManager;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.spatial4j.context.SpatialContext;
import org.locationtech.spatial4j.io.GeohashUtils;
import org.locationtech.spatial4j.shape.Rectangle;

import java.io.IOException;
import java.util.ArrayList;

public class MapHandler {
    GoogleMap mMap;
    SupportiveObject supportiveObject;
    UserObjects userObjects;
    Context context;
    MapsActivity mapsActivity;

    GoogleMap.OnPolygonClickListener listenerPolyClick ;
    GoogleMap.OnPolylineClickListener listenerLineClick;
    GoogleMap.OnCameraMoveListener listenerCameraMove ;
    GoogleMap.OnMapClickListener listenerOnMapClick ;


    public MapHandler(SupportiveObject supportiveObject, GoogleMap mMap, UserObjects userObjects,
                      Context context,MapsActivity mapsActivity){
        this.supportiveObject = supportiveObject;
        this.mMap = mMap;
        this.userObjects = userObjects;
        this.context = context;
        this.mapsActivity = mapsActivity;

        createListeners(this.supportiveObject,this.mMap,this.userObjects,context);
        setListeners();
    }

    public void createListeners(SupportiveObject supportiveObject, GoogleMap mMap, UserObjects userObjects,
                                Context context){
        listenerPolyClick = polygon -> Toast.makeText(context,polygon.getId(),Toast.LENGTH_SHORT).show();

        listenerLineClick = polyline -> Toast.makeText(context,polyline.getId(),Toast.LENGTH_SHORT).show();

        listenerCameraMove = () -> {
            Polygon tempPolygon = null;
            Polyline tempLine = null;
            ArrayList<LatLng> tempShapeLatLng;

            if(userObjects.currShapeLatLng.size()!=0 && !userObjects.bool_drawingPoint){
                LatLng mapLatLong = new LatLng(mMap.getCameraPosition().target.latitude,
                        mMap.getCameraPosition().target.longitude);
                tempShapeLatLng = (ArrayList<LatLng>) userObjects.currShapeLatLng.clone();
                tempShapeLatLng.add(mapLatLong);

                if(userObjects.bool_drawingPoly){
                    if(userObjects.currPolygon != null){
                        tempPolygon = userObjects.currPolygon;
                    }
                    userObjects.currPolygon = mMap.addPolygon(new PolygonOptions().addAll(tempShapeLatLng)
                            .fillColor(0x33FF0000).strokeColor(Color.BLACK).strokeWidth(3.0f)
                            .clickable(false));
                    if (tempPolygon != null){
                        tempPolygon.remove();
                    }
                }

                if(userObjects.bool_drawingLine){
                    if(userObjects.currLine != null){
                        tempLine = userObjects.currLine;
                    }
                    userObjects.currLine = mMap.addPolyline(new PolylineOptions().addAll(tempShapeLatLng)
                            .color(0x33FF0000).width(5.0f)
                            .clickable(false).jointType(2));
                    if (tempLine != null){
                        tempLine.remove();
                    }
                }
            }
        };

        listenerOnMapClick = point -> {
            Coordinate currPointCoordinate = new Coordinate(point.latitude,point.longitude);
            if(userObjects.FinishedPoly.size()!=0){
                Geometry polyGeometry = supportiveObject.getGeometryFromPoly(userObjects.FinishedPoly.get(0));
                GeometryFactory geoFactory = new GeometryFactory();
                Boolean isContained = polyGeometry.contains(geoFactory.createPoint(currPointCoordinate));
                Toast.makeText(context,
                        point.latitude + ", " + point.longitude + " "+ isContained,
                        Toast.LENGTH_SHORT).show();
            }
        };
    }

    public void setListeners(){
        this.mMap.setOnPolygonClickListener(listenerPolyClick);
        this.mMap.setOnPolylineClickListener(listenerLineClick);
        this.mMap.setOnMapClickListener(listenerOnMapClick);
        this.mMap.setOnCameraMoveListener(listenerCameraMove);
    }

    public void populateInitialMap(){
        try{
            int permission_status = context.checkCallingOrSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
            if(permission_status == PackageManager.PERMISSION_DENIED){
                String[] perms = {Manifest.permission.ACCESS_FINE_LOCATION};
                ActivityCompat.requestPermissions(mapsActivity,perms,200);

            } else if (permission_status == PackageManager.PERMISSION_GRANTED){
                userObjects.currLocation = mapsActivity.locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                mapsActivity.textLatLong.setText(userObjects.currLocation.getLatitude()+","+userObjects.currLocation.getLongitude());
                LatLng curr = new LatLng(userObjects.currLocation.getLatitude(),userObjects.currLocation.getLongitude());
                mMap.addMarker(new MarkerOptions().position(curr).title("Current Location")
                        .icon(supportiveObject.bitmapDescriptorFromVector(context,R.drawable.ic_shape)));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(curr,16.0f));

                //Starting Thread to set other parameters
                threadGetLocationParameters getLocationParams = new threadGetLocationParameters();
                getLocationParams.start();
            }

        }catch (Exception e){
            Toast.makeText(context,"Hello",Toast.LENGTH_LONG).show();
            Log.e("ErrorText",e.getMessage());
        }
    }

    public void handleRequestPermission(int requestCode, int[] grantResults){
        switch (requestCode){
            case 200:
                if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    if(mapsActivity.checkCallingOrSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) ==
                            PackageManager.PERMISSION_GRANTED){
                        userObjects.currLocation = mapsActivity.locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        mapsActivity.textLatLong.setText(userObjects.currLocation.getLatitude()+","
                                + userObjects.currLocation.getLongitude());
                        LatLng curr = new LatLng(userObjects.currLocation.getLatitude(),
                                userObjects.currLocation.getLongitude());
                        mMap.addMarker(new MarkerOptions().position(curr).title("Current Location"));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(curr,16.0f));

                        //Starting Thread to set other parameters

                        threadGetLocationParameters getLocationParams = new threadGetLocationParameters();
                        getLocationParams.start();
                    }
                }
        }
    }

    public void drawFinishedShape() {
        if(userObjects.currLine!=null){
            userObjects.FinishedLine.add(mMap.addPolyline(new PolylineOptions().addAll(userObjects.currShapeLatLng).
                    color(0x7048FE4F).width(6.0f)
                    .clickable(true)));
            userObjects.currLine.remove();
            userObjects.currLine = null;
            Log.e("Error","CurrLine not null");
            supportiveObject.moveCameraToCenterOfPoly(userObjects.FinishedLine.get(userObjects.FinishedLine.size()-1)
                    .getPoints(),mMap);
        }
        if(userObjects.currPolygon!=null){
            userObjects.FinishedPoly.add(mMap.addPolygon(new PolygonOptions().addAll(userObjects.currShapeLatLng).
                    fillColor(0x3348FE4F).strokeColor(Color.BLACK).strokeWidth(3.0f)
                    .clickable(true)));
            userObjects.currPolygon.remove();
            userObjects.currPolygon = null;
            Log.e("Error","CurrPoly not null");
            supportiveObject.moveCameraToCenterOfPoly(userObjects.FinishedPoly.get(userObjects.FinishedPoly.size()-1)
                    .getPoints(),mMap);
        }
        userObjects.currShapeLatLng = new ArrayList<>();
        supportiveObject.makeMarkerInvisible(userObjects.currMarkers);
        userObjects.currMarkers = new ArrayList<>();
        mapsActivity.textTopHeader.setText("Set Properties");
        mapsActivity.textTopSubheader.setText("Enter all the relevant attributes about this location");

        mapsActivity.userObjects.setAndShowProps(mapsActivity);
        mapsActivity.viewHandler.showBottomSheet();

    }

    // Parallel Thread to populate the extra location parameters and populate the UI
    class threadGetLocationParameters extends Thread{
        @Override
        public void run() {
            try {
                Address address = mapsActivity.geocoder.getFromLocation(userObjects.currLocation.getLatitude(),
                        userObjects.currLocation.getLongitude(),1).get(0);
                final String locality = address.getLocality();
                final String subLocality = address.getSubLocality();
                final String postalCode = address.getPostalCode();
                final String adminArea = address.getAdminArea();
                final String geohash = GeohashUtils.encodeLatLon(userObjects.currLocation.getLatitude(),
                        userObjects.currLocation.getLongitude(),5);

                SpatialContext sc = SpatialContext.GEO ;
                Rectangle r = GeohashUtils.decodeBoundary(geohash,sc);
                final LatLng topleft = new LatLng(r.getMinY(),r.getMinX());
                final LatLng topright = new LatLng(r.getMaxY(),r.getMinX());
                final LatLng bottomleft = new LatLng(r.getMinY(),r.getMaxX());
                final LatLng bottomright = new LatLng(r.getMaxY(),r.getMaxX());

                mapsActivity.runOnUiThread(new Runnable() {
                    public void run() {
                        mapsActivity.textGeohash.setText(adminArea + " "+postalCode + " " + subLocality + " "+locality+ " "+geohash);
                        mMap.addPolygon((new PolygonOptions()).add(topleft,topright,bottomright,bottomleft));
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
