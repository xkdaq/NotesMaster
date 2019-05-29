package cn.com.caoyue.tinynote.vest.pafacedetector;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import cn.com.caoyue.tinynote.R;
import cn.com.caoyue.tinynote.vest.utils.CommonUtils;


public class TimeOutDialog implements View.OnClickListener {

    private Activity activity;
    private Dialog dialog;
    private Display display;
    private TextView tvExit;
    private TextView tvReStart;

    public TimeOutDialog(Activity activity) {
        this.activity = activity;
        WindowManager windowManager = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
        display = windowManager.getDefaultDisplay();
    }

    public TimeOutDialog builder() {
        View view = LayoutInflater.from(activity).inflate(R.layout.fs_dialog_time_out, null);
        view.setMinimumWidth(display.getWidth());
        dialog = new Dialog(activity, R.style.ActionDialogStyle);
        dialog.setContentView(view);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        initView(view);
        Window dialogWindow = dialog.getWindow();
        dialogWindow.setGravity(Gravity.CENTER);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.x = 0;
        lp.y = 0;
        dialogWindow.setAttributes(lp);
        return this;
    }

    private void initView(View view) {
        tvExit = view.findViewById(R.id.tv_exit);
        tvReStart = view.findViewById(R.id.tv_re_start);
        tvReStart.setBackground(CommonUtils.getBlockDrawable());

        initEvent();
    }

    private void initEvent() {
        tvExit.setOnClickListener(this);
        tvReStart.setOnClickListener(this);
    }


    public void dismiss() {
        dialog.dismiss();
    }

    public void show() {
        if (!activity.isFinishing() && !isShowing()) {
            dialog.show();
        }
    }

    public boolean isShowing() {
        return dialog.isShowing();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.tv_exit) {
            setOnExitClickListener(onExitClickListener);
            if (onExitClickListener != null) {
                onExitClickListener.onExitClick();
                dismiss();
            }
        } else if (id == R.id.tv_re_start) {
            setOnReDetectClickListener(onReDetectListener);
            if (onReDetectListener != null) {
                onReDetectListener.onReDetectClick();
                dismiss();
            }
        }
    }

    private onExitClickListener onExitClickListener;

    public interface onExitClickListener {
        void onExitClick();
    }

    public void setOnExitClickListener(onExitClickListener listener) {
        onExitClickListener = listener;
    }


    private onReDetectClickListener onReDetectListener;

    public interface onReDetectClickListener {
        void onReDetectClick();
    }

    public void setOnReDetectClickListener(onReDetectClickListener listener) {
        onReDetectListener = listener;
    }


}
