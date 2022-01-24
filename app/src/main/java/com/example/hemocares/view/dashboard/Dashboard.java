package com.example.hemocares.view.dashboard;

import android.content.Intent;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.example.hemocares.view.dashboard.adapter.AdapterUserJoin;
import com.example.hemocares.R;
import com.example.hemocares.model.ModelUser;
import com.example.hemocares.model.ModelUserShow;
import com.example.hemocares.service.App;
import com.example.hemocares.service.BaseURL;
import com.example.hemocares.service.GsonHelper;
import com.example.hemocares.service.Prefs;
import com.example.hemocares.view.dashboard.assets.AdapterBannerSlider;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderView;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class Dashboard extends Fragment {

    ModelUser modelUser;
    List<ModelUserShow> listUserData;
    String statusUser = "iya";
    private RequestQueue mRequestQueue;

    SliderView sliderView;
    Button joinHemocaresButton;
    CardView joinCard, emptyData;
    CircleImageView profilePhotoData;
    RecyclerView recycleUser;
    RecyclerView.Adapter adapterRecycleUserJoin;
    LinearLayout availableData;
    TextView viewAllUserJoin;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_dashboard, container, false);

        joinHemocaresButton = v.findViewById(R.id.joinHemocares);
        sliderView = v.findViewById(R.id.bannerSlider);
        joinCard = v.findViewById(R.id.joinUserCard);
        profilePhotoData = v.findViewById(R.id.profilePhotoUser);

        emptyData = v.findViewById(R.id.emptyDataDisplay);
        availableData = v.findViewById(R.id.dataAvailable);
        viewAllUserJoin = v.findViewById(R.id.viewAllUser);
        viewAllUserJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), ShowAllUser.class));
                Animatoo.animateSlideLeft(getActivity());
            }
        });

        mRequestQueue = Volley.newRequestQueue(getActivity());

        List<Integer> imageList = new ArrayList<>();
        imageList.add(R.drawable.banner_1);
        imageList.add(R.drawable.banner_2);
        imageList.add(R.drawable.banner_3);

        AdapterBannerSlider adapterBanner = new AdapterBannerSlider(imageList);

        sliderView.setSliderAdapter(adapterBanner);
        sliderView.setAutoCycle(true);
        sliderView.setIndicatorAnimation(IndicatorAnimationType.WORM);

        modelUser = (ModelUser) GsonHelper.parseGson(App.getPref().getString(Prefs.PREF_STORE_PROFILE, ""), new ModelUser());
        TextView fullnameUserData = v.findViewById(R.id.fullnameUser);
        fullnameUserData.setText(modelUser.getFULLNAME());

        if (modelUser.getSTATUS().equals("tidak")) {
            joinCard.setVisibility(View.VISIBLE);
        } else {
            joinCard.setVisibility(View.GONE);
        }

        if (modelUser.getPHOTO().equals("-")) {
            profilePhotoData.setImageResource(R.drawable.ic_thumbnail);
        } else if (modelUser.getPHOTO().equals(null)){
            profilePhotoData.setImageResource(R.drawable.ic_thumbnail);
        } else {
            Picasso.get().load(BaseURL.baseUrl + "images/" + modelUser.getPHOTO()).into(profilePhotoData);
        }

        joinHemocaresButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), UserAggrement.class));
                Animatoo.animateSlideLeft(getActivity());
            }
        });

        recycleUser = v.findViewById(R.id.listUserJoin);

        listUserData = new ArrayList<>();

        recycleUser.setHasFixedSize(true);
        recycleUser.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        adapterRecycleUserJoin = new AdapterUserJoin(getActivity(), listUserData);

        showDataUserJoinFunction();

        return v;
    }

    private void showDataUserJoinFunction() {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, BaseURL.getUserByStatus + statusUser, null,
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
                                        ModelUserShow dataUserList = new ModelUserShow();
                                        String guidUser = dataObject.getString("GUID");
                                        String bloodTypeUser = dataObject.getString("BLOOD_TYPE");
                                        String fullnameUser = dataObject.getString("FULLNAME");
                                        String ageUser = dataObject.getString("AGE");
                                        String phoneUser = dataObject.getString("PHONE");
                                        String addressUser = dataObject.getString("ADDRESS");
                                        String profilePhotoUser = dataObject.getString("PHOTO");

                                        dataUserList.setGUID(guidUser);
                                        dataUserList.setBLOOD_TYPE(bloodTypeUser);
                                        dataUserList.setFULLNAME(fullnameUser);
                                        dataUserList.setAGE(ageUser);
                                        dataUserList.setPHONE(phoneUser);
                                        dataUserList.setADDRESS(addressUser);
                                        dataUserList.setPHOTO(profilePhotoUser);

                                        listUserData.add(dataUserList);
                                        recycleUser.setAdapter(adapterRecycleUserJoin);
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
}