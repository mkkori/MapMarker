package com.example.latitudelongitude;

import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;

import java.io.IOException;
import java.util.ArrayList;

public class ViewHandler {

    MapsActivity mapsActivity;

    public ViewHandler(MapsActivity mapsActivity){
        this.mapsActivity = mapsActivity;
    }

    public void showMapActionButtons(){
        mapsActivity.buttDone.setVisibility(View.VISIBLE);
        mapsActivity.buttUndone.setVisibility(View.VISIBLE);
    }

    public void hideMapActionButtons(){
        mapsActivity.buttDone.setVisibility(View.INVISIBLE);
        mapsActivity.buttUndone.setVisibility(View.INVISIBLE);
    }

    public void hideRelevantButtons(String shapeClicked){
        switch(shapeClicked){
            case "line":
                mapsActivity.buttStartPoly.setVisibility(View.INVISIBLE);
                mapsActivity.buttStartPoint.setVisibility(View.INVISIBLE);
                break;
            case "poly":
                mapsActivity.buttStartLine.setVisibility(View.INVISIBLE);
                mapsActivity.buttStartPoint.setVisibility(View.INVISIBLE);
                break;
            case "point":
                mapsActivity.buttStartLine.setVisibility(View.INVISIBLE);
                mapsActivity.buttStartPoly.setVisibility(View.INVISIBLE);
                break;
            case "none":
                mapsActivity.buttStartLine.setVisibility(View.VISIBLE);
                mapsActivity.buttStartPoly.setVisibility(View.VISIBLE);
                mapsActivity.buttStartPoint.setVisibility(View.VISIBLE);
                break;
        }
    }

    public void clickHandler(View view){
        FragmentTransaction ft = mapsActivity.getSupportFragmentManager().beginTransaction();
        switch (view.getId()){
            case R.id.butt_center:
                if(mapsActivity.userObjects.bool_drawingPoly | mapsActivity.userObjects.bool_drawingLine){
                    LatLng a = new LatLng(mapsActivity.mMap.getCameraPosition().target.latitude,
                            mapsActivity.mMap.getCameraPosition().target.longitude);
                    mapsActivity.userObjects.currMarkers.add(mapsActivity.mMap.addMarker(new MarkerOptions().position(a)
                            .icon(mapsActivity.supportiveObject.bitmapDescriptorFromVector(mapsActivity.getApplicationContext(),
                                    R.drawable.ic_polymarker))
                            .anchor(0.5f,0.5f)));
                    mapsActivity.userObjects.currShapeLatLng.add(a);
                }
                else if(mapsActivity.userObjects.bool_drawingPoint){
                    LatLng b = new LatLng(mapsActivity.mMap.getCameraPosition().target.latitude,
                            mapsActivity.mMap.getCameraPosition().target.longitude);
                    mapsActivity.userObjects.FinishedPoint.add(mapsActivity.mMap.addMarker(new MarkerOptions().position(b)
                            .icon(mapsActivity.supportiveObject.bitmapDescriptorFromVector(mapsActivity.getApplicationContext(),
                                    R.drawable.ic_shape))));
                    hideRelevantButtons("none");
                    mapsActivity.userObjects.setDrawing("none");
                    mapsActivity.mMap.animateCamera(CameraUpdateFactory.zoomIn());
                    mapsActivity.textTopHeader.setText("Set Properties");
                    mapsActivity.textTopSubheader.setText("Enter all the relevant attributes about this location");
                } else {
                    getFirebaseData();
                }
                break;
            case R.id.butt_line:
                mapsActivity.userObjects.setDrawing("line");
                showMapActionButtons();
                hideRelevantButtons("line");
                ft.show(mapsActivity.topCard);
                ft.commit();
                mapsActivity.textTopHeader.setText("Drawing Line");
                mapsActivity.textTopSubheader.setText("Click on the point to start drawing..");
                break;
            case R.id.butt_point:
                mapsActivity.userObjects.setDrawing("point");
                hideRelevantButtons("point");
                ft.show(mapsActivity.topCard);
                ft.commit();
                mapsActivity.textTopHeader.setText("Drawing Point");
                mapsActivity.textTopSubheader.setText("Click on the point to start drawing..");
                break;
            case R.id.butt_poly:
                mapsActivity.userObjects.setDrawing("poly");
                hideRelevantButtons("poly");
                showMapActionButtons();
                ft.show(mapsActivity.topCard);
                ft.commit();
                mapsActivity.textTopHeader.setText("Drawing Polygon");
                mapsActivity.textTopSubheader.setText("Click on the point to start drawing..");
                break;
            case R.id.butt_done:
                if((mapsActivity.userObjects.currShapeLatLng.size()<2 & mapsActivity.userObjects.bool_drawingLine)|
                        (mapsActivity.userObjects.currShapeLatLng.size()<3 & mapsActivity.userObjects.bool_drawingPoly)){
                    Toast.makeText(mapsActivity.getApplicationContext(),
                            "The Shape Doesn't Have Enough Points.",Toast.LENGTH_LONG).show();
                } else {
                    mapsActivity.mapHandler.drawFinishedShape();
                    mapsActivity.userObjects.setDrawing("none");
                    hideRelevantButtons("none");
                    hideMapActionButtons();
                }
                break;
            case R.id.butt_undone:
                if(mapsActivity.userObjects.currPolygon!=null){
                    mapsActivity.userObjects.currPolygon.remove();
                    mapsActivity.userObjects.currPolygon = null;
                }
                if(mapsActivity.userObjects.currLine!=null){
                    mapsActivity.userObjects.currLine.remove();
                    mapsActivity.userObjects.currLine = null;
                }
                mapsActivity.supportiveObject.removeMarkers(mapsActivity.userObjects.currMarkers);
                mapsActivity.userObjects.currShapeLatLng = new ArrayList<>();
                mapsActivity.userObjects.setDrawing("none");
                hideRelevantButtons("none");
                hideMapActionButtons();
                ft.hide(mapsActivity.topCard);
                ft.commit();
                mapsActivity.textTopHeader.setText("");
                mapsActivity.textTopSubheader.setText("");
                break;

            // case R.id.butt_createKML:
            //     createKmlFromPoly();
            //     break;

            case R.id.butt_save:
                mapsActivity.firebaseDb.child("Users").child("Sukanya").setValue(new FirebaseObject(mapsActivity.userObjects));
                break;
        }

    }

    public void setupInitialScreen(){
        hideMapActionButtons();
        mapsActivity.textLatLong.setVisibility(View.INVISIBLE);
        mapsActivity.textGeohash.setVisibility(View.INVISIBLE);
        FragmentTransaction ft = mapsActivity.getSupportFragmentManager().beginTransaction();
        ft.hide(mapsActivity.topCard);
        ft.commit();
        mapsActivity.getLayoutInflater().inflate(R.layout.cardviewproperty,mapsActivity.bottomCard);
    }

    public void showBottomSheet(){
        //BottomSheetDialogueThatComesFromBelow
        //final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        //bottomSheetDialog.setContentView(R.layout.cardviewproperty);
        //bottomSheetDialog.show();

        //Card Property View
        mapsActivity.bottomCard.setVisibility(View.VISIBLE);
        mapsActivity.bottomCard.setAlpha(0.0f);
        mapsActivity.bottomCard.animate()
                .translationY(-1*mapsActivity.bottomCard.getHeight())
                .alpha(1.0f)
                .setDuration(500);
    }

    public void changeTopBarColor(){

        if (Build.VERSION.SDK_INT >= 21) {
            Window window = mapsActivity.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(mapsActivity.getResources().getColor(R.color.statusBarColor));
        }
    }

    public void SaveSharedPrefs(){
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mapsActivity.getApplicationContext());
        SharedPreferences.Editor editor = settings.edit();
        editor.remove("myObject").commit();
        try {
            editor.putString("myObject",ObjectSerializer.serialize(mapsActivity.userObjects.FinishedPoly));
            //https://examples.javacodegeeks.com/java-basics/exceptions/java-io-notserializableexception-how-to-solve-not-serializable-exception/
            Log.e("PrintPolyLength","stringPut");
        } catch (IOException e) {
            Log.e("PrintPolyLength","stringNotPut");
            e.printStackTrace();
        }
        editor.commit();
        //https://stackoverflow.com/questions/14981233/android-arraylist-of-custom-objects-save-to-sharedpreferences-serializable
    }

    public void readSharedPrefs(){
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mapsActivity.getApplicationContext());
        try {
            ArrayList<Polygon> spPoly = (ArrayList<Polygon>) ObjectSerializer.deserialize(settings.
                    getString("myObject", ObjectSerializer.serialize(new ArrayList<Polygon>())));
            Log.e("PrintPolyLength",Integer.toString(spPoly.size()));

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void getFirebaseData(){
        mapsActivity.firebaseDb.child("Users").child("Sukanya").get()
                .addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                FirebaseObject fb = task.getResult().getValue(FirebaseObject.class);
                Log.e("firebase",fb.finishedLineList.toString());
            }
        });
    }
}
