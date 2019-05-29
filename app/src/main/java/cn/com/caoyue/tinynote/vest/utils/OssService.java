package cn.com.caoyue.tinynote.vest.utils;

import android.content.Context;

import com.alibaba.sdk.android.oss.ClientConfiguration;
import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.callback.OSSProgressCallback;
import com.alibaba.sdk.android.oss.common.auth.OSSCustomSignerCredentialProvider;
import com.alibaba.sdk.android.oss.common.utils.OSSUtils;
import com.alibaba.sdk.android.oss.internal.OSSAsyncTask;
import com.alibaba.sdk.android.oss.model.GetObjectRequest;
import com.alibaba.sdk.android.oss.model.GetObjectResult;
import com.alibaba.sdk.android.oss.model.OSSRequest;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;

public class OssService {

    public OSS mOss;
    private String mBucket;

    public interface OnProfileCallback {
        void onSuccess(String objectKey);

        void onFailure(String msg);
    }

    public interface OnDownLoadCallback {
        void onSuccess(String objectKey);

        void onProgress(long currentSize, long totalSize);

        void onFailure(String msg);
    }

    public static OssService initOSS(Context context, String endpoint, String bucket) {
        OSSCustomSignerCredentialProvider provider = new OSSCustomSignerCredentialProvider() {
            @Override
            public String signContent(String content) {
                return OSSUtils.sign("LTAIoE07bIrxfH4W", "1ld35xkXOXzmePTgQpmJ8IGzfAmgPZ", content);
            }
        };
        ClientConfiguration conf = new ClientConfiguration();
        conf.setConnectionTimeout(15 * 1000);
        conf.setSocketTimeout(15 * 1000);
        conf.setMaxConcurrentRequest(5);
        conf.setMaxErrorRetry(2);
        OSS oss = new OSSClient(context, endpoint, provider, conf);
        return new OssService(oss, bucket);
    }

    public OssService(OSS oss, String bucket) {
        this.mOss = oss;
        this.mBucket = bucket;
    }

    public void asyncPutImage(Context context, final String object, String localFile, final OnProfileCallback profileCallback) {
        if (object.equals("")) {
            return;
        }

        File file = JPEGCompressionUtils.getResizedCompressedPhotoFileFromPath(context, localFile);
        if (!file.exists() || file.length() == 0) {
            if (null != profileCallback) {
                profileCallback.onFailure("");
            }
            return;
        }

        asyncPutImage(object, file.getPath(), profileCallback);
    }

    private void asyncPutImage(String object, String localFile, final OnProfileCallback profileCallback) {
        PutObjectRequest put = new PutObjectRequest(mBucket, object, localFile);
        put.setCRC64(OSSRequest.CRC64Config.YES);
        put.setCallbackParam(new HashMap<String, String>() {
            {
                put("callbackBody", "filename=${object}");
            }
        });

        put.setProgressCallback(new OSSProgressCallback<PutObjectRequest>() {
            @Override
            public void onProgress(PutObjectRequest request, long currentSize, long totalSize) {

            }
        });

        OSSAsyncTask task = mOss.asyncPutObject(put, new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
            @Override
            public void onSuccess(PutObjectRequest request, PutObjectResult result) {
                if (null != profileCallback) {
                    profileCallback.onSuccess(request.getObjectKey());
                }
            }

            @Override
            public void onFailure(PutObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
                String info = "";
                if (clientExcepion != null) {
                    clientExcepion.printStackTrace();
                    info = clientExcepion.toString();
                }
                if (serviceException != null) {
                    info = serviceException.toString();
                }

                if (null != profileCallback) {
                    profileCallback.onFailure(info);
                }
            }
        });
    }

    public void downLoad(final Context context, final String name, String bucketName, String objectKey, final OnDownLoadCallback onDownLoadCallback) {
        GetObjectRequest get = new GetObjectRequest(bucketName, objectKey);
        get.setProgressListener(new OSSProgressCallback<GetObjectRequest>() {
            @Override
            public void onProgress(GetObjectRequest request, long currentSize, long totalSize) {
                if (null != onDownLoadCallback) {
                    onDownLoadCallback.onProgress(currentSize, totalSize);
                }
            }
        });
        OSSAsyncTask task = mOss.asyncGetObject(get, new OSSCompletedCallback<GetObjectRequest, GetObjectResult>() {
            @Override
            public void onSuccess(GetObjectRequest request, GetObjectResult result) {
                try {
                    InputStream inputStream = result.getObjectContent();
                    OutputStream outputStream = null;
                    byte[] buffer = new byte[2048];
                    int len;
                    try {
                        outputStream = context.openFileOutput(name, 0);
                        while ((len = inputStream.read(buffer)) != -1) {
                            outputStream.write(buffer, 0, len);
                        }
                        outputStream.flush();
                        if (null != onDownLoadCallback) {
                            onDownLoadCallback.onSuccess(request.getObjectKey());
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        if (null != onDownLoadCallback) {
                            onDownLoadCallback.onFailure(e.getMessage());
                        }
                    } finally {
                        if (inputStream != null) {
                            inputStream.close();
                        }
                        if (outputStream != null) {
                            outputStream.close();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    if (null != onDownLoadCallback) {
                        onDownLoadCallback.onFailure(e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(GetObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
                if (clientExcepion != null) {
                    clientExcepion.printStackTrace();
                }
                if (serviceException != null) {
                    if (null != onDownLoadCallback) {
                        onDownLoadCallback.onFailure(serviceException.getRawMessage());
                    }
                }
            }
        });
    }
}
