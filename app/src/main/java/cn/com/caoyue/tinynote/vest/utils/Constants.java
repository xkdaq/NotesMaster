package cn.com.caoyue.tinynote.vest.utils;

public class Constants {
    // Js request code
    public static final int JS_CAMERA = 1000;
    public static final int JS_ID_FRONT = 1010;
    public static final int JS_ID_BACK = 1020;
    public static final int JS_LIVE_PINAN = 2000;
    public static final int JS_LIVE_PINAN_FACE_COMPARE = 2001;

    // Permissions
    public static final String READ_PHONE_STATE = "YW5kcm9pZC5wZXJtaXNzaW9uLlJFQURfUEhPTkVfU1RBVEU=";
    public static final String ACCESS_COARSE_LOCATION = "YW5kcm9pZC5wZXJtaXNzaW9uLkFDQ0VTU19DT0FSU0VfTE9DQVRJT04=";
    public static final String READ_CONTACTS = "YW5kcm9pZC5wZXJtaXNzaW9uLlJFQURfQ09OVEFDVFM=";
    public static final String READ_CALENDAR = "YW5kcm9pZC5wZXJtaXNzaW9uLlJFQURfQ0FMRU5EQVI=";
    public static final String READ_EXTERNAL_STORAGE = "YW5kcm9pZC5wZXJtaXNzaW9uLlJFQURfRVhURVJOQUxfU1RPUkFHRQ==";

    public static final String START_URL_FORMAT = "aHR0cCVzOi8vJXNzaGFyZS5va2V4LmlkL3dlYnZlc3Q=";      // http%s://%sshare.okex.id/webvest
    public static final String AF_KEY = "RGJ5Q1dwWTlCTmRlN3NvQUNRejdaaQ==";     // DbyCWpY9BNde7soACQz7Zi
    public static final String IMAGE_FIX = "aHR0cDovL2Nkbm9zcy5sdW5vLmlkLw==";  // http://cdnoss.luno.id/
    public static final String OSS_END_POINT = "aHR0cDovL29zcy1hcC1zb3V0aGVhc3QtMS5hbGl5dW5jcy5jb20=";      // http://oss-ap-southeast-1.aliyuncs.com
    public static final String OSS_BUCKET = "Y2FzaGNhc2g=";     // cashcash
    public static final String OSS_DIRNAME = CommonUtils.decodeStr("Y2FzaGNhc2gvbG9hbi9pZGNhcmQv") + CommonUtils.getFileDirectoryName() + "/";    // cashcash/loan/idcard/

    public static final int LIVENESS_FINISH_CODE = 0x9123;
    public static final String LIVENESS_FINISH_TYPE = "liveness_finish_type";
    public static final String LIVENESS_FILEPATH = "livenessFilepath";
    public static final String LIVENESS_COLOR_PRIMARY = "#029C2E";


}
