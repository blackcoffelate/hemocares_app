package com.example.hemocares.view.dashboard.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hemocares.R;
import com.example.hemocares.model.ModelUserShow;
import com.example.hemocares.service.BaseURL;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdapterUserJoin extends RecyclerView.Adapter<AdapterUserJoin.ViewHolder> {

    private Context context;
    private List<ModelUserShow> listUser;

    public AdapterUserJoin(Context context, List<ModelUserShow> listUser) {
        this.context = context;
        this.listUser = listUser;
    }

    @NonNull
    @Override
    public AdapterUserJoin.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.card_user_dashboard, null);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterUserJoin.ViewHolder holder, int position) {
        final ModelUserShow userModel = listUser.get(position);

        if (userModel.getPHOTO().equals("-")){
            holder.photoProfileUserData.setImageResource(R.drawable.banner_blank);
        } else {
            Picasso.get().load(BaseURL.baseUrl + "images/" + userModel.getPHOTO()).resize(500, 400).centerCrop().into(holder.photoProfileUserData);
        }

        holder.bloodTypeData.setText(userModel.getBLOOD_TYPE());
        holder.fullnameUserData.setText(userModel.getFULLNAME());
        holder.ageUserData.setText(userModel.getAGE());
        holder.phoneUserData.setText(userModel.getPHONE());
        holder.textDeskripsi = userModel.getADDRESS();

        if (holder.textDeskripsi.length() > 30) {
            holder.textDeskripsi = holder.textDeskripsi.substring(0, 25) + "...";
            holder.addressUserData.setText(holder.textDeskripsi);
        } else {
            holder.addressUserData.setText(userModel.getADDRESS());
        }

    }

    @Override
    public int getItemCount() {
        return listUser.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView bloodTypeData, fullnameUserData, ageUserData, phoneUserData, addressUserData;
        ImageView photoProfileUserData;

        String textDeskripsi;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            bloodTypeData = itemView.findViewById(R.id.bloodTypeUserShow);
            fullnameUserData = itemView.findViewById(R.id.fullnameUserShow);
            ageUserData = itemView.findViewById(R.id.ageUserShow);
            phoneUserData = itemView.findViewById(R.id.phoneUserShow);
            addressUserData = itemView.findViewById(R.id.addressUserShow);
            photoProfileUserData = itemView.findViewById(R.id.photoProfileUserShow);

        }
    }
}
