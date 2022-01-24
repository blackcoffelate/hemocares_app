package com.example.hemocares.access;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
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
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import es.dmoral.toasty.Toasty;

public class SignIn extends AppCompatActivity {

    boolean BackPress = false;
    private RequestQueue mRequestQueue;
    ModelUser modelUser;

    Button btnRegist, btnLogin;
    TextInputEditText username, password;
    ProgressDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT < 16) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }

        setContentView(R.layout.activity_sign_in);

        mRequestQueue = Volley.newRequestQueue(this);

        username = findViewById(R.id.usernameField);
        password = findViewById(R.id.passwordField);

        btnRegist = findViewById(R.id.registBtn);
        btnLogin = findViewById(R.id.loginBtn);

        loadingDialog = new ProgressDialog(this);
        loadingDialog.setCancelable(true);

        btnRegist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignIn.this, SignUp.class));
                Animatoo.animateSlideUp(SignIn.this);
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String usernameData = username.getText().toString();
                String passwordData = password.getText().toString();

                if (usernameData.isEmpty()) {
                    Toasty.info(SignIn.this, "Username kamu kosong ya...", Toasty.LENGTH_SHORT).show();
                } else if (passwordData.isEmpty()) {
                    Toasty.info(SignIn.this, "Password kamu kosong ya...", Toasty.LENGTH_SHORT).show();
                } else {
                    loginFunction(usernameData, passwordData);
                }
            }
        });

        modelUser = (ModelUser) GsonHelper.parseGson(App.getPref().getString(Prefs.PREF_STORE_PROFILE, ""), new ModelUser());

        if (Utils.isLoggedIn()){
            int roleUser = Integer.parseInt(modelUser.getROLE());

            if (roleUser == 2){
                startActivity(new Intent(SignIn.this, MainActivity.class));
                Animatoo.animateSlideDown(SignIn.this);
            }
        }
    }

    private void loginFunction(String usernameData, String passwordData) {
        HashMap<String, String> parameter = new HashMap<String, String>();

        parameter.put("USERNAME", usernameData);
        parameter.put("PASSWORD", passwordData);
        parameter.put("ROLE", "2");

        loadingDialog.setTitle("Tunggu sebentar ya...");
        showDialog();

        final JsonObjectRequest request = new JsonObjectRequest(BaseURL.login, new JSONObject(parameter),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        hideDialog();
                        try {
                            String msg = response.getString("message");
                            boolean statusMsg = response.getBoolean("status");

                            if (statusMsg == true) {
                                JSONObject data = response.getJSONObject("data");
                                JSONObject dataUser = data.getJSONObject("user");

                                String roleUser = dataUser.getString("ROLE");
                                App.getPref().put(Prefs.PREF_IS_LOGEDIN, true);
                                Utils.storeProfile(dataUser.toString());

                                if (roleUser.equals("2")) {
                                    startActivity(new Intent(SignIn.this, MainActivity.class));
                                    Animatoo.animateSlideDown(SignIn.this);
                                }
                            } else {
                                Toasty.warning(SignIn.this, msg, Toasty.LENGTH_SHORT).show();
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
        if (BackPress) {
            super.onBackPressed();
            return;
        }
        this.BackPress = true;
        Toasty.info(this, "Tekan lagi untuk keluar ...", Toasty.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                BackPress = false;
            }
        }, 2000);
    }
}