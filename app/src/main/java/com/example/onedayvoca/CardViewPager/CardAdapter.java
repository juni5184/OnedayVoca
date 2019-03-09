package com.example.onedayvoca.CardViewPager;


import android.support.v7.widget.CardView;

public interface CardAdapter {

    int MAX_ELEVATION_FACTOR = 13;

    float getBaseElevation();

    CardView getCardViewAt(int position);

    int getCount();
}
