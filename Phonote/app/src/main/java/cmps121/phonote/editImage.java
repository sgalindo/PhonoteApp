package cmps121.phonote;

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
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_image);
        imageView = findViewById(R.id.ivPic);
        frameLayout = findViewById(R.id.frameLayout);

        picLoc = getIntent().getStringExtra("image");
        bmp = BitmapFactory.decodeFile(picLoc);

        imageView.setImageBitmap(bmp);

        ibCropTop = findViewById(R.id.ibCropTop);
        ibCropBot = findViewById(R.id.ibCropBot);
        ibCropLeft = findViewById(R.id.ibCropLeft);
        ibCropRight = findViewById(R.id.ibCropRight);

        cropButtonEvent();


        //Used to set the color of the bottom bar
        //TODO: Fix the color (probably have to create a bitmap) -Dustin
        SurfaceView svBottomBar = findViewById(R.id.svBottomBar);
        svBottomBar.setBackgroundColor(0x7a7a7a);
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
            Bitmap croppedBmp = Bitmap.createBitmap(bmp,rect.left - 140,rect.top+140,rect.width()+280,rect.height()+140);
            croppedBmp.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
        }catch (FileNotFoundException e){
            Toast.makeText(this, "No Valid file path. Picture not saved", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            Toast.makeText(this, "IOException", Toast.LENGTH_LONG).show();
        }

        Intent intentRoot = new Intent(this,RootMenu.class);
        Toast.makeText(this, "Image Saved =)", Toast.LENGTH_LONG).show();
        //startActivity(intentRoot);
        finish();
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
                        Toast.makeText(getApplicationContext(),  frameLayout.getX() + " " + rect.left + " " + frameLayout.getY() +  " " + rect.top , Toast.LENGTH_LONG).show();
                        canvas = new Canvas(bm);
                        break;
                    case MotionEvent.ACTION_UP:
                        break;
                    case MotionEvent.ACTION_MOVE:
                        FrameLayout.LayoutParams lParams = (FrameLayout.LayoutParams) view.getLayoutParams();
                        lParams.topMargin = y+dy;
                        view.setLayoutParams(lParams);
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
                        FrameLayout.LayoutParams lParams = (FrameLayout.LayoutParams) view.getLayoutParams();
                        lParams.topMargin = y+dy;
                        view.setLayoutParams(lParams);
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
                        break;
                    case MotionEvent.ACTION_UP:
                        break;
                    case MotionEvent.ACTION_MOVE:
                        FrameLayout.LayoutParams lParams = (FrameLayout.LayoutParams) view.getLayoutParams();
                        lParams.leftMargin = x+dx;
                        view.setLayoutParams(lParams);
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
                        FrameLayout.LayoutParams lParams = (FrameLayout.LayoutParams) view.getLayoutParams();
                        lParams.leftMargin = x+dx;
                        view.setLayoutParams(lParams);
                        updateCrop();
                        break;
                }
                return true;
            }
        });
    }

}
