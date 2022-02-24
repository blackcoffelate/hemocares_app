package com.example.hemocares;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.example.hemocares.model.ModelUser;
import com.example.hemocares.service.App;
import com.example.hemocares.service.BaseURL;
import com.example.hemocares.service.GsonHelper;
import com.example.hemocares.service.Prefs;
import com.example.hemocares.view.dailyreport.DailyReport;
import com.example.hemocares.view.dailyreport.DailyReportAdd;
import com.example.hemocares.view.dashboard.Dashboard;
import com.example.hemocares.view.findpeople.FindPeople;
import com.example.hemocares.view.profile.Profile;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.TimeoutException;

import es.dmoral.toasty.Toasty;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    boolean backPress = false;

    private RequestQueue mRequestQueue;
    ModelUser modelUser;

    BottomAppBar bottomAppBars;
    Fragment fragment;
    FragmentManager fragmentManager;
    FloatingActionButton reportDailyButton;

    FusedLocationProviderClient fusedLocationProviderClient;
    private LocationManager locationManager;
    String GUID, LATDATA, LONGDATA, STATUS;
    double latMe, lngMe;
    LocationRequest locationRequest;

    LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            if (locationResult == null) {
                return;
            }
            for (Location location: locationResult.getLocations()) {
                latMe = location.getLatitude();
                lngMe = location.getLongitude();

                LATDATA = String.valueOf(latMe);
                LONGDATA = String.valueOf(lngMe);

                sendLocationFunction(latMe, lngMe);
                updateLocationFunction(latMe, lngMe);

                JSONObject json = new JSONObject();
                JSONArray locatiosArray = new JSONArray();

                try {
                    json.put("GUID", GUID);
                    locatiosArray.put(LATDATA);
                    locatiosArray.put(LONGDATA);
                    json.put("PHONE", modelUser.getPHONE());
                    json.put("FULLNAME", modelUser.getFULLNAME());
                    json.put("BLOOD_TYPE", modelUser.getBLOOD_TYPE());
                    json.put("LOCATION", locatiosArray);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (modelUser.getSTATUS().equals("iya"))
                    amqpSender(json);
            }
        }
    };

    String QUEUE_NAME = "hemocares";
    ConnectionFactory factory;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        factory = new ConnectionFactory();

        mRequestQueue = Volley.newRequestQueue(this);

        modelUser = (ModelUser) GsonHelper.parseGson(App.getPref().getString(Prefs.PREF_STORE_PROFILE, ""), new ModelUser());
        STATUS = modelUser.getSTATUS();
        GUID = modelUser.getGUID();

        bottomAppBars = findViewById(R.id.bottomAppBar);
        reportDailyButton = findViewById(R.id.reportButton);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MainActivity.this);

        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(2000);
        locationRequest.setFastestInterval(4000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocation();
        } else {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
        }

        loadFragment(new Dashboard());
        bottomAppBars.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                fragment = null;
                switch (item.getItemId()) {
                    case R.id.dashboard:
                        fragment = new Dashboard();
                        break;
                    case R.id.search:
                        fragment = new FindPeople();
                        break;
                    case R.id.daily:
                        fragment = new DailyReport();
                        break;
                    case R.id.me:
                        fragment = new Profile();
                        break;
                }

                if (fragment != null) {
                    fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit();
                } else {
                    Log.e(TAG, "Error Creating Fragment");
                }

                return false;
            }
        });

        reportDailyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (STATUS.equals("iya")) {
                    startActivity(new Intent(MainActivity.this, DailyReportAdd.class));
                    Animatoo.animateSlideDown(MainActivity.this);
                } else {
                    Toasty.warning(MainActivity.this, "Anda belum bergabung...", Toasty.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void amqpSender(JSONObject datas) {
        try {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            factory.setHost("rmq2.pptik.id");
            factory.setPort(5672);
            factory.setUsername("kir_tanggamus");
            factory.setPassword("kir_tanggamus");
            factory.setVirtualHost("/kir_tanggamus");

            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();

            channel.queueDeclare(QUEUE_NAME, true, false, false, null);
            channel.basicPublish("amq.topic", "r_hemocares", null, datas.toString().getBytes());
            System.out.println(" [x] Sent '" + datas + "'");
            channel.close();
            connection.close();
        } catch (IOException | TimeoutException e) {
            throw new RuntimeException("PROBLEM", e);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100 && (grantResults.length > 0) && (grantResults[0] + grantResults[1] == PackageManager.PERMISSION_GRANTED)) {
            getCurrentLocation();
        } else {
            Toasty.info(this, "PERMISSION DENIED", Toasty.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("MissingPermission")
    private void getCurrentLocation() {
        LocationSettingsRequest request = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest).build();
        SettingsClient client = LocationServices.getSettingsClient(this);

        Task<LocationSettingsResponse> locationSettingsResponseTask = client.checkLocationSettings(request);
        locationSettingsResponseTask.addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                startLocationUpdate();
            }
        });

        locationSettingsResponseTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    ResolvableApiException apiException = (ResolvableApiException) e;
                    try {
                        apiException.startResolutionForResult(MainActivity.this, 1001);
                    } catch (IntentSender.SendIntentException sendIntentException) {
                        sendIntentException.printStackTrace();
                    }
                }
            }
        });
    }

    private void startLocationUpdate() {
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }

    private void stopLocationUpdate() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopLocationUpdate();
    }

    private void sendLocationFunction(Double latMe, Double lngMe) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("GUID", GUID);
        params.put("LATITUDE", String.valueOf(latMe));
        params.put("LONGITUDE", String.valueOf(lngMe));
        params.put("STATUS", STATUS);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, BaseURL.postLocation, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String msg = response.getString("message");
                            boolean statusMsg = response.getBoolean("status");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("ERROR", error.getMessage());
            }
        });
        mRequestQueue.add(request);
    }

     private void updateLocationFunction(Double latMe, Double lngMe) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("GUID", GUID);
        params.put("LATITUDE", String.valueOf(latMe));
        params.put("LONGITUDE", String.valueOf(lngMe));
        params.put("STATUS", STATUS);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.PUT, BaseURL.updateLocation + GUID, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String msg = response.getString("message");
                            boolean statusMsg = response.getBoolean("status");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("ERROR", error.getMessage());
            }
        });
        mRequestQueue.add(request);
    }

    private void loadFragment(Fragment fragment) {
        FragmentManager f = getSupportFragmentManager();
        FragmentTransaction t = f.beginTransaction();
        t.replace(R.id.fragment_container, fragment).commit();
        t.addToBackStack(null);
    }

    @Override
    public void onBackPressed() {
        if (backPress) {
            super.onBackPressed();
            return;
        }

        this.backPress = true;
        Toasty.info(this, "Tekan sekali lagi untuk keluar...", Toasty.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                backPress = false;
            }
        }, 2000);
    }
}