package com.example.onedayvoca.MyWord;

import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.Layout;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.onedayvoca.Common;
import com.example.onedayvoca.R;
import com.example.onedayvoca.StudyActivity;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;

public class MyWordAdapter extends BaseAdapter{

    private ArrayList<MyWordListItem> listViewItemList=null;
    private Context context;
    private TextView myEng, myKor;
    LayoutInflater inflater;

    private ArrayList<MyWordListItem> tempList= new ArrayList<MyWordListItem>();

    //생성자
    public MyWordAdapter(ArrayList<MyWordListItem> itemList, Context context) {
        this.context= context;
        this.listViewItemList = itemList ;
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
        if (convertView == null) {
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_myword, parent, false);
        }

        // 화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득
        myEng= (TextView)convertView.findViewById(R.id.myEng);
        myKor= (TextView)convertView.findViewById(R.id.myKor);

        final ToggleButton tbMyword= (ToggleButton)convertView.findViewById(R.id.tbMyword);

        // Data Set(listViewItemList)에서 position에 위치한 데이터 참조 획득
        final MyWordListItem listViewItem = listViewItemList.get(position);

        // 아이템 내 각 위젯에 데이터 반영
        myEng.setText(listViewItem.getMyEng());
        myKor.setText(listViewItem.getMyKor());

        //토글버튼 누르면 다이얼로그로 진짜 삭제할거냐고 물어본 뒤에 삭제
        tbMyword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeDialog(position);
            }
        });

        return convertView;
    }


    // 검색을 수행하는 메소드
    public void search(String text) {
        listViewItemList.clear();
        // 문자 입력이 없을때는 모든 데이터를 보여준다.
        if (text.length() == 0) {
            listViewItemList.addAll(tempList);
        } else {

            for(int i = 0;i < tempList.size(); i++){
                // arraylist의 모든 데이터에 입력받은 단어(charText)가 포함되어 있으면 true를 반환한다.
                // 검색된 데이터를 리스트에 추가한다.
                if (tempList.get(i).getMyEng().toLowerCase().contains(text)) {
                    listViewItemList.add(tempList.get(i));
                }else if(tempList.get(i).getMyKor().toLowerCase().contains(text)){
                    listViewItemList.add(tempList.get(i));
                }
            }
        }
        notifyDataSetChanged();
    }



    //삭제하시겠습니까? Dialog
    //study 목록 삭제 다이얼로그
    public void makeDialog(final int position){
        AlertDialog.Builder oDialog = new AlertDialog.Builder(context);
        oDialog.setMessage("'"+ listViewItemList.get(position).getMyEng()+"' 삭제 하시겠습니까?")
                .setTitle("삭제")
                .setPositiveButton("아니오", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(context, "취소 되었습니다.", Toast.LENGTH_LONG).show();
                    }
                })
                .setNeutralButton("예", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //togglebutton onclick listener
                        new deleteWord().execute(Common.Server+"myword_delete.php?eng='"+listViewItemList.get(position).getMyEng()+"'");
                        listViewItemList.remove(position);
                        notifyDataSetChanged();
                    }
                })
                .setCancelable(false)
                .show();
    }


    // 아이템 데이터 추가를 위한 함수. 개발자가 원하는대로 작성 가능.
    public void addItem(String eng, String kor) {
        MyWordListItem item = new MyWordListItem();

        item.setMyEng(eng);
        item.setMyKor(kor);

        listViewItemList.add(item);

        tempList.add(item);
        Log.i("listViewItemList", listViewItemList+"");
        Log.i("tempList", tempList+"");
    }

    public ArrayList<MyWordListItem> getItemList() {
        return listViewItemList ;
    }

    //북마크 해제시
    //단어 삭제
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
            Log.i("serverURL", serverURL + "");
            try {
                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.connect();

                int responseStatusCode = httpURLConnection.getResponseCode();

                InputStream inputStream;
                if (responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                } else {
                    inputStream = httpURLConnection.getErrorStream();
                }

                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line;

                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line);
                }
                bufferedReader.close();
                return sb.toString().trim();

            } catch (Exception e) {

                return null;
            }

        }
    }
}
