package cn.com.caoyue.tinynote.vest.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


public final class JPEGCompressionUtils {

    public static final String TEMP_UPLOADING_IMG_FILE = "tmp_uploading_img";

    public static long MAX_IMG_SIZE = 1024 * 1024;

    private static long MAX_IMG_WIDTH = 4096;
    private static long MAX_IMG_HEIGHT = 4096;

    public static File getResizedCompressedPhotoFileFromPath(Context context, String path) {
        File imgFile = new File(path);
        File exportFile = null;

        if (imgFile.exists()) {

            BitmapFactory.Options options = new BitmapFactory.Options();

            options.inJustDecodeBounds = true;
            Bitmap bmp = BitmapFactory.decodeFile(path, options);
            int imageWidth = options.outWidth;
            int imageHeight = options.outHeight;
            if(null != bmp) {
                bmp.recycle();
            }
            if (imgFile.length() > MAX_IMG_SIZE || imageHeight > MAX_IMG_HEIGHT || imageWidth > MAX_IMG_WIDTH) {
                try {
                    exportFile = compressImage(context, imgFile.getAbsolutePath(),
                            Math.max(65, 100 - (int) (3 * imgFile.length() / MAX_IMG_SIZE)));
                } catch (Throwable t) {
                    exportFile = imgFile;
                }
            } else {
                exportFile = imgFile;
            }
        } else {
            exportFile = null;
        }
        return exportFile;
    }

    private static File compressImage(Context context, String filePath, int quality) throws IOException {

        Bitmap scaledBitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();

        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(filePath, options);

        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;

        float maxHeight = 1200.0f;
        float maxWidth = 1600.0f;

        float imgRatio = ((float) actualWidth) / actualHeight;
        float maxRatio = maxWidth / maxHeight;

        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight;
                actualWidth = (int) (imgRatio * actualWidth);
                actualHeight = (int) maxHeight;
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) maxWidth;
            } else {
                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;
            }
        }

        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);

        options.inJustDecodeBounds = false;

        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[16 * 1024];

        bmp = BitmapFactory.decodeFile(filePath, options);
        scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);

        float ratioX = actualWidth / (float) options.outWidth;
        float ratioY = actualHeight / (float) options.outHeight;
        float middleX = actualWidth / 2.0f;
        float middleY = actualHeight / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

        ExifInterface exif;
        exif = new ExifInterface(filePath);

        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0);
        Matrix matrix = new Matrix();
        if (orientation == 6) {
            matrix.postRotate(90);
        } else if (orientation == 3) {
            matrix.postRotate(180);
        } else if (orientation == 8) {
            matrix.postRotate(270);
        }
        scaledBitmap = Bitmap.createBitmap(scaledBitmap,
                0,
                0,
                scaledBitmap.getWidth(),
                scaledBitmap.getHeight(),
                matrix,
                true);

        FileOutputStream out = null;
        out = context.openFileOutput(TEMP_UPLOADING_IMG_FILE, Context.MODE_PRIVATE);
        scaledBitmap.compress(Bitmap.CompressFormat.JPEG, quality, out);
        bmp.recycle();
        scaledBitmap.recycle();
        return new File(context.getFilesDir(), TEMP_UPLOADING_IMG_FILE);
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        final float totalPixels = width * height;
        final float totalReqPixelsCap = reqWidth * reqHeight * 2;
        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }
        return inSampleSize;
    }

}