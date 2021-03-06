package com.example.kirokhada.Board.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import com.example.kirokhada.Board.data.WriteInfo;
import com.example.kirokhada.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class WritePostActivitybyCamera extends AppCompatActivity {

    // true  : Camera On  : 카메라로 직접 찍어 문자 인식
    // false : Camera Off : 샘플이미지를 로드하여 문자 인식
    private boolean CameraOnOffFlag = true;

    private TessBaseAPI m_Tess; //Tess API reference
    private ProgressCircleDialog m_objProgressCircle = null; // 원형 프로그레스바
    private MessageHandler m_messageHandler;

    private String mDataPath = ""; //언어데이터가 있는 경로
    private String mCurrentPhotoPath; // 사진 경로
    private final String[] mLanguageList = {"eng","kor"}; // 언어
    // View
    private Context mContext;
    private TextView m_ocrTextView; // 결과 변환 텍스트
    private Bitmap image; //사용되는 이미지


    private boolean ProgressFlag = false; // 프로그레스바 상태 플래그

    EditText titleEditText;
    EditText authorEditText;
    EditText ratingEditText;
    EditText keywordEditText;
    EditText contentEditText;

    // View
    Button upload_btn;
    Switch type_switch;

    // DB
    String email = null;
    String userID = null;

    // EditText에서 가져온 데이터
    String title, author, rating, keyword, content = null;

    // 체크 데이터
    String uploadTimeText = "";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_post);

        mContext = this;

        titleEditText = findViewById(R.id.bookTitle_editText);

        m_objProgressCircle = new ProgressCircleDialog(this); // 프로그래스 바---> 원형 모양 만들기 this --> MainActivity.xml
        m_messageHandler = new MessageHandler();

        if(CameraOnOffFlag)
        {
            dispatchTakePictureIntent();
        }
        else
        {
            processImage();
        }

        if(CameraOnOffFlag)
        {
            PermissionCheck(); //버전 체크 후 권한 수락 맡기
            Tesseract();    //언어 데이터 파일 가져오기
        }

        init();
        buttonListener();
    }

    private void buttonListener() {
        upload_btn.setOnClickListener(view -> {
            getData();

            if (title != null && author != null && rating != null && keyword != null && content != null) {
                uploadData();
                finish();
            } else {
                Toast showText = Toast.makeText(getApplicationContext(), "내용을 다 채워주세요~", Toast.LENGTH_SHORT);
                showText.show();
            }
        });
    }


    private void init() {

        upload_btn = findViewById(R.id.upload_button);
        type_switch = findViewById(R.id.type_switch);

        authorEditText = findViewById(R.id.author_editText);
        ratingEditText = findViewById(R.id.rating_editText);
        keywordEditText = findViewById(R.id.keyword_editText);

        contentEditText = findViewById(R.id.content_Edit);

    }

    private void getData() {

        title = titleEditText.getText().toString();
        author = authorEditText.getText().toString();
        rating = ratingEditText.getText().toString();
        keyword = keywordEditText.getText().toString();
        content = contentEditText.getText().toString();

        Date uploadTime = new Date(System.currentTimeMillis());

        @SuppressLint("SimpleDateFormat") SimpleDateFormat mFormat = new SimpleDateFormat("MM/dd HH:mm:ss");

        uploadTimeText = mFormat.format(uploadTime);
    }

    private void uploadData() {
        FirebaseUser auth = FirebaseAuth.getInstance().getCurrentUser();

        if (auth != null) {
            userID = auth.getUid();
            email = auth.getEmail();
            if(!type_switch.isChecked()){
                Log.d("test", "1");
                dbUploadData();
            }else {
                dbUploadDataPrivate();
                Log.d("test", "2");
            }

        }
    }

    private void dbUploadData() {
        FirebaseFirestore fireStore = FirebaseFirestore.getInstance();
        String status = "public";
        String sc = getRandomString();
        WriteInfo data = new WriteInfo(title, author, rating, keyword, content, email, uploadTimeText, userID, status, sc);

        fireStore.collection("book").document(sc).set(data)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(getApplicationContext(), "업로드 성공!", Toast.LENGTH_LONG).show();
                        finish();
                    }
                });
    }

    private void dbUploadDataPrivate(){
        FirebaseFirestore fireStore = FirebaseFirestore.getInstance();
        String status = "private";
        String sc = getRandomString();
        WriteInfo data = new WriteInfo(title, author, rating, keyword, content, email, uploadTimeText, userID, status, sc);

        fireStore.collection("book").document(sc).set(data)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(getApplicationContext(), "업로드 성공!", Toast.LENGTH_LONG).show();
                        finish();
                    }
                });
    }

    private String getRandomString() {
        StringBuffer temp = new StringBuffer();
        Random rnd = new Random();
        for (int i = 0; i < 20; i++) {
            int rIndex = rnd.nextInt(3);
            switch (rIndex) {
                case 0:
                    // a-z
                    temp.append((char) ((int) (rnd.nextInt(26)) + 97));
                    break;
                case 1:
                    // A-Z
                    temp.append((char) ((int) (rnd.nextInt(26)) + 65));
                    break;
                case 2:
                    // 0-9
                    temp.append((rnd.nextInt(10)));
                    break;
            }
        }

        return temp.toString();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case ConstantDefine.PERMISSION_CODE:
                Toast.makeText(this, "권한이 허용되었습니다.", Toast.LENGTH_SHORT).show();
                break;
            case ConstantDefine.ACT_TAKE_PIC:
                //카메라로 찍은 사진을 받는다.
                if ((resultCode == RESULT_OK) ) {

                    try {
                        File file = new File(mCurrentPhotoPath);
                        Bitmap rotatedBitmap = null;  //Bitmap android 에서 이미지를 표현하기 위한 클래스

                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),
                                FileProvider.getUriForFile(WritePostActivitybyCamera.this, //이미지
                                        getApplicationContext().getPackageName() + ".fileprovider", file));

                        // 회전된 사진을 원래대로 돌려 표시한다.
                        if (bitmap != null) {
                            ExifInterface ei = new ExifInterface(mCurrentPhotoPath);
                            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                                    ExifInterface.ORIENTATION_UNDEFINED);
                            switch (orientation) {

                                case ExifInterface.ORIENTATION_ROTATE_90:
                                    rotatedBitmap = rotateImage(bitmap, 90);
                                    break;

                                case ExifInterface.ORIENTATION_ROTATE_180:
                                    rotatedBitmap = rotateImage(bitmap, 180);
                                    break;

                                case ExifInterface.ORIENTATION_ROTATE_270:
                                    rotatedBitmap = rotateImage(bitmap, 270);
                                    break;

                                case ExifInterface.ORIENTATION_NORMAL:
                                default:
                                    rotatedBitmap = bitmap;
                            }
                            OCRThread ocrThread = new OCRThread(rotatedBitmap);
                            ocrThread.setDaemon(true);
                            ocrThread.start();
                            m_ocrTextView.setText(getResources().getString(R.string.LoadingMessage)); //인식된텍스트 표시
                        }
                    } catch (Exception e) {
                    }
                }
                break;
        }
    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }

    public void PermissionCheck() {
        /**
         * 6.0 마시멜로우 이상일 경우에는 권한 체크후 권한을 요청한다.
         */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED &&
                    checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED &&
                    checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {

                // 권한 없음
                ActivityCompat.requestPermissions(WritePostActivitybyCamera.this,
                        new String[]{Manifest.permission.CAMERA,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        ConstantDefine.PERMISSION_CODE);
            } else {
                // 권한 있음
            }
        }
    }


    public void Tesseract() {
        //언어파일 경로
        mDataPath = getFilesDir() + "/tesseract/";

        //트레이닝데이터가 카피되어 있는지 체크
        String lang = "";
        for (String Language : mLanguageList) {
            checkFile(new File(mDataPath + "tessdata/"), Language);
            lang += Language + "+";
        }

        //lang = eng+korea+

        m_Tess = new TessBaseAPI();
        m_Tess.init(mDataPath, lang);
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    /**
     * 기본카메라앱을 실행 시킨다.
     */
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // 사진파일을 생성한다.
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // 사진파일이 정상적으로 생성되었을때
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        this.getApplicationContext().getPackageName()+".fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, ConstantDefine.ACT_TAKE_PIC);
            }
        }
    }



    //copy file to device
    private void copyFiles(String Language) {
        try {
            String filepath = mDataPath + "/tessdata/" + Language + ".traineddata";
            AssetManager assetManager = getAssets();
            InputStream instream = assetManager.open("tessdata/"+Language+".traineddata");
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

    //check file on the device
    private void checkFile(File dir, String Language) {
        //디렉토리가 없으면 디렉토리를 만들고 그후에 파일을 카피
        if (!dir.exists() && dir.mkdirs()) {
            copyFiles(Language);
        }
        //디렉토리가 있지만 파일이 없으면 파일카피 진행
        if (dir.exists()) {
            String datafilepath = mDataPath + "tessdata/" + Language + ".traineddata";
            File datafile = new File(datafilepath);
            if (!datafile.exists()) {
                copyFiles(Language);
            }
        }
    }

    //region Thread
    public class OCRThread extends Thread
    {
        private Bitmap rotatedImage;

        OCRThread(Bitmap rotatedImage)
        {
            this.rotatedImage = rotatedImage;
            if(!ProgressFlag)
                m_objProgressCircle = ProgressCircleDialog.show(mContext, "", "", true);
            ProgressFlag = true;
        }

        @Override
        public void run() {
            super.run();
            // 사진의 글자를 인식해서 옮긴다
            String OCRresult = null;
            m_Tess.setImage(rotatedImage);
            OCRresult = m_Tess.getUTF8Text(); //실질적인 ocr 부분

            Message message = Message.obtain();
            message.what = ConstantDefine.RESULT_OCR;
            message.obj = OCRresult;
            m_messageHandler.sendMessage(message);

        }
    }
    //endregion

    //region Handler
    public class MessageHandler extends Handler
    {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);


            switch (msg.what)
            {
                case ConstantDefine.RESULT_OCR:  //ocr 이 제대로 이루어 졌다면
                    TextView OCRTextView = findViewById(R.id.bookTitle_editText);

                    OCRTextView.setText(String.valueOf(msg.obj).replaceAll("\\n", " ")); //텍스트 변경 ---> ocr 기능으로 변경된 텍스트 출력

                    //원형 프로그레스바 종료
                    if(m_objProgressCircle.isShowing() && m_objProgressCircle !=null)
                        m_objProgressCircle.dismiss();

                    ProgressFlag = false;
                    Toast.makeText(mContext,getResources().getString(R.string.CompleteMessage),Toast.LENGTH_SHORT).show(); //처리하는데 걸린 시간 출력
                    break;
            }
        }
    }
    //endregion

    //Process an Image
    public void processImage() {
        OCRThread ocrThread = new OCRThread(image);
        ocrThread.setDaemon(true);
        ocrThread.start();
    }
}

