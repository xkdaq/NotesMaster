package cn.com.caoyue.tinynote.vest.manager;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import cn.com.caoyue.tinynote.BuildConfig;
import cn.com.caoyue.tinynote.R;
import cn.com.caoyue.tinynote.vest.utils.CommonUtils;

public class CameraActivity extends Activity implements View.OnClickListener {

    public final static int TYPE_IDCARD_FRONT = 1;
    public final static int TYPE_IDCARD_BACK = 2;
    public final static int TYPE_DEFAULT = 3;

    private static final String PROVIDER = BuildConfig.APPLICATION_ID + ".provider";

    private CameraPreview cameraPreview;
    private View containerView;
    private ImageView cropView;
    private ImageView flashImageView;
    private View optionView;
    private View resultView;
    private int type;
    private String cameraFilePath;

    public static void captureForResult(Activity activity, File cameraFile, int type, int requestCode) {
        if (type == TYPE_IDCARD_FRONT) {
            Intent intent = new Intent(activity, CameraActivity.class);
            intent.putExtra("type", type);
            intent.putExtra("cameraFilePath", cameraFile.getAbsolutePath());
            activity.startActivityForResult(intent, requestCode);
        } else if (type == TYPE_IDCARD_BACK) {
            Intent intent = new Intent(activity, CameraActivity.class);
            intent.putExtra("type", type);
            intent.putExtra("cameraFilePath", cameraFile.getAbsolutePath());
            activity.startActivityForResult(intent, requestCode);
        } else if (type == TYPE_DEFAULT) {
            Intent intentFromCapture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Uri uriForFile = FileProvider.getUriForFile(activity, PROVIDER, cameraFile);
                intentFromCapture.putExtra(MediaStore.EXTRA_OUTPUT, uriForFile);
                intentFromCapture.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intentFromCapture.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            } else {
                intentFromCapture.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(cameraFile));
            }
            activity.startActivityForResult(intentFromCapture, requestCode);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        type = getIntent().getIntExtra("type", 0);
        cameraFilePath = getIntent().getStringExtra("cameraFilePath");
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.fs_activity_camera);
        cameraPreview = findViewById(R.id.camera_surface);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                    cameraPreview.focus();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        float screenMinSize = Math.min(getResources().getDisplayMetrics().widthPixels, getResources().getDisplayMetrics().heightPixels);
        float maxSize = screenMinSize / 9.0f * 16.0f;
        RelativeLayout.LayoutParams layoutParams;
        layoutParams = new RelativeLayout.LayoutParams((int) maxSize, (int) screenMinSize);
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        cameraPreview.setLayoutParams(layoutParams);

        containerView = findViewById(R.id.camera_crop_container);
        cropView = findViewById(R.id.camera_crop);
        float height = (int) (screenMinSize * 0.75);
        float width = (int) (height * 75.0f / 47.0f);
        LinearLayout.LayoutParams containerParams = new LinearLayout.LayoutParams((int) width, ViewGroup.LayoutParams.MATCH_PARENT);
        LinearLayout.LayoutParams cropParams = new LinearLayout.LayoutParams((int) width, (int) height);
        containerView.setLayoutParams(containerParams);
        cropView.setLayoutParams(cropParams);
        switch (type) {
            case TYPE_IDCARD_FRONT:
                cropView.setImageResource(R.mipmap.fs_camera_idcard_front);
                break;
            case TYPE_IDCARD_BACK:
                cropView.setImageResource(R.mipmap.fs_camera_idcard_back);
                break;
        }

        flashImageView = findViewById(R.id.camera_flash);
        optionView = findViewById(R.id.camera_option);
        resultView = findViewById(R.id.camera_result);
        cameraPreview.setOnClickListener(this);
        findViewById(R.id.camera_close).setOnClickListener(this);
        findViewById(R.id.camera_take).setOnClickListener(this);
        flashImageView.setOnClickListener(this);
        findViewById(R.id.camera_result_ok).setOnClickListener(this);
        findViewById(R.id.camera_result_cancel).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.camera_surface) {
            cameraPreview.focus();
        } else if (id == R.id.camera_close) {
            finish();
        } else if (id == R.id.camera_take) {
            takePhoto();
        } else if (id == R.id.camera_flash) {
            boolean isFlashOn = cameraPreview.switchFlashLight();
            flashImageView.setImageResource(isFlashOn ? R.mipmap.fs_camera_flash_on : R.mipmap.fs_camera_flash_off);
        } else if (id == R.id.camera_result_ok) {
            goBack();
        } else if (id == R.id.camera_result_cancel) {
            optionView.setVisibility(View.VISIBLE);
            cameraPreview.setEnabled(true);
            resultView.setVisibility(View.GONE);
            cameraPreview.startPreview();
        }
    }

    private void takePhoto() {
        optionView.setVisibility(View.GONE);
        cameraPreview.setEnabled(false);
        cameraPreview.takePhoto(new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(final byte[] data, final Camera camera) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            File originalFile = getOriginalFile();
                            FileOutputStream originalFileOutputStream = new FileOutputStream(originalFile);
                            originalFileOutputStream.write(data);
                            originalFileOutputStream.close();

                            Bitmap bitmap = BitmapFactory.decodeFile(originalFile.getPath());

                            float left, top, right, bottom;
                            left = ((float) containerView.getLeft() - (float) cameraPreview.getLeft()) / (float) cameraPreview.getWidth();
                            top = (float) cropView.getTop() / (float) cameraPreview.getHeight();
                            right = (float) containerView.getRight() / (float) cameraPreview.getWidth();
                            bottom = (float) cropView.getBottom() / (float) cameraPreview.getHeight();
                            Bitmap cropBitmap = Bitmap.createBitmap(bitmap,
                                    (int) (left * (float) bitmap.getWidth()),
                                    (int) (top * (float) bitmap.getHeight()),
                                    (int) ((right - left) * (float) bitmap.getWidth()),
                                    (int) ((bottom - top) * (float) bitmap.getHeight()));

                            final File cropFile = getCropFile();
                            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(cropFile));
                            cropBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                            bos.flush();
                            bos.close();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    camera.stopPreview();
                                    resultView.setVisibility(View.VISIBLE);
                                }
                            });

                            return;
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                optionView.setVisibility(View.VISIBLE);
                                cameraPreview.setEnabled(true);
                            }
                        });
                    }
                }).start();

            }
        });
    }

    private File getOriginalFile() {
        switch (type) {
            case TYPE_IDCARD_FRONT:
                return new File(CommonUtils.getLocalCacheDir(this), "idCardFront.jpg");
            case TYPE_IDCARD_BACK:
                return new File(CommonUtils.getLocalCacheDir(this), "idCardBack.jpg");
        }
        return new File(CommonUtils.getLocalCacheDir(this), "picture.jpg");
    }

    private File getCropFile() {
        switch (type) {
            case TYPE_IDCARD_FRONT:
                return new File(CommonUtils.getLocalCacheDir(this), "idCardFrontCrop.jpg");
            case TYPE_IDCARD_BACK:
                return new File(CommonUtils.getLocalCacheDir(this), "idCardBackCrop.jpg");
        }
        return new File(CommonUtils.getLocalCacheDir(this), "pictureCrop.jpg");
    }

    private void goBack() {
        Intent intent = new Intent();
        intent.putExtra("result", getCropFile().getPath());
        try {
            CommonUtils.copy(getCropFile(), new File(cameraFilePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

}
