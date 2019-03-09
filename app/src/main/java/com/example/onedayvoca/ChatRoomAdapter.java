package com.example.onedayvoca;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.onedayvoca.Chatting.ChatVO;
import com.example.onedayvoca.R;

import java.util.ArrayList;

public class ChatRoomAdapter extends BaseAdapter{
    private Context context;
    private ArrayList<ChatVO> chatData= new ArrayList<ChatVO>();
    private LayoutInflater inflater;
    private String id;


    public ChatRoomAdapter(Context applicationContext, ArrayList<ChatVO> list, String id) {
        this.context = applicationContext;
        this.chatData = list;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.id = id;
    }

    @Override
    public int getCount() { // 전체 데이터 개수
        return chatData.size();
    }

    @Override
    public Object getItem(int position) { // position번째 아이템
        return chatData.get(position);
    }

    @Override
    public long getItemId(int position) { // position번째 항목의 id인데 보통 position
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) { //항목의 index, 전에 inflate 되어있는 view, listView

    //첫항목을 그릴때만 inflate 함 다음거부터는 매개변수로 넘겨줌 (느리기때문) : recycle이라고 함
        ViewHolder holder;

            //initialize
            holder = new ViewHolder();

            //누군지 판별
            Log.i("position",position+"");
//            Log.i("getID",chatData.get(position).getUser_id().toString()+"");
            //현재 접속중인 아이디
            Log.i("id",id+"");
            Log.i("type",chatData.get(position).getType()+"");

            if (chatData.get(position).getType()==2) {

            //내가 메세지 보낼때
            convertView = inflater.inflate(R.layout.item_my_talk, parent, false); //아이디를 가지고 view를 만든다

            holder.my_msg = (TextView) convertView.findViewById(R.id.my_msg);
            holder.my_time = (TextView) convertView.findViewById(R.id.my_time);
            holder.myLinear= (LinearLayout)convertView.findViewById(R.id.myLinear);

            Log.i("ChatData1",chatData.get(position).getContent()+"");
            Log.i("ChatData position",position+"");


            holder.my_time.setText(chatData.get(position).getTime());
            holder.my_msg.setText(chatData.get(position).getContent());
            holder.my_msg.setBackground(context.getDrawable(R.drawable.outbox2));


        } else {
            //메세지 받을때
            if(chatData.get(position).getType()==0){
                //서버의 메세지
                convertView = inflater.inflate(R.layout.view_main, parent, false);
                holder.text= (TextView)convertView.findViewById(R.id.text);
                holder.text.setText(chatData.get(position).getContent());

            }else{
                //친구 메세지
                convertView = inflater.inflate(R.layout.item_friend_talk, parent, false);

//                  holder.img = (ImageView) convertView.findViewById(R.id.iv_profile);
                holder.tv_msg = (TextView) convertView.findViewById(R.id.tv_content);
                holder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
                holder.tv_name= (TextView)convertView.findViewById(R.id.tv_name) ;
                holder.friendLinear= (LinearLayout)convertView.findViewById(R.id.friendLinear);

                Log.i("ChatData2",chatData.get(position)+"");

//                  holder.img.setImageResource(chatData.get(position).getImageID()); // 해당 사람의 프사 가져옴
                holder.tv_msg.setText(chatData.get(position).getContent());
                holder.tv_time.setText(chatData.get(position).getTime());
                holder.tv_msg.setBackground(context.getDrawable(R.drawable.inbox2));
                //친구이름
                holder.tv_name.setText(chatData.get(position).getUser_id());
            }


        }

        convertView.setTag(holder);

        return convertView;
    }

    //뷰홀더패턴
    public class ViewHolder {
        ImageView img;
        LinearLayout myLinear, friendLinear;
        TextView tv_msg;
        TextView tv_time;
        TextView tv_name;
        TextView my_time;
        TextView my_msg;

        TextView text;
    }
}