package com.example.hemocares.view.dashboard.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hemocares.view.profile.ProfileDetails;
import com.example.hemocares.R;
import com.example.hemocares.model.ModelUserShowAll;
import com.example.hemocares.service.BaseURL;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class AdapterUserJoinAll extends RecyclerView.Adapter<AdapterUserJoinAll.ViewHolder> {

    private Context context;
    private List<ModelUserShowAll> listUser;

    public AdapterUserJoinAll(Context context, List<ModelUserShowAll> listUser) {
        this.context = context;
        this.listUser = listUser;
    }

    @NonNull
    @Override
    public AdapterUserJoinAll.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.card_user_all, null);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterUserJoinAll.ViewHolder holder, int position) {
        final ModelUserShowAll userModel = listUser.get(position);

        if (userModel.getPHOTO().equals("-")){
            holder.photoProfileUserData.setImageResource(R.drawable.default_user);
        } else {
            Picasso.get().load(BaseURL.baseUrl + "images/" + userModel.getPHOTO()).resize(500, 400).centerCrop().into(holder.photoProfileUserData);
        }

        holder.bloodTypeData.setText(userModel.getBLOOD_TYPE());
        holder.fullnameUserData.setText(userModel.getFULLNAME());
        holder.ageUserData.setText(userModel.getAGE());
        holder.phoneUserData.setText(userModel.getPHONE());
        holder.religionUserData.setText(userModel.getRELIGION());
        holder.genderUserData.setText(userModel.getGENDER());
        holder.rangeData.setText(userModel.getRANGE());
        holder.textDeskripsi = userModel.getADDRESS();

        if (holder.textDeskripsi.length() > 30) {
            holder.textDeskripsi = holder.textDeskripsi.substring(0, 25) + "...";
            holder.addressUserData.setText(holder.textDeskripsi);
        } else {
            holder.addressUserData.setText(userModel.getADDRESS());
        }

        holder.detailUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, ProfileDetails.class).putExtra("GUID", userModel.getGUID()));
            }
        });
    }

    @Override
    public int getItemCount() {
        return listUser.size();
    }

    public void setFilter(ArrayList<ModelUserShowAll> modelUserAll) {
        listUser = new ArrayList<>();
        listUser.addAll(modelUserAll);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView rangeData, bloodTypeData, fullnameUserData, ageUserData, phoneUserData, addressUserData, genderUserData, religionUserData;
        ImageView photoProfileUserData;
        CardView detailUser;

        String textDeskripsi;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            bloodTypeData = itemView.findViewById(R.id.bloodTypeUserAllShow);
            fullnameUserData = itemView.findViewById(R.id.fullnameUserAllShow);
            detailUser = itemView.findViewById(R.id.detailsUserAll);
            ageUserData = itemView.findViewById(R.id.ageUserAllShow);
            phoneUserData = itemView.findViewById(R.id.phoneUserAllShow);
            addressUserData = itemView.findViewById(R.id.addressUserAllShow);
            photoProfileUserData = itemView.findViewById(R.id.photoProfileUserAllShow);
            genderUserData = itemView.findViewById(R.id.genderUserAllShow);
            religionUserData = itemView.findViewById(R.id.religionUserAllShow);
            rangeData = itemView.findViewById(R.id.rangeUserAllShow);
        }
    }
}
