package net.envigo.linterna;

import android.annotation.TargetApi;
import android.content.Context;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.util.Log;


/**
 * Created by diego on 14/02/18.
 */

class lantern {
    private final CameraManager mCameraManager;

    private String mCameraID;
    //int ToastDuration = Toast.LENGTH_SHORT;


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public lantern(Context context) {

        mCameraManager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);

        try {
            mCameraID = mCameraManager.getCameraIdList()[0];
        } catch (CameraAccessException e ) {
            e.printStackTrace();
        }
    }
    @TargetApi(Build.VERSION_CODES.M)
    public void turnOn() {
        try {
            Log.d("Log", "mCameraID: " + mCameraID);
            mCameraManager.setTorchMode(mCameraID, true);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    public void turnOff() {
        try {
            mCameraManager.setTorchMode(mCameraID, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
