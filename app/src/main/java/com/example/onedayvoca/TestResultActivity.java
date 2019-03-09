package com.example.onedayvoca;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.onedayvoca.MyWord.MyWordAdapter;
import com.example.onedayvoca.MyWord.MyWordListItem;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class TestResultActivity extends AppCompatActivity {

    private int cnt, total;
    private TextView txtCnt;
    private Button btnMenu;
    private ListView lvWrong;

    private String user_id;
    private int type; //테스트 타입 , 기본1 스펠링2 발음 3 맞춤단어4

    private MyWordAdapter adapter;
    private ArrayList<MyWordListItem> itemList = new ArrayList<MyWordListItem>() ;

    ArrayList<String> wrongEng = new ArrayList<>();
    ArrayList<String> wrongKor = new ArrayList<>();

    private static final String TAG="TestResultActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_result);

        init();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("테스트 결과");

        adapter = new MyWordAdapter(itemList,getApplicationContext());

        //hashmap 크기만큼 카드 추가
        for(int i=0; i<wrongEng.size();i++) {
            adapter.addItem(wrongEng.get(i).toString(),wrongKor.get(i).toString());
            new insertMyWord().execute(Common.Server+"myword_insert.php?user_id='"+user_id+"'&eng='"+wrongEng.get(i)+"'&kor='"+wrongKor.get(i)+"'");
        }
        //score table 에 성적 입력
        new insertMyWord().execute(Common.Server+"score_insert.php?user_id='"+user_id+"'&score="+((10-cnt)*10)+"&type="+type);

        lvWrong.setAdapter(adapter);

        txtCnt.setText(cnt+"");
        btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(TestResultActivity.this, MenuActivity.class);
                startActivity(intent);
            }
        });
    }


    private class insertMyWord extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
        }
        @Override
        protected String doInBackground(String... params) {
            String serverURL = params[0];
            Log.i("serverURL",serverURL+"");
            try {
                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.connect();

                int responseStatusCode = httpURLConnection.getResponseCode();

                InputStream inputStream;
                if(responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                }
                else{
                    inputStream = httpURLConnection.getErrorStream();
                }

                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line;

                while((line = bufferedReader.readLine()) != null){
                    sb.append(line);
                }
                bufferedReader.close();
                return sb.toString().trim();

            } catch (Exception e) {
                Log.d(TAG, "InsertData: Error ", e);

                return null;
            }

        }
    }

    public void init(){
        txtCnt= (TextView)findViewById(R.id.txtCnt);
        btnMenu= (Button)findViewById(R.id.btnMenu);
        lvWrong= (ListView)findViewById(R.id.lvWrong);

        user_id= getIntent().getStringExtra("user_id");
        type= getIntent().getIntExtra("type",0);

        wrongEng= (ArrayList<String>)getIntent().getSerializableExtra("wrongEng");
        wrongKor= (ArrayList<String>)getIntent().getSerializableExtra("wrongKor");


        cnt= getIntent().getIntExtra("cnt",0);//틀린갯수
        total= getIntent().getIntExtra("next",0);//전체갯수
    }
}
