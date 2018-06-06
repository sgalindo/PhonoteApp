package cmps121.phonote;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class ImageToText extends AppCompatActivity {

    private ImageView imageView;
    private Button buttonGetImage;
    private Button buttonProcess;
    private TextView textView;
    private static final int PICK_IMAGE = 100;
    Uri imageUri;
    private Bitmap bitmap;

    public JSONObject jo = null;
    public JSONArray jsonArray = null;

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

        // Find File Path
        Bundle bundle = getIntent().getExtras();
        final String name = bundle.getString("name");
        final String rootPath = getFilesDir().getAbsolutePath() + "/projects/" + name + "/notes/";

        // Open file imageNotes.ser . If no file exists create one
        try {
            File f = new File(rootPath + "imageNotes.ser");
            FileInputStream fi = new FileInputStream(f);
            ObjectInputStream o = new ObjectInputStream(fi);
            String jsonString = null;
            try {
                jsonString = (String) o.readObject();
            }
            catch (ClassNotFoundException c) {
                c.printStackTrace();
            }
            try {
                jo = new JSONObject(jsonString);
                jsonArray = jo.getJSONArray("data");
            }
            catch (JSONException je){
                Log.e("ImageToText:", "Couldn't read JsonString");
                je.printStackTrace();
            }
        }
        catch (IOException e) {
            // file doesn't exit yet create new JsonObject
            jo = new JSONObject();
            jsonArray = new JSONArray();
            try {
                jo.put("data", jsonArray);
                Log.i("ImageToText: ", "File Doesn't Exit, Creating JSONObject");
            }
            catch (JSONException je) {
                je.printStackTrace();
            }
        }

        buttonProcess.setOnClickListener(
                new View.OnClickListener(){
                    @Override
                    public void onClick(View view){

                        // This creates the bitmap data that the screen uses to display an image
//                        imageView.buildDrawingCache();
//                        Bitmap bitmap = imageView.getDrawingCache();

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

                            createNote(rootPath, stringBuilder);

//                            JSONObject joTemp = new JSONObject();
//                            try {
//                                joTemp.put("title", );
//                                joTemp.put("text", stringBuilder.toString());
//                            } catch (JSONException e) {
//                                Log.e("ImageToText", "JSONException: Couldn't convert" +
//                                        " text to JSONObject");
//                                e.printStackTrace();
//                            }
//
//                            //Save Text into file
//                            jsonArray.put(joTemp);
//
//                            try {
//                                File f = new File(rootPath + "imageNotes.ser");
//                                FileOutputStream fo = new FileOutputStream(f);
//                                ObjectOutputStream o = new ObjectOutputStream(fo);
//                                String j = jo.toString();
//                                o.writeObject(j);
//                                o.close();
//                                fo.close();
//                            }
//                            catch (IOException e) {
//                                e.printStackTrace();
//                                Log.e("ImageToText: ", "Couldn't save to imageNotes.ser file");
//                            }


                            buttonProcess.setVisibility(View.GONE);
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
    try{
        if(savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                //Extra bundle is null
            }else{
                String method = extras.getString("methodName");
                String picLoc = extras.getString("pictureLocation");
                if (method.equals("textFromEditImage") && picLoc != null) {
                    textFromEditImage(picLoc);
                }
            }
        }
    } catch (Exception e){

    }

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
            Bitmap bitmap1 = null;
            try{
                bitmap1 = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
            }catch (IOException e){
                Log.e("onActivityResult()", "couldn't create bitmap");
            }
            //imageView.setImageURI(imageUri);

            // rotate the image
//            int orientation = getOrientation(imageUri);
//            Log.i("ImageToText", "Rotation:"+orientation);
//            Matrix matrix = new Matrix();
//            imageView.setScaleType(ImageView.ScaleType.MATRIX);
//            matrix.postRotate(orientation);
//            imageView.setImageMatrix(matrix);
            rotateImage(getOrientation(imageUri), bitmap1);

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

    private void rotateImage( int orientation, Bitmap bitmap1){
        Matrix matrix = new Matrix();
        Log.i("Orientation", ""+ orientation);
        switch(orientation){
            case 90:
                matrix.setRotate(90);
                break;
            case 180:
                matrix.setRotate(180);
                break;
            default:
                Toast.makeText(getApplicationContext(), "No orientation change", Toast.LENGTH_SHORT).show();
        }

        Log.i("Orientation", ""+ bitmap1);

        Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap1, 0, 0, bitmap1.getWidth(), bitmap1.getHeight(), matrix, true);
        Log.i("Orientation", "after rotatedBitmap is defined");
        imageView.setImageBitmap(rotatedBitmap);
        bitmap = rotatedBitmap;
        Log.i("Orientation", "Rotating");
    }

    private void createNote(final String rootPath, final StringBuilder stringBuilder){

        AlertDialog.Builder builder = new AlertDialog.Builder(ImageToText.this);
        builder.setTitle("Set Name");

        final EditText name_input = new EditText(ImageToText.this);
        name_input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(name_input);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String new_name;
                new_name = name_input.getText().toString();

                JSONObject joTemp = new JSONObject();
                try {
                    joTemp.put("title", new_name);
                    joTemp.put("text", stringBuilder.toString());
                } catch (JSONException e) {
                    Log.e("ImageToText", "JSONException: Couldn't convert" +
                            " text to JSONObject");
                    e.printStackTrace();
                }

                //Save Text into file
                jsonArray.put(joTemp);

                try {
                    File f = new File(rootPath + "imageNotes.ser");
                    FileOutputStream fo = new FileOutputStream(f);
                    ObjectOutputStream o = new ObjectOutputStream(fo);
                    String j = jo.toString();
                    o.writeObject(j);
                    o.close();
                    fo.close();

                    Toast.makeText(getApplicationContext(), "Note Saved", Toast.LENGTH_SHORT).show();
                }
                catch (IOException e) {
                    e.printStackTrace();
                    Log.e("ImageToText: ", "Couldn't save to imageNotes.ser file");
                }

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                Toast.makeText(getApplicationContext(), "Note Not Saved", Toast.LENGTH_SHORT).show();
            }
        });
        builder.show();
    }

    private void textFromEditImage(String location){

        Bitmap bitmap1 = BitmapFactory.decodeFile(location);

        rotateImage(0, bitmap1);

        // Getting rid of buttons after they have been used.
        buttonProcess.setVisibility(View.VISIBLE);
        buttonGetImage.setVisibility(View.GONE); // You may want to set this to invisible

        //Deletes the file after displaying it since we are only saving as text
        File file = new File(location);
        file.delete();
    }
}
