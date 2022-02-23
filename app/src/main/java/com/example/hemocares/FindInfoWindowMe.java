package com.example.hemocares;

import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.example.hemocares.model.ModelUser;
import com.example.hemocares.service.App;
import com.example.hemocares.service.BaseURL;
import com.example.hemocares.service.GsonHelper;
import com.example.hemocares.service.Prefs;
import com.squareup.picasso.Picasso;

import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.infowindow.InfoWindow;

import de.hdodenhof.circleimageview.CircleImageView;

public class FindInfoWindowMe extends InfoWindow {

    ModelUser modelUser;

    public FindInfoWindowMe(int layoutResId, MapView mapView) {
        super(layoutResId, mapView);
        modelUser = (ModelUser) GsonHelper.parseGson(App.getPref().getString(Prefs.PREF_STORE_PROFILE, ""), new ModelUser());
    }

    public FindInfoWindowMe(View v, MapView mapView) {
        super(v, mapView);
    }

    @Override
    public void onOpen(Object item) {
        CardView markerMeDefault = (CardView) mView.findViewById(R.id.markerMe);
        CircleImageView userMe = (CircleImageView) mView.findViewById(R.id.userMeMarker);
        TextView fullnameMe = (TextView) mView.findViewById(R.id.fullnameMeMarker);
        TextView bloodMe = (TextView) mView.findViewById(R.id.bloodTypeMeMarker);
        TextView phoneMe = (TextView) mView.findViewById(R.id.phoneMeMarker);
        TextView addressMe = (TextView) mView.findViewById(R.id.addressMeMarker);

        fullnameMe.setText(modelUser.getFULLNAME());
        bloodMe.setText(modelUser.getBLOOD_TYPE());
        phoneMe.setText(modelUser.getPHONE());
        addressMe.setText(modelUser.getADDRESS());
        Picasso.get().load(BaseURL.baseUrl + "images/" + modelUser.getPHOTO()).into(userMe);

        markerMeDefault.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeAllInfoWindowsOn(mMapView);
            }
        });
    }

    @Override
    public void onClose() {

    }
}
