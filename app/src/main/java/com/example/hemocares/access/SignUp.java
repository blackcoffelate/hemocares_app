package com.example.hemocares.access;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.example.hemocares.R;
import com.example.hemocares.service.App;
import com.example.hemocares.service.BaseURL;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import es.dmoral.toasty.Toasty;

public class SignUp extends AppCompatActivity {

    Button btnBack, btnRegist;
    TextInputEditText fullname, username, password, phone;
    AutoCompleteTextView bloodType;

    ProgressDialog loadingDialog;

    private RequestQueue mRequestQueue;
    String[] options = {"A", "B", "AB", "O"};
    int min_value = 0;
    int max_value = 99999999;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT < 16) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }

        setContentView(R.layout.activity_sign_up);

        mRequestQueue = Volley.newRequestQueue(this);

        fullname = findViewById(R.id.fullnameField);
        username = findViewById(R.id.usernameField);
        password = findViewById(R.id.passwordField);
        phone = findViewById(R.id.phoneField);
        bloodType = findViewById(R.id.bloodTypeField);

        fullname.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);

        final ArrayAdapter arrayAdapter = new ArrayAdapter(this, R.layout.blood_options, options);
        bloodType.setText(arrayAdapter.getItem(0).toString(), false);
        bloodType.setAdapter(arrayAdapter);

        loadingDialog = new ProgressDialog(this);
        loadingDialog.setCancelable(true);

        btnBack = findViewById(R.id.backloginBtn);
        btnRegist = findViewById(R.id.registBtn);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignUp.this, SignIn.class));
                Animatoo.animateSlideDown(SignUp.this);
            }
        });

        btnRegist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                App.getPref().clear();

                String fullnameData = fullname.getText().toString();
                String usernameData = username.getText().toString();
                String passwordData = password.getText().toString();
                String phoneData = phone.getText().toString();
                String bloodTypeData = bloodType.getText().toString();

                if (fullnameData.isEmpty()) {
                    Toasty.info(SignUp.this, "Nama lengkap kamu kosong ya...", Toasty.LENGTH_SHORT).show();
                } else if (usernameData.isEmpty()) {
                    Toasty.info(SignUp.this, "Username kamu kosong ya...", Toasty.LENGTH_SHORT).show();
                } else if (passwordData.isEmpty()) {
                    Toasty.info(SignUp.this, "Password kamu kosong ya...", Toasty.LENGTH_SHORT).show();
                } else if (phoneData.isEmpty()) {
                    Toasty.info(SignUp.this, "Nomor telpon kamu kosong ya...", Toasty.LENGTH_SHORT).show();
                } else if (bloodTypeData.isEmpty()) {
                    Toasty.info(SignUp.this, "Golongan Darah kamu kosong ya...", Toasty.LENGTH_SHORT).show();
                } else {
                    registFunction(fullnameData, usernameData, passwordData, phoneData, bloodTypeData);
                }
            }
        });
    }

    private void registFunction(String fullnameData, String usernameData, String passwordData, String phoneData, String bloodTypeData) {
        HashMap<String, String> parameter = new HashMap<String, String>();

        int random_noreg = (int) Math.floor(Math.random() * (max_value - min_value + 1) + min_value);
        String random_regnumber = "ID-" + random_noreg;

        parameter.put("NOREG", random_regnumber);
        parameter.put("FULLNAME", fullnameData);
        parameter.put("USERNAME", usernameData);
        parameter.put("PASSWORD", passwordData);
        parameter.put("PHONE", phoneData);
        parameter.put("BLOOD_TYPE", bloodTypeData);
        parameter.put("ROLE", "2");
        parameter.put("STATUS", "tidak");

        loadingDialog.setTitle("Tunggu sebentar ya...");
        showDialog();

        final JsonObjectRequest request = new JsonObjectRequest(BaseURL.register, new JSONObject(parameter),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        hideDialog();
                        try {
                            String msg = response.getString("message");
                            boolean statusMsg = response.getBoolean("status");

                            if (statusMsg == true) {
                                Toasty.success(SignUp.this, msg, Toasty.LENGTH_LONG).show();
                                startActivity(new Intent(SignUp.this, SignIn.class));
                                Animatoo.animateSlideDown(SignUp.this);
                            } else {
                                Toasty.warning(SignUp.this, msg, Toasty.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error: ", error.getMessage());
                hideDialog();
            }
        });
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
        startActivity(new Intent(SignUp.this, SignUp.class));
        Animatoo.animateSlideDown(SignUp.this);
    }
}