package cmps121.phonote;

import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class takePicture extends AppCompatActivity{
    private Camera mCamera = null;
    private FrameLayout mFrame = null;
    private SurfaceView mSurface = null;
    private SurfaceHolder mHolder = null;
    CameraFunction showCamera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_picture);
        mFrame = findViewById(R.id.frameCamera);

        try {
            mCamera = Camera.open();
        } catch (Exception e){
            //Couldn't open camera
        }

        showCamera = new CameraFunction(this, mCamera); //holds the display of the camera
        mFrame.addView(showCamera);
    }



    //Handles taking a picture. called when the take picture button is pressed
    public void captureImage(View v){
        if(mCamera!=null){
            mCamera.takePicture(null,null,mPictureCallback);//takes the picture and calls onPictureTaken
            System.out.println("Success");
            //mCamera.startPreview();
        }
    }

    //Handles saving a picture after taking it
    Camera.PictureCallback mPictureCallback = new Camera.PictureCallback(){
        @Override
        public void onPictureTaken(byte[] data, Camera camera){
            File pictureFile = getOutputMediaFile();
            if (pictureFile == null) {
                return;
            }
            try {
                FileOutputStream fos = new FileOutputStream(pictureFile);
                fos.write(data);
                fos.close();
                //mCamera.release();
            }  catch (IOException e) {
            }
        }
    };

    private static File getOutputMediaFile() {

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Phonote");
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("Phonote", "failed to create directory");
                return null;
            }
        }


        // Create a media file name
        String timeStamp = Long.toString (System.currentTimeMillis()/1000);

        File mediaFile;
        mediaFile = new File(mediaStorageDir.getPath() + File.separator
                + "IMG_" + timeStamp + ".png");

        return mediaFile;
    }

}

