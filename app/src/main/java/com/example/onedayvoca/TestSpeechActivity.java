package com.example.onedayvoca;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
import java.util.Locale;
import java.util.Random;

public class TestSpeechActivity extends AppCompatActivity implements RecognitionListener, TextToSpeech.OnInitListener {

    private SpeechRecognizer speech;

    private Intent recognizerIntent;
    private final int RESULT_SPEECH = 1000;

    private TextView txtSpeech, txtSpeechKor,txtCnt, txtMySpeech;
    private ImageView ttsSpeech;
    private Button btnSpeech, btnNext;

    private ArrayList<String> eng= new ArrayList<String>();
    private ArrayList<String> kor= new ArrayList<String>();
    private ArrayList<String> wrongEng = new ArrayList<>();
    private ArrayList<String> wrongKor = new ArrayList<>();

    private String myJSON, user_id;
    private int next=1;//문제수
    private int cnt=0; //틀린갯수
    private int idx[] = new int[4];
    private ArrayList<Integer> temp = new ArrayList<>();

    private JSONArray jsonArray;
    private Intent intent;

    private TextToSpeech tts;
    int speechCnt = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_speech);

        init();

        speech = SpeechRecognizer.createSpeechRecognizer(this);
        speech.setRecognitionListener(this);
            //말하기 버튼
            btnSpeech.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
//                recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "ko-KR"); //한글 인식
                    recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "en-US"); //언어지정입니다.
                    recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US");
                    recognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getPackageName());
                    recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);
                    recognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5);   //검색을 말한 결과를 보여주는 갯수
                    startActivityForResult(recognizerIntent, RESULT_SPEECH);
                }
            });


            //pass버튼
            btnNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(next<10) {
                        speakOut(eng.get(idx[0]));
                        //틀린단어에 추가
                        wrongEng.add(eng.get(idx[0]).toString());
                        wrongKor.add(kor.get(idx[0]).toString());
                        cnt++; //틀린갯수 추가
                        Set set = new Set();
                        set.start();
                    }else{
                        Toast.makeText(getApplicationContext()," 10개 끝", Toast.LENGTH_SHORT).show();
                        //단어 테스트 끝나면
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
            });

            ttsSpeech.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    speakOut(txtSpeech.getText().toString());
                }
            });


    }

    public void init(){
        txtSpeech=(TextView)findViewById(R.id.txtSpeech);
        txtSpeechKor=(TextView)findViewById(R.id.txtSpeechKor);
        txtCnt= (TextView)findViewById(R.id.txtCnt);
        txtMySpeech= (TextView)findViewById(R.id.txtMySpeech);
        ttsSpeech=(ImageView)findViewById(R.id.ttsSpeech);
        btnSpeech= (Button)findViewById(R.id.btnSpeech);
        btnNext= (Button)findViewById(R.id.btnNext);

        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar1);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("발음 테스트");
        toolbar.setTitleTextColor(Color.WHITE);

        user_id= getIntent().getStringExtra("user_id");

        intent= new Intent(TestSpeechActivity.this, TestResultActivity.class);
        getData(Common.Server+"study.php");
        txtCnt.setText((next)+"/"+10);

        tts = new TextToSpeech(TestSpeechActivity.this, this);
    }


    @Override
    public void onEndOfSpeech() {

    }

    @Override
    public void onReadyForSpeech(Bundle bundle) {

    }

    @Override
    public void onResults(Bundle results) {
        ArrayList<String> matches = results
                .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

        for(int i = 0; i < matches.size() ; i++){
            Log.e("GoogleActivity", "onResults text : " + matches.get(i));
        }

    }

    @Override
    public void onError(int errorCode) {
        String message;
        switch (errorCode) {
            case SpeechRecognizer.ERROR_AUDIO:
                message = "오디오 에러";
                break;
            case SpeechRecognizer.ERROR_CLIENT:
                message = "클라이언트 에러";
                break;
            case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                message = "퍼미션없음";
                break;
            case SpeechRecognizer.ERROR_NETWORK:
                message = "네트워크 에러";
                break;
            case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                message = "네트웍 타임아웃";
                break;
            case SpeechRecognizer.ERROR_NO_MATCH:
                message = "찾을수 없음";;
                break;
            case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                message = "바쁘대";
                break;
            case SpeechRecognizer.ERROR_SERVER:
                message = "서버이상";;
                break;
            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                message = "말하는 시간초과";
                break;
            default:
                message = "알수없음";
                break;
        }
        Log.e("GoogleActivity", "SPEECH ERROR : " + message);
    }

    @Override
    public void onRmsChanged(float v) {

    }

    @Override
    public void onBeginningOfSpeech() {

    }

    @Override
    public void onEvent(int i, Bundle bundle) {

    }

    @Override
    public void onPartialResults(Bundle bundle) {

    }

    @Override
    public void onBufferReceived(byte[] bytes) {

    }

    //결과화면
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case RESULT_SPEECH : {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> text = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                    //5번 인식
                    int right=0;
                    String answer="";
                    for(int i = 0; i < text.size() ; i++){
                        Log.e("GoogleActivity", "onActivityResult text : " + text.get(i));
                        if(text.get(i).equals(eng.get(idx[0]))) {
                            //하나라도 맞으면 right++

                            right++;
                            answer= text.get(i);
                        }
                    }
                    //10문제 아직 안됐을때
                    if (next < 10) {
                        //맞은갯수가 0보다 크면
                        if(right>0){
                            //하나라도 맞았을때
                            txtMySpeech.setText(answer);
                            txtMySpeech.setBackgroundColor(getColor(R.color.colorPrimary));
                            speakOut(txtSpeech.getText().toString());
                            //다음문제로 넘어감
                            Set set = new Set();
                            set.start();

                        }else {
                            if (speechCnt <= 5) {
                                txtMySpeech.setBackgroundColor(getColor(R.color.colorAccent));
                                txtMySpeech.setText("Try again");
                                speechCnt++;
                            } else {
                                //5번 다 틀렸을때
                                speakOut(txtSpeech.getText().toString());
                                //다음문제로 넘어감
                                Set set = new Set();
                                set.start();
                            }
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), " 10개 끝", Toast.LENGTH_SHORT).show();
                        //단어 테스트 끝나면
                        intent.putExtra("cnt", cnt);
                        intent.putExtra("next", next);
                        intent.putExtra("wrongEng", wrongEng);
                        intent.putExtra("wrongKor", wrongKor);
                        intent.putExtra("user_id", user_id);
                        intent.putExtra("type",4);
                        finish();
                        startActivity(intent);
                    }
                }
                break;
            }
        }
    }


    //단어 뜻 읽어주기 TTS
    private void speakOut(String text) {
        Log.i("text", text+"");
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
                ttsSpeech.setEnabled(false);
            } else {
                // 준비 완료
                ttsSpeech.setEnabled(true);
            }
        } else {
            Log.i("onInit","작업 실패");
        }
    }


    //테스트 세팅
    public class Set extends Thread{
        public void run(){
            try {
                Thread.sleep(2000);
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
                    txtSpeech.setText(eng.get(idx[0]));
                    txtSpeechKor.setText(kor.get(idx[0]));
                    next++;
                    txtCnt.setText((next)+"/"+10);
                    Log.i("next","next:"+next);
                    txtMySpeech.setText("Speak");
                    txtMySpeech.setBackgroundColor(getColor(R.color.white));
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
                    txtSpeech.setText(eng.get(idx[0]));
                    txtSpeechKor.setText(kor.get(idx[0]));
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

}
