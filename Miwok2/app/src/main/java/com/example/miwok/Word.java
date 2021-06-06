package com.example.miwok;

import android.widget.ImageView;

public class Word {
    private  String mDefaultTranslation;
    private  String mMiwokTranslation;
    private  Integer mImageResourceId=NO_IMAGE;
    private int mAudioResourceId;
    private static final int NO_IMAGE=-1;


    public Word(String mDefaultTranslation, String mMiwokTranslation,int audioResourceId) {
        this.mDefaultTranslation = mDefaultTranslation;
        this.mMiwokTranslation = mMiwokTranslation;
        this.mAudioResourceId=audioResourceId;
    }

    public Word(String mDefaultTranslation, String mMiwokTranslation, Integer mImageResourceId,int AudioResourceId) {
        this.mDefaultTranslation = mDefaultTranslation;
        this.mMiwokTranslation = mMiwokTranslation;
        this.mImageResourceId = mImageResourceId;
        this.mAudioResourceId=AudioResourceId;
    }

    public String DefaultTranslation() {
        return mDefaultTranslation;
    }


    public String MiwokTranslation() {
        return mMiwokTranslation;
    }
    public int getImageResourceId() {return mImageResourceId; }
    public boolean hasImage(){
         return mImageResourceId!=NO_IMAGE;
    }
    public int getmAudioResourceId(){
        return mAudioResourceId;
    }

}
