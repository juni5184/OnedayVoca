package com.example.onedayvoca;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

public class TestMenuActivity extends AppCompatActivity {

    Button btnTest1, btnTest2, btnTest3, btnTest4;
    Intent intent;
    String user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_menu);

        init();

    }

    public void init(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("테스트 메뉴");
        toolbar.setTitleTextColor(Color.WHITE);

        user_id= getIntent().getStringExtra("user_id");

        btnTest1= (Button)findViewById(R.id.btnTest1);
        btnTest2= (Button)findViewById(R.id.btnTest2);
        btnTest3= (Button)findViewById(R.id.btnTest3);
        btnTest4= (Button)findViewById(R.id.btnTest4);

        btnTest1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent= new Intent(TestMenuActivity.this, TestActivity.class);
                intent.putExtra("user_id", user_id+"");
                startActivity(intent);
            }
        });
        btnTest2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent= new Intent(TestMenuActivity.this, TestSpellActivity.class);
                intent.putExtra("user_id", user_id+"");
                startActivity(intent);
            }
        });
        btnTest3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent= new Intent(TestMenuActivity.this, TestSpeechActivity.class);
                intent.putExtra("user_id", user_id+"");
                startActivity(intent);
            }
        });
        btnTest4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent= new Intent(TestMenuActivity.this, TestMineActivity.class);
                intent.putExtra("user_id", user_id+"");
                startActivity(intent);
            }
        });

    }
}
