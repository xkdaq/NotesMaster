package cn.com.caoyue.tinynote.vest.manager;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.pingan.ai.face.common.PaFaceConstants;
import com.pingan.ai.face.entity.PaFaceDetectFrame;
import com.pingan.ai.face.manager.PaFaceDetectorManager;
import com.pingan.ai.face.manager.impl.OnPaFaceDetectorListener;

import java.io.File;

import cn.com.caoyue.tinynote.R;
import cn.com.caoyue.tinynote.vest.pafacedetector.CameraSurfaceView;
import cn.com.caoyue.tinynote.vest.pafacedetector.CircleProgressBar;
import cn.com.caoyue.tinynote.vest.pafacedetector.FaceDetectRoundView;
import cn.com.caoyue.tinynote.vest.pafacedetector.PreviewCallback;
import cn.com.caoyue.tinynote.vest.pafacedetector.TimeOutDialog;
import cn.com.caoyue.tinynote.vest.utils.BitmapUtils;
import cn.com.caoyue.tinynote.vest.utils.CommonUtils;
import cn.com.caoyue.tinynote.vest.utils.Constants;
import cn.com.caoyue.tinynote.vest.utils.Tips;

public class FaceDetectActivity extends AppCompatActivity implements PreviewCallback {

    private CameraSurfaceView cameraSurfaceView;
    private PaFaceDetectorManager detector;

    private TextView mToolbar, tvBlinkTime, time_tv;
    private FaceDetectRoundView mFaceDetectRoundView;
    private CircleProgressBar mCircleProgressBar;

    private RelativeLayout rl_base_toolbar;

    private int previewCount = 0;
    private int blinkTime;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fs_activity_face_detect);
        initView();
    }

    private void initView() {
        int colorPrimary = Color.parseColor(Constants.LIVENESS_COLOR_PRIMARY);

        rl_base_toolbar = findViewById(R.id.rl_toolbar);
        rl_base_toolbar.setBackgroundColor(colorPrimary);

        mToolbar = findViewById(R.id.tv_title_toolbar);
        mToolbar.setText(R.string.text_title_liveness);

        findViewById(R.id.iv_back_toolbar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(Constants.LIVENESS_FINISH_CODE);
                finish();
            }
        });

        time_tv = (TextView) findViewById(R.id.tv_time);
        time_tv.setTextColor(colorPrimary);
        mCircleProgressBar = (CircleProgressBar) findViewById(R.id.detection_step_timeout_progressBar);
        tvBlinkTime = (TextView) findViewById(R.id.tv_tip);
        mFaceDetectRoundView = (FaceDetectRoundView) findViewById(R.id.detect_face_round);
        FrameLayout frameLayout = (FrameLayout) findViewById(R.id.fl_preview);
        initPreviewView(frameLayout);
        initDetector();
    }

    private void initPreviewView(FrameLayout frameLayout) {
        cameraSurfaceView = new CameraSurfaceView(this);
        cameraSurfaceView.initPreview(frameLayout, Camera.CameraInfo.CAMERA_FACING_FRONT);
        cameraSurfaceView.setPreviewCallback(this);
    }

    private void initDetector() {
        detector = PaFaceDetectorManager.getInstance();
        detector.initFaceDetector(this);
        detector.setOnFaceDetectorListener(onPaFaceDetectorListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        cameraSurfaceView.openCamera();
        cameraSurfaceView.startPreview();
        detector.startFaceDetect();
        timer.start();
    }

    @Override
    public void onPreviewFrame(byte[] frameData) {
        previewCount++;
        detector.detectPreviewFrame(previewCount, frameData, cameraSurfaceView.getCameraMode()
                , cameraSurfaceView.getCameraOri(), cameraSurfaceView.getCameraWidth(), cameraSurfaceView.getCameraHeight());
    }

    OnPaFaceDetectorListener onPaFaceDetectorListener = new OnPaFaceDetectorListener() {
        @Override
        public void onDetectTips(int tip) {
            if (tip == PaFaceConstants.EnvironmentalTips.NORMAL) {
                setBlinkTime(blinkTime);
                mFaceDetectRoundView.processDrawState(true);
            } else if (tip == PaFaceConstants.EnvironmentalTips.NO_FACE) {
                setBlinkTime(-1);
            } else if (tip == PaFaceConstants.EnvironmentalTips.FACE_ILLEGAL) {
                setBlinkTime(-2);
            } else if (tip == PaFaceConstants.EnvironmentalTips.MULTI_FACE) {
                setBlinkTime(-3);
            } else {
                tvBlinkTime.setText(Tips.getDescription(tip));
            }
        }

        @Override
        public void onDetectMotionType(int tip) {
            blinkTime = tip - PaFaceConstants.MotionType.EYE;
            setBlinkTime(blinkTime);
        }

        @Override
        public void onDetectComplete(int tip, PaFaceDetectFrame frame) {
            timer.cancel();
            if (tip == PaFaceConstants.AliveType.ALIVE) {
                toResultActivity(frame);
            } else {
                showErrorDialog(1);
            }
        }

        @Override
        public void onDetectFaceInfo(int tag, PaFaceDetectFrame frame) {
        }
    };

    private void toResultActivity(PaFaceDetectFrame frame) {
        Intent intent = new Intent();
        Bitmap bitmap = BitmapUtils.getBitmap(frame.frmaeWidth, frame.frameHeight, frame.frame, frame.frmaeOri);
        byte[] bytes = BitmapUtils.Bitmap2Bytes(bitmap);
        String comparisonFilePath = CommonUtils.saveJPGFile(this, bytes, "biapcomparison_image.jpg");
        //判断图片是否存在
        File pictureFile = new File(comparisonFilePath);
        if (!pictureFile.exists() || pictureFile.length() == 0) {
            //清除缓存
            CommonUtils.clearAllCache(this);
            showErrorDialog(0);
        } else {
            Bundle bundle = new Bundle();
            bundle.putInt(Constants.LIVENESS_FINISH_TYPE, 1);
            bundle.putString(Constants.LIVENESS_FILEPATH, comparisonFilePath);
            intent.putExtras(bundle);
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    private void setBlinkTime(int blinkTime) {
        if (blinkTime == -3) {
            this.blinkTime = 0;
            detector.resetBlinkState();
            tvBlinkTime.setText(R.string.fs_face_mutii_face);
            mFaceDetectRoundView.processDrawState(false);
        } else if (blinkTime == -2) {
            this.blinkTime = 0;
            detector.resetBlinkState();
            tvBlinkTime.setText(R.string.fs_face_change_attack);
            mFaceDetectRoundView.processDrawState(false);
        } else if (blinkTime == -1) {
            this.blinkTime = 0;
            detector.resetBlinkState();
            tvBlinkTime.setText(R.string.fs_face_no_face);
            mFaceDetectRoundView.processDrawState(false);
        } else if (blinkTime == 0) {
            tvBlinkTime.setText(R.string.fs_face_flash_three_times);
        } else {
            tvBlinkTime.setText(getString(R.string.fs_face_continue_blinking) + "(" + blinkTime + "/3)");
        }
    }

    private void showErrorDialog(int tag) {
        detector.stopFaceDetect();

        tvBlinkTime.setText(R.string.fs_face_tips);
        time_tv.setText("0");
        timer.cancel();

        TimeOutDialog dialog = new TimeOutDialog(this);
        dialog.builder();
        dialog.show();
        dialog.setOnExitClickListener(new TimeOutDialog.onExitClickListener() {
            @Override
            public void onExitClick() {
                finish();
            }
        });

        dialog.setOnReDetectClickListener(new TimeOutDialog.onReDetectClickListener() {
            @Override
            public void onReDetectClick() {
                blinkTime = 0;
                timer.start();
                detector.startFaceDetect();
                setBlinkTime(blinkTime);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        detector.stopFaceDetect();
        cameraSurfaceView.stopPreview();
        timer.cancel();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        detector.relase();
        cameraSurfaceView.relase();
    }


    private CountDownTimer timer = new CountDownTimer(30000, 1000) {

        @Override
        public void onTick(long millisUntilFinished) {
            int time = (int) (millisUntilFinished / 1000);
            time_tv.setText(String.valueOf(time));
            mCircleProgressBar.setProgress(time * 10);
        }

        @Override
        public void onFinish() {
            showErrorDialog(0);
        }
    };



}
