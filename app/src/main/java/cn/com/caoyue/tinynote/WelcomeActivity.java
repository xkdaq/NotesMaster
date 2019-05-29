package cn.com.caoyue.tinynote;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import cn.com.caoyue.tinynote.vest.MainVestActivity;
import cn.com.caoyue.tinynote.vest.utils.PreferencesUtil;


public class WelcomeActivity extends Activity {
    private final long SPLASH_LENGTH = 6000; //模拟等待时间长一点 等af回调状态 后面做马甲包在主页做判断
    Handler handler = new Handler();

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        handler.postDelayed(new Runnable() {  //使用handler的postDelayed实现延时跳转

            public void run() {
                String status = PreferencesUtil.getInstance().getTabType();
                if ("Non-organic".equalsIgnoreCase(status)) {
                    Intent intent = new Intent(WelcomeActivity.this, MainVestActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        }, SPLASH_LENGTH);

    }

}
