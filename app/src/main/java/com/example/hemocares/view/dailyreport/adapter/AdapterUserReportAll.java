package com.example.hemocares.view.dailyreport.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hemocares.R;
import com.example.hemocares.model.ModelUserReportAll;
import com.example.hemocares.service.BaseURL;
import com.example.hemocares.service.Utils;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterUserReportAll extends RecyclerView.Adapter<AdapterUserReportAll.ViewHolder> {

    private Context context;
    private List<ModelUserReportAll> listReport;
    Boolean clicked = true;
    Boolean clickedContent = true;
    int stringRange = 35;
    int likeData = 0;

    public AdapterUserReportAll(Context context, List<ModelUserReportAll> listReport) {
        this.context = context;
        this.listReport = listReport;
    }

    @NonNull
    @Override
    public AdapterUserReportAll.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.card_user_report_all, null);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterUserReportAll.ViewHolder holder, int position) {
        final ModelUserReportAll reportModel = listReport.get(position);

        if (reportModel.getPHOTO().equals("-")) {
            holder.photoProfileUserReport.setImageResource(R.drawable.default_user);
        } else {
            Picasso.get().load(BaseURL.baseUrl + "images/" + reportModel.getPHOTO()).resize(500, 400).centerCrop().into(holder.photoProfileUserReport);
        }

        if (reportModel.getPHOTO_CONTENT().equals("-")) {
            holder.photoContentReportData.setImageResource(R.drawable.default_user);
        } else {
            Picasso.get().load(BaseURL.baseUrl + "images/" + reportModel.getPHOTO_CONTENT()).resize(500, 400).centerCrop().into(holder.photoContentReportData);
        }

        holder.bloodTypeData.setText(reportModel.getBLOOD_TYPE());
        holder.fullnameUserData.setText(reportModel.getFULLNAME());

        holder.textDeskripsi = reportModel.getCONTENT();
        if (holder.textDeskripsi.length() > stringRange) {
            holder.textDeskripsi = holder.textDeskripsi.substring(0, stringRange) + "...";
            holder.contentUserData.setText(holder.textDeskripsi);
        } else {
            holder.contentUserData.setText(reportModel.getCONTENT());
        }

        holder.contentUserData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickedContent) {
                    holder.contentUserData.setText(reportModel.getCONTENT());
                    clickedContent = false;
                } else {
                    if (holder.textDeskripsi.length() > stringRange) {
                        holder.textDeskripsi = holder.textDeskripsi.substring(0, 25) + "...";
                        holder.contentUserData.setText(holder.textDeskripsi);
                    }
                    clickedContent = true;
                }
            }
        });

        holder.datePostData.setText(Utils.convertMongoDateWithoutTime(reportModel.getCREATED_AT()));
        holder.addressUserData.setText(reportModel.getADDRESS());
        holder.typeReportData.setText(reportModel.getREPORT_TYPE());

        holder.likeReportData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clicked) {
                    holder.likeReportData.setColorFilter(ContextCompat.getColor(context, R.color.red));
                    likeData++;
                    holder.countLikeData.setText(String.valueOf(likeData));
                    clicked = false;
                } else {
                    holder.likeReportData.setColorFilter(ContextCompat.getColor(context, R.color.greytransparent));
                    likeData--;
                    holder.countLikeData.setText(String.valueOf(likeData));
                    clicked = true;
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return listReport.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView bloodTypeData, fullnameUserData, contentUserData, addressUserData, datePostData, typeReportData, countLikeData;
        ImageView photoContentReportData, likeReportData;
        CircleImageView photoProfileUserReport;
        String textDeskripsi;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            photoProfileUserReport = itemView.findViewById(R.id.photoProfileUserReport);
            bloodTypeData = itemView.findViewById(R.id.bloodTypeUserReport);
            countLikeData = itemView.findViewById(R.id.countLike);
            likeReportData = itemView.findViewById(R.id.likeReport);
            typeReportData = itemView.findViewById(R.id.typeUserReport);
            fullnameUserData = itemView.findViewById(R.id.fullnameUserReport);
            contentUserData = itemView.findViewById(R.id.contentUserReport);
            addressUserData = itemView.findViewById(R.id.addressUserReport);
            datePostData = itemView.findViewById(R.id.postDateReport);
            photoContentReportData = itemView.findViewById(R.id.photoContentReport);
        }
    }
}
