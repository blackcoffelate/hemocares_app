package com.example.hemocares.view.dashboard.assets;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.hemocares.R;
import com.smarteist.autoimageslider.SliderViewAdapter;

import java.util.List;

public class AdapterBannerSlider extends SliderViewAdapter<AdapterBannerSlider.MyViewHolder> {

    List<Integer> imageList;

    public AdapterBannerSlider(List<Integer> list) {
        this.imageList = list;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.banner_slider_item, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MyViewHolder viewHolder, int position) {
        viewHolder.imageBannerItem.setImageResource(imageList.get(position));
    }

    @Override
    public int getCount() {
        return imageList.size();
    }

    class MyViewHolder extends SliderViewAdapter.ViewHolder {

        ImageView imageBannerItem;

        public MyViewHolder(View itemView) {
            super(itemView);
            imageBannerItem = itemView.findViewById(R.id.bannerImage);
        }
    }

}
