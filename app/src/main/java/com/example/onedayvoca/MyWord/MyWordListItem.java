package com.example.onedayvoca.MyWord;

/**
 * Created by JUNI_DEV on 2018-08-04.
 */

public class MyWordListItem {

    private String myEng ;
    private String myKor ;

    public MyWordListItem() {
    }

    public MyWordListItem(String myEng, String myKor) {
        this.myEng = myEng;
        this.myKor = myKor;
    }

    public String getMyEng() {
        return myEng;
    }

    public void setMyEng(String myEng) {
        this.myEng = myEng;
    }

    public String getMyKor() {
        return myKor;
    }

    public void setMyKor(String myKor) {
        this.myKor = myKor;
    }
}
