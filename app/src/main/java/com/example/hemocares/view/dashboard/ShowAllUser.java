package com.example.hemocares.view.dashboard;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.hemocares.BuildConfig;
import com.example.hemocares.R;
import com.example.hemocares.model.ModelUser;
import com.example.hemocares.model.ModelUserShowAll;
import com.example.hemocares.service.App;
import com.example.hemocares.service.BaseURL;
import com.example.hemocares.service.GsonHelper;
import com.example.hemocares.service.Prefs;
import com.example.hemocares.view.dashboard.adapter.AdapterUserJoinAll;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.Marker;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ShowAllUser extends AppCompatActivity {

    ModelUser modelUser;
    List<ModelUserShowAll> listUserDataAll;
    String statusUser = "iya";
    String searchText;
    private RequestQueue mRequestQueue;

    FusedLocationProviderClient fusedLocationProviderClient;
    final int REQUEST_PERMISSION_CODE = 1;
    Geocoder geocoder;
    GeoPoint point, pointReal;
    double latMe, lngMe;

    RecyclerView recycleUserAll;
    AdapterUserJoinAll adapterRecycleUserJoinAll;
    LinearLayout availableDataAll;
    CardView emptyDataAll;
    SearchView searchUserData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_all_user);

        Context context = ShowAllUser.this.getApplicationContext();
        Configuration.getInstance().load(context, PreferenceManager.getDefaultSharedPreferences(context));
        Configuration.getInstance().setUserAgentValue(BuildConfig.APPLICATION_ID);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        geocoder = new Geocoder(this, Locale.getDefault());

        requestPermissions(new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.ACCESS_WIFI_STATE, Manifest.permission.INTERNET
        });

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocation();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
        }

        emptyDataAll = findViewById(R.id.emptyDataDisplayAll);
        availableDataAll = findViewById(R.id.dataAvailableAll);
        searchUserData = findViewById(R.id.searchUserJoinAll);

        searchUserData.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                newText = newText.toLowerCase();
                ArrayList<ModelUserShowAll> searchModel = new ArrayList<>();
                for (ModelUserShowAll model : listUserDataAll) {
                    String bloodTypeSearch = model.getBLOOD_TYPE().toLowerCase();
                    String ageSearch = model.getAGE().toLowerCase();
                    String addressSearch = model.getADDRESS().toLowerCase();
                    String genderSearch = model.getGENDER().toLowerCase();
                    String religionSearch = model.getRELIGION().toLowerCase();
                    if (bloodTypeSearch.contains(newText)) {
                        searchModel.add(model);
                    } else if (ageSearch.contains(newText)) {
                        searchModel.add(model);
                    } else if (addressSearch.contains(newText)) {
                        searchModel.add(model);
                    } else if (genderSearch.contains(newText)) {
                        searchModel.add(model);
                    } else if (religionSearch.contains(newText)) {
                        searchModel.add(model);
                    }
                }
                adapterRecycleUserJoinAll.setFilter(searchModel);
                return true;
            }
        });

        mRequestQueue = Volley.newRequestQueue(ShowAllUser.this);

        modelUser = (ModelUser) GsonHelper.parseGson(App.getPref().getString(Prefs.PREF_STORE_PROFILE, ""), new ModelUser());

        recycleUserAll = findViewById(R.id.listUserJoinAll);
        listUserDataAll = new ArrayList<>();

        recycleUserAll.setHasFixedSize(true);
        recycleUserAll.setLayoutManager(new LinearLayoutManager(ShowAllUser.this, LinearLayoutManager.VERTICAL, false));
        adapterRecycleUserJoinAll = new AdapterUserJoinAll(ShowAllUser.this, listUserDataAll);

        showDataUserJoinFunction();
    }

    @SuppressLint("MissingPermission")
    private void getCurrentLocation() {
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    Location location = task.getResult();
                    if (location != null) {
                        latMe = location.getLatitude();
                        lngMe = location.getLongitude();
                    } else {
                        LocationRequest locationRequest = new LocationRequest().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY).setInterval(10000).setFastestInterval(1000).setNumUpdates(1);
                        LocationCallback locationCallback = new LocationCallback() {
                            @Override
                            public void onLocationResult(LocationResult locationResult) {
                                Location locationOne = locationResult.getLastLocation();
                                latMe = locationOne.getLatitude();
                                lngMe = locationOne.getLongitude();
                            }
                        };
                        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
                    }
                }
            });
        } else {
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }
    }

    private void showDataUserJoinFunction() {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, BaseURL.getUserByStatusAll + statusUser, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String msg = response.getString("message");
                            boolean statusMsg = response.getBoolean("status");
                            if (statusMsg == true) {
                                JSONArray dataUserArray = response.getJSONArray("data");
                                if (dataUserArray.length() > 0) {
                                    for (int i = 0; i < dataUserArray.length(); i++) {
                                        JSONObject dataObject = dataUserArray.getJSONObject(i);
                                        ModelUserShowAll dataUserListAll = new ModelUserShowAll();
                                        String guidUser = dataObject.getString("GUID");
                                        String bloodTypeUser = dataObject.getString("BLOOD_TYPE");
                                        String fullnameUser = dataObject.getString("FULLNAME");
                                        String ageUser = dataObject.getString("AGE");
                                        String phoneUser = dataObject.getString("PHONE");
                                        String addressUser = dataObject.getString("ADDRESS");
                                        String profilePhotoUser = dataObject.getString("PHOTO");
                                        String genderUser = dataObject.getString("GENDER");
                                        String religionUser = dataObject.getString("RELIGION");

                                        JSONArray dataUserLocation = dataObject.getJSONArray("USER_LOCATIONS");

                                        if (dataUserLocation.length() > 0) {
                                            for (int x = 0; x < dataUserLocation.length(); x++) {
                                                JSONObject dataUserObjectLocations = dataUserLocation.getJSONObject(x);
                                                String latData = dataUserObjectLocations.getString("LATITUDE");
                                                String longData = dataUserObjectLocations.getString("LONGITUDE");

                                                double latDataUser = Double.parseDouble(latData);
                                                double lngDataUser = Double.parseDouble(longData);

                                                final float result[] = new float[10];
                                                Location.distanceBetween(latMe, lngMe, latDataUser, lngDataUser, result);
                                                float distanceLocation = result[0] / 1000;
                                                float resultLocation = (float) (Math.round(distanceLocation * 100)) / 100;
                                                float distanceBetweenUser = resultLocation;

                                                String distanceLocationBetween = String.valueOf(distanceBetweenUser);
                                                dataUserListAll.setRANGE(distanceLocationBetween);
                                            }
                                        }

                                        dataUserListAll.setGUID(guidUser);
                                        dataUserListAll.setBLOOD_TYPE(bloodTypeUser);
                                        dataUserListAll.setFULLNAME(fullnameUser);
                                        dataUserListAll.setAGE(ageUser);
                                        dataUserListAll.setPHONE(phoneUser);
                                        dataUserListAll.setADDRESS(addressUser);
                                        dataUserListAll.setPHOTO(profilePhotoUser);
                                        dataUserListAll.setRELIGION(religionUser);
                                        dataUserListAll.setGENDER(genderUser);

                                        listUserDataAll.add(dataUserListAll);
                                        recycleUserAll.setAdapter(adapterRecycleUserJoinAll);
                                    }
                                    emptyDataAll.setVisibility(View.GONE);
                                    availableDataAll.setVisibility(View.VISIBLE);
                                } else {
                                    emptyDataAll.setVisibility(View.VISIBLE);
                                    availableDataAll.setVisibility(View.GONE);
                                }
                            }
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

    private void requestPermissions(String[] permissions) {
        ArrayList<String> permissionsToRequest = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(permission);
            }
        }
        if (permissionsToRequest.size() > 0) {
            ActivityCompat.requestPermissions(this, permissionsToRequest.toArray(new String[0]), REQUEST_PERMISSION_CODE);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}