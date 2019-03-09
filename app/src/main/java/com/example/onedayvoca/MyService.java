package com.example.onedayvoca;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.example.onedayvoca.Chatting.ChatVO;
import com.example.onedayvoca.Chatting.MySQLiteOpenHelper;
import com.example.onedayvoca.Chatting.NotificationManager;
import com.example.onedayvoca.WebRTC.CallActivity;
import com.example.onedayvoca.WebRTC.CallFragment;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.security.auth.callback.Callback;

/**
 * Created by JUNI_DEV on 2018-08-29.
 */

public class MyService extends Service {

    private boolean isConnect = false;
    private boolean isRunning = false;

    private String user_id;
    private String user_nickname;
    private String receiver;
    private Socket member_socket;

    //현재 시간
    private String getTime;

    private MySQLiteOpenHelper helper;
    String dbName = "voca.db";
    int dbVersion = 1; // 데이터베이스 버전
    private SQLiteDatabase db;
    String tag = "SQLite"; // Log 에 사용할

    private ChatRoomAdapter adapter;
    private ArrayList<ChatVO> list = new ArrayList<ChatVO>();

    //Service Binder
    class MyBinder extends Binder {
        MyService getService() { //서비스 객체를 리턴
            return MyService.this;
        }
    }

    private final IBinder mBinder = new MyBinder();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    //콜백 인터페이스 선언
    public interface ICallback {
        public void recvData(String msg); //액티비티에서 선언한 콜백 함수.
    }

    private ICallback mCallback;

    //액티비티에서 콜백 함수를 등록하기 위함.
    public void registerCallback(ICallback cb) {
        mCallback = cb;
    }


    @Override
    public void onCreate() {
        super.onCreate();


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
        }

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        user_id = getApplicationContext().getSharedPreferences("login",MODE_PRIVATE).getString("user_id","");
        Log.i("service_id",user_id+"");
//        user_id =intent.getStringExtra("user_id");

        adapter = new ChatRoomAdapter(getApplicationContext(), list, user_id);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager.createChannel(MyService.this);
        }

        ConnectionThread connectionThread = new ConnectionThread();
        connectionThread.start();

        Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public class ConnectionThread extends Thread {
        @Override
        public void run() {
            try {
                // 접속한다.
                Log.i("connectionThread", user_id + "");
                final Socket socket = new Socket("ec2-52-78-188-170.ap-northeast-2.compute.amazonaws.com", 1104);
//                final Socket socket = new Socket("192.168.0.212", 1104);
                member_socket = socket;
                Log.i("connectionSocket", member_socket + "");
                // 미리 입력했던 닉네임을 서버로 전달한다.
                String nickName = user_id;
                user_nickname = nickName;     // 화자에 따라 말풍선을 바꿔주기위해
                // 스트림을 추출
                OutputStream os = socket.getOutputStream();
                DataOutputStream dos = new DataOutputStream(os);
                // 닉네임을 송신한다.
                dos.writeUTF(nickName);
//              접속 상태를 true로 셋팅한다.
                isConnect = true;
                isRunning = true;
//              메세지 수신을 위한 스레드 가동
                MessageThread messageThread = new MessageThread();
                messageThread.start();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //수신부
    class MessageThread extends Thread {
        DataInputStream dis;

        public MessageThread() {
            try {
                InputStream is = member_socket.getInputStream();
                dis = new DataInputStream(is);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            try {
                while (isRunning) {
                    // 서버로부터 데이터를 수신받는다.
                    final String msg = dis.readUTF();
                    //여기서 msg가 현재 (보낸 사람 이름 : 받을사람 : 내용 : 타입) 이런식으로 나옴
                    final String[] data = msg.split(" : ");
                    Log.i("messageThreadSocket", member_socket + "");
                    Log.i("messageThread", data[0] + "," + data[1] + "," + data[2]+","+data[3]);
                    //현재시간
                    Date date = new Date();
                    SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss");
                    getTime = sdf.format(date);

                    // 메세지의 시작 이름이 내 닉네임과 일치한다면
                    if (data[0].equals(user_nickname)) {
                        //내가 보냈을때
                        Log.i("send msg", "Me. " + user_nickname);
                        insert(user_nickname, receiver, msg);
                    } else { //현재 통신하고 있는 사용자와 같을때만 받기
                        //남이 보냈을때
                        if (data[3].equals("chatting")) {
                            Log.i("send msg", "Friend. " + msg);
                            insert(data[0], data[1], data[0] + " : " + data[2]);

                            //receiver가 item(position)이랑 같을때 라는 조건도 추가해줘야함
                            if (getRunActivity().equals("com.example.onedayvoca.ChatRoomActivity")) {
                                //여기서 UI 바로 변경하는 handler 돌려줘야할듯
                                //서비스에서 액티비티 함수 호출은..
                                mCallback.recvData(msg);
                            } else {
                                NotificationManager.sendNotification(MyService.this, 1234, NotificationManager.Channel.NOTICE,
                                        data[0] + "", data[0] + " : " + data[2]);
                            }
                        } else if(data[3].equals("video")) {
                        //영상통화
                            Intent intent= new Intent(MyService.this, VideoActivity.class);
                            intent.putExtra("roomId",data[2]+"");
                            intent.putExtra("sender", data[0]+"");
                            intent.putExtra("receiver", data[1]+"");
                            startActivity(intent);
                        } else if(data[3].equals("no")) {
                            //영상통화 disconnect
//                            mCallback.recvData("disconnect");
                            Log.i("영상통화 disconnect", "상대방이 연결을 끊었습니다.");
                            //메소드가 안돌아감
                            //내 화면 꺼줘야함
                            Toast.makeText( CallActivity.CallActivityContext,"상대방이 연결을 끊었습니다.",Toast.LENGTH_SHORT).show();
                            CallActivity.CallActivityContext= (CallActivity)CallActivity.CallActivityContext;
                            CallActivity.CallActivityContext.finish();

                        }

                    }

                }
            } catch (
                    Exception e)

            {
                e.printStackTrace();
            }
        }
    }

    void insert(String sender, String receiver, String content) {
        db.execSQL("insert into chatting (sender,receiver,content) values('" + sender + "','" + receiver + "','" + content + "');");
        Log.d(tag, "insert into chatting (sender,receiver,content) values('" + sender + "','" + receiver + "','" + content + "');");
    }

    // 서버에 데이터를 전달하는 스레드
    class SendToServerThread extends Thread {
        String msg;
        String sendReceive;
        DataOutputStream dos;
        String type;

        public SendToServerThread(String sendReceive, String msg, String type) {
            try {
                Log.i("sendToServerThread", user_id + "," + sendReceive + "," + msg);
                this.msg = msg; // 보내는 메세지
                this.sendReceive = sendReceive;
                this.type= type;
                receiver = sendReceive;

                Log.i("sendToServerSocket", member_socket + "");
                OutputStream os = member_socket.getOutputStream();
                dos = new DataOutputStream(os);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            try {
                Log.i("sendServerThread", user_id + "@" + sendReceive + "@" + msg+"@"+type);
                // 서버로 데이터를 보낸다.
                dos.writeUTF(user_id + "@" + sendReceive + "@" + msg+"@"+type);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    //현재 메세지 수신자가 실행중인 화면 알려줌
    public String getRunActivity() {

        ActivityManager activity_manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> task_info = activity_manager.getRunningTasks(9999);

        Log.e("runningTask", "activity: " + task_info.get(0).topActivity.getClassName());
        return task_info.get(0).topActivity.getClassName();
    }

}
