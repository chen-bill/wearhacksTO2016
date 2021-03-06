package oldpeoplesavers.savetheoldpeople;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.dd.morphingbutton.MorphingButton;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.microsoft.band.BandClient;
import com.microsoft.band.BandClientManager;
import com.microsoft.band.BandException;
import com.microsoft.band.BandIOException;
import com.microsoft.band.BandInfo;
import com.microsoft.band.ConnectionState;
import com.microsoft.band.sensors.BandAccelerometerEvent;
import com.microsoft.band.sensors.BandAccelerometerEventListener;
import com.microsoft.band.sensors.BandHeartRateEvent;
import com.microsoft.band.sensors.BandHeartRateEventListener;
import com.microsoft.band.sensors.HeartRateConsentListener;
import com.microsoft.band.sensors.HeartRateQuality;
import com.microsoft.band.sensors.SampleRate;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks
,GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener,
        ActivityCompat.OnRequestPermissionsResultCallback{

    TextView nameView;
    TextView rateView;

    Firebase andrewlocRef;
    Firebase andrewrateRef;
    Firebase andrewfell;
    Firebase andrewhelp;
    Firebase memoref;

    MorphingButton btnMorph;

    MorphingButton addBtn;

    private BandClient client = null;

    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;

    long currentTimeStamp = 0;

    private LocationRequest mLocationRequest;

    private static final int REQUEST_LOCATION = 1;
    private static String[] PERMISSIONS_LOCATION = {Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION};

    TextView mLatitudeText;
    TextView mLongitudeText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Firebase myFirebaseRef = new Firebase("https://watchdog-app.firebaseio.com/");
        andrewlocRef = new Firebase("https://watchdog-app.firebaseio.com/Bill/People/Andrew/location");
        andrewrateRef = new Firebase("https://watchdog-app.firebaseio.com/Bill/People/Andrew/heartRate");
        andrewfell = new Firebase("https://watchdog-app.firebaseio.com/Bill/People/Andrew/fellDown");
        andrewhelp = new Firebase("https://watchdog-app.firebaseio.com/Bill/People/Andrew/needsHelp");

        final WeakReference<Activity> reference = new WeakReference<Activity>(this);

        new HeartRateConsentTask().execute(reference);

        nameView = (TextView) findViewById(R.id.name);
        rateView = (TextView) findViewById(R.id.rate);

        addBtn = (MorphingButton) findViewById(R.id.addBtn);

        // sample demonstrate how to morph button to green circle with icon
        btnMorph = (MorphingButton) findViewById(R.id.btnMorph);

        btnMorph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage("Are you sure?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("Cancel",dialogClickListener).show();
            }
        });

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomDialogFragment addDialog = new CustomDialogFragment();
                addDialog.show(getSupportFragmentManager(),"Create Dialog");
            }
        });


        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        myFirebaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Log.d("Testing", (String) postSnapshot.getKey());
                    Log.d("Bill's Child", (String) postSnapshot.getChildren().iterator().next().getKey());
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });

        new HeartRateSubscriptionTask().execute();
    }

    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which){
                case DialogInterface.BUTTON_POSITIVE:
                    andrewhelp.setValue(true);
                    MorphingButton.Params circle = MorphingButton.Params.create()
                            .duration(500)
                            .cornerRadius(256) // 56 dp// 56 dp
                            .width(256)
                            .height(256)
                            .color(Color.parseColor("#BBDEFB")) // normal state color
                            .icon(R.drawable.ic_done_white_48dp); // icon
                    btnMorph.morph(circle);

                    btnMorph.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            MorphingButton.Params rekt = MorphingButton.Params.create()
                                    .duration(500)
                                    .width(670)
                                    .height(150)
                                    .color(Color.parseColor("#F44336"))
                                    .text("Request Help");
                            btnMorph.morph(rekt);
                        }
                    }, 2000);
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    //No button clicked
                    break;
            }
        }
    };

    public static long getCurrentTime(){
        //Divide by 1000 to get seconds
        return System.currentTimeMillis() / 1000;
    }

    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }



    private void appendToRate(final String string){
        this.runOnUiThread(new Runnable(){
            @Override
            public void run(){
                rateView.setText(string);
            }
        });
    }

    private BandHeartRateEventListener mHeartRateEventListener = new BandHeartRateEventListener() {
        @Override
        public void onBandHeartRateChanged(final BandHeartRateEvent event) {
            if (event != null) {

                appendToRate(String.format("%d", event.getHeartRate()));

                if (getCurrentTime() >= currentTimeStamp+5){
                    currentTimeStamp = getCurrentTime();
                    if(event.getQuality() == HeartRateQuality.LOCKED){
                        andrewrateRef.child(Long.toString(currentTimeStamp)).setValue(event.getHeartRate());
                    }else{
                        andrewrateRef.child(Long.toString(currentTimeStamp)).setValue(-1);
                    }
                }
            }
        }
    };


    private BandAccelerometerEventListener mAccelerometerEventListener = new BandAccelerometerEventListener() {
        @Override
        public void onBandAccelerometerChanged(final BandAccelerometerEvent event) {
            if (event != null) {

                double mag = Math.pow(event.getAccelerationX(),2) + Math.pow(event.getAccelerationY(),2) + Math.pow(event.getAccelerationZ(), 2);
                if(mag > 7){
                    System.out.println(mag);
                    Log.d("STROKESTROKESTROKE", "I'M HAVING A STROKE");
                    andrewfell.setValue(true);
                }

            }
        }
    };

    @Override
    public void onConnected(Bundle bundle) {
        try {
            getLocation();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void requestLocationPermissions(){
        ActivityCompat.requestPermissions(this, PERMISSIONS_LOCATION, REQUEST_LOCATION);
    }

    public void getLocation() throws IOException {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            requestLocationPermissions();
            return;
        }

        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            Log.d("Log", "Location");

            Map<String, Object> locationRef = new HashMap<String, Object>();
            locationRef.put("lat", mLastLocation.getLatitude());
            locationRef.put("lng", mLastLocation.getLongitude());

            andrewlocRef.updateChildren(locationRef);
        } else {
            Log.d("Log", "Location last null");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_LOCATION) {
            if (PermissionUtil.verifyPermissions(grantResults)) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                        mLocationRequest, MainActivity.this);
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    private class HeartRateSubscriptionTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            try {
                if (getConnectedBandClient()) {
                        client.getSensorManager().registerHeartRateEventListener(mHeartRateEventListener);
                        client.getSensorManager().registerAccelerometerEventListener(mAccelerometerEventListener, SampleRate.MS128);

                } else {
                  //  appendToUI("Band isn't connected. Please make sure bluetooth is on and the band is in range.\n");
                }
            } catch (BandException e) {
                String exceptionMessage="";
                switch (e.getErrorType()) {
                    case UNSUPPORTED_SDK_VERSION_ERROR:
                        exceptionMessage = "Microsoft Health BandService doesn't support your SDK Version. Please update to latest SDK.\n";
                        break;
                    case SERVICE_ERROR:
                        exceptionMessage = "Microsoft Health BandService is not available. Please make sure Microsoft Health is installed and that you have the correct permissions.\n";
                        break;
                    default:
                        exceptionMessage = "Unknown error occured: " + e.getMessage() + "\n";
                        break;
                }
                e.printStackTrace();

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }


    private boolean getConnectedBandClient() throws InterruptedException, BandException {
        if (client == null) {
            BandInfo[] devices = BandClientManager.getInstance().getPairedBands();
            if (devices.length == 0) {
               // appendToUI("Band isn't paired with your phone.\n");
                return false;
            }
            client = BandClientManager.getInstance().create(getBaseContext(), devices[0]);
        } else if (ConnectionState.CONNECTED == client.getConnectionState()) {
            return true;
        }

       // appendToUI("Band is connecting...\n");
        return ConnectionState.CONNECTED == client.connect().await();
    }

    private class HeartRateConsentTask extends AsyncTask<WeakReference<Activity>, Void, Void> {
        @Override
        protected Void doInBackground(WeakReference<Activity>... params) {
            try {
                if (getConnectedBandClient()) {

                    if (params[0].get() != null) {
                        client.getSensorManager().requestHeartRateConsent(params[0].get(), new HeartRateConsentListener() {
                            @Override
                            public void userAccepted(boolean consentGiven) {
                            }
                        });
                    }
                } else {
                 //   appendToUI("Band isn't connected. Please make sure bluetooth is on and the band is in range.\n");
                }
            } catch (BandException e) {
                String exceptionMessage="";
                switch (e.getErrorType()) {
                    case UNSUPPORTED_SDK_VERSION_ERROR:
                        exceptionMessage = "Microsoft Health BandService doesn't support your SDK Version. Please update to latest SDK.\n";
                        break;
                    case SERVICE_ERROR:
                        exceptionMessage = "Microsoft Health BandService is not available. Please make sure Microsoft Health is installed and that you have the correct permissions.\n";
                        break;
                    default:
                        exceptionMessage = "Unknown error occured: " + e.getMessage() + "\n";
                        break;
                }
               // appendToUI(exceptionMessage);

            } catch (Exception e) {
              //  appendToUI(e.getMessage());
            }
            return null;
        }
    }
}