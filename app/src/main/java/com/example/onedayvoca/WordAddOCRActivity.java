/*
 * Copyright 2016 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.onedayvoca;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

//import com.google.api.client.extensions.android.http.AndroidHttp;
//import com.google.api.client.json.gson.GsonFactory;
import com.example.onedayvoca.OCR.PermissionUtils;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;
import com.googlecode.tesseract.android.TessBaseAPI;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class WordAddOCRActivity extends AppCompatActivity implements ClipboardManager.OnPrimaryClipChangedListener{
    private static final String API_KEY = "AIzaSyCuIOvR12e2cOkH-wThgQuMpW4FYKWlWkA";
    public static final String FILE_NAME = "temp.jpg";
    private static final String ANDROID_CERT_HEADER = "X-Android-Cert";
    private static final String ANDROID_PACKAGE_HEADER = "X-Android-Package";
    private static final int MAX_LABEL_RESULTS = 10;
    private static final int MAX_DIMENSION = 1200;

    private static final String TAG = WordAddOCRActivity.class.getSimpleName();
    private static final int GALLERY_PERMISSIONS_REQUEST = 0;
    private static final int GALLERY_IMAGE_REQUEST = 1;
    public static final int CAMERA_PERMISSIONS_REQUEST = 2;
    public static final int CAMERA_IMAGE_REQUEST = 3;

    private ImageView mMainImage;
    private RelativeLayout OCRButtonContainer;
    private TextView btnRun;

    private String datapath = "" ; //언어데이터가 있는 경로
    private TessBaseAPI mTess; //Tess API reference
    private Bitmap image;
    private String OCRresult= null;

    private String user_id;

    private ClipboardManager clipboardManager;

    private ProgressDialog asyncDialog;

    private FloatingActionMenu fab;
    private FloatingActionButton fabCamera, fabGallery, fabAddClip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_add_ocr);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("OCR 단어추가");
        setSupportActionBar(toolbar);

        init();


        //fab로 카메라 이동
        fabCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCamera();
            }
        });

        //fab로 갤러리 이동
        fabGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startGalleryChooser();
            }
        });

        //fab로 단어추가
        fabAddClip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeDialog();
            }
        });

        clipboardManager = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);


        //언어파일 경로
        datapath = getFilesDir()+ "/tesseract/";

        //트레이닝데이터가 카피되어 있는지 체크
        checkFile(new File(datapath + "tessdata/"));

        //Tesseract API
        String lang = "eng";

        mTess = new TessBaseAPI();
        mTess.init(datapath, lang);

    }


    public void makeDialog(){

        //멤버의 세부내역 입력 Dialog 생성 및 보이기
        LayoutInflater inflater=getLayoutInflater();
        final View dialogView= inflater.inflate(R.layout.dialog_wordadd, null);

        AlertDialog.Builder buider= new AlertDialog.Builder(this); //AlertDialog.Builder 객체 생성
        buider.setTitle("단어 추가"); //Dialog 제목
//        buider.setIcon(android.R.drawable.ic_menu_add); //제목옆의 아이콘 이미지(원하는 이미지 설정)
        buider.setView(dialogView); //위에서 inflater가 만든 dialogView 객체 세팅 (Customize)

        EditText editEng= (EditText) dialogView.findViewById(R.id.editEng);
        EditText editKor= (EditText) dialogView.findViewById(R.id.editKor);

        ClipData pData = clipboardManager.getPrimaryClip();
        ClipData.Item item = pData.getItemAt(0);
        String txtpaste = item.getText().toString();

        final Handler textViewHandler = new Handler();

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                TranslateOptions options = TranslateOptions.newBuilder().setApiKey(API_KEY).build();
                Translate translate = options.getService();
                final Translation translation =
                        translate.translate(txtpaste,
                                Translate.TranslateOption.targetLanguage("ko"));
                textViewHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        //edit text에 추가하고
                        //단어 사전 검색 돌리기
                        editEng.setText(txtpaste);
                        editKor.setText(translation.getTranslatedText());
                    }
                });
                return null;
            }
        }.execute();

        buider.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //insert
                //단어추가
                new insertMyWord().execute(Common.Server+"myword_insert.php?user_id='"+user_id+"'&eng='"+editEng.getText().toString()+"'&kor='"+editKor.getText().toString()+"'");
                new insertMyWord().execute(Common.Server+"word_insert.php?eng='"+editEng.getText().toString()+"'&kor='"+editKor.getText().toString()+"'");

                Toast.makeText(WordAddOCRActivity.this, "단어장에 추가되었습니다.", Toast.LENGTH_SHORT).show();
                Log.i("editEng", editEng.getText().toString()+"");
                Log.i("editKor", editKor.getText().toString()+"");
                dialog.dismiss();
            }

        });
        buider.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }

        });

        //설정한 값으로 AlertDialog 객체 생성
        AlertDialog dialog=buider.create();

        //Dialog의 바깥쪽을 터치했을 때 Dialog를 없앨지 설정
        dialog.setCanceledOnTouchOutside(false);//없어지지 않도록 설정

        //Dialog 보이기
        dialog.show();

    }


    //onStop 부분에서 progress dialog를 닫아줘야 에러가 안남
    @Override
    protected void onStop() {
        super.onStop();
        if(asyncDialog!=null){
            asyncDialog.dismiss();
            asyncDialog= null;
        }
    }

    @Override
    public void onPrimaryClipChanged() {
        //updateClipData();
        ClipData pData = clipboardManager.getPrimaryClip();
        ClipData.Item item = pData.getItemAt(0);
        String txtpaste = item.getText().toString();
        Toast.makeText(getApplicationContext(),txtpaste+"",Toast.LENGTH_SHORT).show();
    }

    private class CheckTypesTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            asyncDialog= ProgressDialog.show(
                    WordAddOCRActivity.this,null,"잠시만 기다려 주세요", true, true);
            super.onPreExecute();
        }
        @Override
        protected Void doInBackground(Void... arg0) {
            processImage();
            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            asyncDialog.dismiss();
            super.onPostExecute(result);
        }
    }


    //Process an Image
    public void processImage() {
        //이미지 없을때 에러남
        mTess.setImage(image);
        Log.e("final image", image+"");
        OCRresult = mTess.getUTF8Text();
        new Thread(){
            public void run(){
                Message msg = handler.obtainMessage();
                handler.sendMessage(msg);
            }
        }.start();

    }

    //ocr settext
    final Handler handler = new Handler(){
        public void handleMessage(Message msg){
            // 원래 하고싶었던 일들 (UI변경작업 등...)
            TextView OCRTextView = (TextView) findViewById(R.id.OCRTextView);
            OCRTextView.setText(OCRresult);
            OCRTextView.setTextIsSelectable(true);

        }
    };


    //check file on the device
    private void checkFile(File dir) {
        //디렉토리가 없으면 디렉토리를 만들고 그후에 파일을 카피
        if(!dir.exists()&& dir.mkdirs()) {
            copyFiles();
        }
        //디렉토리가 있지만 파일이 없으면 파일카피 진행
        if(dir.exists()) {
            String datafilepath = datapath+ "/tessdata/eng.traineddata";
            File datafile = new File(datafilepath);
            if(!datafile.exists()) {
                copyFiles();
            }
        }
    }

    //copy file to device
    private void copyFiles() {
        try{
            String filepath = datapath + "/tessdata/eng.traineddata";
            AssetManager assetManager = getAssets();
            InputStream instream = assetManager.open("tessdata/eng.traineddata");
            OutputStream outstream = new FileOutputStream(filepath);
            byte[] buffer = new byte[1024];
            int read;
            while ((read = instream.read(buffer)) != -1) {
                outstream.write(buffer, 0, read);
            }
            outstream.flush();
            outstream.close();
            instream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startGalleryChooser() {
        if (PermissionUtils.requestPermission(this, GALLERY_PERMISSIONS_REQUEST, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select a photo"), GALLERY_IMAGE_REQUEST);
        }
    }

    public void startCamera() {
        if (PermissionUtils.requestPermission(
                this,
                CAMERA_PERMISSIONS_REQUEST,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA)) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            Uri photoUri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", getCameraFile());
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivityForResult(intent, CAMERA_IMAGE_REQUEST);
        }
    }

    public File getCameraFile() {
        File dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return new File(dir, FILE_NAME);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            uploadImage(data.getData());
        } else if (requestCode == CAMERA_IMAGE_REQUEST && resultCode == RESULT_OK) {
            Uri photoUri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", getCameraFile());
            uploadImage(photoUri);
        }
    }

    //권한 체크 결과
    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case CAMERA_PERMISSIONS_REQUEST:
                if (PermissionUtils.permissionGranted(requestCode, CAMERA_PERMISSIONS_REQUEST, grantResults)) {
                    startCamera();
                }
                break;
            case GALLERY_PERMISSIONS_REQUEST:
                if (PermissionUtils.permissionGranted(requestCode, GALLERY_PERMISSIONS_REQUEST, grantResults)) {
                    startGalleryChooser();
                }
                break;
        }
    }

    //이미지 업로드
    public void uploadImage(Uri uri) {
        if (uri != null) {
            try {
                // scale the image to save on bandwidth
                Bitmap bitmap =
                        scaleBitmapDown(
                                MediaStore.Images.Media.getBitmap(getContentResolver(), uri),
                                MAX_DIMENSION);

//                callCloudVision(bitmap);
                mMainImage.setImageBitmap(bitmap);
                image= bitmap;

            } catch (IOException e) {
                Log.d(TAG, "Image picking failed because " + e.getMessage());
                Toast.makeText(this, R.string.image_picker_error, Toast.LENGTH_LONG).show();
            }
        } else {
            Log.d(TAG, "Image picker gave us a null image.");
            Toast.makeText(this, R.string.image_picker_error, Toast.LENGTH_LONG).show();
        }
    }

    //이미지 사이즈 조정
    private Bitmap scaleBitmapDown(Bitmap bitmap, int maxDimension) {

        int originalWidth = bitmap.getWidth();
        int originalHeight = bitmap.getHeight();
        int resizedWidth = maxDimension;
        int resizedHeight = maxDimension;

        if (originalHeight > originalWidth) {
            resizedHeight = maxDimension;
            resizedWidth = (int) (resizedHeight * (float) originalWidth / (float) originalHeight);
        } else if (originalWidth > originalHeight) {
            resizedWidth = maxDimension;
            resizedHeight = (int) (resizedWidth * (float) originalHeight / (float) originalWidth);
        } else if (originalHeight == originalWidth) {
            resizedHeight = maxDimension;
            resizedWidth = maxDimension;
        }
        image= Bitmap.createScaledBitmap(bitmap, resizedWidth, resizedHeight, false);
        Log.i("bitmap image",image+"");
        return Bitmap.createScaledBitmap(bitmap, resizedWidth, resizedHeight, false);
    }

    public void init(){
        mMainImage = findViewById(R.id.main_image);
        OCRButtonContainer= (RelativeLayout)findViewById(R.id.OCRButtonContainer);
        btnRun= (TextView)findViewById(R.id.btnRun);

        user_id= getIntent().getStringExtra("user_id");

        fab= (FloatingActionMenu)findViewById(R.id.fab);
        fabCamera = (FloatingActionButton)findViewById(R.id.fabCamera);
        fabGallery= (FloatingActionButton)findViewById(R.id.fabGallery);
        fabAddClip= (FloatingActionButton)findViewById(R.id.fabAddClip);

        //OCR 돌리는 버튼
        OCRButtonContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckTypesTask task= new CheckTypesTask();
                task.execute();
            }
        });
        //OCR 돌리는 버튼
        btnRun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckTypesTask task= new CheckTypesTask();
                task.execute();
            }
        });


    }

    private class insertMyWord extends AsyncTask<String, Void, String> {
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
                Log.d(TAG, "InsertData: Error ", e);

                return null;
            }

        }
    }



//    private static class LableDetectionTask extends AsyncTask<Object, Void, String> {
//        private final WeakReference<WordAddOCRActivity> mActivityWeakReference;
//        private Vision.Images.Annotate mRequest;
//
//        LableDetectionTask(WordAddOCRActivity activity, Vision.Images.Annotate annotate) {
//            mActivityWeakReference = new WeakReference<>(activity);
//            mRequest = annotate;
//        }
//
//        @Override
//        protected String doInBackground(Object... params) {
//            try {
//                Log.d(TAG, "created Cloud Vision request object, sending request");
//                BatchAnnotateImagesResponse response = mRequest.execute();
//                return convertResponseToString(response);
//
//            } catch (GoogleJsonResponseException e) {
//                Log.d(TAG, "failed to make API request because " + e.getContent());
//            } catch (IOException e) {
//                Log.d(TAG, "failed to make API request because of other IOException " +
//                        e.getMessage());
//            }
//            return "Cloud Vision API request failed. Check logs for details.";
//        }
//
//        protected void onPostExecute(String result) {
//            WordAddOCRActivity activity = mActivityWeakReference.get();
//            if (activity != null && !activity.isFinishing()) {
//                TextView imageDetail = activity.findViewById(R.id.image_details);
//                imageDetail.setText(result);
//            }
//        }
//    }

    //cloud vision 불러내서 사진 정보 추가하기
//    private void callCloudVision(final Bitmap bitmap) {
//        // Switch text to loading
//        mImageDetails.setText(R.string.loading_message);
//
//        // Do the real work in an async task, because we need to use the network anyway
//        try {
//            AsyncTask<Object, Void, String> labelDetectionTask = new LableDetectionTask(WordAddOCRActivity.this, prepareAnnotationRequest(bitmap));
//            labelDetectionTask.execute();
//        } catch (IOException e) {
//            Log.d(TAG, "failed to make API request because of other IOException " +
//                    e.getMessage());
//        }
//    }


    //사진에 대한 정보 message StringBuilder에 붙임
//    private static String convertResponseToString(BatchAnnotateImagesResponse response) {
//        StringBuilder message = new StringBuilder("I found these things:\n\n");
//
//        List<EntityAnnotation> labels = response.getResponses().get(0).getLabelAnnotations();
//        if (labels != null) {
//            for (EntityAnnotation label : labels) {
//                message.append(String.format(Locale.US, "%.3f: %s", label.getScore(), label.getDescription()));
//                message.append("\n");
//            }
//        } else {
//            message.append("nothing");
//        }
//
//        return message.toString();
//    }
}
