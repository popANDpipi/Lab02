package com.example.xch.scanzxing;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.xch.scanzxing.zxing.android.CaptureActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

//扫码或输入链接，获得json文件
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String DECODED_CONTENT_KEY = "codedContent";
    private static final String DECODED_BITMAP_KEY = "codedBitmap";
    private static final int REQUEST_CODE_SCAN = 0x0000;

    private final int SUCCESS = 1;
    private final int FAILURE = 0;
    private final int ERRORCODE = 2;
    protected String weatherResult;

    private ImageButton btn_scan;
    private EditText tv_scanResult;
    private Button btn_next;

    public static List<Activity> activityList = new LinkedList();

    public static String path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MainActivity.activityList.add(this);
        tv_scanResult = (EditText) findViewById(R.id.tv_scanResult);
        btn_scan = (ImageButton) findViewById(R.id.go_to_web);
        btn_scan.setOnClickListener(this);
        btn_next=(Button) findViewById(R.id.btn_next1);
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.go_to_web:
                //动态权限申请

                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA,Manifest.permission.READ_PHONE_STATE,Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                } else {
                    goScan();
                }
                break;
            default:
                break;
        }
    }

    /**
     * 跳转到扫码界面扫码
     */
    private void goScan(){
        Intent intent = new Intent(MainActivity.this, CaptureActivity.class);
        startActivityForResult(intent, REQUEST_CODE_SCAN);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    goScan();
                } else {
                    Toast.makeText(this, "You have rejected the permission application. You may not be able to open the camera to scan the code!", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 扫描二维码/条码回传
        if (requestCode == REQUEST_CODE_SCAN && resultCode == RESULT_OK) {
            if (data != null) {
                //返回的文本内容
                String content = data.getStringExtra(DECODED_CONTENT_KEY);
                //返回的BitMap图像
                Bitmap bitmap = data.getParcelableExtra(DECODED_BITMAP_KEY);
                tv_scanResult.setText(content);
            }
        }
    }
    public void Next(View view) {
        String content=tv_scanResult.getText().toString();
        /* 点击按钮事件，在主线程中开启一个子线程进行网络请求
         * （因为在4.0只有不支持主线程进行网络请求，所以一般情况下，建议另开启子线程进行网络请求等耗时操作）。
         * 因为要有异常处理要求，于是采用了线程处理
         */
        new Thread() {
            public void run() {
                int code;
                try {
                    path = tv_scanResult.getText().toString();;
                    URL url = new URL(path);
                    /**
                     * 这里网络请求使用的是类HttpURLConnection，另外一种可以选择使用类HttpClient。
                     */
                    HttpURLConnection conn = (HttpURLConnection) url
                            .openConnection();
                    conn.setRequestMethod("GET");//使用GET方法获取
                    conn.setConnectTimeout(5000);
                    code = conn.getResponseCode();
                    if (code == 200) {
                        /**
                         * 如果获取的code为200，则证明数据获取是正确的。
                         */
                        InputStream is = conn.getInputStream();
                        String result = HttpUtils.readMyInputStream(is);

                        /**
                         * 子线程发送消息到主线程，并将获取的结果带到主线程，让主线程来更新UI。
                         */
                        Message msg = new Message();
                        /////////////////////////////////////////////////////////////////////////////////
                        msg.obj = result;//这个就是json数据了，使用的时候记得toString()转字符串进行传递。/////
                        /////////////////////////////////////////////////////////////////////////////////
                        msg.what = SUCCESS;
                        //handler.sendMessage(msg);

                        Intent intent=new Intent();
                        intent.setClass(MainActivity.this, welcome.class);
                        intent.putExtra("json",msg.obj.toString());
                        startActivity(intent);


                    } else {

                        Message msg = new Message();
                        msg.what = ERRORCODE;
                        handler.sendMessage(msg);
                    }
                } catch (Exception e) {

                    e.printStackTrace();
                    /**
                     * 如果获取失败，或出现异常，那么子线程发送失败的消息（FAILURE）到主线程，主线程显示Toast，来告诉使用者，数据获取是失败。
                     */
                    Message msg = new Message();
                    msg.what = FAILURE;
                    handler.sendMessage(msg);
                }
            };
        }.start();
    }
    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SUCCESS:
                    /**
                     * 获取信息成功后，对该信息进行JSON解析，得到所需要的信息，然后在textView上展示出来。
                     */
                    //JSONAnalysis(msg.obj.toString());
                    Toast.makeText(MainActivity.this, "Data acquisition successful.", Toast.LENGTH_SHORT)
                            .show();
                    //下面是检测代码，检测完成后可以注释掉
                    Toast.makeText(MainActivity.this,"The Json String is"+msg.obj.toString(),Toast.LENGTH_SHORT).show();
                    break;

                case FAILURE:
                    Toast.makeText(MainActivity.this, "Failed to get data.", Toast.LENGTH_SHORT)
                            .show();
                    break;

                case ERRORCODE:
                    Toast.makeText(MainActivity.this, "The CODE NUMBER is not 200！",
                            Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };

    //json解析函数，如果需要可以在这里写解析式子
    protected void JSONAnalysis(String string) {
        JSONObject object = null;
        try {
            object = new JSONObject(string);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //在这里进行json数据解析：
    }

    public static void exit()
    {

        for(Activity act:activityList)
        {
            act.finish();
        }

        System.exit(0);

    }
}
