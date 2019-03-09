package com.example.onedayvoca;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class GameActivity extends AppCompatActivity {

    Button btn2D, btn3D;
    TextView txt2D, txt3D;

    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        init();

    }

    public void init(){

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar1);
        setSupportActionBar(toolbar);
        setTitle("GAME");
        toolbar.setTitleTextColor(Color.WHITE);

        btn2D= (Button)findViewById(R.id.btn2D);
        btn3D= (Button)findViewById(R.id.btn3D);
        txt2D= (TextView)findViewById(R.id.txt2D);
        txt3D= (TextView)findViewById(R.id.txt3D);


        //unity assets 파일 중복되는 부분 때문에
        //외부 앱 불러오는 형식으로 pacman 연결함
        btn2D.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = getPackageManager().getLaunchIntentForPackage("com.example.pacman");
                startActivity(intent);
            }
        });
        txt2D.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = getPackageManager().getLaunchIntentForPackage("com.example.pacman");
                startActivity(intent);
            }
        });

        btn3D.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent= new Intent(GameActivity.this, UnityPlayerActivity.class);
                startActivity(intent);
            }
        });
        txt3D.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent= new Intent(GameActivity.this, UnityPlayerActivity.class);
                startActivity(intent);
            }
        });


    }
}
