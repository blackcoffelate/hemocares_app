package com.example.hemocares;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.example.hemocares.model.ModelUser;
import com.example.hemocares.model.ModelUserReportAll;
import com.example.hemocares.model.ModelUserShowMarkerAll;
import com.example.hemocares.service.App;
import com.example.hemocares.service.BaseURL;
import com.example.hemocares.service.GsonHelper;
import com.example.hemocares.service.Prefs;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.infowindow.InfoWindow;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class FindInfoWindowAll extends InfoWindow {

    private List<ModelUserShowMarkerAll> listMarker;

    public FindInfoWindowAll(int layoutResId, MapView mapView, List<ModelUserShowMarkerAll> listMarker) {
        super(layoutResId, mapView);
        this.listMarker = listMarker;
    }

    public FindInfoWindowAll(View v, MapView mapView) {
        super(v, mapView);
    }

    @Override
    public void onOpen(Object item) {
        for (int i = 0; i < listMarker.size(); i++) {

            CardView markerMeDefault = (CardView) mView.findViewById(R.id.markerAll);
            CardView callMeDefault = (CardView) mView.findViewById(R.id.callAll);
            CircleImageView userMe = (CircleImageView) mView.findViewById(R.id.userAllMarker);
            TextView fullnameMe = (TextView) mView.findViewById(R.id.fullnameAllMarker);

            ModelUserShowMarkerAll modelMarker = listMarker.get(i);
            Log.d("DATA MARKER", new Gson().toJson(modelMarker));
            fullnameMe.setText(modelMarker.getFULLNAME());
//            bloodMe.setText(modelMarker.getBLOOD_TYPE());
//            phoneMe.setText(modelMarker.getPHONE());
//            addressMe.setText(modelMarker.getADDRESS());
//            Picasso.get().load(BaseURL.baseUrl + "images/" + modelMarker.getPHOTO()).into(userMe);

//            String phoneAllData = modelMarker.getPHONE();
//            String callAllPhone = phoneAllData.replaceFirst("0", "+62");

//            callMeDefault.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
////                    String url = "https://api.whatsapp.com/send?phone=" + callAllPhone;
//                    Intent i = new Intent(Intent.ACTION_VIEW);
//                    i.setPackage("com.whatsapp");
////                    i.setData(Uri.parse(url));
//                    getMapView().getContext().startActivity(i);
//                }
//            });

            markerMeDefault.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    closeAllInfoWindowsOn(mMapView);
                }
            });
        }
    }

    @Override
    public void onClose() {

    }
}
