package com.example.hemocares.view.profile;

import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.example.hemocares.R;
import com.example.hemocares.access.SignIn;
import com.example.hemocares.model.ModelUser;
import com.example.hemocares.service.App;
import com.example.hemocares.service.BaseURL;
import com.example.hemocares.service.GsonHelper;
import com.example.hemocares.service.Prefs;
import com.example.hemocares.service.Utils;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class Profile extends Fragment {

    ModelUser modelUser;
    TextView fullnameUserData, yearsUserData, phoneUserData, emailUserData, addressUserData, ageUserData, birthDateUserData,
            religionUserData, genderUserData, bloodTypeUserData, statusUserData;
    LinearLayout aboutAplicationDetails, signOutApp;
    Dialog aboutApplicationDialog, signOutDialog;
    ImageView editButtonData;
    CircleImageView profilePhotoData;

    String GUID;
    private RequestQueue mRequestQueue;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile, container, false);

        modelUser = (ModelUser) GsonHelper.parseGson(App.getPref().getString(Prefs.PREF_STORE_PROFILE, ""), new ModelUser());

        mRequestQueue = Volley.newRequestQueue(getActivity());

        fullnameUserData = v.findViewById(R.id.fullnameUser);
        yearsUserData = v.findViewById(R.id.yearsUser);
        phoneUserData = v.findViewById(R.id.phoneNumberUser);
        emailUserData = v.findViewById(R.id.emailUser);
        addressUserData = v.findViewById(R.id.addressUser);
        ageUserData = v.findViewById(R.id.ageUser);
        bloodTypeUserData = v.findViewById(R.id.bloodTypeUser);
        statusUserData = v.findViewById(R.id.statusUser);
        birthDateUserData = v.findViewById(R.id.birthDateUser);
        religionUserData = v.findViewById(R.id.religionUser);
        genderUserData = v.findViewById(R.id.genderUser);
        profilePhotoData = v.findViewById(R.id.profilePhotoUser);

        aboutAplicationDetails = v.findViewById(R.id.aboutApplication);
        signOutApp = v.findViewById(R.id.signOut);

        editButtonData = v.findViewById(R.id.editProfileButton);

        fullnameUserData.setText(modelUser.getFULLNAME());
        GUID = modelUser.getGUID();
        phoneUserData.setText(modelUser.getPHONE());
        emailUserData.setText(modelUser.getEMAIL());
        addressUserData.setText(modelUser.getADDRESS());
        ageUserData.setText(modelUser.getAGE());
        birthDateUserData.setText(modelUser.getBIRTHDATE());
        religionUserData.setText(modelUser.getRELIGION());
        genderUserData.setText(modelUser.getGENDER());
        bloodTypeUserData.setText(modelUser.getBLOOD_TYPE());
        yearsUserData.setText(Utils.convertMongoYears(modelUser.getCREATED_AT()));

        if (modelUser.getPHOTO().equals("-")) {
            profilePhotoData.setImageResource(R.drawable.default_user);
        } else if (modelUser.getPHOTO().equals(null)) {
            profilePhotoData.setImageResource(R.drawable.default_user);
        } else {
            Picasso.get().load(BaseURL.baseUrl + "images/" + modelUser.getPHOTO()).into(profilePhotoData);
        }

        if (modelUser.getSTATUS().equals("tidak")) {
            statusUserData.setText("Belum Tergabung");
        } else {
            statusUserData.setText("Tergabung");
        }

        aboutApplicationDialog = new Dialog(getActivity(), R.style.dialogStyle);
        signOutDialog = new Dialog(getActivity(), R.style.dialogStyle);

        aboutApplicationDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        signOutDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        aboutApplicationDialog.setCancelable(false);
        signOutDialog.setCancelable(false);

        aboutApplicationDialog.setContentView(R.layout.dialog_about_application);
        signOutDialog.setContentView(R.layout.dialog_signout);

        ImageView closeDialog = (ImageView) aboutApplicationDialog.findViewById(R.id.closeDialog);
        Button closeDialogSignOut = (Button) signOutDialog.findViewById(R.id.closeDiaglogSignOut);
        Button signOutAppNow = (Button) signOutDialog.findViewById(R.id.signOutNow);

        aboutAplicationDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                aboutApplicationDialog.show();
            }
        });

        closeDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                aboutApplicationDialog.dismiss();
            }
        });

        signOutApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOutDialog.show();
            }
        });

        closeDialogSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOutDialog.dismiss();
            }
        });

        signOutAppNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                App.getPref().clear();
                startActivity(new Intent(getActivity(), SignIn.class));
                Animatoo.animateSlideUp(getActivity());
            }
        });

        editButtonData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), ProfileEdit.class));
                Animatoo.animateSlideLeft(getActivity());
            }
        });

        return v;
    }
}