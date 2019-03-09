package com.example.onedayvoca;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignupActivity extends AppCompatActivity {

    private TextInputEditText signup_name, signup_id, signup_pwd,signup_pwd2, signup_email;
    private Button btnSignup, btnCheck, btnEmail;

    private String user_name,user_id,user_pwd, user_email;
    private String data;
    private String server_id;

    private String myResult;

    private static final String TAG= "SignupActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        init();
        pwdView();


        //아이디 중복확인 버튼
        btnCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(signup_id.getText().toString().equals("")){
                    signup_id.setError("아이디를 입력하세요.");
                }else{
                    GetData task = new GetData();
                    task.execute(Common.Server+"idCheck.php?user_id=" + signup_id.getText().toString());

                }
            }
        });

        //회원가입 버튼
        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //입력받은 값 String에 저장
                user_name= signup_name.getText().toString();
                user_id= signup_id.getText().toString();
                user_pwd= signup_pwd.getText().toString();
                user_email= signup_email.getText().toString();

                //텍스트 전부 입력되어 있는지 확인
                textInputSetting();


            }
        });

        signup_id.addTextChangedListener(idTextWatcher);
        signup_pwd2.addTextChangedListener(pwdTextWatcher);
        signup_email.addTextChangedListener(emailTextWatcher);
    }


    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            Bundle bun = msg.getData();
        }
    };


    //php POST 전송
    public void HttpPostData() {
        try {
            //--------------------------
            //   URL 설정하고 접속하기
            //--------------------------
            URL url = new URL(Common.Server+"signup.php");       // URL 설정
            HttpURLConnection http = (HttpURLConnection) url.openConnection();   // 접속
            //--------------------------
            //   전송 모드 설정 - 기본적인 설정이다
            //--------------------------
            http.setDefaultUseCaches(false);
            http.setDoInput(true);                         // 서버에서 읽기 모드 지정
            http.setDoOutput(true);                       // 서버로 쓰기 모드 지정
            http.setRequestMethod("POST");         // 전송 방식은 POST

            // 서버에게 웹에서 <Form>으로 값이 넘어온 것과 같은 방식으로 처리하라는 걸 알려준다
            http.setRequestProperty("content-type", "application/x-www-form-urlencoded");
            //--------------------------
            //   서버로 값 전송
            //--------------------------
            StringBuffer buffer = new StringBuffer();
            buffer.append("user_name").append("='").append(user_name).append("'&");                 // php 변수에 값 대입
            buffer.append("user_id").append("='").append(user_id).append("'&");   // php 변수 앞에 '$' 붙이지 않는다
            buffer.append("user_pwd").append("='").append(user_pwd).append("'&");           // 변수 구분은 '&' 사용
            buffer.append("user_email").append("='").append(user_email).append("'");

            Log.i("StringBuffer",buffer+"");

            OutputStreamWriter outStream = new OutputStreamWriter(http.getOutputStream(), "EUC-KR");
            PrintWriter writer = new PrintWriter(outStream);
            writer.write(buffer.toString());
            writer.flush();
            //--------------------------
            //   서버에서 전송받기
            //--------------------------
            InputStreamReader tmp = new InputStreamReader(http.getInputStream(), "EUC-KR");
            BufferedReader reader = new BufferedReader(tmp);
            StringBuilder builder = new StringBuilder();
            String str;
            while ((str = reader.readLine()) != null) {       // 서버에서 라인단위로 보내줄 것이므로 라인단위로 읽는다
                builder.append(str + "\n");                     // View에 표시하기 위해 라인 구분자 추가
            }
            myResult = builder.toString();                       // 전송결과를 전역 변수에 저장
            Log.i("StringBuilder",myResult);

        } catch (MalformedURLException e) {
            //
        } catch (IOException e) {
            //
        } // try
    } // HttpPostData


    //php
    //데이터를 읽어오는 AsyncTask
    private class GetData extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;
        String errorString = null;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(SignupActivity.this,
                    "Please Wait", null, true, true);
        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progressDialog.dismiss();
            data=result;

            Log.i("signup_data",data+"");

            if(Integer.parseInt(data)>0){
                signup_id.setError("이미 사용중인 아이디입니다.");
            }else{
                Toast.makeText(getApplicationContext(),"중복확인 되었습니다.",Toast.LENGTH_SHORT).show();
                signup_id.setFocusableInTouchMode(false);
                btnCheck.setEnabled(false);
                btnCheck.setBackgroundColor(getColor(R.color.colorPrimaryDark));
            }


            Log.d(TAG, "response  - " + result);
            if (result == null){
                Log.i("dataErrorResult",server_id+"");
            }
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
                Log.d(TAG, "response code - " + responseStatusCode);

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
                errorString = e.toString();

                return null;
            }

        }
    }



    //editText 입력값 전부 제대로 들어왔는지 확인
    public void textInputSetting(){

        //이름 확인
        if(user_name.equals("")){
            signup_name.setError("이름을 입력하세요.");
        }else{
            Log.i("name",""+user_name);
        }

        //아이디 확인
        if(user_id.equals("")){
            signup_id.setError("아이디를 입력하세요.");
        }else if(data==null){
            signup_id.setError("아이디 중복확인을 해주세요.");
        }else{
            Log.i("id",""+user_id);
        }

        //비밀번호 확인
        if(user_pwd.equals("")){
            signup_pwd.setError("비밀번호를 입력하세요.");
        }else if(!user_pwd.equals(signup_pwd2.getText().toString())) {
            signup_pwd2.setError("비밀번호가 일치하지 않습니다.");
        }else{
            Log.i("pwd",""+user_pwd);
        }

        //이메일 확인, 형식 체크
        if(user_email.equals("")){
            signup_email.setError("이메일을 입력하세요.");
        }else if(checkEmail(user_email)==false){
            Log.i("checkEmail",""+checkEmail(signup_email.toString()));
            signup_email.setError("이메일을 형식에 맞게 입력하세요.");
        }else{
            Log.i("email",""+user_email);
        }

        //전부 맞을 때
        if(!user_name.equals("")&&!user_id.equals("")&&data!=null &&!user_pwd.equals("")&&user_pwd.equals(signup_pwd2.getText().toString())
                &&!user_email.equals("")&&checkEmail(user_email)==true){
            new Thread() {
                public void run() {
                    HttpPostData();
                    Message msg = handler.obtainMessage();
                    handler.sendMessage(msg);

                }
            }.start();

            Intent intent= new Intent(SignupActivity.this, LoginActivity.class);
            startActivity(intent);

            Toast.makeText(getApplicationContext(),"회원가입 되었습니다.",Toast.LENGTH_SHORT).show();

        }

    }


    TextWatcher idTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            //아이디 숫자 + 영문 조합
            if(!Pattern.matches("^[a-zA-Z0-9]*$",signup_id.getText().toString())){
                signup_id.setError("숫자와 영문 조합으로만 가능합니다.");
                btnCheck.setEnabled(false);
            }else{
                signup_id.setError(null);
                btnCheck.setEnabled(true);
            }
        }
    };

    TextWatcher pwdTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            //비밀번호 일치 확인
            if(signup_pwd2.getText().toString().equals(signup_pwd.getText().toString())){
                signup_pwd2.setError(null);
            }else{
                signup_pwd2.setError("비밀번호가 일치하지 않습니다.");
            }
        }
    };

    TextWatcher emailTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            //비밀번호 일치 확인
            if(checkEmail(signup_email.getText().toString())==true){
                signup_email.setError(null);
            }else{
                signup_email.setError("이메일 형식에 맞게 입력하세요.");
            }
        }
    };


    //비밀번호 보이게 / 안보이게 설정
    public void pwdView(){

        signup_pwd.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_RIGHT = 2;

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (event.getRawX() >= (signup_pwd.getRight() - signup_pwd.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        if (signup_pwd.getInputType() == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
                            signup_pwd.setInputType(InputType.TYPE_CLASS_TEXT |
                                    InputType.TYPE_TEXT_VARIATION_PASSWORD);
                            signup_pwd.setCompoundDrawablesWithIntrinsicBounds(0, 0, android.R.drawable.ic_menu_view, 0);
                            signup_pwd.setSelection(signup_pwd.getText().length());
                        } else {
                            signup_pwd.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                            signup_pwd.setCompoundDrawablesWithIntrinsicBounds(0, 0, android.R.drawable.ic_lock_lock, 0);

                        }
                        return true;
                    }
                }
                return false;
            }
        });
    }

    //이메일 형식 체크
    public static boolean checkEmail(String email){
        String regex = "^[_a-zA-Z0-9-\\.]+@[\\.a-zA-Z0-9-]+\\.[a-zA-Z]+$";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(email);
        boolean isNormal = m.matches();
        return isNormal;
    }

    public void init(){
        signup_name= (TextInputEditText)findViewById(R.id.signup_name);
        signup_id= (TextInputEditText)findViewById(R.id.signup_id);
        signup_pwd= (TextInputEditText)findViewById(R.id.signup_pwd);
        signup_pwd2= (TextInputEditText)findViewById(R.id.signup_pwd2);
        signup_email= (TextInputEditText)findViewById(R.id.signup_email);
        btnSignup= (Button)findViewById(R.id.btnSignup);
        btnCheck= (Button)findViewById(R.id.btnCheck);
        //btnEmail= (Button)findViewById(R.id.btnEmail);

    }
}
