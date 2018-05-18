package cmps121.phonote;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class editImage extends AppCompatActivity {
    private Bitmap bmp = null;
    private String picLoc;
    private ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_image);
        imageView = findViewById(R.id.ivPic);

        picLoc = getIntent().getStringExtra("image");
        bmp = BitmapFactory.decodeFile(picLoc);

        imageView.setImageBitmap(bmp);


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
            bmp.compress(Bitmap.CompressFormat.PNG, 100, fos);
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
}
