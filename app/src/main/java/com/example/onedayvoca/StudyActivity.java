package com.example.onedayvoca;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.example.onedayvoca.MyWord.MyWordAdapter;
import com.example.onedayvoca.MyWord.MyWordListItem;
import com.example.onedayvoca.Study.StudyAdapter;
import com.example.onedayvoca.Study.StudyListItem;
import com.example.onedayvoca.swipe.SwipeDismissListViewTouchListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class StudyActivity extends AppCompatActivity {

    private ListView lvStudy;
    private String myJSON;
    private EditText editSearch;
    private FloatingActionButton fab;

    private static final String TAG = "StudyActivity";

    private static final String TAG_RESULTS = "result";
    private static final String TAG_ENG = "eng";
    private static final String TAG_KOR = "kor";

    private SimpleAdapter adapter;

    private JSONArray words = null;

    private ArrayList<HashMap<String, String>> wordList;
    private ArrayList<HashMap<String, String>> tempList;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study);

        init();
    }
    public void init(){

        editSearch= (EditText)findViewById(R.id.editSearch);
        lvStudy= (ListView)findViewById(R.id.lvStudy);
        fab= (FloatingActionButton)findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StudyActivity.this, MyWordActivity.class);
                startActivity(intent);
            }
        });

        wordList= new ArrayList<HashMap<String, String>>();

        Toolbar toolbar = (Toolbar) findViewById(R.id.tbStudy);
        setSupportActionBar(toolbar);
        setTitle("단어목록");
        toolbar.setTitleTextColor(Color.WHITE);

        getData(Common.Server+"study.php"); //수정 필요

        lvStudy.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(StudyActivity.this, CardActivity.class);
                Log.i("S_hashmap",wordList+"");
                Log.i("word_click",wordList.get(position)+"");

                intent.putExtra("hashMap", wordList);
                intent.putExtra("num",position);
                startActivity(intent);
            }
        });

        //리스트 스와이프해서 삭제 할 수 있음
        SwipeDismissListViewTouchListener touchListener =
                new SwipeDismissListViewTouchListener(lvStudy,
                        new SwipeDismissListViewTouchListener.DismissCallbacks() {
                            @Override
                            public boolean canDismiss(int position) {
                                return true;
                            }

                            @Override
                            public void onDismiss(ListView listView, int[] reverseSortedPositions) {
                                for (int position : reverseSortedPositions) {
                                    makeDialog(position);
                                }
                            }
                        });
        lvStudy.setOnTouchListener(touchListener);
        lvStudy.setOnScrollListener(touchListener.makeScrollListener());

        // 검색할 때 리스트뷰 바로바로 바뀌는 기능
        // input창에 검색어를 입력시 "addTextChangedListener" 이벤트 리스너를 정의한다.
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
                search(text);
            }
        });

    }

    // 검색을 수행하는 메소드
    public void search(String text) {

        Log.i("study search text",text+"");

        // 문자 입력시마다 리스트를 지우고 새로 뿌려준다.
        wordList.clear();

        // 문자 입력이 없을때는 모든 데이터를 보여준다.
        if (text.length() == 0) {
            wordList.addAll(tempList);
        }
        // 문자 입력을 할때..
        else{
            // 리스트의 모든 데이터를 검색한다.
            for(int i = 0;i < tempList.size(); i++){
                // arraylist의 모든 데이터에 입력받은 단어(charText)가 포함되어 있으면 true를 반환한다.
                // 검색된 데이터를 리스트에 추가한다.
                if (tempList.get(i).get("eng").toLowerCase().contains(text)) {
                    wordList.add(tempList.get(i));
                }else if(tempList.get(i).get("kor").toLowerCase().contains(text)){
                    wordList.add(tempList.get(i));
                }
            }
        }
        Log.i("study search wordList",wordList+"");
        // 리스트 데이터가 변경되었으므로 아답터를 갱신하여 검색된 데이터를 화면에 보여준다.
        adapter.notifyDataSetChanged();
    }


    //기본 세팅
    protected void showList() {
        try {

            JSONObject jsonObj = new JSONObject(myJSON);
            words = jsonObj.getJSONArray(TAG_RESULTS);

            for (int i = 0; i < words.length(); i++) {
                JSONObject c = words.getJSONObject(i);
                String eng = c.getString(TAG_ENG);
                String kor = c.getString(TAG_KOR);

                HashMap<String, String> words = new HashMap<String, String>();
                words.put(TAG_ENG, eng);
                words.put(TAG_KOR, kor);
                wordList.add(words);
            }
            adapter = new SimpleAdapter(
                    StudyActivity.this, wordList, R.layout.item_study,
                    new String[]{TAG_ENG, TAG_KOR}, new int[]{R.id.txtEng, R.id.txtKor}
            );
            lvStudy.setAdapter(adapter);

            tempList= new ArrayList<HashMap<String, String>>();
            tempList.addAll(wordList);
            Log.i("tempList",tempList+"");


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    //단어 데이터 가져오기
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
//                Log.i("result",result+"");
                myJSON = result;
                showList();
            }
        }
        GetDataJSON g = new GetDataJSON();
        g.execute(url);
    }


    //swipe해서 삭제
    private class deleteWord extends AsyncTask<String, Void, String> {
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


    //삭제하시겠습니까? Dialog
    //study 목록 삭제 다이얼로그
    public void makeDialog(final int position){
        AlertDialog.Builder oDialog = new AlertDialog.Builder(this);
        oDialog.setMessage("'"+wordList.get(position).get("eng")+"' 삭제 하시겠습니까?")
                .setTitle("삭제")
                .setPositiveButton("아니오", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplicationContext(), "취소 되었습니다.", Toast.LENGTH_LONG).show();
                    }
                })
                .setNeutralButton("예", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //hashmap에서 삭제
                        new deleteWord().execute(Common.Server+"wordDelete.php?word="+wordList.get(position).get("eng"));
                        Log.i("delete wordlist",wordList.get(position)+"");
                        wordList.remove(position);
                        //단어 sql에서 삭제
                        showList();
                        getData(Common.Server+"study.php");

                        adapter.notifyDataSetChanged();
                    }
                })
                .setCancelable(false) // 백버튼으로 팝업창이 닫히지 않도록 한다.
                .show();
    }

}
