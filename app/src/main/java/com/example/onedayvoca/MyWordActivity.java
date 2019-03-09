package com.example.onedayvoca;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.onedayvoca.MyWord.MyWordAdapter;
import com.example.onedayvoca.MyWord.MyWordListItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class MyWordActivity extends AppCompatActivity {

    private ListView listWord;
    private EditText editSearch;
    private MyWordAdapter adapter;

    private ArrayList<MyWordListItem> itemList = new ArrayList<MyWordListItem>() ;

    private ArrayList<HashMap<String, String>> wordList= new ArrayList<HashMap<String, String>>();

    private String myJSON;
    private JSONArray myWords = null;

    private JSONObject jsonObj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myword);

        init();

        getData(Common.Server+"myword.php"); //수정 필요

        adapter= new MyWordAdapter(itemList,MyWordActivity.this);
        listWord.setAdapter(adapter);

        Toolbar toolbar = (Toolbar) findViewById(R.id.tb_myword);
        setSupportActionBar(toolbar);
        setTitle("내 단어장");
        toolbar.setTitleTextColor(Color.WHITE);

        listWord.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MyWordActivity.this, Card2Activity.class);
//                Log.i("S_hashmap",wordList+"");
//                Log.i("word_click",wordList.get(position)+"");

                intent.putExtra("hashMap", wordList);
                intent.putExtra("num",position);
                startActivity(intent);
            }
        });


    }

    protected void showList() {
        try {

            jsonObj = new JSONObject(myJSON);
            myWords = jsonObj.getJSONArray("result");

            for (int i = 0; i < myWords.length(); i++) {
                JSONObject jsonObject = myWords.getJSONObject(i);
                String eng = jsonObject.getString("eng");
                String kor = jsonObject.getString("kor");

                HashMap<String, String> words = new HashMap<String, String>();
                words.put("eng", eng);
                words.put("kor", kor);
                wordList.add(words);

                adapter.addItem(eng+"", kor+"");
            }
            listWord.setAdapter(adapter);

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
                try {
                    jsonObj = new JSONObject(myJSON);
                    myWords = jsonObj.getJSONArray("result");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //갯수 너무 적으면
                Log.i("myword jsonarray", myWords.length()+"");
                if(myWords.length()<1){
                    AlertDialog.Builder builder = new AlertDialog.Builder(MyWordActivity.this);
                    builder.setTitle("내 단어장");
                    builder.setMessage("내 단어장에 단어가 없습니다.");
                    builder.setPositiveButton("예",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast.makeText(MyWordActivity.this,"테스트를 진행하세요.",Toast.LENGTH_LONG).show();
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

    public void init(){

        listWord= (ListView)findViewById(R.id.listWord);
        editSearch= (EditText)findViewById(R.id.editSearch);

        // input창에 검색어를 입력시 "addTextChangedListener" 이벤트 리스너를 정의한다.
        // 검색할 때 리스트뷰 바로바로 바뀌는 기능
        editSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void afterTextChanged(Editable editable) {
                // input창에 문자를 입력할때마다 호출된다.
                // search 메소드를 호출한다.
                String text = editSearch.getText().toString();
                adapter.search(text);
            }
        });

    }
}
