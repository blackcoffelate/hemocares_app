package com.example.hemocares.view.dailyreport.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hemocares.R;
import com.example.hemocares.model.ModelUserReportAll;
import com.example.hemocares.service.BaseURL;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterUserReportAll extends RecyclerView.Adapter<AdapterUserReportAll.ViewHolder> {

    private Context context;
    private List<ModelUserReportAll> listReport;

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

        if (reportModel.getPHOTO().equals("-")){
            holder.photoProfileUserReport.setImageResource(R.drawable.banner_blank);
        } else {
            Picasso.get().load(BaseURL.baseUrl + "images/" + reportModel.getPHOTO()).resize(500, 400).centerCrop().into(holder.photoProfileUserReport);
        }

        if (reportModel.getPHOTO_CONTENT().equals("-")) {
            holder.photoContentReportData.setImageResource(R.drawable.banner_blank);
        } else {
            Picasso.get().load(BaseURL.baseUrl + "images/" + reportModel.getPHOTO_CONTENT()).resize(500, 400).centerCrop().into(holder.photoContentReportData);
        }

        holder.bloodTypeData.setText(reportModel.getBLOOD_TYPE());
        holder.fullnameUserData.setText(reportModel.getFULLNAME());
        holder.contentUserData.setText(reportModel.getCONTENT());
        holder.datePostData.setText(reportModel.getCREATED_AT());
        holder.addressUserData.setText(reportModel.getADDRESS());
        holder.typeReportData.setText(reportModel.getREPORT_TYPE());
    }

    @Override
    public int getItemCount() {
        return listReport.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView bloodTypeData, fullnameUserData, contentUserData, addressUserData, datePostData, typeReportData;
        ImageView photoContentReportData;
        CircleImageView photoProfileUserReport;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            photoProfileUserReport = itemView.findViewById(R.id.photoProfileUserReport);
            bloodTypeData = itemView.findViewById(R.id.bloodTypeUserReport);
            typeReportData = itemView.findViewById(R.id.typeUserReport);
            fullnameUserData = itemView.findViewById(R.id.fullnameUserReport);
            contentUserData = itemView.findViewById(R.id.contentUserReport);
            addressUserData = itemView.findViewById(R.id.addressUserReport);
            datePostData = itemView.findViewById(R.id.postDateReport);
            photoContentReportData = itemView.findViewById(R.id.photoContentReport);
        }
    }
}
