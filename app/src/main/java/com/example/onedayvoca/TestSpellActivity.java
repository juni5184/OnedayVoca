package com.example.onedayvoca;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
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

//시간제한 없음
//발음 들려주기
public class TestSpellActivity extends AppCompatActivity implements TextToSpeech.OnInitListener{

    private TextView txtSpell, txtCnt, txtAnswer;
    private EditText editSpell;
    private Button btnSpell, btnHint1, btnHint2;
    private ImageView ttsSpell;
    private String user_id;

    private String myJSON;
    private int next=1;
    private int cnt=0;
    private int idx[] = new int[4];
    private ArrayList<Integer> temp = new ArrayList<>();

    private JSONArray jsonArray;
    private ArrayList<String> eng= new ArrayList<String>();
    private ArrayList<String> kor= new ArrayList<String>();
    private ArrayList<String> wrongEng = new ArrayList<>();
    private ArrayList<String> wrongKor = new ArrayList<>();

    private TextToSpeech tts;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_spell);

        init();

        intent =new Intent(this,TestResultActivity.class);
        btnSpell.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(next < 10) {
                        //맞으면
                        if (editSpell.getText().toString().equals(eng.get(idx[0]))) {
                            txtAnswer.setText(eng.get(idx[0]));
                            txtAnswer.setTextColor(getColor(R.color.colorPrimaryDark));
                        } else { //틀리면
                            txtAnswer.setText(eng.get(idx[0]));
                            txtAnswer.setTextColor(getColor(R.color.colorAccent));
                            wrongEng.add(eng.get(idx[0]).toString());
                            wrongKor.add(kor.get(idx[0]).toString());
                            cnt++;
                        }
                        Set set = new Set();
                        set.start();
                        Log.i("spell answer", eng.get(idx[0]) + "");
                    }else{
                        try {
                            Thread.sleep(1000);
                            Toast.makeText(getApplicationContext()," 10개 끝", Toast.LENGTH_SHORT).show();
                            //단어 테스트 끝나면
                            intent.putExtra("cnt",cnt);
                            intent.putExtra("next",next);
                            intent.putExtra("wrongEng",wrongEng);
                            intent.putExtra("wrongKor",wrongKor);
                            intent.putExtra("user_id",user_id);
                            intent.putExtra("type",2);

                            finish();
                            startActivity(intent);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    }
                }
            });
        tts = new TextToSpeech(TestSpellActivity.this, this);
        ttsSpell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speakOut(eng.get(idx[0]).toString());
            }
        });

        //글자갯수 알려주는 힌트
        btnHint1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnHint1.setBackgroundColor(getColor(R.color.white));
                String wordCnt="";
                Log.i("hint1",eng.get(idx[0]).length()+"");
                for (int i=0; i<eng.get(idx[0]).length(); i++){
                    wordCnt=wordCnt+"ㅡ";
                }
                btnHint1.setText(wordCnt);
            }
        });

        //첫 글자 알려주는 힌트
        btnHint2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnHint2.setBackgroundColor(getColor(R.color.white));
                Log.i("hint2",eng.get(idx[0]).substring(0,1)+"");
                btnHint2.setText(eng.get(idx[0]).substring(0,1)+"");
            }
        });

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
                    txtSpell.setText(kor.get(idx[0]));
                    next++;
                    txtCnt.setText((next)+"/"+10);
                    Log.i("next","next:"+next);
                    editSpell.setText("");
                    txtAnswer.setText("");
                    btnHint1.setText("글자갯수 힌트");
                    btnHint1.setBackground(getDrawable(R.drawable.hint));
                    btnHint2.setText("첫글자 힌트");
                    btnHint2.setBackground(getDrawable(R.drawable.hint));
                }
            });

        }
    }


    //테스트 한 페이지 마다 랜덤 출력
    protected void showList() {
        try {
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
                    txtSpell.setText(kor.get(idx[0]));
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


    //단어 뜻 읽어주기 TTS
    private void speakOut(String text) {
        Log.e("textspeak", text+"");
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null,text+"");
    }

    //TTS 작업
    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            // 작업 성공
            int language = tts.setLanguage(Locale.ENGLISH);  // 언어 설정
            if (language == TextToSpeech.LANG_MISSING_DATA || language == TextToSpeech.LANG_NOT_SUPPORTED) {
                // 언어 데이터가 없거나, 지원하지 않는경우
                ttsSpell.setEnabled(false);
            } else {
                // 준비 완료
                ttsSpell.setEnabled(true);
            }
        } else {
            Log.i("onInit","작업 실패");
        }
    }


    public void init(){
        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar1);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("스펠링 테스트");
        toolbar.setTitleTextColor(Color.WHITE);

        txtSpell= (TextView)findViewById(R.id.txtSpell);
        txtCnt= (TextView)findViewById(R.id.txtCnt);
        txtAnswer= (TextView)findViewById(R.id.txtAnswer);
        editSpell= (EditText)findViewById(R.id.editSpell);
        btnSpell= (Button)findViewById(R.id.btnSpell);
        ttsSpell=(ImageView)findViewById(R.id.ttsSpell);

        btnHint1= (Button)findViewById(R.id.btnHint1);
        btnHint2= (Button)findViewById(R.id.btnHint2);

        user_id= getIntent().getStringExtra("user_id");

        getData(Common.Server+"study.php");
        txtCnt.setText((next)+"/"+10);
    }
}
