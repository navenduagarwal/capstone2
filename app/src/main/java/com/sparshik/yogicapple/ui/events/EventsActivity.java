package com.sparshik.yogicapple.ui.events;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sparshik.yogicapple.R;
import com.sparshik.yogicapple.model.Event;
import com.sparshik.yogicapple.ui.BaseActivity;
import com.sparshik.yogicapple.utils.Constants;
import com.sparshik.yogicapple.utils.PermissionUtils;

import java.util.HashMap;
import java.util.Map;

import timber.log.Timber;

public class EventsActivity extends BaseActivity implements OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener,
        ActivityCompat.OnRequestPermissionsResultCallback, GoogleMap.OnCameraChangeListener, GeoQueryEventListener {
    private static final int PLACE_PICKER_REQUEST = 1;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 2;
    boolean mapReady = false;
    private GoogleMap mMap;
    private boolean mPermissionDenied = false;
    private GeoFire mGeoFire;
    private GeoQuery mGeoQuery;
    private Map<String, Marker> markers;
    private Circle mSearchCircle;
    private DatabaseReference mEventsRef;
    private int mEventCount;

    @Override
    public Resources getResources() {
        return super.getResources();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mEventCount = 0;

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        DatabaseReference geoRef = FirebaseDatabase.getInstance().getReferenceFromUrl(Constants.FIREBASE_URL_GEOFIRE);
        mGeoFire = new GeoFire(geoRef);
        mGeoQuery = mGeoFire.queryAtLocation(Constants.INITIAL_CENTER, 1);

        markers = new HashMap<>();

        mEventsRef = FirebaseDatabase.getInstance().getReferenceFromUrl(Constants.FIREBASE_URL_EVENTS);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, this);
                String toastMsg = getString(R.string.place_message,place.getName());
                Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();
                Timber.i("Places: " + place.getLatLng() + "\n" + place.getAddress() + "\n" + place.getAddress());
                CameraPosition updatedPlace = CameraPosition.builder()
                        .target(place.getLatLng())
                        .zoom(14)
                        .bearing(0)
                        .tilt(45)
                        .build();
                flyTo(updatedPlace);
            }
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mapReady = true;
        mMap = googleMap;
        LatLng latLngCenter = new LatLng(Constants.INITIAL_CENTER.latitude, Constants.INITIAL_CENTER.longitude);
        mSearchCircle = mMap.addCircle(new CircleOptions().center(latLngCenter).radius(1000));
        mSearchCircle.setFillColor(Color.argb(66, 255, 0, 255));
        mSearchCircle.setStrokeColor(Color.argb(66, 0, 0, 0));
        mMap.setOnMyLocationButtonClickListener(this);
        enableMyLocation();
        CameraPosition initialCamera = CameraPosition.builder()
                .target(latLngCenter)
                .zoom(14)
                .bearing(0)
                .tilt(14)
                .build();
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                if (latLng != null) {
                    Timber.i("On Click latlng: " + latLng.toString());
                    Intent intent = new Intent(EventsActivity.this, AddEventActivity.class);
                    intent.putExtra(Constants.KEY_LATITUDE, latLng.latitude);
                    intent.putExtra(Constants.KEY_LONGITUDE, latLng.longitude);
                    startActivity(intent);
                }
            }
        });

        mMap.setOnCameraChangeListener(this);
        flyTo(initialCamera);
    }

    private void flyTo(CameraPosition target) {
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(target));
    }

    /**
     * Override onOptionsItemSelected to use menu_event instead of BaseActivity menu
     *
     * @param menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /* Inflate the menu; this adds items to the action bar if it is present. */
        getMenuInflater().inflate(R.menu.menu_event, menu);
        return true;
    }

    /**
     * Override onOptionsItemSelected to add action_search only to the EventsActivity
     *
     * @param item
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_search) {
            openPlacePicker();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void openPlacePicker() {
        try {
            PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
            startActivityForResult(builder.build(EventsActivity.this), PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException e) {
            GooglePlayServicesUtil
                    .getErrorDialog(e.getConnectionStatusCode(), EventsActivity.this, 0);
        } catch (GooglePlayServicesNotAvailableException e) {
            Toast.makeText(EventsActivity.this, getString(R.string.play_service_unavailable),
                    Toast.LENGTH_LONG)
                    .show();
        }
    }

    @Override
    public boolean onMyLocationButtonClick() {
        return false;
    }

    /**
     * Enables the My Location layer if the fine location permission has been granted.
     */
    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);
        } else if (mMap != null) {
            // Access to the location has been granted to the app.
            mMap.setMyLocationEnabled(true);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        }

        if (PermissionUtils.isPermissionGranted(permissions, grantResults,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Enable the my location layer if the permission has been granted.
            enableMyLocation();
        } else {
            // Display the missing permission error dialog when the fragments resume.
            mPermissionDenied = true;
        }
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (mPermissionDenied) {
            // Permission was not granted, display error dialog.
            showMissingPermissionError();
            mPermissionDenied = false;
        }
    }

    /**
     * Displays a dialog with error message explaining that the location permission is missing.
     */
    private void showMissingPermissionError() {
        PermissionUtils.PermissionDeniedDialog
                .newInstance(true).show(getSupportFragmentManager(), "dialog");
    }


    @Override
    protected void onStop() {
        super.onStop();
        // remove all event listeners to stop updating in the background
        mGeoQuery.removeAllListeners();
        for (Marker marker : this.markers.values()) {
            marker.remove();
        }
        this.markers.clear();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // add an event listener to start updating locations again
        mGeoQuery.addGeoQueryEventListener(this);
    }

    @Override
    public void onKeyEntered(final String key, final GeoLocation location) {
        DatabaseReference markerDataRef = mEventsRef.child(key);
        markerDataRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Event eventData = dataSnapshot.getValue(Event.class);
                if (eventData != null) {
                    // Add a new marker to the map
                    final Marker marker = mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(location.latitude, location.longitude))
                            .title(eventData.getTitle())
                            .snippet(eventData.getEventDesc()));
                    marker.setTag(0);
                    final String url = eventData.getEventUrl();

                    markers.put(key, marker);
                    mEventCount = mEventCount + 1;
                    updateTitle();
//                    loadMarkerIcon(marker, eventData.getEventImageUrl());
                    mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                        @Override
                        public void onInfoWindowClick(Marker markerInt) {
                            Intent browser = new Intent(Intent.ACTION_VIEW);
                            browser.setData(Uri.parse(url));
                            startActivity(browser);
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        Timber.d(key);
    }

    @Override
    public void onKeyExited(String key) {
        // Remove any old marker
        Marker marker = this.markers.get(key);
        if (marker != null) {
            marker.remove();
            this.markers.remove(key);
            mEventCount = mEventCount - 1;
            updateTitle();
        }

    }

    @Override
    public void onKeyMoved(String key, GeoLocation location) {
        // Move the marker
        Marker marker = this.markers.get(key);
        if (marker != null) {
            this.animateMarkerTo(marker, location.latitude, location.longitude);
        }
    }

    @Override
    public void onGeoQueryReady() {

    }

    @Override
    public void onGeoQueryError(DatabaseError error) {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.geofire_dialog_header))
                .setMessage(getString(R.string.geofire_error,error.getMessage()))
                .setPositiveButton(android.R.string.ok, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        // Update the search criteria for this geoQuery and the circle on the map
        LatLng center = cameraPosition.target;
        double radius = zoomLevelToRadius(cameraPosition.zoom);
        mSearchCircle.setCenter(center);
        mSearchCircle.setRadius(radius);
        mGeoQuery.setCenter(new GeoLocation(center.latitude, center.longitude));
        // radius in km
        mGeoQuery.setRadius(radius / 1000);
    }

    private double zoomLevelToRadius(double zoomLevel) {
        // Approximation to fit circle into view
        return 16384000 / Math.pow(2, zoomLevel);
    }

    // Animation handler for old APIs without animation support
    private void animateMarkerTo(final Marker marker, final double lat, final double lng) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        final long DURATION_MS = 3000;
        final Interpolator interpolator = new AccelerateDecelerateInterpolator();
        final LatLng startPosition = marker.getPosition();
        handler.post(new Runnable() {
            @Override
            public void run() {
                float elapsed = SystemClock.uptimeMillis() - start;
                float t = elapsed / DURATION_MS;
                float v = interpolator.getInterpolation(t);

                double currentLat = (lat - startPosition.latitude) * v + startPosition.latitude;
                double currentLng = (lng - startPosition.longitude) * v + startPosition.longitude;
                marker.setPosition(new LatLng(currentLat, currentLng));

                // if animation is not finished yet, repeat
                if (t < 1) {
                    handler.postDelayed(this, 16);
                }
            }
        });
    }

    private void loadMarkerIcon(final Marker marker, String ImageUrl) {
        Glide.with(this).load(ImageUrl)
                .asBitmap()
                .override(100, 100)
                .centerCrop().into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap bitmap, GlideAnimation<? super Bitmap> glideAnimation) {
                BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(bitmap);
                marker.setIcon(icon);
            }
        });
    }

    private void updateTitle() {
        if (mEventCount != 0) {
            setTitle(getString(R.string.title_activity_events) + " (" + mEventCount + ")");
        } else {
            setTitle(getString(R.string.title_activity_events));
        }
    }
}