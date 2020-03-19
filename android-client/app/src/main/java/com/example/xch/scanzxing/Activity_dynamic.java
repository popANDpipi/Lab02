package com.example.xch.scanzxing;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


import com.google.gson.Gson;


//根据json文件动态生成界面，存储数据并上传

public class Activity_dynamic extends AppCompatActivity {

    //private String str_json="{\"id\":\"12344134\",\"pwd\":\"0124678\",\"questions\":[{\"type\":\"single\",\"question\":\"How well do the professors teach at this university?\",\"options\":[\"Extremely well\",\"Very well\"]},{\"type\":\"single\",\"question\":\"How effective is the teaching outside yur major at the univesrity?\",\"options\":[\"Extremetly effective\",\"Very effective\",\"Somewhat effective\",\"Not so effective\",\"Not at all effective\"]},{\"type\":\"multiple\",\"question\":\"Multiple?\",\"options\":[\"AAA\",\"BBB\"]},{\"type\":\"edit\",\"question\":\"Edit?\",\"options\":[]}]}";
    private String str_json;
    MySQLite mysql = new MySQLite(this);
    static int question_index = 0;
    static int Viewid = 0;
    RelativeLayout rootlayout;
    TextView text_title, text_question;
    Button btn;
    RadioGroup rg;
    List<Integer> itemlist;
    List<String> option_index;
    String str_json_output = "";
    File jsondata_internal;
    Questionnaire questionnaire;
    Questionnaire.Response res;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dynamic);
        MainActivity.activityList.add(this);

        Intent intent = getIntent();
        str_json = intent.getStringExtra("json");

        jsondata_internal = new File(Activity_dynamic.this.getFilesDir(),"results.json");
        Gson gson = new Gson();
        questionnaire = gson.fromJson(str_json, Questionnaire.class);
        res = new Questionnaire.Response();
        questionnaire.response = res;
        questionnaire.response.ResponseID = randomstr();
        option_index = new ArrayList<String>();
        init();
    }

    @SuppressLint("ResourceType")
    void init() {
        itemlist = new ArrayList<Integer>();
        rootlayout = new RelativeLayout(this);
        rootlayout.setLayoutParams(new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
        rootlayout.setId(Viewid++);

        try{
            //Set title
            if(question_index < questionnaire.questions.size()){
                text_title = new TextView(this);
                RelativeLayout.LayoutParams text_title_Params = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);
                text_title_Params.setMargins(dip2px(30), 0, dip2px(30), 0);//left top right bottom
                text_title.setLayoutParams(text_title_Params);
                text_title.setText("Question " + String.valueOf(question_index + 1));
                text_title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);
                text_title.setTextColor(Color.BLACK);
                text_title.setId(Viewid++);
                //set Questions
                text_question = new TextView(this);
                RelativeLayout.LayoutParams text_question_Params = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);
                text_question_Params.addRule(RelativeLayout.BELOW, text_title.getId());
                text_question_Params.setMargins(dip2px(30), dip2px(30), dip2px(30), 0);
                text_question.setLayoutParams(text_question_Params);
                text_question.setText(questionnaire.questions.get(question_index).question);
                text_question.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
                text_question.setId(Viewid++);

                //Single
                if(questionnaire.questions.get(question_index).type == Questionnaire.QuestionType.single){
                    rg = new RadioGroup(this);
                    RelativeLayout.LayoutParams rg_Params = new RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.WRAP_CONTENT,
                            RelativeLayout.LayoutParams.WRAP_CONTENT);
                    rg_Params.addRule(RelativeLayout.BELOW, text_question.getId());
                    rg.setId(Viewid++);

                    for(int j = 0; j< questionnaire.questions.get(question_index).options.size(); j++){
                        RadioButton btn = new RadioButton(this);
                        RelativeLayout.LayoutParams btn_Params = new RelativeLayout.LayoutParams(
                                RelativeLayout.LayoutParams.WRAP_CONTENT,
                                RelativeLayout.LayoutParams.WRAP_CONTENT);
                        btn_Params.setMargins(dip2px(50), 0, dip2px(50), 0);

                        btn.setText(questionnaire.questions.get(question_index).options.get(j));
                        btn.setId(Viewid++);
                        itemlist.add(Viewid - 1);
                        rg.addView(btn,btn_Params);
                    }
                    rootlayout.addView(rg, rg_Params);
                }
                //multiple
                else if(questionnaire.questions.get(question_index).type == Questionnaire.QuestionType.multiple){
                    rg = new RadioGroup(this);
                    RelativeLayout.LayoutParams rg_Params = new RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.WRAP_CONTENT,
                            RelativeLayout.LayoutParams.WRAP_CONTENT);
                    rg_Params.addRule(RelativeLayout.BELOW, text_question.getId());
                    rg.setId(Viewid++);

                    for(int j = 0; j< questionnaire.questions.get(question_index).options.size(); j++){
                        CheckBox cb = new CheckBox(this);
                        RelativeLayout.LayoutParams cb_Params = new RelativeLayout.LayoutParams(
                                RelativeLayout.LayoutParams.WRAP_CONTENT,
                                RelativeLayout.LayoutParams.WRAP_CONTENT);
                        cb_Params.setMargins(dip2px(50), 0, dip2px(50), 0);

                        cb.setText(questionnaire.questions.get(question_index).options.get(j));
                        cb.setId(Viewid++);
                        itemlist.add(Viewid-1);
                        rg.addView(cb,cb_Params);
                    }
                    rootlayout.addView(rg, rg_Params);
                }else{
                    EditText et = new EditText(this);
                    RelativeLayout.LayoutParams et_Params = new RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.MATCH_PARENT,
                            RelativeLayout.LayoutParams.WRAP_CONTENT);
                    et_Params.setMargins(dip2px(50), 0, dip2px(50), 0);
                    et_Params.addRule(RelativeLayout.BELOW, text_question.getId());
                    et.setId(Viewid++);
                    itemlist.add(Viewid-1);
                    rootlayout.addView(et,et_Params);
                }

                //set Button
                btn = new Button(this);
                RelativeLayout.LayoutParams btn_Params = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.MATCH_PARENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT
                );
                btn_Params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                btn_Params.setMargins(dip2px(30), 0, dip2px(30), dip2px(10));
                btn.setLayoutParams(btn_Params);
                if(question_index<questionnaire.questions.size()-1)
                    btn.setText("NEXT");
                else
                    btn.setText("SAVE AND FINISH");
                btn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
                btn.setBackgroundColor(Color.parseColor("#009900"));
                btn.setId(Viewid++);
                btn.setOnClickListener(listener);

                rootlayout.addView(text_title, text_title_Params);
                rootlayout.addView(text_question, text_question_Params);
                rootlayout.addView(btn, btn_Params);
                setContentView(rootlayout);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static String getJson(String fileName, Context context) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            AssetManager assetManager = context.getAssets();
            BufferedReader bf = new BufferedReader(new InputStreamReader(assetManager.open(fileName)));
            String line;
            while ((line = bf.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }


    public int dip2px(float dpValue) {
        final float scale = this.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public String randomstr() {
        String dic = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        //指定字符串长度，拼接字符并toString
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < 10; i++) {
            //获取指定长度的字符串中任意一个字符的索引值
            int number = random.nextInt(dic.length());
            //根据索引值获取对应的字符
            char charAt = dic.charAt(number);
            sb.append(charAt);
        }
        return sb.toString();
    }

    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Boolean notnull = false;

            if(questionnaire.questions.get(question_index).type == Questionnaire.QuestionType.single){
                RadioButton temp;
                for(int i=0;i<itemlist.size();i++){
                    temp=findViewById(itemlist.get(i));
                    if(temp.isChecked()){
                        notnull = true;
                        option_index.add(String.valueOf(i+1));
                        break;
                    }
                }
                if(!notnull){
                    Toast.makeText(getApplicationContext(),"Please select one item",Toast.LENGTH_LONG).show();
                    return;
                }
                //Save result
                temp = findViewById(rg.getCheckedRadioButtonId());
                questionnaire.response.answer.add(new Questionnaire.Answer());
                questionnaire.response.answer.get(question_index).AnswerKey = randomstr();
                questionnaire.response.answer.get(question_index).content = temp.getText().toString();
                questionnaire.response.answer.get(question_index).QuestionNum = question_index+1;
            } else if(questionnaire.questions.get(question_index).type == Questionnaire.QuestionType.multiple) {
                CheckBox temp;
                for(int i=0;i<itemlist.size();i++){
                    temp=findViewById(itemlist.get(i));
                    if(temp.isChecked()){
                        notnull = true;
                        break;
                    }
                }
                if(!notnull){
                    Toast.makeText(getApplicationContext(),"Please select one item",Toast.LENGTH_LONG).show();
                    return;
                }
                questionnaire.response.answer.add(new Questionnaire.Answer());
                questionnaire.response.answer.get(question_index).AnswerKey = randomstr();
                //Save result
                String multiple_index = "";
                for(int i=0;i<itemlist.size();i++){
                    temp=findViewById(itemlist.get(i));

                    if(temp.isChecked()){
                        questionnaire.response.answer.get(question_index).content += temp.getText().toString();
                        questionnaire.response.answer.get(question_index).content += " ";
                        multiple_index+=String.valueOf(i+1);
                        multiple_index+=" ";
                    }
                }
                option_index.add(multiple_index);
                questionnaire.response.answer.get(question_index).QuestionNum = question_index+1;
            }else{
                EditText temp = findViewById(itemlist.get(0));
                if(temp.getText().toString().equals("")){
                    Toast.makeText(getApplicationContext(),"Please fill in",Toast.LENGTH_LONG).show();
                    return;
                }
                option_index.add(temp.getText().toString());
                questionnaire.response.answer.add(new Questionnaire.Answer());
                questionnaire.response.answer.get(question_index).AnswerKey = randomstr();
                questionnaire.response.answer.get(question_index).content = temp.getText().toString();
                questionnaire.response.answer.get(question_index).QuestionNum = question_index+1;
            }

            //Have not reached the last question
            if(question_index< questionnaire.questions.size()-1){
                rootlayout.removeAllViews();
                question_index++;
                init();
            } else {
                //Log the results

                //TimeStamp etc. HERE
                questionnaire.response.Latitude=get_location("Latitude");
                questionnaire.response.Longitude=get_location("Longitude");
                questionnaire.response.TimeStamp = get_time();
                questionnaire.response.IMEI=get_IMEI();

                String json_upload = "{\"id\":\"" +
                        questionnaire.id +
                        "\",\"time\":" +
                        String.valueOf(questionnaire.response.TimeStamp) +
                        ",\"Longitude\":" +
                        String.valueOf(questionnaire.response.Longitude) +
                        ",\"Latitude\":" +
                        String.valueOf(questionnaire.response.Latitude) +
                        ",\"IMEI\":\"" +
                        questionnaire.response.IMEI +
                        "\",\"answers\":[";

                String str_ResponseID = questionnaire.response.ResponseID;
                long bigint_TimeStamp = questionnaire.response.TimeStamp;
                long bigint_Longitude = questionnaire.response.Longitude;
                long bigint_Latitude = questionnaire.response.Latitude;
                String str_IMEI = questionnaire.response.IMEI;
                String str_RefsurveyID = questionnaire.id;
                SQLiteDatabase db = mysql.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put("ResponseID",str_ResponseID);
                values.put("TimeStamp",bigint_TimeStamp);
                values.put("Longitude",bigint_Longitude);
                values.put("Latitude",bigint_Latitude);
                values.put("IMEI",str_IMEI);
                values.put("RefsurveyID",str_RefsurveyID);
                db.insert("Response",null,values);
                db.close();

                for(int i=0;i<questionnaire.response.answer.size();i++){
                    String str_AnswerKey = questionnaire.response.answer.get(i).AnswerKey;
                    int int_QuestionNum = questionnaire.response.answer.get(i).QuestionNum;
                    String str_Content = questionnaire.response.answer.get(i).content;
                    String str_RefResponseID = questionnaire.response.ResponseID;
                    db = mysql.getWritableDatabase();
                    ContentValues values_answer = new ContentValues();
                    values_answer.put("AnswerKey",str_AnswerKey);
                    values_answer.put("QuestionNum",int_QuestionNum);
                    values_answer.put("Content",str_Content);
                    values_answer.put("RefResponseID",str_RefResponseID);
                    db.insert("Answer",null,values_answer);
                    db.close();

                    json_upload+="\""+option_index.get(i)+"\"";
                    if(i!=questionnaire.response.answer.size()-1)
                        json_upload+=",";
                }
                json_upload+="]}";

/*
                for (Activity a : MainActivity.allactivities) {
                    a.finish();
                }
 */
                String password=questionnaire.pwd;
                Intent intent = new Intent();
                intent.setClass(Activity_dynamic.this, GestureLock.class);
                intent.putExtra("password",password);
                intent.putExtra("jsonstring",json_upload);
                startActivity(intent);
                //Toast.makeText(Activity_dynamic.this, json_upload, Toast.LENGTH_SHORT).show();
            }
        }
    };

    //获取经纬度
    public long get_location(String str) {

        LocationManager locationmanager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return 0;
        }
        locationmanager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 10, locationListener);

        Location location = locationmanager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        double lat = 0.0;
        double lng = 0.0;
        if (location != null) {
            lat = location.getLatitude();
            lng = location.getLongitude();
        }

        if(str.equals("Longitude"))
            return new Double(lng).longValue();
        else if(str.equals("Latitude"))
            return new Double(lat).longValue();
        else
            return 0;
    }
    final LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            //Toast.makeText(password.this, String.valueOf(location.getLatitude()), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    //获取时间
    @RequiresApi(api = Build.VERSION_CODES.O)
    public long get_time()
    {
        long now = Instant.now().toEpochMilli();
        return now;
    }

    //获取IMEI,AndroidQ以上会抛异常
    @RequiresApi(api = Build.VERSION_CODES.M)
    public String get_IMEI()
    {
        String []imei = {"null","null"};

        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        for (int slot = 0; slot < tm.getPhoneCount(); slot++) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return null;
            }
            else {
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        imei[slot] = tm.getImei(slot);
                    }
                } catch (Exception e) {
                    imei[slot] = null;
                }
            }
        }
        return imei[0]+","+imei[1];
    }
}
