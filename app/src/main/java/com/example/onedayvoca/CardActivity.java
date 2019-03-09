package com.example.onedayvoca;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Button;

import com.example.onedayvoca.CardViewPager.CardItem;
import com.example.onedayvoca.CardViewPager.CardPagerAdapter;
import com.example.onedayvoca.CardViewPager.ShadowTransformer;

import java.util.ArrayList;
import java.util.HashMap;

public class CardActivity extends AppCompatActivity{

    private ViewPager mViewPager;

    private CardPagerAdapter mCardAdapter;
    private ShadowTransformer mCardShadowTransformer;

    private int num;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card);
        mViewPager = (ViewPager) findViewById(R.id.viewPager);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("단어장");
        toolbar.setTitleTextColor(Color.WHITE);


        Intent intent = getIntent();
        num= getIntent().getIntExtra("num",0);
        ArrayList<HashMap<String, String>> hashMap = (ArrayList<HashMap<String, String>>) intent.getSerializableExtra("hashMap");
        Log.i("hashmap",hashMap+"");


        mCardAdapter = new CardPagerAdapter(CardActivity.this,mViewPager);

        //hashmap 크기만큼 카드 추가
        for(int i=0; i<hashMap.size();i++) {
            mCardAdapter.addCardItem(new CardItem(hashMap.get(i).get("eng")+"",hashMap.get(i).get("kor")+""));
        }

        mCardShadowTransformer = new ShadowTransformer(mViewPager, mCardAdapter);
        mCardShadowTransformer.enableScaling(true);

        mViewPager.setAdapter(mCardAdapter);
        mViewPager.setPageTransformer(true, mCardShadowTransformer);
        mViewPager.setOffscreenPageLimit(0);
        mViewPager.setCurrentItem(num);

    }

}
