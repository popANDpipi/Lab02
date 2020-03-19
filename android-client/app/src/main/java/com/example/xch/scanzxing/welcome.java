package com.example.xch.scanzxing;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;

import android.annotation.SuppressLint;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


//过渡的welcome界面

public class welcome extends AppCompatActivity {

    CheckBox c;
    Button btn;
    String json;

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        MainActivity.activityList.add(this);

        //若采用链接则申请权限
        if (ContextCompat.checkSelfPermission(welcome.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(welcome.this, new String[]{Manifest.permission.READ_PHONE_STATE,Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

        Intent intent = getIntent();
        json = intent.getStringExtra("json");

        c=(CheckBox) findViewById(R.id.checkbox0);
        btn=(Button)findViewById(R.id.btn0_1);
        c.setOnCheckedChangeListener(listener);
    }

    CompoundButton.OnCheckedChangeListener listener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if(c.isChecked())
                btn.setVisibility(View.VISIBLE);
            else
                btn.setVisibility(View.GONE);
        }
    };

    public void clicked0(View view){

        //下一个页面是Activity_dynamic
        Intent intent=new Intent();
        intent.setClass(welcome.this, Activity_dynamic.class);
        intent.putExtra("json",json);
        startActivity(intent);
    }
}
