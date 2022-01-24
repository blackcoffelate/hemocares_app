package com.example.hemocares.view.dashboard;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.android.volley.Request;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import es.dmoral.toasty.Toasty;

public class UserAggrement extends AppCompatActivity {

    ImageView backUserAggrement;
    CardView aggrementButton;
    CheckBox checkboxAggrement;
    ProgressDialog loadingDialog;

    private RequestQueue mRequestQueue;
    ModelUser modelUser;

    String GUID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_aggrement);

        mRequestQueue = Volley.newRequestQueue(this);

        modelUser = (ModelUser) GsonHelper.parseGson(App.getPref().getString(Prefs.PREF_STORE_PROFILE, ""), new ModelUser());
        GUID = modelUser.getGUID();

        loadingDialog = new ProgressDialog(this);
        loadingDialog.setCancelable(true);

        backUserAggrement = findViewById(R.id.backButtonUserAggrement);
        aggrementButton = findViewById(R.id.aggrementDataUserButton);
        checkboxAggrement = findViewById(R.id.checkBoxAggrementUser);

        aggrementButton.setEnabled(false);
        aggrementButton.setCardBackgroundColor(getResources().getColor(R.color.grey));

        aggrementButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfileFunction();
            }
        });

        backUserAggrement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UserAggrement.this, MainActivity.class));
                Animatoo.animateSlideRight(UserAggrement.this);
            }
        });

    }

    private void updateProfileFunction() {
        HashMap<String, String> parameter = new HashMap<>();
        parameter.put("STATUS", "iya");

        loadingDialog.setTitle("Tunggu sebentar ya...");
        showDialog();

        final JsonObjectRequest request = new JsonObjectRequest(Request.Method.PUT, BaseURL.updateUserStatus + GUID, new JSONObject(parameter),
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
                                Log.d("MODEL", String.valueOf(data));
                                App.getPref().put(Prefs.PREF_IS_LOGEDIN, true);
                                Utils.storeProfile(dataUser.toString());
                                Toasty.success(UserAggrement.this, msg, Toasty.LENGTH_LONG).show();
                                startActivity(new Intent(UserAggrement.this, MainActivity.class));
                                Animatoo.animateSlideRight(UserAggrement.this);
                            } else {
                                Toasty.warning(UserAggrement.this, msg, Toasty.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error :", error.getMessage());
                hideDialog();
            }
        });
        mRequestQueue.add(request);
    }

    public void itemClick(View view) {
        if (checkboxAggrement.isChecked()){
            aggrementButton.setEnabled(true);
            aggrementButton.setCardBackgroundColor(getResources().getColor(R.color.blue));
        } else {
            aggrementButton.setEnabled(false);
            aggrementButton.setCardBackgroundColor(getResources().getColor(R.color.grey));
        }
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
}