package com.example.onedayvoca;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Telephony;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

//import org.apache.http.HttpResponse;
//import org.apache.http.NameValuePair;
//import org.apache.http.client.HttpClient;
//import org.apache.http.client.entity.UrlEncodedFormEntity;
//import org.apache.http.client.methods.HttpPost;
//import org.apache.http.impl.client.DefaultHttpClient;
//import org.apache.http.message.BasicNameValuePair;
//import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import junit.framework.Test;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class TestActivity extends AppCompatActivity {

    private TextView txtWord, txtCnt;
    private int a,b,c,d;
    private int idx[] = new int[4];
    private Button btn[] = new Button[4];
    private String bt;

    private static final String TAG= "TestActivity";

    private ArrayList<String> eng= new ArrayList<String>();
    private ArrayList<String> kor= new ArrayList<String>();
    private ArrayList<String> wrongEng = new ArrayList<>();
    private ArrayList<String> wrongKor = new ArrayList<>();

    private String myJSON;
    private String user_id;
    private int next=1;
    private int cnt=0;
    private ArrayList<Integer> temp = new ArrayList<>();

    private JSONArray jsonArray;

    private ProgressBar pbTimer;
    private int time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar1);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("기본 테스트");
        toolbar.setTitleTextColor(Color.WHITE);

        txtWord=(TextView)findViewById(R.id.txtWord);
        btn[0]=(Button)findViewById(R.id.kor1);
        btn[1]=(Button)findViewById(R.id.kor2);
        btn[2]=(Button)findViewById(R.id.kor3);
        btn[3]=(Button)findViewById(R.id.kor4);

        user_id= getIntent().getStringExtra("user_id");
        Log.i("test_userId", user_id+"");

        pbTimer= (ProgressBar)findViewById(R.id.pbTimer);
        txtCnt= (TextView)findViewById(R.id.txtCnt);

        getData(Common.Server+"study.php");
        txtCnt.setText((next)+"/"+10);

    }


    public void onClick(View v){
        int num[] = new int[4];
        Intent intent = null;
        for (int i = 0; i < 4; i++) {
            bt = btn[i].getText().toString();
            int idx = bt.indexOf(".");
            bt = bt.substring(idx + 1);
             num[i] = kor.indexOf(bt);
        }
        //10개 문제
        //제한
        if(next < 10){
            switch (v.getId()) {
                case R.id.kor1:
                    if ( num[0] == idx[0]) {
                        btn[0].setBackgroundResource(R.color.colorPrimary);
                    } else {
                        btn[0].setBackgroundResource(R.color.colorAccent);
                        wrongEng.add(eng.get(idx[0]));
                        wrongKor.add(kor.get(idx[0]));
                        cnt++;
                    }
                    Set set = new Set();
                    set.start();
                    break;
                case R.id.kor2:
                    if ( num[1] == idx[0]) {
                        btn[1].setBackgroundResource(R.color.colorPrimary);
                    } else {
                        btn[1].setBackgroundResource(R.color.colorAccent);
                        wrongEng.add(eng.get(idx[0]));
                        wrongKor.add(kor.get(idx[0]));
                        cnt++;
                    }
                    Set set1 = new Set();
                    set1.start();
                    break;
                case R.id.kor3:
                    if ( num[2] == idx[0]) {
                        btn[2].setBackgroundResource(R.color.colorPrimary);
                    } else {
                        btn[2].setBackgroundResource(R.color.colorAccent);
                        wrongEng.add(eng.get(idx[0]));
                        wrongKor.add(kor.get(idx[0]));
                        cnt++;
                    }
                    Set set2 = new Set();
                    set2.start();
                    break;
                case R.id.kor4:
                    if ( num[3] == idx[0]) {
                        btn[3].setBackgroundResource(R.color.colorPrimary);
                    } else {
                        btn[3].setBackgroundResource(R.color.colorAccent);
                        wrongEng.add(eng.get(idx[0]));
                        wrongKor.add(kor.get(idx[0]));
                        cnt++;
                    }
                    Set set3 = new Set();
                    set3.start();
                    break;
            }

        }else{
            Toast.makeText(getApplicationContext()," 10개 끝", Toast.LENGTH_SHORT).show();
            //단어 테스트 끝나면
            intent=new Intent(this,TestResultActivity.class);
            intent.putExtra("cnt",cnt);
            intent.putExtra("next",next);
            intent.putExtra("wrongEng",wrongEng);
            intent.putExtra("wrongKor",wrongKor);
            intent.putExtra("type",1);

            intent.putExtra("user_id",user_id);

            finish();
            startActivity(intent);
        }

    }


    //5초 타이머
    private void ThreadStart(){
        new Thread(){
            @Override
            public void run() {
                TimerProcess();
            }
        }.start();
    }
    private void TimerProcess(){
        for(int i=0; i<1000; i++){
            try {
                //5초 세기
                Thread.sleep(50);
                pbTimer.setProgress(time);
                time++;
                //시간이 다 되면 넘어감
                if(time == pbTimer.getMax()){
                    cnt++;
                    //시간이 다 되면 다음 단어로 넘어가기는하는데
                    //myword에 추가가 안된다
                    //thread에 추가하는 부분을 넣었더니 나중에나 돌면서 여러번 더 들어가는듯
                    wrongEng.add(eng.get(idx[0]));
                    wrongKor.add(kor.get(idx[0]));
                    Set set= new Set();
                    set.start();
                    txtCnt.setText((next)+"/"+10);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    //테스트 한 페이지 마다 랜덤 출력
    protected void showList() {
        try {
            //5초 시간제한
            ThreadStart();

            JSONObject jsonObj = new JSONObject(myJSON);
            jsonArray = jsonObj.getJSONArray("result");
            Log.i("jsonArrayTest", "jArray:" + jsonArray);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject row = jsonArray.getJSONObject(i);
                eng.add(i, row.getString("eng"));
                kor.add(i, row.getString("kor"));
            }
            Log.i("kor","size:"+kor.size());
            Random random = new Random();
            while (true) {
                for (int i = 0; i < 4; i++) {
                    idx[i] = random.nextInt( jsonArray.length());
                }
                if (idx[0] != idx[1] && idx[0] != idx[2] && idx[0] != idx[3] && idx[1] != idx[2] && idx[1] != idx[3] && idx[2] != idx[3])
                    break;
            }
            temp.add(idx[0]);
            Log.i("num", idx[0] + "," + idx[1] + "," + idx[2] + "," + idx[3]);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    txtWord.setText(eng.get(idx[0]));
                    Random random1 = new Random();
                    while (true) {
                        a = random1.nextInt(btn.length);
                        b = random1.nextInt(btn.length);
                        c = random1.nextInt(btn.length);
                        d = random1.nextInt(btn.length);
                        if (a != b && a != c && a != d && b != c && b != d && c != d)
                            break;
                    }
                    btn[a].setText(kor.get(idx[0]));
                    btn[b].setText(kor.get(idx[1]));
                    btn[c].setText(kor.get(idx[2]));
                    btn[d].setText(kor.get(idx[3]));
                    for (int i = 0; i < 4; i++) {
                        btn[i].setBackgroundResource(R.color.white);
                    }
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



    //DB에서 데이터 가져옴
    public void getData(String url) {
        class GetDataJSON extends AsyncTask<String, Void, String> {

            @Override
            protected void onPostExecute(String result) {
                Log.i("result",result+"");
                myJSON = result;
                showList();
            }
            @Override
            protected String doInBackground(String... params) {
                String uri = params[0];
                BufferedReader bufferedReader = null;
                try {
                    URL url = new URL(uri);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    StringBuilder sb = new StringBuilder();

                    bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));

                    String json;
                    while ((json = bufferedReader.readLine()) != null) {
                        sb.append(json + "\n");
                    }
                    return sb.toString().trim();
                } catch (Exception e) {
                    return null;
                }
            }
        }
        GetDataJSON g = new GetDataJSON();
        g.execute(url);
    }

    //테스트 세팅
    public class Set extends Thread{
        public void run(){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Random random = new Random();
            while (true) {
                for (int i = 0; i < 4; i++) {
                    idx[i] = random.nextInt( jsonArray.length());
                    for(int j =0;j<temp.size();j++){
                        if(temp.get(j)==idx[0]){
                            idx[0]=random.nextInt( 10);
                        }
                    }
                }
                if (idx[0] != idx[1] && idx[0] != idx[2] && idx[0] != idx[3] && idx[1] != idx[2] && idx[1] != idx[3] && idx[2] != idx[3])
                    break;
            }
            temp.add(idx[0]);
            Log.i("temp",""+temp);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    txtWord.setText(eng.get(idx[0]));
                    Random random1 = new Random();
                    while (true) {
                        a = random1.nextInt(btn.length);
                        b = random1.nextInt(btn.length);
                        c = random1.nextInt(btn.length);
                        d = random1.nextInt(btn.length);
                        if (a != b && a != c && a != d && b != c && b != d && c != d)
                            break;
                    }
                    btn[a].setText(kor.get(idx[0]));
                    btn[b].setText(kor.get(idx[1]));
                    btn[c].setText(kor.get(idx[2]));
                    btn[d].setText(kor.get(idx[3]));
                    for (int i = 0; i < 4; i++) {
                        btn[i].setBackgroundResource(R.color.white);
                    }
                    next++;

                    time=0;

                    txtCnt.setText((next)+"/"+10);
                    Log.i("next","next:"+next);
                }
            });

        }
    }
}
