package com.example.onedayvoca.Study;

/**
 * Created by JUNI_DEV on 2018-08-04.
 */

public class StudyListItem {

    private String studyEng ;
    private String studyKor ;

    public StudyListItem() {
    }

    public StudyListItem(String myEng, String myKor) {
        this.studyEng = studyEng;
        this.studyKor = studyKor;
    }


    public String getStudyEng() {
        return studyEng;
    }

    public void setStudyEng(String studyEng) {
        this.studyEng = studyEng;
    }

    public String getStudyKor() {
        return studyKor;
    }

    public void setStudyKor(String studyKor) {
        this.studyKor = studyKor;
    }
}
