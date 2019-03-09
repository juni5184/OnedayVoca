package com.example.onedayvoca.CardViewPager;


public class CardItem {

    private String txtEng;
    private String txtKor;

    public CardItem(String eng, String kor) {
        txtEng = eng;
        txtKor = kor;
    }

    public String getEng() {
        return txtEng;
    }

    public String getKor() {
        return txtKor;
    }
}
