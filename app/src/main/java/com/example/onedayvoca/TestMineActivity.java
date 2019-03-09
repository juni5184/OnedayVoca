package com.example.onedayvoca;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;

public class TestMineActivity extends AppCompatActivity {

    private TextView txtMyWord, txtCnt;
    private String day, id;
    private int a,b,c,d;
    private int idx[] = new int[4];
    private Button btn[] = new Button[4];
    private String bt;

    private int bound= 10; //random불러오는 단어 갯수

    private static final String TAG= "TestMineActivity";

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
        setContentView(R.layout.activity_test_mine);

        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar1);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("맞춤형 테스트");
        toolbar.setTitleTextColor(Color.WHITE);

        txtMyWord=(TextView)findViewById(R.id.txtMyWord);

        btn[0]=(Button)findViewById(R.id.mykor1);
        btn[1]=(Button)findViewById(R.id.mykor2);
        btn[2]=(Button)findViewById(R.id.mykor3);
        btn[3]=(Button)findViewById(R.id.mykor4);

        user_id= getIntent().getStringExtra("user_id");
        Log.i("test_userId", user_id+"");

        pbTimer= (ProgressBar)findViewById(R.id.pbTimer);
        txtCnt= (TextView)findViewById(R.id.txtCnt);

        getData(Common.Server+"myword.php");
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
        if(next < 11){
            switch (v.getId()) {
                case R.id.mykor1:
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
                    txtCnt.setText((next)+"/"+10);
                    break;
                case R.id.mykor2:
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
                    txtCnt.setText((next)+"/"+10);
                    break;
                case R.id.mykor3:
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
                    txtCnt.setText((next)+"/"+10);
                    break;
                case R.id.mykor4:
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
                    txtCnt.setText((next)+"/"+10);
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
            intent.putExtra("user_id",user_id);
            intent.putExtra("type",4);

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
                    //시간이 다 되면 다음 단어로 넘어가기는하는데
                    //myword에 추가가 안된다
                    //thread에 추가하는 부분을 넣었더니 나중에나 돌면서 여러번 더 들어가는듯
//                    new insertMyWord().execute(Common.Server+"myword_insert.php?user_id='"+user_id+"'&eng='"+eng.get(idx[0])+"'&kor='"+kor.get(idx[0])+"'");
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
                    txtMyWord.setText(eng.get(idx[0]));
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
                JSONObject jsonObj = null;
                try {
                    jsonObj = new JSONObject(myJSON);
                    jsonArray = jsonObj.getJSONArray("result");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //갯수 너무 적으면
                Log.i("myword jsonarray", jsonArray.length()+"");
                if(jsonArray.length()<10){
                    AlertDialog.Builder builder = new AlertDialog.Builder(TestMineActivity.this);
                    builder.setTitle("맞춤형 테스트");
                    builder.setMessage("내 단어장의 단어갯수가 너무 적습니다.");
                    builder.setPositiveButton("예",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast.makeText(getApplicationContext(),"테스트를 좀 더 진행하세요.",Toast.LENGTH_LONG).show();
                                    finish();
                                }
                            });
                    builder.show();
                }else{
                    showList();
                }

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
                    txtMyWord.setText(eng.get(idx[0]));
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

                    Log.i("next","next:"+next);
                }
            });

        }
    }

}
