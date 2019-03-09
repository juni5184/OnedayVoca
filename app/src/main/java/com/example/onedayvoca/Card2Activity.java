package com.example.onedayvoca;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.onedayvoca.CardViewPager.CardItem;
import com.example.onedayvoca.CardViewPager.ShadowTransformer;
import com.example.onedayvoca.MyWord.MyWordAdapter;
import com.example.onedayvoca.MyWord.MyWordListItem;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class Card2Activity extends AppCompatActivity{
    private RecyclerView mHorizontalView;
    private HorizontalAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;

    private ArrayList<HashMap<String, String>> hashMap;

    private int num;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card2);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("단어목록");
        toolbar.setTitleTextColor(Color.WHITE);

        Intent intent = getIntent();
        //현재 포커즈한 아이템
        num= getIntent().getIntExtra("num",0);
        hashMap = (ArrayList<HashMap<String, String>>) intent.getSerializableExtra("hashMap");
        Log.i("mywordMap",hashMap+"");

        // RecyclerView binding
        mHorizontalView = (RecyclerView) findViewById(R.id.recyclerView);

        // init LayoutManager
        mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL); // 기본값이 VERTICAL //지금 수평

        // setLayoutManager
        mHorizontalView.setLayoutManager(mLayoutManager);

        // init Adapter
        mAdapter = new HorizontalAdapter(getApplicationContext());

        // init Data
        ArrayList<HorizontalData> data = new ArrayList<>();

        int i = 0;
        while (i < hashMap.size()) {
            Log.i("myword data", data+"");
            data.add(new HorizontalData(hashMap.get(i).get("eng"),hashMap.get(i).get("kor")));
            i++;
        }

        // set Data
        mAdapter.setData(data);
        mAdapter.notifyDataSetChanged();

        // set Adapter
        mHorizontalView.setAdapter(mAdapter);

        //set current item focus
        mHorizontalView.smoothScrollToPosition(num);

        //recyclerview itemdecoration
        mHorizontalView.addItemDecoration(new MyItemDecoration());
    }

}

class MyItemDecoration extends RecyclerView.ItemDecoration{
    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state){
        super.getItemOffsets(outRect, view, parent, state);

        int index= parent.getChildAdapterPosition(view)+1;
        //여백
        outRect.set(40,40,0,40);
        //떠있는 느낌 , 그림자
        ViewCompat.setElevation(view, 60.0f);
    }
}


