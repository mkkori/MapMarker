package com.example.latitudelongitude;


import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.Polyline;

import java.util.ArrayList;
import java.util.List;

public class FirebaseObject {
    public String userName;
    public List<FBFinishedLine> finishedLineList;
    public List<FBFinishedPoly> finishedPolyList;
    public List<FBFinishedPoint> finishedPointList;

    public FirebaseObject(UserObjects userObjects){
        this.userName = "Rohit";
        this.finishedLineList = getFinishedLines(userObjects);
        this.finishedPolyList = getFinishedPoly(userObjects);
        this.finishedPointList = getFinishedPoint(userObjects);
    }

    public FirebaseObject(){
        this.userName = null;
        this.finishedLineList = null;
        this.finishedPolyList = null;
        this.finishedPointList = null;
    }

    public List<FBFinishedLine> getFinishedLines(UserObjects userObjects) {
        List<FBFinishedLine> finishedList = new ArrayList<>();
        for(Polyline p : userObjects.FinishedLine){
            finishedList.add(new FBFinishedLine(p.getId(),convertPointToString(p.getPoints())));
        }
        return finishedList;
    }

    public List<FBFinishedPoly> getFinishedPoly(UserObjects userObjects) {
        List<FBFinishedPoly> finishedList = new ArrayList<>();
        for(Polygon p : userObjects.FinishedPoly){
            finishedList.add(new FBFinishedPoly(p.getId(),convertPointToString(p.getPoints())));
        }
        return finishedList;
    }

    public List<FBFinishedPoint> getFinishedPoint(UserObjects userObjects) {
        List<FBFinishedPoint> finishedList = new ArrayList<>();
        for(Marker p : userObjects.FinishedPoint){
            finishedList.add(new FBFinishedPoint(p.getId(),p.getPosition().latitude+","
                    +p.getPosition().longitude));
        }
        return finishedList;
    }

    public String convertPointToString(List<LatLng> points){
        String latLongString = "";
        for(LatLng latLng : points){
            latLongString = latLongString+"#"+latLng.latitude+","+latLng.longitude;
        }
        return latLongString;
    }
}

class FBFinishedLine{
    public String name;
    public String latLong;

    public FBFinishedLine(String name, String latLong){
        this.name = name;
        this.latLong = latLong;
    }

    public FBFinishedLine(){
        this.name = null;
        this.latLong = null;
    }
}

class FBFinishedPoly{
    public String name;
    public String latLong;

    public FBFinishedPoly(String name, String latLong){
        this.name = name;
        this.latLong = latLong;
    }

    public FBFinishedPoly(){
        this.name = null;
        this.latLong = null;
    }
}

class FBFinishedPoint{
    public String name;
    public String latLong;

    public FBFinishedPoint(String name, String latLong){
        this.name = name;
        this.latLong = latLong;
    }

    public FBFinishedPoint(){
        this.name = null;
        this.latLong = null;
    }
}
