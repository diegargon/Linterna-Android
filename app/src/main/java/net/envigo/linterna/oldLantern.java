package net.envigo.linterna;

import android.content.Context;
import android.hardware.Camera;
import android.widget.Toast;


/**
 * Created by diego on 14/02/18.
 */

@SuppressWarnings("deprecation")

public class oldLantern {
    private Camera camera;
    private Context context;
    private static final int ToastDuration = Toast.LENGTH_SHORT;


    public oldLantern(Context c) {
        context = c;
        init();
    }

    void init() {
        if (camera == null) {
            camera = Camera.open();
        }
    }
    void turnOn() {
        init();
        try {
            if (camera != null) {
                Camera.Parameters params = camera.getParameters();
                    if(!params.getFlashMode().equals(Camera.Parameters.FLASH_MODE_TORCH)) {
                        params.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                    }
                    if(params.getFlashMode() == null) {
                        Toast.makeText(context, "No flash mode", ToastDuration).show();
                    }
                    camera.setParameters(params);
                    camera.startPreview();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    void turnOff() {

        try {
            if (camera != null) {
                Camera.Parameters p = camera.getParameters();
                p.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                camera.setParameters(p);
                camera.stopPreview();
                camera.setPreviewCallback(null);
                camera.release();
                camera = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
