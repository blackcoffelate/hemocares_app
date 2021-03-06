package com.example.hemocares.view.findpeople;

import static android.content.Context.LOCATION_SERVICE;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import android.os.Looper;
import android.os.StrictMode;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.hemocares.R;
import com.example.hemocares.model.ModelUser;
import com.example.hemocares.model.ModelUserShowMarkerAll;
import com.example.hemocares.service.App;
import com.example.hemocares.service.BaseURL;
import com.example.hemocares.service.GsonHelper;
import com.example.hemocares.service.Prefs;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.utils.BitmapUtils;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;

public class FindPeople extends Fragment {

    Boolean clicked = true;
    private RequestQueue mRequestQueue;
    ModelUser modelUser;
    List<ModelUserShowMarkerAll> listUserDataMarkerAll;

    FusedLocationProviderClient fusedLocationProviderClient;
    private LocationManager locationManager;
    MapView maps;
    double latMe, lngMe;

    TextView bloodA, bloodB, bloodAB, bloodO;
    CardView filterData, filterBloodData, myLocation;
    String GUID, GUIDALL;
    String guidUser;
    double parsingLat, parsingLng;
    Drawable drawableLive;
    String QUEUE_NAME = "hemocares";
    ConnectionFactory factory;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Mapbox.getInstance(getActivity(), getString(R.string.access_token));

        factory = new ConnectionFactory();

        View v = inflater.inflate(R.layout.fragment_find_people, container, false);

        mRequestQueue = Volley.newRequestQueue(getActivity());

        modelUser = (ModelUser) GsonHelper.parseGson(App.getPref().getString(Prefs.PREF_STORE_PROFILE, ""), new ModelUser());
        GUID = modelUser.getGUID();

        bloodA = v.findViewById(R.id.countBloodA);
        bloodB = v.findViewById(R.id.countBloodB);
        bloodAB = v.findViewById(R.id.countBloodAB);
        bloodO = v.findViewById(R.id.countBloodO);

        filterData = v.findViewById(R.id.showFilter);
        filterBloodData = v.findViewById(R.id.filterBlood);
        myLocation = v.findViewById(R.id.myLocationUpdate);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());

        locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocation();
        } else {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
        }

        myLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    getCurrentLocation();
                } else {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
                }
            }
        });

        filterData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clicked) {
                    filterBloodData.setVisibility(View.VISIBLE);
                    clicked = false;
                } else {
                    filterBloodData.setVisibility(View.GONE);
                    clicked = true;
                }
            }
        });

        maps = v.findViewById(R.id.mapsFindUser);
        maps.onCreate(savedInstanceState);

        showBloodCountFunction();
        getLocationFunction();

        listUserDataMarkerAll = new ArrayList<>();

        return v;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100 && (grantResults.length > 0) && (grantResults[0] + grantResults[1] == PackageManager.PERMISSION_GRANTED)) {
            getCurrentLocation();
        } else {
            Toasty.info(getActivity(), "PERMISSION DENIED", Toasty.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("MissingPermission")
    private void getCurrentLocation() {
        LocationManager manager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
        if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER) || manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    Location location = task.getResult();
                    maps.getMapAsync(new OnMapReadyCallback() {
                        @Override
                        public void onMapReady(MapboxMap mapboxMaps) {
                            if (location != null) {
                                latMe = location.getLatitude();
                                lngMe = location.getLongitude();

                                LatLng myLatLng = new LatLng(latMe, lngMe);

                                MarkerOptions markerOptions = new MarkerOptions();
                                Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_marker_me, null);
                                Bitmap mBitmap = BitmapUtils.getBitmapFromDrawable(drawable);
                                IconFactory iconFactory = IconFactory.getInstance(getActivity());
                                Icon icon = iconFactory.fromBitmap(mBitmap);

                                markerOptions.position(new LatLng(latMe, lngMe));
                                markerOptions.icon(icon);
                                markerOptions.snippet(modelUser.getPHONE());
                                markerOptions.title(modelUser.getFULLNAME());
                                mapboxMaps.addMarker(markerOptions);
                                mapboxMaps.animateCamera(CameraUpdateFactory.newLatLngZoom(myLatLng, 15.0f));
                                if (mapboxMaps != null) {
                                    mapboxMaps.setInfoWindowAdapter(new MapboxMap.InfoWindowAdapter() {
                                        @Override
                                        public View getInfoWindow(Marker marker) {
                                            View v = getLayoutInflater().inflate(R.layout.find_info_window_me, null);

                                            CircleImageView photo = (CircleImageView) v.findViewById(R.id.userMeMarker);
                                            TextView fullnamed = (TextView) v.findViewById(R.id.fullnameMeMarker);
                                            TextView phoned = (TextView) v.findViewById(R.id.phoneMeMarker);

                                            photo.setImageResource(R.drawable.default_user);
                                            fullnamed.setText(marker.getTitle());
                                            phoned.setText(marker.getSnippet());

                                            return v;
                                        }
                                    });
                                }
                            } else {
                                LocationRequest locationRequest = new LocationRequest().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY).setInterval(1000).setFastestInterval(1000).setNumUpdates(1);
                                LocationCallback locationCallback = new LocationCallback() {
                                    @Override
                                    public void onLocationResult(LocationResult locationResult) {
                                        Location location1 = locationResult.getLastLocation();
                                        latMe = location1.getLatitude();
                                        lngMe = location1.getLongitude();

                                        LatLng myLatLng = new LatLng(latMe, lngMe);

                                        MarkerOptions markerOptions = new MarkerOptions();
                                        Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_marker_me, null);
                                        Bitmap mBitmap = BitmapUtils.getBitmapFromDrawable(drawable);
                                        IconFactory iconFactory = IconFactory.getInstance(getActivity());
                                        Icon icon = iconFactory.fromBitmap(mBitmap);

                                        markerOptions.position(new LatLng(latMe, lngMe));
                                        markerOptions.icon(icon);
                                        markerOptions.snippet(modelUser.getPHONE());
                                        markerOptions.title(modelUser.getFULLNAME());
                                        mapboxMaps.addMarker(markerOptions);
                                        mapboxMaps.setMinZoomPreference(15.0f);
                                        mapboxMaps.setMaxZoomPreference(20.0f);
                                        mapboxMaps.animateCamera(CameraUpdateFactory.newLatLngZoom(myLatLng, 15.0f));
                                        if (mapboxMaps != null) {
                                            mapboxMaps.setInfoWindowAdapter(new MapboxMap.InfoWindowAdapter() {
                                                @Override
                                                public View getInfoWindow(Marker marker) {
                                                    View v = getLayoutInflater().inflate(R.layout.find_info_window_me, null);

                                                    CircleImageView photo = (CircleImageView) v.findViewById(R.id.userMeMarker);
                                                    TextView fullnamed = (TextView) v.findViewById(R.id.fullnameMeMarker);
                                                    TextView phoned = (TextView) v.findViewById(R.id.phoneMeMarker);

                                                    photo.setImageResource(R.drawable.default_user);
                                                    fullnamed.setText(marker.getTitle());
                                                    phoned.setText(marker.getSnippet());

                                                    return v;
                                                }
                                            });
                                        }
                                    }
                                };
                                fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
                            }
                        }
                    });
                }
            });
        } else {
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }
    }

    private void showBloodCountFunction() {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, BaseURL.getCountBlood, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String msg = response.getString("message");
                            boolean statusMsg = response.getBoolean("status");
                            if (statusMsg == true) {
                                JSONObject dataUser = response.getJSONObject("data");
                                JSONArray dataArray = dataUser.getJSONArray("user");
                                if (dataArray.length() > 0) {
                                    for (int i = 0; i < dataArray.length(); i++) {
                                        JSONObject dataObject = dataArray.getJSONObject(i);
                                        String bloodType = dataObject.getString("_id");
                                        String countData = dataObject.getString("count");

                                        if (bloodType.equals("A")) {
                                            bloodA.setText(countData);
                                        } else if (bloodType.equals("B")) {
                                            bloodB.setText(countData);
                                        } else if (bloodType.equals("AB")) {
                                            bloodAB.setText(countData);
                                        } else if (bloodType.equals("O")) {
                                            bloodO.setText(countData);
                                        }
                                    }
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

    private void getLocationFunction() {
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

            channel.queueDeclare("hemocares", true, false, false, null);

            System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String message = (new String(delivery.getBody(), StandardCharsets.UTF_8));
                setMarkerData(message);
                System.out.println(" [x] Received '" + message + "'");
            };

            channel.basicConsume("hemocares", true, deliverCallback, consumerTag -> { });
        } catch (IOException | TimeoutException e) {
            throw new RuntimeException("PROBLEM", e);
        }
    }

    private void setMarkerData(String message) {
        maps.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(MapboxMap mapboxMap) {
                final Map<String, Marker> namedMarkers = new HashMap<String, Marker>();

                JSONArray dataArray;
                Double latData = null;
                Double lngData = null;
                GUIDALL = null;
                String FULLNAME = null;
                String PHONE = null;
                String BLOOD_TYPE;

                try {
                    JSONObject json = new JSONObject(message);
                    dataArray = (JSONArray) json.get("LOCATION");
                    GUIDALL = json.getString("GUID");
                    latData = Double.parseDouble(String.valueOf(dataArray.get(0)));
                    lngData = Double.parseDouble(String.valueOf(dataArray.get(1)));
                    PHONE = json.getString("PHONE");
                    FULLNAME = json.getString("FULLNAME");
                    BLOOD_TYPE = json.getString("BLOOD_TYPE");

                    if (BLOOD_TYPE.equals("A")) {
                        drawableLive = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_marker_a, null);
                    } else if (BLOOD_TYPE.equals("B")) {
                        drawableLive = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_marker_b, null);
                    } else if (BLOOD_TYPE.equals("AB")) {
                        drawableLive = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_marker_ab, null);
                    } else if (BLOOD_TYPE.equals("O")) {
                        drawableLive = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_marker_o, null);
                    } else {
                        drawableLive = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_marker_me, null);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (!GUIDALL.equals(modelUser.getGUID())) {
                    LatLng latLongUser = new LatLng(latData, lngData);
                    Marker marker = namedMarkers.get(GUIDALL);

                    if (marker == null) {
                        MarkerOptions options = getMarkerOption(guidUser);
                        marker = mapboxMap.addMarker(options.title(FULLNAME).snippet(PHONE).position(latLongUser));
                        namedMarkers.put(guidUser, marker);
                    } else {
                        marker.setPosition(latLongUser);
                    }

                    if (mapboxMap != null) {
                        mapboxMap.setInfoWindowAdapter(new MapboxMap.InfoWindowAdapter() {
                            @Override
                            public View getInfoWindow(Marker marker) {

                                View v = getLayoutInflater().inflate(R.layout.find_info_window_all, null);

                                CircleImageView photo = (CircleImageView) v.findViewById(R.id.userAllMarker);
                                TextView fullnamed = (TextView) v.findViewById(R.id.fullnameAllMarker);
                                TextView phoned = (TextView) v.findViewById(R.id.phoneAllMarker);
                                CardView called = (CardView) v.findViewById(R.id.callAll);

                                photo.setImageResource(R.drawable.default_user);
                                fullnamed.setText(marker.getTitle());
                                phoned.setText(marker.getSnippet());

                                String phoneDataUser = marker.getSnippet();

                                String callMePhone = phoneDataUser.replaceFirst("0", "+62");

                                called.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        String url = "https://api.whatsapp.com/send?phone=" + callMePhone;
                                        Intent i = new Intent(Intent.ACTION_VIEW);
                                        i.setPackage("com.whatsapp");
                                        i.setData(Uri.parse(url));
                                        startActivity(i);
                                    }
                                });

                                return v;
                            }
                        });
                    }
                }
            }
        });
    }

    private MarkerOptions getMarkerOption(String guidUser) {
        MarkerOptions markerOptionsLive = new MarkerOptions();

        Bitmap mBitmap = BitmapUtils.getBitmapFromDrawable(drawableLive);
        IconFactory iconFactory = IconFactory.getInstance(getActivity());
        Icon icon = iconFactory.fromBitmap(mBitmap);
        markerOptionsLive.icon(icon);

        return markerOptionsLive;
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        maps.onLowMemory();
    }

}