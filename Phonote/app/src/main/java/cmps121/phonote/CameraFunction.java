package cmps121.phonote;

import android.content.Context;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;
import java.util.List;


public class CameraFunction extends SurfaceView implements SurfaceHolder.Callback {
    private SurfaceHolder holder;
    private Camera camera;

    public CameraFunction(Context context, Camera camera) {
        super(context);
        this.camera = camera;
        holder = getHolder();
        holder.addCallback(this);
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

    }


    public void surfaceCreated(SurfaceHolder holder) {
        Camera.Parameters params = camera.getParameters();

        List<Camera.Size> camSizes = params.getSupportedPictureSizes();
        Camera.Size mCamSizes = null;

        //gets all the possible resolution sizes for the camera
        for(Camera.Size size : camSizes){
            mCamSizes = size;

        }

        //Changes the orientation of the camera to match the phone's orientation
        if(this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            params.set("orientation","landscape");
            camera.setDisplayOrientation(0);
            params.setRotation(0);
        } else {
            params.set("orientation","portrait");
            camera.setDisplayOrientation(90);
            params.setRotation(90);
        }
        params.setPictureSize(mCamSizes.width,mCamSizes.height); //sets the view to be the size of the camera
        params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE); //sets auto focus for camera
        camera.setParameters(params);
        try{
            camera.setPreviewDisplay(holder);
            camera.startPreview();
        } catch(IOException e){
            e.printStackTrace();
        }

    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        camera.stopPreview();
        camera.release();
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        try {
            camera.setPreviewDisplay(holder);
            camera.startPreview();
        } catch (Exception e) {
            // intentionally left blank for a test
        }
    }
}

