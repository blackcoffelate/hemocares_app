package com.example.hemocares.view.profile.adapter;

import android.content.Context;
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
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdapterUserReportAllDetail extends RecyclerView.Adapter<AdapterUserReportAllDetail.ViewHolder> {

    private Context context;
    private List<ModelUserReportAll> listReport;
    Boolean clicked = true;
    int likeData = 0;

    public AdapterUserReportAllDetail(Context context, List<ModelUserReportAll> listReport) {
        this.context = context;
        this.listReport = listReport;
    }

    @NonNull
    @Override
    public AdapterUserReportAllDetail.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.card_user_report_detail, null);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterUserReportAllDetail.ViewHolder holder, int position) {
        final ModelUserReportAll reportModel = listReport.get(position);

        if (reportModel.getPHOTO_CONTENT().equals("-")) {
            holder.photoContentReportData.setImageResource(R.drawable.default_user);
        } else {
            Picasso.get().load(BaseURL.baseUrl + "images/" + reportModel.getPHOTO_CONTENT()).resize(500, 400).centerCrop().into(holder.photoContentReportData);
        }

        holder.bloodTypeData.setText(reportModel.getBLOOD_TYPE());
        holder.fullnameUserData.setText(reportModel.getFULLNAME());
        holder.contentUserData.setText(reportModel.getCONTENT());
        holder.textDeskripsi = reportModel.getCONTENT();
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
        String textDeskripsi;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            bloodTypeData = itemView.findViewById(R.id.bloodTypeUserReportDetail);
            countLikeData = itemView.findViewById(R.id.countLikeDetail);
            likeReportData = itemView.findViewById(R.id.likeReportDetail);
            typeReportData = itemView.findViewById(R.id.typeUserReportDetail);
            fullnameUserData = itemView.findViewById(R.id.fullnameUserReportDetail);
            contentUserData = itemView.findViewById(R.id.contentUserReportDetail);
            addressUserData = itemView.findViewById(R.id.addressUserReportDetail);
            datePostData = itemView.findViewById(R.id.postDateReportDetail);
            photoContentReportData = itemView.findViewById(R.id.photoContentReportDetail);
        }
    }
}
