package com.example.hemocares.view.dashboard;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ActionBar;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.hemocares.R;
import com.example.hemocares.model.ModelUser;
import com.example.hemocares.model.ModelUserShowAll;
import com.example.hemocares.service.App;
import com.example.hemocares.service.BaseURL;
import com.example.hemocares.service.GsonHelper;
import com.example.hemocares.service.Prefs;
import com.example.hemocares.view.dashboard.adapter.AdapterUserJoinAll;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ShowAllUser extends AppCompatActivity {

    ModelUser modelUser;
    List<ModelUserShowAll> listUserDataAll;
    String statusUser = "iya";
    String searchText;
    private RequestQueue mRequestQueue;

    RecyclerView recycleUserAll;
    AdapterUserJoinAll adapterRecycleUserJoinAll;
    LinearLayout availableDataAll;
    CardView emptyDataAll;
    SearchView searchUserData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_all_user);

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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}