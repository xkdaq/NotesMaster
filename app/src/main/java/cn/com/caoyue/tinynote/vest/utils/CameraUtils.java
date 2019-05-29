package cn.com.caoyue.tinynote.vest.utils;

import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.view.Surface;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;


public class CameraUtils {
    public final static int CAMERA_ORIENTATION_0 = 0;
    public final static int CAMERA_ORIENTATION_90 = 90;
    public final static int CAMERA_ORIENTATION_180 = 180;
    public final static int CAMERA_ORIENTATION_270 = 270;

    private static final String TAG = CameraUtils.class.getSimpleName();
    private static CameraSizeComparator sizeComparator = new CameraSizeComparator();

    public static int getCameraDisplayOrientation(Context mContext, int cameraId) {
        if (mContext == null) {
            return 0;
        }
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, info);
        int rotation = ((Activity) mContext).getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;

        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = CAMERA_ORIENTATION_0;
                break;
            case Surface.ROTATION_90:
                degrees = CAMERA_ORIENTATION_90;
                break;
            case Surface.ROTATION_180:
                degrees = CAMERA_ORIENTATION_180;
                break;
            case Surface.ROTATION_270:
                degrees = CAMERA_ORIENTATION_270;
                break;
        }
        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;
        } else {
            result = (info.orientation - degrees + 360) % 360;
        }
        return result;
    }

    public static Camera.Size getPropPreviewSize(List<Camera.Size> list, int propHeight, int propWidth) {
        Collections.sort(list, sizeComparator);
        int i = 0;
        for (Camera.Size s : list) {
            if ((s.width == propWidth && s.height == propHeight)) {
                return list.get(i);
            }
            i++;
        }

        return list.get(0);
    }

    private static class CameraSizeComparator implements Comparator<Camera.Size> {
        public int compare(Camera.Size lhs, Camera.Size rhs) {
            if (lhs.width == rhs.width) {
                return 0;
            } else if (lhs.width > rhs.width) {
                return 1;
            } else {
                return -1;
            }
        }
    }

    public static boolean cameraCanUse() {
        boolean isCanUse = true;
        Camera mCamera = null;
        try {
            mCamera = Camera.open();
            Camera.Parameters mParameters = mCamera.getParameters();
            mCamera.setParameters(mParameters);
        } catch (Exception e) {
            isCanUse = false;
        }
        if (mCamera != null) {
            try {
                mCamera.release();
            } catch (Exception e) {
                e.printStackTrace();
                return isCanUse;
            }
        }
        return isCanUse;
    }


    public static ArrayList<HashMap<String, Integer>> getCameraPreviewSize(
            int cameraId) {
        ArrayList<HashMap<String, Integer>> size = new ArrayList<HashMap<String, Integer>>();
        Camera camera = null;
        try {
            camera = Camera.open(cameraId);
            if (camera == null)
                camera = Camera.open(0);

            List<Camera.Size> allSupportedSize = camera.getParameters()
                    .getSupportedPreviewSizes();
            for (Camera.Size tmpSize : allSupportedSize) {
                if (tmpSize.width > tmpSize.height) {
                    HashMap<String, Integer> map = new HashMap<String, Integer>();
                    map.put("width", tmpSize.width);
                    map.put("height", tmpSize.height);
                    size.add(map);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (camera != null) {
                camera.stopPreview();
                camera.setPreviewCallback(null);
                camera.release();
                camera = null;
            }
        }

        return size;
    }
}
