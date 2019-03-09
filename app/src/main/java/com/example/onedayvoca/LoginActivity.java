package com.example.onedayvoca;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.AsyncTask;
import android.support.design.widget.TextInputEditText;
import android.support.v4.content.SharedPreferencesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.InputType;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.kakao.auth.ErrorCode;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.response.model.UserProfile;
import com.kakao.util.exception.KakaoException;
import com.kakao.util.helper.log.Logger;
import com.nhn.android.naverlogin.OAuthLogin;
import com.nhn.android.naverlogin.OAuthLoginHandler;
import com.nhn.android.naverlogin.ui.view.OAuthLoginButton;

import org.json.JSONException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "OAuthSampleActivity";

    private Button btnLogin;
    private TextView signup;
    private TextInputEditText editId, editPwd;
    private CheckBox cb_autoLogin;
    private String apiResult;

    private String user_id,user_pwd;
    private String server_id, server_pwd;
    private String data;

    private static Context mContext;
    private OAuthLoginButton naverLoginButton;
    private static OAuthLogin mOAuthLoginInstance;

    private static String OAUTH_CLIENT_ID = "1m96tjY3j2H1J6EElzJR  ";
    private static String OAUTH_CLIENT_SECRET = "JCWYzrEc3D";
    private static String OAUTH_CLIENT_NAME = "네이버 아이디로 로그인";

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;


    private SessionCallback callback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mContext = this;

        init();
        initData();

        onClick();

        auto_login(); //자동로그인

        getHashKey(); //카카오톡 키해시 발급

        pref= getSharedPreferences("login",MODE_PRIVATE);
        editor= pref.edit();

        //kakao session callback 초기화
        callback = new SessionCallback();
        Session.getCurrentSession().addCallback(callback);

    }



    public void onClick() {
        //로그인 버튼
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user_id = editId.getText().toString();
                user_pwd = editPwd.getText().toString();

                GetData task = new GetData();
                task.execute(Common.Server+"login.php?user_id=" + user_id + "&user_pwd=" + user_pwd);
            }
        });
        //회원가입
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //SignUp page
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });

        //네이버 로그인
        naverLoginButton.setOAuthLoginHandler(mOAuthLoginHandler);


    }


    //기본 로그인 확인 메소드
    public void loginCheck(){
        //아이디, 비밀번호 입력 확인
        if(editId.getText().toString().equals("")) {
            editId.setError("아이디를 입력하세요");
        }else if(editPwd.getText().toString().equals("")){
            editPwd.setError("비밀번호를 입력하세요");
        }else{
            //아이디 비밀번호 입력 OK
            Log.i("login_id",""+editId.getText().toString());
            Log.i("login_pwd",""+editPwd.getText().toString());

            //아이디, 비밀번호 일치 확인
            if(editId.getText().toString().equals(server_id)){
                if(editPwd.getText().toString().equals(server_pwd)){
                    //페이지 이동
                    Intent intent= new Intent(LoginActivity.this, MenuActivity.class);
                    intent.putExtra("user_id",server_id+"");
                    editor.putString("user_id",server_id+"");
                    editor.commit();
                    startActivity(intent);
                }else{
                    editPwd.setError("비밀번호를 다시 확인하세요.");
                }
            }
            //아이디, 비밀번호 둘 중 하나라도 틀리면 => 로그인 실패
            if(!editId.getText().toString().equals(server_id)||!editPwd.getText().toString().equals(server_pwd)){
                Toast.makeText(getApplicationContext(),"로그인에 실패했습니다.",Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(getApplicationContext(),user_id+"님 환영합니다.",Toast.LENGTH_SHORT).show();
            }
        }


    }


    //php => login.php 기본 로그인 확인
    //데이터를 읽어오는 AsyncTask
    private class GetData extends AsyncTask<String, Void, String> {
        //서버접속하는 동안 dialog로 로딩
        ProgressDialog progressDialog;
        String errorString = null;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(LoginActivity.this,
                    "잠시만 기다려 주세요.", null, true, true);
        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progressDialog.dismiss();

            //서버에서 받아온 결과값 => result , JSON형식으로 받아옴
            data=result;

            Log.i("data",data+"");
            try {
                //서버에서 아이디랑 비밀번호 받아옴
                JSONParser dataParser = new JSONParser();
                JSONObject dataObject = (JSONObject) dataParser.parse(data);
                server_id= dataObject.get("user_id").toString();
                server_pwd= dataObject.get("user_pwd").toString();

                apiResult=  server_id+server_pwd+"";

                loginCheck(); //기본 로그인 확인 메소드

            } catch (ParseException e) {
                e.printStackTrace();
            }
            Log.i("dataResult",apiResult+"");

            Log.d(TAG, "response  - " + result);
            if (result == null){
                Log.i("dataErrorResult",apiResult+"");
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

    //naver 로그인 데이터 초기화
    private void initData() {
        mOAuthLoginInstance = OAuthLogin.getInstance();

        mOAuthLoginInstance.showDevelopersLog(true);
        mOAuthLoginInstance.init(mContext, OAUTH_CLIENT_ID, OAUTH_CLIENT_SECRET, OAUTH_CLIENT_NAME);

		/*
	     * 2015년 8월 이전에 등록하고 앱 정보 갱신을 안한 경우 기존에 설정해준 callback intent url 을 넣어줘야 로그인하는데 문제가 안생긴다.
		 * 2015년 8월 이후에 등록했거나 그 뒤에 앱 정보 갱신을 하면서 package name 을 넣어준 경우 callback intent url 을 생략해도 된다.
		 */
        //mOAuthLoginInstance.init(mContext, OAUTH_CLIENT_ID, OAUTH_CLIENT_SECRET, OAUTH_CLIENT_NAME, OAUTH_callback_intent_url);
    }

    //naver 로그인 핸들러
    private OAuthLoginHandler mOAuthLoginHandler = new OAuthLoginHandler() {
        @Override
        public void run(boolean success) {
            if (success) {
                Toast.makeText(mContext,"네이버 로그인 "+mOAuthLoginInstance.getState(mContext).toString(),Toast.LENGTH_SHORT).show();
                new RequestApiTask().execute();
                Intent intent= new Intent(mContext,MenuActivity.class);
                intent.putExtra("user_id",user_id);
                startActivity(intent);
            } else {
                String errorCode = mOAuthLoginInstance.getLastErrorCode(mContext).getCode();
                String errorDesc = mOAuthLoginInstance.getLastErrorDesc(mContext);
                Toast.makeText(mContext, "errorCode:" + errorCode + ", errorDesc:" + errorDesc, Toast.LENGTH_SHORT).show();
            }
        }

    };


    //naver 토큰삭제 => 미사용
    private class DeleteTokenTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            boolean isSuccessDeleteToken = mOAuthLoginInstance.logoutAndDeleteToken(mContext);

            if (!isSuccessDeleteToken) {
                // 서버에서 token 삭제에 실패했어도 클라이언트에 있는 token 은 삭제되어 로그아웃된 상태이다
                // 실패했어도 클라이언트 상에 token 정보가 없기 때문에 추가적으로 해줄 수 있는 것은 없음
                Log.d(TAG, "errorCode:" + mOAuthLoginInstance.getLastErrorCode(mContext));
                Log.d(TAG, "errorDesc:" + mOAuthLoginInstance.getLastErrorDesc(mContext));
            }

            return null;
        }
        protected void onPostExecute(Void v) {
        }
    }


    //카카오톡 로그인 세션 콜백
    public class SessionCallback implements ISessionCallback {
        @Override
        public void onSessionOpened() {
            UserManagement.requestMe(new MeResponseCallback() {
                @Override
                public void onFailure(ErrorResult errorResult) {
                    String message = "failed to get user info. msg=" + errorResult;

                    ErrorCode result = ErrorCode.valueOf(errorResult.getErrorCode());
                    if (result == ErrorCode.CLIENT_ERROR_CODE) {
                        //에러로 인한 로그인 실패
                        //                        finish();
                    } else {
                        //redirectMainActivity();
                    }
                }
                @Override
                public void onSessionClosed(ErrorResult errorResult) {
                }

                @Override
                public void onNotSignedUp() {
                }

                @Override
                public void onSuccess(UserProfile result) {
                    //로그인에 성공하면 로그인한 사용자의 일련번호, 닉네임, 이미지url등을 리턴합니다.
                    //사용자 ID는 보안상의 문제로 제공하지 않고 일련번호는 제공합니다.
                    //쓸 수 있는게 nickname, photo 밖에 없음 user_name을...흠...

                    Log.i("UserProfile", result.toString());
                    Log.i("UserProfile", result.getId() + "");

                    result.getId();
                    result.getNickname();
                    result.getProfileImagePath();
                    result.getEmail();

                    Log.i("result",result.getServiceUserId()+"+"+ result.getNickname()+"+"+result.getProfileImagePath()+"+"+result.getEmail()+"");

                    Intent intent= new Intent(LoginActivity.this,MenuActivity.class);
                    startActivity(intent);

                }
            });

        }



        @Override
        public void onSessionOpenFailed(KakaoException exception) {
            // 세션 연결이 실패했을때

        }
    }



    //naver 로그인 api호출
    private class RequestApiTask extends AsyncTask<Void, Void, String> {
        @Override
        protected void onPreExecute() {
            apiResult="";
            Log.i("naverResult",apiResult+"");
        }
        @Override
        protected String doInBackground(Void... params) {
            String url = "https://openapi.naver.com/v1/nid/me";
            String at = mOAuthLoginInstance.getAccessToken(mContext);
            return mOAuthLoginInstance.requestApi(mContext, at, url);
        }
        protected void onPostExecute(String content) {
            apiResult=(String)content;
            String result[]=new String[4];
            try {
                JSONParser jsonParser = new JSONParser();
                JSONObject jsonObject = (JSONObject) jsonParser.parse(apiResult);
                JSONObject dataObject = (JSONObject) jsonObject.get("response");
                result[0]= dataObject.get("name").toString();
                result[1]= dataObject.get("email").toString();
                result[2]= dataObject.get("birthday").toString();
                result[3]= dataObject.get("profile_image").toString();

                apiResult= result[0]+result[1]+result[2]+result[3];
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Log.i("naverResult",apiResult+"");
        }
    }


    //자동로그인 체크박스
    public void auto_login(){
        //자동로그인 저장
        SharedPreferences setting = getSharedPreferences("setting", MODE_PRIVATE);
        final SharedPreferences.Editor setEditor = setting.edit();

        cb_autoLogin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    setEditor.putBoolean("Auto_Login_enabled", true);
                    setEditor.commit();
                } else {
                    setEditor.clear();
                    setEditor.commit();
                }
            }

        });

        //자동로그인
        if (setting.getBoolean("Auto_Login_enabled", false)) {
            Intent intent = new Intent(LoginActivity.this, MenuActivity.class);
            intent.putExtra("user_id", server_id);
            startActivity(intent);
        }
    }

    //naver 로그인 창 띄우기?
    @Override
    protected void onResume() {
        //naver
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        super.onResume();
    }

    //카카오톡 로그인 키해시
    private void getHashKey(){
        try {
            PackageInfo info = getPackageManager().getPackageInfo("com.example.onedayvoca", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d(TAG,"key_hash="+ Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public void init(){

        btnLogin= (Button)findViewById(R.id.btnLogin);
        signup=(TextView)findViewById(R.id.signUp);
        signup.setText(Html.fromHtml("<u>회원가입</u>",0)); // 밑줄


        editId=(TextInputEditText)findViewById(R.id.editId);
        editPwd=(TextInputEditText)findViewById(R.id.editPwd);
        cb_autoLogin= (CheckBox)findViewById(R.id.cb_autoLogin);
        naverLoginButton= (OAuthLoginButton)findViewById(R.id.naverLoginButton);
        naverLoginButton.setOAuthLoginHandler(mOAuthLoginHandler);


        //비밀번호 보이게, 안보이게
        editPwd.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_RIGHT = 2;

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (event.getRawX() >= (editPwd.getRight() - editPwd.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        if (editPwd.getInputType() == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
                            editPwd.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                            editPwd.setCompoundDrawablesWithIntrinsicBounds(0, 0, android.R.drawable.ic_menu_view, 0);
                            editPwd.setSelection(editPwd.getText().length());
                        } else {
                            editPwd.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                            editPwd.setCompoundDrawablesWithIntrinsicBounds(0, 0, android.R.drawable.ic_lock_lock, 0);

                        }
                        return true;
                    }
                }
                return false;
            }
        });
    }
}
