package com.example.dongja94.sampletmap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.skp.Tmap.TMapView;

public class MainActivity extends AppCompatActivity {

    TMapView mapView;
    LocationManager mLM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        mapView = (TMapView)findViewById(R.id.mapView);
        new RegisterTask().execute();

        mLM = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
    }

    boolean isInitialized = false;

    class RegisterTask extends AsyncTask<String,Void,Boolean> {
        @Override
        protected Boolean doInBackground(String... params) {
            mapView.setSKPMapApiKey("458a10f5-c07e-34b5-b2bd-4a891e024c2a");
            mapView.setLanguage(TMapView.LANGUAGE_KOREAN);
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            isInitialized = true;
            setupMap();
        }
    }

    Location cacheLocation;
    LocationListener mListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            if (isInitialized) {
                moveMap(location.getLatitude(), location.getLongitude());
                setMyLocation(location.getLatitude(), location.getLongitude());
            } else {
                cacheLocation = location;
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    private void moveMap(double lat, double lng) {
        mapView.setCenterPoint(lng, lat);
        mapView.setZoomLevel(17);
    }

    private void setMyLocation(double lat, double lng) {
        mapView.setLocationPoint(lng, lat);
        Bitmap bm = ((BitmapDrawable)getResources().getDrawable(R.drawable.my_icon)).getBitmap();
        mapView.setIcon(bm);
        mapView.setIconVisibility(true);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mLM.requestSingleUpdate(LocationManager.GPS_PROVIDER, mListener, null);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mLM.removeUpdates(mListener);
    }

    private void setupMap() {
        if (cacheLocation != null) {
            moveMap(cacheLocation.getLatitude(), cacheLocation.getLongitude());
            setMyLocation(cacheLocation.getLatitude(), cacheLocation.getLongitude());
            cacheLocation = null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
