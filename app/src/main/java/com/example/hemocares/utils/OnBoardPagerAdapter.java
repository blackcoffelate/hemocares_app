package com.example.hemocares.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.airbnb.lottie.LottieAnimationView;
import com.example.hemocares.R;
import com.example.hemocares.utils.OnBoardItem;

import java.util.List;

public class OnBoardPagerAdapter extends PagerAdapter {

    Context _context;
    List<OnBoardItem> _onBoardItem;

    public OnBoardPagerAdapter(Context _context, List<OnBoardItem> _onBoardItem) {
        this._context = _context;
        this._onBoardItem = _onBoardItem;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater inflater = (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View onBoardScreen = inflater.inflate(R.layout.on_board_screen, null);

        LottieAnimationView lottieAnimationView = onBoardScreen.findViewById(R.id.imageOnBoard);
        TextView title = onBoardScreen.findViewById(R.id.titleOnBoard);
        TextView desc = onBoardScreen.findViewById(R.id.descOnBoard);

        lottieAnimationView.setAnimation(_onBoardItem.get(position).getOnBoardImage());
        title.setText(_onBoardItem.get(position).getTitle());
        desc.setText(_onBoardItem.get(position).getDescription());

        container.addView(onBoardScreen);

        return onBoardScreen;
    }

    @Override
    public int getCount() {
        return _onBoardItem.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
