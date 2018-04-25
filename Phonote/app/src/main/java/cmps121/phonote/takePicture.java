package cmps121.phonote;

import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.hardware.Camera;

import java.io.IOException;

public class takePicture extends AppCompatActivity implements SurfaceHolder.Callback{
    Camera camera;
    SurfaceView surfaceCamera; //where the camera will be displayed
    SurfaceHolder holderCamera; //gets the frame of the surfaceView
    boolean isPreviewing = false; //keeps track if the camera is already opened
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_picture);

        getWindow().setFormat(PixelFormat.UNKNOWN);//?
        surfaceCamera = (SurfaceView) findViewById(R.id.surfaceCamera);
        holderCamera = surfaceCamera.getHolder();
        holderCamera.addCallback(this);
        holderCamera.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS); //?




    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isPreviewing == false) {
            camera = Camera.open(); //Why does this error???
            try{
                if (camera != null){
                    camera.setPreviewDisplay(surfaceCamera.getHolder());
                    camera.startPreview();
                    isPreviewing = true;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

    }
}

