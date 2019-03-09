package com.example.onedayvoca;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TabHost;

import com.example.onedayvoca.Chatting.FriendItem;
import com.example.onedayvoca.Chatting.LastChatVO;
import com.example.onedayvoca.Chatting.MySQLiteOpenHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class ChattingActivity extends AppCompatActivity {

    private TabHost tabHost;
    private FrameLayout frameLayout;

    private ListView friendListView, chatListView;
    private EditText editSearch;

    private FriendListAdapter fAdapter;
    private ChatListAdapter cAdapter;

    private ArrayList<FriendItem> fItems = new ArrayList<FriendItem>() ;
    private ArrayList<LastChatVO> cItems = new ArrayList<LastChatVO>();

    private ArrayList<HashMap<String, String>> friendList= new ArrayList<HashMap<String, String>>();

    private String myJSON;
    private JSONArray myFriends = null;

    private JSONObject jsonObj;

    private MySQLiteOpenHelper helper;
    String dbName = "voca.db";
    int dbVersion = 1; // 데이터베이스 버전
    private SQLiteDatabase db;
    String tag = "SQLite"; // Log 에 사용할 tag

    String user_id;
    ArrayList<FriendItem> selectItem = new ArrayList<FriendItem>() ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatting);

        init();

        fAdapter= new FriendListAdapter(fItems,ChattingActivity.this, user_id);
        friendListView.setAdapter(fAdapter);

        cAdapter= new ChatListAdapter(cItems, ChattingActivity.this);
        chatListView.setAdapter(cAdapter);

        chatListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ChattingActivity.this, ChatRoomActivity.class);
                intent.putExtra("roomName",selectItem.get(position).getName().toString());
                intent.putExtra("user_id",user_id);
                startActivity(intent);
            }
        });
    }

    public void selectLastChat() {
        //친구 수 만큼 친구목록 돌려서 sender는 나고 receiver가 친구인 목록 추출
//        Log.i("fItem",fItems+"");
        for(int i=0;i<fItems.size();i++) {
            Log.i("selectLast", user_id + "," + fItems.get(i).getName());
            Cursor c = db.rawQuery("select DISTINCT receiver from chatting where sender='" + user_id + "';", null);
            FriendItem friendItem= new FriendItem();
            while (c.moveToNext()) {
                String receiver = c.getString(0);
                if(receiver.equals(fItems.get(i).getName())) {
                    friendItem.setName(receiver);
                    selectItem.add(friendItem);
                }
            }
        }

        for(int i=0;i<selectItem.size();i++){
            Cursor c = db.rawQuery("select * from chatting where sender='" + user_id + "' and receiver='" + selectItem.get(i).getName() + "';", null);
            Log.i("lastChatList", selectItem.get(i).getName() + "");
            LastChatVO lastChatVO = new LastChatVO();
            while (c.moveToNext()) {
                String sender = c.getString(1);
                String receiver = c.getString(2);
                String content = c.getString(3);
                String date = c.getString(4);

                //YYYY-MM-dd hh:mm:ss 형식으로 나옴
                String[] time = date.split(" ");
                //친구가 보냈던 메세지일때
                //친구 : 메세지 형식으로 나옴
                String[] con = content.split(" : ");

                //친구가 보냈을때
                if (con.length > 1) {
                    lastChatVO.setUser_id(receiver);
                    lastChatVO.setContent(con[1]);
                    lastChatVO.setTime(time[1]);
                    //내가 보냈던 메세지일때
                } else {
                    lastChatVO.setUser_id(receiver);
                    lastChatVO.setContent(content);
                    lastChatVO.setTime(time[1]);
                }

//                Log.d(tag+" last", "id:" + id + ", sender:" + sender + ", receiver:" + receiver
//                        + ", content:" + content + ", date:" + date);
            }
            cItems.add(lastChatVO);
            Log.i("lastChatVO1", lastChatVO.getUser_id()+"");
            Log.i("lastChatVO2", lastChatVO.getContent()+"");

            Log.i("cItem", cItems+"");
            cAdapter= new ChatListAdapter(cItems, ChattingActivity.this);
            chatListView.setAdapter(cAdapter);
        }
    }


    protected void showList() {
        try {

            jsonObj = new JSONObject(myJSON);
            myFriends = jsonObj.getJSONArray("result");

            for (int i = 0; i < myFriends.length(); i++) {
                JSONObject jsonObject = myFriends.getJSONObject(i);
                String friendName = jsonObject.getString("user_id");

                HashMap<String, String> friends = new HashMap<String, String>();
                if(!friendName.equals(user_id)){
                    friends.put("friendName", friendName);
                    friendList.add(friends);
                    fAdapter.addItem(friendName+"");
                }
            }
            friendListView.setAdapter(fAdapter);
            selectLastChat();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

        //DB에서 데이터 가져옴
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
                    myJSON = result;
                    showList();
                }
            }
            GetDataJSON g = new GetDataJSON();
            g.execute(url);
        }
    public void init(){

        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("채팅");
        toolbar.setTitleTextColor(Color.WHITE);

        getData("http://ec2-52-78-188-170.ap-northeast-2.compute.amazonaws.com/member.php");

        friendListView= (ListView)findViewById(R.id.friendListView);
        chatListView= (ListView)findViewById(R.id.chatListView);
        tabHost= (TabHost)findViewById(R.id.tabhost);
        frameLayout= (FrameLayout)findViewById(android.R.id.tabcontent);

        user_id= getIntent().getStringExtra("user_id");

        tabHost.setup();
        TabHost.TabSpec tab1= tabHost.newTabSpec("1").setContent(R.id.tab1).setIndicator("친구목록");
        TabHost.TabSpec tab2= tabHost.newTabSpec("2").setContent(R.id.tab2).setIndicator("채팅");
        tabHost.addTab(tab1);
        tabHost.addTab(tab2);
        tabHost.setCurrentTab(0);

        //SQLiteHelper 생성
        helper = new MySQLiteOpenHelper(
                this,  // 현재 화면의 제어권자
                dbName,// db 이름
                null,  // 커서팩토리-null : 표준커서가 사용됨
                dbVersion);

        try {
            db = helper.getWritableDatabase(); // 읽고 쓸수 있는 DB
            //db = helper.getReadableDatabase(); // 읽기 전용 DB select문
        } catch (SQLiteException e) {
            e.printStackTrace();
            Log.e(tag, "데이터베이스를 얻어올 수 없음");
            finish(); // 액티비티 종료
        }

        //검색기능
//        editSearch= (EditText)findViewById(R.id.editSearch);

        // input창에 검색어를 입력시 "addTextChangedListener" 이벤트 리스너를 정의한다.
        // 검색할 때 리스트뷰 바로바로 바뀌는 기능
//        editSearch.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//            }
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//            }
//            @Override
//            public void afterTextChanged(Editable editable) {
//                // input창에 문자를 입력할때마다 호출된다.
//                // search 메소드를 호출한다.
//                String text = editSearch.getText().toString();
//                adapter.search(text);
//            }
//        });

    }
}
