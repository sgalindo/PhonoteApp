package cmps121.phonote;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import android.app.AlertDialog.Builder;
import android.app.AlertDialog;

public class editImage extends AppCompatActivity {
    private Bitmap bmp = null;
    private String picLoc;
    private ImageView imageView;
    private FrameLayout frameLayout;
    private ImageButton ibCropTop;
    private ImageButton ibCropBot;
    private ImageButton ibCropLeft;
    private ImageButton ibCropRight;
    private Rect rect = new Rect(0,0,0,0);
    Paint paint = new Paint();
    private Bitmap bm = Bitmap.createBitmap(900,1600, Bitmap.Config.ARGB_8888);
    private Canvas canvas = new Canvas(bm);
    private boolean initializeFrame = true;
    private boolean hasCropBeenUpdated = false;
    private AlertDialog.Builder builder;
    private Display display;
    private String projectName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_image);
        imageView = findViewById(R.id.ivPic);
        frameLayout = findViewById(R.id.frameLayout);

        picLoc = getIntent().getStringExtra("image");
        projectName = getIntent().getStringExtra("name");
        bmp = BitmapFactory.decodeFile(picLoc);

        imageView.setImageBitmap(bmp);

        ibCropTop = findViewById(R.id.ibCropTop);
        ibCropBot = findViewById(R.id.ibCropBot);
        ibCropLeft = findViewById(R.id.ibCropLeft);
        ibCropRight = findViewById(R.id.ibCropRight);

        cropButtonEvent();

        //Used to make a call only after the screen is drawn for the first time.  This is neccessary to make sure
        //we can get the correct size of the screen
        frameLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if(initializeFrame == true){
                    FrameLayout.LayoutParams topParams = (FrameLayout.LayoutParams) ibCropTop.getLayoutParams();
                    FrameLayout.LayoutParams leftParams = (FrameLayout.LayoutParams) ibCropLeft.getLayoutParams();
                    FrameLayout.LayoutParams rightParams = (FrameLayout.LayoutParams) ibCropRight.getLayoutParams();
                    FrameLayout.LayoutParams botParams = (FrameLayout.LayoutParams) ibCropBot.getLayoutParams();
                    leftParams.topMargin = frameLayout.getHeight()/2;
                    rightParams.topMargin = frameLayout.getHeight()/2;
                    rightParams.leftMargin = frameLayout.getWidth()-ibCropRight.getWidth();
                    topParams.leftMargin = (leftParams.leftMargin + rightParams.leftMargin)/2;
                    botParams.leftMargin = (leftParams.leftMargin + rightParams.leftMargin)/2;
                    botParams.topMargin = frameLayout.getHeight()-ibCropBot.getWidth()-50;

                    ibCropLeft.setLayoutParams(leftParams);
                    ibCropRight.setLayoutParams(rightParams);
                    ibCropTop.setLayoutParams(topParams);
                    ibCropBot.setLayoutParams(botParams);
                    initializeFrame = false;
                }


            }

        });

        builder = new AlertDialog.Builder(this);

        builder.setTitle("How would you like to save the image?");
        //builder.setMessage("Are you sure?");

        builder.setPositiveButton("Save as Image", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getApplicationContext(), "Image Saved", Toast.LENGTH_LONG).show();
                //startActivity(intentRoot);
                finish();
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("Save as Text", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(getApplicationContext(), ImageToText.class);
                intent.putExtra("methodName","textFromEditImage");
                intent.putExtra("pictureLocation", picLoc);
                intent.putExtra("name",projectName);
                startActivity(intent);
                finish();
                dialog.dismiss();

            }
        });
    }

    public void rotateRight(View view){
        bmp = rotateBitmap(bmp, 90);
        imageView.setImageBitmap(bmp);
    }

    public void rotateLeft(View view){
        bmp = rotateBitmap(bmp, -90);
        imageView.setImageBitmap(bmp);
    }

    public void confirmChanges(View view){
        try {
            File fileOut = new File(picLoc);
            FileOutputStream fos = new FileOutputStream(fileOut);
            int[] imageRect = getImageLocation(imageView);


            float scaleX = (float)bmp.getWidth()/(float)imageRect[2]; //gets the scale to crop from the actual bitmap instead of the perceived image from the view
            float scaleY = bmp.getHeight()/(float)imageRect[3]; //gets the scale to crop from the actual bitmap
            //Toast.makeText(getApplicationContext(),scaleX + " " + ((float)bmp.getWidth()/(float)imageRect[2]) + " " + bmp.getWidth() + " " + imageView.getWidth(),Toast.LENGTH_LONG).show();
            int left = rect.left - imageRect[0] + 8;
            int top = rect.top + imageRect[1] /*- 63*/ + 8;
            int right = (int)(scaleX * rect.width());
            int bot = (int)(scaleY * rect.height());
            if(left < 0 || left > bmp.getWidth()) left = 0;
            if(right > bmp.getWidth() || right < 0) right = bmp.getWidth();
            if(top < 0 || top > bmp.getHeight()) top = 0;
            if(bot > bmp.getHeight() || bot < 0) bot = bmp.getHeight();

            Bitmap croppedBmp;
            //makes sure to only crop the image if the crop region has ever been updated
            if(hasCropBeenUpdated ==true){
                croppedBmp = Bitmap.createBitmap(bmp,left,top,right,bot);
            } else {
                croppedBmp = bmp;
            }

            croppedBmp.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();

            AlertDialog alert = builder.create();
            alert.show();


        }catch (FileNotFoundException e){
            Toast.makeText(this, "No Valid file path. Picture not saved", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            Toast.makeText(this, "IOException", Toast.LENGTH_LONG).show();
        }


    }

    public static Bitmap rotateBitmap(Bitmap source, float angle)
    {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }
    public void updateCrop(){
        paint.setColor(Color.GRAY);
        paint.setAlpha(100);
        rect.set((int)ibCropLeft.getX(),(int)ibCropTop.getY(),(int)ibCropRight.getX()+ibCropRight.getWidth(),(int)ibCropBot.getY()+ibCropBot.getHeight());
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);//clears the previously drawn rect
        canvas.drawRect(rect,paint);
        FrameLayout fl = findViewById(R.id.frameLayout);
        fl.setForeground(new BitmapDrawable(bm));
        hasCropBeenUpdated = true;
    }

    public void cropButtonEvent(){
        ibCropTop.setOnTouchListener(new View.OnTouchListener() { //top crop button
            int dy;
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                final int y = (int) event.getRawY();
                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:
                        dy =(int)(view.getY() - event.getRawY());
                        bm = Bitmap.createBitmap(frameLayout.getWidth(),frameLayout.getHeight(), Bitmap.Config.ARGB_8888);
                        //Toast.makeText(getApplicationContext(),  frameLayout.getX() + " " + rect.left + " " + frameLayout.getY() +  " " + rect.top , Toast.LENGTH_LONG).show();
                        canvas = new Canvas(bm);
                        break;
                    case MotionEvent.ACTION_UP:
                        break;
                    case MotionEvent.ACTION_MOVE:
                        FrameLayout.LayoutParams topParams = (FrameLayout.LayoutParams) ibCropTop.getLayoutParams();
                        FrameLayout.LayoutParams leftParams = (FrameLayout.LayoutParams) ibCropLeft.getLayoutParams();
                        FrameLayout.LayoutParams rightParams = (FrameLayout.LayoutParams) ibCropRight.getLayoutParams();
                        FrameLayout.LayoutParams botParams = (FrameLayout.LayoutParams) ibCropBot.getLayoutParams();
                        if(y+dy > botParams.topMargin-ibCropBot.getHeight()){ //prevents the crop from being moved below the bot crop
                            topParams.topMargin = botParams.topMargin-ibCropBot.getHeight();
                        } else if (y+dy<0){
                            topParams.topMargin = 0;
                        } else {
                            topParams.topMargin = y+dy;
                        }

                        leftParams.topMargin = (topParams.topMargin + botParams.topMargin)/2;
                        rightParams.topMargin = (topParams.topMargin + botParams.topMargin)/2;
                        view.setLayoutParams(topParams);
                        ibCropLeft.setLayoutParams(leftParams);
                        ibCropRight.setLayoutParams(rightParams);
                        updateCrop();
                        break;
                }
                return true;
            }
        });
        ibCropBot.setOnTouchListener(new View.OnTouchListener() { //bottom crop button
            int dy;
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                final int y = (int) event.getRawY();
                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:
                        dy =(int)(view.getY() - event.getRawY());
                        bm = Bitmap.createBitmap(frameLayout.getWidth(),frameLayout.getHeight(), Bitmap.Config.ARGB_8888);
                        canvas = new Canvas(bm);
                        break;
                    case MotionEvent.ACTION_UP:
                        break;
                    case MotionEvent.ACTION_MOVE:
                        FrameLayout.LayoutParams topParams = (FrameLayout.LayoutParams) ibCropTop.getLayoutParams();
                        FrameLayout.LayoutParams leftParams = (FrameLayout.LayoutParams) ibCropLeft.getLayoutParams();
                        FrameLayout.LayoutParams rightParams = (FrameLayout.LayoutParams) ibCropRight.getLayoutParams();
                        FrameLayout.LayoutParams botParams = (FrameLayout.LayoutParams) ibCropBot.getLayoutParams();
                        if(y+dy < topParams.topMargin+ibCropTop.getHeight()){ //prevents the crop from being moved below the bot crop
                            botParams.topMargin = topParams.topMargin+ibCropTop.getHeight();
                        } else if (y+dy>frameLayout.getHeight()-ibCropBot.getHeight()){
                            botParams.topMargin = frameLayout.getHeight()-ibCropBot.getHeight();
                        } else {
                            botParams.topMargin = y+dy;
                        }
                        leftParams.topMargin = (topParams.topMargin + botParams.topMargin)/2;
                        rightParams.topMargin = (topParams.topMargin + botParams.topMargin)/2;
                        view.setLayoutParams(botParams);
                        ibCropLeft.setLayoutParams(leftParams);
                        ibCropRight.setLayoutParams(rightParams);
                        updateCrop();
                        break;
                }
                return true;
            }
        });

        ibCropLeft.setOnTouchListener(new View.OnTouchListener() { //left crop button
            int dx;
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                final int x = (int) event.getRawX();
                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:
                        dx =(int)(view.getX() - event.getRawX());
                        bm = Bitmap.createBitmap(frameLayout.getWidth(),frameLayout.getHeight(), Bitmap.Config.ARGB_8888);
                        canvas = new Canvas(bm);
                        getImageLocation(imageView);
                        //Toast.makeText(getApplicationContext(),imageView.getLocationOnScreen() + "---" + imageView.getY(), Toast.LENGTH_LONG).show();
                        break;
                    case MotionEvent.ACTION_UP:
                        break;
                    case MotionEvent.ACTION_MOVE:
                        FrameLayout.LayoutParams topParams = (FrameLayout.LayoutParams) ibCropTop.getLayoutParams();
                        FrameLayout.LayoutParams leftParams = (FrameLayout.LayoutParams) ibCropLeft.getLayoutParams();
                        FrameLayout.LayoutParams rightParams = (FrameLayout.LayoutParams) ibCropRight.getLayoutParams();
                        FrameLayout.LayoutParams botParams = (FrameLayout.LayoutParams) ibCropBot.getLayoutParams();
                        if(x+dx > rightParams.leftMargin-ibCropRight.getWidth()){ //prevents the crop from being moved below the bot crop
                            leftParams.leftMargin = rightParams.leftMargin-ibCropRight.getWidth();
                        } else if (x+dx< 0){
                            leftParams.leftMargin = 0;
                        } else{
                            leftParams.leftMargin = x+dx;
                        }
                        topParams.leftMargin = (leftParams.leftMargin + rightParams.leftMargin)/2;
                        botParams.leftMargin = (leftParams.leftMargin + rightParams.leftMargin)/2;
                        view.setLayoutParams(leftParams);
                        ibCropTop.setLayoutParams(topParams);
                        ibCropBot.setLayoutParams(botParams);
                        updateCrop();
                        break;
                }
                return true;
            }
        });

        ibCropRight.setOnTouchListener(new View.OnTouchListener() { //right crop button
            int dx;
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                final int x = (int) event.getRawX();
                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:
                        dx =(int)(view.getX() - event.getRawX());
                        bm = Bitmap.createBitmap(frameLayout.getWidth(),frameLayout.getHeight(), Bitmap.Config.ARGB_8888);
                        canvas = new Canvas(bm);
                        break;
                    case MotionEvent.ACTION_UP:
                        break;
                    case MotionEvent.ACTION_MOVE:
                        FrameLayout.LayoutParams topParams = (FrameLayout.LayoutParams) ibCropTop.getLayoutParams();
                        FrameLayout.LayoutParams leftParams = (FrameLayout.LayoutParams) ibCropLeft.getLayoutParams();
                        FrameLayout.LayoutParams rightParams = (FrameLayout.LayoutParams) ibCropRight.getLayoutParams();
                        FrameLayout.LayoutParams botParams = (FrameLayout.LayoutParams) ibCropBot.getLayoutParams();
                        if(x+dx < leftParams.leftMargin+ibCropRight.getWidth()){ //prevents the crop from being moved below the bot crop
                            rightParams.leftMargin = leftParams.leftMargin+ibCropRight.getWidth();
                        } else if (x+dx > frameLayout.getWidth()-ibCropRight.getWidth()){
                            rightParams.leftMargin =frameLayout.getWidth()-ibCropRight.getWidth();
                        } else{
                            rightParams.leftMargin = x+dx;
                        }
                        topParams.leftMargin = (leftParams.leftMargin + rightParams.leftMargin)/2;
                        botParams.leftMargin = (leftParams.leftMargin + rightParams.leftMargin)/2;
                        view.setLayoutParams(rightParams);
                        ibCropTop.setLayoutParams(topParams);
                        ibCropBot.setLayoutParams(botParams);
                        updateCrop();
                        break;
                }
                return true;
            }
        });
    }
    //0=left 1=top 2=width 3=height
    //Used to find the actual coordinates/dimensions of the image in the imageview
    int[] getImageLocation(ImageView iv) {
        int[] ret = new int[4];

        // Get image dimensions
        // Get image matrix values and place them in an array
        float[] f = new float[9];
        iv.getImageMatrix().getValues(f);

        // Extract the scale values using the constants (if aspect ratio maintained, scaleX == scaleY)
        final float scaleX = f[Matrix.MSCALE_X];
        final float scaleY = f[Matrix.MSCALE_Y];

        // Get the drawable (could also get the bitmap behind the drawable and getWidth/getHeight)
        final Drawable d = iv.getDrawable();
        final int origW = d.getIntrinsicWidth();
        final int origH = d.getIntrinsicHeight();

        // Calculate the actual dimensions
        final int actW = Math.round(origW * scaleX);
        final int actH = Math.round(origH * scaleY);

        ret[2] = actW;
        ret[3] = actH;

        // Get image position
        // We assume that the image is centered into ImageView
        int imgViewW = iv.getWidth();
        int imgViewH = iv.getHeight();

        int[] imgViewScreenLoc = new int[2];
        iv.getLocationOnScreen(imgViewScreenLoc);

        // get the actual image location inside its image view
        int left = imgViewScreenLoc[0] + (imgViewW - actW) / 2;
        int top = imgViewScreenLoc[1] + (imgViewH - actH) / 2;

        ret[0] = left;
        ret[1] = top;
        return ret;
    }

}
