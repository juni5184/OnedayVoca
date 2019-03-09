package com.example.onedayvoca.CardViewPager;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.example.onedayvoca.CardActivity;
import com.example.onedayvoca.CardViewPager.CardAdapter;
import com.example.onedayvoca.CardViewPager.CardItem;
import com.example.onedayvoca.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CardPagerAdapter extends PagerAdapter implements CardAdapter, TextToSpeech.OnInitListener{

    private List<CardView> mViews;
    private List<CardItem> mData;
    private float mBaseElevation;
    private TextView txtEng, txtKor;
    private Button btnHide;
    private TextToSpeech tts;

    private ToggleButton tbStar;
    private ImageView btnVol;

    private Context context;

    private View mCurrentView;

    int currentPosition;

    private boolean mShowingFragments = false;

    public CardPagerAdapter(Context context, ViewPager mViewPager) {
        this.context= context;
        mData = new ArrayList<>();
        mViews = new ArrayList<>();

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                //넘어가고 있는 상황을 알려줌
            }

            @Override
            public void onPageSelected(int position) {
                Log.e("CardPagerAdapter", "onPageSelected"+position);
                //여기 position이 선택한 카드의 position을 나타냄
                currentPosition=position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                Log.i("CardPagerAdapter","onPageScrollStateChanged"+state);
            }
        });
    }

    public void addCardItem(CardItem item) {
        mViews.add(null);
        mData.add(item);
        notifyDataSetChanged();
    }

    public float getBaseElevation() {
        return mBaseElevation;
    }

    @Override
    public CardView getCardViewAt(int position) {
        return mViews.get(position);
    }

    //뷰 소멸
    //너무 많은 뷰 객체가 생성될 수 있으므로 소멸시켜줌
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
        mViews.set(position, null);
    }

    //항목 갯수
    @Override
    public int getCount() {
        return mData.size();
    }

    //항목을 위한 뷰 결정
    @Override
    public boolean isViewFromObject(View view, Object object) {
        return object == view;
    }

    //항목 구성
    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        //여기서 position은 현재 보이는 페이지의 position이 맞음
        View view = LayoutInflater.from(container.getContext()).inflate(R.layout.item_card, container, false);
        container.addView(view);
//        bind(mData.get(position), view);

        //tts 초기화
        tts = new TextToSpeech(context,this);

        //여기 들어오는 position이 0,1,2 이런식으로 세개씩 부름
        //button이 마지막 position의 button에 적용되는 것 같음
        Log.i("position",position+"");
        Log.i("currentPosition",currentPosition+"");
        //setText만 잘됨
        txtEng = (TextView) view.findViewById(R.id.txtEng);
        txtKor = (TextView) view.findViewById(R.id.txtKor);
        txtEng.setText(mData.get(position).getEng());
        txtKor.setText(mData.get(position).getKor());
        Log.i("itemBind", mData.get(position).getEng()+"");

        //뜻 가리기 => 2칸 뒤에꺼 가림ㅠㅜㅠ???
        btnHide= (Button) view.findViewById(R.id.btnHide);
        btnHide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mShowingFragments) {
//                    Log.i("txtKor","보이게"+position);
                    btnHide.setText("뜻 보이게");
                    txtKor.setVisibility(View.INVISIBLE);
                } else {
//                    Log.i("txtKor","안보이게"+position);
                    btnHide.setText("뜻 가리기");
                    txtKor.setVisibility(View.VISIBLE);
                }

                mShowingFragments = !mShowingFragments;
            }
        });

        //북마크 버튼
        tbStar= (ToggleButton)view.findViewById(R.id.tbStar);
        tbStar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tbStar.isChecked()){
                    tbStar.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.star_off,null));
//                    insert.start();
                }else{
                    tbStar.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.star_on,null));
//                    delete.start();
                }
            }
        });


        Log.i("mData.get(position)",mData.get(position).getEng().toString()+"");
        CardView cardView = (CardView) view.findViewById(R.id.cardView);

        if (mBaseElevation == 0) {
            mBaseElevation = cardView.getCardElevation();
        }

        //듣기 버튼 눌렀을때 현재 영어단어 읽어주기
        //2칸 뒤에꺼 읽었었는데 txtEng.getText말고 mData.get(position) 해줬더니 잘 읽음
        //mData는 현재페이지 번호를 잘 알려주는것 같다
        //viewpager는 이후 3개까지 더 불러와서 생기는 문제라고 하는데...

        //mData나 position의 문제가 아니고 TextView나 Button이 다다음거를 불러오는 것 같다
        btnVol= (ImageView)view.findViewById(R.id.btnVol);
        btnVol.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speakOut(mData.get(position).getEng()+"");
            }
        });


        cardView.setMaxCardElevation(mBaseElevation * MAX_ELEVATION_FACTOR);
        mViews.set(position, cardView);
        return view;
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
                btnVol.setEnabled(false);
            } else {
                // 준비 완료
                btnVol.setEnabled(true);
            }
        } else {
            Log.i("onInit","작업 실패");
        }
    }
}
