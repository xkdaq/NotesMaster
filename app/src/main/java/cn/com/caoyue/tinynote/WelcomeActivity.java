package cn.com.caoyue.tinynote;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import cn.com.caoyue.tinynote.vest.MainVestActivity;


public class WelcomeActivity extends Activity {
    private final long SPLASH_LENGTH = 3000;
    Handler handler = new Handler();

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        handler.postDelayed(new Runnable() {  //使用handler的postDelayed实现延时跳转

            public void run() {
//                String status = PreferencesUtil.getInstance().getTabType();
//                if ("Organic".equalsIgnoreCase(status)) {
//                    Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
//                    startActivity(intent);
//                    finish();
//                } else {
//                    Intent intent = new Intent(WelcomeActivity.this, MainVestActivity.class);
//                    startActivity(intent);
//                    finish();
//                }

                Intent intent = new Intent(WelcomeActivity.this, MainVestActivity.class);
                startActivity(intent);
                finish();
            }
        }, SPLASH_LENGTH);//3秒后跳转至应用主界面MainActivity

    }

}
