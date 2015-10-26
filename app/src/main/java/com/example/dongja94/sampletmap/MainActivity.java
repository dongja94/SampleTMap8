package com.example.dongja94.sampletmap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
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
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.skp.Tmap.TMapData;
import com.skp.Tmap.TMapMarkerItem;
import com.skp.Tmap.TMapPOIItem;
import com.skp.Tmap.TMapPoint;
import com.skp.Tmap.TMapPolyLine;
import com.skp.Tmap.TMapView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    TMapView mapView;
    LocationManager mLM;
    ListView listView;
    EditText keywordView;
    ArrayAdapter<POIItem> mAdapter;
    RadioGroup group;

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
        listView = (ListView)findViewById(R.id.listView);
        keywordView = (EditText)findViewById(R.id.edit_keyword);
        group = (RadioGroup)findViewById(R.id.radioGroup);
        mAdapter = new ArrayAdapter<POIItem>(this, android.R.layout.simple_list_item_1);
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                POIItem item = (POIItem)listView.getItemAtPosition(position);
                moveMap(item.poi.getPOIPoint().getLatitude(), item.poi.getPOIPoint().getLongitude());
            }
        });

        new RegisterTask().execute();

        mLM = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

        Button btn = (Button)findViewById(R.id.btn_marker);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TMapPoint point = mapView.getCenterPoint();
                TMapMarkerItem item = new TMapMarkerItem();
                item.setTMapPoint(point);
                Bitmap icon = ((BitmapDrawable)getResources().getDrawable(android.R.drawable.ic_dialog_map)).getBitmap();
                item.setIcon(icon);
                item.setPosition(0.5f, 1);
                item.setCalloutTitle("My Marker");
                item.setCalloutSubTitle("Map Marker Test");
                Bitmap left = ((BitmapDrawable)getResources().getDrawable(android.R.drawable.ic_dialog_alert)).getBitmap();
                item.setCalloutLeftImage(left);
                Bitmap right = ((BitmapDrawable)getResources().getDrawable(android.R.drawable.ic_dialog_info)).getBitmap();
                item.setCalloutRightButtonImage(right);
                item.setCanShowCallout(true);

                mapView.addMarkerItem("marker0", item);

            }
        });

        btn = (Button)findViewById(R.id.btn_search);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String keyword = keywordView.getText().toString();
                if (!TextUtils.isEmpty(keyword)) {
                    TMapData data = new TMapData();
                    data.findAllPOI(keyword, new TMapData.FindAllPOIListenerCallback() {
                        @Override
                        public void onFindAllPOI(final ArrayList<TMapPOIItem> arrayList) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    clearAll();
                                    for (TMapPOIItem item : arrayList) {
                                        POIItem poi = new POIItem();
                                        poi.poi = item;
                                        mAdapter.add(poi);
                                    }
//                                    mapView.addTMapPOIItem(arrayList);
                                    addMarkerPOI(arrayList);
                                    if (arrayList.size() > 0) {
                                        TMapPOIItem first = arrayList.get(0);
                                        moveMap(first.getPOIPoint().getLatitude(), first.getPOIPoint().getLongitude());
                                    }
                                }
                            });
                        }
                    });
                }
            }
        });

        btn = (Button)findViewById(R.id.btn_route);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (start != null && end != null) {
                    TMapData data = new TMapData();
                    data.findPathData(start, end, new TMapData.FindPathDataListenerCallback() {
                        @Override
                        public void onFindPathData(final TMapPolyLine tMapPolyLine) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    tMapPolyLine.setLineColor(Color.RED);
                                    tMapPolyLine.setLineWidth(5);
                                    mapView.addTMapPath(tMapPolyLine);
                                    Bitmap sbm = ((BitmapDrawable)getResources().getDrawable(android.R.drawable.ic_input_add)).getBitmap();
                                    Bitmap ebm = ((BitmapDrawable)getResources().getDrawable(android.R.drawable.ic_input_get)).getBitmap();
                                    mapView.setTMapPathIcon(sbm, ebm);
                                    start = null;
                                    end = null;
                                }
                            });
                        }
                    });
                }
            }
        });
    }

    private void clearAll() {
//        for (int i = 0 ; i < mAdapter.getCount(); i++) {
//            POIItem item = mAdapter.getItem(i);
//            mapView.removeMarkerItem(item.poi.getPOIID());
//        }
        mapView.removeAllMarkerItem();
        mAdapter.clear();
    }

    private void addMarkerPOI(ArrayList<TMapPOIItem> list) {
        for (TMapPOIItem poi : list) {
            TMapPoint point = poi.getPOIPoint();
            TMapMarkerItem item = new TMapMarkerItem();
            item.setTMapPoint(point);
            Bitmap icon = ((BitmapDrawable)getResources().getDrawable(android.R.drawable.ic_dialog_map)).getBitmap();
            item.setIcon(icon);
            item.setPosition(0.5f, 1);
            item.setCalloutTitle(poi.getPOIName());
            item.setCalloutSubTitle(poi.getPOIContent());
            Bitmap left = ((BitmapDrawable)getResources().getDrawable(android.R.drawable.ic_dialog_alert)).getBitmap();
            item.setCalloutLeftImage(left);
            Bitmap right = ((BitmapDrawable)getResources().getDrawable(android.R.drawable.ic_dialog_info)).getBitmap();
            item.setCalloutRightButtonImage(right);
            item.setCanShowCallout(true);

            mapView.addMarkerItem(poi.getPOIID(), item);
        }
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

    TMapPoint start, end;

    private void setupMap() {
        if (cacheLocation != null) {
            moveMap(cacheLocation.getLatitude(), cacheLocation.getLongitude());
            setMyLocation(cacheLocation.getLatitude(), cacheLocation.getLongitude());
            cacheLocation = null;
        }

        mapView.setOnCalloutRightButtonClickListener(new TMapView.OnCalloutRightButtonClickCallback() {
            @Override
            public void onCalloutRightButton(TMapMarkerItem tMapMarkerItem) {
//                Toast.makeText(MainActivity.this, "Marker Click", Toast.LENGTH_SHORT).show();
                switch (group.getCheckedRadioButtonId()) {
                    case R.id.radio_start :
                        start = tMapMarkerItem.getTMapPoint();
                        Toast.makeText(MainActivity.this, "set start", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.radio_end :
                        end = tMapMarkerItem.getTMapPoint();
                        Toast.makeText(MainActivity.this, "set end", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
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
