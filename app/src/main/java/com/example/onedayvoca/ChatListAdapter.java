package com.example.onedayvoca;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.onedayvoca.Chatting.FriendItem;
import com.example.onedayvoca.Chatting.LastChatVO;
import com.example.onedayvoca.R;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class ChatListAdapter extends BaseAdapter{

    private ArrayList<LastChatVO> listViewItemList=null;
    private Context context;
    private TextView chatName, chatContent,chatTime;
    private LayoutInflater inflater;

    private ArrayList<FriendItem> tempList= new ArrayList<FriendItem>();

    //생성자
    public ChatListAdapter(ArrayList<LastChatVO> itemList, Context context) {
        this.context= context;
        this.listViewItemList = itemList ;
        Log.i("listViewItemList", listViewItemList+"");
        inflater= LayoutInflater.from(context);
    }

    // Adapter에 사용되는 데이터의 개수를 리턴. : 필수 구현
    @Override
    public int getCount() {
        return listViewItemList.size() ;
    }

    // 지정한 위치(position)에 있는 데이터 리턴 : 필수 구현
    @Override
    public Object getItem(int position) {
        return listViewItemList.get(position) ;
    }

    // 지정한 위치(position)에 있는 데이터와 관계된 아이템(row)의 ID를 리턴. : 필수 구현
    @Override
    public long getItemId(int position) {
        return position ;
    }


    // position에 위치한 데이터를 화면에 출력하는데 사용될 View를 리턴. : 필수 구현
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        //item layout 설정
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.item_chatting, parent, false);

        // 화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득
        chatName= (TextView)convertView.findViewById(R.id.chatName);
        chatContent= (TextView)convertView.findViewById(R.id.chatContent);
        chatTime= (TextView)convertView.findViewById(R.id.chatTime);

        // Data Set(listViewItemList)에서 position에 위치한 데이터 참조 획득
        final LastChatVO listViewItem = listViewItemList.get(position);
        Log.i("lastChatVO", listViewItem+"");

        // 아이템 내 각 위젯에 데이터 반영
        chatName.setText(listViewItem.getUser_id());
        chatContent.setText(listViewItem.getContent());
        chatTime.setText(listViewItem.getTime());

        return convertView;
    }



    //삭제하시겠습니까? Dialog
    //study 목록 삭제 다이얼로그
//    public void makeDialog(final int position){
//        AlertDialog.Builder oDialog = new AlertDialog.Builder(context);
//        oDialog.setMessage("'"+ listViewItemList.get(position).getMyEng()+"' 삭제 하시겠습니까?")
//                .setTitle("삭제")
//                .setPositiveButton("아니오", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        Toast.makeText(context, "취소 되었습니다.", Toast.LENGTH_LONG).show();
//                    }
//                })
//                .setNeutralButton("예", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        //togglebutton onclick listener
//                        new deleteWord().execute(Common.Server+"myword_delete.php?eng='"+listViewItemList.get(position).getMyEng()+"'");
//                        listViewItemList.remove(position);
//                        notifyDataSetChanged();
//                    }
//                })
//                .setCancelable(false)
//                .show();
//    }


    // 아이템 데이터 추가를 위한 함수. 개발자가 원하는대로 작성 가능.
    public void addItem(String name) {
        LastChatVO item = new LastChatVO();

        item.setUser_id(name);
//        item.setContent(contet);

        listViewItemList.add(item);
    }

}
