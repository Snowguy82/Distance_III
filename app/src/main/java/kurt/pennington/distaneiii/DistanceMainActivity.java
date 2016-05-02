package kurt.pennington.distaneiii;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import static com.google.android.gms.location.LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY;
import static com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY;
import static com.google.android.gms.location.LocationRequest.PRIORITY_LOW_POWER;
import static com.google.android.gms.location.LocationServices.FusedLocationApi;


public class DistanceMainActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks,
        LocationListener, ActivityCompat.OnRequestPermissionsResultCallback {

    private final static String[] PERMISSIONS = {"Manifest.permission.ACCESS_FINE_LOCATION",
            "Manifest.permission.ACCESS_COARSE_LOCATION"};
    private final String TAG = ">>>> KP Dist Test";
    private final int REQUEST_PERMISSION_CODE = 1;
    private GoogleApiClient mGoogleAPI;
    private double mTotalDistance;
    private Location mPreviousLocation;
    private Location mCurrentLocation;
    private boolean mUpdateLocation;
    private LocationRequest mLocationRequest;
    private TextView mTotalDistanceTextView;
    private TextView mLatTextView;
    private TextView mLongTextView;
    private TextView mAccuracyTextView;
    private TextView mElapsedTimeTextView;
    private TextView mPriorityTextView;

    private Button mStartButton;
    private Button mStopButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_distance_main);
        startGoogleApi();

        mTotalDistance = 0.0;
        mUpdateLocation = false;

        mStartButton = (Button) findViewById(R.id.start_button);
        mStopButton = (Button) findViewById(R.id.stop_button);

        mStartButton.setEnabled(true);
        mStopButton.setEnabled(false);


        mTotalDistanceTextView = (TextView) findViewById(R.id.total_distance_textView);
        mLatTextView = (TextView) findViewById(R.id.lat_textView);
        mLongTextView = (TextView) findViewById(R.id.long_textView);
        mAccuracyTextView = (TextView) findViewById(R.id.accuracy_textView);
        mElapsedTimeTextView = (TextView) findViewById(R.id.elapsed_time_textView);
        mPriorityTextView = (TextView) findViewById(R.id.priority_textView);

        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(PRIORITY_HIGH_ACCURACY);

  //
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.d(TAG, "onLocationChanged");
                Log.d(TAG, location.toString());
                mPreviousLocation = mCurrentLocation;
                mCurrentLocation = location;
                calculateDistance();
            }
        };


        mStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Start Click");
                mUpdateLocation = true;
                mStopButton.setEnabled(true);
                mStartButton.setEnabled(false);
                startLocationUpdates();
            }
        });

        mStopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Stop Click");
                mUpdateLocation = false;
                mStartButton.setEnabled(true);
                mStopButton.setEnabled(false);
                stopUpdates();
            }
        });
        Log.d(TAG, "Google is connected: " + mGoogleAPI.isConnected());
    }

    private void startGoogleApi() {
        Log.d(TAG, "startGoogleApi");
        if (mGoogleAPI == null) {
            mGoogleAPI = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        };
        mGoogleAPI.connect();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if ((requestCode == REQUEST_PERMISSION_CODE) && (grantResults.length > 1)
                && (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                && (grantResults[1] == PackageManager.PERMISSION_GRANTED)) {

            FusedLocationApi.requestLocationUpdates(
                    mGoogleAPI, mLocationRequest, this);
        }
    }

    @Override
    protected void onStart() {
        Log.d(TAG, "onStart");
        super.onStart();

    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause");
        super.onPause();


    }

    private void stopUpdates() {
        Log.d(TAG, "stopUpdates");

        if(FusedLocationApi != null)
            FusedLocationApi.removeLocationUpdates(mGoogleAPI, this);
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();

    }

    protected void startLocationUpdates() {
        Log.d(TAG, "startLocationUpdates");
        Log.d(TAG, "Google is connected: " + mGoogleAPI.isConnected());
        PermissionRationaleDialogFragment rationaleDialog;
        Bundle dialogBundle;
        String locationString = "Location Permission";

        if (mGoogleAPI.isConnected()) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this, Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION)) {

                    rationaleDialog = new PermissionRationaleDialogFragment();

                } else if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION)) {
                    rationaleDialog = new PermissionRationaleDialogFragment();

                } else {

                    // No explanation needed, we can request the permission.

                    ActivityCompat.requestPermissions(this, PERMISSIONS,  REQUEST_PERMISSION_CODE);
                }

                for (int i = 0; i < 6; i++) {
                    Log.d(TAG, "Permissions Denied *************");
                }
                Log.d(TAG, String.valueOf(PackageManager.GET_PERMISSIONS));

                return;
            }
            Log.d(TAG, "Permissions GRANTED *************");
            FusedLocationApi.requestLocationUpdates(mGoogleAPI, mLocationRequest, this);
        } else {
            startGoogleApi();
        }
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "onStop");
        super.onStop();

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG, "onConnected, mUpdateLoc: " + mUpdateLocation);

        if (mUpdateLocation) {
            startLocationUpdates();
        }
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "CONNECTION FAILED <<<<<<<<<<<<<");

    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "CONNECTION SUSPENDED <<<<<<<<<<<<<");
    }


    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "onLocationChanged");
        Log.d(TAG, location.toString());

        // Handle first location where both current and previous are the same place
        if(mCurrentLocation == null)
            mCurrentLocation = location;

        mPreviousLocation = mCurrentLocation;
        mCurrentLocation = location;
        calculateDistance();

    }

	/**
     * Calculates the distance between two location readings.
     *
     * Calculation modified from data provided by Meridian World Data found
     * online at http://www.meridianworlddata.com/distance-calculation/.
     */
    private void calculateDistance() {
        Log.d(TAG, "calculateDistance");
        double dLat = Math.abs(mCurrentLocation.getLatitude() - mPreviousLocation.getLatitude());
        double dLong = Math.abs(mCurrentLocation.getLongitude() - mPreviousLocation.getLongitude());

        dLat = dLat * 69.1;
        dLong = dLong * 69.1 * Math.cos(mPreviousLocation.getLatitude() / 57.3);

        mTotalDistance += Math.sqrt(dLat * dLat + dLong + dLong);


        updateUI();
    }

    private void updateUI() {
        Log.d(TAG, "updateUI");
        mTotalDistanceTextView.setText(Double.toString(mTotalDistance));
        mLatTextView.setText(Double.toString(mCurrentLocation.getLatitude()));
        mLongTextView.setText(Double.toString(mCurrentLocation.getLongitude()));
        mPriorityTextView.setText(Integer.toString(mLocationRequest.getPriority()));
        mElapsedTimeTextView.setText(Long.toString(mCurrentLocation.getElapsedRealtimeNanos()));
        mAccuracyTextView.setText(Float.toString(mCurrentLocation.getAccuracy()));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.high_priority_menu_item:
                mLocationRequest.setPriority(PRIORITY_HIGH_ACCURACY);
                return true;
            case R.id.balanced_priority_menu_item:
                mLocationRequest.setPriority(PRIORITY_BALANCED_POWER_ACCURACY);
                return true;
            case R.id.low_priority_menu_item:
                mLocationRequest.setPriority(PRIORITY_LOW_POWER);
                return true;
            case R.id.reset_menu_item:
                reset();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void reset() {
        mTotalDistanceTextView.setText("");
        mLatTextView.setText("");
        mLongTextView.setText("");
        mPriorityTextView.setText("");
        mElapsedTimeTextView.setText("");
        mAccuracyTextView.setText("");
        mTotalDistance = 0.0;
        mStartButton.setEnabled(true);
        mStopButton.setEnabled(false);
        stopUpdates();
        mPreviousLocation = null;
        mCurrentLocation = null;

    }
}

