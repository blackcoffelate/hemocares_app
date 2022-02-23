package com.example.hemocares.view.profile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.hemocares.R;
import com.example.hemocares.model.ModelUserDetails;
import com.example.hemocares.model.ModelUserReportAll;
import com.example.hemocares.service.BaseURL;
import com.example.hemocares.view.profile.adapter.AdapterUserReportAllDetail;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileDetails extends AppCompatActivity {

    TextView fullnameUser, statusUser, emailUser, phoneUser, addressUser, religionUser, bloodUser, ageUser, genderUser;
    ImageView userPhotoBackgroundData;
    CircleImageView photoUserData;
    Button callMeData;
    LinearLayout availableData, emptyData;

    RecyclerView recycleUserDetail;
    RecyclerView.Adapter adapterRecycleUserJoinDetail;
    List<ModelUserReportAll> listUserDataReportDetail;

    String GUID;
    private RequestQueue mRequestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_details);

        mRequestQueue = Volley.newRequestQueue(this);

        fullnameUser = findViewById(R.id.fullnameUserDetails);
        statusUser = findViewById(R.id.statusUserDetails);
        emailUser = findViewById(R.id.emailUserDetails);
        phoneUser = findViewById(R.id.phoneUserDetails);
        addressUser = findViewById(R.id.addressUserDetails);
        religionUser = findViewById(R.id.religionUserDetails);
        bloodUser = findViewById(R.id.bloodUserDetails);
        ageUser = findViewById(R.id.ageUserDetails);
        genderUser = findViewById(R.id.genderUserDetails);
        userPhotoBackgroundData = findViewById(R.id.photoUserBackground);
        photoUserData = findViewById(R.id.photoUserDetails);
        callMeData = findViewById(R.id.callMe);

        emptyData = findViewById(R.id.emptyDataDisplayDetail);
        availableData = findViewById(R.id.dataAvailableDetail);

        recycleUserDetail = findViewById(R.id.listUserJoinDetail);
        listUserDataReportDetail = new ArrayList<>();

        recycleUserDetail.setHasFixedSize(true);
        recycleUserDetail.setLayoutManager(new LinearLayoutManager(ProfileDetails.this, LinearLayoutManager.VERTICAL, false));
        adapterRecycleUserJoinDetail = new AdapterUserReportAllDetail(ProfileDetails.this, listUserDataReportDetail);

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            GUID = extras.getString("GUID");
        }

        showDetailUserFunction();
        showDataReportFunction();
    }

    private void showDataReportFunction() {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, BaseURL.getReportAllDetail + GUID, null,
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
                                        ModelUserReportAll dataUserReportList = new ModelUserReportAll();
                                        String guidUser = dataObject.getString("GUID");
                                        String contentReport = dataObject.getString("CONTENT");
                                        String addressReport = dataObject.getString("ADDRESS");
                                        String photoReport = dataObject.getString("PHOTO");
                                        String typeReport = dataObject.getString("REPORT_TYPE");
                                        String createData = dataObject.getString("CREATED_AT");

                                        JSONArray dataUserDetail = dataObject.getJSONArray("USER_DATA");

                                        if (dataUserDetail.length() > 0) {
                                            for (int x = 0; x < dataUserDetail.length(); x++) {
                                                JSONObject dataUserObject = dataUserDetail.getJSONObject(x);
                                                String fullnameUser = dataUserObject.getString("FULLNAME");
                                                String photoUser = dataUserObject.getString("PHOTO");
                                                String bloodTypeUser = dataUserObject.getString("BLOOD_TYPE");

                                                dataUserReportList.setFULLNAME(fullnameUser);
                                                dataUserReportList.setPHOTO(photoUser);
                                                dataUserReportList.setBLOOD_TYPE(bloodTypeUser);
                                            }
                                        }

                                        dataUserReportList.setGUID(guidUser);
                                        dataUserReportList.setCONTENT(contentReport);
                                        dataUserReportList.setADDRESS(addressReport);
                                        dataUserReportList.setPHOTO_CONTENT(photoReport);
                                        dataUserReportList.setREPORT_TYPE(typeReport);
                                        dataUserReportList.setCREATED_AT(createData);

                                        listUserDataReportDetail.add(dataUserReportList);
                                        recycleUserDetail.setAdapter(adapterRecycleUserJoinDetail);
                                    }
                                    emptyData.setVisibility(View.GONE);
                                    availableData.setVisibility(View.VISIBLE);
                                } else {
                                    emptyData.setVisibility(View.VISIBLE);
                                    availableData.setVisibility(View.GONE);
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

    private void showDetailUserFunction() {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, BaseURL.getUserById + GUID, null,
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
                                        ModelUserDetails dataUserList = new ModelUserDetails();
                                        String guidUserData = dataObject.getString("GUID");
                                        String bloodTypeUserData = dataObject.getString("BLOOD_TYPE");
                                        String fullnameUserData = dataObject.getString("FULLNAME");
                                        String ageUserData = dataObject.getString("AGE");
                                        String phoneUserData = dataObject.getString("PHONE");
                                        String addressUserData = dataObject.getString("ADDRESS");
                                        String profilePhotoUserData = dataObject.getString("PHOTO");
                                        String emailUserData = dataObject.getString("EMAIL");
                                        String religionUserData = dataObject.getString("RELIGION");
                                        String genderUserData = dataObject.getString("GENDER");
                                        String statusUserData = dataObject.getString("STATUS");

                                        fullnameUser.setText(fullnameUserData);
                                        phoneUser.setText(phoneUserData);
                                        emailUser.setText(emailUserData);
                                        addressUser.setText(addressUserData);
                                        bloodUser.setText(bloodTypeUserData);
                                        ageUser.setText(ageUserData);
                                        religionUser.setText(religionUserData);
                                        genderUser.setText(genderUserData);

                                        String callMePhone = phoneUserData.replaceFirst("0", "+62");

                                        callMeData.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                String url = "https://api.whatsapp.com/send?phone=" + callMePhone;
                                                Intent i = new Intent(Intent.ACTION_VIEW);
                                                i.setPackage("com.whatsapp");
                                                i.setData(Uri.parse(url));
                                                startActivity(i);
                                            }
                                        });

                                        if (profilePhotoUserData.equals("-")) {
                                            userPhotoBackgroundData.setImageResource(R.drawable.default_user);
                                            photoUserData.setImageResource(R.drawable.default_user);
                                        } else {
                                            Picasso.get().load(BaseURL.baseUrl + "images/" + profilePhotoUserData).into(userPhotoBackgroundData);
                                            Picasso.get().load(BaseURL.baseUrl + "images/" + profilePhotoUserData).into(photoUserData);
                                        }

                                        if (statusUserData.equals("iya")) {
                                            statusUser.setText("Tergabung");
                                        } else {
                                            statusUser.setText("Belum Tergabung");
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
}