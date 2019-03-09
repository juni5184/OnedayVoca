package com.example.onedayvoca;

import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.IBinder;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.onedayvoca.Token.GenerationActivity;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;
import com.nhn.android.naverlogin.OAuthLogin;

public class MenuActivity extends AppCompatActivity implements Button.OnClickListener{

    private static OAuthLogin mOAuthLoginInstance;
    private Button btnStudy, btnAdd, btnTest, btnScore, btnChat, btnMypage, btnToken, btnGame;

    private String user_id;

    private boolean mIsBound;
    private MyService myService;

    private final ServiceConnection mConnection= new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            try {
                myService= ((MyService.MyBinder)service).getService();
                Toast.makeText(MenuActivity.this, "service connected", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Toast.makeText(MenuActivity.this, "service disconnected", Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        init();

        //채팅 서비스 실행
        Intent intent= new Intent(MenuActivity.this, MyService.class);
        intent.putExtra("user_id", user_id);
        startService(intent);

        //lock screen 서비스 실행
        Intent intent2= new Intent(MenuActivity.this, OnLock_Service.class);
        startService(intent2);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("메뉴");
        toolbar.setTitleTextColor(Color.WHITE);

    }


    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.btnStudy:
                intent= new Intent(MenuActivity.this, StudyActivity.class);
                intent.putExtra("user_id", user_id+"");
                startActivity(intent);
                break;
            case R.id.btnAdd:
                intent= new Intent(MenuActivity.this, WordAddMenuActivity.class);
                intent.putExtra("user_id", user_id+"");
                startActivity(intent);
                break;
            case R.id.btnTest:
                intent= new Intent(MenuActivity.this, TestMenuActivity.class);
                intent.putExtra("user_id", user_id+"");
                startActivity(intent);
                break;
            case R.id.btnScore:
                intent= new Intent(MenuActivity.this, ScoreActivity.class);
                intent.putExtra("user_id", user_id+"");
                startActivity(intent);
                break;
            case R.id.btnChat:
                intent= new Intent(MenuActivity.this, ChattingActivity.class);
                intent.putExtra("user_id", user_id+"");
                startActivity(intent);
                break;
            case R.id.btnMypage:
                intent= new Intent(MenuActivity.this, MyWordActivity.class);
                intent.putExtra("user_id", user_id);
                startActivity(intent);
                
                break;
            case R.id.btnToken:
                intent= new Intent(MenuActivity.this, GenerationActivity.class);
                intent.putExtra("user_id", user_id);
                startActivity(intent);
                break;
            case R.id.btnGame:
                intent= new Intent(MenuActivity.this, GameActivity.class);
                intent.putExtra("user_id", user_id);
                startActivity(intent);
                break;
        }
    }

        //로그아웃 버튼 누르면
    public void btn_logout() {
        new AlertDialog.Builder(this)
                .setTitle("로그아웃").setMessage("로그아웃 하시겠습니까?")
                .setPositiveButton("로그아웃", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        SharedPreferences setting = getSharedPreferences("setting", MODE_PRIVATE);
                        final SharedPreferences.Editor setEditor = setting.edit();
                        setEditor.putBoolean("Auto_Login_enabled", false);
                        setEditor.commit();

                        //카카오톡 로그아웃
                        UserManagement.requestLogout(new LogoutResponseCallback() {
                            @Override
                            public void onCompleteLogout() {
                            }
                        });

                        Intent i = new Intent(MenuActivity.this, LoginActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP| Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(i);
                        Toast.makeText(getApplicationContext(), "로그아웃 되었습니다.",Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                    }
                })
                .show();



    }


    //toolbar 로그아웃 메뉴
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.logout:
                btn_logout();
                break;
        }
        return true;
    }//


    public void init(){
        user_id= getIntent().getStringExtra("user_id");
        Log.i("Menu_id", user_id+"");

        btnStudy= (Button)findViewById(R.id.btnStudy);
        btnAdd= (Button)findViewById(R.id.btnAdd);
        btnTest= (Button)findViewById(R.id.btnTest);
        btnScore= (Button)findViewById(R.id.btnScore);
        btnChat= (Button)findViewById(R.id.btnChat);
        btnMypage= (Button)findViewById(R.id.btnMypage);
        btnToken= (Button)findViewById(R.id.btnToken);
        btnGame= (Button)findViewById(R.id.btnGame);

        btnStudy.setOnClickListener(this);
        btnAdd.setOnClickListener(this);
        btnTest.setOnClickListener(this);
        btnScore.setOnClickListener(this);
        btnChat.setOnClickListener(this);
        btnMypage.setOnClickListener(this);
        btnToken.setOnClickListener(this);
        btnGame.setOnClickListener(this);
    }

    @Override
    protected void onDestroy() {
        if(mIsBound){
            unbindService(mConnection);
            mIsBound= false;
        }
        super.onDestroy();
    }
}
