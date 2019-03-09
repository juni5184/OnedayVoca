package com.example.onedayvoca;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Color;
import android.os.*;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.example.onedayvoca.Chatting.ChatVO;
import com.example.onedayvoca.Chatting.MySQLiteOpenHelper;
import com.example.onedayvoca.Chatting.NotificationManager;

public class ChatRoomActivity extends AppCompatActivity {
    // 서버 접속 여부를 판별하기 위한 변수
    private boolean isConnect = false;
    // 어플 종료시 스레드 중지를 위해...
    private boolean isRunning = false;

    private EditText edit1;
    private Button btn1;
    private ListView chatListView;
    private ChatRoomAdapter adapter;
    private ArrayList<ChatVO> list = new ArrayList<ChatVO>();
    private ArrayList<HashMap<String, String>> chatList = new ArrayList<HashMap<String, String>>();

    private String user_id;

    private ChatVO chatVO;
    // 사용자 닉네임( 내 닉넴과 일치하면 내가보낸 말풍선으로 설정 아니면 반대설정)
    private String user_nickname;

    //현재 시간
    private String getTime;
    //메세지 수신자
    private String receiver;

    private MySQLiteOpenHelper helper;
    String dbName = "voca.db";
    int dbVersion = 1; // 데이터베이스 버전
    private SQLiteDatabase db;
    String tag = "SQLite"; // Log 에 사용할 tag

    private boolean mIsBound;
    private MyService myService= new MyService();

    Handler handler;


    private final ServiceConnection mConnection= new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
                MyService.MyBinder binder= (MyService.MyBinder) service;
                myService= binder.getService();
                myService.registerCallback(mCallback);
                Log.i("ChatRoomActivity", "service connected");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.i("ChatRoomActivity", "service disconnected");
            myService= null;
        }
    };

    private MyService.ICallback mCallback = new MyService.ICallback() {
        @Override
        public void recvData(String msg) {
            Log.d("BindService",msg+"");
            //서비스에서 메세지 받아왔음
            Bundle data= new Bundle();
            data.putString("data",msg);
            Message message = new Message();
            message.setData(data);
            handler.sendMessage(message);

//            list.add(new ChatVO(R.drawable.ic_launcher_foreground, data[0], data[2] + "", getTime + "", 1));
//            chatListView.setSelection(list.size());
//            adapter = new ChatRoomAdapter(getApplicationContext(), list, user_id);
//            chatListView.setAdapter(adapter);
//            adapter.notifyDataSetChanged();
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);

        init();

        //해당 페이지에 들어왔을때 noti 자동삭제
        NotificationManager.cancel(ChatRoomActivity.this);

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


        mIsBound= bindService(new Intent(ChatRoomActivity.this, MyService.class),mConnection, Context.BIND_AUTO_CREATE);

        receiver = getIntent().getStringExtra("roomName");

        handler = new Handler(){
            public void handleMessage(Message msg){
                String message = msg.getData().getString("data");
                String data[]= message.split(" : ");
                Log.i("handler", message+"");

                //현재시간
                Date date = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss");
                getTime = sdf.format(date);

                list.add(new ChatVO(R.drawable.ic_launcher_foreground, data[0], data[2] + "", getTime + "", 1));
                adapter = new ChatRoomAdapter(getApplicationContext(), list, user_id);
                chatListView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                chatListView.setSelection(list.size());
            }
        };


        adapter = new ChatRoomAdapter(getApplicationContext(), list, user_id);
        chatListView.setAdapter(adapter);

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                drop();
//                delete();
                // 입력한 문자열을 가져온다.
                String msg = edit1.getText().toString();
                //현재시간
                Date date = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss");
                getTime = sdf.format(date);

                // 서비스 송신 스레드 가동
                MyService.SendToServerThread serverThread= myService.new SendToServerThread(receiver,msg,"chatting");
                serverThread.start();


                list.add(new ChatVO(R.drawable.ic_launcher_foreground, user_id, msg + "", getTime + "", 2));

//                adapter = new ChatRoomAdapter(getApplicationContext(), list, user_id);
//                chatListView.setAdapter(adapter);
                chatListView.setSelection(list.size());
                insert(user_id,receiver,msg);
                edit1.setText("");
            }
        });

        select();
    }

    void drop() {
        db.execSQL("drop table chatting;");
        Log.i(tag + "delete", "삭제완료");
    }

    void delete() {
        db.execSQL("delete from chatting;");
        Log.i(tag + "delete", "삭제완료");
    }

    void insert(String sender, String receiver, String content) {
        db.execSQL("insert into chatting (sender,receiver,content) values('" + sender + "','" + receiver + "','" + content + "');");
        Log.d(tag, "insert into chatting (sender,receiver,content) values('" + sender + "','" + receiver + "','" + content + "');");
    }


    void select() {
//        String selectQuery= "select * from chatting where sender='"+receiver+"';";
        Log.i("select", user_id+","+receiver);
        Cursor c = db.rawQuery("select * from chatting where sender='"+user_id+"' and receiver='"+receiver+"'" +
                "UNION ALL select * from chatting where sender='"+receiver+"' and receiver='"+user_id+"' order by date;", null);
        ArrayList<ChatVO> dbList = new ArrayList<ChatVO>();
        Log.i("dbList", dbList + "");
        while (c.moveToNext()) {
            int id = c.getInt(0);
            String sender = c.getString(1);
            String receiver = c.getString(2);
            String content = c.getString(3);
            String date = c.getString(4);

            //YYYY-MM-dd hh:mm:ss 형식으로 나옴
            String[] time = date.split(" ");
            //친구가 보냈던 메세지일때
            //친구 : 메세지 형식으로 나옴
            String[] con = content.split(" : ");
            Log.i("Chatroom", content+"");

            //initialize 를 잃어버리면 어떡하냐 진짜
            //그러니까 계속 null이 뜨지 아오
            ChatVO chatVO= new ChatVO();

            //친구가 보냈을때
            if (con.length > 1) {
                chatVO.setUser_id(sender);
                chatVO.setContent(con[1]);
                chatVO.setTime(time[1]);
                chatVO.setType(1);
                dbList.add(chatVO);
            } else {
                //내가 보냈던 메세지일때
                chatVO.setUser_id(user_id);
                chatVO.setContent(content);
                chatVO.setTime(time[1]);
                chatVO.setType(2);
                dbList.add(chatVO);
            }
            list.add(chatVO);
            chatListView.setSelection(list.size());

            Log.d(tag+" select", "id:" + id + ", sender:" + sender + ", receiver:" + receiver
                    + ", content:" + content + ", date:" + date);
        }
    }





    public void init() {

        edit1 = findViewById(R.id.editText);
        btn1 = findViewById(R.id.button);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getIntent().getStringExtra("roomName"));
        toolbar.setTitleTextColor(Color.WHITE);

        chatListView = findViewById(R.id.chatListView);

        //수신자 아이디
        user_id = getIntent().getStringExtra("user_id");
        Log.i("받아온 아이디", getIntent().getStringExtra("user_id"));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mIsBound){
            unbindService(mConnection);
            mIsBound= false;
        }
    }
}