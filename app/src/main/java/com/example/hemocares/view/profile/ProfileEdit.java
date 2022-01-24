package com.example.hemocares.view.profile;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;

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
import com.example.hemocares.service.Utils;
import com.example.hemocares.service.VolleyMultipart;
import com.google.android.material.textfield.TextInputEditText;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;

public class ProfileEdit extends AppCompatActivity {

    ImageView backButtonEdit;
    AutoCompleteTextView religionData, genderData;
    TextInputEditText emailData, addressData, ageData, birthDateData;
    CardView editProfileButton;
    LinearLayout changePhotoData;
    CircleImageView photoUserData;

    String[] optionsReligion = {"Islam", "Hindu", "Budha", "Katholik", "Protestan", "Konghuchu", "Lainnya"};
    String[] optionsGender = {"Laki - Laki", "Perempuan"};

    Calendar calendar = Calendar.getInstance();
    ProgressDialog loadingDialog;

    private RequestQueue mRequestQueue;
    ModelUser modelUser;
    Bitmap bitmap;

    String GUID;
    public static final String KEY_User_Document1 = "PHOTO";
    private String imageFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        mRequestQueue = Volley.newRequestQueue(this);

        modelUser = (ModelUser) GsonHelper.parseGson(App.getPref().getString(Prefs.PREF_STORE_PROFILE, ""), new ModelUser());
        GUID = modelUser.getGUID();

        backButtonEdit = findViewById(R.id.backButtonEditProfile);
        backButtonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        emailData = findViewById(R.id.emailField);
        addressData = findViewById(R.id.addressField);
        addressData.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        ageData = findViewById(R.id.ageField);
        birthDateData = findViewById(R.id.birthDateField);
        religionData = findViewById(R.id.religionField);
        genderData = findViewById(R.id.genderField);

        editProfileButton = findViewById(R.id.updateDataUserButton);
        changePhotoData = findViewById(R.id.changePhotoUser);
        photoUserData = findViewById(R.id.photoUser);

        if (Build.VERSION.SDK_INT >= 24) {
            try {
                Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
                m.invoke(null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (modelUser.getPHOTO().equals("-")) {
            photoUserData.setImageResource(R.drawable.ic_thumbnail);
        } else {
            Picasso.get().load(BaseURL.baseUrl + "images/" + modelUser.getPHOTO()).into(photoUserData);
        }

        if (modelUser.getEMAIL().equals("-")) {
            emailData.setText("");
        } else {
            emailData.setText(modelUser.getEMAIL());
        }

        if (modelUser.getADDRESS().equals("-")) {
            addressData.setText("");
        } else {
            addressData.setText(modelUser.getADDRESS());
        }

        if (modelUser.getAGE().equals("-")) {
            ageData.setText("");
        } else {
            ageData.setText(modelUser.getAGE());
        }

        if (modelUser.getBIRTHDATE().equals("-")) {
            birthDateData.setText("");
        } else {
            birthDateData.setText(modelUser.getBIRTHDATE());
        }

        final ArrayAdapter arrayAdapterReligion = new ArrayAdapter(this, R.layout.blood_options, optionsReligion);
        religionData.setText(arrayAdapterReligion.getItem(0).toString(), false);
        religionData.setAdapter(arrayAdapterReligion);

        final ArrayAdapter arrayAdapterGender = new ArrayAdapter(this, R.layout.blood_options, optionsGender);
        genderData.setText(arrayAdapterGender.getItem(0).toString(), false);
        genderData.setAdapter(arrayAdapterGender);

        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }
        };

        birthDateData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(ProfileEdit.this, date, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        changePhotoData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takeCameraFunction();
            }
        });

        editProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailUserData = emailData.getText().toString();
                String addressUserData = addressData.getText().toString();
                String ageUserData = ageData.getText().toString();
                String birthDateUserData = birthDateData.getText().toString();
                String religionUserData = religionData.getText().toString();
                String genderUserData = genderData.getText().toString();

                if (emailUserData.isEmpty()) {
                    Toasty.info(ProfileEdit.this, "Email kamu kosong ya...", Toasty.LENGTH_SHORT).show();
                } else if (addressUserData.isEmpty()) {
                    Toasty.info(ProfileEdit.this, "Alamat lengkap kamu kosong ya...", Toasty.LENGTH_SHORT).show();
                } else if (ageUserData.isEmpty()) {
                    Toasty.info(ProfileEdit.this, "usia kamu kosong ya...", Toasty.LENGTH_SHORT).show();
                } else if (birthDateUserData.isEmpty()) {
                    Toasty.info(ProfileEdit.this, "Tanggal lahir kamu kosong ya...", Toasty.LENGTH_SHORT).show();
                } else if (religionUserData.isEmpty()) {
                    Toasty.info(ProfileEdit.this, "Agama kamu kosong ya...", Toasty.LENGTH_SHORT).show();
                } else if (genderUserData.isEmpty()) {
                    Toasty.info(ProfileEdit.this, "Jenis kelamin kamu kosong ya...", Toasty.LENGTH_SHORT).show();
                } else {
                    updateProfileFunction(bitmap);
                }
            }
        });

        loadingDialog = new ProgressDialog(this);
        loadingDialog.setCancelable(true);
    }

    private void takeCameraFunction() {
        addPermission();
        final CharSequence[] options = {"Ambil Foto", "Pilih dari Gallery", "Cancle"};
        AlertDialog.Builder builder = new AlertDialog.Builder(ProfileEdit.this);
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
                    photoUserData.setImageBitmap(bitmap);
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
                photoUserData.setImageBitmap(bitmap);
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
        Dexter.withActivity(ProfileEdit.this)
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
                        Toasty.error(ProfileEdit.this, "ERROR", Toasty.LENGTH_SHORT).show();
                    }
                })
                .onSameThread()
                .check();
    }

    private void updateProfileFunction(final Bitmap bitmap) {
        loadingDialog.setTitle("Tunggu sebentar ya...");
        showDialog();

        VolleyMultipart request = new VolleyMultipart(Request.Method.PUT, BaseURL.updateUser + GUID,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        hideDialog();
                        try {
                            JSONObject jsonObject = new JSONObject(new String(response.data));
                            String msg = jsonObject.getString("message");
                            boolean statusMsg = jsonObject.getBoolean("status");
                            if (statusMsg == true) {
                                JSONObject dataUser = jsonObject.getJSONObject("data");
                                JSONObject data = dataUser.getJSONObject("data");
                                JSONObject userData = data.getJSONObject("user");
                                App.getPref().put(Prefs.PREF_IS_LOGEDIN, true);
                                Utils.storeProfile(userData.toString());
                                Toasty.success(ProfileEdit.this, msg, Toasty.LENGTH_LONG).show();
                                startActivity(new Intent(ProfileEdit.this, MainActivity.class));
                                Animatoo.animateSlideRight(ProfileEdit.this);
                            } else {
                                Toasty.warning(ProfileEdit.this, msg, Toasty.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideDialog();
                Toasty.warning(ProfileEdit.this, error.getMessage(), Toasty.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> parameter = new HashMap<>();
                parameter.put("EMAIL", emailData.getText().toString());
                parameter.put("ADDRESS", addressData.getText().toString());
                parameter.put("AGE", ageData.getText().toString());
                parameter.put("BIRTHDATE", birthDateData.getText().toString());
                parameter.put("RELIGION", religionData.getText().toString());
                parameter.put("GENDER", genderData.getText().toString());
                parameter.put(KEY_User_Document1, imageFile);
                return parameter;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        mRequestQueue = Volley.newRequestQueue(ProfileEdit.this);
        mRequestQueue.add(request);
    }

    private void updateLabel() {
        String formatDate = "dd-MM-yyyy";
        SimpleDateFormat dateFormat = new SimpleDateFormat(formatDate, Locale.US);
        birthDateData.setText(dateFormat.format(calendar.getTime()));
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