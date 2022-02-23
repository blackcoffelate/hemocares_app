package com.example.hemocares.view.dailyreport;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.example.hemocares.MainActivity;
import com.example.hemocares.R;
import com.example.hemocares.model.ModelUser;
import com.example.hemocares.service.App;
import com.example.hemocares.service.BaseURL;
import com.example.hemocares.service.GsonHelper;
import com.example.hemocares.service.Prefs;
import com.example.hemocares.service.VolleyMultipart;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import es.dmoral.toasty.Toasty;

public class DailyReportAdd extends AppCompatActivity {

    ImageView backButton, photoReportData;
    CardView changePhotoData, reportDataButton;
    TextInputEditText reportData;
    AutoCompleteTextView typeReport;
    TextView latUserData, longUserData, addressUserData;

    String[] optionsReport = {"Donor Darah", "Seminar Kesehatan", "Donasi"};

    ProgressDialog loadingDialog;

    private RequestQueue mRequestQueue;
    ModelUser modelUser;
    Bitmap bitmap;

    String GUID, LATITUDEDATA, LONGITUDEDATA, ADDRESSDATA, STATUSUSER;
    public static final String KEY_User_Document1 = "PHOTO";
    private String imageFile;
    FusedLocationProviderClient fusedLocationProviderClient;
    Geocoder geocoder;
    List<Address> addresses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_report_add);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(DailyReportAdd.this);
        geocoder = new Geocoder(this, Locale.getDefault());

        mRequestQueue = Volley.newRequestQueue(this);

        modelUser = (ModelUser) GsonHelper.parseGson(App.getPref().getString(Prefs.PREF_STORE_PROFILE, ""), new ModelUser());
        GUID = modelUser.getGUID();
        STATUSUSER = modelUser.getSTATUS();

        backButton = findViewById(R.id.backButtonDailyReport);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        reportData = findViewById(R.id.reportField);
        typeReport = findViewById(R.id.typeReportField);

        reportDataButton = findViewById(R.id.reportDataUserButton);
        changePhotoData = findViewById(R.id.changePhotoReport);
        photoReportData = findViewById(R.id.photoReport);
        latUserData = findViewById(R.id.latUser);
        longUserData = findViewById(R.id.longUser);
        addressUserData = findViewById(R.id.addressNowUser);

        if (Build.VERSION.SDK_INT >= 24) {
            try {
                Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
                m.invoke(null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        latUserData.setText("-00.00000000");
        longUserData.setText("00.00000000");
        addressUserData.setText("-");

        final ArrayAdapter arrayAdapterReportType = new ArrayAdapter(this, R.layout.blood_options, optionsReport);
        typeReport.setText(arrayAdapterReportType.getItem(0).toString(), false);
        typeReport.setAdapter(arrayAdapterReportType);

        changePhotoData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(DailyReportAdd.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(DailyReportAdd.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    takeCameraFunction();
                    getCurrentLocation();
                } else {
                    ActivityCompat.requestPermissions(DailyReportAdd.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
                }
            }
        });

        reportDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String typeReportUserData = typeReport.getText().toString();
                String reportUserData = reportData.getText().toString();

                if (typeReportUserData.isEmpty()) {
                    Toasty.info(DailyReportAdd.this, "Jenis pelaporan kosong ya...", Toasty.LENGTH_SHORT).show();
                } else if (reportUserData.isEmpty()) {
                    Toasty.info(DailyReportAdd.this, "Pelaporan kamu kosong ya...", Toasty.LENGTH_SHORT).show();
                } else {
                    reportFunction(bitmap);
                }
            }
        });

        loadingDialog = new ProgressDialog(this);
        loadingDialog.setCancelable(true);
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 100 && grantResults.length > 0 && (grantResults[0] + grantResults[1] == PackageManager.PERMISSION_GRANTED)) {
            getCurrentLocation();
        } else {
            Toasty.warning(DailyReportAdd.this, "PERMISSION DENIED", Toasty.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("MissingPermission")
    private void getCurrentLocation() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    Location location = task.getResult();
                    if (location != null) {
                        latUserData.setText(String.valueOf(location.getLatitude()));
                        LATITUDEDATA = String.valueOf(location.getLatitude());
                        longUserData.setText(String.valueOf(location.getLongitude()));
                        LONGITUDEDATA = String.valueOf(location.getLongitude());
                        try {
                            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                            addressUserData.setText(addresses.get(0).getAddressLine(0));
                            ADDRESSDATA = addresses.get(0).getAddressLine(0);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        LocationRequest locationRequest = new LocationRequest().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY).setInterval(10000).setFastestInterval(1000).setNumUpdates(1);
                        LocationCallback locationCallback = new LocationCallback() {
                            @Override
                            public void onLocationResult(LocationResult locationResult) {
                                Location locationOne = locationResult.getLastLocation();
                                latUserData.setText(String.valueOf(locationOne.getLatitude()));
                                LATITUDEDATA = String.valueOf(locationOne.getLatitude());
                                longUserData.setText(String.valueOf(locationOne.getLongitude()));
                                LONGITUDEDATA = String.valueOf(locationOne.getLongitude());
                                try {
                                    addresses = geocoder.getFromLocation(locationOne.getLatitude(), locationOne.getLongitude(), 1);
                                    addressUserData.setText(addresses.get(0).getAddressLine(0));
                                    ADDRESSDATA = addresses.get(0).getAddressLine(0);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
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

    private void takeCameraFunction() {
        addPermission();
        final CharSequence[] options = {"Ambil Foto", "Pilih dari Gallery", "Cancle"};
        AlertDialog.Builder builder = new AlertDialog.Builder(DailyReportAdd.this);
        builder.setTitle("Tambahkan Foto");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (options[which].equals("Ambil Foto")) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    File file = new File(Environment.getExternalStorageDirectory(), "temp.jpg");
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
                    startActivityForResult(intent, 1);
                } else if (options[which].equals("Pilih dari Gallery")) {
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, 2);
                } else if (options[which].equals("Cancle")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_CANCELED) {
            if (requestCode == 1) {
                File file = new File(Environment.getExternalStorageDirectory().toString());
                for (File temp : file.listFiles()) {
                    if (temp.getName().equals("temp.jpg")) {
                        file = temp;
                        break;
                    }
                }
                try {
                    BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
                    bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), bitmapOptions);
                    bitmap = getResizedBitmap(bitmap, 400);
                    photoReportData.setImageBitmap(bitmap);
                    BitMapToString(bitmap);
                    String path = Environment.getExternalStorageDirectory() + File.separator + "hemocares" + File.separator + "default";
                    file.delete();
                    OutputStream outFile = null;
                    File file_a = new File(path, String.valueOf(System.currentTimeMillis()) + ".jpg");

                    try {
                        file_a.mkdirs();
                        outFile = new FileOutputStream(file_a);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 20, outFile);
                        outFile.flush();
                        outFile.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (requestCode == 2) {
                Uri selectedImage = data.getData();
                String[] filePath = {MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(selectedImage, filePath, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePath[0]);
                String picturePath = cursor.getString(columnIndex);
                cursor.close();
                bitmap = (BitmapFactory.decodeFile(picturePath));
                bitmap = getResizedBitmap(bitmap, 400);
                Log.w("path of", picturePath + "");
                photoReportData.setImageBitmap(bitmap);
                BitMapToString(bitmap);
            }
        }
    }

    public String BitMapToString(Bitmap userImage1) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        userImage1.compress(Bitmap.CompressFormat.PNG, 20, baos);
        byte[] b = baos.toByteArray();
        imageFile = Base64.encodeToString(b, Base64.DEFAULT);
        return imageFile;
    }

    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    public void addPermission() {
        Dexter.withActivity(DailyReportAdd.this)
                .withPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                        }
                        if (report.isAnyPermissionPermanentlyDenied()) {
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).
                withErrorListener(new PermissionRequestErrorListener() {
                    @Override
                    public void onError(DexterError error) {
                        Toasty.error(DailyReportAdd.this, "ERROR", Toasty.LENGTH_SHORT).show();
                    }
                })
                .onSameThread()
                .check();
    }

    private void reportFunction(Bitmap bitmap) {
        loadingDialog.setTitle("Tunggu sebentar ya...");
        showDialog();

        VolleyMultipart request = new VolleyMultipart(Request.Method.POST, BaseURL.postReport,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        hideDialog();
                        try {
                            JSONObject jsonObject = new JSONObject(new String(response.data));
                            String msg = jsonObject.getString("message");
                            boolean statusMsg = jsonObject.getBoolean("status");
                            if (statusMsg == true) {
                                Toasty.success(DailyReportAdd.this, msg, Toasty.LENGTH_LONG).show();
                                startActivity(new Intent(DailyReportAdd.this, MainActivity.class));
                                Animatoo.animateSlideRight(DailyReportAdd.this);
                            } else {
                                Toasty.warning(DailyReportAdd.this, msg, Toasty.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideDialog();
                Toasty.warning(DailyReportAdd.this, error.getMessage(), Toasty.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> parameter = new HashMap<>();
                parameter.put("GUID", GUID);
                parameter.put("CONTENT", reportData.getText().toString());
                parameter.put("REPORT_TYPE", typeReport.getText().toString());
                parameter.put("LATITUDE", LATITUDEDATA);
                parameter.put("LONGITUDE", LONGITUDEDATA);
                parameter.put("ADDRESS", ADDRESSDATA);
                parameter.put("STATUS_USER", STATUSUSER);
                parameter.put(KEY_User_Document1, imageFile);
                return parameter;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        mRequestQueue = Volley.newRequestQueue(DailyReportAdd.this);
        mRequestQueue.add(request);
    }

    private void showDialog() {
        if (!loadingDialog.isShowing()) {
            loadingDialog.show();
            loadingDialog.setCancelable(false);
            loadingDialog.setContentView(R.layout.loading);
            loadingDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
    }

    private void hideDialog() {
        if (loadingDialog.isShowing()) {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    loadingDialog.dismiss();
                }
            }, 3000);
            loadingDialog.setContentView(R.layout.loading);
            loadingDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}