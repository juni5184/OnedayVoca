/*
 * Copyright (C) The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.onedayvoca;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.onedayvoca.OCR.OcrCaptureActivity;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class CameraActivity extends AppCompatActivity implements View.OnClickListener, ClipboardManager.OnPrimaryClipChangedListener {

    // Use a compound button so either checkbox or switch widgets work.
    private CompoundButton autoFocus, useFlash;
    private TextView statusMessage;
    private TextView textValue, txtResult;
    private Button btnTranslate, btnAddClip;
    private EditText editWord, editWord2;
    private ImageView imgAdd;

    private String user_id;

    private static final int RC_OCR_CAPTURE = 9003;

    private static final String TAG = "CameraActivity";
    private ClipboardManager clipboardManager;

    private static final String API_KEY = "AIzaSyCuIOvR12e2cOkH-wThgQuMpW4FYKWlWkA";

    private WebView webView;
    private WebSettings webSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        init();

        findViewById(R.id.read_text).setOnClickListener(this);

        //웹뷰 기본화면 설정
        webView.setWebViewClient(new WebViewClient());
        webSettings= webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        webView.loadUrl("https://endic.naver.com/search.nhn");



        final Handler textViewHandler = new Handler();

        clipboardManager = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);

        imgAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                insert into study table
                makeDialog();

            }
        });

        //복사한 클립보드에 있는 단어 추가
        btnAddClip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ClipData pData = clipboardManager.getPrimaryClip();
                ClipData.Item item = pData.getItemAt(0);
                String txtpaste = item.getText().toString();

                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... params) {
                        TranslateOptions options = TranslateOptions.newBuilder().setApiKey(API_KEY).build();
                        Translate translate = options.getService();
                        final Translation translation =
                                translate.translate(txtpaste,
                                        Translate.TranslateOption.targetLanguage("ko"));
                        textViewHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                //edit text에 추가하고
                                //단어 사전 검색 돌리기
                                editWord.setText(txtpaste);
                                editWord2.setText(translation.getTranslatedText());
                                webView.loadUrl("https://endic.naver.com/search.nhn?sLn=en&isOnlyViewEE=N&query="+txtpaste);
                            }
                        });
                        return null;
                    }
                }.execute();
            }
        });

        //번역기 버튼
        btnTranslate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                final Handler textViewHandler = new Handler();

                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... params) {
                        TranslateOptions options = TranslateOptions.newBuilder().setApiKey(API_KEY).build();
                        Translate translate = options.getService();
                        final Translation translation =
                                translate.translate(textValue.getText().toString(),
                                        Translate.TranslateOption.targetLanguage("ko"));
                        textViewHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                    txtResult.setText(translation.getTranslatedText());
                            }
                        });
                        return null;
                    }
                }.execute();

            }
        });

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.read_text) {
            // launch Ocr capture activity.
            Intent intent = new Intent(this, OcrCaptureActivity.class);
            intent.putExtra(OcrCaptureActivity.AutoFocus, autoFocus.isChecked());
            intent.putExtra(OcrCaptureActivity.UseFlash, useFlash.isChecked());

            startActivityForResult(intent, RC_OCR_CAPTURE);
        }
    }

    public void makeDialog(){
        AlertDialog.Builder oDialog = new AlertDialog.Builder(CameraActivity.this);
        oDialog.setMessage("'"+ editWord.getText()+"' 단어장에 추가 하시겠습니까?")
                .setTitle("삭제")
                .setPositiveButton("아니오", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(CameraActivity.this, "취소 되었습니다.", Toast.LENGTH_LONG).show();
                    }
                })
                .setNeutralButton("예", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(CameraActivity.this, "단어장에 추가되었습니다.", Toast.LENGTH_SHORT).show();
                        new insertMyWord().execute(Common.Server+"myword_insert.php?user_id='"+user_id+"'&eng='"+editWord.getText().toString()+"'&kor='"+editWord2.getText().toString()+"'");
                        new insertMyWord().execute(Common.Server+"word_insert.php?eng='"+editWord.getText().toString()+"'&kor='"+editWord2.getText().toString()+"'");
                    }
                })
                .setCancelable(false)
                .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == RC_OCR_CAPTURE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    String text = data.getStringExtra(OcrCaptureActivity.TextBlockObject);
                    statusMessage.setText(R.string.ocr_success);
                    textValue.setText(text);
                    //textview 드래그, 복사 가능
//                    textValue.setTextIsSelectable(true);
                    Log.d(TAG, "Text read: " + text);
                } else {
                    statusMessage.setText(R.string.ocr_failure);
                    Log.d(TAG, "No Text captured, intent data is null");
                }
            } else {
                statusMessage.setText(String.format(getString(R.string.ocr_error),
                        CommonStatusCodes.getStatusCodeString(resultCode)));
            }
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onPrimaryClipChanged() {
        //updateClipData();
        ClipData pData = clipboardManager.getPrimaryClip();
        ClipData.Item item = pData.getItemAt(0);
        String txtpaste = item.getText().toString();
        txtResult.setText(txtpaste);
        Toast.makeText(getApplicationContext(),"Data Pasted from Clipboard",Toast.LENGTH_SHORT).show();
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

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("카메라 인식 단어추가");
        toolbar.setTitleTextColor(Color.WHITE);

        statusMessage = (TextView)findViewById(R.id.status_message);
        textValue = (TextView)findViewById(R.id.text_value);
        txtResult= (TextView)findViewById(R.id.txtResult);
        btnTranslate= (Button)findViewById(R.id.btnTranslate);
        btnAddClip= (Button)findViewById(R.id.btnAddClip);

        editWord= (EditText)findViewById(R.id.editWord);
        editWord2= (EditText)findViewById(R.id.editWord2);
        imgAdd= (ImageView)findViewById(R.id.imgAdd);

        autoFocus = (CompoundButton) findViewById(R.id.auto_focus);
        useFlash = (CompoundButton) findViewById(R.id.use_flash);

        webView= (WebView)findViewById(R.id.webView);

        user_id= getIntent().getStringExtra("user_id");

    }
}
