package cn.com.caoyue.tinynote.vest.utils;

import com.pingan.ai.face.common.PaFaceConstants;

import cn.com.caoyue.tinynote.R;

public class Tips {

    public static int getDescription(int id) {
        switch (id) {
            case PaFaceConstants.EnvironmentalTips.NORMAL:
                return R.string.fs_face_detection;
            case PaFaceConstants.EnvironmentalTips.MULTI_FACE:
                return R.string.fs_face_mutii_face;
            case PaFaceConstants.EnvironmentalTips.NO_FACE:
                return R.string.fs_face_no_face;
            case PaFaceConstants.EnvironmentalTips.FACE_ROLL_LEFT:
                return R.string.fs_face_too_left;
            case PaFaceConstants.EnvironmentalTips.FACE_ROLL_RIGHT:
                return R.string.fs_face_too_right;
            case PaFaceConstants.EnvironmentalTips.FACE_YAW_LEFT:
                return R.string.fs_face_too_left;
            case PaFaceConstants.EnvironmentalTips.FACE_YAW_RIGHT:
                return R.string.fs_face_too_right;
            case PaFaceConstants.EnvironmentalTips.FACE_PITCH_UP:
                return R.string.fs_face_phone_screen;
            case PaFaceConstants.EnvironmentalTips.FACE_PITCH_DOWN:
                return R.string.fs_face_phone_screen;
            case PaFaceConstants.EnvironmentalTips.TOO_BRIGHT:
                return R.string.fs_face_too_bright;
            case PaFaceConstants.EnvironmentalTips.TOO_DARK:
                return R.string.fs_face_too_dark;
            case PaFaceConstants.EnvironmentalTips.TOO_FUZZY:
                return R.string.fs_face_too_blur;
            case PaFaceConstants.EnvironmentalTips.TOO_CLOSE:
                return R.string.fs_face_too_near;
            case PaFaceConstants.EnvironmentalTips.TOO_FAR:
                return R.string.fs_face_too_small;
            case PaFaceConstants.EnvironmentalTips.FACE_ILLEGAL:
                return R.string.fs_face_change_attack;
            case PaFaceConstants.AliveType.ALIVE:
                return R.string.fs_face_alive;
            case PaFaceConstants.AliveType.UNALIVE:
                return R.string.fs_face_unalive;
        }
        return 0;
    }
}
