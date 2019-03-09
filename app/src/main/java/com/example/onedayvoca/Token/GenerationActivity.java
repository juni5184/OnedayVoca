package com.example.onedayvoca.Token;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.onedayvoca.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class GenerationActivity extends AppCompatActivity implements GenerationContract.View {

    public static final String TAG = GenerationActivity.class.getName();
    private static final int REQUEST_PERMISSION_WRITE_STORAGE = 0;
    private GenerationContract.Presenter mWalletPresenter;
    private Button mGenerateWalletButton;
    private String mWalletAddress;
    private EditText mPassword;
    private String user_id;
    private String json;
    private boolean idHere;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generation);

        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar1);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("이더리움 토큰 지갑");
        toolbar.setTitleTextColor(Color.WHITE);

        init();


        mGenerateWalletButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getData("http://ec2-52-78-188-170.ap-northeast-2.compute.amazonaws.com/wallet.php?user_id='"+user_id+"'");
            }
        });
    }


    private void init() {
        mGenerateWalletButton = (Button) findViewById(R.id.generate_wallet_button);
        mPassword = (EditText) findViewById(R.id.password);
        user_id= getIntent().getStringExtra("user_id");
    }

    @Override
    public void setPresenter(GenerationContract.Presenter presenter) {
        mWalletPresenter = presenter;
    }

    @Override
    public void showGeneratedWallet(String address) {
        mWalletAddress = address;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION_WRITE_STORAGE: {
                if (grantResults.length == 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    finish();
                } else {
                    mWalletPresenter.generateWallet(mPassword.getText().toString());
                }
                break;
            }
        }
    }

    //지갑 주소 가져오기
    public void getData(String url) {
        class GetDataJSON extends AsyncTask<String, Void, String> {
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
            @Override
            protected void onPostExecute(String result) {
                Log.i("result",result+"");
                json= result;
                String getId="";
                int cnt=0;
                //wallet에 있는 member를 가져와서 현재 userId가 있으면 isHere=true
                try {
                    JSONObject jsonObject= new JSONObject(json);
                    JSONArray jsonArray= jsonObject.getJSONArray("result");
                    for(int i=0;i<jsonArray.length();i++){
                        JSONObject jObject= jsonArray.getJSONObject(i);
                        getId= jObject.getString("user_id");
                        Log.i("userId getId", getId+"");
                        if(getId.equals(user_id)){
                            Log.i("userId Generate", user_id+"");
                            cnt++;
                        }
                    }
                    if(cnt>0){
                        idHere=true;
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                int permissionCheck = ContextCompat.checkSelfPermission(GenerationActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE);
                if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(
                            GenerationActivity.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            REQUEST_PERMISSION_WRITE_STORAGE);
                } else {
                    if(idHere== true){
                        try {


                            JSONObject jsonObject = new JSONObject(json);
                            JSONArray jsonArray= jsonObject.getJSONArray("result");
                            JSONObject jObject= jsonArray.getJSONObject(0);
                            String getPwd= jObject.getString("passwd");
                            String getAddress= jObject.getString("address");
                            if(mPassword.getText().toString().equals(getPwd)){
                                Intent intent = new Intent(GenerationActivity.this, WalletActivity.class);
                                intent.putExtra("password", getPwd);
                                intent.putExtra("WalletAddress", getAddress);
                                startActivity(intent);
                            }else{
                                Toast.makeText(getApplicationContext(), "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();;
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }else{
                        mWalletPresenter = new GenerationPresenter(GenerationActivity.this,
                                mPassword.getText().toString());
                        mWalletPresenter.generateWallet(mPassword.getText().toString());
                        new insert().execute("http://ec2-52-78-188-170.ap-northeast-2.compute.amazonaws.com/"
                                +"wallet_insert.php?user_id='"+user_id+"'&address='"+mWalletAddress+"'&passwd='"+mPassword.getText().toString()+"'");
                        Intent intent = new Intent(GenerationActivity.this, WalletActivity.class);
                        intent.putExtra("WalletAddress", mWalletAddress);
                        startActivity(intent);
                        Toast.makeText(getApplicationContext(), "지갑이 생성되었습니다.", Toast.LENGTH_SHORT).show();

                    }
                }
            }
        }
        GetDataJSON g = new GetDataJSON();
        g.execute(url);
    }

    private class insert extends AsyncTask<String, Void, String> {
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


}
