package com.example.onedayvoca;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class WordAddMenuActivity extends AppCompatActivity {

    private Button btnAdd1, btnAdd2, btnAdd3;
    private Intent intent;

    private String user_id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_add_menu);

        init();

        //기본 단어추가
        btnAdd1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeDialog();
            }
        });

        //갤러리에서 사진 불러와서 단어 추가
        btnAdd2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent= new Intent(WordAddMenuActivity.this, WordAddOCRActivity.class);
                startActivity(intent);
            }
        });

        //카메라로 사진 인식 => text detector
        btnAdd3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent= new Intent(WordAddMenuActivity.this, CameraActivity.class);
                startActivity(intent);
            }
        });
    }

    public void makeDialog(){

        //멤버의 세부내역 입력 Dialog 생성 및 보이기
        LayoutInflater inflater=getLayoutInflater();
        final View dialogView= inflater.inflate(R.layout.dialog_wordadd, null);


        AlertDialog.Builder buider= new AlertDialog.Builder(this); //AlertDialog.Builder 객체 생성
        buider.setTitle("단어 추가"); //Dialog 제목
//        buider.setIcon(android.R.drawable.ic_menu_add); //제목옆의 아이콘 이미지(원하는 이미지 설정)
        buider.setView(dialogView); //위에서 inflater가 만든 dialogView 객체 세팅 (Customize)

        buider.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EditText editEng= (EditText) dialogView.findViewById(R.id.editEng);
                EditText editKor= (EditText) dialogView.findViewById(R.id.editKor);


                Toast.makeText(WordAddMenuActivity.this, "단어장에 추가되었습니다.", Toast.LENGTH_SHORT).show();

                //단어추가
                new insertMyWord().execute(Common.Server+"myword_insert.php?user_id='"+user_id+"'&eng='"+editEng.getText().toString()+"'&kor='"+editKor.getText().toString()+"'");
                new insertMyWord().execute(Common.Server+"word_insert.php?eng='"+editEng.getText().toString()+"'&kor='"+editKor.getText().toString()+"'");

                Log.i("editEng", editEng.getText().toString()+"");
                Log.i("editKor", editKor.getText().toString()+"");
                dialog.dismiss();
            }

        });
        buider.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
               dialog.dismiss();
            }

        });

        //설정한 값으로 AlertDialog 객체 생성
        AlertDialog dialog=buider.create();

        //Dialog의 바깥쪽을 터치했을 때 Dialog를 없앨지 설정
        dialog.setCanceledOnTouchOutside(false);//없어지지 않도록 설정

        //Dialog 보이기
        dialog.show();

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
                return null;
            }

        }
    }


    public void init(){

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("단어추가 메뉴");
        toolbar.setTitleTextColor(Color.WHITE);

        user_id= getIntent().getStringExtra("user_id");

        btnAdd1= (Button)findViewById(R.id.btnAdd1);
        btnAdd2= (Button)findViewById(R.id.btnAdd2);
        btnAdd3= (Button)findViewById(R.id.btnAdd3);

    }
}
