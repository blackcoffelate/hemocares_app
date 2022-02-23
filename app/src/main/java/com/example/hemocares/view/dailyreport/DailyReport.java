package com.example.hemocares.view.dailyreport;

import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.example.hemocares.model.ModelUserReportAll;
import com.example.hemocares.service.App;
import com.example.hemocares.service.BaseURL;
import com.example.hemocares.service.GsonHelper;
import com.example.hemocares.service.Prefs;
import com.example.hemocares.view.dailyreport.adapter.AdapterUserReportAll;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DailyReport extends Fragment {

    ModelUser modelUser;
    List<ModelUserReportAll> listUserDataReport;
    private RequestQueue mRequestQueue;

    RecyclerView recycleUserReportAll;
    RecyclerView.Adapter adapterRecycleUserReportAll;
    LinearLayout availableDataAll, emptyDataAll;
    String STATUSUSER = "iya";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_daily_report, container, false);

        emptyDataAll = v.findViewById(R.id.emptyDataReportDisplayAll);
        availableDataAll = v.findViewById(R.id.dataAvailableReportAll);
        mRequestQueue = Volley.newRequestQueue(getActivity());

        modelUser = (ModelUser) GsonHelper.parseGson(App.getPref().getString(Prefs.PREF_STORE_PROFILE, ""), new ModelUser());

        recycleUserReportAll = v.findViewById(R.id.listUserReportAll);
        listUserDataReport = new ArrayList<>();

        recycleUserReportAll.setHasFixedSize(true);
        recycleUserReportAll.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        adapterRecycleUserReportAll = new AdapterUserReportAll(getActivity(), listUserDataReport);

        showDataReportFunction();

        return v;
    }

    private void showDataReportFunction() {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, BaseURL.getReportAll + STATUSUSER, null,
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

                                        listUserDataReport.add(dataUserReportList);
                                        recycleUserReportAll.setAdapter(adapterRecycleUserReportAll);
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

}