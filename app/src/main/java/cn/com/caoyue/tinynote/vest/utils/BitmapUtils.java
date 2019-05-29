package cn.com.caoyue.tinynote.vest.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.YuvImage;
import android.os.Build;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;


public class BitmapUtils {

    public final static int CV_ROTATE_90_CLOCKWISE = 0;
    public final static int CV_ROTATE_180 = 1;
    public final static int CV_ROTATE_90_COUNTERCLOCKWISE = 2;
    public final static int CV_ROTATE_360 = 3;

    public static byte[] Bitmap2Bytes(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        return baos.toByteArray();
    }

    public static Bitmap getBitmap(int imageWidth, int imageHeight, byte[] frame, int ori) {
        Bitmap bitmap = null;
        try {
            YuvImage image = new YuvImage(frame, ImageFormat.NV21, imageWidth, imageHeight, null);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            image.compressToJpeg(new Rect(0, 0, imageWidth, imageHeight), 100, stream);
            bitmap = BitmapFactory.decodeByteArray(stream.toByteArray(), 0, stream.size());
            stream.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        float rotate;
        if (ori == CV_ROTATE_90_CLOCKWISE) {
            rotate = 90;
        } else if (ori == CV_ROTATE_90_COUNTERCLOCKWISE) {
            rotate = 270;
        } else if (ori == CV_ROTATE_180) {
            rotate = 180;
        } else {
            rotate = 360;
        }
        if (bitmap != null) {
            bitmap = rotateBitmap(bitmap, rotate);
        }
        return bitmap;
    }

    public static Bitmap getSmallBitmap(byte[] frame, int x, int y, int w, int h, int width, int height, int ori, Bitmap bitmap) {
        try {
            if (ori == CV_ROTATE_90_CLOCKWISE ||
                    ori == CV_ROTATE_90_COUNTERCLOCKWISE) {
                width = height;
                height = width;
            }
            RectF rect;
            if (x >= 0 && y >= 0 && w >= 0 && h >= 0) {
                rect = new RectF(x, y, w, h);
                if (frame.length != 0) {
                    int nw, nh, xn, yn, x0, y0;
                    float ratio = 1.45f;
                    int x1 = (int) rect.left;
                    int y1 = (int) rect.top;
                    int w1 = (int) rect.width() + x1;
                    int h1 = (int) rect.height() + y1;

                    x0 = (int) (x1 - w1 * (ratio - 1) * 0.5);
                    xn = (int) (x0 + w1 * ratio);
                    if (x0 < 0) {
                        x0 = 0;
                    }
                    if (xn > width - 1) {
                        xn = width - 1;
                    }
                    nw = xn - x0 + 1;

                    y0 = (int) (y1 - h1 * (ratio - 1));
                    yn = (int) (y0 + h1 * (1 + (ratio - 1) * 1.5));
                    if (y0 < 0) {
                        y0 = 0;
                    }
                    if (yn > height - 1) {
                        yn = height - 1;
                    }
                    nh = yn - y0 + 1;
                    return Bitmap.createBitmap(bitmap, (int) x0, (int) y0, (int) nw, (int) nh);
                }
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static Bitmap rotateBitmap(Bitmap origin, float alpha) {
        if (origin == null) {
            return null;
        }
        int width = origin.getWidth();
        int height = origin.getHeight();
        Matrix matrix = new Matrix();
        matrix.setRotate(alpha);
        Bitmap newBM = Bitmap.createBitmap(origin, 0, 0, width, height, matrix, false);
        if (newBM.equals(origin)) {
            return newBM;
        }
        origin.recycle();
        return newBM;
    }

    public static byte[] bitmap2Bytes(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }


    public static int getBitmapSize(Bitmap bitmap) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            return bitmap.getAllocationByteCount();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
            return bitmap.getByteCount();
        }
        return bitmap.getRowBytes() * bitmap.getHeight();
    }

    public static byte[] file2byte(String filePath) {
        byte[] buffer = null;
        try {
            File file = new File(filePath);
            FileInputStream fis = new FileInputStream(file);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] b = new byte[1024];
            int n;
            while ((n = fis.read(b)) != -1) {
                bos.write(b, 0, n);
            }
            fis.close();
            bos.close();
            buffer = bos.toByteArray();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buffer;
    }


}
