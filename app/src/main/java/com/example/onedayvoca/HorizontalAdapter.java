package com.example.onedayvoca;

import android.content.Context;
import android.os.AsyncTask;
import android.speech.tts.TextToSpeech;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by JUNI_DEV on 2018-08-11.
 */


public class HorizontalAdapter extends RecyclerView.Adapter<HorizontalViewHolder> implements TextToSpeech.OnInitListener{

    private ArrayList<HorizontalData> HorizontalDatas;

    private TextToSpeech tts;
    boolean mShowingFragments= false;
    private Context context;
    HorizontalViewHolder holder;
    private float mBaseElevation;

    public HorizontalAdapter(Context context) {
        this.context = context;
    }

    public void setData(ArrayList<HorizontalData> list){
        HorizontalDatas = list;
    }

    @Override
    public HorizontalViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        // 사용할 아이템의 뷰를 생성해준다.
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card, parent, false);

        HorizontalViewHolder holder = new HorizontalViewHolder(view);

        return holder;
    }



    @Override
    public void onBindViewHolder(final HorizontalViewHolder holder, final int position) {
        final HorizontalData data = HorizontalDatas.get(position);
        this.holder= holder;
        tts= new TextToSpeech(context,this);

        holder.txtEng.setText(data.getEng());
        holder.txtKor.setText(data.getKor());

        //뜻 숨기기 버튼
        holder.btnHide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mShowingFragments) {
                    Log.i("txtKor","보이게"+position);
                    holder.btnHide.setText("뜻 보이게");
                    holder.txtKor.setVisibility(View.INVISIBLE);
                } else {
                    Log.i("txtKor","안보이게"+position);
                    holder.btnHide.setText("뜻 가리기");
                    holder.txtKor.setVisibility(View.VISIBLE);
                }

                mShowingFragments = !mShowingFragments;
            }
        });

        holder.btnVol.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speakOut(data.getEng().toString());
            }
        });

        //북마크 토글버튼
        holder.tbStar.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.star_on,null));
        holder.tbStar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //삭제 못하게 막아놓자

//                if(holder.tbStar.isChecked()){
//                    //삭제
//                    //delete.start();
//                    holder.tbStar.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.star_off,null));
//
//                    new deleteWord().execute(Common.Server+"myword_delete.php?eng='"+data.getEng()+"'");
//                    Log.i("delete wordlist",data.getEng()+"");
//                    HorizontalDatas.remove(position);
//                    notifyDataSetChanged();
//                }else{
//                    holder.tbStar.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.star_on,null));
////                    insert.start();
//                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return HorizontalDatas.size();
    }

    //단어 뜻 읽어주기 TTS
    private void speakOut(String text) {
        Log.i("text", text+"");
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null,text+"");
    }

    //TTS 작업
    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            // 작업 성공
            int language = tts.setLanguage(Locale.ENGLISH);  // 언어 설정
            if (language == TextToSpeech.LANG_MISSING_DATA || language == TextToSpeech.LANG_NOT_SUPPORTED) {
                // 언어 데이터가 없거나, 지원하지 않는경우
                holder.btnVol.setEnabled(false);
            } else {
                // 준비 완료
                holder.btnVol.setEnabled(true);
            }
        } else {
            Log.i("onInit","작업 실패");
        }
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
                return null;
            }

        }
    }

}

class HorizontalViewHolder extends RecyclerView.ViewHolder {

    public TextView txtEng, txtKor;
    public Button btnHide;
    public ToggleButton tbStar;
    public ImageView btnVol;

    public HorizontalViewHolder(View itemView) {
        super(itemView);

        txtEng = (TextView) itemView.findViewById(R.id.txtEng);
        txtKor = (TextView) itemView.findViewById(R.id.txtKor);
        btnHide = (Button) itemView.findViewById(R.id.btnHide);
        tbStar= (ToggleButton)itemView.findViewById(R.id.tbStar);
        btnVol= (ImageView)itemView.findViewById(R.id.btnVol);
    }
}



class HorizontalData {

    private String eng,kor;

    public HorizontalData(String eng, String kor) {
        this.eng = eng;
        this.kor = kor;
    }

    public String getEng() {
        return eng;
    }

    public void setEng(String eng) {
        this.eng = eng;
    }

    public String getKor() {
        return kor;
    }

    public void setKor(String kor) {
        this.kor = kor;
    }
}
