package cmps121.phonote;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

public class ImageToText extends AppCompatActivity {

    private ImageView imageView;
    private Button buttonGetImage;
    private Button buttonProcess;
    private TextView textView;
    private static final int PICK_IMAGE = 100;
    Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_to_text);

        imageView = (ImageView) findViewById(R.id.image_view_Img2Txt);
        buttonGetImage = (Button)findViewById(R.id.button_pickImage);
        buttonProcess = (Button) findViewById(R.id.button_process);
        textView = (TextView) findViewById(R.id.textView_result_Img2Txt);

        // Set bitmap image from resources
//        final Bitmap bitmap = BitmapFactory.decodeResource(
//                getApplicationContext().getResources(),
//                R.drawable.test_image
//        );
//        imageView.setImageBitmap(bitmap);

        buttonProcess.setOnClickListener(
                new View.OnClickListener(){
                    @Override
                    public void onClick(View view){

                        // This creates the bitmap data that the screen uses to display an image
                        imageView.buildDrawingCache();
                        Bitmap bitmap = imageView.getDrawingCache();

                        // This reads the text and puts it into the textView
                        TextRecognizer textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();
                        if(!textRecognizer.isOperational()) {
                            Toast.makeText(getApplicationContext(), "Could Not Get Text", Toast.LENGTH_SHORT).show();
                        }else {
                            Frame frame = new Frame.Builder().setBitmap(bitmap).build();
                            SparseArray<TextBlock> items = textRecognizer.detect(frame);
                            StringBuilder stringBuilder = new StringBuilder();
                            for(int i = 0; i < items.size(); i++){
                                TextBlock item = items.valueAt(i);
                                stringBuilder.append(item.getValue());
                                stringBuilder.append("\n");
                            }
                            textView.setText(stringBuilder.toString());

                            buttonProcess.setVisibility(View.INVISIBLE);
                        }
                    }
                }
        );

        buttonGetImage.setOnClickListener(
                new View.OnClickListener(){
                    @Override
                    public void onClick(View view){
                        // Pick an image from the phone Gallery
                        openGallery();
                    }
                }
        );
    }

    private void openGallery(){
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Grabs image data from the gallery and puts it in the image view
        if(resultCode == RESULT_OK && requestCode == PICK_IMAGE){
            imageUri = data.getData();
            imageView.setImageURI(imageUri);

            // rotate the image
//            int orientation = getOrientation(imageUri);
//            Log.i("ImageToText", "Rotation:"+orientation);
//            Matrix matrix = new Matrix();
//            imageView.setScaleType(ImageView.ScaleType.MATRIX);
//            matrix.postRotate(orientation);
//            imageView.setImageMatrix(matrix);

            // Getting rid of buttons after they have been used.
            buttonProcess.setVisibility(View.VISIBLE);
            buttonGetImage.setVisibility(View.GONE); // You may want to set this to invisible
        }
    }

    // Gets the rotation value of an image so you can rotate it correctly later
    public int getOrientation(Uri selectedImage) {
        Context context = getApplicationContext();
        int orientation = 0;
        final String[] projection = new String[]{MediaStore.Images.Media.ORIENTATION};
        final Cursor cursor = context.getContentResolver().query(selectedImage, projection, null, null, null);
        if(cursor != null) {
            final int orientationColumnIndex = cursor.getColumnIndex(MediaStore.Images.Media.ORIENTATION);
            if(cursor.moveToFirst()) {
                orientation = cursor.isNull(orientationColumnIndex) ? 0 : cursor.getInt(orientationColumnIndex);
            }
            cursor.close();
        }
        return orientation;
    }
}
