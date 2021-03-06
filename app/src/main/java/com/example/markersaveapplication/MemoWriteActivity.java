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

    //????????? ?????? ??????. ????????? ????????? ????????? ??????????????? ????????????.
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




        //imageview ???????????? ???????????? ????????? ????????????.
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

//        //Spinner ?????? ??????
//        Spinner spinner = (Spinner) findViewById(R.id.spinner_field);
//        //????????? field_xml??? item??? String ????????? ????????????
//        emotion = getResources().getStringArray(R.array.spinnerArray);
//
//        //spinner_item.xml??? emotion ????????? ?????????(??????, ??????) ??????
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_item, emotion);
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spinner.setAdapter(adapter);
//
//        //Spinner ????????? ?????????
//        spinner.setOnItemSelectedListener(this);

        Intent intent = getIntent();
        if (intent.hasExtra("POSITION")) {
            //?????? adapter?????? ???????????? ??????
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
            et2.setText("["+newTitle+ "] ??? ??????....");
        }

        //??????????????? ??????????????? ????????? ???????????? ????????????.
        if (position != -1) {
            //???????????? ???????????? ???... position??? ????????? ?????? id?????? ??????????????????.
            //????????? id?????? ???????????? ?????? ???????????? ??? ????????? ????????? ????????????



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
        intent.setType("image/*");      //????????? ???????????? type???????????????. image??? ????????? ?????????.
        startActivityForResult(intent, REQUEST_CODE);
    }




//    //Spinner ????????? ????????? ?????????
//    @Override
//    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//        emotionSelected = emotion[position];
//    }
//
//    //spinner?????? ?????? ?????? ??? ????????? ??????
//    @Override
//    public void onNothingSelected(AdapterView<?> parent) {
//    }




    }



