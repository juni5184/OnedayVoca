package com.example.onedayvoca;

import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.example.onedayvoca.ScoreFragment.Score1Fragment;
import com.example.onedayvoca.ScoreFragment.Score2Fragment;
import com.example.onedayvoca.ScoreFragment.Score3Fragment;
import com.example.onedayvoca.ScoreFragment.Score4Fragment;


public class ScoreActivity extends AppCompatActivity {

    ViewPager pager;
//    Button btn_first, btn_second, btn_third, btn_fourth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);

        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar1);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("성적표");
        toolbar.setTitleTextColor(Color.WHITE);

        pager = (ViewPager)findViewById(R.id.scorePager);
//        btn_first = (Button)findViewById(R.id.btn_first);
//        btn_second = (Button)findViewById(R.id.btn_second);
//        btn_third = (Button)findViewById(R.id.btn_third);
//        btn_fourth = (Button)findViewById(R.id.btn_fourth);

        pager.setAdapter(new pagerAdapter(getSupportFragmentManager()));
        pager.setCurrentItem(0);

        View.OnClickListener movePageListener = new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                int tag = (int)view.getTag();
                pager.setCurrentItem(tag);
                Log.i("pager tag", tag+"");
            }
        };

//        btn_first.setOnClickListener(movePageListener);
//        btn_first.setTag(0);
//        btn_second.setOnClickListener(movePageListener);
//        btn_second.setTag(1);
//        btn_third.setOnClickListener(movePageListener);
//        btn_third.setTag(2);
//        btn_fourth.setOnClickListener(movePageListener);
//        btn_fourth.setTag(3);
    }

    private class pagerAdapter extends FragmentStatePagerAdapter{
        public pagerAdapter(FragmentManager fm ){
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch(position){
                case 0:
                    Log.e("fragment1","fragment1");
                    Score1Fragment fragment1= new Score1Fragment();
                    return fragment1;
                case 1:
                    Log.e("fragment2","fragment2");
                    Score2Fragment fragment2= new Score2Fragment();
                    return fragment2;
                case 2:
                    Log.e("fragment3","fragment3");
                    Score3Fragment fragment3= new Score3Fragment();
                    return fragment3;
                case 3:
                    Log.e("fragment4","fragment4");
                    Score4Fragment fragment4= new Score4Fragment();
                    return fragment4;
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            // total page count
            return 4;
        }
    }
}
