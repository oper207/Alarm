package com.example.markersaveapplication;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MemoWriteActivity extends AppCompatActivity {

    EditText et, et2;
    int position = -1;

    String userId;

    CheckBox checkSecret;
    private boolean secretChecked;

    private ImageView imageView;
    private static final int REQUEST_CODE = 0;

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    String imagePath;

    Button angryBtn, sadBtn, anxiousBtn, hurtBtn, embarrassedBtn, happyBtn;
    String RealEmotion = " ";
    TextView tv_emotion_Write, tv_link;

    Uri uri = null;
    String uriStr = "LINK";
    String newTitle;

    List<MemoData> datas;

    String id;

    private ValueEventListener postListener;
    String link_pre;
    String key;

    String format_time, title, content, id_insert;

    String storageImg;

    public MemoWriteActivity() {
    }

    //동적인 권한 획득. 갤러리 접근을 위해서 추가적으로 해줘야해.
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_EXTERNAL_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    startStorage();
                } else {
                    // Permission Denied
                    Toast.makeText(MemoWriteActivity.this, "READ_STORAGE Denied", Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.memowrite);

        SharedPreferences sp = getSharedPreferences("shared", MODE_PRIVATE);

        EditText scrollEdit = findViewById(R.id.content);
        scrollEdit.setMovementMethod(new ScrollingMovementMethod());

        angryBtn = findViewById(R.id.angryBtn);
        sadBtn = findViewById(R.id.sadBtn);
        anxiousBtn = findViewById(R.id.anxiousBtn);
        hurtBtn = findViewById(R.id.hurtBtn);
        embarrassedBtn = findViewById(R.id.embarrassedBtn);
        happyBtn = findViewById(R.id.happyBtn);




        //imageview 클릭하면 갤러리로 인텐트 넘겨주기.
        imageView = findViewById(R.id.iv_attachment);
        imageView.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                startStorageWrapper();
            }
        });


        et = (EditText) findViewById(R.id.edit);
        et2 = (EditText) findViewById(R.id.content);
        checkSecret = findViewById(R.id.checkBoxSecret);
        tv_emotion_Write = findViewById(R.id.tv_emotion_write);
        tv_link = findViewById(R.id.link);

//        //Spinner 객체 생성
//        Spinner spinner = (Spinner) findViewById(R.id.spinner_field);
//        //생성한 field_xml의 item을 String 배열로 가져오기
//        emotion = getResources().getStringArray(R.array.spinnerArray);
//
//        //spinner_item.xml과 emotion 인자로 어댑터(접속, 연결) 생성
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_item, emotion);
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spinner.setAdapter(adapter);
//
//        //Spinner 이벤트 리스너
//        spinner.setOnItemSelectedListener(this);

        Intent intent = getIntent();
        if (intent.hasExtra("POSITION")) {
            //얘는 adapter에서 보내주는 친구
            position = intent.getIntExtra("POSITION", -1);
            id = intent.getStringExtra("ID");
            //userId = intent.getExtras().getString("CheckID");
        } else if(intent.hasExtra("URI")){
            uri = intent.getParcelableExtra("URI");
            newTitle = intent.getStringExtra("TITLE");
        }

        String strUserId = sp.getString("checkId", "");
        userId = strUserId;

        if(uri != null){
            tv_link.setText(uri.toString());
            et2.setText("["+newTitle+ "] 을 읽고....");
        }

        //수정시에는 쉐어드에서 데이터 받아와서 보여주기.
        if (position != -1) {
            //데이터를 받아와서 음... position이 아니라 해당 id값을 받아오는거야.
            //그래서 id값을 비교해서 해당 아이디면 그 자료의 값들을 가져오는



        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void startStorageWrapper() {
        int permission = ActivityCompat.checkSelfPermission(MemoWriteActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            requestPermissions(PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
            return;
        }
        startStorage();
    }

    public void startStorage() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");      //암시적 인텐트의 type설정해준거. image인 얘들만 받겠다.
        startActivityForResult(intent, REQUEST_CODE);
    }




//    //Spinner 이벤트 리스너 구체화
//    @Override
//    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//        emotionSelected = emotion[position];
//    }
//
//    //spinner에서 항목 선택 안 했을때 처리
//    @Override
//    public void onNothingSelected(AdapterView<?> parent) {
//    }




    }



