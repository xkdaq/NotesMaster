package cn.com.caoyue.tinynote.vest.pafacedetector;

import android.content.Context;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.FrameLayout;


import java.io.IOException;
import java.util.List;

import cn.com.caoyue.tinynote.vest.utils.CameraUtils;

public class CameraSurfaceView implements SurfaceHolder.Callback, Camera.PreviewCallback {

    private Context mContext;
    private Camera mCamera;
    private Camera.Parameters mCameraParameters;
    private SurfaceView mSurfaceView;
    private SurfaceHolder mHolder;
    private int mWidth;
    private int mHeight;
    private int mCameraDisplayOrientation;
    private int mCameraMode = Camera.CameraInfo.CAMERA_FACING_FRONT;

    public CameraSurfaceView(Context context) {
        mContext = context;
    }

    public void initPreview(FrameLayout frameLayout, int cameraMode) {
        mCameraMode = cameraMode;
        WindowManager manager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        mSurfaceView = new SurfaceView(mContext);
        mHolder = mSurfaceView.getHolder();
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(display.getWidth(),
                display.getWidth() * 4 / 3);
        mSurfaceView.setLayoutParams(layoutParams);
        frameLayout.addView(mSurfaceView);
    }

    public void openCamera() {
        mHolder.addCallback(this);
        if (mCamera != null) {
            relaseCamera();
        }
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        int cameraCount = Camera.getNumberOfCameras();
        for (int i = 0; i < cameraCount; i++) {
            Camera.getCameraInfo(i, cameraInfo);
            if (cameraInfo.facing == mCameraMode) {
                mCamera = Camera.open(i);
                break;
            } else if (cameraCount == 1) {
                mCamera = Camera.open(i);
                mCameraMode = i;
            }
        }

        mCameraParameters = mCamera.getParameters();
        Camera.Size previewSize =
                CameraUtils.getPropPreviewSize(mCameraParameters.getSupportedPreviewSizes(),
                        480, 640);
        mWidth = previewSize.width;
        mHeight = previewSize.height;
        mCameraParameters.setPreviewFormat(ImageFormat.NV21);
        mCameraParameters.setPreviewSize(mWidth, mHeight);
        List<String> focusModes = mCameraParameters.getSupportedFocusModes();
        if (focusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
            mCameraParameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        } else if (focusModes.contains(Camera.Parameters.FOCUS_MODE_FIXED)) {
            mCameraParameters.setFocusMode(Camera.Parameters.FOCUS_MODE_FIXED);
        } else if (focusModes.contains(Camera.Parameters.FOCUS_MODE_INFINITY)) {
            mCameraParameters.setFocusMode(Camera.Parameters.FOCUS_MODE_INFINITY);
        } else if (focusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
            mCameraParameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
        } else {
            mCameraParameters.setFocusMode(focusModes.get(0));
        }
        mCameraParameters.setPreviewFrameRate(30);
        mCamera.setParameters(mCameraParameters);
    }

    public void startPreview() {
        if (mCamera != null) {
            try {
                mCameraDisplayOrientation = CameraUtils.getCameraDisplayOrientation(mContext, mCameraMode);
                mCamera.setPreviewDisplay(mHolder);
                mCamera.setDisplayOrientation(mCameraDisplayOrientation);
                mCamera.setPreviewCallback(this);
                mCamera.startPreview();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void stopPreview() {
        if (mCamera != null) {
            mCamera.stopPreview();
        }
    }

    private void relaseCamera() {
        try {
            if (mCamera != null) {
                mCamera.release();
                mCamera = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void relase() {
        if (mContext != null) {
            mContext = null;
        }
        if (mPreviewCallback != null) {
            mPreviewCallback = null;
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (mCamera != null) {
            stopPreview();
            startPreview();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        stopPreview();
        relaseCamera();
        mHolder.removeCallback(this);
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        mPreviewCallback.onPreviewFrame(data);
    }

    private PreviewCallback mPreviewCallback;

    public void setPreviewCallback(PreviewCallback onFrameDataCallback) {
        mPreviewCallback = onFrameDataCallback;
    }

    public int getCameraOri() {
        return mCameraDisplayOrientation;
    }

    public int getCameraWidth() {
        return mWidth;
    }

    public int getCameraHeight() {
        return mHeight;
    }

    public int getCameraMode() {
        return mCameraMode;
    }
}
