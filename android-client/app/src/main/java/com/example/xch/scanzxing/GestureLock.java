package com.example.xch.scanzxing;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.wangnan.library.GestureLockView;
import com.wangnan.library.listener.OnGestureLockListener;
import com.wangnan.library.model.Point;
import com.wangnan.library.painter.AliPayPainter;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

//解锁界面

public class GestureLock extends AppCompatActivity {

    private final int SUCCESS = 1;
    private final int FAILURE = 0;
    private final int ERRORCODE = 2;

    //禁用返回键
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            //do something.
            return true;
        } else {
            return super.dispatchKeyEvent(event);
        }
    }

    Button submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gesture);
        MainActivity.activityList.add(this);

        Intent intent = getIntent();
        //从json文件获得的密码,与要上传的json
        final String password = intent.getStringExtra("password");
        final String jsonstring=intent.getStringExtra("jsonstring");
        final String path=MainActivity.path;;

        update(jsonstring,path);

        TextView text=findViewById(R.id.tx1);
        text.setText("password："+password);
        submit=(Button)findViewById(R.id.btn_sub);

        //手势解锁以及监听器和绘制风格
        final GestureLockView mGestureLockView = (GestureLockView) findViewById(R.id.g_lock);
        mGestureLockView.setGestureLockListener(new OnGestureLockListener() {
            @Override
            public void onStarted() {

            }

            @Override
            public void onProgress(String progress) {

            }

            @Override
            public void onComplete(String result) {
                if (TextUtils.isEmpty(result)) {
                    return;
                }
                if (password.equals(result)) {
                    //Toast.makeText(GestureLock.this, jsonstring, Toast.LENGTH_SHORT).show();
                    //Toast.makeText(GestureLock.this, path, Toast.LENGTH_SHORT).show();
                    Toast.makeText(GestureLock.this, "unlock success", Toast.LENGTH_SHORT).show();
                    submit.setVisibility(View.VISIBLE);
                    mGestureLockView.showErrorStatus(600);
                } else {
                    Toast.makeText(GestureLock.this, "please check password", Toast.LENGTH_SHORT).show();
                    //Toast.makeText(GestureLock.this, get_location(), Toast.LENGTH_SHORT).show();
                    //Toast.makeText(GestureLock.this, get_time(), Toast.LENGTH_SHORT).show();
                    //Toast.makeText(GestureLock.this, get_IMEI(), Toast.LENGTH_SHORT).show();
                    mGestureLockView.showErrorStatus(600);
                }
            }
        });
        mGestureLockView.setPainter(new NiCaiFu360Painter());
    }

    //绘制风格
    class NiCaiFu360Painter extends AliPayPainter {
        @Override
        public void drawNormalPoint(Point point, Canvas canvas, Paint normalPaint) {
            // 1.绘制实心圆
            canvas.drawCircle(point.x, point.y, point.radius / 3.0F, normalPaint);
        }

        /**
         * 绘制按下状态的点
         *
         * @param point      单位点
         * @param canvas     画布
         * @param pressPaint 按下状态画笔
         */
        @Override
        public void drawPressPoint(Point point, Canvas canvas, Paint pressPaint) {
            // 1.改变透明度绘制外层实心圆
            pressPaint.setAlpha(32);
            canvas.drawCircle(point.x, point.y, point.radius, pressPaint);
            // 2.还原透明度绘制内存实心圆
            pressPaint.setAlpha(255);
            canvas.drawCircle(point.x, point.y, point.radius / 3.0F, pressPaint);
        }

        /**
         * 绘制出错状态的点
         *
         * @param point      单位点
         * @param canvas     画布
         * @param errorPaint 错误状态画笔
         */
        @Override
        public void drawErrorPoint(Point point, Canvas canvas, Paint errorPaint) {
            // 1.改变透明度绘制外层实心圆
            errorPaint.setAlpha(32);
            canvas.drawCircle(point.x, point.y, point.radius, errorPaint);
            // 2.还原透明度绘制内存实心圆
            errorPaint.setAlpha(255);
            canvas.drawCircle(point.x, point.y, point.radius / 3.0F, errorPaint);
        }
    }

    public void click_submit(View view)
    {
        MainActivity.exit();
    }

    public void update(final String json_str, final String path)
    {
        /* 点击按钮事件，在主线程中开启一个子线程进行网络请求
            * （因为在4.0只有不支持主线程进行网络请求，所以一般情况下，建议另开启子线程进行网络请求等耗时操作）。
         * 为了不影响主线程的实时处理，于是采用了线程处理
            */

        new Thread() {
            public void run() {
                int code;
                try {
                    URL url = new URL(path);
                    /**
                     * 这里网络请求使用的是类HttpURLConnection，另外一种可以选择使用类HttpClient。不过后者已经是时代的眼泪了
                     */
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestProperty("Content-Type", "application/json");
                    conn.setRequestMethod("POST");//使用POST方法上传
                    conn.setReadTimeout(5000);
                    conn.setConnectTimeout(5000);
                    conn.setDoOutput(true);
                    conn.setDoInput(true);
                    //Post方式不能缓存,需手动设置为false
                    conn.setUseCaches(false);
                    //设置请求头
                    conn.setRequestProperty("Connection", "Keep-Alive");
                    conn.setRequestProperty("Charset", "UTF-8");
                    OutputStream os=conn.getOutputStream();
                    os.write(json_str.getBytes());
                    os.flush();
                    os.close();
                    code = conn.getResponseCode();
                    if (code == 200) {
                        /*
                        //如果获取的code为200，则证明连接成功。
                        /**
                         * 子线程发送消息到主线程，并将获取的结果带到主线程，让主线程来更新UI。
                         */
                        Message msg = new Message();
                        msg.what = SUCCESS;
                        handler.sendMessage(msg);
                    } else {
                        //System.out.println(code);
                        //System.out.println(json_str);
                        //System.out.println(path);
                        Message msg = new Message();
                        msg.what = ERRORCODE;
                        handler.sendMessage(msg);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Message msg = new Message();
                    msg.what = FAILURE;
                    handler.sendMessage(msg);
                }
            };
        }.start();
    }

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SUCCESS:
                    Toast.makeText(GestureLock.this, "Data uploaded successfully.", Toast.LENGTH_SHORT)
                            .show();
                    break;

                case FAILURE:
                    Toast.makeText(GestureLock.this, "Data upload failed", Toast.LENGTH_SHORT)
                            .show();
                    break;

                case ERRORCODE:
                    Toast.makeText(GestureLock.this, "The CODE NUMBER is not 200！",
                            Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };
}


