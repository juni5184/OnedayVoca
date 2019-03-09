package com.example.onedayvoca;

import android.content.Intent;
import android.os.AsyncTask;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.locks.Lock;

public class LockActivity extends AppCompatActivity implements TextToSpeech.OnInitListener{

    private TextView txtEng, txtKor;
    private ImageView btnVol;
    private String myJSON;
    private ArrayList<String> eng= new ArrayList<String>();
    private ArrayList<String> kor= new ArrayList<String>();
    private TextToSpeech tts;
    private Random random;
    private int num;
    private Button btnKnow, btnDontKnow;
    private String user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock);

        init();

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                WindowManager.LayoutParams.FLAG_FULLSCREEN |
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        Intent intent= new Intent(LockActivity.this, OnLock_Service.class);
        startService(intent);

//        Toast.makeText(LockActivity.this, "APK install Success.. ", Toast.LENGTH_SHORT).show();
//        finish();
    }

    public void init(){
        txtEng= (TextView)findViewById(R.id.txtEng);
        txtKor= (TextView)findViewById(R.id.txtKor);
        btnVol= (ImageView)findViewById(R.id.btnVol);

        btnKnow= (Button)findViewById(R.id.btnKnow);
        btnDontKnow= (Button)findViewById(R.id.btnDontKnow);

        getData(Common.Server+"study.php");
        tts = new TextToSpeech(LockActivity.this, this);

        btnVol.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speakOut(eng.get(num));
            }
        });

        //아는단어 체크 버튼
        btnKnow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Toast.makeText(getApplicationContext(),"아는단어 입니다.",Toast.LENGTH_SHORT).show();

            }
        });
        //모르는 단어 체크 버튼
        btnDontKnow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user_id = getApplicationContext().getSharedPreferences("login",MODE_PRIVATE).getString("user_id","");
                new insertMyWord().execute(Common.Server+"myword_insert.php?user_id='"+user_id+"'&eng='"+eng.get(num)+"'&kor='"+kor.get(num)+"'");
                Toast.makeText(getApplicationContext(),"내 단어장에 추가되었습니다.",Toast.LENGTH_SHORT).show();
                finish();
            }
        });
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


    //테스트 한 페이지 마다 랜덤 출력
    protected void showList() {
        try {
            JSONObject jsonObj = new JSONObject(myJSON);
            JSONArray jsonArray = jsonObj.getJSONArray("result");
            Log.i("jsonArrayTest", "jArray:" + jsonArray);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject row = jsonArray.getJSONObject(i);
                eng.add(i, row.getString("eng"));
                kor.add(i, row.getString("kor"));
            }
            Log.i("kor","size:"+kor.size());
            random = new Random();
            num= random.nextInt(kor.size());
            Log.i("num",num+"");

            Log.i("Random",random+"");

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    txtEng.setText(eng.get(num));
                    txtKor.setText(kor.get(num));
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
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
                Log.d("LockActivity", "InsertData: Error ", e);

                return null;
            }

        }
    }

    //단어 뜻 읽어주기 TTS
    private void speakOut(String text) {
        Log.e("textspeak", text+"");
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null,text+"");
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            // 작업 성공
            int language = tts.setLanguage(Locale.ENGLISH);  // 언어 설정
            if (language == TextToSpeech.LANG_MISSING_DATA || language == TextToSpeech.LANG_NOT_SUPPORTED) {
                // 언어 데이터가 없거나, 지원하지 않는경우
                btnVol.setEnabled(false);
            } else {
                // 준비 완료
                btnVol.setEnabled(true);
            }
        } else {
            Log.i("onInit","작업 실패");
        }
    }
}
